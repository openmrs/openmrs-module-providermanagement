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

package org.openmrs.module.providermanagement.api;

import org.hibernate.PropertyValueException;
import org.hibernate.exception.GenericJDBCException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.exception.SuggestionEvaluationException;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestionType;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Iterator;
import java.util.List;

public class ProviderSuggestionServiceTest extends BaseModuleContextSensitiveTest {

    protected static final String XML_DATASET_PATH = "org/openmrs/module/providermanagement/include/";

    protected static final String XML_DATASET = "providerManagement-dataset.xml";

    protected static final String SUGGESTION_XML_DATASET = "providerSuggestion-dataset.xml";

    private ProviderManagementService providerManagementService;

    private ProviderSuggestionService providerSuggestionService;

    @Before
    public void init() throws Exception {
        // execute the provider management test dataset
        executeDataSet(XML_DATASET_PATH + XML_DATASET);
        executeDataSet(XML_DATASET_PATH + SUGGESTION_XML_DATASET);

        // initialize the services
        providerManagementService = Context.getService(ProviderManagementService.class);
        providerSuggestionService = Context.getService(ProviderSuggestionService.class);
    }

    @Test
    public void getProviderSuggestion_shouldGetProviderSuggestionById() {
        ProviderSuggestion suggestion = providerSuggestionService.getProviderSuggestion(1);
        Assert.assertEquals("Same given name suggestion", suggestion.getName());
    }

    @Test
    public void getProviderSuggestionByUuid_shouldGetProviderSuggestionByUuid() {
        ProviderSuggestion suggestion = providerSuggestionService.getProviderSuggestionByUuid("da7f623f-27ee-4bb2-86d6-6d1d05312bd5");
        Assert.assertEquals(new Integer(2), suggestion.getId());
    }

    @Test
    public void getProviderSuggestionsForRelationshipType_shouldGetProviderSuggestionsForRelationshipType() {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        List<ProviderSuggestion> suggestions = providerSuggestionService.getProviderSuggestionsByRelationshipType(relationshipType);

        // there should be three suggestions
        Assert.assertEquals(3, suggestions.size());

        Iterator<ProviderSuggestion> i = suggestions.iterator();

        while (i.hasNext()) {
            ProviderSuggestion providerSuggestion = i.next();
            int id = providerSuggestion.getId();

            if (id == 1 || id == 2 || id == 3) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, suggestions.size());
    }

