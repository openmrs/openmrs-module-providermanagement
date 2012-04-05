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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.awt.peer.ListPeer;
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
    public void getProviderSuggestionsForRelationshipType_shouldGetProviderSuggestionsForRelationshipType() {
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
    public void getProviderSuggestion_shouldReturnAllProvidersForRelationshipTypeIfNoRuleSpecified() throws Exception {
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1002);

        List<Person> providers = providerSuggestionService.suggestProvidersForPatient(patient, relationshipType);

        // there should be five providers
        Assert.assertEquals(5, providers.size());

        Iterator<Person> i = providers.iterator();

        while (i.hasNext()) {
            Person person = i.next();
            int id = person.getId();

            if (id == 2 || id == 6 || id == 7 || id == 8 || id == 9) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, providers.size());
    }

    @Test
    public void getProviderSuggestion_shouldReturnAllProvidersForRelationshipTypeBasedOnRule() throws Exception {
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

    // TODO: handle all the exception/null cases
    // TODO: handle a bad rule?


}
