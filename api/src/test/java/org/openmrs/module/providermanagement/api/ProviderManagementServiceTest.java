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
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderManagementUtils;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.exception.*;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Tests for ProviderManagementService.
 */
public class  ProviderManagementServiceTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/providermanagement/include/";

    protected static final String XML_DATASET = "providerManagement-dataset.xml";

    private ProviderManagementService providerManagementService;

    public static final Date DATE = ProviderManagementUtils.clearTimeComponent(new Date());

    public static final Date PAST_DATE = ProviderManagementUtils.clearTimeComponent(new Date(DATE.getTime() - 31536000000L));

    public static final Date FURTHER_PAST_DATE = ProviderManagementUtils.clearTimeComponent(new Date(PAST_DATE.getTime() - 31536000000L));

    public static final Date FUTURE_DATE = ProviderManagementUtils.clearTimeComponent(new Date(DATE.getTime() + 31536000000L));

    // TODO: some tests involving persons with multiple providers?

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
        Assert.assertEquals(11, roleCount);

        roles = providerManagementService.getAllProviderRoles(false);
        roleCount = roles.size();
        Assert.assertEquals(11, roleCount);
    }

    @Test
    public void getAllProviderRoles_shouldGetAllProviderRolesIncludingRetired() {
        List<ProviderRole> roles = providerManagementService.getAllProviderRoles(true);
        int roleCount = roles.size();
        Assert.assertEquals(12, roleCount);
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
        Assert.assertEquals(new Integer(2), (Integer) role.getRelationshipTypes().size());
        Assert.assertEquals(new Integer(1), (Integer) role.getSuperviseeProviderRoles().size());
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
        Assert.assertEquals(new Integer(5), (Integer) providerRoles.size());
        
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
        Assert.assertEquals(new Integer(0), (Integer) providerRoles.size());
    }

    @Test(expected = APIException.class)
    public void getProviderRolesByRelationshipType_shouldThrowExceptionIfRelationshipTypeNull() {
        providerManagementService.getProviderRolesByRelationshipType(null);
    }

    @Test
    public void getProviderRolesBySuperviseeProviderRole_shouldGetAllProviderRolesThatCanSuperviseeProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1001);
        List<ProviderRole> providerRoles = providerManagementService.getProviderRolesBySuperviseeProviderRole(role);
        Assert.assertEquals(new Integer(5), (Integer) providerRoles.size());

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
        Assert.assertEquals(new Integer(0), (Integer) providerRoles.size());
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
        Assert.assertEquals(12, providerManagementService.getAllProviderRoles().size());
    }

    @Test
    public void deleteProviderRole_shouldDeleteProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.purgeProviderRole(role);
        Assert.assertEquals(10, providerManagementService.getAllProviderRoles().size());
        Assert.assertNull(providerManagementService.getProviderRole(1002));
    }

    // TODO: remove the ignore from these two tests once the retiring of child collections issue is figured out (TRUNK-3174)

    @Ignore
    @Test
    public void retireProviderRole_shouldRetireProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.retireProviderRole(role, "test");
        Assert.assertEquals(9, providerManagementService.getAllProviderRoles().size());
        
        role = providerManagementService.getProviderRole(1002);
        Assert.assertTrue(role.isRetired());
        Assert.assertEquals("test", role.getRetireReason());
        
    }

    @Ignore
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
    public void getAllProviderRoleRelationshipTypes_shouldGetAllProviderRelationshipTypes() {
        List<RelationshipType> relationshipTypes = providerManagementService.getAllProviderRoleRelationshipTypes(true);
        Assert.assertEquals(new Integer(3), (Integer) relationshipTypes.size());

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
        List<RelationshipType> relationshipTypes = providerManagementService.getAllProviderRoleRelationshipTypes();
        Assert.assertEquals(new Integer(2), (Integer) relationshipTypes.size());

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
    public void assignProviderRoleToProvider_shouldAssignProviderRole() {
        // add a new role to the existing provider
        Person provider = Context.getProviderService().getProvider(1006).getPerson();
        ProviderRole role = providerManagementService.getProviderRole(1003);
        providerManagementService.assignProviderRoleToProvider(provider, role, "123");

        // the provider should now have two roles
        List<ProviderRole> providerRoles = ProviderManagementUtils.getProviderRoles(provider);
        Assert.assertEquals(new Integer(2), (Integer) providerRoles.size());

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
    public void assignProviderRoleToProvider_shouldNotFailIfProviderAlreadyHasRole() {
        // add a role that the provider already has
        Person provider = Context.getProviderService().getProvider(1006).getPerson();
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.assignProviderRoleToProvider(provider, role, "123");

        // the provider should still only have one role
        List<ProviderRole> providerRoles = ProviderManagementUtils.getProviderRoles(provider);
        Assert.assertEquals(new Integer(1), (Integer) providerRoles.size());
        Assert.assertEquals(new Integer(1002), providerRoles.get(0).getId());
    }

    @Test(expected = APIException.class)
    public void assignProviderRoleToProvider_shouldFailIfUnderlyingPersonVoided() {
        Person provider = Context.getProviderService().getProvider(1006).getPerson();
        ProviderRole role = providerManagementService.getProviderRole(1002);
        
        // void this person, then attempt to add a role to it
        Context.getPersonService().voidPerson(provider, "test");
        
        providerManagementService.assignProviderRoleToProvider(provider,role, "123");
    }

    @Test
    public void unassignProviderRoleFromProvider_shouldUnassignRoleFromProvider() {
        Person provider = Context.getProviderService().getProvider(1006).getPerson();
        ProviderRole role = providerManagementService.getProviderRole(1002);
        providerManagementService.unassignProviderRoleFromProvider(provider, role);
        
        Assert.assertEquals(new Integer(0), (Integer) ProviderManagementUtils.getProviderRoles(provider).size());
    }

    @Test
    public void unassignProviderRoleFromProvider_shouldLeaveOtherRoleUntouched() {
        // get the provider with two roles
        Person provider = Context.getPersonService().getPerson(2);
        
        // unassign one of these roles
        providerManagementService.unassignProviderRoleFromProvider(provider, providerManagementService.getProviderRole(1001));
        
        // verify that only the other role remains
        List<ProviderRole> roles = ProviderManagementUtils.getProviderRoles(provider);
        Assert.assertEquals(new Integer(1), (Integer) roles.size());
        Assert.assertEquals(new Integer(1005), (Integer) roles.get(0).getId());
    }

    @Test
    public void unassignProviderRoleFromProvider_shouldNotFailIfProviderDoesNotHaveRole() {
        // get a binome
        Person provider = Context.getPersonService().getPerson(6);

        // unassign some other role
        providerManagementService.unassignProviderRoleFromProvider(provider, providerManagementService.getProviderRole(1002));

        // verify that the binome role still remains
        List<ProviderRole> roles = ProviderManagementUtils.getProviderRoles(provider);
        Assert.assertEquals(new Integer(1), (Integer) roles.size());
        Assert.assertEquals(new Integer(1001), (Integer) roles.get(0).getId());
    }

   @Test
   public void unassignProviderRoleFromProvider_shouldNotFailIfProviderHasNoRoles() {
       // get the provider with no roles
       Person provider = Context.getPersonService().getPerson(1);

       // unassign some role that this person does not have
       providerManagementService.unassignProviderRoleFromProvider(provider, providerManagementService.getProviderRole(1002));

       List<ProviderRole> roles = ProviderManagementUtils.getProviderRoles(provider);
       Assert.assertEquals(new Integer(0), (Integer) roles.size());
    }

    @Test
    public void unassignProviderRoleFromProvider_shouldNotFailIfPersonIsNotProvider() {
        // get the provider with no roles
        Person provider = Context.getPersonService().getPerson(502);

        // unassign some role
        providerManagementService.unassignProviderRoleFromProvider(provider, providerManagementService.getProviderRole(1002));
        Assert.assertTrue(!ProviderManagementUtils.isProvider(provider));
    }

    @Test
    public void getProvidersByRole_shouldGetProvidersByRole() {
        ProviderRole role = providerManagementService.getProviderRole(1001);
        List<Person> providers = providerManagementService.getProvidersByRole(role);

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

    // TODO: test to make sure retired providers are excluded once TRUNK-3170 is complete

    @Test(expected = APIException.class)
    public void getProvidersByRole_shouldFailIfCalledWithNull() {
        List<Person> providers = providerManagementService.getProvidersByRole(null);
    }

    @Test
    public void getProvidersByRoles_shouldGetProvidersByRole() {
        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));
        roles.add(providerManagementService.getProviderRole(1002));

        List<Person> providers = providerManagementService.getProvidersByRoles(roles);

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

    // TODO: test to make sure retired providers are excluded once TRUNK-3170 is complete

    @Test(expected = APIException.class)
    public void getProvidersByRoles_shouldFailIfCalledWithNull() {
        List<Person> providers = providerManagementService.getProvidersByRole(null);
    }

    @Test
    public void getProvidersByRelationshipType_shouldReturnProvidersThatSupportRelationshipType() {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1002);
        List<Person> providers = providerManagementService.getProvidersByRelationshipType(relationshipType);

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
        List<Person> providers = providerManagementService.getProvidersByRelationshipType(relationshipType);
        Assert.assertEquals(new Integer(0), (Integer) providers.size());

        // also try a relationship type that has a matching role, but no providers have that role
        relationshipType = Context.getPersonService().getRelationshipType(1003);
        providers = providerManagementService.getProvidersByRelationshipType(relationshipType);
        Assert.assertEquals(new Integer(0), (Integer) providers.size());
    }

    @Test(expected = APIException.class)
    public void getProvidersByRelationshipType_shouldFailIfCalledWithNull() {
        List<Person> providers = providerManagementService.getProvidersByRelationshipType(null);
    }

    @Test
    public void getProvidersBySuperviseeRole_shouldReturnProvidersThatCanSuperviseProviderRole() {
        ProviderRole providerRole = providerManagementService.getProviderRole(1001);
        List<Person> providers = providerManagementService.getProvidersBySuperviseeProviderRole(providerRole);
        Assert.assertEquals(new Integer(3), (Integer) providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the three that SHOULD be there
        Iterator<Person> i = providers.iterator();
        
        while (i.hasNext()) {
            Person provider = i.next();

            int id = provider.getId();

            if (id == 2 || id == 8 || id == 501) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProvidersBySuperviseeProviderRole_shouldReturnEmptyListIfNoMatchingProvidersFound() {
        ProviderRole providerRole = providerManagementService.getProviderRole(1008);
        List<Person> providers = providerManagementService.getProvidersBySuperviseeProviderRole(providerRole);
        Assert.assertEquals(new Integer(0), (Integer) providers.size());
    }

    @Test(expected = APIException.class)
    public void getProvidersBySuperviseeProviderRole_shouldFailIfCalledWithNull() {
        List<Person> providers = providerManagementService.getProvidersBySuperviseeProviderRole(null);
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

        // confirm that the relationship has been created with the appropriate start date
        List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(patient);
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());
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
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());   // sanity check
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
        Assert.assertEquals(new Integer(2), (Integer) Context.getPersonService().getRelationships(provider, null, relationshipType).size());

        // now end all the relationships
        providerManagementService.unassignAllPatientsFromProvider(provider, relationshipType);

        // there still should be 2 relationships, but they both should have a end date of the current date
        List<Relationship> relationships = Context.getPersonService().getRelationships(provider, null, relationshipType);
        Assert.assertEquals(new Integer(2), (Integer) relationships.size());
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
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());
        Assert.assertEquals(ProviderManagementUtils.clearTimeComponent(DATE), relationships.get(0).getEndDate());

        relationships = Context.getPersonService().getRelationships(provider, null, accompagneteur);
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());
        Assert.assertEquals(ProviderManagementUtils.clearTimeComponent(DATE), relationships.get(0).getEndDate());
    }

    @Test
    public void unassignAllPatientsFromProvider_shouldNotFailIfProviderHasNoPatients() throws Exception {
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        // just confirm that this doesn't throw an exception
        providerManagementService.unassignAllPatientsFromProvider(provider);
    }

    // TODO: should there be other unit test for unassigning patients--think about null cases, and also when happens when voiding/etc
    
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
        List<Patient> patients = providerManagementService.getPatients(provider, relationshipType);
        Assert.assertEquals(new Integer(2), (Integer) patients.size());

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
        List<Patient> patients = providerManagementService.getPatients(provider, relationshipType, PAST_DATE);
        Assert.assertEquals(new Integer(1), (Integer) patients.size());
        Assert.assertEquals(new Integer(2), patients.get(0).getId());

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
        List<Patient> patients = providerManagementService.getPatients(provider, relationshipType, DATE);
        Assert.assertEquals(new Integer(1), (Integer) patients.size());
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
        List<Patient> patients = providerManagementService.getPatients(provider, relationshipType, DATE);
        Assert.assertEquals(new Integer(1), (Integer) patients.size());
        Assert.assertEquals(new Integer(8), patients.get(0).getId());
    }

    @Test(expected = InvalidRelationshipTypeException.class)
    public void getPatients_shouldFailIfRelationshipTypeIsNotProviderToPatientType() throws Exception {
        Person provider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerManagementService.getPatients(provider, relationshipType);
    }

    @Test(expected = APIException.class)
    public void getPatients_shouldFailIfProviderNull() throws Exception {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.getPatients(null, relationshipType);
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
        providerManagementService.getPatients(provider, relationshipType).size();
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
        List<Patient> patients = providerManagementService.getPatients(provider, null);
        Assert.assertEquals(new Integer(2), (Integer) patients.size());

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
        List<Patient> patients = providerManagementService.getPatients(provider, null, PAST_DATE);

        // confirm that only the patient from the past is present
        Assert.assertEquals(new Integer(1), (Integer) patients.size());
        Assert.assertEquals(new Integer(2), patients.get(0).getId());
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
        
        // now confirm that these two relationships are returned when we call getProviderRelationships
        List<Relationship> relationships = providerManagementService.getProviderRelationships(patient, null, null);

        // there should be two relationships
        Assert.assertEquals(new Integer(2), (Integer) relationships.size());

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
        List<Relationship> relationships = providerManagementService.getProviderRelationships(patient, provider1, null);

        // there should be one relationship
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());
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
        List<Relationship> relationships = providerManagementService.getProviderRelationships(patient, null, acc);

        // there should be one relationship
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());
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
        List<Relationship> relationships = providerManagementService.getProviderRelationships(patient, provider2, acc);

        // there should be one relationship
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());
        Assert.assertEquals(new Integer(8), relationships.get(0).getPersonA().getId());
        Assert.assertEquals(new Integer(1002), relationships.get(0).getRelationshipType().getId());
    }

    @Test
    public void getProviderRelationships_shouldReturnEmptyListIfPatientHasNoProviderRelationships() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        List<Relationship> relationships = providerManagementService.getProviderRelationships(patient, null, null);
        Assert.assertEquals(new Integer(0), (Integer) relationships.size());
    }

    @Test(expected = APIException.class)
    public void getProviderRelationships_shouldFailIfPatientNull() throws Exception {
        List<Relationship> relationships = providerManagementService.getProviderRelationships(null, null, null);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void getProviderRelationships_shouldFailIfPersonIsNotProvider() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        Person person = Context.getPersonService().getPerson(502);
        List<Relationship> relationships = providerManagementService.getProviderRelationships(patient, person, null);
    }

    @Test(expected = InvalidRelationshipTypeException.class)
    public void getProviderRelationships_shouldFailIfRelationshipIsNotProviderRelationship() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        List<Relationship> relationships = providerManagementService.getProviderRelationships(patient, null, relationshipType);
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

        // set up a non-provider relationship (so we can test that getProviderRelationships ignore it)
        Relationship relationship = new Relationship();
        relationship.setPersonA(provider1);
        relationship.setPersonB(patient);
        relationship.setRelationshipType(Context.getPersonService().getRelationshipType(1));
        
        // now confirm that these two relationships are returned when we call getProviderRelationships
        List<Relationship> relationships = providerManagementService.getProviderRelationships(patient, null, null);

        // there should be two relationships
        Assert.assertEquals(new Integer(2), (Integer) relationships.size());

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
        List<Relationship> relationships = providerManagementService.getProviderRelationships(patient, null, null, PAST_DATE);

        // there should be one relationship
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());
        Assert.assertEquals(new Integer(6), relationships.get(0).getPersonA().getId());
        Assert.assertEquals(new Integer(1001), relationships.get(0).getRelationshipType().getId());
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
        List<Relationship> relationships = providerManagementService.getProviderRelationships(patient, null, binome);
        Context.getPersonService().voidRelationship(relationships.get(0), "test");
        
        // now fetch all relationships
        relationships = providerManagementService.getProviderRelationships(patient, null, null);

        // there should be only one relationship returned
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());
        Assert.assertEquals(new Integer(8), relationships.get(0).getPersonA().getId());
        Assert.assertEquals(new Integer(1002), relationships.get(0).getRelationshipType().getId());
    }


    @Test
    public void getProviders_shouldReturnAllProvidersForPatient() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now confirm that these two providers are returned when we call getProviderRelationships
        List<Person> providers = providerManagementService.getProviders(patient, null);

        // there should be two providers
        Assert.assertEquals(new Integer(2), (Integer) providers.size());

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
    public void getProviders_shouldReturnProviderWithSpecifiedRelationshipType() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now fetch only providers with a specific relationship type
        List<Person> providers = providerManagementService.getProviders(patient, acc);

        // there should be one provider
        Assert.assertEquals(new Integer(1), (Integer) providers.size());
        Assert.assertEquals(new Integer(8), providers.get(0).getId());
    }

    @Test
    public void getProviders_shouldReturnEmptyListIfPatientHasNoProviderRelationships() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        List<Person> providers = providerManagementService.getProviders(patient, null);
        Assert.assertEquals(new Integer(0), (Integer) providers.size());
    }

    @Test(expected = APIException.class)
    public void getProviders_shouldFailIfPatientNull() throws Exception {
        providerManagementService.getProviders(null, null);
    }

    @Test(expected = InvalidRelationshipTypeException.class)
    public void getProviders_shouldFailIfRelationshipIsNotProviderRelationship() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerManagementService.getProviders(patient, relationshipType);
    }

    @Test
    public void getProviders_shouldReturnProvidersOnSpecifiedDate() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient--but one in the past, and one in the present
        providerManagementService.assignPatientToProvider(patient, provider1, binome, PAST_DATE);
        providerManagementService.assignPatientToProvider(patient, provider2, acc, DATE);

        // now get relationships in the past
        List<Person> providers = providerManagementService.getProviders(patient, null, PAST_DATE);

        // there should be one relationship
        Assert.assertEquals(new Integer(1), (Integer) providers.size());
        Assert.assertEquals(new Integer(6), providers.get(0).getId());
    }

    @Test
    public void getProviders_shouldIgnoreRetiredProviders() throws Exception {

        Patient patient = Context.getPatientService().getPatient(2);
        Person provider1 = Context.getPersonService().getPerson(6);
        Person provider2 = Context.getPersonService().getPerson(8);

        RelationshipType binome = Context.getPersonService().getRelationshipType(1001);
        RelationshipType acc = Context.getPersonService().getRelationshipType(1002);

        // assign these two providers to this patient
        providerManagementService.assignPatientToProvider(patient, provider1, binome);
        providerManagementService.assignPatientToProvider(patient, provider2, acc);

        // now fetch only providers with a specific relationship type
        List<Person> providers = providerManagementService.getProviders(patient, acc);

        // there should be one provider
        Assert.assertEquals(new Integer(1), (Integer) providers.size());
        Assert.assertEquals(new Integer(8), providers.get(0).getId());
    }

    @Test
    public void getProviders_shouldNotIgnoreRetiredProviders() throws Exception {

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

        // now confirm that these two providers are returned when we call getProviderRelationships
        List<Person> providers = providerManagementService.getProviders(patient, null);

        // there should be two providers
        Assert.assertEquals(new Integer(2), (Integer) providers.size());

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

    // TODO: transferAllPatients should not fail if no patients?
    // TODO: get patients should not fial if no patients?
   
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
        List<Patient> oldProviderPatients = providerManagementService.getPatients(oldProvider, null);
        List<Patient> newProviderPatients = providerManagementService.getPatients(newProvider, null);

        // on the current date, both patients should be assigned to both patients
        Assert.assertEquals(new Integer(2), (Integer) oldProviderPatients.size());
        Assert.assertEquals(new Integer(2), (Integer) newProviderPatients.size());

        // but, on some future date, the patients should no longer be associated with the old provider
        oldProviderPatients = providerManagementService.getPatients(oldProvider, null, FUTURE_DATE);
        newProviderPatients = providerManagementService.getPatients(newProvider, null, FUTURE_DATE);
        Assert.assertEquals(new Integer(0), (Integer) oldProviderPatients.size());
        Assert.assertEquals(new Integer(2), (Integer) newProviderPatients.size());

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
        List<Patient> oldProviderPatients = providerManagementService.getPatients(oldProvider, null, FUTURE_DATE);
        List<Patient> newProviderPatients = providerManagementService.getPatients(newProvider, null, FUTURE_DATE);
        Assert.assertEquals(new Integer(1), (Integer) oldProviderPatients.size());
        Assert.assertEquals(new Integer(2), oldProviderPatients.get(0).getId());
        Assert.assertEquals(new Integer(1), (Integer) newProviderPatients.size());
        Assert.assertEquals(new Integer(8), newProviderPatients.get(0).getId());
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

    @Test(expected = APIException.class)
    public void transferAllPatients_shouldFailIfRelationshipTypeNull() throws Exception {
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        Person newProvider = Context.getProviderService().getProvider(1005).getPerson();
        providerManagementService.transferAllPatients(oldProvider, newProvider, null);
    }

    @Test(expected = SourceProviderSameAsDestinationProviderException.class)
    public void transferAllPatients_shouldFailIfSourceProviderEqualsDestinationProvider() throws Exception {
        Person oldProvider = Context.getProviderService().getProvider(1004).getPerson();
        Person newProvider = Context.getProviderService().getProvider(1004).getPerson();
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.transferAllPatients(oldProvider, newProvider, relationshipType);
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
        List<Patient> oldProviderPatients = providerManagementService.getPatients(oldProvider, null);
        List<Patient> newProviderPatients = providerManagementService.getPatients(newProvider, null);
        Assert.assertEquals(new Integer(2), (Integer) oldProviderPatients.size());
        // the second patient should already be assigned to the destination provider
        Assert.assertEquals(new Integer(1), (Integer) newProviderPatients.size());
        Assert.assertEquals(new Integer(8), newProviderPatients.get(0).getId());

        // now do the transfer; everything should work, although the patient has already been assigned to new provider
        providerManagementService.transferAllPatients(oldProvider, newProvider, relationshipType);

        oldProviderPatients = providerManagementService.getPatients(oldProvider, null, FUTURE_DATE);
        newProviderPatients = providerManagementService.getPatients(newProvider, null, FUTURE_DATE);
        Assert.assertEquals(new Integer(1), (Integer) oldProviderPatients.size());
        Assert.assertEquals(new Integer(2), oldProviderPatients.get(0).getId());
        Assert.assertEquals(new Integer(1), (Integer) newProviderPatients.size());
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
        List<Patient> oldProviderPatients = providerManagementService.getPatients(oldProvider, null, FUTURE_DATE);
        List<Patient> newProviderPatients = providerManagementService.getPatients(newProvider, null, FUTURE_DATE);
        Assert.assertEquals(new Integer(0), (Integer) oldProviderPatients.size());
        Assert.assertEquals(new Integer(1), (Integer) newProviderPatients.size());
        Assert.assertEquals(new Integer(2), newProviderPatients.get(0).getId());

        // double check that we can fetch the patient from the destination provider via either relationship type
        relationshipType = Context.getPersonService().getRelationshipType(1001);
        newProviderPatients = providerManagementService.getPatients(newProvider, relationshipType, FUTURE_DATE);
        Assert.assertEquals(new Integer(1), (Integer) newProviderPatients.size());
        Assert.assertEquals(new Integer(2), newProviderPatients.get(0).getId());

        relationshipType = Context.getPersonService().getRelationshipType(1002);
        newProviderPatients = providerManagementService.getPatients(newProvider, relationshipType, FUTURE_DATE);
        Assert.assertEquals(new Integer(1), (Integer) newProviderPatients.size());
        Assert.assertEquals(new Integer(2), newProviderPatients.get(0).getId());
    }
}

