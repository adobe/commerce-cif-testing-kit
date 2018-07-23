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

import com.adobe.cq.util.HttpUtils;
import org.apache.http.*;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpRequest;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The abstract base client for all implementing integration test clients.
 */
@Immutable
public class CloudClient implements Closeable {

    //private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * The clientId for the client, generated automatically during instantiation of client.
     */
    private final String clientId;

    /**
     * The HttpClient object to which http calls are delegated.
     * It can be shared across multiple AbstractSlingClients (by using adaptTo())
     */
    private final CloseableHttpClient http;

    public final CloudConfig config;

    private final static URI slash = URI.create("/");

    /**
     * Constructor used by Builders and adaptTo(). <b>Should never be called directly from the code.</b>
     *
     * @param http http client to handle the delegated calls
     * @param config immutable object holding the config
     * @throws ClientException if the client could not be initialized
     */
    public CloudClient(CloseableHttpClient http, CloudConfig config) throws ClientException {
        // Generate client ID
        this.clientId = this.getClass() + "-" + UUID.randomUUID().toString();
        this.http = http;
        this.config = config;
    }

    public CloudClient(CloudConfig cloudConfig) throws ClientException {
        this(createDefaultHttpClientBuilder().build(), cloudConfig);
    }

    /**
     * Returns the unique id for this client, generated automatically during instantiation.<br>
     *
     * @return client's unique id
     */
    protected String getClientId() {
        return clientId;
    }

    /**
     * <p>Base HTTP URI of the server under test. It includes the context path, if present, and always ends with a slash</p>
     * <p>Example: {@code http://localhost:8080/a/}</p>
     *
     * @return the server's URL
     */
    public URI getUrl() {
        return config.getUrl();
    }

