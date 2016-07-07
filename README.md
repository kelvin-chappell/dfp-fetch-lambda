# DFP Fetch Lambda

Fetches a report from DFP and writes it to S3.

## To test report fetching and content
Run `sbt -Ddev=true run`

## To deploy
* Run `scripts/aws-create` script for initial deployment.
* On subsequent updates, run `scripts/aws-update-code` script.
* If configuration has changed, run `scripts/aws-update-configuration`.
