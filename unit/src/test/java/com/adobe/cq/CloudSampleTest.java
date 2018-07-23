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

    /**
     * Test API specification of postCart action.
     */
    @Test
    public void testPostCartsAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postCartsAction("{currency}", "{productVariantId}", -1, "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of getCart action.
     */
    @Test
    public void testGetCartsIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getCartsIdAction("{id}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of deleteCart action.
     */
    @Test
    public void testDeleteCartsIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.deleteCartsIdAction("{id}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of postBillingAddress action.
     */
    @Test
    public void testPostCartsIdBillingaddressAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postCartsIdBillingaddressAction("{id}", "{body}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of deleteBillingAddress action.
     */
    @Test
    public void testDeleteCartsIdBillingaddressAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.deleteCartsIdBillingaddressAction("{id}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of postCoupons action.
     */
    @Test
    public void testPostCartsIdCouponsAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postCartsIdCouponsAction("{id}", "{code}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of deleteCoupons action.
     */
    @Test
    public void testDeleteCartsIdCouponsCouponIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.deleteCartsIdCouponsCouponIdAction("{id}", "{couponId}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of postCartEntry action.
     */
    @Test
    public void testPostCartsIdEntriesAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postCartsIdEntriesAction("{id}", "{productVariantId}", -1, "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of putCartEntry action.
     */
    @Test
    public void testPutCartsIdEntriesCartEntryIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.putCartsIdEntriesCartEntryIdAction("{id}", "{cartEntryId}", -1, "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of deleteCartEntry action.
     */
    @Test
    public void testDeleteCartsIdEntriesCartEntryIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.deleteCartsIdEntriesCartEntryIdAction("{id}", "{cartEntryId}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of postPayment action.
     */
    @Test
    public void testPostCartsIdPaymentAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postCartsIdPaymentAction("{id}", "{body}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of deletePayment action.
     */
    @Test
    public void testDeleteCartsIdPaymentAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.deleteCartsIdPaymentAction("{id}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of postShippingAddress action.
     */
    @Test
    public void testPostCartsIdShippingaddressAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postCartsIdShippingaddressAction("{id}", "{body}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of deleteShippingAddress action.
     */
    @Test
    public void testDeleteCartsIdShippingaddressAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.deleteCartsIdShippingaddressAction("{id}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of postShippingMethod action.
     */
    @Test
    public void testPostCartsIdShippingmethodAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postCartsIdShippingmethodAction("{id}", "{shippingMethodId}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of deleteShippingMethod action.
     */
    @Test
    public void testDeleteCartsIdShippingmethodAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.deleteCartsIdShippingmethodAction("{id}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Cart", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of getShippingMethods action.
     */
    @Test
    public void testGetCartsIdShippingmethodsAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getCartsIdShippingmethodsAction("{id}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("undefined", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of getCategories action.
     */
    @Test
    public void testGetCategoriesAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getCategoriesAction("{sort}", -1, -1, "{type}", -1);
        List<Integer> expectedResults = Arrays.asList(200);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/PagedResponseCategory", response.getContent());
        }
        
    }

    /**
     * Test API specification of getCategoryById action.
     */
    @Test
    public void testGetCategoriesIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getCategoriesIdAction("{id}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Category", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of postCustomerLogin action.
     */
    @Test
    public void testPostCustomersLoginAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postCustomersLoginAction("{email}", "{password}", "{anonymousCartId}");
        List<Integer> expectedResults = Arrays.asList(200, 400);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/LoginResult", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of getCustomerById action.
     */
    @Test
    public void testGetCustomersIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getCustomersIdAction("{id}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Customer", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of query action.
     */
    @Test
    public void testGetInventoryQueryAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getInventoryQueryAction("{productId}", "{scope}", -1, -1);
        List<Integer> expectedResults = Arrays.asList(200, 400);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/PagedResponseInventoryItem", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of postOrder action.
     */
    @Test
    public void testPostOrdersAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postOrdersAction("{cartId}", "{customerId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Order", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of searchProducts action.
     */
    @Test
    public void testGetProductsSearchAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getProductsSearchAction("{text}", "{filter}", "{selectedFacets}", "{queryFacets}", "{sort}", -1, -1);
        List<Integer> expectedResults = Arrays.asList(200, 400);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/PagedResponseProduct", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of getProductById action.
     */
    @Test
    public void testGetProductsIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getProductsIdAction("{id}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/Product", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of getShoppingLists action.
     */
    @Test
    public void testGetShoppinglistsAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getShoppinglistsAction(-1, -1);
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/PagedResponseShoppingList", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of postShoppingList action.
     */
    @Test
    public void testPostShoppinglistsAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postShoppinglistsAction("{name}", "{description}", "{Content-Language}");
        List<Integer> expectedResults = Arrays.asList(200, 201, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/ShoppingList", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 201) {
            this.actions.checkResponseJSONSchema("#/definitions/ShoppingList", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of getShoppingList action.
     */
    @Test
    public void testGetShoppinglistsIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getShoppinglistsIdAction("{id}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/ShoppingList", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of putShoppingList action.
     */
    @Test
    public void testPutShoppinglistsIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.putShoppinglistsIdAction("{id}", "{name}", "{description}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/ShoppingList", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of deleteShoppingList action.
     */
    @Test
    public void testDeleteShoppinglistsIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.deleteShoppinglistsIdAction("{id}");
        List<Integer> expectedResults = Arrays.asList(204, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of getShoppingListEntries action.
     */
    @Test
    public void testGetShoppinglistsIdEntriesAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getShoppinglistsIdEntriesAction("{id}", -1, -1);
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/PagedResponseShoppingListEntry", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of postShoppingListEntry action.
     */
    @Test
    public void testPostShoppinglistsIdEntriesAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.postShoppinglistsIdEntriesAction("{id}", -1, "{productVariantId}");
        List<Integer> expectedResults = Arrays.asList(200, 201, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/ShoppingListEntry", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 201) {
            this.actions.checkResponseJSONSchema("#/definitions/ShoppingList", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of getShoppingListEntry action.
     */
    @Test
    public void testGetShoppinglistsIdEntriesEntryIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.getShoppinglistsIdEntriesEntryIdAction("{id}", "{entryId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/ShoppingListEntry", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of putShoppingListEntry action.
     */
    @Test
    public void testPutShoppinglistsIdEntriesEntryIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.putShoppinglistsIdEntriesEntryIdAction("{id}", "{entryId}", -1, "{productVariantId}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 200) {
            this.actions.checkResponseJSONSchema("#/definitions/ShoppingListEntry", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

    /**
     * Test API specification of deleteShoppingListEntry action.
     */
    @Test
    public void testDeleteShoppinglistsIdEntriesEntryIdAction() throws ClientException, IOException, ProcessingException {
        CloudResponse response = this.actions.deleteShoppinglistsIdEntriesEntryIdAction("{id}", "{entryId}");
        List<Integer> expectedResults = Arrays.asList(204, 400, 403, 404);
        LOG.info("Got status {}", response.getStatusLine());
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));

        if(response.getStatusLine().getStatusCode() == 400) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 403) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        if(response.getStatusLine().getStatusCode() == 404) {
            this.actions.checkResponseJSONSchema("#/definitions/ErrorResponse", response.getContent());
        }
        
    }

}