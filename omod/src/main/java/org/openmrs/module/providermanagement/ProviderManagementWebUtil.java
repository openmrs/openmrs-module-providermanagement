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
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.Model;

import java.util.List;

public class ProviderManagementWebUtil {

    // TODO: add some unit tests

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

        // if we still haven't managed to find a person, throw an error
        if (person == null) {
            throw new RuntimeException("No valid person passed to provider view fragment");
        }

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
            // TODO: implement this
            throw new RuntimeException("Provider Management module currently does not support persons with multiple providers associated with them");
        }

        return providers.get(0);
    }
}
