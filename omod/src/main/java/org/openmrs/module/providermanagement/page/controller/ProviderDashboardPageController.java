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
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
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
                            @RequestParam(value = "personId", required = false) Integer personId)
                throws PersonIsNotProviderException, InvalidRelationshipTypeException {

        ProviderManagementService pmService = Context.getService(ProviderManagementService.class);

        // util fetches the appropriate person, throwing an exception if need be
        Person person = ProviderManagementWebUtil.getPerson(personParam, personId);
        pageModel.addAttribute("person", person);

        // util fetches the appropriate provider, throwing an exception if need be
        Provider provider = ProviderManagementWebUtil.getProvider(person);
        pageModel.addAttribute("provider", provider);

        // add the patients of this provider, grouped by relationship type
        Map<RelationshipType, List<Patient>> patients = new HashMap<RelationshipType,List<Patient>>();
        if (provider.getProviderRole() != null && provider.getProviderRole().getRelationshipTypes() != null) {
            for (RelationshipType relationshipType : provider.getProviderRole().getRelationshipTypes() ) {
                if (!relationshipType.isRetired()) {
                    patients.put(relationshipType, new ArrayList<Patient>());

                    for (Patient patient : pmService.getPatientsOfProvider(person, relationshipType)) {
                        patients.get(relationshipType).add(patient);
                    }
                }
            }
        }

       pageModel.addAttribute("patients", patients);

       // TODO: add some sort of check here so to that the supervising box can be hidden for roles that don't allow supervising

       List<Person> supervisees = pmService.getSuperviseesForSupervisor(person);
       pageModel.addAttribute("supervisees", supervisees);
    }
}
