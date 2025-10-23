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
import org.openmrs.Provider;
import org.openmrs.ProviderAttributeType;
import org.openmrs.ProviderRole;
import org.openmrs.RelationshipType;
import org.openmrs.module.providermanagement.ProviderRoleProviderAttributeType;
import org.openmrs.module.providermanagement.ProviderRoleRelationshipType;
import org.openmrs.module.providermanagement.ProviderRoleSuperviseeProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestionType;

import java.util.List;

/**
 *  Database methods for {@link ProviderManagementService}.
 */
public interface ProviderManagementDAO {

    // ProviderRoleProviderAttributeType

    List<ProviderRoleProviderAttributeType> getAllProviderRoleProviderAttributeTypes();
    
    List<ProviderAttributeType> getProviderAttributeTypesForProviderRole(ProviderRole providerRole);
    
    List<ProviderRole> getProviderRolesByProviderAttributeType(ProviderAttributeType providerAttributeType);
    
    ProviderRoleProviderAttributeType saveProviderRoleProviderAttributeType(ProviderRoleProviderAttributeType providerRoleProviderAttributeType);
    
    void deleteProviderRoleProviderAttributeType(ProviderRoleProviderAttributeType providerRoleProviderAttributeType);

    // ProviderRoleRelationshipType

    List<ProviderRoleRelationshipType> getAllProviderRoleRelationshipTypes();

    List<RelationshipType> getRelationshipTypesForProviderRole(ProviderRole providerRole);

    List<ProviderRole> getProviderRolesByRelationshipType(RelationshipType relationshipType);

    ProviderRoleRelationshipType saveProviderRoleRelationshipType(ProviderRoleRelationshipType providerRoleRelationshipType);

    void deleteProviderRoleRelationshipType(ProviderRoleRelationshipType providerRoleRelationshipType);

    // ProviderRoleSuperviseeProviderRole

    List<ProviderRoleSuperviseeProviderRole> getAllProviderRoleSuperviseeProviderRoles();

    List<ProviderRole> getSuperviseeProviderRolesForProviderRole(ProviderRole supervisorProviderRole);

    List<ProviderRole> getProviderRolesBySuperviseeProviderRole(ProviderRole superviseeProviderRole);

    ProviderRoleSuperviseeProviderRole saveProviderRoleSuperviseeProviderRole(ProviderRoleSuperviseeProviderRole providerRoleSuperviseeProviderRole);

    void deleteProviderRoleSuperviseeProviderRole(ProviderRoleSuperviseeProviderRole providerRoleSuperviseeProviderRole);

    // Additional provider service methods

    List<Provider> getProvidersByProviderRoles(List<ProviderRole> roles, boolean includeRetired);

    List<Provider> getProvidersByPerson(Person person, boolean includeRetired);

    // ProviderSuggestion

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
