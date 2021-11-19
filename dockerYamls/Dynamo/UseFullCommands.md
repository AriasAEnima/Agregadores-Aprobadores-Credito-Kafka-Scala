# Commands

## Configure Credentials

```aws configure```

Solo necesita la region, de resto puede ser fake.

## Create a table
``` aws dynamodb create-table --cli-input-json file://dockerYamls/Dynamo/tables/DataSourcesResponses.json --endpoint-url http://localhost:8000 ```

``` aws dynamodb create-table --cli-input-json file://dockerYamls/Dynamo/tables/ResultTable.json --endpoint-url http://localhost:8000 ```

``` aws dynamodb create-table --cli-input-json file://dockerYamls/Dynamo/tables/ResultTableCampaign.json --endpoint-url http://localhost:8000 ```


## List Tables 
``` aws dynamodb list-tables --endpoint-url http://localhost:8000 ```


## Delete Table
``` aws dynamodb delete-table --table-name DataSourcesResponses --endpoint-url http://localhost:8000 ```

``` aws dynamodb delete-table --table-name ResultTable --endpoint-url http://localhost:8000 ```

``` aws dynamodb delete-table --table-name ResultTableCampaign --endpoint-url http://localhost:8000 ```
