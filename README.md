# DFP Fetch Lambda

Fetches a report from DFP and writes it to S3.

## To deploy
0. Run sbt `assembly` task.
0. Run `./aws-create` script.
0. On subsequent updates, run `./aws-update-code` script.
