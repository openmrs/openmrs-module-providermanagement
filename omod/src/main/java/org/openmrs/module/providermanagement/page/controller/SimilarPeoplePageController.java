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

package org.openmrs.module.providermanagement.page.controller;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderManagementGlobalProperties;
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.comparator.PersonByFirstNameComparator;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.openmrs.ui.framework.page.Redirect;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SimilarPeoplePageController {

    public String controller (PageModel pageModel, @RequestParam(value = "name", required = true) String name,
                            UiUtils ui)
            throws PersonIsNotProviderException {

        // first find any similar providers and persons
        List<Person> similarProviders = new ArrayList<Person>();
        List<Person> similarPeople;

        // don't bother searching if the name is less than 3 characters
        if (name != null && name.length() > 2) {
            similarPeople =  Context.getPersonService().getPeople(name, false);

            // if there are no similar persons, go directly to the create provider page
            if (similarPeople == null || similarPeople.size() == 0) {
                return "redirect:" + new Redirect("providermanagement", "providerCreate","name=" + name).getUrl();
            }

            // otherwise, split into two lists, one of providers and the other of persons
            Iterator<Person> i = similarPeople.iterator();
            while (i.hasNext()) {
                Person p = i.next();
                if (Context.getService(ProviderManagementService.class).isProvider(p)) {
                    similarProviders.add(p);
                    i.remove();
                }
            }

            // the get people search doesn't appear to sort by name, so we do that here
            Collections.sort(similarPeople, new PersonByFirstNameComparator());
            Collections.sort(similarProviders, new PersonByFirstNameComparator());

            // add the lists, as well as the original name, to the page model
            // (note that we have to convert similarProviders to a simple object so that we can pick up the associated provider attributes; similarPersons is fine as-is)
            pageModel.addAttribute("similarPeople",similarPeople);
            pageModel.addAttribute("similarProviders", ProviderManagementWebUtil.convertPersonListToSimpleObjectList(similarProviders, ui, ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_SEARCH_DISPLAY_FIELDS().values().toArray(new String [0])));
            pageModel.addAttribute("name",name);

            // add the global properties that specifies the fields to display in the provider and patient search results
            pageModel.addAttribute("providerSearchDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_SEARCH_DISPLAY_FIELDS());
            pageModel.addAttribute("personSearchDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PERSON_SEARCH_DISPLAY_FIELDS());

            // in this case we just want to use the default page view, so return null
            return null;
        }
        else {
            return "redirect:" + new Redirect("providermanagement", "providerCreate","name=" + name).getUrl();
        }
    }
}
