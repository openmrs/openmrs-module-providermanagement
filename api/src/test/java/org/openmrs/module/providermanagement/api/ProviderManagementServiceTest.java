/**
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
package org.openmrs.module.providermanagement.api;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

/**
 * Tests for ProviderManagementService.
 */
public class  ProviderManagementServiceTest extends BaseModuleContextSensitiveTest {

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
	public void shouldSetupContext() {
		assertNotNull(Context.getService(ProviderManagementService.class));
	}

    @Test
    public void getAllProviderRoles_shouldGetAllProviderUnretiredRoles() {
        List<ProviderRole> roles = providerManagementService.getAllProviderRoles();
        int roleCount = roles.size();
        Assert.assertEquals(9, roleCount);

        roles = providerManagementService.getAllProviderRoles(false);
        roleCount = roles.size();
        Assert.assertEquals(9, roleCount);
    }

    @Test
    public void getAllProviderRoles_shouldGetAllProviderRolesIncludingRetired() {
        List<ProviderRole> roles = providerManagementService.getAllProviderRoles(true);
        int roleCount = roles.size();
        Assert.assertEquals(10, roleCount);
    }

    @Test
    public void getProviderRole_shouldGetProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(2);
        Assert.assertEquals(new Integer(2), role.getId());
        Assert.assertEquals("Binome supervisor", role.getName());
    }

    @Test
    public void getProviderRole_shouldReturnNullIfNoProviderForId() {
        Assert.assertNull(providerManagementService.getProviderRole(200));
    }

    @Test
    public void getProviderRoleByUuid_shoulldGetProviderRoleByUuid() {
        ProviderRole role = providerManagementService.getProviderRoleByUuid("db7f523f-27ce-4bb2-86d6-6d1d05312bd5");
        Assert.assertEquals(new Integer(3), role.getId());
        Assert.assertEquals("Cell supervisor", role.getName());
    }

    @Test
    public void getProviderRoleByUuid_shouldReturnNUllIfNoProviderForUuid() {
        ProviderRole role = providerManagementService.getProviderRoleByUuid("zzz");
    }

    @Test
    public void saveProviderRole_shouldSaveBasicProviderRole() {
        ProviderRole role = new ProviderRole();
        role.setName("Some provider role");
        Context.getService(ProviderManagementService.class).saveProviderRole(role);
        Assert.assertEquals(10, providerManagementService.getAllProviderRoles().size());
    }

    @Test
    public void deleteProviderRole_shouldDeleteProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(2);
        providerManagementService.purgeProviderRole(role);
        Assert.assertEquals(8, providerManagementService.getAllProviderRoles().size());
        Assert.assertNull(providerManagementService.getProviderRole(2));
    }
    
    @Test
    public void retireProviderRole_shouldRetireProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(2);
        providerManagementService.retireProviderRole(role, "test");
        Assert.assertEquals(8, providerManagementService.getAllProviderRoles().size());
        
        role = providerManagementService.getProviderRole(2);
        Assert.assertTrue(role.isRetired());
        Assert.assertEquals("test", role.getRetireReason());
        
    }

    @Test
    public void unretireProviderRole_shouldUnretireProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(2);
        providerManagementService.retireProviderRole(role, "test");
        Assert.assertEquals(8, providerManagementService.getAllProviderRoles().size());

       role = providerManagementService.getProviderRole(2);
       providerManagementService.unretireProviderRole(role);
       Assert.assertFalse(role.isRetired());
    }

    @Test
    public void getProviderProviderRole_shouldGetProviderProviderRole() {
        Provider provider = Context.getProviderService().getProvider(1);  // provider from the standard test dataset
        ProviderRole role = providerManagementService.getProviderRole(provider);
        Assert.assertEquals(new Integer(2), role.getId());
    }

    @Test
    public void setProviderProviderRole_shouldSetProviderProviderRole() {
        // change the provider role for the existing provider
        Provider provider = Context.getProviderService().getProvider(1);  // provider from the standard test dataset
        ProviderRole role = providerManagementService.getProviderRole(3);
        providerManagementService.setProviderRole(provider,role);

        // now make sure we can fetch that role
        ProviderRole role2 = providerManagementService.getProviderRole(provider);
        Assert.assertEquals(new Integer(3), role2.getId());
    }

    // TODO: tests for nullifying a provider role, and for getting provider role when there isn't oen for a provider

}
