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
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderManagementGlobalProperties;
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: fields of multi-select widgets should be customizable

public class ProviderDashboardPageController {

    public void controller (PageModel pageModel,
                            @RequestParam(value = "person", required = false) Person personParam,
                            @RequestParam(value = "personId", required = false) Integer personId,
                            UiUtils ui)
                throws PersonIsNotProviderException, InvalidRelationshipTypeException {

        ProviderManagementService pmService = Context.getService(ProviderManagementService.class);

        // util fetches the appropriate person, throwing an exception if need be
        Person person = ProviderManagementWebUtil.getPerson(personParam, personId);
        pageModel.addAttribute("person", person);

        // util fetches the appropriate provider, throwing an exception if need be
        Provider provider = ProviderManagementWebUtil.getProvider(person);
        pageModel.addAttribute("provider", provider);

        // add the patients of this provider, grouped by relationship type
        Map<RelationshipType, List<Patient>> patientMap = new HashMap<RelationshipType,List<Patient>>();
        if (provider.getProviderRole() != null && provider.getProviderRole().getRelationshipTypes() != null) {
            for (RelationshipType relationshipType : provider.getProviderRole().getRelationshipTypes() ) {
                if (!relationshipType.isRetired()) {
                    patientMap.put(relationshipType, new ArrayList<Patient>());

                    for (Patient patient : pmService.getPatientsOfProvider(person, relationshipType)) {
                        patientMap.get(relationshipType).add(patient);
                    }
                }
            }
        }

       pageModel.addAttribute("patientMap", patientMap);

       List<Person> supervisors = pmService.getSupervisorsForProvider(person);
       pageModel.addAttribute("supervisors", ProviderManagementWebUtil.convertPersonListToSimpleObjectList(supervisors, ui, ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_LIST_DISPLAY_FIELDS().toArray(new String[0]) ));

       List<Person> supervisees = pmService.getSuperviseesForSupervisor(person);
       pageModel.addAttribute("supervisees", ProviderManagementWebUtil.convertPersonListToSimpleObjectList(supervisees, ui, ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_LIST_DISPLAY_FIELDS().toArray(new String[0])));

        // add the global properties that specifies the fields to display in the provider and patient field and search results
        pageModel.addAttribute("providerSearchDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_SEARCH_DISPLAY_FIELDS());
        pageModel.addAttribute("providerListDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_LIST_DISPLAY_FIELDS());
        pageModel.addAttribute("patientSearchDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PATIENT_SEARCH_DISPLAY_FIELDS());
        pageModel.addAttribute("patientListDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PATIENT_LIST_DISPLAY_FIELDS());
    }
}
