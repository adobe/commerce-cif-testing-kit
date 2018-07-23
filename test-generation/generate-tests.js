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

const handlebars = require('handlebars');
const fs = require('fs');
const path = require('path');

/**
 * TEMPLATE HELPER
 */

let capitalize = function(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
};

let getParameterDataTypeJava = function(parameter) {
    if(parameter.type == "string" || parameter.in == "body") {
        return "String";
    } else if(parameter.type == "integer") {
        return "int";
    } else if(parameter.type == "array") {
        return getParameterDataTypeJava(parameter.items);
    }
    return "";
};

let getExampleForParameter = function(action, name) {
    for(let param of action.parameters) {
        if(param.name == name) {
            if('example' in param) {
                return param.example;
            }
            if('examples' in param) {
                for (let example in param.examples) {
                    return param.examples[example].value;
                }
            }
        }
    }
    return undefined
};

let getActionName = function(path, method) {
    let parts = [method]
    parts = parts.concat(path.split(/[\/{}]/));
    return parts.reduce(function(a, v) { 
        if(a == "") { 
            return a += v; 
        } 
        return a += v.charAt(0).toUpperCase() + v.slice(1) 
    }, "");
};

let getActionParameters = function(action) {
    let params = [];
    for(let parameter of action.parameters) {
        params.push(parameter);
    }
    let ret = [];
  
    for(let param of params) {
      ret.push(getParameterDataTypeJava(param) + ' ' + param.name.replace('-', ''));
    }

    return ret.join(', ');
};

let getActionNameCapitalized = function(path, method) {
    return capitalize(getActionName(path, method));
};

