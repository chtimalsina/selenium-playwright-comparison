package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;
import com.microsoft.playwright.*;

/**
 * ABSTRACTION & INHERITANCE - Base test class demonstrating test setup patterns
 * 
 * This class serves as a parent for all test classes, providing common setup
 * and teardown.
 * It demonstrates both Selenium and Playwright initialization and
 * configuration.
 * 
 * KEY FRAMEWORK DIFFERENCES IN TEST SETUP:
 * 
 * 1. BROWSER INITIALIZATION:
 * SELENIUM:
 * - Requires browser-specific driver (ChromeDriver, FirefoxDriver,
 * SafariDriver, etc.)
 * - Driver must match browser version (can use WebDriverManager to automate)
 * - Each browser type requires different driver class
 * - Options configured per browser type (ChromeOptions, FirefoxOptions)
 * 
 * PLAYWRIGHT:
 * - Single Playwright instance manages all browser types
 * - Browser binaries included with Playwright (no separate driver needed)
 * - Same API across all browsers (Chromium, Firefox, WebKit)
 * - Unified configuration approach
 * 
 * 2. BROWSER CONTEXT:
 * SELENIUM:
 * - Each WebDriver instance = One browser window
 * - To simulate multiple users, need multiple WebDriver instances
 * - No built-in isolation between tests
 * 
 * PLAYWRIGHT:
 * - Browser → BrowserContext → Page (3-level hierarchy)
 * - BrowserContext provides isolated environment (like incognito mode)
 * - Multiple contexts can share one browser instance (faster, less memory)
 * - Perfect for testing multi-user scenarios
 * 
 * 3. PARALLEL EXECUTION:
 * SELENIUM:
 * - Can run parallel tests but requires careful WebDriver management
 * - ThreadLocal pattern often needed for thread safety
 * - More complex parallel setup
 * 
 * PLAYWRIGHT:
 * - Built for parallel execution
 * - BrowserContext provides natural isolation
 * - Simpler parallel test implementation
 * 
 * 4. SETUP PERFORMANCE:
 * SELENIUM:
 * - Browser startup: 2-5 seconds
 * - Driver initialization overhead
 * 
 * PLAYWRIGHT:
 * - Browser startup: 1-3 seconds
 * - Context creation: ~100ms (very fast for parallel tests)
 */
public class BaseTest {

    // ========== SELENIUM COMPONENTS ==========

    /**
     * ENCAPSULATION - Protected WebDriver instance
     * 
     * Protected modifier allows child test classes to access driver
     * but keeps it hidden from external classes (encapsulation principle)
     * 
     * WebDriver is the main interface for browser control in Selenium
     * It's implemented by browser-specific classes (ChromeDriver, SafariDriver,
     * etc.)
     */
    protected WebDriver driver;

    // ========== PLAYWRIGHT COMPONENTS ==========

    /**
     * PLAYWRIGHT HIERARCHY: Playwright → Browser → BrowserContext → Page
     * 
     * 1. Playwright: Entry point, manages browser instances
     * 2. Browser: Represents browser application (Chrome, Firefox, etc.)
     * 3. BrowserContext: Isolated session (like incognito mode)
     * 4. Page: Individual tab/page within a context
     * 
     * This hierarchy provides better isolation and resource management
     */
    protected Playwright playwright; // Main Playwright instance
    protected Browser browser; // Browser application instance
    protected BrowserContext context; // Isolated browser context
    protected Page page; // Individual page/tab

    /**
     * Flag to determine which framework to use
     * Can be set via TestNG parameter for flexible test execution
     */
    protected boolean usePlaywright = false;

    // ========== TEST SETUP ==========

