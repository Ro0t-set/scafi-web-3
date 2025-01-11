package features
import java.time.Duration
import io.cucumber.scala.{EN, ScalaDsl, Scenario}
import org.openqa.selenium.By
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

class WebPage extends ScalaDsl with EN {

  val options = new FirefoxOptions()

  options.addArguments("--no-sandbox")
  options.addArguments("--disable-dev-shm-usage")
  options.addArguments("--headless")

  val driver = new FirefoxDriver(options)

  Given("I am on the Scafi Web Page") {
    driver.get("localhost:8080")
  }

  Then("the page title should start with {string}") {
    (titleStartsWith: String) =>
      WebDriverWait(driver, Duration.ofSeconds(5)).until(
        ExpectedConditions.titleContains(titleStartsWith)
      )

  }

  Then("the engine {string} is loaded") { (engine: String) =>
    WebDriverWait(driver, Duration.ofSeconds(10)).until(
      ExpectedConditions.jsReturnsValue(s"return $engine.name")
    )
  }

  Then("the canvas {string} is loaded") { (canvasClassName: String) =>
    WebDriverWait(driver, Duration.ofSeconds(10)).until(
      ExpectedConditions.presenceOfElementLocated(
        By.cssSelector(s"""canvas[data-engine*='$canvasClassName']""")
      )
    )
  }

  After {
    driver.quit()
  }
}
