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
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.comparator.PersonByFirstNameComparator;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PersonSearchFragmentController {

    // TODO: I believe this method (and therefore this controller) is no longer in use; it was originally used
    // TODO: when be explicitly had the functionality for searching for a person to promote to a provider

    public List<SimpleObject> getPeople(@RequestParam(value="searchValue", required=true) String searchValue,
                                          @RequestParam(value="resultFields[]", required=true) String[] resultFields,
                                          UiUtils ui) {

        if (resultFields == null || resultFields.length == 0) {
            resultFields = new String[] {"personName"};
        }

        // always want to return the id of the result objects
        resultFields = ArrayUtils.add(resultFields, "id");

        // now fetch the results
        List<Person> people = Context.getPersonService().getPeople(searchValue, false);

        // exclude anyone who is already a provider
        Iterator<Person> i = people.iterator();
        while (i.hasNext()) {
            Person p = i.next();
            if (Context.getService(ProviderManagementService.class).isProvider(p)) {
                i.remove();
            }
        }


        // the get people search doesn't appear to sort by name, so we do that here
        Collections.sort(people, new PersonByFirstNameComparator());

        return SimpleObject.fromCollection(people, ui, resultFields);
    }


}
