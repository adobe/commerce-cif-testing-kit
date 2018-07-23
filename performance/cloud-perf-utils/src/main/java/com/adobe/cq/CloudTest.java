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

import com.adobe.qe.toughday.api.core.SequentialTest;
import com.adobe.qe.toughday.api.core.config.GlobalArgs;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class CloudTest extends SequentialTest {
    private CloudClient defaultClient;
    public CloudTest() {
        benchmark().registerHierarchyProxyFactory(CloudClient.class, new CloudClientsProxyFactory());
    }

    public CloudClient getDefaultClient() throws Exception {
        if (defaultClient == null) {
            defaultClient = createClient(getGlobalArgs());
        }
        return defaultClient;
    }

    public static CloudClient createClient(GlobalArgs args) throws URISyntaxException, ClientException {
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme(args.getProtocol())
                .setHost(args.getHost())
                .setPort(args.getPort());

        if(args.getContextPath() != null) {
            URI cp = URI.create("/").resolve(args.getContextPath());
            uriBuilder.setPath(cp.getPath());
        }

        return new CloudClient(CloudConfig.builder().withURL(uriBuilder.build()).build());
    }
}
