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

import com.adobe.qe.toughday.api.annotations.labels.Nullable;
import com.adobe.qe.toughday.api.core.AbstractTest;
import com.adobe.qe.toughday.api.annotations.labels.NotThreadSafe;
import com.adobe.qe.toughday.api.core.benchmark.Benchmark;
import com.adobe.qe.toughday.api.core.benchmark.Proxy;
import com.adobe.qe.toughday.api.core.benchmark.ResultInfo;
import com.adobe.qe.toughday.api.core.benchmark.TestResult;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.*;

@NotThreadSafe(description = "Don't reuse a proxy for requests. Always create a new proxy for a new request.")
public class CloudClientProxy extends CloudClient implements Proxy<CloudClient> {

    public CloudClientProxy(CloseableHttpClient http, CloudConfig config) throws ClientException {
        super(http, config);
    }

    private AbstractTest test;
    private CloudClient target;
    private Benchmark benchmark;

    @Override
    public void setTest(AbstractTest parent) {
        this.test = parent;
    }

    @Override
    public void setTarget(CloudClient target) {
        this.target = target;
    }

    @Override
    public void setBenchmark(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    @Override
    public Benchmark benchmark() {
        return benchmark;
    }

    private boolean recordResult = true;
    private TestResult<Map<String, Object>> testResult;

    private boolean shouldIRecord() {
        boolean tmp = recordResult;
        recordResult = false;
        return tmp;
    }

    public CloudResponse doGet(String requestPath, List<NameValuePair> parameters, List<Header> headers, int... expectedStatus) throws ClientException {
        boolean recordResultHere = shouldIRecord();
        ResultInfo<CloudResponse, Map<String, Object>> result = benchmark().computeTestResult(test, (TestResult<Map<String, Object>> testResult) -> {
            return super.doGet(requestPath, parameters, headers, expectedStatus);
        });

        TestResult<Map<String, Object>> currentResult = result.getTestResult();
        @Nullable CloudResponse response = result.getReturnValue();
        Throwable throwable = result.getThrowable();

        currentResult.withData(this.testResult.getData());
        if(response != null) {
            currentResult.getData().put("bytes", response.getContent().length());
        }

        if(recordResultHere) {
            doRecord(currentResult);
        } else {
            this.testResult = currentResult;
        }

        doThrow(throwable);
        return response;
    }

    @Override
    public CloudResponse doRequest(HttpUriRequest request, List<Header> headers, int... expectedStatus) throws ClientException {
        boolean recordResultHere = shouldIRecord();
        ResultInfo<CloudResponse, Map<String, Object>> result = benchmark().computeTestResult(test, (TestResult<Map<String, Object>> testResult) -> {
            return super.doRequest(request, headers, expectedStatus);
        });

        TestResult<Map<String, Object>> currentResult = result.getTestResult();
        @Nullable CloudResponse response = result.getReturnValue();
        Throwable throwable = result.getThrowable();

        currentResult.withData(this.testResult.getData());
        if(response != null) {
            currentResult.getData().put("bytes", response.getContent().length());
        }

        if(recordResultHere) {
            doRecord(testResult);
        } else {
            this.testResult = currentResult;
        }

        doThrow(throwable);
        return response;
    }

    @Override
    public CloudResponse doRawRequest(String method, String uri, List<Header> headers, int... expectedStatus) throws ClientException {
        boolean recordResultHere = shouldIRecord();

        ResultInfo<CloudResponse, Map<String, Object>> result = benchmark().computeTestResult(test, (TestResult<Map<String, Object>> testResult) -> {
            Map<String, Object> data = testResult.getData() != null ? testResult.getData() : new HashMap<String, Object>();
            CloudResponse response = target.doRawRequest(method, uri, headers, expectedStatus);
            Header contentLengthHeader = response.getFirstHeader("Content-Length");
            long bytes = contentLengthHeader != null ? Long.parseLong(contentLengthHeader.getValue()) : response.getEntity().getContentLength();
            data.put("method", method);
            data.put("url", uri);
            data.put("response_code", response.getStatusLine().getStatusCode());
            data.put("bytes", bytes);

            // Adobe IO Gateway Caching header
            Header cacheHeader = response.getFirstHeader("x-gw-cache");
            if(cacheHeader != null) {
                data.put(cacheHeader.getName(), cacheHeader.getValue());
            }

            // Collect performance headers
            for(Header h : response.getAllHeaders()) {
                if(h.getName().toLowerCase().startsWith("perf-")) {
                    data.put(h.getName(), h.getValue());
                }
            }

            testResult.withData(data);
            return response;
        });

        TestResult<Map<String, Object>> currentResult = result.getTestResult();
        @Nullable CloudResponse response = result.getReturnValue();
        Throwable throwable = result.getThrowable();
        if(response != null) {
            currentResult.getData().put("latency", currentResult.getDuration());
        }

        if(recordResultHere) {
            doRecord(currentResult);
        } else {
            this.testResult = currentResult;
        }

        doThrow(throwable);
        return response;
    }

    @Override
    public CloudResponse doStreamRequest(HttpUriRequest request, List<Header> headers, int... expectedStatus) throws ClientException {
        boolean recordResultHere = shouldIRecord();

        ResultInfo<CloudResponse, Map<String, Object>> result = benchmark().computeTestResult(test, (TestResult<Map<String, Object>> testResult) -> {
            Map<String, Object> data = testResult.getData() != null ? testResult.getData() : new HashMap<String, Object>();
            CloudResponse response = target.doStreamRequest(request, headers, expectedStatus);
            Header contentLengthHeader = response.getFirstHeader("Content-Length");
            long bytes = contentLengthHeader != null ? Long.parseLong(contentLengthHeader.getValue()) : response.getEntity().getContentLength();
            data.put("method", request.getMethod());
            data.put("url", request.getURI().toString());
            data.put("response_code", response.getStatusLine().getStatusCode());
            data.put("bytes", bytes);

            // Adobe IO Gateway Caching header
            Header cacheHeader = response.getFirstHeader("x-gw-cache");
            if(cacheHeader != null) {
                data.put(cacheHeader.getName(), cacheHeader.getValue());
            }

            // Collect performance headers
            for(Header h : response.getAllHeaders()) {
                if(h.getName().toLowerCase().startsWith("perf-")) {
                    data.put(h.getName(), h.getValue());
                }
            }

            testResult.withData(data);
            return response;
        });

        TestResult<Map<String, Object>> currentResult = result.getTestResult();
        @Nullable CloudResponse response = result.getReturnValue();
        Throwable throwable = result.getThrowable();
        if(response != null) {
            currentResult.getData().put("latency", currentResult.getDuration());
        }

        if(recordResultHere) {
            doRecord(currentResult);
        } else {
            this.testResult = currentResult;
        }

        doThrow(throwable);
        return response;
    }

    private List<Map<String, String>> getQueryString(HttpUriRequest request) {
        if (request instanceof HttpEntityEnclosingRequestBase) {
            HttpEntityEnclosingRequestBase httpRequest = (HttpEntityEnclosingRequestBase) request;
            if(httpRequest.getEntity() instanceof UrlEncodedFormEntity) {
                UrlEncodedFormEntity urlEncodedFormEntity = (UrlEncodedFormEntity) httpRequest.getEntity();
                try {
                    List<Map<String, String>> query = new ArrayList<>();
                    for(NameValuePair nameValuePair : URLEncodedUtils.parse(urlEncodedFormEntity)) {
                        query.add(Collections.singletonMap(nameValuePair.getName(), nameValuePair.getValue()));
                    }
                    return query;
                } catch (Exception e) {
                }
            }
        }

        return null;
    }

    private void doRecord(TestResult<Map<String, Object>> currentTestResult) {
        benchmark().getRunMap().record(currentTestResult);
        recordResult = true;
        this.testResult = null;
    }

    private void doThrow(Throwable throwable) throws ClientException{
        if(throwable != null) {
            if (throwable instanceof ClientException) {
                throw (ClientException) throwable;
            }
            throw new ClientException(throwable.getMessage(), throwable);
        }
    }
}
