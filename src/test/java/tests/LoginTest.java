package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SecurePage;

/**
 * LOGIN TEST CLASS - Demonstrating Selenium vs Playwright test implementation
 * 
 * This class contains test methods that work with BOTH Selenium and Playwright.
 * The framework choice is determined by the BaseTest setup.
 * 
 * KEY TESTING DIFFERENCES:
 * 
 * 1. TEST EXECUTION SPEED:
 * SELENIUM:
 * - Each test typically takes 5-10 seconds
 * - Overhead from WebDriver protocol (HTTP requests)
 * - Browser startup adds 2-3 seconds
 * 
 * PLAYWRIGHT:
 * - Each test typically takes 2-5 seconds
 * - Direct browser communication (faster)
 * - Browser context creation ~100ms
 * 
 * 2. TEST RELIABILITY:
 * SELENIUM:
 * - Requires explicit waits for stability
 * - Can have flaky tests due to timing issues
 * - StaleElementReferenceException common
 * 
 * PLAYWRIGHT:
 * - Auto-wait reduces flakiness
 * - Automatic retries on transient failures
 * - More stable tests out of the box
 * 
 * 3. DEBUGGING:
 * SELENIUM:
 * - Standard Java debugging
 * - Screenshots require manual implementation
 * - Limited built-in debugging tools
 * 
 * PLAYWRIGHT:
 * - Inspector tool (PWDEBUG=1)
 * - Auto-capture screenshots/videos on failure
 * - Trace viewer for step-by-step analysis
 * - Better error messages with context
 * 
 * 4. ASSERTIONS:
 * Both use TestNG assertions (framework-independent)
 * - Assert.assertTrue() - Verify boolean condition
 * - Assert.assertEquals() - Compare values
 * - Assert.assertNotNull() - Verify not null
 * 
 * TEST ORGANIZATION:
 * - @Test annotation marks test methods
 * - priority determines execution order
 * - description provides test documentation
 * - Tests are independent (can run in any order ideally)
 */
public class LoginTest extends BaseTest {

    /**
     * TEST 1: Successful Login Flow
     * 
     * OBJECTIVE: Verify that a user can successfully login with valid credentials
     * 
     * TEST STEPS:
     * 1. Navigate to login page
     * 2. Enter valid username
     * 3. Enter valid password
     * 4. Click login button
     * 5. Verify logout button is visible (confirms login success)
     * 6. Verify success message is displayed
     * 
     * SELENIUM EXECUTION:
     * - Creates LoginPage with WebDriver
     * - Uses explicit waits for each interaction
     * - Typical execution time: 6-8 seconds
     * 
     * PLAYWRIGHT EXECUTION:
     * - Creates LoginPage with Page object
     * - Auto-waits handle synchronization
     * - Typical execution time: 3-4 seconds
     * 
     * @throws Exception if test fails
     */
    @Test(priority = 1, description = "Verify successful login with valid credentials")
    public void testSuccessfulLogin() {
        System.out.println("\n=== Running Test: Successful Login (" + getFrameworkName() + ") ===");

        if (usePlaywright) {
            // ========== PLAYWRIGHT VERSION ==========

            // Create page object with Playwright Page
            LoginPage loginPage = new LoginPage(page);

            // Navigate to login page
            // Auto-waits for page load
            loginPage.openPage(true);
            System.out.println("Navigated to login page");

            // Perform login
            // Auto-waits for elements to be actionable
            loginPage.login("tomsmith", "SuperSecretPassword!", true);
            System.out.println("Login credentials entered and submitted");

            // Verify login success
            SecurePage securePage = new SecurePage(page);

            // Assert logout button is visible
            // Playwright's isVisible() checks actual visibility
            Assert.assertTrue(securePage.isLogoutButtonVisible(true),
                    "Logout button should be visible after successful login");
            System.out.println("✓ Logout button is visible");

            // Verify success message
            String message = securePage.getMessage(true);
            Assert.assertTrue(message.contains("You logged into a secure area!"),
                    "Success message should be displayed. Actual: " + message);
            System.out.println("✓ Success message verified: " + message.trim());

        } else {
            // ========== SELENIUM VERSION ==========

            // Create page object with WebDriver
            LoginPage loginPage = new LoginPage(driver);

            // Navigate to login page
            // Waits for page load event
            loginPage.openPage();
            System.out.println("Navigated to login page");

            // Perform login
            // Uses explicit waits from BasePage
            loginPage.login("tomsmith", "SuperSecretPassword!");
            System.out.println("Login credentials entered and submitted");

            // Verify login success
            SecurePage securePage = new SecurePage(driver);

            // Assert logout button is visible
            // Selenium's isDisplayed() checks CSS visibility
            Assert.assertTrue(securePage.isLogoutButtonVisible(),
                    "Logout button should be visible after successful login");
            System.out.println("✓ Logout button is visible");

            // Verify success message
            String message = securePage.getMessage();
            Assert.assertTrue(message.contains("You logged into a secure area!"),
                    "Success message should be displayed. Actual: " + message);
            System.out.println("✓ Success message verified: " + message.trim());
        }

        System.out.println("=== Test Passed: Successful Login ===");
    }

