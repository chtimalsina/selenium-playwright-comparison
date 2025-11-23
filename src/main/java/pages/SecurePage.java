package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

/**
 * INHERITANCE & ENCAPSULATION - SecurePage demonstrating Selenium vs Playwright
 * 
 * This class represents the secure area page that appears after successful
 * login.
 * It demonstrates both Selenium and Playwright approaches to page object
 * modeling.
 * 
 * PAGE OBJECT MODEL (POM) BENEFITS:
 * 1. MAINTAINABILITY - Changes to UI require updates only in page classes
 * 2. REUSABILITY - Page methods can be used across multiple tests
 * 3. READABILITY - Tests read like business actions, not technical steps
 * 4. SEPARATION OF CONCERNS - Test logic separate from page interaction logic
 * 
 * COMPARISON HIGHLIGHTS:
 * 
 * SELENIUM APPROACH:
 * - Traditional, mature approach used since ~2004
 * - Requires PageFactory initialization
 * - Elements found via @FindBy annotations
 * - Explicit waits needed for reliability
 * - Prone to StaleElementReferenceException
 * 
 * PLAYWRIGHT APPROACH:
 * - Modern approach (released 2020)
 * - No factory pattern - direct locator creation
 * - Locators are resilient to DOM changes
 * - Built-in auto-waiting and retries
 * - Better performance and reliability
 */
public class SecurePage extends BasePage {

    // ========== SELENIUM IMPLEMENTATION ==========

    /**
     * SELENIUM ELEMENTS using PageFactory pattern
     * 
     * @FindBy ANNOTATION STRATEGIES:
     *         - id: Fastest, most reliable if IDs are unique and stable
     *         - css: Flexible, supports complex selectors
     *         - xpath: Powerful but slower, harder to maintain
     *         - name: Good for form inputs
     *         - className: Risky if classes change or aren't unique
     *         - linkText/partialLinkText: For anchor tags
     * 
     *         BEST PRACTICE: Prefer id > css > xpath for performance and stability
     */

    @FindBy(css = ".icon-2x.icon-signout")
    private WebElement logoutButton; // Button to logout from secure area

    @FindBy(id = "flash")
    private WebElement messageElement; // Flash message element

    // ========== PLAYWRIGHT IMPLEMENTATION ==========

    /**
     * PLAYWRIGHT LOCATORS - Modern lazy-evaluation approach
     * 
     * LOCATOR ADVANTAGES:
     * 1. Never become stale - re-query DOM on each action
     * 2. Auto-retry - automatically retry if element not found
     * 3. Lazy evaluation - no DOM query until action is performed
     * 4. Composable - can be chained and filtered
     * 5. Framework support - better integration with async operations
     * 
     * SELECTOR ENGINE FEATURES:
     * - Supports CSS, XPath, text content, and custom engines
     * - Can pierce shadow DOM automatically
     * - Supports >> (deep combinators) and custom pseudo-classes
     * - Has built-in role-based selectors for accessibility
     */
    private Locator playwrightLogoutButton;
    private Locator playwrightMessageElement;

    // ========== CONSTRUCTORS ==========

    /**
     * SELENIUM CONSTRUCTOR with PageFactory initialization
     * 
     * INITIALIZATION PROCESS:
     * 1. super(driver) - Initialize parent BasePage with WebDriver
     * 2. PageFactory.initElements() - Scan for @FindBy annotations and create
     * proxies
     * 
     * UNDER THE HOOD:
     * PageFactory uses Java reflection to:
     * - Find all fields with @FindBy annotation
     * - Create dynamic proxies for WebElement fields
     * - Store locator strategy (id, css, etc.) in proxy
     * - Actual element lookup happens when element is first used (lazy proxy)
     * 
     * PERFORMANCE CONSIDERATION:
     * - Reflection has small overhead during initialization
     * - But provides cleaner syntax and reduced boilerplate
     * 
     * @param driver WebDriver instance for Selenium browser automation
     */
    public SecurePage(WebDriver driver) {
        super(driver); // INHERITANCE - call parent constructor

        // PageFactory pattern - Selenium specific initialization
        // Initializes all @FindBy annotated fields
        PageFactory.initElements(driver, this);
    }

