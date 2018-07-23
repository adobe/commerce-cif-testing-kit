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

    /**
     * Creates an empty cart. For convenience it also adds a cart entry when product variant id and quantity are provided.
     */
    public CloudResponse postCartsAction(String currency, String productVariantId, int quantity, String customerId) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        if(currency != null) {
        	form.add(new BasicNameValuePair("currency", currency));
        }
        if(productVariantId != null) {
        	form.add(new BasicNameValuePair("productVariantId", productVariantId));
        }
        form.add(new BasicNameValuePair("quantity", String.valueOf(quantity)));
        if(customerId != null) {
        	form.add(new BasicNameValuePair("customerId", customerId));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPost("/carts", entity);
    }
    
    /**
     * Returns a cart by ID.
     */
    public CloudResponse getCartsIdAction(String id, String customerId) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(customerId != null) {
        	params.add(new BasicNameValuePair("customerId", customerId));
        }
        
        return this.client.doGet("/carts/" + id + "", params);
    }
    
    /**
     * Deletes the cart.
     */
    public CloudResponse deleteCartsIdAction(String id, String customerId) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(customerId != null) {
        	params.add(new BasicNameValuePair("customerId", customerId));
        }
        
        return this.client.doDelete("/carts/" + id + "", params);
    }
    
    /**
     * Sets the billing address for the cart.
     */
    public CloudResponse postCartsIdBillingaddressAction(String id, String body) throws ClientException {
        StringEntity payload = new StringEntity(body, ContentType.APPLICATION_JSON);
        return this.client.doPost("/carts/" + id + "/billingaddress", payload);
    }
    
    /**
     * Deletes the billing address for the cart.
     */
    public CloudResponse deleteCartsIdBillingaddressAction(String id, String customerId) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(customerId != null) {
        	params.add(new BasicNameValuePair("customerId", customerId));
        }
        
        return this.client.doDelete("/carts/" + id + "/billingaddress", params);
    }
    
    /**
     * Adds a coupon to the shopping cart.
     */
    public CloudResponse postCartsIdCouponsAction(String id, String code, String customerId) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        if(code != null) {
        	form.add(new BasicNameValuePair("code", code));
        }
        if(customerId != null) {
        	form.add(new BasicNameValuePair("customerId", customerId));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPost("/carts/" + id + "/coupons", entity);
    }
    
    /**
     * Deletes a coupon from the shopping cart.
     */
    public CloudResponse deleteCartsIdCouponsCouponIdAction(String id, String couponId, String customerId) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(customerId != null) {
        	params.add(new BasicNameValuePair("customerId", customerId));
        }
        
        return this.client.doDelete("/carts/" + id + "/coupons/" + couponId + "", params);
    }
    
    /**
     * Adds a new cart entry to an existing cart.
     */
    public CloudResponse postCartsIdEntriesAction(String id, String productVariantId, int quantity, String customerId) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        if(productVariantId != null) {
        	form.add(new BasicNameValuePair("productVariantId", productVariantId));
        }
        form.add(new BasicNameValuePair("quantity", String.valueOf(quantity)));
        if(customerId != null) {
        	form.add(new BasicNameValuePair("customerId", customerId));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPost("/carts/" + id + "/entries", entity);
    }
    
    /**
     * Updates an existing cart entry.
     */
    public CloudResponse putCartsIdEntriesCartEntryIdAction(String id, String cartEntryId, int quantity, String customerId) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("quantity", String.valueOf(quantity)));
        if(customerId != null) {
        	form.add(new BasicNameValuePair("customerId", customerId));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPut("/carts/" + id + "/entries/" + cartEntryId + "", entity);
    }
    
    /**
     * Removes a cart entry from the cart.
     */
    public CloudResponse deleteCartsIdEntriesCartEntryIdAction(String id, String cartEntryId, String customerId) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(customerId != null) {
        	params.add(new BasicNameValuePair("customerId", customerId));
        }
        
        return this.client.doDelete("/carts/" + id + "/entries/" + cartEntryId + "", params);
    }
    
    /**
     * Creates a payment for this shopping cart.
     */
    public CloudResponse postCartsIdPaymentAction(String id, String body) throws ClientException {
        StringEntity payload = new StringEntity(body, ContentType.APPLICATION_JSON);
        return this.client.doPost("/carts/" + id + "/payment", payload);
    }
    
    /**
     * Removes the payment from the shopping cart. 
     */
    public CloudResponse deleteCartsIdPaymentAction(String id, String customerId) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(customerId != null) {
        	params.add(new BasicNameValuePair("customerId", customerId));
        }
        
        return this.client.doDelete("/carts/" + id + "/payment", params);
    }
    
    /**
     * Sets the shipping address for the cart.
     */
    public CloudResponse postCartsIdShippingaddressAction(String id, String body) throws ClientException {
        StringEntity payload = new StringEntity(body, ContentType.APPLICATION_JSON);
        return this.client.doPost("/carts/" + id + "/shippingaddress", payload);
    }
    
    /**
     * Deletes the shipping address for the cart.
     */
    public CloudResponse deleteCartsIdShippingaddressAction(String id, String customerId) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(customerId != null) {
        	params.add(new BasicNameValuePair("customerId", customerId));
        }
        
        return this.client.doDelete("/carts/" + id + "/shippingaddress", params);
    }
    
    /**
     * Updates the shipping method for the cart.
     */
    public CloudResponse postCartsIdShippingmethodAction(String id, String shippingMethodId, String customerId) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        if(shippingMethodId != null) {
        	form.add(new BasicNameValuePair("shippingMethodId", shippingMethodId));
        }
        if(customerId != null) {
        	form.add(new BasicNameValuePair("customerId", customerId));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPost("/carts/" + id + "/shippingmethod", entity);
    }
    
    /**
     * Deletes the shipping method for the cart.
     */
    public CloudResponse deleteCartsIdShippingmethodAction(String id, String customerId) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(customerId != null) {
        	params.add(new BasicNameValuePair("customerId", customerId));
        }
        
        return this.client.doDelete("/carts/" + id + "/shippingmethod", params);
    }
    
    /**
     * Retrieves the available shipping methods for the current cart.
     */
    public CloudResponse getCartsIdShippingmethodsAction(String id, String customerId) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(customerId != null) {
        	params.add(new BasicNameValuePair("customerId", customerId));
        }
        
        return this.client.doGet("/carts/" + id + "/shippingmethods", params);
    }
    
    /**
     * Returns the entire category structure or a subset of it depending on pagination
     */
    public CloudResponse getCategoriesAction(String sort, int offset, int limit, String type, int depth) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(sort != null) {
        	params.add(new BasicNameValuePair("sort", sort));
        }
        params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        params.add(new BasicNameValuePair("limit", String.valueOf(limit)));
        if(type != null) {
        	params.add(new BasicNameValuePair("type", type));
        }
        params.add(new BasicNameValuePair("depth", String.valueOf(depth)));
        
        return this.client.doGet("/categories", params);
    }
    
    /**
     * Returns a category by ID
     */
    public CloudResponse getCategoriesIdAction(String id) throws ClientException {
        return this.client.doGet("/categories/" + id + "");
    }
    
    /**
     * Performs a customer login, potentially merging an anonymous cart with a customer cart.
     */
    public CloudResponse postCustomersLoginAction(String email, String password, String anonymousCartId) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        if(email != null) {
        	form.add(new BasicNameValuePair("email", email));
        }
        if(password != null) {
        	form.add(new BasicNameValuePair("password", password));
        }
        if(anonymousCartId != null) {
        	form.add(new BasicNameValuePair("anonymousCartId", anonymousCartId));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPost("/customers/login", entity);
    }
    
    /**
     * Returns a customer by ID.
     */
    public CloudResponse getCustomersIdAction(String id) throws ClientException {
        return this.client.doGet("/customers/" + id + "");
    }
    
    /**
     * Queries inventory based on the given query parameters.
     */
    public CloudResponse getInventoryQueryAction(String productId, String scope, int offset, int limit) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(productId != null) {
        	params.add(new BasicNameValuePair("productId", productId));
        }
        if(scope != null) {
        	params.add(new BasicNameValuePair("scope", scope));
        }
        params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        params.add(new BasicNameValuePair("limit", String.valueOf(limit)));
        
        return this.client.doGet("/inventory/query", params);
    }
    
    /**
     * Creates an order based on a cart.
     */
    public CloudResponse postOrdersAction(String cartId, String customerId) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        if(cartId != null) {
        	form.add(new BasicNameValuePair("cartId", cartId));
        }
        if(customerId != null) {
        	form.add(new BasicNameValuePair("customerId", customerId));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPost("/orders", entity);
    }
    
    /**
     * Searches products based on the given query parameters
     */
    public CloudResponse getProductsSearchAction(String text, String filter, String selectedFacets, String queryFacets, String sort, int offset, int limit) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        if(text != null) {
        	params.add(new BasicNameValuePair("text", text));
        }
        if(filter != null) {
        	params.add(new BasicNameValuePair("filter", filter));
        }
        if(selectedFacets != null) {
        	params.add(new BasicNameValuePair("selectedFacets", selectedFacets));
        }
        if(queryFacets != null) {
        	params.add(new BasicNameValuePair("queryFacets", queryFacets));
        }
        if(sort != null) {
        	params.add(new BasicNameValuePair("sort", sort));
        }
        params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        params.add(new BasicNameValuePair("limit", String.valueOf(limit)));
        
        return this.client.doGet("/products/search", params);
    }
    
    /**
     * Returns a product by ID.
     */
    public CloudResponse getProductsIdAction(String id) throws ClientException {
        return this.client.doGet("/products/" + id + "");
    }
    
    /**
     * Gets a users shopping lists.
     */
    public CloudResponse getShoppinglistsAction(int offset, int limit) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        params.add(new BasicNameValuePair("limit", String.valueOf(limit)));
        
        return this.client.doGet("/shoppinglists", params);
    }
    
    /**
     * Creates a new shopping list for the current user.
     */
    public CloudResponse postShoppinglistsAction(String name, String description, String ContentLanguage) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        if(name != null) {
        	form.add(new BasicNameValuePair("name", name));
        }
        if(description != null) {
        	form.add(new BasicNameValuePair("description", description));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPost("/shoppinglists", entity);
    }
    
    /**
     * Gets a users shopping list with a given id.
     */
    public CloudResponse getShoppinglistsIdAction(String id) throws ClientException {
        return this.client.doGet("/shoppinglists/" + id + "");
    }
    
    /**
     * Replaces a shopping list with the given one.
     */
    public CloudResponse putShoppinglistsIdAction(String id, String name, String description) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        if(name != null) {
        	form.add(new BasicNameValuePair("name", name));
        }
        if(description != null) {
        	form.add(new BasicNameValuePair("description", description));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPut("/shoppinglists/" + id + "", entity);
    }
    
    /**
     * Deletes a shopping list.
     */
    public CloudResponse deleteShoppinglistsIdAction(String id) throws ClientException {
        return this.client.doDelete("/shoppinglists/" + id + "");
    }
    
    /**
     * Gets all entries from a shopping list.
     */
    public CloudResponse getShoppinglistsIdEntriesAction(String id, int offset, int limit) throws ClientException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("offset", String.valueOf(offset)));
        params.add(new BasicNameValuePair("limit", String.valueOf(limit)));
        
        return this.client.doGet("/shoppinglists/" + id + "/entries", params);
    }
    
    /**
     * Creates a new entry for a shopping list.
     */
    public CloudResponse postShoppinglistsIdEntriesAction(String id, int quantity, String productVariantId) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("quantity", String.valueOf(quantity)));
        if(productVariantId != null) {
        	form.add(new BasicNameValuePair("productVariantId", productVariantId));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPost("/shoppinglists/" + id + "/entries", entity);
    }
    
    /**
     * Gets a single entry from a shopping list.
     */
    public CloudResponse getShoppinglistsIdEntriesEntryIdAction(String id, String entryId) throws ClientException {
        return this.client.doGet("/shoppinglists/" + id + "/entries/" + entryId + "");
    }
    
    /**
     * Replaces an entry with the given one.
     */
    public CloudResponse putShoppinglistsIdEntriesEntryIdAction(String id, String entryId, int quantity, String productVariantId) throws ClientException {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("quantity", String.valueOf(quantity)));
        if(productVariantId != null) {
        	form.add(new BasicNameValuePair("productVariantId", productVariantId));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, Consts.UTF_8);
        
        return this.client.doPut("/shoppinglists/" + id + "/entries/" + entryId + "", entity);
    }
    
    /**
     * Deletes an entry from a shopping list.
     */
    public CloudResponse deleteShoppinglistsIdEntriesEntryIdAction(String id, String entryId) throws ClientException {
        return this.client.doDelete("/shoppinglists/" + id + "/entries/" + entryId + "");
    }
    

}