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
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class PatientSearchFragmentController {

    public List<SimpleObject> getPatients(@RequestParam(value="searchValue", required=true) String searchValue,
                                          @RequestParam(value="resultFields", required=false) String[] resultFields,
                                           UiUtils ui) {

        // TODO: set resultFields required=true and remove this default once binding problem is fixed
        if (resultFields == null || resultFields.length == 0) {
            resultFields = new String[] {"personName"};
        }

        // always want to return the id of the result objects
        resultFields = ArrayUtils.add(resultFields, "id");

        // TODO: add searching by identifier

        // now fetch the results
        List<Patient> patients = Context.getPatientService().getPatients(searchValue, null, null, false);
        return SimpleObject.fromCollection(patients, ui, resultFields);
    }


}
