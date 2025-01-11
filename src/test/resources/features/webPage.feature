Feature: Web Page Is Working

    @web
    Scenario: Page is loaded
        Given I am on the Scafi Web Page
        Then the page title should start with "ScaFi Web 3"
        Then the engine "EngineImpl" is loaded
        Then the canvas "three_canvas" is loaded
