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
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderManagementUtils;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.exception.PatientAlreadyAssignedToProviderException;
import org.openmrs.module.providermanagement.exception.PatientNotAssignedToProviderException;
import org.openmrs.module.providermanagement.exception.ProviderDoesNotSupportRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.ProviderNotAssociatedWithPersonException;
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
    public void setProviderProviderRole_shouldSetProviderProviderRole() {
        // change the provider role for the existing provider
        Provider provider = Context.getProviderService().getProvider(1006);
        ProviderRole role = providerManagementService.getProviderRole(1003);
        providerManagementService.setProviderRole(provider,role);

        // now make sure the role is correct
        ProviderRole role2 = ProviderManagementUtils.getProviderRole(provider);
        Assert.assertEquals(new Integer(1003), role2.getId());
    }

    @Test
    public void setProviderRole_shouldNullifyProviderRole()  {
        // change the provider role for the existing provider
        Provider provider = Context.getProviderService().getProvider(1006);
        providerManagementService.setProviderRole(provider,null);

        // now make sure the role is correct
        ProviderRole role = ProviderManagementUtils.getProviderRole(provider);
        Assert.assertNull(role);
    }

    @Test
    public void getProvidersByRole_shouldGetProvidersByRole() {
        ProviderRole role = providerManagementService.getProviderRole(1001);
        List<Provider> providers = providerManagementService.getProvidersByRole(role);

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

    // TODO: test to make sure retired providers are excluded once TRUNK-3170 is complete

    @Test(expected = APIException.class)
    public void getProvidersByRole_shouldFailIfCalledWithNull() {
        List<Provider> providers = providerManagementService.getProvidersByRole(null);
    }

    @Test
    public void getProvidersByRoles_shouldGetProvidersByRole() {
        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(providerManagementService.getProviderRole(1001));
        roles.add(providerManagementService.getProviderRole(1002));

        List<Provider> providers = providerManagementService.getProvidersByRoles(roles);

        // there should be four providers with the binome  or binome supervisor role
        Assert.assertEquals(4, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the three that SHOULD be there
        Iterator<Provider> i = providers.iterator();

        while (i.hasNext()) {
            Provider provider = i.next();
            int id = provider.getId();

            if (id == 1003 || id == 1004  || id == 1005 || id == 1006) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    // TODO: test to make sure retired providers are excluded once TRUNK-3170 is complete

    @Test(expected = APIException.class)
    public void getProvidersByRoles_shouldFailIfCalledWithNull() {
        List<Provider> providers = providerManagementService.getProvidersByRole(null);
    }

    @Test
    public void getProvidersByRelationshipType_shouldReturnProvidersThatSupportRelationshipType() {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1002);
        List<Provider> providers = providerManagementService.getProvidersByRelationshipType(relationshipType);

        // there should be four providers (the 3 binomes, the binome supervisor, and the accompagnateur) that support the accompagnateur relationship
        Assert.assertEquals(5, providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the three that SHOULD be there
        Iterator<Provider> i = providers.iterator();

        while (i.hasNext()) {
            Provider provider = i.next();
            int id = provider.getId();

            if (id == 1003 || id == 1004  || id == 1005 || id == 1006 || id == 1007) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProvidersByRelationshipType_shouldReturnEmptyListIfNoMatchingProvidersFound() {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);   // a relationship type from the standard test data
        List<Provider> providers = providerManagementService.getProvidersByRelationshipType(relationshipType);
        Assert.assertEquals(new Integer(0), (Integer) providers.size());

        // also try a relationship type that has a matching role, but no providers have that role
        relationshipType = Context.getPersonService().getRelationshipType(1003);
        providers = providerManagementService.getProvidersByRelationshipType(relationshipType);
        Assert.assertEquals(new Integer(0), (Integer) providers.size());
    }

    @Test(expected = APIException.class)
    public void getProvidersByRelationshipType_shouldFailIfCalledWithNull() {
        List<Provider> providers = providerManagementService.getProvidersByRelationshipType(null);
    }

    @Test
    public void getProvidersBySuperviseeRole_shouldReturnProvidersThatCanSuperviseProviderRole() {
        ProviderRole providerRole = providerManagementService.getProviderRole(1001);
        List<Provider> providers = providerManagementService.getProvidersBySuperviseeProviderRole(providerRole);
        Assert.assertEquals(new Integer(2), (Integer) providers.size());

        // double-check to make sure the are the correct providers
        // be iterating through and removing the three that SHOULD be there
        Iterator<Provider> i = providers.iterator();

        while (i.hasNext()) {
            Provider provider = i.next();
            int id = provider.getId();

            if (id == 1006 || id == 1008) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProvidersBySuperviseeProviderRole_shouldReturnEmptyListIfNoMatchingProvidersFound() {
        ProviderRole providerRole = providerManagementService.getProviderRole(1008);
        List<Provider> providers = providerManagementService.getProvidersBySuperviseeProviderRole(providerRole);
        Assert.assertEquals(new Integer(0), (Integer) providers.size());
    }

    @Test(expected = APIException.class)
    public void getProvidersBySuperviseeProividerRole_shouldFailIfCalledWithNull() {
        List<Provider> providers = providerManagementService.getProvidersBySuperviseeProviderRole(null);
    }

    @Test(expected = APIException.class)
    public void assignPatientToProvider_shouldFailIfPatientNull() throws Exception {
        Provider provider = Context.getProviderService().getProvider(1);
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
        Provider provider = Context.getProviderService().getProvider(1001);
        providerManagementService.assignPatientToProvider(patient, provider, null, null);
    }

    @Test(expected = APIException.class)
    public void assignPatientToProvider_shouldFailIfPatientVoided() throws Exception {
        Patient patient = Context.getPatientService().getPatient(999);  // voided patient from the standard test dataset
        Provider provider = Context.getProviderService().getProvider(1001);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, null);
    }

    @Test(expected = ProviderNotAssociatedWithPersonException.class)
    public void assignPatientToProvider_shouldFailIfProviderNotAssociatedWithPerson() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        Provider provider = Context.getProviderService().getProvider(1002); // provider with no underlying person
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, null);
    }

    @Test(expected = ProviderDoesNotSupportRelationshipTypeException.class)
    public void assignPatientToProvider_shouldFailIfProviderDoesNotSupportRelationshipType() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        Provider provider = Context.getProviderService().getProvider(1007);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, null);
    }

    @Test
    public void assignPatientToProvider_shouldAssignPatientToProvider() throws Exception {
        Patient patient = Context.getPatientService().getPatient(8);
        Provider provider = Context.getProviderService().getProvider(1004);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, null);

        // confirm that the relationship has been created with the appropriate start date
        List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(patient);
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());
        Relationship relationship = relationships.get(0);
        Assert.assertEquals(patient, relationship.getPersonB());
        Assert.assertEquals(provider.getPerson(), relationship.getPersonA());
        Assert.assertEquals(relationship.getStartDate(), ProviderManagementUtils.clearTimeComponent(new Date()));
    }

    @Test(expected = PatientAlreadyAssignedToProviderException.class)
    public void assignPatientToProvider_shouldFailIfRelationshipAlreadyExists() throws Exception {
        Patient patient = Context.getPatientService().getPatient(8);
        Provider provider = Context.getProviderService().getProvider(1004);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType);

       // if we try to do the same thing again, it should fail
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType);
    }
    
    @Test(expected = PatientNotAssignedToProviderException.class)
    public void unassignPatientFromProvider_shouldFailIfRelationshipDoesNotExist() throws Exception {
        Patient patient = Context.getPatientService().getPatient(8);
        Provider provider = Context.getProviderService().getProvider(1004);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.unassignPatientFromProvider(patient, provider, relationshipType, null);
    }

    @Test
    public void unassignPatientFromProvider_shouldUnassignPatientFromProvider() throws Exception {
        // first, assign a patient to a provider
        Patient patient = Context.getPatientService().getPatient(8);
        Provider provider = Context.getProviderService().getProvider(1004);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType, new Date(new Date().getTime() - 31536000000L));

        // now, end that relationship on today's date
        providerManagementService.unassignPatientFromProvider(patient, provider, relationshipType);

        // confirm that the relationship has been ended
        List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(patient);
        Assert.assertEquals(new Integer(1), (Integer) relationships.size());   // sanity check
        Relationship relationship = relationships.get(0);
        Assert.assertEquals(relationship.getEndDate(), ProviderManagementUtils.clearTimeComponent(new Date()));
    }
}
