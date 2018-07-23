# CIF API Test Case Generation

## Instructions
1. Copy your `swagger.json` in `test-generation` folder.
2. If you have examples provided in your `swagger.json`, continue with step 4.
3. Create a `swagger-examples.json` file that includes examples for parameters and execute `npm run merge`. An example file is provided: [swagger-examples.json](swagger-examples.json).
4. To generate unit test cases, run `npm run generate`. Test cases are generated in the `/unit` project. Also add the `swagger.json` file to the resources folder.
5. To generate Toughday2 test cases, run `npm run toughday`. Test cases are generated in the `/performance/cloud-perf-example` project.

## Customization
This project generates JUnit tests which can be used alone or together with Toughday. 
The test case generation can be customized by updating the `junit-template.tpl` and `generate-tests.js`.
