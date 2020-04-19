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

package org.openmrs.module.providermanagement.suggestion;

import org.openmrs.module.providermanagement.ProviderRole;

public class SupervisionSuggestion extends Suggestion {

    private Integer supervisionSuggestionId;

    // provider role this rule applies to
    private ProviderRole providerRole;

    // whether is a "supervisor" suggestion or a "supervisee" suggestion
    private SupervisionSuggestionType suggestionType;

    public SupervisionSuggestion() {
    }

    @Override
    public Integer getId() {
        return supervisionSuggestionId;
    }

    @Override
    public void setId(Integer id) {
        this.supervisionSuggestionId = id;
    }

    public Integer getSupervisionSuggestionId() {
        return supervisionSuggestionId;
    }

    public void setSupervisionSuggestionId(Integer supervisionSuggestionId) {
        this.supervisionSuggestionId = supervisionSuggestionId;
    }

    public ProviderRole getProviderRole() {
        return providerRole;
    }

    public void setProviderRole(ProviderRole providerRole) {
        this.providerRole = providerRole;
    }

    public SupervisionSuggestionType getSuggestionType() {
        return suggestionType;
    }

    public void setSuggestionType(SupervisionSuggestionType suggestionType) {
        this.suggestionType = suggestionType;
    }
}
