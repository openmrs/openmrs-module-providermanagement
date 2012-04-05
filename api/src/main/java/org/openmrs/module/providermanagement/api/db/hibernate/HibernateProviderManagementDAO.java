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
package org.openmrs.module.providermanagement.api.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.db.ProviderManagementDAO;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;

import java.util.List;

/**
 * It is a default implementation of  {@link ProviderManagementDAO}.
 */
public class HibernateProviderManagementDAO implements ProviderManagementDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;

    /**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }

    @Override
    public List<ProviderRole> getAllProviderRoles(boolean includeRetired) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ProviderRole.class);
        if (!includeRetired) {
            criteria.add(Restrictions.eq("retired", false));
        }
        return (List<ProviderRole>) criteria.list();
    }

    @Override
    public ProviderRole getProviderRole(Integer id) {
        return (ProviderRole) sessionFactory.getCurrentSession().get(ProviderRole.class, id);
    }

    @Override
    public ProviderRole getProviderRoleByUuid(String uuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ProviderRole.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        return (ProviderRole) criteria.uniqueResult();
    }

    @Override
    public List<ProviderRole> getProviderRolesByRelationshipType(RelationshipType relationshipType) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ProviderRole.class);
        criteria.add(Restrictions.eq("retired", false));
        criteria.createCriteria("relationshipTypes").add(Restrictions.eq("relationshipTypeId", relationshipType.getId()));
        return (List<ProviderRole>) criteria.list();
    }

    @Override
    public List<ProviderRole> getProviderRolesBySuperviseeProviderRole(ProviderRole providerRole) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ProviderRole.class);
        criteria.add(Restrictions.eq("retired", false));
        criteria.createCriteria("superviseeProviderRoles").add(Restrictions.eq("providerRoleId", providerRole.getId()));
        return (List<ProviderRole>) criteria.list();
    }

    @Override
    public void saveProviderRole(ProviderRole role) {
        sessionFactory.getCurrentSession().saveOrUpdate(role);
    }

    @Override
    public void deleteProviderRole(ProviderRole role) {
        sessionFactory.getCurrentSession().delete(role);
    }

    @Override
    public List<Provider> getProvidersByPerson(Person person, boolean includeRetired) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Provider.class);
        criteria.add(Restrictions.eq("person", person));
        if (!includeRetired) {
            criteria.add(Restrictions.eq("retired", false));
        }
        criteria.addOrder(Order.asc("providerId"));
        @SuppressWarnings("unchecked")
        List<Provider> list = criteria.list();
        return list;
    }

    @Override
    public List<Provider> getProvidersByProviderRoles(List<ProviderRole> roles, boolean includeRetired) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Provider.class);
        criteria.add(Restrictions.in("providerRole", roles));
        if (!includeRetired) {
            criteria.add(Restrictions.eq("retired", false));
        }
        criteria.addOrder(Order.asc("providerId"));
        @SuppressWarnings("unchecked")
        List<Provider> list = criteria.list();
        return list;
    }

    @Override
    public ProviderSuggestion getProviderSuggestion(Integer id) {
        return (ProviderSuggestion) sessionFactory.getCurrentSession().get(ProviderSuggestion.class, id);
    }

    @Override
    public List<ProviderSuggestion> getProviderSuggestionsByRelationshipType(RelationshipType relationshipType) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ProviderSuggestion.class);
        criteria.add(Restrictions.eq("retired", false));
        criteria.add(Restrictions.eq("relationshipType", relationshipType));
        return (List<ProviderSuggestion>) criteria.list();
    }
}