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

import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;

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
     * Saves/updates a provider role
     *
     * @param role the provider role to save
     */
    public void saveProviderRole(ProviderRole role);

    /**
     * Deletes a provider role
     *
     * @param role the provider role to delete
     */
    public void deleteProviderRole(ProviderRole role);


}
