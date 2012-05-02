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

import org.apache.commons.lang3.ArrayUtils;
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
import java.util.List;
import java.util.Map;

public class ProviderSearchFragmentController {

    public List<SimpleObject> getProviders(@RequestParam(value="searchValue", required=true) String searchValue,
                                          @RequestParam(value="providerRoleIds", required=false) Integer[] providerRoleIds,
                                          @RequestParam(value="resultFields[]", required=false) String[] resultFields,
                                          UiUtils ui)
                throws PersonIsNotProviderException {

        // NOTE that by default we return an empty list if the searchValue size < 2
        if (searchValue == null || searchValue.length() < 2) {
            return new ArrayList<SimpleObject>();
        }

        if (resultFields == null || resultFields.length == 0) {
            resultFields = new String[] {"personName"};
        }

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

        // build the list of roles from the request params
        List<ProviderRole> providerRoles = new ArrayList<ProviderRole>();

        // TODO: may need to set providerRoleId back to a String if this doesn't work correctly once binding is fixed
        if (providerRoleIds != null && providerRoleIds.length > 0) {
            for (Integer providerRoleId : providerRoleIds) {
                providerRoles.add(Context.getService(ProviderManagementService.class).getProviderRole(providerRoleId));
            }
        }

        // now fetch the results
        List<Person> persons = Context.getService(ProviderManagementService.class).getProviders(searchValue, providerRoles, false);

        List<SimpleObject> simpleProviders;

       // if any provider fields have been requested, we must manually add them to the simpleObject we are returning
       if (providerResultFields.size() > 0) {
           simpleProviders = new ArrayList<SimpleObject>();
           for (Person person : persons) {
               SimpleObject simpleProvider = SimpleObject.fromObject(person, ui,  personResultFields.toArray(new String[0]));
               simpleProvider.put("provider", SimpleObject.fromObject(ProviderManagementWebUtil.getProvider(person), ui, providerResultFields.toArray(new String[0])));
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