    /**
     * BEFORE METHOD - Runs before each test method
     * 
     * TestNG Annotations:
     * 
     * @BeforeMethod - Executes before each @Test method
     * @Parameters - Allows passing parameters from testng.xml
     * @Optional - Provides default value if parameter not specified
     * 
     *           SELENIUM SETUP PROCESS:
     *           1. Create browser-specific driver instance (ChromeDriver or
     *           SafariDriver)
     *           2. Configure browser options (headless, window size, etc.)
     *           3. Maximize window for consistent viewport
     *           4. Ready to navigate to URLs
     * 
     *           PLAYWRIGHT SETUP PROCESS:
     *           1. Create Playwright instance (manages all browsers)
     *           2. Launch browser with options (headless, slow-mo, etc.)
     *           3. Create isolated BrowserContext (like incognito mode)
     *           4. Create Page within context
     *           5. Ready to navigate to URLs
     * 
     * @param framework String parameter to choose framework ("selenium" or
     *                  "playwright")
     */
    @BeforeMethod
    @Parameters({ "framework" })
    public void setUp(@Optional("selenium") String framework) {

        // Determine which framework to use based on parameter
        usePlaywright = "playwright".equalsIgnoreCase(framework);

        if (usePlaywright) {
            // ========== PLAYWRIGHT INITIALIZATION ==========

            System.out.println("=== Initializing Playwright ===");

            /**
             * STEP 1: Create Playwright instance
             * This is the entry point for Playwright automation
             * Manages browser binaries and processes
             */
            playwright = Playwright.create();

            /**
             * STEP 2: Launch browser with options
             * 
             * BROWSER TYPES AVAILABLE:
             * - playwright.chromium() - Chromium-based browsers (Chrome, Edge)
             * - playwright.firefox() - Firefox browser
             * - playwright.webkit() - WebKit (Safari engine)
             * 
             * LAUNCH OPTIONS:
             * - headless(false) - Show browser window (true = hide browser)
             * - slowMo(50) - Slow down operations by X ms (helpful for debugging)
             * - devtools(true) - Open DevTools on launch
             * - args() - Additional browser arguments
             */
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(false) // Show browser for demo purposes
                    .setSlowMo(50); // Slow down by 50ms for visibility

            // Launch Chromium browser
            browser = playwright.chromium().launch(launchOptions);

            /**
             * STEP 3: Create BrowserContext
             * 
             * BrowserContext is like an incognito session:
             * - Isolated cookies, localStorage, sessionStorage
             * - Separate authentication state
             * - Can set viewport size, user agent, permissions, etc.
             * 
             * ADVANTAGES:
             * - Fast to create (~100ms vs 2-3s for new browser)
             * - Perfect for parallel testing
             * - Easy to test multiple users
             * - Can record video/screenshots per context
             */
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                    .setViewportSize(1920, 1080) // Set viewport size
                    .setLocale("en-US"); // Set locale

            context = browser.newContext(contextOptions);

            /**
             * STEP 4: Create Page
             * Page represents a single tab in the browser
             * Multiple pages can exist in one context
             */
            page = context.newPage();

            System.out.println("Playwright initialized successfully");
            System.out.println("Browser: Chromium");
            System.out.println("Viewport: 1920x1080");

        } else {
            // ========== SELENIUM INITIALIZATION ==========

            System.out.println("=== Initializing Selenium ===");

            /**
             * BROWSER DETECTION & INITIALIZATION:
             * Using SafariDriver for macOS by default
             * Can be extended to support multiple browsers via parameters
             */

            // For Safari (macOS default)
            driver = new SafariDriver();

            // ALTERNATIVE: Chrome with options
            // ChromeOptions options = new ChromeOptions();
            // options.addArguments("--disable-blink-features=AutomationControlled");
            // options.addArguments("--start-maximized");
            // driver = new ChromeDriver(options);

            /**
             * MAXIMIZE WINDOW:
             * Ensures consistent viewport size across test runs
             * Important for:
             * - Element visibility
             * - Screenshot consistency
             * - Responsive design testing
             */
            driver.manage().window().maximize();

            System.out.println("Selenium initialized successfully");
            System.out.println("Browser: Safari");
            System.out.println("Window: Maximized");
        }
    }

    // ========== TEST TEARDOWN ==========

