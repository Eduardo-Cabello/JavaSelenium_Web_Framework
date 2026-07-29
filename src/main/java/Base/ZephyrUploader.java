package Base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class ZephyrUploader {

    private static final Map<String, String> TEST_MAPPING = loadTestMapping();

    private static Map<String, String> loadTestMapping() {
        Map<String, String> map = new HashMap<>();
        Path mapping = Paths.get("src", "test", "resources", "test-mapping.csv");
        if (!Files.exists(mapping)) return map;
        try {
            List<String> lines = Files.readAllLines(mapping);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String left = parts[0].trim();
                    String right = parts[1].trim();
                    if (!left.isEmpty() && !right.isEmpty()) {
                        map.put(left, right);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to read test mapping: " + e.getMessage());
        }
        return map;
    }

    public static void uploadSurefireReports() {
        String enabled = Utilities.getProperties("zephyr.enabled");
        if (enabled == null || !enabled.equalsIgnoreCase("true")) {
            System.out.println("Zephyr upload disabled (zephyr.enabled!=true)");
            return;
        }

        String domain = Utilities.getProperties("zephyr.domain");
        String projectKey = Utilities.getProperties("zephyr.projectKey");
        String authType = Utilities.getProperties("zephyr.authType");
        String bearerToken = Utilities.getProperties("zephyr.bearerToken");
        String basicEmail = Utilities.getProperties("zephyr.basicEmail");
        String basicApiToken = Utilities.getProperties("zephyr.basicApiToken");
        String testCycleKey = Utilities.getProperties("zephyr.testCycleKey");
        String autoCreate = Utilities.getProperties("zephyr.autoCreateTestcases");

        if (domain == null || domain.isBlank() || projectKey == null || projectKey.isBlank()) {
            System.err.println("Zephyr upload skipped: domain or projectKey not configured.");
            return;
        }

        Path reportsDir = Paths.get("target", "surefire-reports");
        if (!Files.exists(reportsDir)) {
            System.out.println("No surefire-reports folder found, skipping Zephyr upload.");
            return;
        }

        try {
            List<Path> xmlFiles = Files.list(reportsDir)
                    .filter(p -> p.toString().toLowerCase().endsWith(".xml"))
                    .collect(Collectors.toList());

            if (xmlFiles.isEmpty()) {
                System.out.println("No XML test result files found to upload.");
                return;
            }

            for (Path xml : xmlFiles) {
                uploadSingleFile(domain, projectKey, authType, bearerToken, basicEmail, basicApiToken, testCycleKey, autoCreate, xml);
            }
        } catch (IOException e) {
            System.err.println("Error reading surefire reports: " + e.getMessage());
        }
    }

    private static void uploadSingleFile(String domain,
                                         String projectKey,
                                         String authType,
                                         String bearerToken,
                                         String basicEmail,
                                         String basicApiToken,
                                         String testCycleKey,
                                         String autoCreate,
                                         Path filePath) {
        String url = "https://" + domain + "/rest/zephyr-scale/1.0/import/executions";
        String boundary = "------ZephyrBoundary" + System.currentTimeMillis();

        try {
            // Read file content and inject Jira keys from mapping when possible
            String content = Files.readString(filePath, StandardCharsets.UTF_8);
            if (!TEST_MAPPING.isEmpty()) {
                for (Map.Entry<String, String> e : TEST_MAPPING.entrySet()) {
                    String key = e.getKey(); // format: fully.qualified.ClassName#method
                    String jira = e.getValue();
                    int idx = key.lastIndexOf('#');
                    if (idx <= 0) continue;
                    String fqClass = key.substring(0, idx);
                    String method = key.substring(idx + 1);
                    try {
                        String regex = "(<testcase[^>]*classname=\\\"" + Pattern.quote(fqClass) + "[^>]*name=\\\")" + Pattern.quote(method) + "(\\\")";
                        Pattern p = Pattern.compile(regex);
                        String replacement = "$1" + method + " [" + jira + "]$2";
                        content = p.matcher(content).replaceAll(replacement);
                    } catch (Exception ex) {
                        // ignore faulty regex
                    }
                }
            }
            byte[] fileBytes = content.getBytes(StandardCharsets.UTF_8);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // projectKey field
            writeFormField(baos, boundary, "projectKey", projectKey);

            if (testCycleKey != null && !testCycleKey.isBlank()) {
                writeFormField(baos, boundary, "testCycleKey", testCycleKey);
            }

            if (autoCreate != null && !autoCreate.isBlank()) {
                writeFormField(baos, boundary, "autoCreateTestcases", autoCreate);
            }

            // file part
            baos.write(("--" + boundary + "\r\n").getBytes());
            baos.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + filePath.getFileName().toString() + "\"\r\n").getBytes());
            baos.write(("Content-Type: application/xml\r\n\r\n").getBytes());
            baos.write(fileBytes);
            baos.write("\r\n".getBytes());

            // end boundary
            baos.write(("--" + boundary + "--\r\n").getBytes());

            byte[] body = baos.toByteArray();

            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body));

            // Authorization
            if (authType != null && authType.equalsIgnoreCase("basic") && basicEmail != null && !basicEmail.isBlank() && basicApiToken != null) {
                String pair = basicEmail + ":" + basicApiToken;
                String b64 = Base64.getEncoder().encodeToString(pair.getBytes());
                reqBuilder.header("Authorization", "Basic " + b64);
            } else if (authType != null && authType.equalsIgnoreCase("bearer") && bearerToken != null && !bearerToken.isBlank()) {
                reqBuilder.header("Authorization", "Bearer " + bearerToken);
            } else if (bearerToken != null && !bearerToken.isBlank()) {
                reqBuilder.header("Authorization", "Bearer " + bearerToken);
            }

            HttpRequest request = reqBuilder.build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Uploaded " + filePath.getFileName() + " -> status: " + response.statusCode());
            System.out.println("Response: " + response.body());

        } catch (IOException | InterruptedException e) {
            System.err.println("Error uploading " + filePath.getFileName() + ": " + e.getMessage());
        }
    }

    private static void writeFormField(ByteArrayOutputStream baos, String boundary, String name, String value) throws IOException {
        baos.write(("--" + boundary + "\r\n").getBytes());
        baos.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes());
        baos.write(value == null ? "".getBytes() : value.getBytes());
        baos.write("\r\n".getBytes());
    }
}
