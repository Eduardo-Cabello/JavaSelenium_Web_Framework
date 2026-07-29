# Selenium Testing Framework

This project is a Java-based test automation framework built with Selenium, TestNG, Maven, and Allure. It is designed to help teams create maintainable browser automation tests for web applications.

## What this framework includes

- Selenium WebDriver support for Chrome, Edge, and Firefox
- TestNG-based test execution
- Page Object Model (POM) structure
- Reusable base utilities for browser actions and waits
- Screenshot and Word report generation
- Allure reporting integration
- Optional Zephyr Scale integration for test result upload

## Requirements

Before using the framework, make sure you have the following installed:

- Java 17 or higher
- Maven 3.8+
- A supported browser: Chrome, Edge, or Firefox
- The corresponding WebDriver available on your system PATH

## Project structure

- [src/main/java/Base](src/main/java/Base) — core framework utilities such as driver management, configuration, and browser actions
- [src/main/java/DemoWeb/Actions](src/main/java/DemoWeb/Actions) — test action classes
- [src/main/java/DemoWeb/Pages](src/main/java/DemoWeb/Pages) — Page Object classes for the application under test
- [src/test/java/TestCases](src/test/java/TestCases) — TestNG test classes and listeners
- [src/test/resources/data.properties](src/test/resources/data.properties) — test configuration values
- [target/reports](target/reports) — generated screenshots and Word reports

## Setup

1. Clone the repository
   ```bash
   git clone <repository-url>
   cd SeleniumTesting_Framework
   ```

2. Configure test data

   Edit [src/test/resources/data.properties](src/test/resources/data.properties) and update the values for:

   - `url`
   - `username`
   - `password`
   - `role`
   - `browser` (optional, default is `chrome`)
   - `implicit.timeout` (optional)
   - `page.load.timeout` (optional)

   Example:
   ```properties
   url=https://your-app-url
   username=your-username
   password=your-password
   role=Consultant
   browser=chrome
   implicit.timeout=6
   page.load.timeout=30
   evidence.enabled=true
   ```

3. Make sure the browser driver is available

   For example, if you use Chrome, ensure `chromedriver` is installed and available in your PATH.

## Running tests

Run all tests:
```bash
mvn test
```

Run a specific test class:
```powershell
mvn "-Dtest=TestCases.DemoTestCase" test
```

Run a specific test method:
```powershell
mvn "-Dtest=TestCases.DemoTestCase#CP002" test
```

> In PowerShell, the `-Dtest` value should be wrapped in quotes to avoid parsing issues.

## Generating reports

### Allure report

After running tests, generate the report with:
```bash
mvn allure:report
```

To open the report locally:
```bash
mvn allure:serve
```

If the `allure:serve` command is not available, install Allure Commandline or use the generated HTML report from the `target/site/allure-maven-plugin` folder.

## Evidence and reporting

The framework automatically creates:

- screenshots under [target/reports/screenshots](target/reports/screenshots)
- Word reports under [target/reports](target/reports)

You can disable evidence generation by setting:
```properties
evidence.enabled=false
```

## Zephyr Scale integration (optional)

If you want to upload test results to Zephyr Scale, configure the following values in [src/test/resources/data.properties](src/test/resources/data.properties):

- `zephyr.enabled=true`
- `zephyr.domain`
- `zephyr.projectKey`
- `zephyr.authType`
- `zephyr.bearerToken` or `zephyr.basicEmail` and `zephyr.basicApiToken`
- `zephyr.testCycleKey` (optional)
- `zephyr.autoCreateTestcases` (optional)

You can also use the PowerShell script in [scripts/upload-zephyr-scale.ps1](scripts/upload-zephyr-scale.ps1).

## Extending the framework

To add new tests:

1. Create a new class in [src/test/java/TestCases](src/test/java/TestCases)
2. Extend [src/test/java/TestCases/BaseTest.java](src/test/java/TestCases/BaseTest.java)
3. Add page objects under [src/main/java/DemoWeb/Pages](src/main/java/DemoWeb/Pages)
4. Add reusable actions under [src/main/java/DemoWeb/Actions](src/main/java/DemoWeb/Actions)

## Notes

- The framework is designed to be easy to extend for new pages, flows, and test cases.
- Keep selectors centralized in Page Object classes to avoid duplication.
- Prefer using the base utilities in [src/main/java/Base](src/main/java/Base) instead of writing raw Selenium code directly inside tests.

## Maintainer

This project can be maintained and extended by any team member familiar with Java, Maven, and Selenium.
