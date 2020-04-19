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

import org.openmrs.RelationshipType;

public class ProviderSuggestion extends Suggestion {

    private Integer providerSuggestionId;

    // the relationship type this rule is associated with
    private RelationshipType relationshipType;

    @Override
    public Integer getId() {
        return providerSuggestionId;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setId(Integer id) {
        this.providerSuggestionId = id;
    }

    public Integer getProviderSuggestionId() {
        return providerSuggestionId;
    }

    public void setProviderSuggestionId(Integer providerSuggestionId) {
        this.providerSuggestionId = providerSuggestionId;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }
}
