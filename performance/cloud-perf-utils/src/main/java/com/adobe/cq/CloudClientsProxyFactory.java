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

import com.adobe.qe.toughday.api.core.AbstractTest;
import com.adobe.qe.toughday.api.core.benchmark.Benchmark;
import com.adobe.qe.toughday.api.core.benchmark.ProxyFactory;
import com.adobe.qe.toughday.api.core.benchmark.ProxyHelpers;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

public class CloudClientsProxyFactory implements ProxyFactory<CloudClient> {

    @Override
    public CloudClient createProxy(CloudClient target, AbstractTest test, Benchmark benchmark) {
        try {
            CloudClient CloudClientProxy = spy(target);
            CloudClientProxy proxy = target.adaptTo(CloudClientProxy.class);
            proxy.setTest(test);
            proxy.setTarget(target);
            proxy.setBenchmark(benchmark);

            doAnswer(doStreamRequestAnswer(proxy)).when(CloudClientProxy).doStreamRequest(any(), any(), any());
            doAnswer(doRawRequestAnswer(proxy)).when(CloudClientProxy).doRawRequest(any(), any(), any(), any());
            doAnswer(doRequestAnswer(proxy)).when(CloudClientProxy).doRequest(any(), any(), any());
            doAnswer(doGetAnswer(proxy)).when(CloudClientProxy).doGet(any(), any(), any(), any());

            return CloudClientProxy;
        } catch (ClientException e) {
            //If something goes wrong and we can't create a proxy, return the original object
            return target;
        }
    }

    private Answer<CloudResponse> doStreamRequestAnswer(CloudClientProxy proxy) {
        return invocation -> {
            Object[] arguments = ProxyHelpers.canonicArguments(invocation.getMethod(), invocation.getArguments());
            return proxy.doStreamRequest((HttpUriRequest) arguments[0], (List<Header>) arguments[1], (int[])arguments[2]);
        };
    }

    private Answer<CloudResponse> doRawRequestAnswer(CloudClientProxy proxy) {
        return invocation -> {
            Object[] arguments = ProxyHelpers.canonicArguments(invocation.getMethod(), invocation.getArguments());
            return proxy.doRawRequest((String) arguments[0], (String) arguments[1], (List<Header>) arguments[2], (int[]) arguments[3]);
        };
    }

    private Answer<CloudResponse> doRequestAnswer(CloudClientProxy proxy) {
        return invocation -> {
            Object[] arguments = ProxyHelpers.canonicArguments(invocation.getMethod(), invocation.getArguments());
            return proxy.doRequest((HttpUriRequest) arguments[0], (List<Header>) arguments[1], (int[]) arguments[2]);
        };
    }

    private Answer<CloudResponse> doGetAnswer(CloudClient proxy) {
        return invocation -> {
            Object[] arguments = ProxyHelpers.canonicArguments(invocation.getMethod(), invocation.getArguments());
            return proxy.doGet((String)arguments[0], (List<NameValuePair>)arguments[1], (List<Header>)arguments[2], (int[]) arguments[3]);
        };
    }
}
