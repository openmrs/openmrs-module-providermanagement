/**
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
package org.openmrs.module.providermanagement.api.db;

import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.RelationshipType;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestionType;

import java.util.List;

/**
 *  Database methods for {@link ProviderManagementService}.
 */
public interface ProviderManagementDAO {

	/*
	 * Base Methods for saving and loading provider roles
	 */

    /**
     * Gets all Provider Roles in the database
     *
     * @param includeRetired whether or not to include retired providers
     * @return list of al provider roles in the system
     */
    public List<ProviderRole> getAllProviderRoles(boolean includeRetired);

    /**
     * Gets the provider role referenced by the specified id
     *
     * @param id
     * @return providerRole
     */
    public ProviderRole getProviderRole(Integer id);

    /**
     * Gets the provider role referenced by the specified uui
     *
     * @param uuid
     * @return providerRole
     */
    public ProviderRole getProviderRoleByUuid(String uuid);

    /**
     * Gets the list of provider roles that support the specified relationship type
     * (Excludes retired provider roles)
     *
     * @param relationshipType
     * @return list of provider roles that support that relationship type
     */
    public List<ProviderRole> getProviderRolesByRelationshipType(RelationshipType relationshipType);

    /**
     * Returns all provider roles that are able to supervise the specified provider role
     * (Excluded retired provider roles)
     *
     * @param providerRole
     * @return the provider roles that can supervise the specified provider role
     */
    public List<ProviderRole> getProviderRolesBySuperviseeProviderRole(ProviderRole providerRole);

    /**
     * Saves/updates a provider role
     *
     * @param role the provider role to save
     * @return provider role
     */
    public ProviderRole saveProviderRole(ProviderRole role);

    /**
     * Deletes a provider role
     *
     * @param role the provider role to delete
     */
    public void deleteProviderRole(ProviderRole role);

    /**
     * Gets the list of providers that match the specified name, identifier, and provider roles
     * (If any field is null it is ignored)
     *
     * @param name name to search on
     * @param identifier provider identifier
     * @param personAddress address to search on
     * @param personAttribute person attribute to search
     * @param providerRoles restrict results to providers with at least one of these roles
     * @param includeRetired whether or not to include retired providers
     * @return result list of providers
     */
    public List<Person> getProviders(String name, String identifier, PersonAddress personAddress, PersonAttribute personAttribute, List<ProviderRole> providerRoles, Boolean includeRetired);

    /**
     * Gets all providers associated with the current person
     *
     * @param person
     * @param includeRetired whether or not to include retired providers
     * @return all providers associated with the current person
     */
    public List<Provider> getProvidersByPerson(Person person, boolean includeRetired);

    /**
     * Gets all providers with the selected provider roles
     *
     * @param roles
     * @param includeRetired whether or not to include retired providers
     * @return all providers with the selected provider roles
     */
    public List<Provider> getProvidersByProviderRoles(List<ProviderRole> roles, boolean includeRetired);

    /**
     * Gets the provider suggestion referenced by the specified id
     *
     * @param id
     * @return providerSuggestion
     */
    public ProviderSuggestion getProviderSuggestion(Integer id);

    /**
     * Gets the provider suggestion referenced by the specified uuid
     *
     * @param uuid
     * @return  the provider suggestion referenced by the specified uuid
     */
    public ProviderSuggestion getProviderSuggestionByUuid(String uuid);

    /**
     * Gets the list of provider suggestions for the specified relationship type
     * (Excludes retired provider roles)
     *
     * @param relationshipType
     * @return ist of provider suggestions for the specified relationship type
     */
    public List<ProviderSuggestion> getProviderSuggestionsByRelationshipType(RelationshipType relationshipType);

    /**
     * Gets all the provider suggestions
     *
     * @param includeRetired
     * @return
     */
    public List<ProviderSuggestion> getAllProviderSuggestions(Boolean includeRetired);

    /**
     * Saves the specified provider suggestion
     *
     * @param suggestion
     * @return provider suggestion
     */
    public ProviderSuggestion saveProviderSuggestion(ProviderSuggestion suggestion);

    /**
     * Deletes the specified provider suggestion
     *
     * @param suggestion
     */
    public void deleteProviderSuggestion(ProviderSuggestion suggestion);

    /**
     * Gets the supervision suggestion referenced by the specified id
     *
     * @param id
     * @return supervision suggestion
     */
    public SupervisionSuggestion getSupervisionSuggestion(Integer id);

    /**
     * Gets the supervision suggestion referenced by the specified uuid
     *
     * @param uuid
     * @return  the supervision suggestion referenced by the specified uuid
     */
    public SupervisionSuggestion getSupervisionSuggestionByUuid(String uuid);

    /**
     * Gets the list of supervision suggestions for the specified provider role of the specified type
     * (Excludes retired provider roles)
     *
     * @param providerRole
     * @param suggestionType
     * @return ist of provider suggestions for the specified relationship type
     */
    public List<SupervisionSuggestion> getSupervisionSuggestionsByProviderRoleAndSuggestionType(ProviderRole providerRole, SupervisionSuggestionType suggestionType);


    /**
     * Gets all the supervision suggestions
     *
     * @param includeRetired
     * @return
     */
    public List<SupervisionSuggestion> getAllSupervisionSuggestions(Boolean includeRetired);

    /**
     * Saves the specified supervision suggestion
     *
     * @param suggestion
     * @return supervision suggestion
     */
    public SupervisionSuggestion saveSupervisionSuggestion(SupervisionSuggestion suggestion);

    /**
     * Deletes the specified supervision suggestion
     *
     * @param suggestion
     */
    public void deleteSupervisionSuggestion(SupervisionSuggestion suggestion);

}
