# SeleniumTesting_Framework

Lightweight Selenium + TestNG automation framework example in Java (Maven).

## Requirements
- Java 17
- Maven
- Chrome or Edge and corresponding WebDriver on `PATH` (or in default locations)

## Quick refactors and fixes applied
- Cached reading of `src/test/resources/data.properties` to avoid repeated file reads (`Utilities.getProperties` now uses a cached Properties object).
- Fixed an incorrect call `wait.wait(500)` in `Base.clearType()` to use `Thread.sleep(500)` with proper interruption handling.
- Added `evidence.enabled` and `zephyr.*` properties to control report generation and Zephyr uploads.
- Added `Base.ZephyrUploader` and a PowerShell script `scripts/upload-zephyr-scale.ps1` to upload TestNG/JUnit XMLs to Zephyr Scale.

## Project structure
- `src/main/java` — framework and page objects (`Base`, `DemoWeb`)
- `src/test/java` — TestNG test cases
- `src/test/resources/data.properties` — test data (URL, username, password, role)
- `target/reports` — generated Word reports and screenshots

## Common tasks

Run all tests:
```bash
mvn test
```

Run a single test class (example):
```bash
mvn -Dtest=TestCases.DemoTestCase test
```

Where to configure the AUT URL and credentials:
- Edit `src/test/resources/data.properties` and set `url`, `username`, `password`, `role`.

Zephyr and evidence configuration
- `src/test/resources/data.properties` contains the following Zephyr and evidence keys:
	- `zephyr.enabled` — `true`/`false` to enable uploads
	- `zephyr.domain` — your Atlassian domain, e.g. `your-domain.atlassian.net`
	- `zephyr.projectKey` — Zephyr project key
	- `zephyr.authType` — `bearer` or `basic`
	- `zephyr.bearerToken` or `zephyr.basicEmail` and `zephyr.basicApiToken`
	- `zephyr.testCycleKey` — optional test cycle to import into
	- `zephyr.autoCreateTestcases` — `true`/`false` to auto-create tests
	- `evidence.enabled` — `true`/`false` to control screenshot and Word report generation

Reports and screenshots
- During tests the framework captures step screenshots in `target/reports/screenshots`.
- Per-test Word reports are created under `target/reports/*.docx` by `Base.Utilities` using Apache POI.

If `evidence.enabled=false` screenshots are not created and the Word report is skipped.

Notes
- `Base.startDriver("chrome")` is used by tests; change to `edge` if you want Edge.
- Ensure the appropriate browser driver (chromedriver/msedgedriver) matches your browser version.

Uploading results to Zephyr Scale (Atlassian Cloud)

Use the included script `scripts/upload-zephyr-scale.ps1`.

Example invocation:
```powershell
powershell -ExecutionPolicy Bypass -File scripts\upload-zephyr-scale.ps1
```

Update the variables at the top of `scripts/upload-zephyr-scale.ps1` before running:
- `$domain` — your Atlassian domain (for example `your-domain.atlassian.net`)
- `$projectKey` — Zephyr project key
- `$bearerToken` — your Atlassian API token

The script uploads all `*.xml` files from `target/surefire-reports` to:
`https://<domain>/rest/zephyr-scale/1.0/import/executions`

CI integration
- After `mvn test`, run the upload script using a CI secret for the token. Store secrets in CI variables, not `data.properties`.

Next steps and suggestions
- Consider adding a TestNG parameter or Maven profile to toggle `evidence.enabled` without editing files.
- Improve mapping between TestNG methods and Jira Test keys (embed keys in test names or maintain a mapping file).
- Add attaching screenshots to Zephyr executions via the Zephyr Scale Attachments API.
 
Mapping tests to Jira Test keys
- Use `src/test/resources/test-mapping.csv` to map test methods to Jira Test keys. Format:
	- `fully.qualified.ClassName#methodName,JIRA-KEY`
	- Example: `TestCases.DemoTestCase#CP002,PROJ-123`

Maven profiles and runtime toggles
- Use the provided Maven profiles to toggle behavior without editing `data.properties`:
  - Disable evidence (screenshots & Word reports): `mvn test -Pevidence-off`
  - Enable Zephyr upload profile: `mvn test -Pzephyr-on -Dzephyr.bearerToken=YOUR_TOKEN`
  - You can also pass system properties directly: `mvn test -Devidence.enabled=false -Dzephyr.enabled=true -Dzephyr.bearerToken=...`

Contact
- Maintainer: repository owner
