package tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Selector playground focused on Playwright locators.
 *
 * Two curated tests show how to lean on static hooks (IDs, names, data-test
 * attributes) versus dynamic locators (partial matches, :has-text, XPath) so a
 * beginner can graduate toward advanced targeting patterns.
 */
public class PlaywrightSelectorTest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeClass
    public void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true));
    }

    @BeforeMethod
    public void spinUpSandbox() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1366, 768));
        page = context.newPage();
        page.setContent(buildSelectorPlayground());
    }

    @AfterMethod
    public void cleanContext() {
        if (context != null) {
            context.close();
        }
    }

    @AfterClass
    public void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Test(description = "Static selectors rely on IDs, names, and data-test hooks that rarely change.")
    public void demonstrateStaticSelectors() {
        Locator usernameField = page.locator("#static-username");
        Locator passwordField = page.locator("input[name='static-password']");
        Locator rememberMeCheckbox = page.locator("label.login-toggle input");
        Locator primaryCta = page.locator("[data-test='primary-cta']");

        usernameField.fill("qa.analyst@acme.dev");
        passwordField.fill("SuperSecret!");
        rememberMeCheckbox.check();
        primaryCta.click();

        String result = page.locator("#cta-result").innerText().trim();
        assertEquals(result, "CTA clicked via static selectors");
        assertEquals(usernameField.count(), 1);
        assertTrue(primaryCta.isVisible());
    }

    @Test(description = "Dynamic selectors respond to volatile IDs/text using partial matches and structure cues.")
    public void demonstrateDynamicSelectors() {
        page.locator("#add-user").click();
        page.locator("#add-user").click();

        Locator firstDynamicRow = page.locator("[data-row-id^='user-']").first();
        Locator newestRow = page.locator("[data-row-id^='user-']").last();

        assertTrue(firstDynamicRow.locator(".user-handle").isVisible());
        assertTrue(newestRow.locator(".user-handle").isVisible());

        Locator designCard = page.locator("li.feed-card:has-text('Design System Crash Course')");
        String pillText = designCard.locator(".content-pill").innerText().trim().toUpperCase();
        assertEquals(pillText, "PINNED");

        Locator runtimeButton = page.locator("button:has-text('Promote user')").nth(1);
        runtimeButton.click();

        String feedLog = page.locator("#feed-log").innerText().trim();
        assertTrue(feedLog.startsWith("Promoted"));

        Locator xpathFallback = page.locator("xpath=(//div[@data-row-id])[last()]");
        assertTrue(xpathFallback.innerText().contains("@dynamic"));
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
