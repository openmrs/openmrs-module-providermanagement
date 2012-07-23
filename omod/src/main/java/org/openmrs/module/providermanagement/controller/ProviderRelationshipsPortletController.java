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

package org.openmrs.module.providermanagement.controller;

import org.openmrs.Patient;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.web.controller.PortletController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

// TODO: note that this portlet is currently not in use, see https://tickets.openmrs.org/browse/PROV-24

public class ProviderRelationshipsPortletController extends PortletController {

    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {

        // add the possible provider relationship types
        List<RelationshipType> providerRoleRelationshipType = Context.getService(ProviderManagementService.class).getAllProviderRoleRelationshipTypes(false);
        model.put("providerRelationshipTypes", providerRoleRelationshipType);

        // add the existing provider relationships
        Integer patientId = (Integer) model.get("patientId");
        Patient patient = Context.getPatientService().getPatient(patientId);

        try {
            model.put("providerRelationships", Context.getService(ProviderManagementService.class).getProviderRelationshipsForPatient(patient, null, null));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