    /**
     * TEST 2: Login with Invalid Username
     * 
     * OBJECTIVE: Verify proper error handling when invalid username is provided
     * 
     * TEST STEPS:
     * 1. Navigate to login page
     * 2. Enter invalid username
     * 3. Enter valid password
     * 4. Click login button
     * 5. Verify error message indicating invalid username
     * 
     * NEGATIVE TESTING:
     * Tests error paths and validation logic
     * Important for security and user experience
     * 
     * ERROR MESSAGE VALIDATION:
     * - Checks that appropriate error is shown
     * - Confirms application validates username
     * - Ensures user gets helpful feedback
     */
    @Test(priority = 2, description = "Verify login fails with invalid username")
    public void testLoginWithInvalidUsername() {
        System.out.println("\n=== Running Test: Invalid Username (" + getFrameworkName() + ") ===");

        if (usePlaywright) {
            // PLAYWRIGHT VERSION
            LoginPage loginPage = new LoginPage(page);
            loginPage.openPage(true);
            System.out.println("Navigated to login page");

            // Attempt login with invalid username
            loginPage.login("invaliduser", "SuperSecretPassword!", true);
            System.out.println("Invalid username entered and submitted");

            // Verify error message
            String message = loginPage.getMessage(true);
            Assert.assertTrue(message.contains("Your username is invalid!"),
                    "Error message should indicate invalid username. Actual: " + message);
            System.out.println("✓ Error message verified: " + message.trim());

        } else {
            // SELENIUM VERSION
            LoginPage loginPage = new LoginPage(driver);
            loginPage.openPage();
            System.out.println("Navigated to login page");

            // Attempt login with invalid username
            loginPage.login("invaliduser", "SuperSecretPassword!");
            System.out.println("Invalid username entered and submitted");

            // Verify error message
            String message = loginPage.getMessage();
            Assert.assertTrue(message.contains("Your username is invalid!"),
                    "Error message should indicate invalid username. Actual: " + message);
            System.out.println("✓ Error message verified: " + message.trim());
        }

        System.out.println("=== Test Passed: Invalid Username ===");
    }

    /**
     * TEST 3: Login with Invalid Password
     * 
     * OBJECTIVE: Verify proper error handling when invalid password is provided
     * 
     * TEST STEPS:
     * 1. Navigate to login page
     * 2. Enter valid username
     * 3. Enter invalid password
     * 4. Click login button
     * 5. Verify error message indicating invalid password
     * 
     * SECURITY TESTING:
     * - Confirms password validation works
     * - Ensures clear error messaging
     * - Tests authentication security
     * 
     * BEST PRACTICE:
     * Error messages should not reveal whether username or password was wrong
     * for security, but this demo app shows specific errors
     */
    @Test(priority = 3, description = "Verify login fails with invalid password")
    public void testLoginWithInvalidPassword() {
        System.out.println("\n=== Running Test: Invalid Password (" + getFrameworkName() + ") ===");

        if (usePlaywright) {
            // PLAYWRIGHT VERSION
            LoginPage loginPage = new LoginPage(page);
            loginPage.openPage(true);
            System.out.println("Navigated to login page");

            // Attempt login with invalid password
            loginPage.login("tomsmith", "wrongpassword", true);
            System.out.println("Invalid password entered and submitted");

            // Verify error message
            String message = loginPage.getMessage(true);
            Assert.assertTrue(message.contains("Your password is invalid!"),
                    "Error message should indicate invalid password. Actual: " + message);
            System.out.println("✓ Error message verified: " + message.trim());

        } else {
            // SELENIUM VERSION
            LoginPage loginPage = new LoginPage(driver);
            loginPage.openPage();
            System.out.println("Navigated to login page");

            // Attempt login with invalid password
            loginPage.login("tomsmith", "wrongpassword");
            System.out.println("Invalid password entered and submitted");

            // Verify error message
            String message = loginPage.getMessage();
            Assert.assertTrue(message.contains("Your password is invalid!"),
                    "Error message should indicate invalid password. Actual: " + message);
            System.out.println("✓ Error message verified: " + message.trim());
        }

        System.out.println("=== Test Passed: Invalid Password ===");
    }

