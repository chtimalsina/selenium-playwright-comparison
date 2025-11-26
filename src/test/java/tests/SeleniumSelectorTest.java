package tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selenium-first equivalent of the selector playground used in Playwright
 * tests.
 *
 * While the DOM is the same, the examples focus on WebDriver-friendly APIs such
 * as By.id, By.cssSelector, By.xpath, chained findElements, and async waits.
 */
public class SeleniumSelectorTest {

    private WebDriver driver;

    @BeforeClass
    public void openBrowser() {
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1366, 768));
    }

    @BeforeMethod
    public void loadPlayground() {
        String encoded = Base64.getEncoder().encodeToString(buildSelectorPlayground().getBytes(StandardCharsets.UTF_8));
        String dataUrl = "data:text/html;base64," + encoded;
        driver.get(dataUrl);
    }

    @AfterClass(alwaysRun = true)
    public void quitBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(description = "Showcase battle-tested static selectors using By.id, name, className, and data-* attributes.")
    public void seleniumStaticSelectors() {
        WebElement usernameInput = driver.findElement(By.id("static-username"));
        WebElement passwordInput = driver.findElement(By.name("static-password"));
        WebElement rememberToggle = driver.findElement(By.cssSelector("label.login-toggle input"));
        WebElement primaryCta = driver.findElement(By.cssSelector("[data-test='primary-cta']"));

        usernameInput.sendKeys("architect@acme.dev");
        passwordInput.sendKeys("TestAutomationFTW");
        rememberToggle.click();
        primaryCta.click();

        WebElement ctaResult = driver.findElement(By.id("cta-result"));
        assertEquals(ctaResult.getText().trim(), "CTA clicked via static selectors");
    }

    @Test(description = "Demonstrate dynamic strategies: attribute wildcards, contains text, positional XPath, JS hooks.")
    public void seleniumDynamicSelectors() {
        driver.findElement(By.id("add-user")).click();
        driver.findElement(By.id("add-user")).click();

        WebElement firstRow = driver.findElement(By.cssSelector("[data-row-id^='user-']"));
        assertTrue(firstRow.findElement(By.className("user-handle")).getText().contains("@dynamic"));

        WebElement promoteButton = driver.findElements(By.cssSelector("button.promote-btn")).get(1);
        promoteButton.click();

        WebElement feedLog = driver.findElement(By.id("feed-log"));
        assertTrue(feedLog.getText().startsWith("Promoted"));

        WebElement pinnedCard = driver.findElement(
                By.xpath("//li[contains(@class,'feed-card')][.//strong[contains(text(),'Design System')]]"));
        String pillText = pinnedCard.findElement(By.className("content-pill")).getText().trim().toUpperCase();
        assertEquals(pillText, "PINNED");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        String lastRendered = (String) js.executeScript(
                "return document.querySelector('[data-row-id]:last-of-type').getAttribute('data-row-id');");
        assertTrue(lastRendered.startsWith("user-"));
    }

    private String buildSelectorPlayground() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='en'>\n");
        html.append("<head>\n");
        html.append("  <meta charset='UTF-8' />\n");
        html.append("  <title>Selector Playground</title>\n");
        html.append("  <style>\n");
        html.append(
                "    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; padding: 32px; background: #f5f7fb; }\n");
        html.append(
                "    section { background: white; padding: 24px; margin-bottom: 32px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.06); }\n");
        html.append("    .login-form label { display: block; margin-bottom: 12px; font-weight: 600; }\n");
        html.append(
                "    .login-form input { width: 100%; padding: 12px; border-radius: 8px; border: 1px solid #d6d9e0; margin-top: 6px; }\n");
        html.append("    .login-toggle { display: flex; align-items: center; gap: 8px; margin: 12px 0 24px; }\n");
        html.append(
                "    button { border: none; border-radius: 999px; padding: 12px 24px; background: #007aff; color: white; font-weight: 600; cursor: pointer; }\n");
        html.append("    ul.feed { list-style: none; padding: 0; margin: 0; }\n");
        html.append(
                "    li.feed-card { padding: 16px; border: 1px solid #e5e7ef; border-radius: 10px; margin-bottom: 16px; background: #fbfcff; }\n");
        html.append(
                "    .content-pill { padding: 2px 12px; border-radius: 12px; font-size: 12px; text-transform: uppercase; letter-spacing: 0.1em; background: #fff2d5; color: #c78100; margin-right: 8px; }\n");
        html.append("    #dynamic-users { display: grid; gap: 12px; margin-top: 16px; }\n");
        html.append(
                "    .user-card { border: 1px dashed #b8c0d6; padding: 12px; border-radius: 10px; background: #fff; display: flex; justify-content: space-between; align-items: center; }\n");
        html.append("    .user-handle { color: #3c3f55; font-weight: 600; }\n");
        html.append("    .log { margin-top: 16px; font-size: 14px; color: #5a6078; }\n");
        html.append("  </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("  <section class='login-form'>\n");
        html.append("    <h2>Static selector zone</h2>\n");
        html.append("    <label for='static-username'>Username</label>\n");
        html.append(
                "    <input id='static-username' type='email' name='static-username' placeholder='e.g. qa.tester@acme.dev' />\n");
        html.append("    <label for='static-password'>Password</label>\n");
        html.append(
                "    <input id='static-password' type='password' name='static-password' placeholder='Use a strong passphrase' />\n");
        html.append(
                "    <label class='login-toggle'><input type='checkbox' name='static-remember' />Keep me signed in</label>\n");
        html.append("    <button data-test='primary-cta' id='primary-cta'>Create account</button>\n");
        html.append("    <p id='cta-result' style='margin-top:12px;color:#1d1d1f;font-weight:600;'></p>\n");
        html.append("  </section>\n");
        html.append("  <section class='dynamic-feed'>\n");
        html.append("    <h2>Dynamic selector zone</h2>\n");
        html.append("    <button id='add-user'>Add beta tester</button>\n");
        html.append("    <ul class='feed'>\n");
        html.append("      <li class='feed-card' data-user-id='user-2048'>\n");
        html.append("        <span class='content-pill'>Pinned</span>\n");
        html.append("        <strong>Design System Crash Course</strong>\n");
        html.append("        <p>Perfect candidate for :has-text() and contains() demos.</p>\n");
        html.append("      </li>\n");
        html.append("    </ul>\n");
        html.append("    <div id='dynamic-users'></div>\n");
        html.append("    <p id='feed-log' class='log'></p>\n");
        html.append("  </section>\n");
        html.append("  <script>\n");
        html.append("    const ctaResult = document.getElementById('cta-result');\n");
        html.append("    document.getElementById('primary-cta').addEventListener('click', () => {\n");
        html.append("      ctaResult.textContent = 'CTA clicked via static selectors';\n");
        html.append("    });\n");
        html.append("    let counter = 1;\n");
        html.append("    document.getElementById('add-user').addEventListener('click', () => {\n");
        html.append("      const container = document.getElementById('dynamic-users');\n");
        html.append("      const card = document.createElement('div');\n");
        html.append("      const generatedId = `user-${Date.now()}-${counter++}`;\n");
        html.append("      card.className = 'user-card';\n");
        html.append("      card.setAttribute('data-row-id', generatedId);\n");
        html.append(
                "      card.innerHTML = `<span class='user-handle'>@dynamic_${counter}</span><button class='promote-btn'>Promote user</button>`;\n");
        html.append("      container.appendChild(card);\n");
        html.append("      document.getElementById('feed-log').textContent = 'Rendered ' + generatedId;\n");
        html.append("    });\n");
        html.append("    document.body.addEventListener('click', (event) => {\n");
        html.append("      if (event.target.classList.contains('promote-btn')) {\n");
        html.append("        const parent = event.target.closest('[data-row-id]');\n");
        html.append(
                "        document.getElementById('feed-log').textContent = 'Promoted ' + parent.getAttribute('data-row-id');\n");
        html.append("      }\n");
        html.append("    });\n");
        html.append("  </script>\n");
        html.append("</body>\n");
        html.append("</html>\n");
        return html.toString();
    }
}
