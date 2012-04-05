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

package org.openmrs.module.providermanagement.api;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.SuggestionEvaluationException;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestion;

import java.util.Date;
import java.util.List;

// TODO: random note--will need to create a "provider" tag that displays provider information whe given a person?

public interface ProviderSuggestionService {

    // TODO: document all this!

    public ProviderSuggestion getProviderSuggestion(Integer id);

    public ProviderSuggestion getProviderSuggestionByUuid(String uuid);

    public List<ProviderSuggestion> getProviderSuggestionsByRelationshipType(RelationshipType relationshipType);

    public void saveProviderSuggestion(ProviderSuggestion suggestion);

    public void retireProviderSuggestion(ProviderSuggestion suggestion, String reason);

    public void unretireProviderSuggestion(ProviderSuggestion suggestion);

    public void purgeProviderSuggestion(ProviderSuggestion suggestion);

    public List<Person> suggestProvidersForPatient(Patient patient, RelationshipType relationshipType)
            throws InvalidRelationshipTypeException, SuggestionEvaluationException;



    // TODO: so for "SupervisionSuggestion" do we want an "auto-assign method"?
}
