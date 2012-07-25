package org.openmrs.module.providermanagement;/*
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

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.Model;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class ProviderManagementWebUtil {

    // TODO: unit test web utility methods (see PROV-52)

    /**
     * Utility method that, given a model, a person param, and a person id, fetches a
     * person; this method is used by fragment controllers that need to resolve a person
     * based on the multiple incoing source: their parent page model, a Person fragment paramter,
     * and a person id fragment parameter
     *
     * @param model
     * @param personParam
     * @param personId
     * @return
     */
    public static Person getPerson(Model model, Person personParam, Integer personId) {

        // used to test if both a Person object and a personId were passed to this fragment
        if (personParam != null && personId != null) {
            throw new RuntimeException("Cannot specify both person object and person id");
        }

        Person person = null;

        // fetch any provider on the shared page
        person = (model != null ? (Person) model.getAttribute("person") : null);

        // now override that person with any that may have been specified as as param
        person = (personParam != null) ? personParam : person;
        person = (personId != null) ? Context.getPersonService().getPerson(personId) : person;

        return person;
    }

    public static Person getPerson(Person personParam, Integer personId) {
        return getPerson(null, personParam, personId);
    }

    /**
     * Fetches the provider object associated with the passed person
     * Throws an exception if there are no provider objects associated with the person,
     * or more than 1 objects associated with the person
     *
     * @param person
     * @return
     */
    public static Provider getProvider(Person person)
            throws PersonIsNotProviderException {

        List<Provider> providers = Context.getService(ProviderManagementService.class).getProvidersByPerson(person, true);

        // check to make sure this person is really a provider
        if (providers == null || providers.size() == 0) {
            throw new PersonIsNotProviderException();
        }

        // make sure the person isn't associated with multiple providers
        if (providers.size() > 1) {
            // TODO: (PROV-21) Improve UI to handle persons with one or more associated Provider objects
            throw new RuntimeException("Provider Management module currently does not support persons with multiple providers associated with them");
        }

        return providers.get(0);
    }

    /**
     * Converts a list of persons to a SimpleObject with the specified result fields
     * The key thing this method handles that the SimpleObject.fromCollection does
     * not is the ability reference fields of the provider associated with the person
     *
     * @param persons
     * @param resultFields
     * @param ui
     * @return
     * @throws PersonIsNotProviderException
     */
    public static List<SimpleObject> convertPersonListToSimpleObjectList(List<Person> persons, UiUtils ui, String [] resultFields)
            throws PersonIsNotProviderException {

        // separate the person object fields from the provider object fields
        List<String> personResultFields = new ArrayList<String>();
        List<String> providerResultFields = new ArrayList<String>();

        for (String resultField : resultFields) {
            if (resultField.startsWith("provider.")) {
                StringBuilder builder = new StringBuilder(resultField);
                builder.delete(0,9); // delete the "provider" prefix
                providerResultFields.add(builder.toString());
            }
            else {
                personResultFields.add(resultField);
            }
        }

        // always want to return the id of the result objects
        personResultFields.add("id");

        List<SimpleObject> simpleProviders;

        // if any provider fields have been requested, we must manually add them to the simpleObject we are returning
        if (providerResultFields.size() > 0) {
            simpleProviders = new ArrayList<SimpleObject>();
            for (Person person : persons) {
                SimpleObject simpleProvider = SimpleObject.fromObject(person, ui,  personResultFields.toArray(new String[0]));
                simpleProvider.put("provider", SimpleObject.fromObject(getProvider(person), ui, providerResultFields.toArray(new String[0])));
                simpleProviders.add(simpleProvider);
            }
        }
        // otherwise, just create the simpleProviders from the persons object
        else {
            simpleProviders = SimpleObject.fromCollection(persons, ui, personResultFields.toArray(new String[0]));
        }

        return simpleProviders;
    }
}
