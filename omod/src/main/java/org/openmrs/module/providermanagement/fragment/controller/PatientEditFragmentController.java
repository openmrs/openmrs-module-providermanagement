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
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class PatientEditFragmentController {

    public FragmentActionResult addProviders(@RequestParam(value = "patient", required = true) Patient patient,
                                                @RequestParam(value = "relationshipType", required = true) RelationshipType relationshipType,
                                                @RequestParam(value = "providers", required = true) List<Person> providers) {

        // TODO: better handle error cases
        try {
            for (Person provider : providers) {
                Context.getService(ProviderManagementService.class).assignPatientToProvider(patient, provider, relationshipType);
            }
            return new SuccessResult();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public FragmentActionResult removeProviders(@RequestParam(value = "patient", required = true) Patient patient,
                                                   @RequestParam(value = "relationshipType", required = true) RelationshipType relationshipType,
                                                   @RequestParam(value = "providers", required = true) List<Person> providers) {

        // TODO: better handle error cases
        try {
            for (Person provider : providers) {
                Context.getService(ProviderManagementService.class).unassignPatientFromProvider(patient, provider, relationshipType);
            }
            return new SuccessResult();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
