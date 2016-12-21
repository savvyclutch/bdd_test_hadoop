Feature: CallStream normal data folder structure
  Check is CallStream job creates the correct file/directory structure
  for the collected callstream data

  Scenario: processing data from one user from UA
    Given a file with theme calls_log containing the following lines
      | country |
      | UA      |
    When I run the CallStream job
    Then the following directory structure should be created:
      | UA/ |
    And folder 'UA/' should contain following files:
      | part-00000-00000 |

  Scenario: processing data from two partners from US
    Given a file with theme calls_log containing the following lines
      | country |
      | UA      |
      | US      |
    When I run the CallStream job
    Then the following directory structure should be created:
      | UA/ |
      | US/ |
    And folder 'UA/' should contain following files:
      | part-00000-00000 |
    And folder 'US/' should contain following files too:
      | part-00000-00001 |
