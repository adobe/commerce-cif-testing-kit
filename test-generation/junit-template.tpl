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

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;


public class CloudSampleTest {

    private CloudSampleActions actions;

    private static final Logger LOG = LoggerFactory.getLogger(CloudSampleTest.class);

    @Before
    public void setup() throws URISyntaxException, ClientException {
        if(this.actions != null) {
            return;
        }
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme("https")
                .setHost("runtime.adobe.io")
                .setPort(443)
                .setPath("/apis/namespace/commerce");

        CloudClient client = new CloudClient(CloudConfig.builder().withURL(uriBuilder.build()).build());
        this.actions = new CloudSampleActions(client);
    }

    {{#each paths as |methods path|}}
    {{#each methods as |action method|}}
    /**
     * Test API specification of {{action.operationId}} action.
     */
    @Test
    public void test{{getActionNameCapitalized path method}}Action() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.{{getActionName path method}}Action({{getActionParameterValues action}});
        List<Integer> expectedResults = Arrays.asList({{getExpectedResponseCodes action}});
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

{{getSchemaVerification 8 action}}
    }

    {{/each}}
    {{/each}}
}