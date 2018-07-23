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
public class CloudClientGetPathTest {

    @Parameterized.Parameters(name = "{index} - serverUrl: {0}, input: {1}, expected: {2}")
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][] {
                {"http://HOST",              "http://HOST/page.html",             "/page.html"},
                {"http://HOST",              "http://HOST/my/page.html",          "/my/page.html"},
                {"http://HOST",              "http://HOST/my/",                   "/my/"},
                {"http://HOST",              "http://HOST/my",                    "/my"},
                {"http://HOST",              "http://HOST/",                      "/"},
                {"http://HOST",              "http://HOST",                       "/"},
                {"http://HOST",              "/page.html",                        "/page.html"},
                {"http://HOST",              "/my/page.html",                     "/my/page.html"},
                {"http://HOST",              "/my/",                              "/my/"},
                {"http://HOST",              "/",                                 "/"},
                {"http://HOST",              "page.html",                         "/page.html"},
                {"http://HOST",              "my/page.html",                      "/my/page.html"},
                {"http://HOST",              "my",                                "/my"},
                {"http://HOST",              "",                                  "/"},

                {"http://HOST:4502",         "http://HOST:4502/page.html",        "/page.html"},
                {"http://HOST:4502",         "http://HOST:4502/my/page.html",     "/my/page.html"},
                {"http://HOST:4502",         "http://HOST:4502/my/",              "/my/"},
                {"http://HOST:4502",         "http://HOST:4502/my",               "/my"},
                {"http://HOST:4502",         "http://HOST:4502/",                 "/"},
                {"http://HOST:4502",         "http://HOST:4502",                  "/"},
                {"http://HOST:4502",         "/page.html",                        "/page.html"},
                {"http://HOST:4502",         "/my/page.html",                     "/my/page.html"},
                {"http://HOST:4502",         "/my/",                              "/my/"},
                {"http://HOST:4502",         "/my",                               "/my"},
                {"http://HOST:4502",         "/",                                 "/"},
                {"http://HOST:4502",         "page.html",                         "/page.html"},
                {"http://HOST:4502",         "my/page.html",                      "/my/page.html"},
                {"http://HOST:4502",         "my/",                               "/my/"},
                {"http://HOST:4502",         "my",                                "/my"},
                {"http://HOST:4502",         "",                                  "/"},

                {"http://HOST:4502/",        "http://HOST:4502/page.html",        "/page.html"},
                {"http://HOST:4502/",        "http://HOST:4502/my/page.html",     "/my/page.html"},
                {"http://HOST:4502/",        "http://HOST:4502/my/",              "/my/"},
                {"http://HOST:4502/",        "http://HOST:4502/my",               "/my"},
                {"http://HOST:4502/",        "http://HOST:4502/",                 "/"},
                {"http://HOST:4502/",        "http://HOST:4502",                  "/"},
                {"http://HOST:4502/",        "/page.html",                        "/page.html"},
                {"http://HOST:4502/",        "/my/page.html",                     "/my/page.html"},
                {"http://HOST:4502/",        "/my/",                              "/my/"},
                {"http://HOST:4502/",        "/my",                               "/my"},
                {"http://HOST:4502/",        "/",                                 "/"},
                {"http://HOST:4502/",        "page.html",                         "/page.html"},
                {"http://HOST:4502/",        "my/page.html",                      "/my/page.html"},
                {"http://HOST:4502/",        "my/",                               "/my/"},
                {"http://HOST:4502/",        "my",                                "/my"},
                {"http://HOST:4502/",        "",                                  "/"},

                {"http://HOST:4502/CTX",     "http://HOST:4502/CTX/page.html",    "/page.html"},
                {"http://HOST:4502/CTX",     "http://HOST:4502/CTX/my/page.html", "/my/page.html"},
                {"http://HOST:4502/CTX",     "http://HOST:4502/CTX/my/",          "/my/"},
                {"http://HOST:4502/CTX",     "http://HOST:4502/CTX/my",           "/my"},
                {"http://HOST:4502/CTX",     "http://HOST:4502/CTX/",             "/"},
                {"http://HOST:4502/CTX",     "http://HOST:4502/CTX",              "/"},
                {"http://HOST:4502/CTX",     "/CTX",                              "/"},
                {"http://HOST:4502/CTX",     "/CTX/",                             "/"},
                {"http://HOST:4502/CTX",     "/CTX/page.html",                    "/page.html"},
                {"http://HOST:4502/CTX",     "/page.html",                        "/page.html"},
                {"http://HOST:4502/CTX",     "/my/page.html",                     "/my/page.html"},
                {"http://HOST:4502/CTX",     "/my/",                              "/my/"},
                {"http://HOST:4502/CTX",     "/my",                               "/my"},
                {"http://HOST:4502/CTX",     "/",                                 "/"},
                {"http://HOST:4502/CTX",     "CTX",                               "/"},
                {"http://HOST:4502/CTX",     "CTX/",                              "/"},
                {"http://HOST:4502/CTX",     "CTX/page.html",                     "/page.html"},
                {"http://HOST:4502/CTX",     "page.html",                         "/page.html"},
                {"http://HOST:4502/CTX",     "my/page.html",                      "/my/page.html"},
                {"http://HOST:4502/CTX",     "my/",                               "/my/"},
                {"http://HOST:4502/CTX",     "my",                                "/my"},
                {"http://HOST:4502/CTX",     "",                                  "/"},

                {"http://HOST:4502/CTX/",    "http://HOST:4502/CTX/page.html",    "/page.html"},
                {"http://HOST:4502/CTX/",    "http://HOST:4502/CTX/my/page.html", "/my/page.html"},
                {"http://HOST:4502/CTX/",    "http://HOST:4502/CTX/my/",          "/my/"},
                {"http://HOST:4502/CTX/",    "http://HOST:4502/CTX/my",           "/my"},
                {"http://HOST:4502/CTX/",    "http://HOST:4502/CTX/",             "/"},
                {"http://HOST:4502/CTX/",    "http://HOST:4502/CTX",              "/"},
                {"http://HOST:4502/CTX/",    "/CTX",                              "/"},
                {"http://HOST:4502/CTX/",    "/CTX/",                             "/"},
                {"http://HOST:4502/CTX/",    "/CTX/page.html",                    "/page.html"},
                {"http://HOST:4502/CTX/",    "/page.html",                        "/page.html"},
                {"http://HOST:4502/CTX/",    "/my/page.html",                     "/my/page.html"},
                {"http://HOST:4502/CTX/",    "/my/",                              "/my/"},
                {"http://HOST:4502/CTX/",    "/my",                               "/my"},
                {"http://HOST:4502/CTX/",    "/",                                 "/"},
                {"http://HOST:4502/CTX/",    "CTX",                               "/"},
                {"http://HOST:4502/CTX/",    "CTX/",                              "/"},
                {"http://HOST:4502/CTX/",    "CTX/page.html",                     "/page.html"},
                {"http://HOST:4502/CTX/",    "page.html",                         "/page.html"},
                {"http://HOST:4502/CTX/",    "my/page.html",                      "/my/page.html"},
                {"http://HOST:4502/CTX/",    "my/",                               "/my/"},
                {"http://HOST:4502/CTX/",    "my",                                "/my"},
                {"http://HOST:4502/CTX/",    "",                                  "/"},

                {"http://HOST:4502/CTX/",    "http://www.google.com",             "http://www.google.com"},
        });
    }

    @Parameterized.Parameter(value = 0)
    public String serverUrl;

    @Parameterized.Parameter(value = 1)
    public String inputUri;

    @Parameterized.Parameter(value = 2)
    public String expectedPath;

    @Test
    public void testGetPath() throws ClientException, URISyntaxException {
        CloudClient c = new CloudClient(CloudConfig.builder().withURL(serverUrl).build());
        assertEquals(URI.create(expectedPath), c.getPath(inputUri));
    }
}










