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

import org.hibernate.ObjectNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Provider;
import org.openmrs.ProviderAttributeType;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderManagementUtils;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.exception.DateCannotBeInFutureException;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.InvalidSupervisorException;
import org.openmrs.module.providermanagement.exception.PatientAlreadyAssignedToProviderException;
import org.openmrs.module.providermanagement.exception.PatientNotAssignedToProviderException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.exception.ProviderAlreadyAssignedToSupervisorException;
import org.openmrs.module.providermanagement.exception.ProviderDoesNotSupportRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.ProviderNotAssignedToSupervisorException;
import org.openmrs.module.providermanagement.exception.ProviderRoleInUseException;
import org.openmrs.module.providermanagement.exception.SourceProviderSameAsDestinationProviderException;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;


/**
 * Tests for ProviderManagementService.
 */
public class  ProviderManagementServiceTest extends BaseModuleContextSensitiveTest {

    // TODO: add some more tests of the retired use cases

    protected static final String XML_DATASET_PATH = "org/openmrs/module/providermanagement/include/";

    protected static final String XML_DATASET = "providerManagement-dataset.xml";

    private ProviderManagementService providerManagementService;

    public static final Date DATE = ProviderManagementUtils.clearTimeComponent(new Date());

    public static final Date PAST_DATE = ProviderManagementUtils.clearTimeComponent(new Date(DATE.getTime() - 31536000000L));

    public static final Date FURTHER_PAST_DATE =ProviderManagementUtils.clearTimeComponent(new Date(PAST_DATE.getTime() - 31536000000L));

    public static final Date FUTURE_DATE = ProviderManagementUtils.clearTimeComponent(new Date(DATE.getTime() + 31536000000L));

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
    public void getAllProviderRoles_shouldGetAllProviderRoles() {
        List<ProviderRole> roles = providerManagementService.getAllProviderRoles(true);
        int roleCount = roles.size();
        Assert.assertEquals(12, roleCount);

        roles = providerManagementService.getAllProviderRoles(true);
        roleCount = roles.size();
        Assert.assertEquals(12, roleCount);
    }

    @Test
    public void getAllProviderRoles_shouldGetAllProviderRolesExcludingRetired() {
        List<ProviderRole> roles = providerManagementService.getAllProviderRoles(false);
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
    public void confirmRelationshipTypesAndSuperviseesProperlyAssociatedWithProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1002);

