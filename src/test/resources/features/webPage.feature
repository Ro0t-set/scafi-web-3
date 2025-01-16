Feature: Web Page Is Working

    @web
    Scenario: Page is loaded
        Given I am on the Scafi Web Page
        Then the page title should start with "ScaFi Web 3"
        Then the engine "EngineImpl" is loaded
        Then the canvas "three_canvas" is loaded


    @web
    Scenario: High Update Rate Support
        Given I am on the Scafi Web Page
        Then the engine "EngineImpl" is loaded
        Then the graph 10x10x2 should support more than "30" updates per second
