package features
import java.time.Duration
import io.cucumber.scala.{EN, ScalaDsl, Scenario}
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

class WebPage extends ScalaDsl with EN {
  final private val driver = new FirefoxDriver();

  Given("I am on the Scafi Web Page") {
    driver.get("https://www.tommasopatriti.me/scafi-web-3/dist/")
  }

  Then("the page title should start with {string}") {
    (titleStartsWith: String) =>
      WebDriverWait(driver, Duration.ofSeconds(3)).until(
        ExpectedConditions.titleContains(titleStartsWith)
      )

  }

  After {
    driver.quit()
  }
}
