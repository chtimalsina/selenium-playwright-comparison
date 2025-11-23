package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

/**
 * INHERITANCE & PAGE OBJECT MODEL - LoginPage demonstrates both Selenium and
 * Playwright implementations
 * 
 * This class extends BasePage and represents the Login page of the application.
 * It demonstrates key differences between Selenium and Playwright approaches.
 * 
 * KEY ARCHITECTURAL DIFFERENCES:
 * 
 * 1. ELEMENT LOCATION:
 * SELENIUM:
 * - Uses @FindBy annotations with PageFactory pattern
 * - Elements are initialized when PageFactory.initElements() is called
 * - Elements are stored as WebElement fields
 * - Eager evaluation: Elements are located immediately (can throw
 * NoSuchElementException)
 * 
 * PLAYWRIGHT:
 * - Uses page.locator() method to create Locator objects
 * - No annotations or factory pattern needed
 * - Locators are lazy: Not evaluated until an action is performed
 * - More resilient to timing issues and dynamic content
 * 
 * 2. PAGE FACTORY PATTERN:
 * SELENIUM:
 * - Requires PageFactory.initElements() in constructor
 * - Uses reflection to initialize fields annotated with @FindBy
 * - Adds some overhead but provides clean syntax
 * 
 * PLAYWRIGHT:
 * - No factory pattern needed - direct locator creation
 * - Simpler initialization, less magic/reflection
 * - More explicit and easier to debug
 * 
 * 3. SELECTOR STRATEGIES:
 * SELENIUM: Supports - id, name, className, tagName, linkText, partialLinkText,
 * cssSelector, xpath
 * PLAYWRIGHT: Supports - CSS, XPath, Text, and its own powerful selector engine
 * (can combine strategies)
 */
public class LoginPage extends BasePage {

    // ========== SELENIUM IMPLEMENTATION ==========

    /**
     * ENCAPSULATION - Private fields with Selenium's @FindBy annotations
     * 
     * @FindBy uses PageFactory pattern to initialize elements
     *         Syntax: @FindBy(strategy = "value")
     *         Common strategies: id, name, className, css, xpath, linkText
     */

    @FindBy(id = "username")
    private WebElement usernameField; // Input field for username

    @FindBy(id = "password")
    private WebElement passwordField; // Input field for password

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton; // Submit button

    @FindBy(id = "flash")
    private WebElement messageElement; // Flash message element (success/error)

    // ========== PLAYWRIGHT IMPLEMENTATION ==========

    /**
     * Playwright Locators - Lazy evaluation approach
     * 
     * Unlike Selenium's eager WebElements, Playwright Locators:
     * - Don't search the DOM until an action is performed
     * - Are automatically retried if element is not found
     * - More resilient to dynamic content and timing issues
     * - Can be stored and reused without becoming stale
     * 
     * PLAYWRIGHT ADVANTAGE: No StaleElementReferenceException
     * Selenium WebElements can become "stale" if DOM changes, requiring re-location
     * Playwright Locators are always fresh because they re-query the DOM on each
     * action
     */
    private Locator playwrightUsernameField;
    private Locator playwrightPasswordField;
    private Locator playwrightLoginButton;
    private Locator playwrightMessageElement;

    // ========== CONSTRUCTORS ==========

    /**
     * SELENIUM CONSTRUCTOR
     * 
     * Demonstrates Selenium's PageFactory pattern initialization
     * 
     * STEPS:
     * 1. Call parent constructor with WebDriver (super(driver))
     * 2. Initialize page elements using PageFactory.initElements()
     * 
     * PageFactory.initElements() uses reflection to:
     * - Find all fields annotated with @FindBy
     * - Create WebElement proxies for each field
     * - Store locator strategies for lazy initialization
     * 
     * @param driver WebDriver instance for browser control
     */
    public LoginPage(WebDriver driver) {
        super(driver); // Calling parent constructor - demonstrates INHERITANCE

        // PageFactory initialization - required for @FindBy annotations to work
        // This is specific to Selenium and adds some reflection overhead
        PageFactory.initElements(driver, this);
    }

    /**
     * PLAYWRIGHT CONSTRUCTOR
     * 
     * Demonstrates Playwright's simpler initialization approach
     * 
     * ADVANTAGES OVER SELENIUM:
     * - No PageFactory needed - more straightforward
     * - No reflection overhead
     * - Locators are created explicitly (easier to understand and debug)
     * - Can use variables for selectors (more flexible)
     * 
     * @param page Playwright Page instance for browser control
     */
    public LoginPage(Page page) {
        super(page); // Calling parent constructor

        // Initialize Playwright locators - no factory pattern needed
        // page.locator() creates a Locator that will be evaluated when used

        // CSS ID selector
        this.playwrightUsernameField = page.locator("#username");

        // CSS ID selector
        this.playwrightPasswordField = page.locator("#password");

        // CSS attribute selector
        this.playwrightLoginButton = page.locator("button[type='submit']");

        // CSS ID selector
        this.playwrightMessageElement = page.locator("#flash");

        // PLAYWRIGHT ALTERNATIVE SELECTORS (more powerful):
        // - Text-based: page.locator("text=Login")
        // - Combined: page.locator("button:has-text('Login')")
        // - Role-based: page.getByRole("button", new
        // Page.GetByRoleOptions().setName("Login"))
        // - Chained: page.locator("form").locator("button")
    }