    @Test
    public void getProviderSuggestionForRelationshipType_shouldReturnNullOrEmptyListIfNoSuggestions()  {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1002);
        List<ProviderSuggestion> suggestions = providerSuggestionService.getProviderSuggestionsByRelationshipType(relationshipType);
        Assert.assertTrue(suggestions == null || suggestions.size() == 0);
    }

    @Test
    public void getProviderSuggestionForRelationshipType_shouldReturnNullOrEmptyListIfInvalidRelationshipType()  {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        List<ProviderSuggestion> suggestions = providerSuggestionService.getProviderSuggestionsByRelationshipType(relationshipType);
        Assert.assertTrue(suggestions == null || suggestions.size() == 0);
    }

    @Test
    public void getAllProviderSuggestions_shouldGetAllProviderSuggestions() {
        List<ProviderSuggestion> suggestions = providerSuggestionService.getAllProviderSuggestions(true);

        Assert.assertEquals(3, suggestions.size());

        Iterator<ProviderSuggestion> i = suggestions.iterator();

        while (i.hasNext()) {
            ProviderSuggestion providerSuggestion = i.next();
            int id = providerSuggestion.getId();

            if (id == 1 || id == 2 || id == 3) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, suggestions.size());
    }

    @Test
    public void saveProviderSuggestion_shouldSaveProviderSuggestion() {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1002);
        ProviderSuggestion suggestion = new ProviderSuggestion();
        suggestion.setName("new suggestion");
        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        suggestion.setCriteria("-- some groovy code --");
        suggestion.setRelationshipType(relationshipType);

        providerSuggestionService.saveProviderSuggestion(suggestion);

        List<ProviderSuggestion> suggestions = providerSuggestionService.getProviderSuggestionsByRelationshipType(relationshipType);
        Assert.assertEquals(1, suggestions.size());
        Assert.assertEquals("new suggestion", suggestions.get(0).getName());
    }


    @Test
    public void retireProviderSuggestion_shouldRetireProviderSuggestion() {
        ProviderSuggestion suggestion = providerSuggestionService.getProviderSuggestion(1);
        providerSuggestionService.retireProviderSuggestion(suggestion, "test");

        // make sure only two suggestions are now returned
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        List<ProviderSuggestion> suggestions = providerSuggestionService.getProviderSuggestionsByRelationshipType(relationshipType);

        // there should be two suggestions
        Assert.assertEquals(2, suggestions.size());

        Iterator<ProviderSuggestion> i = suggestions.iterator();

        while (i.hasNext()) {
            ProviderSuggestion providerSuggestion = i.next();
            int id = providerSuggestion.getId();

            if (id == 2 || id == 3) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, suggestions.size());
    }

    @Test
    public void unretireProviderSuggestion_shouldUnRetireProviderSuggestion() {
        ProviderSuggestion suggestion = providerSuggestionService.getProviderSuggestion(1);
        providerSuggestionService.retireProviderSuggestion(suggestion, "test");
        providerSuggestionService.unretireProviderSuggestion(suggestion);

        // make sure only all three suggestions are still returned
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        List<ProviderSuggestion> suggestions = providerSuggestionService.getProviderSuggestionsByRelationshipType(relationshipType);

        // there should be two suggestions
        Assert.assertEquals(3, suggestions.size());

        Iterator<ProviderSuggestion> i = suggestions.iterator();

        while (i.hasNext()) {
            ProviderSuggestion providerSuggestion = i.next();
            int id = providerSuggestion.getId();

            if (id == 1 || id == 2 || id == 3) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, suggestions.size());
    }

    @Test
    public void purgeProviderSuggestion_shouldPurgeProviderSuggestion() {
        ProviderSuggestion suggestion = providerSuggestionService.getProviderSuggestion(1);
        providerSuggestionService.purgeProviderSuggestion(suggestion);

        // make sure only two suggestions are now returned
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        List<ProviderSuggestion> suggestions = providerSuggestionService.getProviderSuggestionsByRelationshipType(relationshipType);

        // there should be two suggestions
        Assert.assertEquals(2, suggestions.size());

        Iterator<ProviderSuggestion> i = suggestions.iterator();

        while (i.hasNext()) {
            ProviderSuggestion providerSuggestion = i.next();
            int id = providerSuggestion.getId();

            if (id == 2 || id == 3) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, suggestions.size());

        // trying to fetch suggestion 1 should now return null
        Assert.assertNull(providerSuggestionService.getProviderSuggestion(1));
    }

    // TODO: (PROV-12) create a "validateProviderSuggestion" method that attempts to parse and validate the groovy code in the criteria

    @Test
    public void suggestProvidersForPatient_shouldReturnNullfNoSuggestionSpecified() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1002);
        List<Person> providers = providerSuggestionService.suggestProvidersForPatient(patient, relationshipType);
        Assert.assertNull(providers);
    }

    @Test
    public void suggestProvidersForPatient_shouldReturnAllProvidersForRelationshipTypeBasedOnRule() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        List<Person> providers = providerSuggestionService.suggestProvidersForPatient(patient, relationshipType);

        // there should be two providers
        Assert.assertEquals(3, providers.size());

        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person person = i.next();
            int id = person.getId();

            if (id == 2 || id == 7 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void suggestProvidersForPatient_shouldIgnoreProvidersAlreadyAssignedToPatient() throws Exception {
        // this does the same thing as the above test, but assigned person 7 to the patient first
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);

        Person provider = Context.getPersonService().getPerson(7);
        providerManagementService.assignPatientToProvider(patient, provider, relationshipType);

        List<Person> providers = providerSuggestionService.suggestProvidersForPatient(patient, relationshipType);

        // there should be two providers
        Assert.assertEquals(2, providers.size());

        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person person = i.next();
            int id = person.getId();

            if (id == 2 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test(expected = APIException.class)
    public void suggestProvidersForPatient_shouldFailIfPatientNull() throws Exception {
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);
        providerSuggestionService.suggestProvidersForPatient(null, relationshipType);
    }

    @Test(expected = APIException.class)
    public void suggestProvidersForPatient_shouldFailIfRelationshipTypeNull() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        providerSuggestionService.suggestProvidersForPatient(patient, null);
    }

    @Test(expected = InvalidRelationshipTypeException.class)
    public void suggestProvidersForPatient_shouldFailIfRelationshipTypeInvalidNull() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1);
        providerSuggestionService.suggestProvidersForPatient(patient, relationshipType);
    }

    @Test(expected = SuggestionEvaluationException.class)
    public void suggestProvidersForPatient_shouldFailIfInvalidSuggestion() throws Exception {

        // add an invalid suggestion
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1002);
        ProviderSuggestion suggestion = new ProviderSuggestion();
        suggestion.setName("new suggestion");
        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        suggestion.setCriteria("invalid groovy code");
        suggestion.setRelationshipType(relationshipType);
        providerSuggestionService.saveProviderSuggestion(suggestion);

        Patient patient = Context.getPatientService().getPatient(2);
        providerSuggestionService.suggestProvidersForPatient(patient, relationshipType);
    }

    @Test
    public void getSupervisionSuggestion_shouldGetSupervisionSuggestionById() {
        SupervisionSuggestion suggestion = providerSuggestionService.getSupervisionSuggestion(1);
        Assert.assertEquals("Some supervisor suggestion", suggestion.getName());
    }

    @Test
    public void getSupervisionSuggestionByUuid_shouldGetSupervisionSuggestionByUuid() {
        SupervisionSuggestion suggestion = providerSuggestionService.getSupervisionSuggestionByUuid("da7f623f-27ee-4bb2-89d6-6d1d05315bd5");
        Assert.assertEquals(new Integer(1), suggestion.getId());
    }

    @Test
    public void getSupervisionSuggestionByProviderRole_shouldGetAllSuggestionsForProviderRole() {
        ProviderRole role = providerManagementService.getProviderRole(1001);
        List<SupervisionSuggestion> suggestions = providerSuggestionService.getSupervisionSuggestionsByProviderRole(role);

        Assert.assertEquals(2, suggestions.size());

        Iterator<SupervisionSuggestion> i = suggestions.iterator();

        while (i.hasNext()) {
            SupervisionSuggestion providerSuggestion = i.next();
            int id = providerSuggestion.getId();

            if (id == 1 || id == 3) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, suggestions.size());
    }

    @Test
    public void getSupervisionSuggestionByProviderRoleAndSuggestionType_shouldGetAllSuggestionsForProviderRoleAndSuggestionType() {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        List<SupervisionSuggestion> suggestions = providerSuggestionService.getSupervisionSuggestionsByProviderRoleAndSuggestionType(role, SupervisionSuggestionType.SUPERVISEE_SUGGESTION);

        Assert.assertEquals(2, suggestions.size());

       Iterator<SupervisionSuggestion> i = suggestions.iterator();

        while (i.hasNext()) {
            SupervisionSuggestion providerSuggestion = i.next();
            int id = providerSuggestion.getId();

            if (id == 2 || id == 4) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, suggestions.size());;
    }

    @Test
    public void getSupervisionSuggestionForProviderRole_shouldReturnNullOrEmptyListIfNoSuggestions()  {
        ProviderRole role = providerManagementService.getProviderRole(1003);
        List<SupervisionSuggestion> suggestions = providerSuggestionService.getSupervisionSuggestionsByProviderRole(role);
        Assert.assertTrue(suggestions == null || suggestions.size() == 0);
    }

    @Test
    public void getAllSupervisionSuggestions_shouldGetAllSupervisionSuggestions() {
        List<SupervisionSuggestion> suggestions = providerSuggestionService.getAllSupervisionSuggestions(true);

        Assert.assertEquals(4, suggestions.size());

        Iterator<SupervisionSuggestion> i = suggestions.iterator();

        while (i.hasNext()) {
            SupervisionSuggestion supervisionSuggestion = i.next();
            int id = supervisionSuggestion.getId();

            if (id == 1 || id == 2 || id == 3 || id == 4) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, suggestions.size());
    }


    @Test
    public void saveSupervisionSuggestion_shouldSaveSupervisionSuggestion() {
        ProviderRole role = providerManagementService.getProviderRole(1003);
        SupervisionSuggestion suggestion = new SupervisionSuggestion();
        suggestion.setName("new suggestion");
        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        suggestion.setCriteria("-- some groovy code --");
        suggestion.setProviderRole(role);
        suggestion.setSuggestionType(SupervisionSuggestionType.SUPERVISEE_SUGGESTION);

        providerSuggestionService.saveSupervisionSuggestion(suggestion);

        List<SupervisionSuggestion> suggestions = providerSuggestionService.getSupervisionSuggestionsByProviderRole(role);
        Assert.assertEquals(1, suggestions.size());
        Assert.assertEquals("new suggestion", suggestions.get(0).getName());
    }

    // TODO: this will need to be changed once/if we add a validator?
    @Test(expected = GenericJDBCException.class)
    public void saveSupervisionSuggestion_shouldFailIfNoTypeSpecified() {
        ProviderRole role = providerManagementService.getProviderRole(1002);
        SupervisionSuggestion suggestion = new SupervisionSuggestion();
        suggestion.setName("new suggestion");
        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        suggestion.setCriteria("-- some groovy code --");
        suggestion.setProviderRole(role);

        providerSuggestionService.saveSupervisionSuggestion(suggestion);
    }

    @Test
    public void retireSupervisionSuggestion_shouldRetireSupervisionSuggestion() {
        SupervisionSuggestion suggestion = providerSuggestionService.getSupervisionSuggestion(1);
        providerSuggestionService.retireSupervisionSuggestion(suggestion, "test");

        // make sure only the unretired suggestion is now returned
        ProviderRole role = providerManagementService.getProviderRole(1001) ;
        List<SupervisionSuggestion> suggestions = providerSuggestionService.getSupervisionSuggestionsByProviderRole(role);

        // there should be only one
        Assert.assertEquals(1, suggestions.size());
        Assert.assertEquals(new Integer(3), suggestions.get(0).getId());
    }

    @Test
    public void unretireSupervisionSuggestion_shouldUnretireSupervisionSuggestion() {
        SupervisionSuggestion suggestion = providerSuggestionService.getSupervisionSuggestion(1);
        providerSuggestionService.retireSupervisionSuggestion(suggestion, "test");
        providerSuggestionService.unretireSupervisionSuggestion(suggestion);

        ProviderRole role = providerManagementService.getProviderRole(1001) ;
        List<SupervisionSuggestion> suggestions = providerSuggestionService.getSupervisionSuggestionsByProviderRole(role);

        Assert.assertEquals(2, suggestions.size());

        Iterator<SupervisionSuggestion> i = suggestions.iterator();

        while (i.hasNext()) {
            SupervisionSuggestion providerSuggestion = i.next();
            int id = providerSuggestion.getId();

            if (id == 1 || id == 3) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, suggestions.size());
    }

    @Test
    public void purgeSupervisionSuggestion_shouldPurgeSupervisionSuggestion() {
        SupervisionSuggestion suggestion = providerSuggestionService.getSupervisionSuggestion(1);
        providerSuggestionService.purgeSupervisionSuggestion(suggestion);

        // make sure only the un-purged selection is now returned
        ProviderRole role = providerManagementService.getProviderRole(1001) ;
        List<SupervisionSuggestion> suggestions = providerSuggestionService.getSupervisionSuggestionsByProviderRole(role);

        Assert.assertEquals(1, suggestions.size());
        Assert.assertEquals(new Integer(3), suggestions.get(0).getId());

        // trying to fetch suggestion 1 should now return null
        Assert.assertNull(providerSuggestionService.getSupervisionSuggestion(1));
    }

    @Test
    public void suggestSupervisorsForProvider_shouldReturnNullIfNoRulesSpecified() throws Exception {
        Person provider = Context.getPersonService().getPerson(9);
        List<Person> providers = providerSuggestionService.suggestSupervisorsForProvider(provider);
        Assert.assertNull(providers);
    }


    @Test
    public void suggestSupervisorsForProvider_shouldReturnAllProvidersBasedOnRulesExcludingInvalid() throws Exception {
        Person provider = Context.getPersonService().getPerson(2);

        List<Person> providers = providerSuggestionService.suggestSupervisorsForProvider(provider);

        // there are two supervisor rules for binomes--one that suggests person 8, and the other that suggests person 9
        // however, since person 9 is an accompagetuer, person 8 (a binome supervisor) should be the only valid result

        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(8), providers.get(0).getId());
    }

    @Test
    public void suggestSupervisorsForProvider_shouldIgnoreProvidersAlreadyAssignedToSupervise() throws Exception {
        // this does the same thing as the above test, but assigned person 8 to supervise person 2 first
        Person provider = Context.getPersonService().getPerson(2);
        Person supervisor = Context.getPersonService().getPerson(8);
        providerManagementService.assignProviderToSupervisor(provider, supervisor);

        // now when we look for suggestions, there shouldn't be any (since the only valid supervisor for
        // this provider has already been assigned to this provider)
        List<Person> providers = providerSuggestionService.suggestSupervisorsForProvider(provider);
        Assert.assertEquals(0, providers.size());
    }

    @Test(expected = APIException.class)
    public void suggestSupervisorsForProvider_shouldFailIfProviderNull() throws Exception {
        providerSuggestionService.suggestSupervisorsForProvider(null);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void suggestSupervisorsForProvider_shouldFailIfPersonIsNotProvider() throws Exception {
        Person provider = Context.getPersonService().getPerson(502);
        providerSuggestionService.suggestSupervisorsForProvider(provider);
    }

    @Test(expected = SuggestionEvaluationException.class)
    public void suggestSupervisorsForProvider_shouldFailIfInvalidSuggestion() throws Exception {

        // add an invalid suggestion
        ProviderRole role = providerManagementService.getProviderRole(1001);
        SupervisionSuggestion suggestion = new SupervisionSuggestion();
        suggestion.setName("new suggestion");
        suggestion.setProviderRole(role);
        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        suggestion.setCriteria("invalid groovy code");
        suggestion.setSuggestionType(SupervisionSuggestionType.SUPERVISOR_SUGGESTION);
        providerSuggestionService.saveSupervisionSuggestion(suggestion);

        Person provider = Context.getPersonService().getPerson(2);
        providerSuggestionService.suggestSupervisorsForProvider(provider);
    }

    @Test
    public void suggestSuperviseesForProvider_shouldReturnNullIfNoRulesSpecified() throws Exception {
        Person provider = Context.getPersonService().getPerson(501);
        List<Person> providers = providerSuggestionService.suggestSuperviseesForProvider(provider);
        Assert.assertNull(providers);
    }


    @Test
    public void suggestSuperviseesForProvider_shouldReturnAllProvidersBasedOnRules() throws Exception {
        Person provider = Context.getPersonService().getPerson(8);

        List<Person> providers = providerSuggestionService.suggestSuperviseesForProvider(provider);

        Assert.assertEquals(2, providers.size());

        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person person = i.next();
            int id = person.getId();

            if (id == 2 || id == 6) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void suggestSuperviseeForProvider_shouldIgnoreProvidersAlreadyAssignedToSupervise() throws Exception {
        // this does the same thing as the above test, but assigned person 8 to supervise person 2 first
        Person provider = Context.getPersonService().getPerson(8);
        Person supervisee = Context.getPersonService().getPerson(2);
        providerManagementService.assignProviderToSupervisor(supervisee, provider);

        List<Person> providers = providerSuggestionService.suggestSuperviseesForProvider(provider);
        Assert.assertEquals(1, providers.size());
        Assert.assertEquals(new Integer(6), providers.get(0).getId());
    }

    @Test(expected = APIException.class)
    public void suggestSuperviseeForProvider_shouldFailIfProviderNull() throws Exception {
        providerSuggestionService.suggestSuperviseesForProvider(null);
    }

    @Test(expected = PersonIsNotProviderException.class)
    public void suggestSuperviseesForProvider_shouldFailIfPersonIsNotProvider() throws Exception {
        Person provider = Context.getPersonService().getPerson(502);
        providerSuggestionService.suggestSuperviseesForProvider(provider);
    }

    @Test(expected = SuggestionEvaluationException.class)
    public void suggestSuperviseesForProvider_shouldFailIfInvalidSuggestion() throws Exception {

        // add an invalid suggestion
        ProviderRole role = providerManagementService.getProviderRole(1002);
        SupervisionSuggestion suggestion = new SupervisionSuggestion();
        suggestion.setName("new suggestion");
        suggestion.setProviderRole(role);
        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        suggestion.setCriteria("invalid groovy code");
        suggestion.setSuggestionType(SupervisionSuggestionType.SUPERVISEE_SUGGESTION);
        providerSuggestionService.saveSupervisionSuggestion(suggestion);

        Person provider = Context.getPersonService().getPerson(8);
        providerSuggestionService.suggestSuperviseesForProvider(provider);
    }
}
