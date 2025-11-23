package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import java.time.Duration;

/**
 * INHERITANCE & ABSTRACTION - Base class demonstrating OOP principles
 * 
 * This class serves as a parent class for all page objects, providing common
 * functionality that child classes can inherit and reuse.
 * 
 * KEY DIFFERENCES BETWEEN SELENIUM AND PLAYWRIGHT:
 * 
 * 1. WAIT MECHANISM:
 * - Selenium: Requires explicit waits (WebDriverWait) to be configured manually
 * Developer must specify conditions and timeout durations
 * - Playwright: Has built-in auto-waiting for most actions (clicks, fills,
 * etc.)
 * Automatically waits for elements to be actionable before interacting
 * 
 * 2. ELEMENT INTERACTION:
 * - Selenium: Works with WebElement objects obtained from driver.findElement()
 * Elements are eagerly evaluated (found immediately)
 * - Playwright: Uses Locator objects which are lazy evaluated
 * Locators are created but elements aren't searched until action is performed
 * 
 * 3. API DESIGN:
 * - Selenium: More verbose, requires chaining actions explicitly
 * - Playwright: More concise, chainable API with better TypeScript-like syntax
 * 
 * 4. PERFORMANCE:
 * - Selenium: Generally slower due to extra HTTP requests for WebDriver
 * protocol
 * - Playwright: Faster execution due to direct browser communication via
 * DevTools Protocol
 */
public class BasePage {
    // ENCAPSULATION - Protected fields accessible only to child classes

    // Selenium components
    protected WebDriver driver; // WebDriver instance for browser control
    protected WebDriverWait wait; // Explicit wait mechanism for Selenium

    // Playwright components
    protected Page page; // Playwright Page instance (similar to WebDriver)

    /**
     * CONSTRUCTOR for Selenium-based page objects
     * 
     * @param driver WebDriver instance to control the browser
     * 
     *               SELENIUM INITIALIZATION:
     *               - Sets up WebDriver instance
     *               - Creates WebDriverWait with 10-second timeout
     *               - Wait must be used explicitly in methods to avoid
     *               NoSuchElementException
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        // Explicit wait configuration - required for Selenium to avoid race conditions
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * CONSTRUCTOR for Playwright-based page objects
     * 
     * @param page Playwright Page instance to control the browser
     * 
     *             PLAYWRIGHT INITIALIZATION:
     *             - Sets up Page instance (no separate wait object needed)
     *             - Playwright has built-in auto-waiting mechanism
     *             - Default timeout is 30 seconds (configurable globally or
     *             per-action)
     */
    public BasePage(Page page) {
        this.page = page;
        // No explicit wait configuration needed - Playwright handles this automatically
    }

