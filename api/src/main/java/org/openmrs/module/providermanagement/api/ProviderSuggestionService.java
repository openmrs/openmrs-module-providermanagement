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
import org.openmrs.annotation.Authorized;
import org.openmrs.module.providermanagement.ProviderManagementConstants;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.exception.SuggestionEvaluationException;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestionType;

import java.util.List;

public interface ProviderSuggestionService {

    /**
     * Gets the provider suggestion referenced by the specified id
     *
     * @param id
     * @return the provider suggestion referenced by the specified id
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public ProviderSuggestion getProviderSuggestion(Integer id);

    /**
     * Gets the provider suggestion referenced by the specified uuid
     *
     * @param uuid
     * @return the provider suggestion referenced by the specified uuid
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public ProviderSuggestion getProviderSuggestionByUuid(String uuid);

    /**
     * Gets the provider suggestions for the specified relationship type
     *
     * @param relationshipType
     * @return the provider suggestions for the specified relationship type
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<ProviderSuggestion> getProviderSuggestionsByRelationshipType(RelationshipType relationshipType);

    /**
     * Gets all the provider suggestions
     *
     * @param includeRetired
     * @return
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<ProviderSuggestion> getAllProviderSuggestions(Boolean includeRetired);

    /**
     * Saves the specified provider suggestion
     *
     * @param suggestion
     * @return the saved provider suggestion
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public ProviderSuggestion saveProviderSuggestion(ProviderSuggestion suggestion);


    /**
     * Retires the specified provider suggestion
     *
     * @param suggestion
     * @param reason
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void retireProviderSuggestion(ProviderSuggestion suggestion, String reason);

    /**
     * Unretired the specified provider suggestion
     *
     * @param suggestion
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void unretireProviderSuggestion(ProviderSuggestion suggestion);

    /**
     * Purges the specified provider suggestion
     *
     * @param suggestion
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void purgeProviderSuggestion(ProviderSuggestion suggestion);

    /**
     * Suggests all the potential providers for a patient based on relationship type
     *
     * If one or more ProviderSuggestions are found for the specified relationship type, the method will
     * take the union of the provider sets returns by the suggestions; from the resultant set it will *remove*
     * any providers who do not support the specified relationship type. Finally, any providers currently
     * associated with specified patient via the specified relationship type are removed from the result set
     *
     *  If no ProviderSuggestions are found for the specified relationship type, this
     * method will return null
     *
     * @param patient
     * @param relationshipType
     * @return a list of potential providers for a patient based on relationship type
     * @throws InvalidRelationshipTypeException
     * @throws SuggestionEvaluationException
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> suggestProvidersForPatient(Patient patient, RelationshipType relationshipType)
            throws InvalidRelationshipTypeException, SuggestionEvaluationException;

    /**
     * Gets the Supervision Suggestion referenced by the specified id
     *
     * @param id
     * @return the Supervision Suggestion referenced by the specified id
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public SupervisionSuggestion getSupervisionSuggestion(Integer id);

    /**
     * Gets the Supervision Suggestion referenced by the specified uuid
     *
     * @param uuid
     * @return the Supervision Suggestion referenced by the specified uuid
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public SupervisionSuggestion getSupervisionSuggestionByUuid(String uuid);

    /**
     * Gets all Supervision Suggestions for the specified provider role of the specified type
     *
     * @param providerRole
     * @param suggestionType if set to null will return all suggestions for provider role regardless of type
     * @return  all Supervision Suggestions for the specified provider role of the specified type
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<SupervisionSuggestion> getSupervisionSuggestionsByProviderRoleAndSuggestionType(ProviderRole providerRole, SupervisionSuggestionType suggestionType);

    /**
     * Gets all Supervision Suggestions for the specified provider role
     *
     * @param providerRole
     * @return  all Supervision Suggestions for the specified provider role
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<SupervisionSuggestion> getSupervisionSuggestionsByProviderRole(ProviderRole providerRole);

    /**
     * Gets all the supervision suggestions
     *
     * @param includeRetired
     * @return
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<SupervisionSuggestion> getAllSupervisionSuggestions(Boolean includeRetired);

    /**
     * Saves the specified supervision suggestion
     *
     * @param suggestion
     * @return the saved supervision suggestion
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public SupervisionSuggestion saveSupervisionSuggestion(SupervisionSuggestion suggestion);

    /**
     * Retires the specified supervision suggestion
     *
     * @param suggestion
     * @param reason
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void retireSupervisionSuggestion(SupervisionSuggestion suggestion, String reason);

    /**
     * Unretires the specified supervision suggestion
     *
     * @param suggestion
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void unretireSupervisionSuggestion(SupervisionSuggestion suggestion);

    /**
     * Purges the specified supervision suggestion
     *
     * @param suggestion
     */
    @Authorized(ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE)
    public void purgeSupervisionSuggestion(SupervisionSuggestion suggestion);

    /**
     * Suggests the supervisors for a provider based on that provider's role(s)
     *
     * If one or more SupervisionSuggestions of type "Supervisor" exist for the provider's roles , the method will take the union
     * of the provider sets returns by the suggestions; from the resultant set it will *remove* any providers who do not
     * have a role that is valid supervisory role for one or more of the roles of the passed provider.
     * Finally, any providers currently supervising the specified provider are removed from the result set
     *                                                     *
     * If no SupervisionSuggestions of type "Supervisor" are found for the provider's roles, this
     * method will return null
     *
     * @param provider
     * @return
     * @throws PersonIsNotProviderException
     * @throws SuggestionEvaluationException
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> suggestSupervisorsForProvider(Person provider)
            throws PersonIsNotProviderException, SuggestionEvaluationException;

    /**
     * Suggests the supervisees for a provider based on that provider's role(s)
     *
     * If one or more SupervisionSuggestions of type "Supervisee" exist, the method will take the union of the provider sets
     * returns by the suggestions; from the resultant set it will *remove* any providers who do not
     * have a role that is valid supervisee role for one or more of the roles of the passed provider.
     * Finally, any providers currently being supervised by specified provider are removed from the result set
     *
     * If no SupervisionSuggestions of type "Supervisee" are found for the provider's roles, this
     * method will return null
     *
     * @param provider
     * @return
     * @throws PersonIsNotProviderException
     * @throws SuggestionEvaluationException
     */
    @Authorized(value = { ProviderManagementConstants.PROVIDER_MANAGEMENT_API_PRIVILEGE, ProviderManagementConstants.PROVIDER_MANAGEMENT_API_READ_ONLY_PRIVILEGE }, requireAll = false)
    public List<Person> suggestSuperviseesForProvider(Person provider)
            throws PersonIsNotProviderException, SuggestionEvaluationException;
}