    /**
     * AFTER METHOD - Runs after each test method
     * 
     * @AfterMethod annotation ensures cleanup happens after every test,
     *              even if the test fails or throws an exception
     * 
     *              IMPORTANCE OF CLEANUP:
     *              1. Release system resources (memory, CPU)
     *              2. Close browser processes
     *              3. Prevent resource leaks in long test runs
     *              4. Ensure clean state for next test
     * 
     *              SELENIUM CLEANUP:
     *              - driver.quit() closes all windows and ends WebDriver session
     *              - Terminates browser process
     *              - Cleans up temporary files
     * 
     *              PLAYWRIGHT CLEANUP:
     *              - Close in order: Page → Context → Browser → Playwright
     *              - Each level cleans up its resources
     *              - Playwright.close() kills browser processes
     */
    @AfterMethod
    public void tearDown() {

        if (usePlaywright) {
            // ========== PLAYWRIGHT CLEANUP ==========

            System.out.println("=== Cleaning up Playwright ===");

            /**
             * CLEANUP ORDER IS IMPORTANT:
             * Close from innermost to outermost:
             * Page → Context → Browser → Playwright
             */

            // Close page if it exists
            if (page != null) {
                page.close();
                System.out.println("Page closed");
            }

            // Close context (also closes all pages in it)
            if (context != null) {
                context.close();
                System.out.println("Browser context closed");
            }

            // Close browser (also closes all contexts in it)
            if (browser != null) {
                browser.close();
                System.out.println("Browser closed");
            }

            // Close Playwright instance (cleans up all resources)
            if (playwright != null) {
                playwright.close();
                System.out.println("Playwright closed");
            }

            /**
             * RESOURCE CLEANUP:
             * - All browser processes terminated
             * - Temporary files removed
             * - Video/screenshot files saved (if recording was enabled)
             * - Network connections closed
             */

        } else {
            // ========== SELENIUM CLEANUP ==========

            System.out.println("=== Cleaning up Selenium ===");

            /**
             * QUIT vs CLOSE:
             * - driver.close() - Closes current window only
             * - driver.quit() - Closes ALL windows and ends WebDriver session
             * 
             * ALWAYS USE quit() in tearDown to ensure complete cleanup
             */
            if (driver != null) {
                driver.quit(); // Close browser and end session
                System.out.println("Browser closed and WebDriver session ended");
            }

            /**
             * RESOURCE CLEANUP:
             * - Browser process terminated
             * - Temporary profile directory deleted
             * - Port released
             * - Driver process killed
             */
        }
    }

    /**
     * UTILITY METHOD: Get framework name being used
     * Useful for logging and conditional test logic
     */
    protected String getFrameworkName() {
        return usePlaywright ? "Playwright" : "Selenium";
    }

    /**
     * UTILITY METHOD: Take screenshot (framework-agnostic)
     * Demonstrates how to handle screenshots in both frameworks
     */
    protected void takeScreenshot(String fileName) {
        if (usePlaywright && page != null) {
            // PLAYWRIGHT SCREENSHOT:
            // - Built-in screenshot capability
            // - Can capture full page (scrolls automatically)
            // - Can mask/hide elements
            // - Can capture specific elements
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(java.nio.file.Paths.get(fileName + ".png"))
                    .setFullPage(true)); // Capture full scrollable page

            System.out.println("Playwright screenshot saved: " + fileName + ".png");

        } else if (driver != null) {
            // SELENIUM SCREENSHOT:
            // - Requires casting to TakesScreenshot interface
            // - Captures visible viewport only (unless using third-party lib)
            // - Need to handle file saving manually

            // Note: Implementation would require TakesScreenshot import
            // File screenshot = ((TakesScreenshot)
            // driver).getScreenshotAs(OutputType.FILE);
            // FileUtils.copyFile(screenshot, new File(fileName + ".png"));

            System.out.println("Selenium screenshot capability not implemented in this example");
        }
    }
}
