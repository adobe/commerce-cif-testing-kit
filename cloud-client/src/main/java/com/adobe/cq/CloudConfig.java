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

import org.apache.http.Header;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CloudConfig {
    public static Builder builder() {
        return new Builder();
    }

    public List<Header> globalHeaders;

    /**
     * Base URI of the server under test.
     */
    protected final URI url;

    /*
     * TODO authentication information
     */

    protected CloudConfig(URI url, List<Header> globalHeaders) {
        this.url = url;
        this.globalHeaders = globalHeaders;
    }

    public URI getUrl() {
        return url;
    }

    public List<Header> getGlobalHeaders() {
        return globalHeaders;
    }

    public static class Builder {
        private URI url;
        private List<Header> headers = new ArrayList<>();

        public <T extends Builder> T withURL(URI url) {
            this.url = url;
            if (this.url.getPath() == null || this.url.getPath().isEmpty() || !this.url.getPath().endsWith("/")) {
                this.url = this.url.resolve((this.url.getPath() == null ? "" : this.url.getPath()) + "/");
            }
            return (T) this;
        }

        public <T extends Builder> T withURL(String url) throws URISyntaxException {
            withURL(new URI(url));
            return (T) this;
        }

        public <T extends Builder> T withGlobalHeader(Header header) {
            this.headers.add(header);
            return (T) this;
        }

        public <T extends Builder> T withGlobalHeaders(List<Header> headers) {
            this.headers.addAll(headers);
            return (T) this;
        }

        public CloudConfig build() {
            assert url != null : "The URL may not be null";
            
            return new CloudConfig(url, headers);
        }
    }
}
