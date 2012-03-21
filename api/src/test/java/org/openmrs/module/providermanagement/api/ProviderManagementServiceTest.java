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
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Iterator;
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
        Assert.assertEquals(10, roleCount);

        roles = providerManagementService.getAllProviderRoles(false);
        roleCount = roles.size();
        Assert.assertEquals(10, roleCount);
    }

    @Test
    public void getAllProviderRoles_shouldGetAllProviderRolesIncludingRetired() {
        List<ProviderRole> roles = providerManagementService.getAllProviderRoles(true);
        int roleCount = roles.size();
        Assert.assertEquals(11, roleCount);
    }

    @Test
    public void getProviderRole_shouldGetProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        Assert.assertEquals(new Integer(1002), role.getId());
        Assert.assertEquals("Binome supervisor", role.getName());
    }

    @Test
    public void getProviderRole_shouldReturnNullIfNoProviderForId() {
        Assert.assertNull(providerManagementService.getProviderRole(200));
    }

    @Test
    public void getProviderRoleByUuid_shoulldGetProviderRoleByUuid() {
        ProviderRole role = providerManagementService.getProviderRoleByUuid("db7f523f-27ce-4bb2-86d6-6d1d05312bd5");
        Assert.assertEquals(new Integer(1003), role.getId());
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
        Assert.assertEquals(11, providerManagementService.getAllProviderRoles().size());
    }

    @Test
    public void deleteProviderRole_shouldDeleteProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.purgeProviderRole(role);
        Assert.assertEquals(9, providerManagementService.getAllProviderRoles().size());
        Assert.assertNull(providerManagementService.getProviderRole(1002));
    }
    
    @Test
    public void retireProviderRole_shouldRetireProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.retireProviderRole(role, "test");
        Assert.assertEquals(9, providerManagementService.getAllProviderRoles().size());
        
        role = providerManagementService.getProviderRole(1002);
        Assert.assertTrue(role.isRetired());
        Assert.assertEquals("test", role.getRetireReason());
        
    }

    @Test
    public void unretireProviderRole_shouldUnretireProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.retireProviderRole(role, "test");
        Assert.assertEquals(9, providerManagementService.getAllProviderRoles().size());

       role = providerManagementService.getProviderRole(1002);
       providerManagementService.unretireProviderRole(role);
       Assert.assertFalse(role.isRetired());
    }

    @Test
    public void getProviderProviderRole_shouldGetProviderProviderRole() {
        Provider provider = Context.getProviderService().getProvider(1006);
        ProviderRole role = providerManagementService.getProviderRole(provider);
        Assert.assertEquals(new Integer(1002), role.getId());
    }

    @Test
    public void setProviderProviderRole_shouldSetProviderProviderRole() {
        // change the provider role for the existing provider
        Provider provider = Context.getProviderService().getProvider(1006);
        ProviderRole role = providerManagementService.getProviderRole(1003);
        providerManagementService.setProviderRole(provider,role);

        // now make sure we can fetch that role
        ProviderRole role2 = providerManagementService.getProviderRole(provider);
        Assert.assertEquals(new Integer(1003), role2.getId());
    }

    @Test
    public void getProviderRole_shouldReturnNullForProviderWithNoRole()  {
        // change the provider role for the existing provider
        Provider provider = Context.getProviderService().getProvider(1002);
        ProviderRole role = providerManagementService.getProviderRole(provider);
        Assert.assertNull(role);
    }

    @Test
    public void setProviderRole_shouldNullifyProviderRole()  {
        // change the provider role for the existing provider
        Provider provider = Context.getProviderService().getProvider(1006);
        providerManagementService.setProviderRole(provider,null);

        // now make sure we can fetch that role
        ProviderRole role = providerManagementService.getProviderRole(provider);
        Assert.assertNull(role);
    }

    @Test
    public void getProviders_shouldGetProvidersByRole() {
        ProviderRole role = providerManagementService.getProviderRole(1001);
        List<Provider> providers = providerManagementService.getProviders(role);

        // there should be three providers with the binome role
        Assert.assertEquals(3, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the three that SHOULD be there
        Iterator<Provider> i = providers.iterator();
        
        while (i.hasNext()) {
            Provider provider = i.next();
            int id = provider.getId();

            if (id == 1003 || id == 1004  || id == 1005) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test(expected = APIException.class)
    public void getProviders_shouldFailIfCalledWithNull() {
        List<Provider> providers = providerManagementService.getProviders(null);
    }

    // TODO: what happens if you get the providers with NO role?  what should happen?
    // TODO: document what happens is null is passed

    // TODO: test retired after TRUNK-3170 is completed



}
