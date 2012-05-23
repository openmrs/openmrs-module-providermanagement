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

import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.api.ProviderSuggestionService;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.ui.framework.fragment.action.FailureResult;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class ProviderSuggestionFormFragmentController {

    public void controller(FragmentModel model,
                           @FragmentParam(value= "providerSuggestion", required=false) ProviderSuggestion suggestion) {

        // hard code the evaluator to groovy evaluator since this is the only type we currently support
        if (suggestion == null) {
            suggestion = new ProviderSuggestion();
            suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");
        }

        model.addAttribute("providerSuggestion", suggestion);

        // add possible relationship types
        List<RelationshipType> relationshipTypes = Context.getService(ProviderManagementService.class).getAllProviderRoleRelationshipTypes(false);
        model.addAttribute("relationshipTypes", relationshipTypes);

    }

    public FragmentActionResult deleteProviderSuggestion(@RequestParam(value = "providerSuggestion", required = true) ProviderSuggestion providerSuggestion) {
        try {
            Context.getService(ProviderSuggestionService.class).purgeProviderSuggestion(providerSuggestion);
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }

    public FragmentActionResult retireProviderSuggestion(@RequestParam(value = "providerSuggestion", required = true) ProviderSuggestion providerSuggestion) {
        try {
            Context.getService(ProviderSuggestionService.class).retireProviderSuggestion(providerSuggestion, "retired via provider management ui");
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
}

    public FragmentActionResult saveProviderSuggestion(@BindParams() ProviderSuggestion suggestion) {

        // TODO: (PROV-12) add validation to check to make sure criteria is valid Groovy code

        // hard code the evaluator to the groovy evaluator since this is the only type we currently support
        suggestion.setEvaluator("org.openmrs.module.providermanagement.suggestion.GroovySuggestionEvaluator");

        try {
            Context.getService(ProviderSuggestionService.class).saveProviderSuggestion(suggestion);
            return new SuccessResult();
        }
        catch (Exception e) {
            return new FailureResult(e.getLocalizedMessage());
        }
    }
}