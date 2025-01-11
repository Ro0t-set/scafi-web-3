package features

import java.time.Duration
import io.cucumber.scala.{EN, ScalaDsl, Scenario}
import org.openqa.selenium.{By, WebDriver}
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.edge.{EdgeDriver, EdgeOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

object BrowserFactory:
  def createDriver(browserType: String, isLocal: Boolean): WebDriver =
    browserType.toLowerCase match
      case "firefox" =>
        val options = new FirefoxOptions()
        if (!isLocal) {
          options.addArguments("--no-sandbox")
          options.addArguments("--disable-dev-shm-usage")
          options.addArguments("--headless")
        }
        new FirefoxDriver(options)

      case "chrome" =>
        val options = new ChromeOptions()
        if (!isLocal) {
          options.addArguments("--no-sandbox")
          options.addArguments("--disable-dev-shm-usage")
          options.addArguments("--headless")
        }
        new ChromeDriver(options)

      case "edge" =>
        val options = new EdgeOptions()
        if (!isLocal) {
          options.addArguments("--no-sandbox")
          options.addArguments("--disable-dev-shm-usage")
          options.addArguments("--headless")
        }
        new EdgeDriver(options)

@SuppressWarnings(Array("org.wartremover.warts.All"))
class WebPageSteps extends ScalaDsl with EN {

  println(System.getProperty("testEnv", "ci"))
  println(System.getProperty("browser", "firefox"))

  private val env     = System.getProperty("testEnv", "ci")
  private val isLocal = env == "local"
  var driver: WebDriver = BrowserFactory.createDriver(
    System.getProperty("browser", "firefox"),
    isLocal
  )

  Given("I am on the Scafi Web Page") {
    driver.get("http://localhost:8080")
  }

  Then("the page title should start with {string}") {
    (titleStartsWith: String) =>
      WebDriverWait(driver, Duration.ofSeconds(5))
        .until(ExpectedConditions.titleContains(titleStartsWith))
  }

  Then("the engine {string} is loaded") { (engine: String) =>
    WebDriverWait(driver, Duration.ofSeconds(10))
      .until(ExpectedConditions.jsReturnsValue(s"return $engine.name"))
  }

  /** If we are in CI/CD mode, we skip this step entirely. If we are in local
    * mode, we perform the canvas check.
    */
  Then("the canvas {string} is loaded") { (canvasClassName: String) =>
    if (!isLocal) {
      println(s"Skipping canvas check in CI/CD mode (env=$env)")
    } else {
      WebDriverWait(driver, Duration.ofSeconds(10)).until(
        ExpectedConditions.visibilityOfElementLocated(
          By.xpath(s"//*[@id='$canvasClassName']/canvas")
        )
      )
    }
  }

  After("@web") { (scenario: Scenario) =>
    println(s"[${scenario.getName}] => Tearing down driver. Mode: $env")
    if (driver != null) {
      driver.quit()
    }
  }
}