    /**
     * <p>Gets the full URL for a given path.</p>
     *
     * <p>The input path is considered relative to server url path ("/" or context path), even though it starts with a slash.
     * The path is relativized and appended to the {@code server url}.</p>
     *
     * <p>Note: in the case of a server url with context path - the input path should not contain the context path, otherwise
     * it will be duplicated in the resulting url</p>
     *
     * @param path the relative path
     * @return the absolute URI
     * @throws IllegalArgumentException if path cannot be parsed into an URI
     * @throws NullPointerException if path is null
     */
    public URI getUrl(String path) {
        try {
            URI pathUri = slash.relativize(new URI(path));
            return getUrl().resolve(pathUri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Creates a full URL for a given path with additional parameters. Same as {@link #getUrl(String)}, but adds the parameters in the URI.
     *
     * @param path path relative to server url; can start with / but should not include the server context path
     * @param parameters url parameters to be added to the url. If the given argument is {@code null}, nothing will be added to the url.
     *                   If the given argument is an empty array, it will force a "?" at the end of the url.
     * @return full url as URI
     * @throws IllegalArgumentException if path or parameters cannot be parsed into an URI
     * @throws NullPointerException if path is null
     */
    public URI getUrl(String path, List<NameValuePair> parameters) {
        // add server url and path
        URIBuilder uriBuilder = new URIBuilder(getUrl(path));
        // add parameters
        if(parameters != null) {
            uriBuilder.addParameters(parameters);
        }

        try {
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * <p>Transforms an external {@code url} into a sling path, by subtracting the {@code server url} (incl. contextPath).
     * The returned path will not contain the context path, so it can be used with {@link #getUrl(String)}</p>
     *
     * <p>The url can be absolute (incl. hostname) or relative to root (starts with "/").</p>
     *
     * <p>If the server url is not a prefix of the given url, it returns the given url</p>
     *
     * <p>If the url is just a path, it returns the path (with leading slash if not already present)</p>
     *
     * @param url full url
     * @return sling path
     */
    public URI getPath(URI url) {
        // special case for urls that are server urls, but without trailing slash
        if (url.relativize(getUrl()).equals(URI.create(""))) {
            return slash;
        }

        URI contextPath = URI.create(getUrl().getPath());
        URI relativeUrl = contextPath.relativize(slash.resolve(url));

        if (relativeUrl.relativize(contextPath).equals(URI.create(""))) {
            return slash;
        }

        return slash.resolve(getUrl().relativize(relativeUrl));
    }

    /**
     * Extracts the relative sling path (to server url) from an url. Identical to {@link CloudClient#getPath(URI)},
     * except that it also parses the String int URI
     *
     * @param url string containing the full url
     * @return relative path as URI
     * @throws IllegalArgumentException if the parameter cannot be parsed
     * @throws NullPointerException if url is null
     */
    public URI getPath(String url) {
        try {
            return getPath(new URI(url));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * <p>Returns an instance of any class extending the CloudClient. The new client will
     * use the the same {@link HttpClient} and {@link CloudConfig} </p>
     *
     * @param clientClass the type of client requested, identified by its Class
     * @param <T>         any class extending the CloudClient
     * @return instance of a class extending the CloudClient
     * @throws ClientException if client can't be instantiated
     */
    @SuppressWarnings("unchecked")
    public <T extends CloudClient> T adaptTo(Class<T> clientClass) throws ClientException {
        T client;
        try {
            Constructor cons = clientClass.getConstructor(CloseableHttpClient.class, CloudConfig.class);
            client = (T) cons.newInstance(this.http, this.config);
        } catch (Exception e) {
            throw new ClientException("Could not initialize client: '" + clientClass.getCanonicalName() + "'.", e);
        }
        return client;
    }

    //
    // HTTP convenience methods
    //

    /**
     * <p>Executes an HTTP request, WITHOUT consuming the entity in the response. The caller is responsible for consuming the entity or
     * closing the response's InputStream in order to release the connection.
     * Otherwise, the client might run out of connections and will block</p>
     *
     * <p><b>Use this with caution and only if necessary for streaming</b>, otherwise use the safe method
     * {@link #doRequest(HttpUriRequest, List, int...)}</p>
     *
     * <p>Adds the headers and checks the response against expected status</p>
     *
     * @param request the request to be executed
     * @param headers optional headers to be added to the request
     * @param expectedStatus if passed, the response status is checked against it/them, and has to match at least one of them
     * @return the response, with the entity not consumed
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doStreamRequest(HttpUriRequest request, List<Header> headers, int... expectedStatus)
            throws ClientException {
        // create context from config
        HttpClientContext context = createHttpClientContextFromConfig();

        List<Header> resolvedHeaders = getHeaders(headers);

        // add headers
        if (resolvedHeaders != null) {
            request.setHeaders(resolvedHeaders.toArray(new Header[resolvedHeaders.size()]));
        }

        try {
            //log.debug("request {} {}", request.getMethod(), request.getURI());
            CloudResponse response = new CloudResponse(http.execute(request, context));
            //log.debug("response {}", HttpUtils.getHttpStatus(response));
            // Check the status and throw a ClientException if it doesn't match expectedStatus, but close the entity before
            if (expectedStatus != null && expectedStatus.length > 0) {
                try {
                    HttpUtils.verifyHttpStatus(response, expectedStatus);
                } catch (ClientException e) {
                    // catch the exception to make sure we close the entity before re-throwing it
                    response.close();
                    throw e;
                }
            }

            return response;
        } catch (IOException e) {
            throw new ClientException("Could not execute http request", e);
        }
    }

    /**
     * <p>Executes a raw HTTP request, WITHOUT consuming the entity in the response. The caller is responsible for consuming the entity or
     * closing the response's InputStream in order to release the connection.
     * Otherwise, the client might run out of connections and will block</p>
     *
     * <p><b>Use this with caution and only if necessary for custom methods or for paths that must not be encoded</b>,
     * otherwise use the safe method {@link #doRequest(HttpUriRequest, List, int...)}</p>
     *
     * <p>It behaves as {@link #doStreamRequest(HttpUriRequest, List, int...)}, so the entity is not consumed.</p>
     * <p>Adds the headers and checks the response against expected status</p>
     *
     * @param method the request to be executed
     * @param uri the uri to be sent as it is (will not prepend the context path)
     * @param headers optional headers to be added to the request
     * @param expectedStatus if passed, the response status is checked against it/them, and has to match at least one of them
     * @return the response, with the entity not consumed
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doRawRequest(String method, String uri, List<Header> headers, int... expectedStatus)
            throws ClientException {
        // create context from config
        HttpClientContext context = createHttpClientContextFromConfig();

        HttpHost host = new HttpHost(getUrl().getHost(), getUrl().getPort(), getUrl().getScheme());
        HttpRequest request = new BasicHttpRequest(method, uri);

        List<Header> resolvedHeaders = getHeaders(headers);
        // add headers
        if (resolvedHeaders != null) {
            request.setHeaders(resolvedHeaders.toArray(new Header[resolvedHeaders.size()]));
        }

        try {
            //log.debug("request {} {}", method, uri);
            CloudResponse response = new CloudResponse(http.execute(host, request, context));
            //log.debug("response {}", HttpUtils.getHttpStatus(response));
            // Check the status and throw a ClientException if it doesn't match expectedStatus, but close the entity before
            if (expectedStatus != null && expectedStatus.length > 0) {
                try {
                    HttpUtils.verifyHttpStatus(response, expectedStatus);
                } catch (ClientException e) {
                    // catch the exception to make sure we close the entity before re-throwing it
                    response.close();
                    throw e;
                }
            }

            return response;
        } catch (IOException e) {
            throw new ClientException("Could not execute http request", e);
        }
    }

    private HttpClientContext createHttpClientContextFromConfig() {
        // create context from config
        HttpClientContext context = HttpClientContext.create();

        //TODO implement the context

        return context;
    }

    /**
     * Combines the given list of headers with the global ones
     * @param headers given headers
     * @return a list which contains both the global and the given headers
     */
    private List<Header> getHeaders(List<Header> headers) {
        List<Header> headerList = new ArrayList<>();

        headerList.addAll(config.getGlobalHeaders());

        if(headers != null) {
            headerList.addAll(headers);
        }

        return headerList.size() != 0 ? headerList : null;
    }

    /**
     * <p>Executes a GET request WITHOUT consuming the entity in the response. The caller is responsible to close the connection.
     * Otherwise, the client might run out of connections and will block</p>
     *
     * <p><b>Use this with caution and only if necessary for streaming</b>, otherwise use the safe method
     * {@link #doGet(String, List, List, int...)}</p>
     *
     * <p>Adds the given parameters and headers and checks the response against expected status</p>
     * @param requestPath path relative to client url
     * @param parameters optional url parameters to be added
     * @param headers optional headers to be added
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity not consumed
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doStreamGet(String requestPath, List<NameValuePair> parameters, List<Header> headers, int... expectedStatus)
            throws ClientException {
        // create full uri, including server url, given path and given parameters
        URI uri = getUrl(requestPath, parameters);
        // execute request
        HttpUriRequest request = new HttpGet(uri);
        return doStreamRequest(request, headers, expectedStatus);
    }

    /**
     * <p>Executes a POST request WITHOUT consuming the entity in the response. The caller is responsible to close the connection</p>
     *
     * <p><b>Use this with caution and only if necessary for streaming</b>, otherwise use the safe method
     * {@link #doPost(String, HttpEntity, List, int...)}</p>
     *
     * <p>Adds the headers and checks the response against expected status</p>
     * @param requestPath path relative to client url
     * @param entity http entity to be sent by POST
     * @param headers optional headers to be added
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity not consumed
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doStreamPost(String requestPath, HttpEntity entity, List<Header> headers, int... expectedStatus)
            throws ClientException {
        HttpEntityEnclosingRequestBase request = new HttpPost(getUrl(requestPath));
        if (entity != null) {
            request.setEntity(entity);
        }
        return doStreamRequest(request, headers, expectedStatus);
    }

    /**
     * <p>Execute an HTTP request and consumes the entity in the response. The content is cached and can be retrieved using
     * {@code response.getContent()}.
     * This method is safe to use because it closes the entity so the caller has no responsibility.</p>
     *
     * <p>This means the response entity SHOULD NOT BE USED to read the content, e.g. {@code response.getEntity().getContent()}</p>
     *
     * @param request the request to be executed
     * @param headers optional headers to be added to the request
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed and the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doRequest(HttpUriRequest request, List<Header> headers, int... expectedStatus) throws ClientException {
        CloudResponse response = doStreamRequest(request, headers, expectedStatus);

        // Consume entity and cache the content so the connection is closed
        response.getContent();

        return response;
    }

    /**
     * <p>Executes a GET request and consumes the entity in the response (so the connection is closed immediately)
     * The content is cached and can be retrieved using {@code response.getContent()}.</p>
     *
     * <p>Adds the passed parameters and headers and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @param parameters optional url parameters to be added
     * @param headers optional headers to be added
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed amd the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doGet(String requestPath, List<NameValuePair> parameters, List<Header> headers, int... expectedStatus)
            throws ClientException {
        CloudResponse response = doStreamGet(requestPath, parameters, headers, expectedStatus);

        // Consume entity and cache the content so the connection is closed
        response.getContent();

        return response;
    }

    /**
     * <p>Executes a GET request and consumes the entity in the response (so the connection is closed immediately)
     * The content is cached and can be retrieved using {@code response.getContent()}.</p>
     *
     * <p>Adds the passed parameters and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @param parameters optional url parameters to be added
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed amd the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doGet(String requestPath, List<NameValuePair> parameters, int... expectedStatus)
            throws ClientException {
        return doGet(requestPath, parameters, null, expectedStatus);
    }

    /**
     * <p>Executes a GET request and consumes the entity in the response (so the connection is closed immediately)
     * The content is cached and can be retrieved using {@code response.getContent()}.</p>
     *
     * @param requestPath path relative to client url
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed amd the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doGet(String requestPath, int... expectedStatus)
            throws ClientException {
        return doGet(requestPath, null, null, expectedStatus);
    }

    /**
     * <p>Executes a HEAD request</p>
     *
     * <p>Adds the passed parameters and headers and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @param parameters optional url parameters to be added
     * @param headers optional headers to be added
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doHead(String requestPath, List<NameValuePair> parameters, List<Header> headers, int... expectedStatus)
            throws ClientException {
        HttpUriRequest request = new HttpHead(getUrl(requestPath, parameters));
        return doRequest(request, headers, expectedStatus);
    }


    /**
     * <p>Executes a POST request and consumes the entity in the response. The content is cached and be retrieved by calling
     * {@code response.getContent()}</p>
     *
     * <p>Adds the passed entity and headers and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @param entity the entity to be added to request
     * @param headers optional headers to be added
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed and the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doPost(String requestPath, HttpEntity entity, List<Header> headers, int... expectedStatus)
            throws ClientException {
        HttpEntityEnclosingRequestBase request = new HttpPost(getUrl(requestPath));
        if (entity != null) {
            request.setEntity(entity);
        }
        return doRequest(request, headers, expectedStatus);
    }

    /**
     * <p>Executes a POST request and consumes the entity in the response. The content is cached and be retrieved by calling
     * {@code response.getContent()}</p>
     *
     * <p>Adds the passed entity and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @param entity the entity to be added to request
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed and the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doPost(String requestPath, HttpEntity entity, int... expectedStatus)
            throws ClientException {
        return doPost(requestPath, entity, null, expectedStatus);
    }

    /**
     * <p>Executes a PUT request and consumes the entity in the response. The content is cached and be retrieved by calling
     * {@code response.getContent()}</p>
     *
     * <p>Adds the passed entity and headers and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @param entity the entity to be added to request
     * @param headers optional url parameters to be added
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed and the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doPut(String requestPath, HttpEntity entity, List<Header> headers, int... expectedStatus)
            throws ClientException {
        HttpEntityEnclosingRequestBase request = new HttpPut(getUrl(requestPath));
        if (entity != null) {
            request.setEntity(entity);
        }
        return doRequest(request, headers, expectedStatus);
    }

    /**
     * <p>Executes a PUT request and consumes the entity in the response. The content is cached and be retrieved by calling
     * {@code response.getContent()}</p>
     *
     * <p>Adds the passed entity and headers and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @param entity the entity to be added to request
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed and the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doPut(String requestPath, HttpEntity entity, int... expectedStatus)
            throws ClientException {
        HttpEntityEnclosingRequestBase request = new HttpPut(getUrl(requestPath));
        if (entity != null) {
            request.setEntity(entity);
        }
        return doRequest(request, null, expectedStatus);
    }

    /**
     * <p>Executes a PATCH request and consumes the entity in the response. The content is cached and be retrieved by calling
     * {@code response.getContent()}</p>
     *
     * <p>Adds the passed entity and headers and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @param entity the entity to be added to request
     * @param headers optional url parameters to be added
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed and the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doPatch(String requestPath, HttpEntity entity, List<Header> headers, int... expectedStatus)
            throws ClientException {
        HttpEntityEnclosingRequestBase request = new HttpPatch(getUrl(requestPath));
        if (entity != null) {
            request.setEntity(entity);
        }
        return doRequest(request, headers, expectedStatus);
    }

    /**
     * <p>Executes a DELETE request and consumes the entity in the response. The content is cached and be retrieved by calling
     * {@code response.getContent()}</p>
     *
     * <p>Adds the passed parameters and headers and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @param parameters optional url parameters to be added
     * @param headers optional url parameters to be added
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed and the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doDelete(String requestPath, List<NameValuePair> parameters, List<Header> headers, int... expectedStatus)
            throws ClientException {
        HttpUriRequest request = new HttpDelete(getUrl(requestPath, parameters));
        return doRequest(request, headers, expectedStatus);
    }

    /**
     * <p>Executes a DELETE request and consumes the entity in the response. The content is cached and be retrieved by calling
     * {@code response.getContent()}</p>
     *
     * <p>Adds the passed parameters and headers and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @return the response with the entity consumed and the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doDelete(String requestPath) throws ClientException {
        return this.doDelete(requestPath, null, null);
    }

    /**
     * <p>Executes a DELETE request and consumes the entity in the response. The content is cached and be retrieved by calling
     * {@code response.getContent()}</p>
     *
     * <p>Adds the passed parameters and headers and checks the expected status</p>
     *
     * @param requestPath path relative to client url
     * @param parameters optional url parameters to be added
     * @param expectedStatus if passed, the response status will have to match one of them
     * @return the response with the entity consumed and the content cached
     * @throws ClientException if the request could not be executed
     */
    public CloudResponse doDelete(String requestPath, List<NameValuePair> parameters, int... expectedStatus)
            throws ClientException {
        HttpUriRequest request = new HttpDelete(getUrl(requestPath, parameters));
        return doRequest(request, null, expectedStatus);
    }

    @Override
    /**
     * <p>Closes the http client and makes sure all the underlying resources, like the connection manager, shut down </p>
     *
     */
    public void close() throws IOException {
        this.http.close();
    }

    public static HttpClientBuilder createDefaultHttpClientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.useSystemProperties();
        httpClientBuilder.setUserAgent("Java");
        // Connection
        httpClientBuilder.setMaxConnPerRoute(10);
        httpClientBuilder.setMaxConnTotal(100);

        return httpClientBuilder;
    }
}
