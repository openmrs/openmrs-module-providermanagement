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

package org.openmrs.module.providermanagement.suggestion;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Collection;
import java.util.Iterator;

public class SuggestionTest extends BaseModuleContextSensitiveTest {

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
    public void shouldEvaluateBasicGroovyRule() {

        Suggestion suggestion = new SupervisionSuggestion();

        suggestion.setCriteria("Set<org.openmrs.Person> persons = new HashSet<org.openmrs.Person>();" +
                            "persons.add(personService.getPerson(2));" +
                            "persons.add(personService.getPerson(6));" +
                            "return persons;");

        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        SuggestionEvaluator evaluator = suggestion.instantiateEvaluator();
        Collection<Person> persons = evaluator.evaluate(suggestion, null);

        Assert.assertEquals(2, persons.size());

        // double check that the patients assigned to the new provider are correct
        Iterator<Person> i = persons.iterator();

        while (i.hasNext()) {
            Person p = i.next();
            int id = p.getId();

            if (id == 2 || id == 6) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, persons.size());
    }

    @Test
    public void shouldEvaluateBasicGroovyRuleUsingPassedProvider() {

        Suggestion suggestion = new SupervisionSuggestion();

        suggestion.setCriteria("org.openmrs.module.providermanagement.ProviderRole role = providerManagementService.getProviderRoles(provider)[0];" +
                "Set<org.openmrs.Person> persons = new HashSet<org.openmrs.Person>();" +
                "persons.addAll(providerManagementService.getProvidersAsPersonsByRole(role));" +
                "return persons;");

        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        SuggestionEvaluator evaluator = suggestion.instantiateEvaluator();
        Person person = Context.getPersonService().getPerson(6);  // this person is a binome; suggestion says to fetch all providers with same role
        Collection<Person> persons = evaluator.evaluate(suggestion, person);

        Assert.assertEquals(3, persons.size());

        // double check that the patients assigned to the new provider are correct
        Iterator<Person> i = persons.iterator();

        while (i.hasNext()) {
            Person p = i.next();
            int id = p.getId();

            if (id == 2 || id == 6 || id == 7) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, persons.size());
    }


    @Test
    public void shouldEvaluateGroovyRuleUsingSQLCall() {

        Suggestion suggestion = new SupervisionSuggestion();

        suggestion.setCriteria("List<List<Object>> results = administrationService.executeSQL(\"select person_id from person where person_id='2' or person_id=${provider.getId()}\",false);" +
                "Set<org.openmrs.Person> persons = new HashSet<org.openmrs.Person>();" +
                "persons.add(personService.getPerson(results[0][0]));" +
                "persons.add(personService.getPerson(results[1][0]));" +
                "return persons;");

        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        SuggestionEvaluator evaluator= suggestion.instantiateEvaluator();
        Person person = Context.getPersonService().getPerson(6);  // this person is a binome; suggestion says to fetch all providers with same role
        Collection<Person> persons = evaluator.evaluate(suggestion, person);
        Assert.assertEquals(2, persons.size());

        // double check that the patients assigned to the new provider are correct
        Iterator<Person> i = persons.iterator();

        while (i.hasNext()) {
            Person p = i.next();
            int id = p.getId();

            if (id == 2 || id == 6) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, persons.size());
    }

    @Test
    public void shouldEvaluateBasicGroovyRuleForPatient() {

        Suggestion suggestion = new ProviderSuggestion();
        Patient patient = Context.getPatientService().getPatient(2);
        RelationshipType relationshipType = Context.getPersonService().getRelationshipType(1001);


        suggestion.setCriteria("Set<org.openmrs.Person> persons = new HashSet<org.openmrs.Person>();" +
                "persons.addAll(providerManagementService.getProvidersAsPersonsByRelationshipType(relationshipType));" +
                "return persons;");

        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        SuggestionEvaluator evaluator = suggestion.instantiateEvaluator();
        Collection<Person> persons = evaluator.evaluate(suggestion, patient, relationshipType);

        Assert.assertEquals(4, persons.size());

        // double check that the patients assigned to the new provider are correct
        Iterator<Person> i = persons.iterator();

        while (i.hasNext()) {
            Person p = i.next();
            int id = p.getId();

            if (id == 2 || id == 6 || id == 7 || id == 8) {
                i.remove();
            }
        }

        // list should now be empty
        Assert.assertEquals(0, persons.size());
    }
}