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
package org.openmrs.module.providermanagement.api.impl;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.api.db.ProviderManagementDAO;

import java.util.List;

/**
 * It is a default implementation of {@link ProviderManagementService}.
 */
public class ProviderManagementServiceImpl extends BaseOpenmrsService implements ProviderManagementService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private ProviderManagementDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(ProviderManagementDAO dao) {
	    this.dao = dao;
    }

    /**
     * @return the dao
     */
    public ProviderManagementDAO getDao() {
	    return dao;
    }

    @Override
    public List<ProviderRole> getAllProviderRoles() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ProviderRole> getAllProviderRoles(boolean includeRetired) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ProviderRole getProviderRole(Integer id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ProviderRole getProviderRoleByUuid(String uuid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void saveProviderRole(ProviderRole role) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void retireProviderRole(ProviderRole role, String reason) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unretireProviderRole(ProviderRole role) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void purgeProviderRole(ProviderRole role) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}