    // ========== PUBLIC METHODS - Page Actions ==========

    /**
     * SELENIUM VERSION: Perform login action
     * 
     * Demonstrates method overloading and Selenium's approach to page interactions
     * 
     * PROCESS:
     * 1. Enter username (uses inherited enterText method)
     * 2. Enter password (uses inherited enterText method)
     * 3. Click login button (uses inherited clickElement method)
     * 
     * Each action includes explicit waits (defined in BasePage)
     * 
     * @param username String username to enter
     * @param password String password to enter
     */
    public void login(String username, String password) {
        // SELENIUM: Each interaction requires explicit wait handling
        // enterText() waits for visibility, clears, then sends keys
        enterText(usernameField, username);
        enterText(passwordField, password);

        // clickElement() waits for element to be clickable, then clicks
        clickElement(loginButton);

        // PERFORMANCE NOTE: Selenium typically takes 2-4 seconds for this sequence
        // due to WebDriver protocol overhead (HTTP requests for each command)
    }

    /**
     * PLAYWRIGHT VERSION: Perform login action
     * 
     * METHOD OVERLOADING: Same method name, different parameters (Page vs WebDriver
     * context)
     * 
     * KEY DIFFERENCES FROM SELENIUM:
     * - Auto-waits are built-in (no explicit wait configuration needed)
     * - Faster execution (direct browser communication via CDP)
     * - fill() is faster than clear() + sendKeys()
     * - More reliable (automatic retries on transient failures)
     * 
     * @param username     String username to enter
     * @param password     String password to enter
     * @param isPlaywright Boolean flag to differentiate from Selenium version
     */
    public void login(String username, String password, boolean isPlaywright) {
        // PLAYWRIGHT: Auto-waits and optimized interactions
        // fill() automatically waits, clears, and fills in one operation
        enterText(playwrightUsernameField, username);
        enterText(playwrightPasswordField, password);

        // click() automatically waits for element to be actionable
        clickElement(playwrightLoginButton);

        // PERFORMANCE NOTE: Playwright typically completes this in 1-2 seconds
        // 50-60% faster than Selenium for the same operations
    }

    /**
     * SELENIUM VERSION: Get flash message text
     * 
     * Returns the text content of the flash message element
     * Used to verify login success or error messages
     * 
     * @return String containing the message text
     */
    public String getMessage() {
        // SELENIUM: getText() returns visible text after waiting for visibility
        return messageElement.getText();
    }

    /**
     * PLAYWRIGHT VERSION: Get flash message text
     * 
     * DIFFERENCES FROM SELENIUM:
     * - innerText() vs getText() - similar functionality but Playwright's
     * implementation
     * is more accurate for complex scenarios (shadow DOM, hidden elements)
     * - Auto-waits for element to exist before getting text
     * 
     * @param isPlaywright Boolean flag to differentiate from Selenium version
     * @return String containing the message text
     */
    public String getMessage(boolean isPlaywright) {
        // PLAYWRIGHT: innerText() gets visible text content
        return getElementText(playwrightMessageElement);
    }

    /**
     * SELENIUM VERSION: Navigate to the login page
     * 
     * Uses WebDriver's get() method to navigate to URL
     * 
     * SELENIUM NAVIGATION:
     * - driver.get(url) - Navigates and waits for page load
     * - Triggers onload event
     * - Returns after document.readyState is "complete"
     */
    public void openPage() {
        // SELENIUM: Simple navigation using get() method
        driver.get("https://the-internet.herokuapp.com/login");

        // IMPLICIT WAIT: Selenium waits for page load event
        // Does NOT wait for dynamic content or AJAX calls
    }

    /**
     * PLAYWRIGHT VERSION: Navigate to the login page
     * 
     * DIFFERENCES FROM SELENIUM:
     * - page.navigate() has more options (waitUntil, timeout, etc.)
     * - Can wait for different load states: load, domcontentloaded, networkidle
     * - Better handling of single-page applications (SPAs)
     * - Can wait for network to be idle (useful for AJAX-heavy pages)
     * 
     * @param isPlaywright Boolean flag to differentiate from Selenium version
     */
    public void openPage(boolean isPlaywright) {
        // PLAYWRIGHT: Navigate with better control over loading states
        page.navigate("https://the-internet.herokuapp.com/login");

        // DEFAULT: Waits for 'load' event (similar to Selenium)
        // OPTIONAL: Can wait for 'networkidle' for AJAX-heavy pages
        // page.navigate(url, new
        // Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
    }
}
