$domain = 'your-domain.atlassian.net'
$projectKey = 'PROJ'
$bearerToken = 'REPLACE_BEARER_TOKEN'

Get-ChildItem -Path 'target/surefire-reports' -Filter '*.xml' -File | ForEach-Object {
    $file = $_.FullName
    curl -H "Authorization: Bearer $bearerToken" -F "file=@$file" -F "projectKey=$projectKey" "https://$domain/rest/zephyr-scale/1.0/import/executions"
}
