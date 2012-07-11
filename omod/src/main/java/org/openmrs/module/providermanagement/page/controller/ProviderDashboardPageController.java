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
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderManagementConstants;
import org.openmrs.module.providermanagement.ProviderManagementGlobalProperties;
import org.openmrs.module.providermanagement.ProviderManagementUtils;
import org.openmrs.module.providermanagement.ProviderManagementWebUtil;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.api.ProviderSuggestionService;
import org.openmrs.module.providermanagement.command.PatientAndRelationship;
import org.openmrs.module.providermanagement.command.ProviderAndRelationship;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.exception.SuggestionEvaluationException;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderDashboardPageController {

    public void controller (HttpServletRequest request, PageModel pageModel,
                            @RequestParam(value = "person", required = false) Person personParam,
                            @RequestParam(value = "personId", required = false) Integer personId,
                            @RequestParam(value = "paneId", required = false) String paneId,
                            UiUtils ui)
                throws PersonIsNotProviderException, InvalidRelationshipTypeException, SuggestionEvaluationException {

        ProviderManagementService pmService = Context.getService(ProviderManagementService.class);

        // util fetches the appropriate person, throwing an exception if need be
        Person person = ProviderManagementWebUtil.getPerson(personParam, personId);
        pageModel.addAttribute("person", person);

        // util fetches the appropriate provider, throwing an exception if need be
        Provider provider = ProviderManagementWebUtil.getProvider(person);
        pageModel.addAttribute("provider", provider);

        // if the provider has the appropriate privilege, add the patients of the provider
        if (Context.hasPrivilege(ProviderManagementConstants.PROVIDER_MANAGEMENT_DASHBOARD_VIEW_PATIENTS_PRIVILEGE)) {
            // add the patients of this provider, grouped by relationship type
            Map<RelationshipType, List<PatientAndRelationship>> currentPatientMap = new HashMap<RelationshipType,List<PatientAndRelationship>>();
            Map<RelationshipType, List<PatientAndRelationship>> historicalPatientMap = new HashMap<RelationshipType, List<PatientAndRelationship>>();

            if (provider.getProviderRole() != null && provider.getProviderRole().getRelationshipTypes() != null) {
                for (RelationshipType relationshipType : provider.getProviderRole().getRelationshipTypes() ) {

                    if (!relationshipType.isRetired()) {
                        currentPatientMap.put(relationshipType, new ArrayList<PatientAndRelationship>());
                        historicalPatientMap.put(relationshipType, new ArrayList<PatientAndRelationship>());

                        for (Relationship relationship : pmService.getPatientRelationshipsForProvider(person, relationshipType, null)) {

                            Patient patient = Context.getPatientService().getPatient(relationship.getPersonB().getId());

                            if (ProviderManagementUtils.isRelationshipActive(relationship)) {
                                currentPatientMap.get(relationshipType).add(new PatientAndRelationship(patient, relationship));
                            }
                            else {
                                historicalPatientMap.get(relationshipType).add(new PatientAndRelationship(patient, relationship));
                            }

                        }

                    }

                }
            }
            pageModel.addAttribute("currentPatientMap", currentPatientMap);
            pageModel.addAttribute("historicalPatientMap", historicalPatientMap);
        }
        // otherwise, if the patient does not have view patient privileges, calculate an aggregate patient count
        else {
            Map<RelationshipType, Integer> patientCount = new HashMap<RelationshipType,Integer>();
            if (provider.getProviderRole() != null && provider.getProviderRole().getRelationshipTypes() != null) {
                for (RelationshipType relationshipType : provider.getProviderRole().getRelationshipTypes() ) {
                    if (!relationshipType.isRetired()) {
                        patientCount.put(relationshipType, pmService.getPatientsOfProviderCount(person, relationshipType, new Date()));
                    }
                }
            }
            pageModel.addAttribute("patientCount", patientCount);
        }

        // add the supervisors of the provider

        List<ProviderAndRelationship> currentSupervisors = new ArrayList<ProviderAndRelationship>();
        List<ProviderAndRelationship> historicalSupervisors = new ArrayList<ProviderAndRelationship>();

        for (Relationship relationship : pmService.getSupervisorRelationshipsForProvider(person, null)) {

            Provider p = ProviderManagementWebUtil.getProvider(relationship.getPersonA());

            if (ProviderManagementUtils.isRelationshipActive(relationship)) {
                currentSupervisors.add(new ProviderAndRelationship(p, relationship));
            }
            else {
                historicalSupervisors.add(new ProviderAndRelationship(p, relationship));
            }
        }

        pageModel.addAttribute("currentSupervisors", currentSupervisors);
        pageModel.addAttribute("historicalSupervisors", historicalSupervisors);

        // add the supervisees of the provider

        if (provider.getProviderRole() != null && provider.getProviderRole().isSupervisorRole()) {
            List<ProviderAndRelationship> currentSupervisees = new ArrayList<ProviderAndRelationship>();
            List<ProviderAndRelationship> historicalSupervisees = new ArrayList<ProviderAndRelationship>();

            for (Relationship relationship : pmService.getSuperviseeRelationshipsForSupervisor(person, null)) {

                Provider p = ProviderManagementWebUtil.getProvider(relationship.getPersonB());

                if (ProviderManagementUtils.isRelationshipActive(relationship)) {
                    currentSupervisees.add(new ProviderAndRelationship(p, relationship));
                }
                else {
                    historicalSupervisees.add(new ProviderAndRelationship(p, relationship));
                }
            }

            pageModel.addAttribute("currentSupervisees", currentSupervisees);
            pageModel.addAttribute("historicalSupervisees", historicalSupervisees);
        }


        if (Context.hasPrivilege(ProviderManagementConstants.PROVIDER_MANAGEMENT_DASHBOARD_EDIT_PROVIDERS_PRIVILEGE)) {
            // calculate suggested supervisees
            if (provider.getProviderRole() != null && provider.getProviderRole().isSupervisorRole()) {
                List<Person> suggestedSupervisees = Context.getService(ProviderSuggestionService.class).suggestSuperviseesForProvider(person);
                if (suggestedSupervisees != null && suggestedSupervisees.size() > 0) {
                    pageModel.addAttribute("suggestedSupervisees", ProviderManagementWebUtil.convertPersonListToSimpleObjectList(suggestedSupervisees, ui, ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_SEARCH_DISPLAY_FIELDS().values().toArray(new String[0])));
                }
                else {
                    pageModel.addAttribute("suggestedSupervisees", null);
                }
            }
            else {
                pageModel.addAttribute("suggestedSupervisees", null);
            }
        }

        // add the pane id (so that we know which pane to display
        pageModel.addAttribute("paneId", paneId);

        // add the global properties that specifies the fields to display in the provider and patient field and search results
        pageModel.addAttribute("providerSearchDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_SEARCH_DISPLAY_FIELDS());
        pageModel.addAttribute("providerListDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PROVIDER_LIST_DISPLAY_FIELDS());
        pageModel.addAttribute("patientSearchDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PATIENT_SEARCH_DISPLAY_FIELDS());
        pageModel.addAttribute("patientListDisplayFields", ProviderManagementGlobalProperties.GLOBAL_PROPERTY_PATIENT_LIST_DISPLAY_FIELDS());
    }
}
