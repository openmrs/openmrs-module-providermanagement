/**
 * The contents of this file are subject to the OpenMRS License
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
import org.openmrs.Provider;
import org.openmrs.ProviderAttributeType;
import org.openmrs.ProviderRole;
import org.openmrs.RelationshipType;
import org.openmrs.module.providermanagement.ProviderManagementProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestionType;

import java.util.List;

/**
 *  Database methods for {@link ProviderManagementService}.
 */
public interface ProviderManagementDAO {

    ProviderManagementProviderRole getProviderRole(Integer providerRoleId);

    ProviderManagementProviderRole getProviderRoleByUuid(String uuid);

    List<ProviderManagementProviderRole> getAllProviderRoles(boolean includeRetired);

    ProviderManagementProviderRole saveProviderRole(ProviderManagementProviderRole providerManagementProviderRole);

    void deleteProviderRole(ProviderManagementProviderRole providerManagementProviderRole);

    ProviderManagementProviderRole getProviderRole(Provider provider);

    List<ProviderManagementProviderRole> getProviderRolesByRelationshipType(RelationshipType relationshipType);

    List<ProviderManagementProviderRole> getProviderRolesBySuperviseeProviderRole(ProviderRole providerRole);

    List<Provider> getProvidersByProviderRoles(List<ProviderRole> roles, boolean includeRetired);

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
    List<Person> getProviders(String name, String identifier, PersonAddress personAddress, PersonAttribute personAttribute, List<ProviderRole> providerRoles, Boolean includeRetired);

    List<Provider> getProvidersByPerson(Person person, boolean includeRetired);

    /**
     * Gets the provider suggestion referenced by the specified id
     *
     * @param id
     * @return providerSuggestion
     */
    ProviderSuggestion getProviderSuggestion(Integer id);

    /**
     * Gets the provider suggestion referenced by the specified uuid
     *
     * @param uuid
     * @return  the provider suggestion referenced by the specified uuid
     */
    ProviderSuggestion getProviderSuggestionByUuid(String uuid);

    /**
     * Gets the list of provider suggestions for the specified relationship type
     * (Excludes retired provider roles)
     *
     * @param relationshipType
     * @return ist of provider suggestions for the specified relationship type
     */
    List<ProviderSuggestion> getProviderSuggestionsByRelationshipType(RelationshipType relationshipType);

    /**
     * Gets all the provider suggestions
     *
     * @param includeRetired
     * @return
     */
    List<ProviderSuggestion> getAllProviderSuggestions(Boolean includeRetired);

    /**
     * Saves the specified provider suggestion
     *
     * @param suggestion
     * @return provider suggestion
     */
    ProviderSuggestion saveProviderSuggestion(ProviderSuggestion suggestion);

    /**
     * Deletes the specified provider suggestion
     *
     * @param suggestion
     */
    void deleteProviderSuggestion(ProviderSuggestion suggestion);

    /**
     * Gets the supervision suggestion referenced by the specified id
     *
     * @param id
     * @return supervision suggestion
     */
    SupervisionSuggestion getSupervisionSuggestion(Integer id);

    /**
     * Gets the supervision suggestion referenced by the specified uuid
     *
     * @param uuid
     * @return  the supervision suggestion referenced by the specified uuid
     */
    SupervisionSuggestion getSupervisionSuggestionByUuid(String uuid);

    /**
     * Gets the list of supervision suggestions for the specified provider role of the specified type
     * (Excludes retired provider roles)
     *
     * @param providerRole
     * @param suggestionType
     * @return ist of provider suggestions for the specified relationship type
     */
    List<SupervisionSuggestion> getSupervisionSuggestionsByProviderRoleAndSuggestionType(ProviderRole providerRole, SupervisionSuggestionType suggestionType);


    /**
     * Gets all the supervision suggestions
     *
     * @param includeRetired
     * @return
     */
    List<SupervisionSuggestion> getAllSupervisionSuggestions(Boolean includeRetired);

    /**
     * Saves the specified supervision suggestion
     *
     * @param suggestion
     * @return supervision suggestion
     */
    SupervisionSuggestion saveSupervisionSuggestion(SupervisionSuggestion suggestion);

    /**
     * Deletes the specified supervision suggestion
     *
     * @param suggestion
     */
    void deleteSupervisionSuggestion(SupervisionSuggestion suggestion);

}
