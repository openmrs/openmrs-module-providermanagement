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
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class ProviderRoleTest extends BaseModuleContextSensitiveTest {

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
    public void shouldCreateNewProviderRole() {
        new ProviderRole();
    }
    
    @Test
    public void shouldSpecifyWhetherRoleIsSupervisorRole() {
        ProviderRole role = providerManagementService.getProviderRole(1001);
        Assert.assertFalse(role.isSupervisorRole());

        role = providerManagementService.getProviderRole(1002);
        Assert.assertTrue(role.isSupervisorRole());

        role = providerManagementService.getProviderRole(1003);
        Assert.assertTrue(role.isSupervisorRole());
    }

    @Test
    public void shouldSpecifyWhetherRoleIsDirectCareRole() {
        ProviderRole role = providerManagementService.getProviderRole(1001);
        Assert.assertTrue(role.isDirectPatientCareRole());

        role = providerManagementService.getProviderRole(1002);
        Assert.assertTrue(role.isDirectPatientCareRole());

        role = providerManagementService.getProviderRole(1003);
        Assert.assertFalse(role.isDirectPatientCareRole());
    }

    @Test
    public void shouldTestWhetherRoleSupportsRelationshipType() {
        ProviderRole role =  providerManagementService.getProviderRole(1011);
        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType accompagneteur =  Context.getPersonService().getRelationshipType(1002);
        
        Assert.assertFalse(role.supportsRelationshipType(binome));
        Assert.assertTrue(role.supportsRelationshipType(accompagneteur));
    }

    @Test
    public void equalsTest() {
        ProviderRole role1 = providerManagementService.getProviderRole(1001);
        ProviderRole role2 = providerManagementService.getProviderRole(1001);
        ProviderRole role3 = providerManagementService.getProviderRole(1002);
        
        Assert.assertTrue(role1.equals(role2));
        Assert.assertFalse(role1.equals(role3));
    }
}
