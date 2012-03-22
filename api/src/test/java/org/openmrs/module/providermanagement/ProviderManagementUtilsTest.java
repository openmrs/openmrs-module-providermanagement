/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.providermanagement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class ProviderManagementUtilsTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/providermanagement/include/";

    protected static final String XML_DATASET = "providerManagement-dataset.xml";

    private ProviderManagementService providerManagementService;

    @Before
    public void init() throws Exception {
        // execute the provider management test dataset
        executeDataSet(XML_DATASET_PATH + XML_DATASET);

        // initialize the service
        providerManagementService = Context.getService(ProviderManagementService.class);
    }

    @Test
    public void getProviderProviderRole_shouldGetProviderProviderRole() {
        Provider provider = Context.getProviderService().getProvider(1006);
        ProviderRole role = ProviderManagementUtils.getProviderRole(provider);
        Assert.assertEquals(new Integer(1002), role.getId());
    }


    @Test
    public void getProviderRole_shouldReturnNullForProviderWithNoRole()  {
        // change the provider role for the existing provider
        Provider provider = Context.getProviderService().getProvider(1002);
        ProviderRole role = ProviderManagementUtils.getProviderRole(provider);
        Assert.assertNull(role);
    }
    
    @Test(expected = APIException.class)
    public void supportsRelationshipType_shouldFailIfProviderIsNull() {
        Assert.assertNull(ProviderManagementUtils.supportsRelationshipType(null,Context.getPersonService().getRelationshipType(1001)));
    }

    @Test(expected = APIException.class)
    public void supportsRelationshipType_shouldFailIfRelationshipTypeIsNull() {
        Assert.assertNull(ProviderManagementUtils.supportsRelationshipType(Context.getProviderService().getProvider(1), null));
    }

    @Test
    public void supportsRelationshipType_shouldReturnFalseIfProviderHasNoRole() {
        Provider provider = Context.getProviderService().getProvider(1002);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        Assert.assertFalse(ProviderManagementUtils.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnTrueIfProviderSupportsRelationshipType() {
        Provider provider = Context.getProviderService().getProvider(1003);  // binome
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);  // binome relationship
        Assert.assertTrue(ProviderManagementUtils.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnFalseIfProviderDoesNotSupportsRelationshipType() {
        Provider provider = Context.getProviderService().getProvider(1007); // accompagneteur
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001); // binome relationship
        Assert.assertFalse(ProviderManagementUtils.supportsRelationshipType(provider, relationshipType));
    }

}
