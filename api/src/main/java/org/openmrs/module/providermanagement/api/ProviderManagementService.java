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
package org.openmrs.module.providermanagement.api;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.providermanagement.ProviderRole;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Provider Management Service
 */
@Transactional
public interface ProviderManagementService extends OpenmrsService {

    // TODO: add permissions

	/*
	 * Basic methods for operating on provider roles
	 */

    /**
     * Gets all unretired provider roles
     * @return list of all unretired provider roles
     */
    @Transactional(readOnly = true)
    public List<ProviderRole> getAllProviderRoles();

    /**
     * Gets all Provider Roles in the database
     *
     * @param includeRetired whether or not to include retired providers
     * @return list of all provider roles in the system
     */
    @Transactional(readOnly = true)
    public List<ProviderRole> getAllProviderRoles(boolean includeRetired);

    /**
     * Gets the provider role referenced by the specified id
     *
     * @param id
     * @return providerRole
     */
    @Transactional(readOnly = true)
    public ProviderRole getProviderRole(Integer id);

    /**
     * Gets the provider role referenced by the specified uui
     *
     * @param uuid
     * @return providerRole
     */
    @Transactional(readOnly = true)
    public ProviderRole getProviderRoleByUuid(String uuid);

    /**
     * Saves/updates a provider role
     *
     * @param role the provider role to save
     */
    @Transactional
    public void saveProviderRole(ProviderRole role);

    /**
     * Retires a provider role
     * @param role the role to retire
     * @param reason the reason the role is being retired
     */
    @Transactional
    public void retireProviderRole(ProviderRole role, String reason);

    /**
     * Unretires a provider role
     * @param role the role to unretire
     */
    @Transactional
    public void unretireProviderRole(ProviderRole role);
    
    /**
     * Deletes a provider role
     *
     * @param role the provider role to delete
     */
    @Transactional
    public void purgeProviderRole(ProviderRole role);

}