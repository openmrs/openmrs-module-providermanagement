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

package org.openmrs.module.providermanagement.fragment.controller;

import org.openmrs.Person;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.List;

public class ProviderViewFragmentController {

    public void controller(FragmentModel model,
                           @FragmentParam(value = "personId", required = true) Integer personId)
                    throws PersonIsNotProviderException{

        // first, fetch the appropriate person
        Person person = Context.getPersonService().getPerson(personId);

        if (person == null) {
            throw new RuntimeException("Invalid personId passed to provider view fragment");
        }

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


        // add the person and the provider to the module
        model.addAttribute("person", person);
        model.addAttribute("provider", providers.get(0));
    }
}
