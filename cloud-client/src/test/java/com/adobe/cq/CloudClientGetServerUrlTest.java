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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CloudClientGetServerUrlTest {

    @Parameterized.Parameters(name = "{index} - serverUrl: {0}, path: {1}, expected: {2}")
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
                {"http://HOST",             "http://HOST/"},
                {"http://HOST:4502",        "http://HOST:4502/"},
                {"http://HOST:4502/",       "http://HOST:4502/"},
                {"http://HOST:4502/CTX",    "http://HOST:4502/CTX/"},
                {"http://HOST:4502/CTX/",   "http://HOST:4502/CTX/"},
        });
    }

    @Parameterized.Parameter(value = 0)
    public String serverUrl;

    @Parameterized.Parameter(value = 1)
    public String expectedUrl;

    @Test
    public void testGetUrl() throws ClientException, URISyntaxException {
        CloudClient c = new CloudClient(CloudConfig.builder().withURL(serverUrl).build());
        assertEquals("", URI.create(expectedUrl), c.getUrl());
    }
}