    /**
     * POLYMORPHISM & ENCAPSULATION - Click operation with different implementations
     * 
     * SELENIUM VERSION:
     * - Explicitly waits for element to be clickable
     * - Uses ExpectedConditions.elementToBeClickable() to ensure:
     * 1. Element is visible
     * 2. Element is enabled
     * 3. Element is not obscured
     * - Performs click action
     * 
     * @param element WebElement to click (Selenium)
     */
    protected void clickElement(WebElement element) {
        // SELENIUM: Explicit wait until element is in clickable state
        // This prevents ElementNotInteractableException or
        // StaleElementReferenceException
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    /**
     * PLAYWRIGHT VERSION of click operation
     * 
     * KEY DIFFERENCES FROM SELENIUM:
     * - No explicit wait needed - Playwright auto-waits for element to be
     * actionable
     * - Auto-waiting checks:
     * 1. Element is attached to DOM
     * 2. Element is visible
     * 3. Element is stable (not animating)
     * 4. Element receives events (not covered by another element)
     * 5. Element is enabled
     * - Timeout is 30 seconds by default (vs our 10 seconds in Selenium)
     * 
     * @param locator Playwright Locator (lazy-evaluated selector)
     */
    protected void clickElement(Locator locator) {
        // PLAYWRIGHT: Auto-wait is implicit - just call click()
        // Playwright automatically waits for the element to be actionable
        locator.click();

        // PERFORMANCE NOTE: Playwright is typically 2-3x faster here because:
        // 1. Direct browser communication (no WebDriver protocol overhead)
        // 2. Smarter waiting algorithm that checks actionability more efficiently
    }

    /**
     * SELENIUM VERSION: Enter text into an input field
     * 
     * PROCESS:
     * 1. Wait for element to be visible (ExpectedConditions.visibilityOf)
     * 2. Clear existing text (element.clear())
     * 3. Send new text (element.sendKeys())
     * 
     * @param element WebElement representing input field
     * @param text    String to enter into the field
     */
    protected void enterText(WebElement element, String text) {
        // SELENIUM: Explicit wait for visibility
        // visibilityOf() ensures element is present in DOM and has height/width > 0
        wait.until(ExpectedConditions.visibilityOf(element));

        // Clear existing content first (best practice to avoid appending)
        element.clear();

        // Send keys one by one (simulates typing)
        element.sendKeys(text);
    }

    /**
     * PLAYWRIGHT VERSION: Enter text into an input field
     * 
     * KEY DIFFERENCES FROM SELENIUM:
     * - fill() method clears and types in one action (more efficient)
     * - Auto-waits for element to be editable (visible, enabled, not readonly)
     * - Types text faster than Selenium's sendKeys (can be configured to type
     * instantly)
     * - Better handling of special characters and international keyboards
     * 
     * @param locator Playwright Locator for the input field
     * @param text    String to enter into the field
     */
    protected void enterText(Locator locator, String text) {
        // PLAYWRIGHT: fill() automatically:
        // 1. Waits for element to be editable
        // 2. Clears existing content
        // 3. Types new text (instant by default, can be configured to type slower)
        locator.fill(text);

        // ALTERNATIVE: For simulating realistic typing with delays
        // locator.type(text, new Locator.TypeOptions().setDelay(100));
    }

    /**
     * SELENIUM VERSION: Get text content from an element
     * 
     * @param element WebElement to get text from
     * @return String containing the visible text of the element
     */
    protected String getElementText(WebElement element) {
        // SELENIUM: Wait for visibility before getting text
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText();
    }

    /**
     * PLAYWRIGHT VERSION: Get text content from an element
     * 
     * DIFFERENCES:
     * - textContent() returns all text including hidden elements
     * - innerText() returns only visible text (more similar to Selenium's
     * getText())
     * - Auto-waits for element to be attached to DOM
     * 
     * @param locator Playwright Locator for the element
     * @return String containing the text content
     */
    protected String getElementText(Locator locator) {
        // PLAYWRIGHT: innerText() for visible text (similar to Selenium's getText())
        // Auto-waits for element to be attached to DOM
        return locator.innerText();

        // ALTERNATIVE: Use textContent() to get all text including hidden
        // return locator.textContent();
    }

    /**
     * SELENIUM VERSION: Check if element is visible
     * 
     * @param element WebElement to check
     * @return boolean true if element is displayed, false otherwise
     */
    protected boolean isElementVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            // Element not found or not in DOM
            return false;
        }
    }

    /**
     * PLAYWRIGHT VERSION: Check if element is visible
     * 
     * DIFFERENCES:
     * - isVisible() is more reliable than Selenium's isDisplayed()
     * - Checks actual visibility in viewport, not just CSS display property
     * - Auto-waits briefly before checking (configurable)
     * 
     * @param locator Playwright Locator for the element
     * @return boolean true if element is visible, false otherwise
     */
    protected boolean isElementVisible(Locator locator) {
        // PLAYWRIGHT: isVisible() checks if element is actually visible
        // More accurate than Selenium - considers viewport, opacity, dimensions
        return locator.isVisible();
    }
}