    /**
     * PLAYWRIGHT CONSTRUCTOR - Simple and direct initialization
     * 
     * NO REFLECTION OR FACTORY PATTERN:
     * - More straightforward than Selenium
     * - No hidden magic or proxy objects
     * - Easier to debug and understand
     * - Better IDE support (autocomplete, refactoring)
     * 
     * LOCATOR CREATION:
     * - page.locator(selector) creates a Locator object
     * - Locator is lazy - doesn't search DOM until used
     * - Can be reused multiple times without going stale
     * - Supports strict mode (fails if multiple elements match)
     * 
     * @param page Playwright Page instance for browser automation
     */
    public SecurePage(Page page) {
        super(page); // INHERITANCE - call parent constructor

        // Direct locator initialization - no factory needed
        // More explicit and easier to understand than Selenium's approach

        // CSS class selector - finds element with both classes
        this.playwrightLogoutButton = page.locator(".icon-2x.icon-signout");

        // CSS ID selector - fastest and most reliable
        this.playwrightMessageElement = page.locator("#flash");

        // PLAYWRIGHT ALTERNATIVE SELECTORS (more robust):
        // - Text-based: page.locator("text=Logout")
        // - Role-based: page.getByRole("link", new
        // Page.GetByRoleOptions().setName("Logout"))
        // - Combined: page.locator("a:has-text('Logout')")

        // ADVANTAGE: If UI text changes from "Logout" icon to button,
        // role-based selectors still work without code changes
    }

    // ========== PUBLIC METHODS - Page Actions ==========

    /**
     * SELENIUM VERSION: Perform logout action
     * 
     * EXECUTION FLOW:
     * 1. clickElement() from BasePage is called
     * 2. WebDriverWait waits for element to be clickable (up to 10 seconds)
     * 3. ExpectedConditions.elementToBeClickable checks:
     * - Element is visible
     * - Element is enabled
     * - No overlay blocking it
     * 4. element.click() sends click command via WebDriver protocol
     * 5. HTTP request sent to browser driver (ChromeDriver, etc.)
     * 6. Driver executes click in browser
     * 
     * POTENTIAL ISSUES:
     * - StaleElementReferenceException if DOM changes between find and click
     * - ElementClickInterceptedException if element is covered
     * - Slower due to WebDriver protocol overhead
     */
    public void logout() {
        // SELENIUM: Explicit wait then click
        // Uses WebDriverWait from BasePage (10 second timeout)
        clickElement(logoutButton);

        // PERFORMANCE: Typically takes 500ms - 1.5s
        // Time includes: wait check, HTTP request, browser execution
    }

    /**
     * PLAYWRIGHT VERSION: Perform logout action
     * 
     * EXECUTION FLOW:
     * 1. clickElement() from BasePage is called with Locator
     * 2. Playwright auto-waits for element to be actionable (up to 30 seconds)
     * 3. Actionability checks (performed automatically every 100ms):
     * - Element is attached to DOM
     * - Element is visible (has width/height > 0)
     * - Element is stable (not animating)
     * - Element receives events (not covered)
     * - Element is enabled (not disabled)
     * 4. locator.click() communicates directly via Chrome DevTools Protocol
     * 5. Click executed immediately in browser
     * 
     * ADVANTAGES:
     * - No StaleElementReferenceException (Locator re-queries DOM)
     * - Faster execution (direct browser communication)
     * - More reliable (comprehensive actionability checks)
     * - Auto-retry on transient failures
     * 
     * @param isPlaywright Boolean flag to differentiate from Selenium version
     */
    public void logout(boolean isPlaywright) {
        // PLAYWRIGHT: Auto-wait and click
        // Built-in auto-waiting (30 second default timeout)
        clickElement(playwrightLogoutButton);

        // PERFORMANCE: Typically takes 200ms - 800ms
        // 40-60% faster than Selenium for same operation

        // DEBUGGING TIP: Playwright has excellent debugging:
        // - Run with PWDEBUG=1 for step-through debugging
        // - Auto-generates screenshots and videos on failure
        // - Better error messages with actionability check details
    }

    /**
     * SELENIUM VERSION: Get flash message text
     * 
     * PROCESS:
     * 1. WebDriverWait waits for element to be visible
     * 2. element.getText() retrieves visible text
     * 3. Returns concatenated text of element and descendants
     * 
     * getText() BEHAVIOR:
     * - Returns only visible text (CSS display/visibility matters)
     * - Trims whitespace
     * - Returns empty string if element is hidden
     * - Concatenates text from child elements
     * 
     * @return String containing the flash message text
     */
    public String getMessage() {
        // SELENIUM: Wait for visibility, then get text
        return messageElement.getText();

        // LIMITATION: If element has complex structure or shadow DOM,
        // getText() might not capture all content correctly
    }

