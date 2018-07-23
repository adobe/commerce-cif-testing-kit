# Adobe Commerce CIF API Testing Kit
This repository contains tools to generate HTTP-based unit and Toughday2 tests for the CIF API.

The purpose of the test kit is provided some CIF API compliance checks to test CIF implementations from Adobe and integration partners. Customers can use it as well to validate that their customizations don't break API compliance.

## Structure
* `test-generation` Toolkit to generate test cases based on templates and the Swagger API specification.
* `cloud-client` Java Client library to write JUnit test cases against HTTP endpoints.
* `performance` Sample project for Toughday2 performance tests.
* `unit` Sample project for JUnit tests.

## Test Case Generation
Please refer to the [README.md](test-generation/README.md) in the `test-generation` folder.

## Runtime REST API Deployment
Since the test cases are generated from the Swagger API definition, that describes a REST API, 
Adobe I/O Runtime has to be configured to expose a REST API on top of the action deployment.
To do that, please run the following command:

```bash
wsk api create -c path/to/swagger.json
```

## Running Unit Tests
First do a `mvn clean install` in the root folder to install all dependencies. To run the unit tests, execute the following command in the `unit` folder:
```bash
mvn clean verify
```

## Running Toughday2 Tests with ELK
### 1. Elasticsearch
```bash
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.2.3
```

Create the indexes `cif-results` and `cif-results-activation` using:
```bash
curl -X PUT -d @logstash/cif-results-template.json http://HOSTNAME:9200/cif-results
curl -X PUT -d @logstash/cif-results-activation-template.json http://HOSTNAME:9200/cif-results-activation
```

### 2. Kibana
```bash
docker run -p 5601:5601 -e ELASTICSEARCH_URL=http://HOSTNAME:9200 docker.elastic.co/kibana/kibana:6.2.3
```

### 3. Logstash
We provide two logstash configs. The first config `logstash/toughday.conf` converts the Toughday CSV output and reports it to elasticsearch. The second config `logstash/openwhisk.conf` polls the activation logs from your OpenWhisk environment via HTTP and reports it as well.

```bash
# Logstash for Toughday
docker run -it --rm \
-v $(pwd)/results:/results \
-e "ES_INDEX=cif-results" \
-e "ES_USERNAME=user" \
-e "ES_PASSWORD=password" \
-v $(pwd)/logstash.conf:/usr/share/logstash/pipeline/logstash.conf \
docker.elastic.co/logstash/logstash:6.2.3

# Build Logstash container that includes OpenWhisk plugin
docker build -t logstash-openwhisk ./logstash

# Logstash for OpenWhisk activations
docker run -it --rm --name logstash \
-e "OW_HOSTNAME=runtime.adobe.io" \
-e "OW_NAMESPACE=namespace" \
-e "OW_USERNAME=username" \
-e "OW_PASSWORD=password" \
-e "ES_INDEX=cif-results-activation" \
-e "ES_USERNAME=username" \
-e "ES_PASSWORD=password" \
-v $(pwd)/logstash/openwhisk.conf:/usr/share/logstash/pipeline/logstash.conf \
logstash-openwhisk:latest
```
For the `OW_username` environment variable, please use the first part of your API token (before the `:`). For `OW_password` use the latter part.

### 5. Toughday

#### Build Tests
Configure Maven to use the public Adobe Maven repository as described here: https://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html.

Build the Toughday tests with the following command from the root folder:
```bash
mvn clean install
```

You can run the tests with the following commands:

#### Simulate Users with Think Time (Jar)
```bash
java -jar toughday2.jar \
--add GetCategoriesTest \
--add GetProductsIdTest \
--add GetProductsSearchTest \
--add GetCategoriesIdTest \
--add ./performance/cloud-perf-example/target/cloud-perf-example-1.0-SNAPSHOT.jar \
--protocol=https \
--host=runtime.adobe.io \
--port=443 \
--contextpath=/apis/namespace/commerce \
--installsamplecontent=false \
--runmode type=normal concurrency=100 waittime=2000 \
--add CSVPublisher rawpublish=true rawfilepath=/results/results.csv append=true
```

#### Constant Load (Jar)
```bash
java -jar toughday2.jar \
--add GetCategoriesTest \
--add GetProductsIdTest \
--add GetProductsSearchTest \
--add GetCategoriesIdTest \
--add ./performance/cloud-perf-example/target/cloud-perf-example-1.0-SNAPSHOT.jar \
--protocol=https \
--host=runtime.adobe.io \
--port=443 \
--contextpath=/apis/namespace/commerce \
--installsamplecontent=false \
--runmode type=constantload load=1 \
--add CSVPublisher rawpublish=true rawfilepath=/results/results.csv append=true
```

### Toughday2 Documentation
For more information on Toughday2 and the download, refer to:
https://helpx.adobe.com/experience-manager/6-4/sites/developing/using/tough-day.html
