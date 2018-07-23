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
package com.adobe.cq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import org.apache.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CloudSampleActions {

    private CloudClient client;

    private static final Logger LOG = LoggerFactory.getLogger(CloudSampleActions.class);

    /**
     * Constructor.
     * 
     * @param client Test client reference.
     */
    public CloudSampleActions(CloudClient client) {
        this.client = client;
    }

    /**
     * Load a JSON resource.
     *
     * @param name fileName
     * @return JsonNode of resource
     * @throws IOException
     */
    public static JsonNode loadResource(final String name) throws IOException {
        return JsonLoader.fromResource("/com/adobe/cq" + name);
    }

    /**
     * Generate a JSON schema for the given definition from the swagger file.
     * The swagger.json needs to be located in the resources folder.
     *
     * @param def Reference to JSON schema
     * @return ObjectNode of JSON schema
     * @throws IOException
     */
    public ObjectNode getSchemaForDefinition(String def) throws IOException {
        JsonNode schemaJson = loadResource("/swagger.json");
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        root.put("$ref", def);
        root.set("definitions", schemaJson.get("definitions"));
        return root;
    }

    /**
     * Validate a given response against the schema of the given definition from
     * the swagger model definition.
     * 
     * @param def Reference to JSON schema
     * @param response Response to be verified
     * @throws IOException
     * @throws ProcessingException
     */
    public void checkResponseJSONSchema(String def, String response) throws IOException, ProcessingException {
        // Parse response to JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseJson = mapper.readTree(response);

        // Verify schema
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonSchema schema = factory.getJsonSchema(this.getSchemaForDefinition(def));
        ProcessingReport report = schema.validate(responseJson);

        if(!report.isSuccess()) {
            Assert.fail(report.toString());
        }
    }

    {{#each paths as |methods path|}}
    {{#each methods as |action method|}}
    /**
     * {{action.summary}}
     */
    public CloudResponse {{getActionName path method}}Action({{getActionParameters action}}) throws ClientException {
{{actionMethodBody 8 path method action}}
    }
    
    {{/each}}
    {{/each}}

}