    /**
     * TEST 4: Complete Login-Logout Flow
     * 
     * OBJECTIVE: Verify full user journey from login to logout
     * 
     * TEST STEPS:
     * 1. Navigate to login page
     * 2. Login with valid credentials
     * 3. Verify login success
     * 4. Click logout button
     * 5. Verify logout message
     * 6. Verify back on login page
     * 
     * END-TO-END TESTING:
     * - Tests complete user workflow
     * - Verifies session management
     * - Confirms logout functionality
     * 
     * IMPORTANCE:
     * - Validates full authentication cycle
     * - Tests session cleanup
     * - Ensures users can successfully logout
     */
    @Test(priority = 4, description = "Verify successful logout after login")
    public void testSuccessfulLogout() {
        System.out.println("\n=== Running Test: Login-Logout Flow (" + getFrameworkName() + ") ===");

        if (usePlaywright) {
            // PLAYWRIGHT VERSION

            // Step 1: Login
            LoginPage loginPage = new LoginPage(page);
            loginPage.openPage(true);
            System.out.println("Navigated to login page");

            loginPage.login("tomsmith", "SuperSecretPassword!", true);
            System.out.println("Logged in successfully");

            // Step 2: Logout
            SecurePage securePage = new SecurePage(page);
            securePage.logout(true);
            System.out.println("Clicked logout button");

            // Step 3: Verify logout message
            // Back on login page after logout
            String message = loginPage.getMessage(true);
            Assert.assertTrue(message.contains("You logged out of the secure area!"),
                    "Logout message should be displayed. Actual: " + message);
            System.out.println("✓ Logout message verified: " + message.trim());

            // Optional: Verify we're back on login page
            String currentUrl = securePage.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/login"),
                    "Should be redirected to login page after logout");
            System.out.println("✓ Redirected to login page: " + currentUrl);

        } else {
            // SELENIUM VERSION

            // Step 1: Login
            LoginPage loginPage = new LoginPage(driver);
            loginPage.openPage();
            System.out.println("Navigated to login page");

            loginPage.login("tomsmith", "SuperSecretPassword!");
            System.out.println("Logged in successfully");

            // Step 2: Logout
            SecurePage securePage = new SecurePage(driver);
            securePage.logout();
            System.out.println("Clicked logout button");

            // Step 3: Verify logout message
            // Back on login page after logout
            String message = loginPage.getMessage();
            Assert.assertTrue(message.contains("You logged out of the secure area!"),
                    "Logout message should be displayed. Actual: " + message);
            System.out.println("✓ Logout message verified: " + message.trim());

            // Optional: Verify we're back on login page
            String currentUrl = securePage.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/login"),
                    "Should be redirected to login page after logout");
            System.out.println("✓ Redirected to login page: " + currentUrl);
        }

        System.out.println("=== Test Passed: Login-Logout Flow ===");
    }

    /**
     * COMPARISON SUMMARY (based on typical execution):
     * 
     * SELENIUM:
     * - Setup time: ~3 seconds
     * - Per test time: ~6-8 seconds
     * - Total for 4 tests: ~30-35 seconds
     * - Requires explicit waits
     * - More code for stability
     * 
     * PLAYWRIGHT:
     * - Setup time: ~1.5 seconds
     * - Per test time: ~3-4 seconds
     * - Total for 4 tests: ~15-18 seconds
     * - Auto-waits included
     * - Less code needed
     * - ~50% faster overall
     * 
     * WHEN TO USE EACH:
     * 
     * USE SELENIUM WHEN:
     * - Team already experienced with Selenium
     * - Need to test on real Safari (not WebKit)
     * - Require specific browser versions
     * - Extensive existing test suite
     * - Need support for older browsers
     * 
     * USE PLAYWRIGHT WHEN:
     * - Starting new automation project
     * - Need faster test execution
     * - Want better debugging tools
     * - Require network interception/mocking
     * - Testing modern web applications
     * - Need video recording of tests
     * - Want mobile browser testing
     */
}