let getActionParameterValues = function(action) {
    let params = [];
    for(let parameter of action.parameters) {
        params.push(parameter);
    }
    let ret = [];
  
    for(let param of params) {
        let example = getExampleForParameter(action, param.name);
        let type = getParameterDataTypeJava(param);
        if(type == "String") {
            if(example === undefined) {
                example = "{" + param.name + "}";
            }
            ret.push('"' + example.replace(/"/g, '\\"') + '"');
        }else if(type == "int") {
            if(example === undefined) {
                example = -1;
            }
            ret.push(example);
        }
    }

    return new handlebars.SafeString(ret.join(', '));
};

let getParametersIn = function(action, include) {
    let params = [];
    for(let parameter of action.parameters) {
        if(parameter.in != include) continue;
        params.push(parameter);
    }
    return params;
};

let pathWithParameter = function(path, action) {
    const findRegex = /({[a-zA-Z]*})/;

    let resultPath = path;
    let hit = resultPath.match(findRegex);
    while(hit != null) {
        let splitted = resultPath.split(findRegex);
        for (let i = 0; i < splitted.length; i++) {
            if(splitted[i] == hit[1]) {
                splitted[i] = '" + ' + hit[1].slice(1, -1) + ' + "';
            }
        }
        resultPath = splitted.join('');
        let offset = hit[1].length + hit.index;
        hit = resultPath.slice(offset).match(findRegex);
    }
    return resultPath
};

let getExpectedResponseCodes = function(action) {
    return Object.keys(action.responses).join(', ');
};

let indent = function(size, str) {
    let prefix = "";
    for(let i = 0; i < size; i++) {
        prefix += " ";
    }

    let lineArray = str.split("\n");
    for(let i = 0; i < lineArray.length; i++) {
        lineArray[i] = prefix + lineArray[i];
    }

    return lineArray.join("\n");
};

let actionMethodBody = function(indentSize, path, method, action) {
    let output = "";

    if(method == "get" || method == "head" || method == "delete") {
        let queryParams = getParametersIn(action, "query");
        if(queryParams.length > 0) {
            output += "List<NameValuePair> params = new ArrayList<>();\n";
            for(let queryParam of queryParams) {
                let type = getParameterDataTypeJava(queryParam);
                if(type == "String") {
                    output += "if(" + queryParam.name + " != null) {\n";
                    output += "\tparams.add(new BasicNameValuePair(\"" + queryParam.name + "\", " + queryParam.name + "));\n";
                    output += "}\n";
                }else if(type == "int") {
                    output += "params.add(new BasicNameValuePair(\"" + queryParam.name + "\", String.valueOf(" + queryParam.name + ")));\n";
                }
            }
            output += "\n";
            output += "return this.client.do" + capitalize(method) + "(\"" + pathWithParameter(path, action) + "\", params);";
        }else{
            output += "return this.client.do" + capitalize(method) + "(\"" + pathWithParameter(path, action) + "\");";
        } 
    }

    if(method != "get" && method != "head" && method != "delete") {
        let formParams = getParametersIn(action, "formData");
        let bodyParams = getParametersIn(action, "body");
        if(formParams.length > 0) {
            output += "List<NameValuePair> form = new ArrayList<>();\n"
            for(let formParam of formParams) {
                let type = getParameterDataTypeJava(formParam);
                if(type == "String") {
                    output += "if(" + formParam.name + " != null) {\n";
                    output += "\tform.add(new BasicNameValuePair(\"" + formParam.name + "\", " + formParam.name + "));\n";
                    output += "}\n";
                }else if(type == "int") {
                    output += "form.add(new BasicNameValuePair(\"" + formParam.name + "\", String.valueOf(" + formParam.name + ")));\n";
                }
            }
            output += "UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);\n\n"
            output += "return this.client.do" + capitalize(method) + "(\"" + pathWithParameter(path, action) + "\", entity);";
        }else if(bodyParams.length == 1) {
            let bodyParam = bodyParams[0];
            output += "StringEntity payload = new StringEntity(" + bodyParam.name +", ContentType.APPLICATION_JSON);\n";
            output += "return this.client.do" + capitalize(method) + "(\"" + pathWithParameter(path, action) + "\", payload);";
        }else{
            output += "return this.client.do" + capitalize(method) + "(\"" + pathWithParameter(path, action) + "\", null);";
        }
    }

    output = indent(indentSize, output);

    return new handlebars.SafeString(output);
};

let getSchemaVerification = function(indentSize, action) {
    let output = "";

    for(let responseCode in action.responses) {
        let response = action.responses[responseCode];
        if(!("schema" in response)) {
            continue;
        }
        output += "if(response.getStatusLine().getStatusCode() == " + responseCode + ") {\n";
        output += "    this.actions.checkResponseJSONSchema(\"" +response.schema['$ref'] + "\", response.getContent());\n";
        output += "}\n";
    }

    output = indent(indentSize, output);

    return new handlebars.SafeString(output);
};

handlebars.registerHelper('capitalize', capitalize);
handlebars.registerHelper('getActionName', getActionName);
handlebars.registerHelper('getActionParameters', getActionParameters)
handlebars.registerHelper("getActionNameCapitalized", getActionNameCapitalized);
handlebars.registerHelper("getActionParameterValues", getActionParameterValues);
handlebars.registerHelper("getExpectedResponseCodes", getExpectedResponseCodes);
handlebars.registerHelper('actionMethodBody', actionMethodBody);
handlebars.registerHelper('getSchemaVerification', getSchemaVerification);

/**
 * TEST CASE GENERATION
 */

let argv = require('yargs')
    .usage('Usage: $0 command <options>')
    .describe('swagger', 'Path to swagger file.')
    .describe('template', 'Path to the template file.')
    .describe('target', 'Path to the output file.')
    .default('swagger', 'swagger.json')
    .demandOption(['swagger', 'target'])
    .command(['all', '$0'], 'Generates a file for all test cases', () => {}, (argv) => {
        let generateAllTestTemplate = function() {
            let swagger = JSON.parse(fs.readFileSync(argv.swagger));
        
            const template = fs.readFileSync(argv.template, 'utf8');
            let compiled = handlebars.compile(template);
            let output = compiled(swagger);
        
            fs.writeFileSync(argv.target, output, 'utf8');
        }();
    })
    .command(['single'], 'Generates one file for each test case', () => {}, (argv) => {
        let generateSingleTestTemplate = function() {
            let swagger = JSON.parse(fs.readFileSync(argv.swagger));

            const template = fs.readFileSync(argv.template, 'utf8');
            let compiled = handlebars.compile(template);

            // Iterate actions
            for(let actionPath in swagger.paths) {
                for(let method in swagger.paths[actionPath]) {
                    let action = swagger.paths[actionPath][method];

                    targetPath = path.parse(argv.target);
                    targetFile = path.join(targetPath.dir, getActionNameCapitalized(actionPath, method) + targetPath.base);

                    let output = compiled({
                        'path': actionPath,
                        'method': method,
                        'action': action,
                        'targetPath': targetPath
                    });

                    

                    fs.writeFileSync(targetFile, output, 'utf8');
                }
            }
        }();
    })
    .help()
    .argv;