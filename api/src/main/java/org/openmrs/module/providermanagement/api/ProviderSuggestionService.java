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
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.exception.SuggestionEvaluationException;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestionType;

import java.util.List;

// TODO: random note--will need to create a "provider" tag that displays provider information whe given a person?

public interface ProviderSuggestionService {

    /**
     * Gets the provider suggestion referenced by the specified id
     *
     * @param id
     * @return the provider suggestion referenced by the specified id
     */
    public ProviderSuggestion getProviderSuggestion(Integer id);

    /**
     * Gets the provider suggestion referenced by the specified uuid
     *
     * @param uuid
     * @return the provider suggestion referenced by the specified uuid
     */
    public ProviderSuggestion getProviderSuggestionByUuid(String uuid);

    /**
     * Gets the provider suggestions for the specified relationship type
     *
     * @param relationshipType
     * @return the provider suggestions for the specified relationship type
     */
    public List<ProviderSuggestion> getProviderSuggestionsByRelationshipType(RelationshipType relationshipType);

    /**
     * Saves the specified provider suggestion
     *
     * @param suggestion
     */
    public void saveProviderSuggestion(ProviderSuggestion suggestion);


    /**
     * Retires the specified provider suggestion
     *
     * @param suggestion
     * @param reason
     */
    public void retireProviderSuggestion(ProviderSuggestion suggestion, String reason);

    /**
     * Unretired the specified provider suggestion
     *
     * @param suggestion
     */
    public void unretireProviderSuggestion(ProviderSuggestion suggestion);

    /**
     * Purges the specified provider suggestion
     *
     * @param suggestion
     */
    public void purgeProviderSuggestion(ProviderSuggestion suggestion);

    /**
     * Suggests all the potential providers for a patient based on relationship type
     *
     * If no ProviderSuggestions are found for the specified relationship type, this
     * method will return all providers in the system who support the specified relationship type
     *
     * If one or more ProviderSuggestions exist, the method will take the union of the provider sets
     * returns by the suggestions; from the resultant set it will *remove* any providers who do not support
     * the specified relationship type
     *
     * Finally, any providers currently associated with specified patient via the specified relationship type
     * are removed from the result set
     *
     * @param patient
     * @param relationshipType
     * @return a list of potential providers for a patient based on relationship type
     * @throws InvalidRelationshipTypeException
     * @throws SuggestionEvaluationException
     */
    public List<Person> suggestProvidersForPatient(Patient patient, RelationshipType relationshipType)
            throws InvalidRelationshipTypeException, SuggestionEvaluationException;

    /**
     * Gets the Supervision Suggestion referenced by the specified id
     *
     * @param id
     * @return the Supervision Suggestion referenced by the specified id
     */
    public SupervisionSuggestion getSupervisionSuggestion(Integer id);

    /**
     * Gets the Supervision Suggestion referenced by the specified uuid
     *
     * @param uuid
     * @return the Supervision Suggestion referenced by the specified uuid
     */
    public SupervisionSuggestion getSupervisionSuggestionByUuid(String uuid);

    /**
     * Gets all Supervision Suggestions for the specified provider role of the specified type
     *
     * @param providerRole
     * @param suggestionType if set to null will return all suggestions for provider role regardless of type
     * @return  all Supervision Suggestions for the specified provider role of the specified type
     */
    public List<SupervisionSuggestion> getSupervisionSuggestionsByProviderRoleAndSuggestionType(ProviderRole providerRole, SupervisionSuggestionType suggestionType);

    /**
     * Gets all Supervision Suggestions for the specified provider role
     *
     * @param providerRole
     * @return  all Supervision Suggestions for the specified provider role
     */
    public List<SupervisionSuggestion> getSupervisionSuggestionsByProviderRole(ProviderRole providerRole);

    /**
     * Saves the specified supervision suggestion
     *
     * @param suggestion
     */
    public void saveSupervisionSuggestion(SupervisionSuggestion suggestion);

    /**
     * Retires the specified supervision suggestion
     *
     * @param suggestion
     * @param reason
     */
    public void retireSupervisionSuggestion(SupervisionSuggestion suggestion, String reason);

    /**
     * Unretires the specified supervision suggestion
     *
     * @param suggestion
     */
    public void unretireSupervisionSuggestion(SupervisionSuggestion suggestion);

    /**
     * Purges the specified supervision suggestion
     *
     * @param suggestion
     */
    public void purgeSupervisionSuggestion(SupervisionSuggestion suggestion);

    /**
     * Suggests the supervisors for a provider based on that provider's role(s)
     *
     * If no SupervisionSuggestions of type "Supervisor" are found for the provider's roles, this
     * method will return all providers who have roles that are valid supervisor roles for the roles
     * of the passed provider
     *
     * If one or more SupervisionSuggestions exist, the method will take the union of the provider sets
     * returns by the suggestions; from the resultant set it will *remove* any providers who do not
     * have a role that is valid supervisory role for one or more of the roles of the passed provider
     *
     * Finally, any providers currently supervising the specified provider are removed from the result set
     *
     * @param provider
     * @return
     * @throws PersonIsNotProviderException
     * @throws SuggestionEvaluationException
     */
    public List<Person> suggestSupervisorsForProvider(Person provider)
            throws PersonIsNotProviderException, SuggestionEvaluationException;

    /**
     * Suggests the supervisees for a provider based on that provider's role(s)
     *
     * If no SupervisionSuggestions of type "Supervisee" are found for the provider's roles, this
     * method will return all providers who have roles that are valid supervisee roles for the roles
     * of the passed provider
     *
     * If one or more SupervisionSuggestions exist, the method will take the union of the provider sets
     * returns by the suggestions; from the resultant set it will *remove* any providers who do not
     * have a role that is valid supervisee role for one or more of the roles of the passed provider
     *
     * Finally, any providers currently being supervised by specified provider are removed from the result set
     *
     * @param provider
     * @return
     * @throws PersonIsNotProviderException
     * @throws SuggestionEvaluationException
     */
    public List<Person> suggestSuperviseesForProvider(Person provider)
            throws PersonIsNotProviderException, SuggestionEvaluationException;
}