        // just check the counts as a sanity check
        Assert.assertEquals(2, role.getRelationshipTypes().size());
        Assert.assertEquals(1, role.getSuperviseeProviderRoles().size());
    }

    @Test
    public void confirmProviderAttributeTypesProperlyAssociatedWithProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1001);

        // just check the counts as a sanity check
        Assert.assertEquals(2, role.getProviderAttributeTypes().size());
    }

    @Test
    public void getProviderRole_shouldReturnNullIfNoProviderForId() {
        Assert.assertNull(providerManagementService.getProviderRole(200));
    }

    @Test
    public void getProviderRoleByUuid_shouldGetProviderRoleByUuid() {
        ProviderRole role = providerManagementService.getProviderRoleByUuid("db7f523f-27ce-4bb2-86d6-6d1d05312bd5");
        Assert.assertEquals(new Integer(1003), role.getId());
        Assert.assertEquals("Cell supervisor", role.getName());
    }

    @Test
    public void getProviderRoleByUuid_shouldReturnNUllIfNoProviderForUuid() {
        ProviderRole role = providerManagementService.getProviderRoleByUuid("zzz");
    }

    @Test
    public void getProviderRolesByRelationshipType_shouldGetAllProviderRolesThatSupportRelationshipType() {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1002);
        List<ProviderRole> providerRoles = providerManagementService.getProviderRolesByRelationshipType(relationshipType);
        Assert.assertEquals(5, providerRoles.size());
        
        // confirm that the right provider roles have been returned
        Iterator<ProviderRole> i = providerRoles.iterator();

        while (i.hasNext()) {
            ProviderRole providerRole = i.next();
            int id = providerRole.getId();

            if (id == 1001 || id == 1002  || id == 1006 || id == 1009 || id == 1011) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providerRoles.size());
    }

    @Test
    public void getProviderRolesByRelationshipType_shouldReturnEmptyListForRelationshipTypeNotAssociatedWithAnyProviderRoles() {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);  // a relationship type in the standard test dataset
        List<ProviderRole> providerRoles = providerManagementService.getProviderRolesByRelationshipType(relationshipType);
        Assert.assertEquals(0, providerRoles.size());
    }

    @Test(expected = APIException.class)
    public void getProviderRolesByRelationshipType_shouldThrowExceptionIfRelationshipTypeNull() {
        providerManagementService.getProviderRolesByRelationshipType(null);
    }

    @Test
    public void getProviderRolesBySuperviseeProviderRole_shouldGetAllProviderRolesThatCanSuperviseeProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1001);
        List<ProviderRole> providerRoles = providerManagementService.getProviderRolesBySuperviseeProviderRole(role);
        Assert.assertEquals(5, providerRoles.size());

        // confirm that the right provider roles have been returned
        Iterator<ProviderRole> i = providerRoles.iterator();

        while (i.hasNext()) {
            ProviderRole providerRole = i.next();
            int id = providerRole.getId();

            if (id == 1002 || id == 1003  || id == 1004 || id == 1005 || id == 1008) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providerRoles.size());
    }

    @Test
    public void getProviderRolesBySuperviseeProviderRole_shouldReturnEmptyListForProviderRoleThatHasNoSupervisorRoles() {
        ProviderRole role = providerManagementService.getProviderRole(1004);
        List<ProviderRole> providerRoles = providerManagementService.getProviderRolesBySuperviseeProviderRole(role);
        Assert.assertEquals(0, providerRoles.size());
    }

    @Test(expected = APIException.class)
    public void getProviderRolesBySuperviseeProviderRole_shouldThrowExceptionIfProviderRoleNull() {
        providerManagementService.getProviderRolesBySuperviseeProviderRole(null);
    }

    @Test
        public void saveProviderRole_shouldSaveBasicProviderRole() {
        ProviderRole role = new ProviderRole();
        role.setName("Some provider role");
        Context.getService(ProviderManagementService.class).saveProviderRole(role);
        Assert.assertEquals(13, providerManagementService.getAllProviderRoles(true).size());
    }

    @Test
    public void saveProviderRole_shouldSaveProviderRoleWithProviderAttributeTypes() {
        ProviderRole role = new ProviderRole();
        role.setName("Some provider role");

        Set<ProviderAttributeType> attributeTypes = new HashSet<ProviderAttributeType>();
        attributeTypes.add(Context.getProviderService().getProviderAttributeType(1001));
        attributeTypes.add(Context.getProviderService().getProviderAttributeType(1002));

        Context.getService(ProviderManagementService.class).saveProviderRole(role);
        Assert.assertEquals(13, providerManagementService.getAllProviderRoles(true).size());
    }

    @Test
    public void deleteProviderRole_shouldDeleteProviderRole() throws Exception {
        ProviderRole role = providerManagementService.getProviderRole(1012);
        providerManagementService.purgeProviderRole(role);
        Assert.assertEquals(11, providerManagementService.getAllProviderRoles(true).size());
        Assert.assertNull(providerManagementService.getProviderRole(1012));
    }

    @Test(expected = ProviderRoleInUseException.class)
    public void deleteProviderRole_shouldFailIfForeignKeyConstraintExists() throws Exception {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.purgeProviderRole(role);
    }

    @Test
    public void retireProviderRole_shouldRetireProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.retireProviderRole(role, "test");
        Assert.assertEquals(10, providerManagementService.getAllProviderRoles(false).size());
        
        role = providerManagementService.getProviderRole(1002);
        Assert.assertTrue(role.isRetired());
        Assert.assertEquals("test", role.getRetireReason());
        
    }

    @Test
    public void unretireProviderRole_shouldUnretireProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.retireProviderRole(role, "test");
        Assert.assertEquals(10, providerManagementService.getAllProviderRoles(false).size());

       role = providerManagementService.getProviderRole(1002);
       providerManagementService.unretireProviderRole(role);
       Assert.assertFalse(role.isRetired());
    }

    @Test
    public void getAllProviderRoleRelationshipTypes_shouldGetAllProviderRelationshipTypes() {
        List<RelationshipType> relationshipTypes = providerManagementService.getAllProviderRoleRelationshipTypes(true);
        Assert.assertEquals(3, relationshipTypes.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the three that SHOULD be there
        Iterator<RelationshipType> i = relationshipTypes.iterator();

        while (i.hasNext()) {
            RelationshipType relationshipType = i.next();
            int id = relationshipType.getId();

            if (id == 1001 || id == 1002  || id == 1003) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, relationshipTypes.size());
    }

    @Test
    public void getAllProviderRoleRelationshipTypes_shouldGetAllNonRetiredProviderRelationshipTypes() {
        // retire one of the relationship types
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1003);
        Context.getPersonService().retireRelationshipType(relationshipType, "test");

        // verify that there are now only 2
        List<RelationshipType> relationshipTypes = providerManagementService.getAllProviderRoleRelationshipTypes(false);
        Assert.assertEquals(2, relationshipTypes.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the three that SHOULD be there
        Iterator<RelationshipType> i = relationshipTypes.iterator();

        while (i.hasNext()) {
            relationshipType = i.next();
            int id = relationshipType.getId();

            if (id == 1001 || id == 1002) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, relationshipTypes.size());
    }

    @Test
    public void getProviderRoles_shouldGetProviderRoles() {
        Person provider = Context.getPersonService().getPerson(2);
        List<ProviderRole> roles = providerManagementService.getProviderRoles(provider);
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
        List<ProviderRole> roles = providerManagementService.getProviderRoles(provider);
        Assert.assertEquals(new Integer(0), (Integer) roles.size());
    }

    @Test
    public void getProviderRoles_shouldIgnoreRetiredRoles() {
        Person provider = Context.getPersonService().getPerson(2);
        // retire one provider object associated with this person
        Context.getProviderService().retireProvider(Context.getProviderService().getProvider(1003), "test");

        List<ProviderRole> roles = providerManagementService.getProviderRoles(provider);
        Assert.assertEquals(new Integer(1), (Integer) roles.size());
        Assert.assertEquals(new Integer(1005), roles.get(0).getId());
    }

    @Test
    public void assignProviderRoleToPerson_shouldAssignProviderRole() {
        // add a new role to the existing provider
        Person provider = Context.getProviderService().getProvider(1006).getPerson();
        ProviderRole role = providerManagementService.getProviderRole(1003);
        providerManagementService.assignProviderRoleToPerson(provider, role, "123");

        // the provider should now have two roles
        List<ProviderRole> providerRoles = providerManagementService.getProviderRoles(provider);
        Assert.assertEquals(2, providerRoles.size());

        // double-check to make sure the are the correct roles
        // be iterating through and removing the two that SHOULD be there
        Iterator<ProviderRole> i = providerRoles.iterator();

        while (i.hasNext()) {
            ProviderRole providerRole = i.next();
            int id = providerRole.getId();

            if (id == 1002 || id == 1003) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providerRoles.size());
    }

    @Test
    public void assignProviderRoleToPerson_shouldNotFailIfProviderAlreadyHasRole() {
        // add a role that the provider already has
        Person provider = Context.getProviderService().getProvider(1006).getPerson();
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.assignProviderRoleToPerson(provider, role, "123");

        // the provider should still only have one role
        List<ProviderRole> providerRoles = providerManagementService.getProviderRoles(provider);
        Assert.assertEquals(1, providerRoles.size());
        Assert.assertEquals(new Integer(1002), providerRoles.get(0).getId());
    }

    @Test(expected = APIException.class)
    public void assignProviderRoleToPerson_shouldFailIfUnderlyingPersonVoided() {
        Person provider = Context.getProviderService().getProvider(1006).getPerson();
        ProviderRole role = providerManagementService.getProviderRole(1002);
        
        // void this person, then attempt to add a role to it
        Context.getPersonService().voidPerson(provider, "test");
        
        providerManagementService.assignProviderRoleToPerson(provider, role, "123");
    }

    @Test
    public void unassignProviderRoleFromPerson_shouldUnassignRoleFromProvider() {
        Person provider = Context.getProviderService().getProvider(1006).getPerson();
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.unassignProviderRoleFromPerson(provider, role);
        
        Assert.assertEquals(0, providerManagementService.getProviderRoles(provider).size());

        Provider p = Context.getProviderService().getProvider(1006);
        Assert.assertTrue(p.isRetired());
    }

    @Test
    public void unassignProviderRoleFromPerson_shouldLeaveOtherRoleUntouched() {
        // get the provider with two roles
        Person provider = Context.getPersonService().getPerson(2);
        
        // unassign one of these roles
        providerManagementService.unassignProviderRoleFromPerson(provider, providerManagementService.getProviderRole(1001));
        
        // verify that only the other role remains
        List<ProviderRole> roles = providerManagementService.getProviderRoles(provider);
        Assert.assertEquals(1, roles.size());
        Assert.assertEquals(new Integer(1005), roles.get(0).getId());
    }

    @Test
    public void unassignProviderRoleFromPerson_shouldNotFailIfProviderDoesNotHaveRole() {
        // get a binome
        Person provider = Context.getPersonService().getPerson(6);

        // unassign some other role
        providerManagementService.unassignProviderRoleFromPerson(provider, providerManagementService.getProviderRole(1002));

        // verify that the binome role still remains
        List<ProviderRole> roles = providerManagementService.getProviderRoles(provider);
        Assert.assertEquals(1,roles.size());
        Assert.assertEquals(new Integer(1001), roles.get(0).getId());
    }

   @Test
   public void unassignProviderRoleFromPerson_shouldNotFailIfProviderHasNoRoles() {
       // get the provider with no roles
       Person provider = Context.getPersonService().getPerson(1);

       // unassign some role that this person does not have
       providerManagementService.unassignProviderRoleFromPerson(provider, providerManagementService.getProviderRole(1002));

       List<ProviderRole> roles = providerManagementService.getProviderRoles(provider);
       Assert.assertEquals(0, roles.size());
    }

    @Test
    public void unassignProviderRoleFromPerson_shouldNotFailIfPersonIsNotProvider() {
        // get the provider with no roles
        Person provider = Context.getPersonService().getPerson(502);

        // unassign some role
        providerManagementService.unassignProviderRoleFromPerson(provider, providerManagementService.getProviderRole(1002));
        Assert.assertTrue(!providerManagementService.isProvider(provider));
    }

    @Test(expected = ObjectNotFoundException.class)
    public void purgeProviderRoleFromPerson_shouldPurgeRoleFromProvider() {
        Person provider = Context.getProviderService().getProvider(1006).getPerson();
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.purgeProviderRoleFromPerson(provider, role);

        Assert.assertEquals(0, providerManagementService.getProviderRoles(provider).size());
        Context.getProviderService().getProvider(1006).getId();  // should not be able to find this object
    }

    @Test
    public void purgeProviderRoleFromPerson_shouldLeaveOtherRoleUntouched() {
        // get the provider with two roles
        Person provider = Context.getPersonService().getPerson(2);

        // purge one of these roles
        providerManagementService.purgeProviderRoleFromPerson(provider, providerManagementService.getProviderRole(1001));

        // verify that only the other role remains
        List<ProviderRole> roles = providerManagementService.getProviderRoles(provider);
        Assert.assertEquals(1, roles.size());
        Assert.assertEquals(new Integer(1005), roles.get(0).getId());
    }

    @Test
    public void purgeProviderRoleFromPerson_shouldNotFailIfProviderDoesNotHaveRole() {
        // get a binome
        Person provider = Context.getPersonService().getPerson(6);

        // purge some other role
        providerManagementService.purgeProviderRoleFromPerson(provider, providerManagementService.getProviderRole(1002));

        // verify that the binome role still remains
        List<ProviderRole> roles = providerManagementService.getProviderRoles(provider);
        Assert.assertEquals(1,roles.size());
        Assert.assertEquals(new Integer(1001), roles.get(0).getId());
    }

    @Test
    public void purgeProviderRoleFromPerson_shouldNotFailIfProviderHasNoRoles() {
        // get the provider with no roles
        Person provider = Context.getPersonService().getPerson(1);

        // purge some role that this person does not have
        providerManagementService.purgeProviderRoleFromPerson(provider, providerManagementService.getProviderRole(1002));

        List<ProviderRole> roles = providerManagementService.getProviderRoles(provider);
        Assert.assertEquals(0, roles.size());
    }

    @Test
    public void purgeProviderRoleFromPerson_shouldNotFailIfPersonIsNotProvider() {
        // get the provider with no roles
        Person provider = Context.getPersonService().getPerson(502);

        // purge some role
        providerManagementService.purgeProviderRoleFromPerson(provider, providerManagementService.getProviderRole(1002));
        Assert.assertTrue(!providerManagementService.isProvider(provider));
    }

    @Test
    public void getProvidersByRole_shouldGetProvidersByRole() {
        ProviderRole role = providerManagementService.getProviderRole(1001);
        List<Person> providers = providerManagementService.getProvidersAsPersonsByRole(role);

        // there should be three providers with the binome role
        Assert.assertEquals(3, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the three that SHOULD be there
        Iterator<Person> i = providers.iterator();
        
        while (i.hasNext()) {
            Person provider = i.next();
            int id = provider.getId();

            if (id == 2 || id == 6  || id == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProvidersByRole_shouldIgnoreRetiredProviders() {

        // retire a provider
        Provider providerToRetire = Context.getProviderService().getProvider(1003);
        Context.getProviderService().retireProvider(providerToRetire, "test");

        ProviderRole role = providerManagementService.getProviderRole(1001);
        List<Person> providers = providerManagementService.getProvidersAsPersonsByRole(role);

        // there should now only be three providers with the binome role
        Assert.assertEquals(2, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the three that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person provider = i.next();
            int id = provider.getId();

            if (id == 6  || id == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test(expected = APIException.class)
    public void getProvidersByRole_shouldFailIfCalledWithNull() {
        List<Person> providers = providerManagementService.getProvidersAsPersonsByRole(null);
    }

    @Test
    public void getProvidersByRoles_shouldGetProvidersByRole() {

        // retire a provider
        Provider providerToRetire = Context.getProviderService().getProvider(1003);
        Context.getProviderService().retireProvider(providerToRetire, "test");

        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));
        roles.add(providerManagementService.getProviderRole(1002));

        List<Person> providers = providerManagementService.getProvidersAsPersonsByRoles(roles);

        // there should be four providers with the binome  or binome supervisor role
        Assert.assertEquals(3, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the three that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person provider = i.next();
            int id = provider.getId();

            if (id == 6  || id == 7 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProvidersByRoles_shouldIgnoreRetiredRoles() {
        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));
        roles.add(providerManagementService.getProviderRole(1002));

        List<Person> providers = providerManagementService.getProvidersAsPersonsByRoles(roles);

        // there should be four providers with the binome  or binome supervisor role
        Assert.assertEquals(4, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the three that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person provider = i.next();
            int id = provider.getId();

            if (id == 2 || id == 6  || id == 7 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test(expected = APIException.class)
    public void getProvidersByRoles_shouldFailIfCalledWithNull() {
        List<Person> providers = providerManagementService.getProvidersAsPersonsByRole(null);
    }



    @Test
    public void getProvidersByRelationshipType_shouldReturnProvidersThatSupportRelationshipType() {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1002);
        List<Person> providers = providerManagementService.getProvidersAsPersonsByRelationshipType(relationshipType);

        // there should be four providers (the 3 binomes, the binome supervisor, and the accompagnateur) that support the accompagnateur relationship

        Assert.assertEquals(5, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the three that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person provider = i.next();
            int id = provider.getId();

            if (id == 2 || id == 6  || id == 7 || id == 8 || id == 9) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProvidersByRelationshipType_shouldReturnEmptyListIfNoMatchingProvidersFound() {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);   // a relationship type from the standard test data
        List<Person> providers = providerManagementService.getProvidersAsPersonsByRelationshipType(relationshipType);
        Assert.assertEquals(0, providers.size());

        // also try a relationship type that has a matching role, but no providers have that role
        relationshipType = Context.getPersonService().getRelationshipType(1003);
        providers = providerManagementService.getProvidersAsPersonsByRelationshipType(relationshipType);
        Assert.assertEquals(0, providers.size());
    }

    @Test(expected = APIException.class)
    public void getProvidersByRelationshipType_shouldFailIfCalledWithNull() {
        List<Person> providers = providerManagementService.getProvidersAsPersonsByRelationshipType(null);
    }

    @Test
    public void getProviderRolesThatCanSuperviseThisProvider_shouldReturnProviderRolesThatCanSuperviseProvider() {
        Person provider = Context.getPersonService().getPerson(2);
        List<ProviderRole> roles = providerManagementService.getProviderRolesThatCanSuperviseThisProvider(provider);
        Assert.assertEquals(5, roles.size());

        // double-check to make sure the are the correct roles
        // be iterating through and removing the three that SHOULD be there
        Iterator<ProviderRole> i = roles.iterator();
        
        while (i.hasNext()) {
            ProviderRole role = i.next();

            int id = role.getId();

            if (id == 1002 || id == 1003 || id == 1004 || id == 1005 || id == 1008) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, roles.size());
    }

    @Test
    public void getProviderRolesThatCanSuperviseThisProvider_shouldReturnEmptyListIfNoMatchingProvidersFound() {
        Person provider = Context.getPersonService().getPerson(501);
        List<ProviderRole> roles = providerManagementService.getProviderRolesThatCanSuperviseThisProvider(provider);
        Assert.assertEquals(0, roles.size());
    }

    @Test(expected = APIException.class)
    public void getProviderRolesThatCanSuperviseThisProvider_shouldFailIfCalledWithNull() {
        providerManagementService.getProviderRolesThatCanSuperviseThisProvider(null);
    }

    @Test
    public void isProvider_shouldReturnTrue() {
        Assert.assertTrue(providerManagementService.isProvider(Context.getPersonService().getPerson(2)));
    }

    @Test
    public void isProvider_shouldReturnFalse() {
        Assert.assertFalse(providerManagementService.isProvider(Context.getPersonService().getPerson(502)));
    }

    @Test
    public void isProvider_shouldReturnTrueEvenIfAllAssociatedProvidersRetired() {
        Context.getProviderService().retireProvider(Context.getProviderService().getProvider(1003), "test");
        Context.getProviderService().retireProvider(Context.getProviderService().getProvider(1009), "test");

        Assert.assertTrue(providerManagementService.isProvider(Context.getPersonService().getPerson(2)));
    }

    @Test(expected = APIException.class)
    public void isProvider_shouldFailIfPersonNull() {
        providerManagementService.isProvider(null);
    }

    @Test
    public void hasRole_shouldReturnTrue() {
        ProviderRole role1 = Context.getService(ProviderManagementService.class).getProviderRole(1001);
        ProviderRole role2 = Context.getService(ProviderManagementService.class).getProviderRole(1005);
        Person provider = Context.getPersonService().getPerson(2);

        Assert.assertTrue(providerManagementService.hasRole(provider, role1));
        Assert.assertTrue(providerManagementService.hasRole(provider, role2));
    }

    @Test
    public void hasRole_shouldReturnFalse() {
        ProviderRole role = Context.getService(ProviderManagementService.class).getProviderRole(1002);
        Person provider = Context.getPersonService().getPerson(2);
        Assert.assertFalse(providerManagementService.hasRole(provider, role));
    }

    @Test
    public void hasRole_shouldReturnFalseIfRoleRetired() {
        ProviderRole role = Context.getService(ProviderManagementService.class).getProviderRole(1001);
        Person provider = Context.getPersonService().getPerson(2);

        // retire the provider object associated with this role
        Context.getProviderService().retireProvider(Context.getProviderService().getProvider(1003), "test");

        Assert.assertFalse(providerManagementService.hasRole(provider, role));
    }

    @Test
    public void hasRole_shouldReturnFalseIfProviderHasNoRoles() {
        ProviderRole role = Context.getService(ProviderManagementService.class).getProviderRole(1002);
        Person provider = Context.getPersonService().getPerson(1);
        Assert.assertFalse(providerManagementService.hasRole(provider, role));
    }

    @Test
    public void hasRole_shouldReturnFalseIfPersonIsNotProvider() {
        ProviderRole role = Context.getService(ProviderManagementService.class).getProviderRole(1002);
        Person provider = Context.getPersonService().getPerson(502);
        Assert.assertFalse(providerManagementService.hasRole(provider, role));
    }

    @Test(expected = APIException.class)
    public void supportsRelationshipType_shouldFailIfRelationshipTypeIsNull() {
        Assert.assertNull(providerManagementService.supportsRelationshipType(Context.getProviderService().getProvider(1).getPerson(), null));
    }

    @Test
    public void supportsRelationshipType_shouldReturnFalseIfProviderHasNoRole() {
        Person provider = Context.getProviderService().getProvider(1002).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        Assert.assertFalse(providerManagementService.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnTrueIfProviderSupportsRelationshipType() {
        Person provider = Context.getProviderService().getProvider(1003).getPerson();  // binome
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);  // binome relationship
        Assert.assertTrue(providerManagementService.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnTrueIfProviderAssociatedWithRoleRetired() {
        Person provider = Context.getProviderService().getProvider(1003).getPerson();  // binome
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);  // binome relationship

        // retire the provider object associated with this role
        Context.getProviderService().retireProvider(Context.getProviderService().getProvider(1003), "test");

        Assert.assertFalse(providerManagementService.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnFalseIfProviderDoesNotSupportsRelationshipType() {
        Person provider = Context.getProviderService().getProvider(1007).getPerson(); // accompagneteur
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001); // binome relationship
        Assert.assertFalse(providerManagementService.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnFalseIfProviderPersonHasNoRole() {
        Person provider = Context.getProviderService().getProvider(1002).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        Assert.assertFalse(providerManagementService.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnTrueIfProviderPersonSupportsRelationshipType() {
        Person provider = Context.getProviderService().getProvider(1003).getPerson();  // binome
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);  // binome relationship
        Assert.assertTrue(providerManagementService.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void supportsRelationshipType_shouldReturnFalseIfProviderPersonDoesNotSupportsRelationshipType() {
        Person provider = Context.getProviderService().getProvider(1007).getPerson(); // accompagneteur
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001); // binome relationship
        Assert.assertFalse(providerManagementService.supportsRelationshipType(provider, relationshipType));
    }

    @Test
    public void getProviderRolesThatProviderCanSupervise_shouldReturnRolesThatProviderCanSupervise() {
        Person provider = Context.getPersonService().getPerson(2); // person who is both a binome supervisor and a community health nurse
        List<ProviderRole> roles = providerManagementService.getProviderRolesThatProviderCanSupervise(provider);
        Assert.assertEquals(new Integer (3), (Integer) roles.size());

        // double-check to make sure the are the correct roles
        // by iterating through and removing the two that SHOULD be there
        Iterator<ProviderRole> i = roles.iterator();

        while (i.hasNext()) {
            ProviderRole role = i.next();
            int id = role.getId();

            if (id == 1001 || id == 1002 || id == 1011) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, roles.size());

    }

    @Test
    public void getProviderRolesThatProviderCanSupervise_shouldReturnEmptyListIfProviderRoleDoesNotSupportSupervision() {
        Person provider = Context.getPersonService().getPerson(6); // person who just a binome
        List<ProviderRole> roles = providerManagementService.getProviderRolesThatProviderCanSupervise(provider);
        Assert.assertEquals(new Integer (0), (Integer) roles.size());
    }

    @Test
    public void getProviderRolesThatProviderCanSupervise_shouldReturnEmptyListIfPersonIsNotProvider() {
        Person provider = Context.getPersonService().getPerson(502); // person who is not a provider
        List<ProviderRole> roles = providerManagementService.getProviderRolesThatProviderCanSupervise(provider);
        Assert.assertEquals(new Integer (0), (Integer) roles.size());
    }

    @Test(expected = APIException.class)
    public void getProviderRolesThatProviderCanSupervise_shouldReturnEmptyListIfProviderNull() {
        List<ProviderRole> roles = providerManagementService.getProviderRolesThatProviderCanSupervise(null);
        Assert.assertEquals(new Integer (0), (Integer) roles.size());
    }

    @Test
    public void canSupervise_shouldReturnTrue() {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person supervisee = Context.getPersonService().getPerson(6);    // binome
        Assert.assertTrue(providerManagementService.canSupervise(supervisor, supervisee));
    }

    @Test
    public void canSupervise_shouldReturnFalse() {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person supervisee = Context.getPersonService().getPerson(9);    // accompagnateur
        Assert.assertFalse(providerManagementService.canSupervise(supervisor, supervisee));

        supervisor = Context.getPersonService().getPerson(6);  // binome
        supervisee = Context.getPersonService().getPerson(7);    // binome
        Assert.assertFalse(providerManagementService.canSupervise(supervisor, supervisee));
    }

    @Test
    public void canSupervise_shouldReturnFalseIfSupervisorAndSuperviseeAreSamePerson() {
        Person supervisor = Context.getPersonService().getPerson(2);  // person who is both a binome and community health nurse
        Person supervisee = Context.getPersonService().getPerson(2);    // same person
        Assert.assertFalse(providerManagementService.canSupervise(supervisor, supervisee));
    }
    
    @Test(expected = APIException.class)
    public void assignPatientToProvider_shouldFailIfPatientNull() throws Exception {
        Person provider = Context.getProviderService().getProvider(1).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerManagementService.assignPatientToProvider(null, provider, relationshipType, null);
    }

    @Test(expected = APIException.class)
    public void assignPatientToProvider_shouldFailIfProviderNull() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerManagementService.assignPatientToProvider(patient, null, relationshipType, null);
    }

    @Test(expected = APIException.class)
    public void assignPatientToProvider_shouldFailIfRelationshipTypeNull() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        Person provider = Context.getProviderService().getProvider(1003).getPerson();
        providerManagementService.assignPatientToProvider(patient, provider, null, null);
    }

    @Test(expected = APIException.class)
    public void assignPatientToProvider_shouldFailIfPatientVoided() throws Exception {
        Patient patient = Context.getPatientService().getPatient(999);  // voided patient from the standard test dataset
        Person provider = Context.getProviderService().getProvider(1003).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, null);
    }

    @Test(expected = ProviderDoesNotSupportRelationshipTypeException.class)
    public void assignPatientToProvider_shouldFailIfProviderDoesNotSupportRelationshipType() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        Person provider = Context.getProviderService().getProvider(1007).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, null);
    }

    @Test
    public void assignPatientToProvider_shouldAssignPatientToProvider() throws Exception {
        Patient patient = Context.getPatientService().getPatient(8);
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, null);

        // note that we've got a voided relationship of this type in the sample test data to confirm
        // that voided relationships are ignored when checked for existing relationships

        // confirm that the relationship has been created with the appropriate start date
        List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(patient);
        Assert.assertEquals(1,relationships.size());
        Relationship relationship = relationships.get(0);
        Assert.assertEquals(patient, relationship.getPersonB());
        Assert.assertEquals(provider, relationship.getPersonA());
        Assert.assertEquals(relationship.getStartDate(), ProviderManagementUtils.clearTimeComponent(DATE));
    }

    @Test(expected = PatientAlreadyAssignedToProviderException.class)
    public void assignPatientToProvider_shouldFailIfRelationshipAlreadyExists() throws Exception {
        Patient patient = Context.getPatientService().getPatient(8);
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType);

       // if we try to do the same thing again, it should fail
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType);
    }
    
    @Test(expected = PatientNotAssignedToProviderException.class)
    public void unassignPatientFromProvider_shouldFailIfRelationshipDoesNotExist() throws Exception {
        Patient patient = Context.getPatientService().getPatient(8);
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.unassignPatientFromProvider(patient, provider, relationshipType, null);
    }

    @Test
    public void unassignPatientFromProvider_shouldUnassignPatientFromProvider() throws Exception {
        // first, assign a patient to a provider
        Patient patient = Context.getPatientService().getPatient(8);
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);

        // now, end that relationship on today's date
        providerManagementService.unassignPatientFromProvider(patient, provider, relationshipType);

        // confirm that the relationship has been ended
        List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(patient);
        Assert.assertEquals(1,relationships.size());   // sanity check
        Relationship relationship = relationships.get(0);
        Assert.assertEquals(relationship.getEndDate(), ProviderManagementUtils.clearTimeComponent(DATE));
    }

    @Test(expected = APIException.class)
    public void unassignAllPatientsFromProvider_shouldFailIfProviderNull() throws Exception {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerManagementService.unassignAllPatientsFromProvider(null, relationshipType);
    }

    @Test(expected = APIException.class)
    public void unassignAllPatientsFromProvider_shouldFailIfRelationshipTypeNull() throws Exception {
        Person provider = Context.getProviderService().getProvider(1003).getPerson();
        providerManagementService.unassignAllPatientsFromProvider(provider, null);
    }

    @Test
    public void unassignAllPatientsFromProvider_shouldUnassignAllPatientsFromProvider() throws Exception {
        // first, assign a couple patients to a provider
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);

        // sanity check
        Assert.assertEquals(2,Context.getPersonService().getRelationships(provider, null, relationshipType).size());

        // now end all the relationships
        providerManagementService.unassignAllPatientsFromProvider(provider, relationshipType);

        // there still should be 2 relationships, but they both should have a end date of the current date
        List<Relationship> relationships = Context.getPersonService().getRelationships(provider, null, relationshipType);
        Assert.assertEquals(2, relationships.size());
        for (Relationship relationship : relationships) {
            Assert.assertEquals(ProviderManagementUtils.clearTimeComponent(DATE), relationship.getEndDate());
        }
        
    }

    @Test
    public void unnassignAllPatientsFromProvider_shouldUnassignAllPatientsOfMultipleRelationshipTypesFromProvider() throws Exception {
        // first, assign a couple patients to a provider
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        
        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, binome, PAST_DATE);
        
        RelationshipType accompagneteur = Context.getPersonService().getRelationshipType(1002);
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, accompagneteur, PAST_DATE);

        // now end all the relationships
        providerManagementService.unassignAllPatientsFromProvider(provider);

        // assert that both the relationships exist, with an end date of the current date
        List<Relationship> relationships = Context.getPersonService().getRelationships(provider, null, binome);
        Assert.assertEquals(1,relationships.size());
        Assert.assertEquals(ProviderManagementUtils.clearTimeComponent(DATE), relationships.get(0).getEndDate());

        relationships = Context.getPersonService().getRelationships(provider, null, accompagneteur);
        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(ProviderManagementUtils.clearTimeComponent(DATE), relationships.get(0).getEndDate());
    }

    @Test
    public void unassignAllPatientsFromProvider_shouldNotFailIfProviderHasNoPatients() throws Exception {
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        // just confirm that this doesn't throw an exception
        providerManagementService.unassignAllPatientsFromProvider(provider);
    }
    
    @Test
    public void getPatients_shouldGetAllPatientsOfAProviderOnCurrentDate() throws Exception{
        
        // first, assign a couple patients to a provider
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // assign this patient in the past
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);

        // assign this patient on today's date
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);


        // confirm that the two patients are reported as patients of the provider
        List<Patient> patients = providerManagementService.getPatientsOfProvider(provider, relationshipType, new Date());
        Assert.assertEquals(2, patients.size());

        // double-check to make sure the are the correct patients
        // be iterating through and removing the three that SHOULD be there
        Iterator<Patient> i = patients.iterator();

        while (i.hasNext()) {
            Patient p = i.next();
            int id = p.getId();

            if (id == 2 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, patients.size());
    }

    @Test
    public void getPatients_shouldReturnEmptyListIfProviderHasNoPatients() throws Exception {
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        List<Patient> patients = providerManagementService.getPatientsOfProvider(provider, relationshipType, new Date());
        Assert.assertEquals(0, patients.size());
    }

    @Test
    public void getPatients_shouldGetPatientsOfAProviderOnSpecifiedDate() throws Exception{

        // first, assign a couple patients to a provider (but on different dates)
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // assign this patient in the past
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);

        // assign this patient on today's date
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // confirm that only the patient added in the past is reported if we query on the past date
        List<Patient> patients = providerManagementService.getPatientsOfProvider(provider, relationshipType, PAST_DATE);
        Assert.assertEquals(1, patients.size());
        Assert.assertEquals(new Integer(2), patients.get(0).getId());

    }

    @Test
    public void getPatients_shouldGetAllPatientsOfAProvider() throws Exception {

        // first, assign a couple patients to a provider (but on different dates)
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // assign this patient in the past
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);

        // assign this patient on today's date
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // confirm that both patients are returned if we query across all dates
        List<Patient> patients = providerManagementService.getPatientsOfProvider(provider, relationshipType);
        Assert.assertEquals(2, patients.size());

        // double-check to make sure the are the correct patients
        // be iterating through and removing the three that SHOULD be there
        Iterator<Patient> i = patients.iterator();

        while (i.hasNext()) {
            Patient p = i.next();
            int id = p.getId();

            if (id == 2 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, patients.size());
    }

    @Test
    public void getPatients_shouldIgnorePatientsOfADifferentRelationshipType() throws Exception{

        // first, assign a couple patients to a provider (but via different relationships)
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // assign this patient using a different relationship type
        relationshipType = Context.getPersonService().getRelationshipType(1002);
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // confirm that only the patient of the specified relationship type is returned
        List<Patient> patients = providerManagementService.getPatientsOfProvider(provider, relationshipType, DATE);
        Assert.assertEquals(1, patients.size());
        Assert.assertEquals(new Integer(8), patients.get(0).getId());

    }

    @Test
    public void getPatients_shouldIgnoreVoidedPatients() throws Exception {

        //  assign a couple patients to a provider (but on different dates)
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // now void one of the patients
        Patient p = Context.getPatientService().getPatient(2);
        Context.getPatientService().voidPatient(p, "test");
        
        // confirm that only the non-voided patient is returned
        List<Patient> patients = providerManagementService.getPatientsOfProvider(provider, relationshipType, DATE);
        Assert.assertEquals(1, patients.size());
        Assert.assertEquals(new Integer(8), patients.get(0).getId());
    }

    @Test(expected = InvalidRelationshipTypeException.class)
    public void getPatients_shouldFailIfRelationshipTypeIsNotProviderToPatientType() throws Exception {
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerManagementService.getPatientsOfProvider(provider, relationshipType);
    }

    @Test(expected = APIException.class)
    public void getPatients_shouldFailIfProviderNull() throws Exception {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.getPatientsOfProvider(null, relationshipType);
    }

    @Test(expected = APIException.class)
    public void getPatients_shouldFailIfInvalidRelationshipFound() throws Exception {
        Person person = Context.getPersonService().getPerson(502);  // person from standard test dataset who is not a patient
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // sanity check
        Assert.assertFalse(person.isPatient());
        
        // create this "illegal" relationship (illegal because person b is not a patient)
        Relationship relationship = new Relationship();
        relationship.setStartDate(ProviderManagementUtils.clearTimeComponent(DATE));
        relationship.setPersonA(provider);
        relationship.setPersonB(person);
        relationship.setRelationshipType(relationshipType);
        Context.getPersonService().saveRelationship(relationship);

        // this should fail when it finds a relationship to someone who isn't a patient
        providerManagementService.getPatientsOfProvider(provider, relationshipType).size();
    }

    @Test
    public void getPatients_shouldGetAllPatientsOfProvider() throws Exception {
        // first, assign a couple patients to a provider
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // assign this patient in the past
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);

        // assign this patient on today's date, but using a different relationship type
        patient = Context.getPatientService().getPatient(8);
        relationshipType = Context.getPersonService().getRelationshipType(1002);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // confirm that the two patients are reported as patients of the provider
        List<Patient> patients = providerManagementService.getPatientsOfProvider(provider, null, new Date());
        Assert.assertEquals(2, patients.size());

        // double-check to make sure the are the correct patients
        // be iterating through and removing the three that SHOULD be there
        Iterator<Patient> i = patients.iterator();

        while (i.hasNext()) {
            Patient p = i.next();
            int id = p.getId();

            if (id == 2 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, patients.size());
    }

    @Test
    public void getPatients_shouldGetAllPatientsOfProviderOnSpecifiedDate() throws Exception {
        // first, assign a couple patients to a provider
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // assign this patient in the past
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);

        // assign this patient on today's date, but using a different relationship type
        patient = Context.getPatientService().getPatient(8);
        relationshipType = Context.getPersonService().getRelationshipType(1002);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // fetch the patients on the past date
        List<Patient> patients = providerManagementService.getPatientsOfProvider(provider, null, PAST_DATE);

        // confirm that only the patient from the past is present
        Assert.assertEquals(1, patients.size());
        Assert.assertEquals(new Integer(2), patients.get(0).getId());
    }

    @Test
    public void getPatientsOfProviderCount_shouldGetCountOfAllPatientsOfAProviderOnCurrentDate() throws Exception {

        // first, assign a couple patients to a provider
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // assign this patient in the past
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);

        // assign this patient on today's date
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // confirm that the count returns two patients
        Assert.assertEquals(2, providerManagementService.getPatientsOfProviderCount(provider, relationshipType, new Date()));
    }

    @Test
    public void getPatientsOfProviderCount_shouldReturnZeroIfProviderHasNoPatients() throws Exception {
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        Assert.assertEquals(0, providerManagementService.getPatientsOfProviderCount(provider, relationshipType, new Date()));
    }

    @Test
    public void getPatientsOfProviderCount_shouldGetCountOfPatientsOfAProviderOnSpecifiedDate() throws Exception{

        // first, assign a couple patients to a provider (but on different dates)
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // assign this patient in the past
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);

        // assign this patient on today's date
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        Assert.assertEquals(1, providerManagementService.getPatientsOfProviderCount(provider, relationshipType, PAST_DATE));
    }

    @Test
    public void getPatientsOfProviderCount_shouldGetCountOfAllPatientsOfAProvider() throws Exception {

        // first, assign a couple patients to a provider (but on different dates)
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // assign this patient in the past
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, PAST_DATE);

        // assign this patient on today's date
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // confirm that both patients are counted if we query across all dates
        Assert.assertEquals(2,  providerManagementService.getPatientsOfProviderCount(provider, relationshipType, null));
    }

    @Test
    public void getPatientsOfProviderCount_shouldIgnorePatientsOfADifferentRelationshipType() throws Exception{

        // first, assign a couple patients to a provider (but via different relationships)
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // assign this patient using a different relationship type
        relationshipType = Context.getPersonService().getRelationshipType(1002);
        patient = Context.getPatientService().getPatient(8);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, DATE);

        // confirm that only the patient of the specified relationship type are counted
        Assert.assertEquals(1, providerManagementService.getPatientsOfProviderCount(provider, relationshipType, DATE));
    }


    @Test
    public void getProviderRelationships_shouldReturnAllRelationshipsForPatient() throws Exception {
        
        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);
        
        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);
        
        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);
        
        // now confirm that these two relationships are returned when we call getProviderRelationshipsForPatient
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, null, null, new Date());

        // there should be two relationships
        Assert.assertEquals(2, relationships.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Relationship> i = relationships.iterator();

        while (i.hasNext()) {
            Relationship r = i.next();
            int provider = r.getPersonA().getId();
            int relationshipType = r.getRelationshipType().getId();

            if ( (provider == 6 && relationshipType == 1001) || (provider == 8 && relationshipType == 1002)) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, relationships.size());
        
    }

    @Test
    public void getProviderRelationships_shouldReturnRelationshipWithSpecifiedProvider() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now fetch only relationships with a specific provider
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, provider1, null, new Date());

        // there should be one relationship
        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(new Integer(6), relationships.get(0).getPersonA().getId());
        Assert.assertEquals(new Integer(1001), relationships.get(0).getRelationshipType().getId());
    }

    @Test
    public void getProviderRelationships_shouldReturnRelationshipWithSpecifiedRelationshipType() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now fetch only relationships with a specific relationship type
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, null, acc, new Date());

        // there should be one relationship
        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(new Integer(8), relationships.get(0).getPersonA().getId());
        Assert.assertEquals(new Integer(1002), relationships.get(0).getRelationshipType().getId());
    }

    @Test
     public void getProviderRelationships_shouldReturnRelationshipWithSpecifiedProviderAndRelationshipType() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now fetch only relationships with a specific provider type
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, provider2, acc, new Date());

        // there should be one relationship
        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(new Integer(8), relationships.get(0).getPersonA().getId());
        Assert.assertEquals(new Integer(1002), relationships.get(0).getRelationshipType().getId());
    }

    @Test
    public void getProviderRelationships_shouldReturnEmptyListIfPatientHasNoProviderRelationships() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, null, null, new Date());
        Assert.assertEquals(0, relationships.size());
    }

    @Test(expected = APIException.class)
    public void getProviderRelationships_shouldFailIfPatientNull() throws Exception {
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(null, null, null);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void getProviderRelationships_shouldFailIfPersonIsNotProvider() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        Person person = Context.getPersonService().getPerson(502);
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, person, null);
    }

    @Test(expected = InvalidRelationshipTypeException.class)
    public void getProviderRelationships_shouldFailIfRelationshipIsNotProviderRelationship() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, null, relationshipType);
    }

    @Test
    public void getProviderRelationships_shouldIgnoreNonProviderRelationships() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // set up a non-provider relationship (so we can test that getProviderRelationshipsForPatient ignore it)
        Relationship relationship = new Relationship();
        relationship.setPersonA(provider1);
        relationship.setPersonB(patient);
        relationship.setRelationshipType(Context.getPersonService().getRelationshipType(1));
        
        // now confirm that these two relationships are returned when we call getProviderRelationshipsForPatient
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, null, null, new Date());

        // there should be two relationships
        Assert.assertEquals(2, relationships.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Relationship> i = relationships.iterator();

        while (i.hasNext()) {
            Relationship r = i.next();
            int provider = r.getPersonA().getId();
            int relationshipType = r.getRelationshipType().getId();

            if ( (provider == 6 && relationshipType == 1001) || (provider == 8 && relationshipType == 1002)) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, relationships.size());
    }

    @Test
    public void getProviderRelationships_shouldReturnRelationshipsOnSpecifiedDate() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient--but one in the past, and one in the present
        providerManagementService.assignPatientToProvider(patient, provider1, binome, PAST_DATE);
        providerManagementService.assignPatientToProvider(patient, provider2, acc, DATE);

        // now get relationships in the past
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, null, null, PAST_DATE);

        // there should be one relationship
        Assert.assertEquals(1,relationships.size());
        Assert.assertEquals(new Integer(6), relationships.get(0).getPersonA().getId());
        Assert.assertEquals(new Integer(1001), relationships.get(0).getRelationshipType().getId());
    }

    @Test
    public void getProviderRelationships_shouldReturnAllRelationships() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient--but one in the past, and one in the present
        providerManagementService.assignPatientToProvider(patient, provider1, binome, PAST_DATE);
        providerManagementService.assignPatientToProvider(patient, provider2, acc, DATE);

        // now get relationships without specifying a date
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, null, null);

        // both relationships should be returned
        Assert.assertEquals(2,relationships.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Relationship> i = relationships.iterator();

        while (i.hasNext()) {
            Relationship r = i.next();
            int provider = r.getPersonA().getId();
            int relationshipType = r.getRelationshipType().getId();

            if ( (provider == 6 && relationshipType == 1001) || (provider == 8 && relationshipType == 1002)) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, relationships.size());
    }


    @Test
    public void getProviderRelationships_shouldIgnoreVoidedRelationships() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now fetch and void the binome relationship
        List<Relationship> relationships = providerManagementService.getProviderRelationshipsForPatient(patient, null, binome, new Date());
        Context.getPersonService().voidRelationship(relationships.get(0), "test");
        
        // now fetch all relationships
        relationships = providerManagementService.getProviderRelationshipsForPatient(patient, null, null, new Date());

        // there should be only one relationship returned
        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(new Integer(8), relationships.get(0).getPersonA().getId());
        Assert.assertEquals(new Integer(1002), relationships.get(0).getRelationshipType().getId());
    }


    @Test
    public void getProvidersForPatient_shouldReturnAllProvidersForPatient() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now confirm that these two providers are returned when we call getProviderRelationshipsForPatient
        List<Person> providers = providerManagementService.getProvidersAsPersonsForPatient(patient, null, new Date());

        // there should be two providers
        Assert.assertEquals(2, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the two that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person person = i.next();
            int id = person.getId();

            if (id == 6 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProvidersForPatient_shouldReturnProviderWithSpecifiedRelationshipType() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now fetch only providers with a specific relationship type
        List<Person> providers = providerManagementService.getProvidersAsPersonsForPatient(patient, acc, new Date());

        // there should be one provider
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(8), providers.get(0).getId());
    }

    @Test
    public void getProvidersForPatient_shouldReturnEmptyListIfPatientHasNoProviderRelationships() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        List<Person> providers = providerManagementService.getProvidersAsPersonsForPatient(patient, null, new Date());
        Assert.assertEquals(0, providers.size());
    }

    @Test(expected = APIException.class)
    public void getProvidersForPatient_shouldFailIfPatientNull() throws Exception {
        providerManagementService.getProvidersAsPersonsForPatient(null, null);
    }

    @Test(expected = InvalidRelationshipTypeException.class)
    public void getProvidersForPatient_shouldFailIfRelationshipIsNotProviderRelationship() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerManagementService.getProvidersAsPersonsForPatient(patient, relationshipType);
    }

    @Test
    public void getProvidersForPatient_shouldReturnProvidersOnSpecifiedDate() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient--but one in the past, and one in the present
        providerManagementService.assignPatientToProvider(patient, provider1, binome, PAST_DATE);
        providerManagementService.assignPatientToProvider(patient, provider2, acc, DATE);

        // now get relationships in the past
        List<Person> providers = providerManagementService.getProvidersAsPersonsForPatient(patient, null, PAST_DATE);

        // there should be one relationship
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(6), providers.get(0).getId());
    }


    @Test
    public void getProvidersForPatient_shouldReturnAllProviders() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient--but one in the past, and one in the present
        providerManagementService.assignPatientToProvider(patient, provider1, binome, PAST_DATE);
        providerManagementService.assignPatientToProvider(patient, provider2, acc, DATE);

        // now get relationships without specifying a date
        List<Person> providers = providerManagementService.getProvidersAsPersonsForPatient(patient, null, null);

        // there should be two relationships
        Assert.assertEquals(2, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the two that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person person = i.next();
            int id = person.getId();

            if (id == 6 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProvidersForPatient_shouldIgnoreRetiredProviders() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now fetch only providers with a specific relationship type
        List<Person> providers = providerManagementService.getProvidersAsPersonsForPatient(patient, acc, new Date());

        // there should be one provider
        Assert.assertEquals(1,providers.size());
        Assert.assertEquals(new Integer(8), providers.get(0).getId());
    }

    @Test
    public void getProvidersForPatient_shouldNotIgnoreRetiredProviders() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now retire one of the the providers
        Provider providerToRetire = Context.getProviderService().getProvider(1004);
        Context.getProviderService().retireProvider(providerToRetire, "test");

        // now confirm that these two providers are returned when we call getProviderRelationshipsForPatient
        List<Person> providers = providerManagementService.getProvidersAsPersonsForPatient(patient, null, new Date());

        // there should be two providers
        Assert.assertEquals(2, providers.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person person = i.next();
            int id = person.getId();

            if (id == 6 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }
   
    @Test
    public void transferAllPatients_shouldTransferAllPatientsFromOneProviderToAnother() throws Exception {
        // first, assign a couple patients to a provider
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // first patient
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // for the second patient, use a different relationship type
        patient = Context.getPatientService().getPatient(8);
        relationshipType = Context.getPersonService().getRelationshipType(1002);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // now move these patients to the new provider
        Person newProvider = Context.getProviderService().getProvider(1005).getPerson();
        providerManagementService.transferAllPatients(oldProvider, newProvider);

        // now fetch the patients of each provider and verify that they are accurate
        List<Patient> oldProviderPatients = providerManagementService.getPatientsOfProvider(oldProvider, null, new Date());
        List<Patient> newProviderPatients = providerManagementService.getPatientsOfProvider(newProvider, null, new Date());

        // on the current date, both patients should now be assigned to the new provider, but not the old provider
        Assert.assertEquals(0, oldProviderPatients.size());
        Assert.assertEquals(2, newProviderPatients.size());

        // double check that the patients assigned to the new provider are correct
        Iterator<Patient> i = newProviderPatients.iterator();

        while (i.hasNext()) {
            Patient p = i.next();
            int id = p.getId();

            if (id == 2 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, newProviderPatients.size());
    }

    @Test
    public void transferAllPatients_shouldTransferAllPatientsFromOneProviderToAnotherOnSpecifiedDate() throws Exception {
        // first, assign a couple patients to a provider
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // first patient
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, FURTHER_PAST_DATE);

        // for the second patient, use a different relationship type
        patient = Context.getPatientService().getPatient(8);
        relationshipType = Context.getPersonService().getRelationshipType(1002);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // now set up a transfer for PAST_DATE
        Person newProvider = Context.getProviderService().getProvider(1005).getPerson();
        providerManagementService.transferAllPatients(oldProvider, newProvider, PAST_DATE);

        // now fetch the patients of each provider and verify that they are accurate
        List<Patient> oldProviderPatients = providerManagementService.getPatientsOfProvider(oldProvider, null, new Date());
        List<Patient> newProviderPatients = providerManagementService.getPatientsOfProvider(newProvider, null, new Date());

        // on the current date, the first patient should have been transferred to the new provider, but the second patient should still be with the first provider
        Assert.assertEquals(1, oldProviderPatients.size());
        Assert.assertEquals(1, newProviderPatients.size());

        Assert.assertEquals(new Integer(8), oldProviderPatients.get(0).getId());
        Assert.assertEquals(new Integer(2), newProviderPatients.get(0).getId());
    }

    @Test
    public void transferAllPatients_shouldTransferAllPatientsOfSpecifiedRelationshipTypeFromOneProviderToAnother() throws Exception {
        // first, assign a couple patients to a provider
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // first patient
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // for the second patient, use a different relationship type
        patient = Context.getPatientService().getPatient(8);
        relationshipType = Context.getPersonService().getRelationshipType(1002);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // now move the patient from the second relationship type to the new provider
        Person newProvider = Context.getProviderService().getProvider(1005).getPerson();
        providerManagementService.transferAllPatients(oldProvider, newProvider, relationshipType);

        // on some future date, one patient should still be associated to the old provider
        // and the other patient should be associated with the new provider
        List<Patient> oldProviderPatients = providerManagementService.getPatientsOfProvider(oldProvider, null, FUTURE_DATE);
        List<Patient> newProviderPatients = providerManagementService.getPatientsOfProvider(newProvider, null, FUTURE_DATE);
        Assert.assertEquals(1, oldProviderPatients.size());
        Assert.assertEquals(new Integer(2), oldProviderPatients.get(0).getId());
        Assert.assertEquals(1, newProviderPatients.size());
        Assert.assertEquals(new Integer(8), newProviderPatients.get(0).getId());
    }

    @Test
    public void transferAllPatients_shouldTransferAllPatientsOfSpecifiedRelationshipTypeFromOneProviderToAnotherOnSpecifiedDate() throws Exception {
        // first, assign a couple patients to a provider
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // first patient
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // for the second patient and third patients, use a different relationship type
        patient = Context.getPatientService().getPatient(8);
        relationshipType = Context.getPersonService().getRelationshipType(1002);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, FURTHER_PAST_DATE);

        patient = Context.getPatientService().getPatient(7);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // now move the patient from the second relationship type to the new provider
        Person newProvider = Context.getProviderService().getProvider(1005).getPerson();
        providerManagementService.transferAllPatients(oldProvider, newProvider, relationshipType, PAST_DATE);

        // on some future date, only patient #8 should now be associated with the new provider
        List<Patient> oldProviderPatients = providerManagementService.getPatientsOfProvider(oldProvider, null, FUTURE_DATE);
        List<Patient> newProviderPatients = providerManagementService.getPatientsOfProvider(newProvider, null, FUTURE_DATE);
        Assert.assertEquals(2, oldProviderPatients.size());
        Assert.assertEquals(1, newProviderPatients.size());
        Assert.assertEquals(new Integer(8), newProviderPatients.get(0).getId());
    }

    @Test
    public void transferAllPatients_shouldNotFailIfSourceProviderHasNoPatients() throws Exception {
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        Person newProvider = Context.getProviderService().getProvider(1005).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.transferAllPatients(oldProvider, newProvider);

        // confirm that neither provider has any patients
        List<Patient> oldProviderPatients = providerManagementService.getPatientsOfProvider(oldProvider, null, new Date());
        List<Patient> newProviderPatients = providerManagementService.getPatientsOfProvider(newProvider, null, new Date());
        Assert.assertEquals(0, oldProviderPatients.size());
        Assert.assertEquals(0, newProviderPatients.size());
    }

    @Test(expected = APIException.class)
    public void transferAllPatients_shouldFailIfSourceProviderNull() throws Exception {
        Person newProvider = Context.getProviderService().getProvider(1005).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.transferAllPatients(null, newProvider, relationshipType);
    }

    @Test(expected = APIException.class)
    public void transferAllPatients_shouldFailIfDestinationProviderNull() throws Exception {
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.transferAllPatients(oldProvider, null, relationshipType);
    }

    @Test(expected = SourceProviderSameAsDestinationProviderException.class)
    public void transferAllPatients_shouldFailIfSourceProviderEqualsDestinationProvider() throws Exception {
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        Person newProvider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.transferAllPatients(oldProvider, newProvider, relationshipType);
    }

    @Test(expected = DateCannotBeInFutureException.class)
    public void transferAllPatients_shouldFailIfFutureDate() throws Exception {
        Person oldProvider = Context.getProviderService().getProvider(1005).getPerson();
        Person newProvider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // assign a patient
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        providerManagementService.transferAllPatients(oldProvider, newProvider, relationshipType, FUTURE_DATE);
    }

    @Test(expected = ProviderDoesNotSupportRelationshipTypeException.class)
    public void transferAllPatients_shouldFailIfDestinationProviderDoesNotSupportRequiredRelationshipType() throws Exception {
        // first, assign a patients to a binome via a binome relatinship
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // now attempt to move the patient to a provider that does not support the binome relationshp
        Person newProvider = Context.getProviderService().getProvider(1007).getPerson();
        providerManagementService.transferAllPatients(oldProvider, newProvider, relationshipType);
    }

    @Test
    public void transferAllPatients_shouldNotFailIfPatientAlreadyAssociatedWithDestinationPatient() throws Exception {
        // first, assign a couple patients to a provider
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        // first patient
        Patient patient = Context.getPatientService().getPatient(2);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // for the second patient, use a different relationship type
        patient = Context.getPatientService().getPatient(8);
        relationshipType = Context.getPersonService().getRelationshipType(1002);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // now, assign the patient to the destinaton provider as well
        Person newProvider = Context.getProviderService().getProvider(1005).getPerson();
        providerManagementService.assignPatientToProvider(patient, newProvider, relationshipType, DATE);

        // sanity check
        List<Patient> oldProviderPatients = providerManagementService.getPatientsOfProvider(oldProvider, null, new Date());
        List<Patient> newProviderPatients = providerManagementService.getPatientsOfProvider(newProvider, null, new Date());
        Assert.assertEquals(2, oldProviderPatients.size());
        // the second patient should already be assigned to the destination provider
        Assert.assertEquals(1, newProviderPatients.size());
        Assert.assertEquals(new Integer(8), newProviderPatients.get(0).getId());

        // now do the transfer; everything should work, although the patient has already been assigned to new provider
        providerManagementService.transferAllPatients(oldProvider, newProvider, relationshipType);

        oldProviderPatients = providerManagementService.getPatientsOfProvider(oldProvider, null, FUTURE_DATE);
        newProviderPatients = providerManagementService.getPatientsOfProvider(newProvider, null, FUTURE_DATE);
        Assert.assertEquals(1, oldProviderPatients.size());
        Assert.assertEquals(new Integer(2), oldProviderPatients.get(0).getId());
        Assert.assertEquals(1, newProviderPatients.size());
        Assert.assertEquals(new Integer(8), newProviderPatients.get(0).getId());
    }

    @Test
    public void transferAllPatients_shouldTransferPatientWithMultipleRelationshipTypes() throws Exception {
        // first, assign the same patient to a provider via two different relationship types
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);
        relationshipType = Context.getPersonService().getRelationshipType(1002);
        providerManagementService.assignPatientToProvider(patient, oldProvider, relationshipType, DATE);

        // now move the patient to the new provider
        Person newProvider = Context.getProviderService().getProvider(1005).getPerson();
        providerManagementService.transferAllPatients(oldProvider, newProvider);

        // on some future date, the patient should be associated with new provider, but not the old
        List<Patient> oldProviderPatients = providerManagementService.getPatientsOfProvider(oldProvider, null, FUTURE_DATE);
        List<Patient> newProviderPatients = providerManagementService.getPatientsOfProvider(newProvider, null, FUTURE_DATE);
        Assert.assertEquals(0, oldProviderPatients.size());
        Assert.assertEquals(1, newProviderPatients.size());
        Assert.assertEquals(new Integer(2), newProviderPatients.get(0).getId());

        // double check that we can fetch the patient from the destination provider via either relationship type
        relationshipType = Context.getPersonService().getRelationshipType(1001);
        newProviderPatients = providerManagementService.getPatientsOfProvider(newProvider, relationshipType, FUTURE_DATE);
        Assert.assertEquals(1, newProviderPatients.size());
        Assert.assertEquals(new Integer(2), newProviderPatients.get(0).getId());

        relationshipType = Context.getPersonService().getRelationshipType(1002);
        newProviderPatients = providerManagementService.getPatientsOfProvider(newProvider, relationshipType, FUTURE_DATE);
        Assert.assertEquals(1, newProviderPatients.size());
        Assert.assertEquals(new Integer(2), newProviderPatients.get(0).getId());
    }

    @Test
    public void getSupervisorRelationshipType_shouldGetSupervisorRelationshipType() {
        RelationshipType supervisorRelationshipType = providerManagementService.getSupervisorRelationshipType();
        Assert.assertEquals(new Integer(1004), supervisorRelationshipType.getId());
    }

    @Test
    public void assignProviderToSupervisor_shouldAssignProviderToSupervisor() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person provider = Context.getPersonService().getPerson(6);    // binome

        // do the assignment
        providerManagementService.assignProviderToSupervisor(provider, supervisor);

        // verify that the relationship now exists
        List<Relationship> relationships = Context.getPersonService().getRelationships(supervisor, provider, providerManagementService.getSupervisorRelationshipType());
        Assert.assertEquals(1, relationships.size());
    }

    @Test
    public void assignProviderToSupervisor_shouldAssignProviderToSupervisorOnSpecifiedDates() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, supervisor, PAST_DATE);
        providerManagementService.assignProviderToSupervisor(provider2, supervisor, DATE);

        // if we query the past date, only the first relationship should exist
        List<Relationship> relationships = Context.getPersonService().getRelationships(supervisor, null, providerManagementService.getSupervisorRelationshipType(), PAST_DATE);
        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(provider1, relationships.get(0).getPersonB());

        // but if query on the current date, both the relationships should exist
        relationships = Context.getPersonService().getRelationships(supervisor, null, providerManagementService.getSupervisorRelationshipType(), DATE);
        Assert.assertEquals(2, relationships.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Relationship> i = relationships.iterator();

        while (i.hasNext()) {
            Relationship r = i.next();

            if (r.getPersonB().getId() == 6 || r.getPersonB().getId() == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, relationships.size());
    }

    @Test(expected = APIException.class)
    public void assignProviderToSupervisor_shouldFailIfProviderNull() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        providerManagementService.assignProviderToSupervisor(null, supervisor);
    }

    @Test(expected = APIException.class)
    public void assignProviderToSupervisor_shouldFailIfSupervisorNull() throws Exception {
        Person provider = Context.getPersonService().getPerson(6);  // binome
        providerManagementService.assignProviderToSupervisor(provider, null);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void assignProviderToSupervisor_shouldFailIfProviderNotAProvider() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person provider = Context.getPersonService().getPerson(502);    // not a provider
        providerManagementService.assignProviderToSupervisor(provider, supervisor);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void assignProviderToSupervisor_shouldFailIfSupervisorNotAProvider() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(502);  // not a provider
        Person provider = Context.getPersonService().getPerson(6);    // binome
        providerManagementService.assignProviderToSupervisor(provider, supervisor);
    }

    @Test(expected = InvalidSupervisorException.class)
    public void assignProviderToSupervisor_shouldFailIFSupervisorDoesNotSupportProvider() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(7);  // binome
        Person provider = Context.getPersonService().getPerson(6);    // binome
        providerManagementService.assignProviderToSupervisor(provider, supervisor);
    }

    @Test(expected = ProviderAlreadyAssignedToSupervisorException.class)
    public void assignProviderToSupervisor_shouldFailIfProviderAlreadyAssignedToSupervisor() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person provider = Context.getPersonService().getPerson(6);    // binome

        // do the assignment
        providerManagementService.assignProviderToSupervisor(provider, supervisor);

       // if we try to do the assignment again, it should fail
       providerManagementService.assignProviderToSupervisor(provider, supervisor);
    }

    @Test
    public void unassignProviderFromSupervisor_shouldUnassignProviderFromSupervisor() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person provider = Context.getPersonService().getPerson(6);    // binome

        // do the assignment
        providerManagementService.assignProviderToSupervisor(provider, supervisor);

        // now do the unassignment
        providerManagementService.unassignProviderFromSupervisor(provider, supervisor);

        // the relationship should still exist on the current date, but it should have an end date of the current date
        List<Relationship> relationships = Context.getPersonService().getRelationships(supervisor, provider, providerManagementService.getSupervisorRelationshipType(), DATE);
        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(ProviderManagementUtils.clearTimeComponent(new Date()), relationships.get(0).getEndDate());

        // the relationship should not exist on a future date
        // the relationship should still exist on the current date, but it should have an end date of the current date
        relationships = Context.getPersonService().getRelationships(supervisor, provider, providerManagementService.getSupervisorRelationshipType(), FUTURE_DATE);
        Assert.assertEquals(0, relationships.size());
    }

    @Test
    public void unassignProviderFromSupervisor_shouldSetProperEndDate() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person provider = Context.getPersonService().getPerson(6);    // binome

        // do the assignment on a date in the past
        providerManagementService.assignProviderToSupervisor(provider, supervisor, FURTHER_PAST_DATE);

        // now do the unassignment
        providerManagementService.unassignProviderFromSupervisor(provider, supervisor, PAST_DATE);

        // the relationship should not exist on the current date
        // the relationship should still exist on the current date, but it should have an end date of the current date
        List<Relationship> relationships = Context.getPersonService().getRelationships(supervisor, provider, providerManagementService.getSupervisorRelationshipType(), DATE);
        Assert.assertEquals(0, relationships.size());
        
        // confirm that the relationship has the proper end date
        relationships = Context.getPersonService().getRelationships(supervisor, provider, providerManagementService.getSupervisorRelationshipType(), PAST_DATE);
        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(PAST_DATE, relationships.get(0).getEndDate());
    }

    @Test(expected = APIException.class)
    public void unassignProviderFromSupervisor_shouldFailIfProviderNull() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person provider = Context.getPersonService().getPerson(6);    // binome

        // do the assignment
        providerManagementService.assignProviderToSupervisor(provider, supervisor);

        // attempt the unassignment
        providerManagementService.unassignProviderFromSupervisor(null, supervisor);
    }

    @Test(expected = APIException.class)
    public void unassignProviderFromSupervisor_shouldFailIfSupervisorNull() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person provider = Context.getPersonService().getPerson(6);    // binome

        // do the assignment
        providerManagementService.assignProviderToSupervisor(provider, null);


        // attempt the unassignment
        providerManagementService.unassignProviderFromSupervisor(provider, supervisor);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void unassignProviderFromSupervisor_shouldFailIfSupervisorNotProvider() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(502);  // not a provider
        Person provider = Context.getPersonService().getPerson(6);    // binome

        // attempt the unassignment
        providerManagementService.unassignProviderFromSupervisor(provider, supervisor);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void unassignProviderFromSupervisor_shouldFailIfProviderNotProvider() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person provider = Context.getPersonService().getPerson(502);    // not a provider

        // attempt the unassignment
        providerManagementService.unassignProviderFromSupervisor(provider, supervisor);
    }

    @Test(expected = ProviderNotAssignedToSupervisorException.class)
    public void unassignProviderFromSupervisor_shouldFailIfProviderNotAssignedToSupervisor() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person provider = Context.getPersonService().getPerson(6);    // binome

        // attempt the unassignment
        providerManagementService.unassignProviderFromSupervisor(provider, supervisor);
    }

    @Test
    public void unassignAllSupervisorsFromProvider_shouldUnassignAllSupervisorsFromProvider() throws Exception {
        // first, assign a couple supervisors to a provider
        Person provider = Context.getPersonService().getPerson(6);    // binome
        Person supervisor1 = Context.getPersonService().getPerson(8);  // binome supervisor
        Person supervisor2 = Context.getPersonService().getPerson(501);  // community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider, supervisor1);
        providerManagementService.assignProviderToSupervisor(provider, supervisor2);

        // sanity check
        List<Relationship> relationships = Context.getPersonService().getRelationships(null, provider, providerManagementService.getSupervisorRelationshipType(), DATE);
        Assert.assertEquals(2, relationships.size());
        
        // now do the unassignment
        providerManagementService.unassignAllSupervisorsFromProvider(provider);
        
        // the relationships should still on the current date, but should be ended on the current date
        relationships = Context.getPersonService().getRelationships(null, provider, providerManagementService.getSupervisorRelationshipType(), DATE);
        Assert.assertEquals(2, relationships.size());
        for (Relationship r : relationships) {
            Assert.assertEquals(DATE, r.getEndDate());
        }

        // and if we check in the future, the relationships should not be returned
        relationships = Context.getPersonService().getRelationships(null, provider, providerManagementService.getSupervisorRelationshipType(), FUTURE_DATE);
        Assert.assertEquals(0, relationships.size());
    }

    @Test(expected = APIException.class)
    public void unassignAllSupervisorsFromProvider_shouldFailIfProviderNull() throws Exception {
        providerManagementService.unassignAllSupervisorsFromProvider(null);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void unassignAllSupervisorsFromProvider_shouldFailIfProviderNotProvider() throws Exception {
        Person provider = Context.getPersonService().getPerson(502);
        providerManagementService.unassignAllSupervisorsFromProvider(provider);
    }
    
    @Test
    public void unassignAllProvidersFromSupervisor_shouldUnassignAllProvidersFromSupervisor() throws Exception {
        // first, assign a couple supervisors to a provider
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, supervisor);
        providerManagementService.assignProviderToSupervisor(provider2, supervisor);

        // sanity check
        List<Relationship> relationships = Context.getPersonService().getRelationships(supervisor, null, providerManagementService.getSupervisorRelationshipType(), DATE);
        Assert.assertEquals(2, relationships.size());

        // now do the unassignment
        providerManagementService.unassignAllProvidersFromSupervisor(supervisor);

        // the relationships should still on the current date, but should be ended on the current date
        relationships = Context.getPersonService().getRelationships(supervisor, null, providerManagementService.getSupervisorRelationshipType(), DATE);
        Assert.assertEquals(2, relationships.size());
        for (Relationship r : relationships) {
            Assert.assertEquals(DATE, r.getEndDate());
        }

        // and if we check in the future, the relationships should not be returned
        relationships = Context.getPersonService().getRelationships(supervisor, null, providerManagementService.getSupervisorRelationshipType(), FUTURE_DATE);
        Assert.assertEquals(0, relationships.size());
    }

    @Test(expected = APIException.class)
    public void unassignAllProvidersFromSupervisor_shouldFailIfSupervisorNull() throws Exception {
        providerManagementService.unassignAllProvidersFromSupervisor(null);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void unassignAllProvidersFromSupervisor_shouldFailIfSupervisorNotProvider() throws Exception {
        Person supervisor = Context.getPersonService().getPerson(502);
        providerManagementService.unassignAllProvidersFromSupervisor(supervisor);
    }

    @Test
    public void getSuperviseeRelationships_shouldGetAllSuperviseeRelationshipsForSupervisor() throws Exception {
        // first, assign a couple providers to a supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, supervisor);
        providerManagementService.assignProviderToSupervisor(provider2, supervisor);

        // verify that the getSuperviseeRelationship method returns these relationships
        List<Relationship> relationships = providerManagementService.getSuperviseeRelationshipsForSupervisor(supervisor, new Date());
        
        Assert.assertEquals(2, relationships.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Relationship> i = relationships.iterator();

        while (i.hasNext()) {
            Relationship r = i.next();

            if (r.getPersonB().getId() == 6 || r.getPersonB().getId() == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, relationships.size());
    }

    @Test
    public void getSuperviseeRelationships_shouldGetAllSuperviseeRelationshipsForSupervisorOnSpecifiedDate() throws Exception {
        // first, assign a couple providers to a supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, supervisor, DATE);
        providerManagementService.assignProviderToSupervisor(provider2, supervisor, FURTHER_PAST_DATE);  // assign this relationship the past and end it
        providerManagementService.unassignProviderFromSupervisor(provider2, supervisor, PAST_DATE);

        // verify that the getSupervisorRelationship method returns these relationships
        List<Relationship> relationships = providerManagementService.getSuperviseeRelationshipsForSupervisor(supervisor, DATE);  // only query for relationships on past date

        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(new Integer(6), relationships.get(0).getPersonB().getId());
    }

    @Test
    public void getSuperviseeRelationships_shouldGetAllHistoricalSuperviseeRelationshipsForSupervisor() throws Exception {
        // first, assign a couple providers to a supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, supervisor, DATE);
        providerManagementService.assignProviderToSupervisor(provider2, supervisor, FURTHER_PAST_DATE);  // assign this relationship the past and end it
        providerManagementService.unassignProviderFromSupervisor(provider2, supervisor, PAST_DATE);

        // verify that the getSupervisorRelationship method returns these relationships
        List<Relationship> relationships = providerManagementService.getSuperviseeRelationshipsForSupervisor(supervisor);  // query for all relationships across dates

        Assert.assertEquals(2, relationships.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Relationship> i = relationships.iterator();

        while (i.hasNext()) {
            Relationship r = i.next();

            if (r.getPersonB().getId() == 6 || r.getPersonB().getId() == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, relationships.size());
    }

    @Test(expected = APIException.class)
    public void getSuperviseeRelationships_shouldFailIfSupervisorNull() throws Exception {
        providerManagementService.getSuperviseeRelationshipsForSupervisor(null, DATE);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void getSuperviseeRelationships_shouldFailIfSuperviseeNotProvider() throws Exception {
        Person supervisee = Context.getPersonService().getPerson(502);    // not a provider
        providerManagementService.getSuperviseeRelationshipsForSupervisor(supervisee, DATE);
    }

    @Test
    public void getSupervisees_shouldGetAllSuperviseesForSupervisor() throws Exception {
        // first, assign a couple providers to a supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, supervisor);
        providerManagementService.assignProviderToSupervisor(provider2, supervisor);

        // verify that the getSuperviseesForSupervisor method returns these providers
        List<Person> supervisees = providerManagementService.getSuperviseesForSupervisor(supervisor, new Date());

        Assert.assertEquals(2, supervisees.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Person> i = supervisees.iterator();

        while (i.hasNext()) {
            Person p = i.next();

            if (p.getId() == 6 || p.getId() == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, supervisees.size());
    }

    @Test
    public void getSupervisees_shouldGetAllSuperviseesForSupervisorOnSpecifiedDate() throws Exception {
        // first, assign a couple providers to a supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, supervisor, DATE);
        providerManagementService.assignProviderToSupervisor(provider2, supervisor, FURTHER_PAST_DATE);  // assign this relationship the past and end it
        providerManagementService.unassignProviderFromSupervisor(provider2, supervisor, PAST_DATE);

        // verify that the getSupervisorRelationship method returns these relationships
        List<Person> providers = providerManagementService.getSuperviseesForSupervisor(supervisor, DATE);  // only query for relationships on past date

        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(6), providers.get(0).getId());
    }

    @Test
    public void getSupervisees_shouldGetAllHistoricalSuperviseesForSupervisor() throws Exception {
        // first, assign a couple providers to a supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person supervisor = Context.getPersonService().getPerson(8);  // binome supervisor

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, supervisor, DATE);
        providerManagementService.assignProviderToSupervisor(provider2, supervisor, FURTHER_PAST_DATE);  // assign this relationship the past and end it
        providerManagementService.unassignProviderFromSupervisor(provider2, supervisor, PAST_DATE);

        // verify that the getSupervisorRelationship method returns these relationships
        List<Person> supervisees = providerManagementService.getSuperviseesForSupervisor(supervisor); // query across all dates

        Assert.assertEquals(2, supervisees.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Person> i = supervisees.iterator();

        while (i.hasNext()) {
            Person p = i.next();

            if (p.getId() == 6 || p.getId() == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, supervisees.size());
    }

    @Test
    public void transferSupervisees_shouldTransferSupervisees() throws Exception {

        // first, assign a couple providers to a supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person oldSupervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person newSupervisor = Context.getPersonService().getPerson(501); // a community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, oldSupervisor);
        providerManagementService.assignProviderToSupervisor(provider2, oldSupervisor);

        // now transfer only one of the supervisees to the new supervisor
        List<Person> superviseesToTransfer = new ArrayList<Person>();
        superviseesToTransfer.add(provider1);
        providerManagementService.transferSupervisees(superviseesToTransfer,oldSupervisor, newSupervisor);

        // confirm that the old supervisor still is supervising a single supervisee
        List<Person> supervisees = providerManagementService.getSuperviseesForSupervisor(oldSupervisor, new Date());
        Assert.assertEquals(1, supervisees.size());
        Assert.assertEquals(new Integer(7), supervisees.get(0).getId());

        // confirm that the new supervisor is supervising the supervisee who was moved
        supervisees = providerManagementService.getSuperviseesForSupervisor(newSupervisor, new Date());
        Assert.assertEquals(1, supervisees.size());
        Assert.assertEquals(new Integer(6), supervisees.get(0).getId());
    }


    @Test
    public void transferAllSupervisees_shouldTransferAllSupervisees() throws Exception {

        // first, assign a couple providers to a supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person oldSupervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person newSupervisor = Context.getPersonService().getPerson(501); // a community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, oldSupervisor);
        providerManagementService.assignProviderToSupervisor(provider2, oldSupervisor);

        // now transfer the supervisees to the new supervisor
        providerManagementService.transferAllSupervisees(oldSupervisor, newSupervisor);

        // confirm that the old supervisor now doesn't have any supervisees
        Assert.assertEquals(0, providerManagementService.getSuperviseesForSupervisor(oldSupervisor, new Date()).size());

        // confirm that the new supervisor now has both the supervisees assigned to them
        List<Person> supervisees = providerManagementService.getSuperviseesForSupervisor(newSupervisor, new Date());

        Assert.assertEquals(2, supervisees.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Person> i = supervisees.iterator();

        while (i.hasNext()) {
            Person p = i.next();

            if (p.getId() == 6 || p.getId() == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, supervisees.size());

    }

    @Test
    public void transferAllSupervisees_shouldTransferAllSuperviseesOnSpecifiedDate() throws Exception {

        // first, assign a couple providers to a supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person oldSupervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person newSupervisor = Context.getPersonService().getPerson(501); // a community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, oldSupervisor, FURTHER_PAST_DATE);
        providerManagementService.assignProviderToSupervisor(provider2, oldSupervisor, DATE);

        // now transfer the supervisees to the new supervisor
        // note that since the second provider was not assigned to provider on the past date, should not be transfser
        providerManagementService.transferAllSupervisees(oldSupervisor, newSupervisor, PAST_DATE);

        Assert.assertEquals(1, providerManagementService.getSuperviseesForSupervisor(oldSupervisor, new Date()).size());
        Assert.assertEquals(1, providerManagementService.getSuperviseesForSupervisor(newSupervisor, new Date()).size());
        Assert.assertEquals(new Integer(7), providerManagementService.getSuperviseesForSupervisor(oldSupervisor, new Date()).get(0).getId());
        Assert.assertEquals(new Integer(6), providerManagementService.getSuperviseesForSupervisor(newSupervisor, new Date()).get(0).getId());

    }

    @Test(expected = DateCannotBeInFutureException.class)
    public void transferAllSupervisees_shouldFailIfTransferDateInFuture() throws Exception {

        // first, assign a couple providers to a supervisor
        Person provider1 = Context.getPersonService().getPerson(6);    // binome
        Person provider2 = Context.getPersonService().getPerson(7);    // binome
        Person oldSupervisor = Context.getPersonService().getPerson(8);  // binome supervisor
        Person newSupervisor = Context.getPersonService().getPerson(501); // a community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider1, oldSupervisor);
        providerManagementService.assignProviderToSupervisor(provider2, oldSupervisor);

        providerManagementService.transferAllSupervisees(oldSupervisor, newSupervisor, FUTURE_DATE);
    }


    @Test
    public void getSupervisorRelationships_shouldGetAllSupervisorRelationshipsForProvider() throws Exception {
        // first, assign a couple supervisors to a provider
        Person provider = Context.getPersonService().getPerson(6);    // binome
        Person supervisor1 = Context.getPersonService().getPerson(8);  // binome supervisor
        Person supervisor2 = Context.getPersonService().getPerson(501);  // community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider, supervisor1);
        providerManagementService.assignProviderToSupervisor(provider, supervisor2);

        // verify that the getSupervisorRelationship method returns these relationships
        List<Relationship> relationships = providerManagementService.getSupervisorRelationshipsForProvider(provider, new Date());

        Assert.assertEquals(2, relationships.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Relationship> i = relationships.iterator();

        while (i.hasNext()) {
            Relationship r = i.next();

            if (r.getPersonA().getId() == 8 || r.getPersonA().getId() == 501) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, relationships.size());
    }

       @Test
    public void getSupervisorRelationships_shouldGetAllSupervisorRelationshipsForProviderOnSpecifiedDate() throws Exception {
        // first, assign a couple supervisors to a provider
        Person provider = Context.getPersonService().getPerson(6);    // binome
        Person supervisor1 = Context.getPersonService().getPerson(8);  // binome supervisor
        Person supervisor2 = Context.getPersonService().getPerson(501);  // community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider, supervisor1, DATE);
        providerManagementService.assignProviderToSupervisor(provider, supervisor2, FURTHER_PAST_DATE);  // assign this relationship the past and end it
        providerManagementService.unassignProviderFromSupervisor(provider, supervisor2, PAST_DATE);

        // verify that the getSupervisorRelationship method returns these relationships
        List<Relationship> relationships = providerManagementService.getSupervisorRelationshipsForProvider(provider, DATE);  // only query for relationships on past date

        Assert.assertEquals(1, relationships.size());
        Assert.assertEquals(new Integer(8), relationships.get(0).getPersonA().getId());
    }

    @Test
    public void getSupervisorRelationships_shouldGetAllHistoricalSupervisorRelationshipsForProvider() throws Exception {
        // first, assign a couple supervisors to a provider
        Person provider = Context.getPersonService().getPerson(6);    // binome
        Person supervisor1 = Context.getPersonService().getPerson(8);  // binome supervisor
        Person supervisor2 = Context.getPersonService().getPerson(501);  // community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider, supervisor1, DATE);
        providerManagementService.assignProviderToSupervisor(provider, supervisor2, FURTHER_PAST_DATE);  // assign this relationship the past and end it
        providerManagementService.unassignProviderFromSupervisor(provider, supervisor2, PAST_DATE);

        // verify that the getSupervisorRelationship method returns these relationships
        List<Relationship> relationships = providerManagementService.getSupervisorRelationshipsForProvider(provider);  // query for relationships across all dates
        Assert.assertEquals(2, relationships.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Relationship> i = relationships.iterator();

        while (i.hasNext()) {
            Relationship r = i.next();

            if (r.getPersonA().getId() == 8 || r.getPersonA().getId() == 501) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, relationships.size());
   }


    @Test(expected = APIException.class)
    public void getSupervisorRelationships_shouldFailIfProviderNull() throws Exception {
        providerManagementService.getSupervisorRelationshipsForProvider(null, DATE);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void getSupervisorRelationships_shouldFailIfProviderNotProvider() throws Exception {
        Person provider = Context.getPersonService().getPerson(502);    // not a provider
        providerManagementService.getSupervisorRelationshipsForProvider(provider, DATE);
    }

    @Test
    public void getSupervisors_shouldGetAllSupervisorsForProvider() throws Exception {
        // first, assign a couple supervisors to a provider
        Person provider = Context.getPersonService().getPerson(6);    // binome
        Person supervisor1 = Context.getPersonService().getPerson(8);  // binome supervisor
        Person supervisor2 = Context.getPersonService().getPerson(501);  // community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider, supervisor1);
        providerManagementService.assignProviderToSupervisor(provider, supervisor2);

        // verify that the getSupervisorRelationship method returns these relationships
        List<Person> supervisors = providerManagementService.getSupervisorsForProvider(provider, new Date());

        Assert.assertEquals(2, supervisors.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Person> i = supervisors.iterator();

        while (i.hasNext()) {
            Person p = i.next();

            if (p.getId() == 8 || p.getId() == 501) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, supervisors.size());
    }

    @Test
    public void getSupervisors_shouldGetAllSupervisorsForProviderOnSpecifiedDate() throws Exception {
        // first, assign a couple supervisors to a provider
        Person provider = Context.getPersonService().getPerson(6);    // binome
        Person supervisor1 = Context.getPersonService().getPerson(8);  // binome supervisor
        Person supervisor2 = Context.getPersonService().getPerson(501);  // community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider, supervisor1, DATE);
        providerManagementService.assignProviderToSupervisor(provider, supervisor2, FURTHER_PAST_DATE);  // assign this relationship the past and end it
        providerManagementService.unassignProviderFromSupervisor(provider, supervisor2, PAST_DATE);

        // verify that the getSupervisorRelationship method returns these relationships
        List<Person> providers = providerManagementService.getSupervisorsForProvider(provider, DATE);  // only query for relationships on past date

        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(8), providers.get(0).getId());
    }

    @Test
    public void getSupervisors_shouldGetAllHistoricalSupervisorsForProvider() throws Exception {
        // first, assign a couple supervisors to a provider
        Person provider = Context.getPersonService().getPerson(6);    // binome
        Person supervisor1 = Context.getPersonService().getPerson(8);  // binome supervisor
        Person supervisor2 = Context.getPersonService().getPerson(501);  // community health nurse

        // do the assignments
        providerManagementService.assignProviderToSupervisor(provider, supervisor1, DATE);
        providerManagementService.assignProviderToSupervisor(provider, supervisor2, FURTHER_PAST_DATE);  // assign this relationship the past and end it
        providerManagementService.unassignProviderFromSupervisor(provider, supervisor2, PAST_DATE);

        // verify that the getSupervisorRelationship method returns all relationships if we don't specify a date relationships
        List<Person> supervisors = providerManagementService.getSupervisorsForProvider(provider);

        Assert.assertEquals(2, supervisors.size());

        // double-check to make sure the are the correct relationships
        // be iterating through and removing the two that SHOULD be there
        Iterator<Person> i = supervisors.iterator();

        while (i.hasNext()) {
            Person p = i.next();

            if (p.getId() == 8 || p.getId() == 501) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, supervisors.size());
    }

    @Test(expected = RuntimeException.class)
    public void getProviders_shouldThrowExceptionIfIncludeRetiredNotSpecified() {
        providerManagementService.getProvidersAsPersons(null, null, null, null);
    }

    @Test
    public void getProviders_shouldGetProvidersReferencedByName() throws Exception {
        List<Person> providers = providerManagementService.getProvidersAsPersons("jimmy", null, null, false);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(9), providers.get(0).getId());

        providers = providerManagementService.getProvidersAsPersons("anet oloo", null, null, false);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(8), providers.get(0).getId());
    }

    @Test
    public void getProviders_shouldGetProvidersReferencedByIdentifier() throws Exception {
        List<Person> providers = providerManagementService.getProvidersAsPersons(null, "2a5", null, false);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(2), providers.get(0).getId());

        // try a partial match
        providers = providerManagementService.getProvidersAsPersons(null, "2a", null, false);
        Assert.assertEquals(5, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing those that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person p = i.next();

            if (p.getId() == 1 || p.getId() == 2 || p.getId() == 6 || p.getId() == 7 || p.getId() == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProviders_shouldGetProvidersByRole() throws Exception {

        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));

        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, roles, false);
        Assert.assertEquals(3, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the those that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person p = i.next();

            if (p.getId() == 2 || p.getId() == 6 || p.getId() == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProviders_shouldGetProvidersByRoles() throws Exception {

        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));
        roles.add(providerManagementService.getProviderRole(1011));

        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, roles, false);
        Assert.assertEquals(4, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the those that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person p = i.next();

            if (p.getId() == 2 || p.getId() == 6 || p.getId() == 7 || p.getId() == 9) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProviders_shouldNotGetSameProviderTwice() throws Exception {

        // person 2 is associated with 2 providers, but result set should be unique
        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));
        roles.add(providerManagementService.getProviderRole(1005));

        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, roles, false);
        Assert.assertEquals(4, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the those that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person p = i.next();
            if (p.getId() == 2 || p.getId() == 6 || p.getId() == 7 || p.getId() == 501) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProviders_shouldNotIgnoreRetiredProviders() throws Exception {

        // retire a provider
        Provider provider = Context.getProviderService().getProvider(1005);
        Context.getProviderService().retireProvider(provider, "test");

        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));

        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, roles, true);
        Assert.assertEquals(3, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the those that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person p = i.next();

            if (p.getId() == 2 || p.getId() == 6 || p.getId() == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProviders_shouldIgnoreRetiredProviders() throws Exception {

        // retire a provider
        Provider provider = Context.getProviderService().getProvider(1005);
        Context.getProviderService().retireProvider(provider, "test");

        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));

        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, roles, false);
        Assert.assertEquals(2, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the those that SHOULD be there
        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person p = i.next();

            if (p.getId() == 2 || p.getId() == 6) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProvider_shouldSearchOnMultipleParameters() throws Exception {
        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));
        List<Person> providers = providerManagementService.getProvidersAsPersons("John Doe", "2a6", roles, false);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(6), providers.get(0).getId());
    }

    @Test
    public void getProviders_shouldOrderByName() throws Exception {

        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));

        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, roles, false);
        Assert.assertEquals(3, providers.size());
        Assert.assertEquals(new Integer(7), providers.get(0).getId());
        Assert.assertEquals(new Integer(2), providers.get(1).getId());
        Assert.assertEquals(new Integer(6), providers.get(2).getId());
    }


    @Test
    public void getProviders_shouldReturnNullOrEmptyListIfNoMatches() throws Exception {

        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));

        List<Person> providers = providerManagementService.getProvidersAsPersons("barack", null, roles, false);
        Assert.assertTrue(providers == null || providers.size() == 0);
    }

    @Test
    public void getProviders_shouldIgnoreVoidedPersons() throws Exception {

        // void person 9
        Person person = Context.getPersonService().getPerson(9);
        Context.getPersonService().voidPerson(person, "test");

        List<Person> providers = providerManagementService.getProvidersAsPersons("jimmy", null, null, false);
        Assert.assertTrue(providers == null || providers.size() == 0);
    }

    @Test
    public void getProviders_shouldGetPersonsByAddress() throws Exception {
        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1("wishard");

        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, personAddress, null, null, false);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(2), providers.get(0).getId());

        personAddress = new PersonAddress();
        personAddress.setCityVillage("kapi");
        providers = providerManagementService.getProvidersAsPersons(null, null, personAddress, null, null, false);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(7), providers.get(0).getId());
    }

    @Test
    public void getProviders_shouldGetPersonsByAddressWithTwoFields() throws Exception {
        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1("wishard");
        personAddress.setCityVillage("ind");

        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, personAddress, null, null, false);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(2), providers.get(0).getId());
    }

    @Test
    public void getProviders_shouldGetPersonsByAddressBothFieldsMustMatch() throws Exception {
        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1("wishard");
        personAddress.setCityVillage("boston");

        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, personAddress, null, null, false);
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProviders_shouldGetPersonsByAddressAndName() throws Exception {
        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1("wishard");
        personAddress.setCityVillage("ind");

        List<Person> providers = providerManagementService.getProvidersAsPersons("horatio", null, personAddress, null, null, false);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(2), providers.get(0).getId());
    }

    @Test
    public void getProviders_shouldIntersectAddressAndNameSearch() throws Exception {
        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1("wishard");
        personAddress.setCityVillage("ind");

        // search for a valid person name, but not the name of the person with the above address
        List<Person> providers = providerManagementService.getProvidersAsPersons("jimmy", null, personAddress, null, null, false);
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProviders_shouldGetByPersonAttribute() throws Exception {
        PersonAttributeType personAttributeType = Context.getPersonService().getPersonAttributeType(1001);
        PersonAttribute attribute = new PersonAttribute(personAttributeType,"test");

        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, null, attribute, null, false);

        Assert.assertEquals(2, providers.size());
        Assert.assertEquals(new Integer(8), providers.get(0).getId());
        Assert.assertEquals(new Integer(6), providers.get(1).getId());
    }

    @Test
    public void getProviders_shouldIntersectNameAndAttributeSearch() throws Exception {
        PersonAttributeType personAttributeType = Context.getPersonService().getPersonAttributeType(1001);
        PersonAttribute attribute = new PersonAttribute(personAttributeType,"test");

        // searches for a valid person name, but not the person with the above attribute
        List<Person> providers = providerManagementService.getProvidersAsPersons("jimmy", null, null, attribute, null, false);

        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProviders_shouldIgnoreBlankFields() throws Exception {

        // verify that if some of the parameters are empty strings/blank they are ignored (instead of requiring the field to be blank/empty

        PersonAddress emptyAddress = new PersonAddress();
        emptyAddress.setAddress1("");
        emptyAddress.setCityVillage("");

        List<ProviderRole> emptyList = new ArrayList<ProviderRole>();

        PersonAttribute emptyAttribute = new PersonAttribute();
        emptyAttribute.setValue("");

        List<Person> providers = providerManagementService.getProvidersAsPersons("jimmy", "", emptyAddress, emptyAttribute, emptyList, false);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(9), providers.get(0).getId());

        providers = providerManagementService.getProvidersAsPersons("", "2a6", emptyAddress, emptyAttribute, emptyList, false);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(6), providers.get(0).getId());
    }

    @Test
    public void getProvidersQuery_shouldReturnNullIfNoQuery() throws Exception {
        List<Person> providers = providerManagementService.getProvidersAsPersons(null, null, false);
        Assert.assertTrue(providers == null || providers.size() == 0);
    }

    @Test
    public void getProvidersQuery_shouldFetchByIdentifierAndNameAndOrderByName()  throws Exception {
        List<Person> providers = providerManagementService.getProvidersAsPersons("b", null, false);
        Assert.assertEquals(3, providers.size());
        Assert.assertEquals(new Integer(501), providers.get(0).getId());
        Assert.assertEquals(new Integer(2), providers.get(1).getId());
        Assert.assertEquals(new Integer(9), providers.get(2).getId());
    }
    
    @Test (expected = APIException.class)
    public void getPatientRelationshipForProvider_shouldThrowAPIExceptionIfNoProviderOrRelationshipSpecified() throws Exception {
    	List<Relationship> actual = providerManagementService.getPatientRelationshipsForProvider(null, null, new Date());
    	Assert.assertTrue(actual == null || actual.size() == 0);
    }
    
    @Test (expected = PersonIsNotProviderException.class)
    public void getPatientRelationshipsForProvider_shouldThrowPersonNotProviderException() throws Exception {
    	RelationshipType type = new RelationshipType(1004);
    	// This person is not a provider.
    	Person p = new Person(202);
    	Date date = new Date();
    	List<Relationship> actual = providerManagementService.getPatientRelationshipsForProvider(p, type, date);
    	Assert.assertTrue(actual == null || actual.size() == 0);
    }
    
   @Test (expected = InvalidRelationshipTypeException.class)
    public void getPatientRelationshipsForProvider_shouldThrowInvalidRelationshipTypeException() throws Exception {
        // Parent Child Relationship -- not a provider relationship
	   	RelationshipType type = new RelationshipType(1005);
    	Person provider = new Person(2);
    	Date date = new Date();
    	List<Relationship> actual = providerManagementService.getPatientRelationshipsForProvider(provider, type, date);
    	Assert.assertTrue(actual == null || actual.size() == 0);
   }
   
   @Test
   public void getPatientRelationshipsForProvider_shouldReturnAllRelationshipsIfRelationshipTypeNotSpecified() throws Exception {
	   Person provider = new Person(9);
	   Date date = new Date();
	   List<Relationship> actual = providerManagementService.getPatientRelationshipsForProvider(provider, null, date);
	   Assert.assertTrue(actual.size() == 3);
   }
   
   @Test
   public void getPatientRelationshipsForProvider_shouldReturnNoRelationship() throws Exception {
	   // This provider has no relationships defined 
	   Person provider = new Person(501);
	   Date date = new Date();
	   List<Relationship> actual = providerManagementService.getPatientRelationshipsForProvider(provider, null, date);
	   Assert.assertTrue(actual.size() == 0);
   }
   
   @Test 
   public void getPatientRelationshipsForProvider_shouldReturnSpecificRelationship() throws Exception {
	   Person provider = new Person(9);
	   Date date = new Date();
	   RelationshipType accompagnateur, binome;
	   accompagnateur = Context.getPersonService().getRelationshipType(1002);
	   binome  = Context.getPersonService().getRelationshipType(1001);
	   List<Relationship> accompagnateurRelationships = providerManagementService.getPatientRelationshipsForProvider(provider, accompagnateur, date);
	   List<Relationship> binomeRelationships = providerManagementService.getPatientRelationshipsForProvider(provider, binome, date);
	   Assert.assertTrue(accompagnateurRelationships.size() == 1);
	   Assert.assertTrue(binomeRelationships.size() == 2);
   }
}