    /**
     * PLAYWRIGHT VERSION: Get flash message text
     * 
     * DIFFERENCES FROM SELENIUM:
     * - innerText() similar to Selenium's getText()
     * - More accurate for modern web apps (handles shadow DOM better)
     * - Auto-waits for element to be attached
     * - Can use textContent() for all text including hidden
     * 
     * TEXT RETRIEVAL OPTIONS IN PLAYWRIGHT:
     * - innerText() - visible text (similar to Selenium)
     * - textContent() - all text including hidden elements
     * - innerHTML() - HTML content as string
     * 
     * @param isPlaywright Boolean flag to differentiate from Selenium version
     * @return String containing the flash message text
     */
    public String getMessage(boolean isPlaywright) {
        // PLAYWRIGHT: Auto-wait and get text
        return getElementText(playwrightMessageElement);

        // ADVANTAGE: Better handling of:
        // - Shadow DOM elements
        // - Dynamically loaded content
        // - SVG text elements
        // - Complex nested structures
    }

    /**
     * SELENIUM VERSION: Check if logout button is visible
     * 
     * VISIBILITY CHECK:
     * - isDisplayed() checks if element is visible on page
     * - Considers CSS properties: display, visibility, opacity
     * - Checks if element has height and width > 0
     * - Does NOT check if element is in viewport
     * 
     * COMMON ISSUES:
     * - May return true for elements outside viewport
     * - Can throw NoSuchElementException if element not found
     * - Requires try-catch for robust checking
     * 
     * @return boolean true if logout button is visible, false otherwise
     */
    public boolean isLogoutButtonVisible() {
        try {
            // SELENIUM: isDisplayed() checks CSS visibility properties
            return logoutButton.isDisplayed();
        } catch (Exception e) {
            // Element not found in DOM
            return false;
        }

        // LIMITATION: Not suitable for checking viewport visibility
        // Element might be off-screen but still return true
    }

    /**
     * PLAYWRIGHT VERSION: Check if logout button is visible
     * 
     * VISIBILITY CHECK DIFFERENCES:
     * - isVisible() is more comprehensive than Selenium's isDisplayed()
     * - Checks actual visibility including viewport consideration
     * - More reliable for modern web apps with dynamic rendering
     * - Built-in timeout (waits briefly before checking)
     * 
     * WHAT isVisible() CHECKS:
     * - Element is attached to DOM
     * - Element has non-zero bounding box
     * - Element's visibility CSS property is 'visible'
     * - Element's opacity is greater than 0.05
     * - Element is not hidden by other elements (optional check)
     * 
     * @param isPlaywright Boolean flag to differentiate from Selenium version
     * @return boolean true if logout button is visible, false otherwise
     */
    public boolean isLogoutButtonVisible(boolean isPlaywright) {
        // PLAYWRIGHT: More reliable visibility check
        return isElementVisible(playwrightLogoutButton);

        // ADVANTAGE:
        // - No exception handling needed
        // - More accurate for single-page applications
        // - Considers animations and transitions
        // - Better performance (direct browser query)

        // ALTERNATIVE CHECKS:
        // - locator.isEnabled() - checks if element is enabled
        // - locator.isEditable() - checks if input is editable
        // - locator.isChecked() - checks checkbox/radio state
    }

    /**
     * UTILITY METHOD: Get current page URL
     * 
     * Demonstrates difference in getting current URL between frameworks
     */
    public String getCurrentUrl() {
        // SELENIUM VERSION: Uses WebDriver's getCurrentUrl()
        if (driver != null) {
            return driver.getCurrentUrl();
        }

        // PLAYWRIGHT VERSION: Uses Page's url()
        if (page != null) {
            return page.url();
        }

        return "";
    }

    /**
     * UTILITY METHOD: Wait for page to load completely
     * 
     * Demonstrates different waiting strategies
     */
    public void waitForPageLoad() {
        // SELENIUM: No built-in wait for page load after initial navigation
        // Would need custom JavaScript executor:
        // ((JavascriptExecutor) driver).executeScript("return
        // document.readyState").equals("complete");

        // PLAYWRIGHT: Multiple wait options available
        if (page != null) {
            // Wait for load state
            page.waitForLoadState();

            // ALTERNATIVES:
            // page.waitForLoadState(LoadState.NETWORKIDLE); // Wait for network to be idle
            // page.waitForLoadState(LoadState.DOMCONTENTLOADED); // Wait for DOM ready
        }
    }
}
