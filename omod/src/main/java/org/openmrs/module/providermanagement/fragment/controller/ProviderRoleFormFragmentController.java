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

import org.openmrs.ProviderAttributeType;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class ProviderRoleFormFragmentController {

    public void controller(FragmentModel model,
                           @FragmentParam(value= "providerRoleId", required=false) Integer providerRoleId) {


        // add any existing provider role to the form
        ProviderRole providerRole = null;

        if (providerRoleId != null) {
            providerRole = Context.getService(ProviderManagementService.class).getProviderRole(providerRoleId);
        }

        model.addAttribute("providerRole", providerRole);

        // add possible relationship types
        List<RelationshipType> relationshipTypes = Context.getPersonService().getAllRelationshipTypes(false);
        model.addAttribute("relationshipTypes", relationshipTypes);

        // add possible supervisee roles
        List<ProviderRole> providerRoles = Context.getService(ProviderManagementService.class).getAllProviderRoles(false);
        model.addAttribute("providerRoles", providerRoles);

        // add possible provider attributes
        List<ProviderAttributeType> providerAttributeTypes = Context.getProviderService().getAllProviderAttributeTypes(true);
        model.addAttribute("providerAttributeTypes", providerAttributeTypes);
    }

    public FragmentActionResult deleteProviderRole(@RequestParam(value = "providerRoleId", required = true) Integer providerRoleId) {

        ProviderManagementService providerManagementService = Context.getService(ProviderManagementService.class);

        try {
            providerManagementService.purgeProviderRole(providerManagementService.getProviderRole(providerRoleId));
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }

    public FragmentActionResult retireProviderRole(@RequestParam(value = "providerRoleId", required = true) Integer providerRoleId) {

        ProviderManagementService providerManagementService = Context.getService(ProviderManagementService.class);

        try {
            providerManagementService.retireProviderRole(providerManagementService.getProviderRole(providerRoleId), "retired via provider management ui");
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }

    public FragmentActionResult saveProviderRole(@BindParams() ProviderRole providerRole) {

        try {
            Context.getService(ProviderManagementService.class).saveProviderRole(providerRole);
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }
}
