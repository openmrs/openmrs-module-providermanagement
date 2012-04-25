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

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

public class ProviderUpdateFragmentController {

    public FragmentActionResult addSupervisee(@RequestParam(value = "superviserId", required = true) Integer supervisorId,
                                             @RequestParam(value="id", required=true) Integer superviseeId) {

        // TODO: better handle error cases
        try {
            Person supervisor = Context.getPersonService().getPerson(supervisorId);
            Person supervisee = Context.getPersonService().getPerson(superviseeId);
            Context.getService(ProviderManagementService.class).assignProviderToSupervisor(supervisee, supervisor);
            return new SuccessResult();
        }
        catch (Exception e) {
           throw new RuntimeException(e);
        }

    }

    public FragmentActionResult addPatient(@RequestParam(value = "providerId", required = true) Integer providerId,
                                           @RequestParam(value = "relationshipTypeId", required = true) Integer relationshipTypeId,
                                           @RequestParam(value = "id", required = true) Integer patientId) {

        // TODO: better handle error cases
        try {
            Person provider = Context.getPersonService().getPerson(providerId);
            Patient patient = Context.getPatientService().getPatient(patientId);
            RelationshipType relationshipType = Context.getPersonService().getRelationshipType(relationshipTypeId);
            Context.getService(ProviderManagementService.class).assignPatientToProvider(patient, provider, relationshipType);
            return new SuccessResult();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
