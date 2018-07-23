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
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;


public class PutShoppinglistsIdTest extends CloudTest {
    
    private CloudSampleActions actions;

    public PutShoppinglistsIdTest() {
    }

    public PutShoppinglistsIdTest(CloudSampleActions actions) {
        this.actions = actions;
    }
    
    @Override
    public void test() throws Throwable {
        this.actions = new CloudSampleActions(benchmark().measure(this, "putShoppingList", getDefaultClient()));
        CloudResponse response = this.actions.putShoppinglistsIdAction("{id}", "{name}", "{description}");
        List<Integer> expectedResults = Arrays.asList(200, 400, 403, 404);
        Assert.assertTrue(expectedResults.contains(response.getStatusLine().getStatusCode()));
    }

    @Override
    public AbstractTest newInstance() {
        return new PutShoppinglistsIdTest();
    }
}