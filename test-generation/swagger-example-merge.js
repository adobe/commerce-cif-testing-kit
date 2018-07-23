/*******************************************************************************
 *
 *    Copyright 2018 Adobe. All rights reserved.
 *    This file is licensed to you under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License. You may obtain a copy
 *    of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software distributed under
 *    the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 *    OF ANY KIND, either express or implied. See the License for the specific language
 *    governing permissions and limitations under the License.
 *
 ******************************************************************************/
const fs = require('fs');

// Parse command line
var argv = require('yargs')
    .usage('Usage: $0 -swagger [path] -example [path]')
    .describe('swagger', 'Path to swagger file.')
    .describe('example', 'Path to swagger example file.')
    .default('swagger', 'swagger.json')
    .demandOption(['swagger', 'example'])
    .argv;

// Parse source files
let swagger = JSON.parse(fs.readFileSync(argv.swagger));
let examples = JSON.parse(fs.readFileSync(argv.example));

// Add properties to swagger file
for(let path in examples.paths) {
    for(let method in examples.paths[path]) {
        for(let parameter in examples.paths[path][method].parameters) {
            // Find parameter in swagger file
            for(let sParameter of swagger.paths[path][method].parameters) {
                if(sParameter.name == parameter) {
                    // Add example parameters to object
                    for(let key in examples.paths[path][method].parameters[parameter]) {
                        sParameter[key] = examples.paths[path][method].parameters[parameter][key];
                    }
                }
            }
        }
    }
}

// Write file back
fs.writeFile(argv.swagger, JSON.stringify(swagger, null, 4), 'utf8', function (err) {
    if(err) {
        return console.log(err);
    }
    console.log("Added examples to %s.", argv.swagger);
});