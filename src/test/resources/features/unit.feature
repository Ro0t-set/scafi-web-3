Feature: Unit Test Feature

  @unit
  Scenario Outline: Unit Test Scenario
    Then The unit tests called "<testName>" should pass
    Examples:
      | testName                 |
      | state.AnimationStateSpec |
      | state.GraphStateSpec     |
      | API.NodeParserSpec       |
