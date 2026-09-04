Feature: Trainer workload processing
  As the training-queue consumer
  I want incoming TrainerWorkloadRequest messages
  to update the trainer's monthly workload summary correctly

  Scenario: A single ADD workload request creates a new trainer summary
    When an ADD workload request for trainer "Bekzat.Isakov" with duration 60 for 1 month(s) from now is published
    Then the trainer summary for "Bekzat.Isakov" should eventually show 60 minutes for 1 month(s) from now

  Scenario: Two ADD workload requests for the same month accumulate duration
    When an ADD workload request for trainer "Bekzat.Isakov" with duration 40 for 2 month(s) from now is published
    And an ADD workload request for trainer "Bekzat.Isakov" with duration 25 for 2 month(s) from now is published
    Then the trainer summary for "Bekzat.Isakov" should eventually show 65 minutes for 2 month(s) from now

  Scenario: A DELETE workload request decreases duration and floors at zero
    When an ADD workload request for trainer "Bekzat.Isakov" with duration 30 for 3 month(s) from now is published
    And a DELETE workload request for trainer "Bekzat.Isakov" with duration 50 for 3 month(s) from now is published
    Then the trainer summary for "Bekzat.Isakov" should eventually show 0 minutes for 3 month(s) from now

  Scenario: An invalid workload request with a blank trainer username is never applied
    When an invalid workload request with a blank trainer username is published
    Then no trainer summary should ever be created for a blank username
