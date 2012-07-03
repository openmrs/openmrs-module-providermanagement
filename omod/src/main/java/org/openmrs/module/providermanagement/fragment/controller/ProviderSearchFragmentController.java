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
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ProviderSearchFragmentController {

    public List<SimpleObject> getProviders(@RequestParam(value="searchValue", required=true) String searchValue,
                                          @RequestParam(value="excludeSuperviseesOf", required=false) Person excludeSuperviseesOf,
                                          @RequestParam(value="excludeProvider", required=false) Person excludeProvider,
                                          @RequestParam(value="providerRoles[]", required=false) ProviderRole[] providerRoles,
                                          @RequestParam(value="resultFields[]", required=false) String[] resultFields,
                                          UiUtils ui)
                throws PersonIsNotProviderException {

        System.out.println(excludeProvider);

        // NOTE that by default we return an empty list if the searchValue size < 2
        if (searchValue == null || searchValue.length() < 3) {
            return new ArrayList<SimpleObject>();
        }

        if (resultFields == null || resultFields.length == 0) {
            resultFields = new String[] {"personName"};
        }

        // now fetch the results
        List<Person> persons = Context.getService(ProviderManagementService.class).getProviders(searchValue, providerRoles != null ? Arrays.asList(providerRoles) : null, false);

        // exclude supervisees of a provider if needed
        if (excludeSuperviseesOf != null) {
            List<Person> supervisees = Context.getService(ProviderManagementService.class).getSuperviseesForSupervisor(excludeSuperviseesOf, new Date());
            persons.removeAll(supervisees);
        }

        // exclude any specified provider
        if (excludeProvider != null) {
            persons.remove(excludeProvider);
        }

        // convert to a simple object list
        return ProviderManagementWebUtil.convertPersonListToSimpleObjectList(persons, ui, resultFields);
    }

}
