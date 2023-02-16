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

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderManagementGlobalProperties;
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.api.ProviderSuggestionService;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.exception.SuggestionEvaluationException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientProviderDashboardPageController {

    public void controller (PageModel pageModel,
                             @RequestParam(value = "patient", required = true) Patient patient, UiUtils ui)
            throws InvalidRelationshipTypeException, PersonIsNotProviderException, SuggestionEvaluationException {

        ProviderManagementService pmService = Context.getService(ProviderManagementService.class);

        Map<RelationshipType, List<SimpleObject>> providerMap = new HashMap<RelationshipType, List<SimpleObject>>();
        Map<RelationshipType,List<SimpleObject>> providerSuggestionMap = new HashMap<RelationshipType, List<SimpleObject>>();

        // first find the provider (or list of providers) for each relationship type
        for (RelationshipType relationshipType : pmService.getAllProviderRoleRelationshipTypes(false)) {
            List<Person> p = pmService.getProvidersAsPersonsForPatient(patient, relationshipType, new Date());

            // if we have existing providers, add them to the results list
            if (p != null && p.size() > 0) {
                providerMap.put(relationshipType, ProviderManagementWebUtil.convertPersonListToSimpleObjectList(p, ui, ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_LIST_DISPLAY_FIELDS().values().toArray(new String[0])));
            }
            // otherwise, get suggestions for this relationship type
            else {
                // note that we still still add a blank map entry to providers so that we can iterate over the keys in the view
                providerMap.put(relationshipType, null);

                // get the suggestions for the patient, cnonvert them to a simple object, and add them to the map
                providerSuggestionMap.put(relationshipType,
                        ProviderManagementWebUtil.convertPersonListToSimpleObjectList(
                                Context.getService(ProviderSuggestionService.class).suggestProvidersForPatient(patient, relationshipType),
                                ui, ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_LIST_DISPLAY_FIELDS().values().toArray(new String[0])));
            }
        }

        pageModel.addAttribute("patient", patient);

        pageModel.addAttribute("providerMap", providerMap);
        pageModel.addAttribute("providerSuggestionMap", providerSuggestionMap);

        // add the global properties that specifies the fields to display in the provider and patient field and search results
        pageModel.addAttribute("providerSearchDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_SEARCH_DISPLAY_FIELDS());
        pageModel.addAttribute("providerListDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_LIST_DISPLAY_FIELDS());
    }


}
