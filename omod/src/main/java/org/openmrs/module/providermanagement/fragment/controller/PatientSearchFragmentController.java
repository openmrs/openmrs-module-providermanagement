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
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

public class PatientSearchFragmentController {

    public List<SimpleObject> getPatients(@RequestParam(value="searchValue", required=true) String searchValue,
                                          @RequestParam(value="excludePatientsOf", required=false) Person excludePatientsOf,
                                          @RequestParam(value="existingRelationshipTypeToExclude", required=false) RelationshipType existingRelationshipTypeToExclude,
                                          @RequestParam(value="resultFields[]", required=true) String[] resultFields,
                                           UiUtils ui)
               throws PersonIsNotProviderException, InvalidRelationshipTypeException {

        if (resultFields == null || resultFields.length == 0) {
            resultFields = new String[] {"personName"};
        }

        // always want to return the id of the result objects
        resultFields = ArrayUtils.add(resultFields, "id");

        // now fetch the results
        List<Patient> patients = Context.getPatientService().getPatients(searchValue);

        // exclude any patients if specified
        if (excludePatientsOf != null && existingRelationshipTypeToExclude != null) {
            List<Patient> patientsToExclude = Context.getService(ProviderManagementService.class).getPatientsOfProvider(excludePatientsOf, existingRelationshipTypeToExclude, new Date());
            patients.removeAll(patientsToExclude);
        }

        return SimpleObject.fromCollection(patients, ui, resultFields);
    }


}
