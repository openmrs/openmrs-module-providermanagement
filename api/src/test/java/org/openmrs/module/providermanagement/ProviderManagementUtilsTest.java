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
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Iterator;
import java.util.List;

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
    public void getProviderRoles_shouldGetProviderRoles() {
        Person provider = Context.getPersonService().getPerson(2);
        List<ProviderRole> roles = ProviderManagementUtils.getProviderRoles(provider);
        Assert.assertEquals(new Integer(2), (Integer) roles.size());

        // double-check to make sure the are the correct roles
        // be iterating through and removing the two that SHOULD be there
        Iterator<ProviderRole> i = roles.iterator();

        while (i.hasNext()) {
            ProviderRole role = i.next();
            int id = role.getId();

            if (id == 1001 || id == 1005 ) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, roles.size());
    }


    @Test
    public void getProviderRoles_shouldReturnEmptySetForProviderWithNoRole()  {
        Person provider = Context.getProviderService().getProvider(1002).getPerson();
        List<ProviderRole> roles = ProviderManagementUtils.getProviderRoles(provider);
        Assert.assertEquals(new Integer(0), (Integer) roles.size());
    }

    @Test
    public void getProviderRoles_shouldIgnoreRetiredRoles() {
        Person provider = Context.getPersonService().getPerson(2);
        // retire one provider object associated with this person
        Context.getProviderService().retireProvider(Context.getProviderService().getProvider(1003), "test");

        List<ProviderRole> roles = ProviderManagementUtils.getProviderRoles(provider);
        Assert.assertEquals(new Integer(1), (Integer) roles.size());
        Assert.assertEquals(new Integer(1005), roles.get(0).getId());
    }

    @Test
    public void isProvider_shouldReturnTrue() {
        Assert.assertTrue(ProviderManagementUtils.isProvider(Context.getPersonService().getPerson(2)));
    }

    @Test
    public void isProvider_shouldReturnFalse() {
        Assert.assertFalse(ProviderManagementUtils.isProvider(Context.getPersonService().getPerson(502)));
    }

    @Test
    public void isProvider_shouldReturnTrueEvenIfAllAssociatedProvidersRetired() {
        Context.getProviderService().retireProvider(Context.getProviderService().getProvider(1003), "test");
        Context.getProviderService().retireProvider(Context.getProviderService().getProvider(1009), "test");

        Assert.assertTrue(ProviderManagementUtils.isProvider(Context.getPersonService().getPerson(2)));
    }

    @Test(expected = APIException.class)
    public void isProvider_shouldFailIfPersonNull() {
        ProviderManagementUtils.isProvider(null);
    }
    
    @Test
    public void hasRole_shouldReturnTrue() {
        ProviderRole role1 = Context.getService(ProviderManagementService.class).getProviderRole(1001);
        ProviderRole role2 = Context.getService(ProviderManagementService.class).getProviderRole(1005);
        Person provider = Context.getPersonService().getPerson(2);
        
        Assert.assertTrue(ProviderManagementUtils.hasRole(provider, role1));
        Assert.assertTrue(ProviderManagementUtils.hasRole(provider, role2));
    }

    @Test
    public void hasRole_shouldReturnFalse() {
        ProviderRole role = Context.getService(ProviderManagementService.class).getProviderRole(1002);
        Person provider = Context.getPersonService().getPerson(2);
        Assert.assertFalse(ProviderManagementUtils.hasRole(provider, role));
    }

    @Test
    public void hasRole_shouldReturnFalseIfRoleRetired() {
        ProviderRole role = Context.getService(ProviderManagementService.class).getProviderRole(1001);
        Person provider = Context.getPersonService().getPerson(2);

        // retire the provider object associated with this role
        Context.getProviderService().retireProvider(Context.getProviderService().getProvider(1003), "test");

        Assert.assertFalse(ProviderManagementUtils.hasRole(provider, role));
    }

    @Test
    public void hasRole_shouldReturnFalseIfProviderHasNoRoles() {
        ProviderRole role = Context.getService(ProviderManagementService.class).getProviderRole(1002);
        Person provider = Context.getPersonService().getPerson(1);
        Assert.assertFalse(ProviderManagementUtils.hasRole(provider, role));
    }

    @Test
    public void hasRole_shouldReturnFalseIfPersonIsNotProvider() {
        ProviderRole role = Context.getService(ProviderManagementService.class).getProviderRole(1002);
        Person provider = Context.getPersonService().getPerson(502);
        Assert.assertFalse(ProviderManagementUtils.hasRole(provider, role));
    }

    @Test(expected = APIException.class)
    public void supportsRelationshipType_shouldFailIfRelationshipTypeIsNull() {
        Assert.assertNull(ProviderManagementUtils.supportsRelationshipType(Context.getProviderService().getProvider(1).getPerson(), null));
    }

    @Test
    public void supportsRelationshipType_shouldReturnFalseIfProviderHasNoRole() {
        Person provider = Context.getProviderService().getProvider(1002).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        Assert.assertFalse(ProviderManagementUtils.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnTrueIfProviderSupportsRelationshipType() {
        Person provider = Context.getProviderService().getProvider(1003).getPerson();  // binome
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);  // binome relationship
        Assert.assertTrue(ProviderManagementUtils.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnTrueIfProviderAssociatedWithRoleRetired() {
        Person provider = Context.getProviderService().getProvider(1003).getPerson();  // binome
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);  // binome relationship

        // retire the provider object associated with this role
        Context.getProviderService().retireProvider(Context.getProviderService().getProvider(1003), "test");

        Assert.assertFalse(ProviderManagementUtils.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnFalseIfProviderDoesNotSupportsRelationshipType() {
        Person provider = Context.getProviderService().getProvider(1007).getPerson(); // accompagneteur
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001); // binome relationship
        Assert.assertFalse(ProviderManagementUtils.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnFalseIfProviderPersonHasNoRole() {
        Person provider = Context.getProviderService().getProvider(1002).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        Assert.assertFalse(ProviderManagementUtils.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnTrueIfProviderPersonSupportsRelationshipType() {
        Person provider = Context.getProviderService().getProvider(1003).getPerson();  // binome
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);  // binome relationship
        Assert.assertTrue(ProviderManagementUtils.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnFalseIfProviderPersonDoesNotSupportsRelationshipType() {
        Person provider = Context.getProviderService().getProvider(1007).getPerson(); // accompagneteur
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001); // binome relationship
        Assert.assertFalse(ProviderManagementUtils.supportsRelationshipType(provider, relationshipType));
    }

}
