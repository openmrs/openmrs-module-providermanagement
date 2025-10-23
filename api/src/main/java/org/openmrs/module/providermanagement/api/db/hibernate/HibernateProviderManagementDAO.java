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

import lombok.Setter;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.ProviderAttributeType;
import org.openmrs.ProviderRole;
import org.openmrs.RelationshipType;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.providermanagement.ProviderRoleProviderAttributeType;
import org.openmrs.module.providermanagement.ProviderRoleRelationshipType;
import org.openmrs.module.providermanagement.ProviderRoleSuperviseeProviderRole;
import org.openmrs.module.providermanagement.api.db.ProviderManagementDAO;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestionType;

import java.util.List;

/**
 * It is a default implementation of  {@link ProviderManagementDAO}.
 */
public class HibernateProviderManagementDAO implements ProviderManagementDAO {
	
    @Setter
	private DbSessionFactory sessionFactory;

    DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    <T> List<T> list(Criteria criteria, Class<T> type) {
        return (List<T>) criteria.list();
    }

    @Override
    public List<ProviderRoleProviderAttributeType> getAllProviderRoleProviderAttributeTypes() {
        Criteria criteria = getSession().createCriteria(ProviderRoleProviderAttributeType.class);
        return list(criteria, ProviderRoleProviderAttributeType.class);
    }

    @Override
    public List<ProviderAttributeType> getProviderAttributeTypesForProviderRole(ProviderRole providerRole) {
        Criteria criteria = getSession().createCriteria(ProviderRoleProviderAttributeType.class);
        criteria.setProjection(Property.forName("providerAttributeType"));
        criteria.add(Restrictions.eq("providerRole", providerRole));
        return list(criteria, ProviderAttributeType.class);
    }

    @Override
    public List<ProviderRole> getProviderRolesByProviderAttributeType(ProviderAttributeType providerAttributeType) {
        Criteria criteria = getSession().createCriteria(ProviderRoleProviderAttributeType.class);
        criteria.setProjection(Property.forName("providerRole"));
        criteria.add(Restrictions.eq("providerAttributeType", providerAttributeType));
        return list(criteria, ProviderRole.class);
    }

    @Override
    public ProviderRoleProviderAttributeType saveProviderRoleProviderAttributeType(ProviderRoleProviderAttributeType providerRoleProviderAttributeType) {
        getSession().saveOrUpdate(providerRoleProviderAttributeType);
        return providerRoleProviderAttributeType;
    }

    @Override
    public void deleteProviderRoleProviderAttributeType(ProviderRoleProviderAttributeType providerRoleProviderAttributeType) {
        getSession().delete(providerRoleProviderAttributeType);
    }

    @Override
    public List<ProviderRoleRelationshipType> getAllProviderRoleRelationshipTypes() {
        Criteria criteria = getSession().createCriteria(ProviderRoleRelationshipType.class);
        return list(criteria, ProviderRoleRelationshipType.class);
    }

    @Override
    public List<RelationshipType> getRelationshipTypesForProviderRole(ProviderRole providerRole) {
        Criteria criteria = getSession().createCriteria(ProviderRoleRelationshipType.class);
        criteria.setProjection(Property.forName("relationshipType"));
        criteria.add(Restrictions.eq("providerRole", providerRole));
        return list(criteria, RelationshipType.class);
    }

    @Override
    public List<ProviderRole> getProviderRolesByRelationshipType(RelationshipType relationshipType) {
        Criteria criteria = getSession().createCriteria(ProviderRoleRelationshipType.class);
        criteria.setProjection(Property.forName("providerRole"));
        criteria.add(Restrictions.eq("relationshipType", relationshipType));
        return list(criteria, ProviderRole.class);
    }

    @Override
    public ProviderRoleRelationshipType saveProviderRoleRelationshipType(ProviderRoleRelationshipType providerRoleRelationshipType) {
        getSession().saveOrUpdate(providerRoleRelationshipType);
        return providerRoleRelationshipType;
    }

    @Override
    public void deleteProviderRoleRelationshipType(ProviderRoleRelationshipType providerRoleRelationshipType) {
        getSession().delete(providerRoleRelationshipType);
    }

    @Override
    public List<ProviderRoleSuperviseeProviderRole> getAllProviderRoleSuperviseeProviderRoles() {
        Criteria criteria = getSession().createCriteria(ProviderRoleSuperviseeProviderRole.class);
        return list(criteria, ProviderRoleSuperviseeProviderRole.class);
    }

    @Override
    public List<ProviderRole> getSuperviseeProviderRolesForProviderRole(ProviderRole supervisorProviderRole) {
        Criteria criteria = getSession().createCriteria(ProviderRoleSuperviseeProviderRole.class);
        criteria.setProjection(Property.forName("superviseeProviderRole"));
        //criteria.add(Restrictions.eq("providerRole", supervisorProviderRole));
        return list(criteria, ProviderRole.class);
    }

    @Override
    public List<ProviderRole> getProviderRolesBySuperviseeProviderRole(ProviderRole providerRole) {
        Criteria criteria = getSession().createCriteria(ProviderRoleSuperviseeProviderRole.class);
        criteria.setProjection(Property.forName("providerRole"));
        criteria.add(Restrictions.eq("superviseeProviderRole", providerRole));
        return list(criteria, ProviderRole.class);
    }

    @Override
    public ProviderRoleSuperviseeProviderRole saveProviderRoleSuperviseeProviderRole(ProviderRoleSuperviseeProviderRole providerRoleSuperviseeProviderRole) {
        getSession().saveOrUpdate(providerRoleSuperviseeProviderRole);
        return providerRoleSuperviseeProviderRole;
    }

    @Override
    public void deleteProviderRoleSuperviseeProviderRole(ProviderRoleSuperviseeProviderRole providerRoleSuperviseeProviderRole) {
        getSession().delete(providerRoleSuperviseeProviderRole);
    }

    @Override
    public List<Provider> getProvidersByProviderRoles(List<ProviderRole> roles, boolean includeRetired) {
        Criteria criteria = getSession().createCriteria(Provider.class);
        criteria.add(Restrictions.in("providerRole", roles));
        if (!includeRetired) {
            criteria.add(Restrictions.eq("retired", false));
        }
        criteria.addOrder(Order.asc("providerId"));
        return list(criteria, Provider.class);
    }

    @Override
    public List<Provider> getProvidersByPerson(Person person, boolean includeRetired) {
        Criteria criteria = getSession().createCriteria(Provider.class);
        criteria.add(Restrictions.eq("person", person));
        if (!includeRetired) {
            criteria.add(Restrictions.eq("retired", false));
        }
        criteria.addOrder(Order.asc("providerId"));
        return list(criteria, Provider.class);
    }

    @Override
    public ProviderSuggestion getProviderSuggestion(Integer id) {
        return (ProviderSuggestion) getSession().get(ProviderSuggestion.class, id);
    }

    @Override
    public ProviderSuggestion getProviderSuggestionByUuid(String uuid) {
        Criteria criteria = getSession().createCriteria(ProviderSuggestion.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        return (ProviderSuggestion) criteria.uniqueResult();
    }

    @Override
    public List<ProviderSuggestion> getProviderSuggestionsByRelationshipType(RelationshipType relationshipType) {
        Criteria criteria = getSession().createCriteria(ProviderSuggestion.class);
        criteria.add(Restrictions.eq("retired", false));
        criteria.add(Restrictions.eq("relationshipType", relationshipType));
        return list(criteria, ProviderSuggestion.class);
    }

    @Override
    public List<ProviderSuggestion> getAllProviderSuggestions(Boolean includeRetired) {
        Criteria criteria = getSession().createCriteria(ProviderSuggestion.class);
        if (!includeRetired) {
            criteria.add(Restrictions.eq("retired", false));
        }
        return list(criteria, ProviderSuggestion.class);
    }

    @Override
    public ProviderSuggestion saveProviderSuggestion(ProviderSuggestion suggestion) {
        getSession().saveOrUpdate(suggestion);
        return suggestion;
    }

    @Override
    public void deleteProviderSuggestion(ProviderSuggestion suggestion) {
        getSession().delete(suggestion);
    }

    @Override
    public SupervisionSuggestion getSupervisionSuggestion(Integer id) {
        return (SupervisionSuggestion) getSession().get(SupervisionSuggestion.class, id);
    }

    @Override
    public SupervisionSuggestion getSupervisionSuggestionByUuid(String uuid) {
        Criteria criteria = getSession().createCriteria(SupervisionSuggestion.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        return (SupervisionSuggestion) criteria.uniqueResult();
    }

    @Override
    public List<SupervisionSuggestion> getSupervisionSuggestionsByProviderRoleAndSuggestionType(ProviderRole providerRole, SupervisionSuggestionType suggestionType) {
        Criteria criteria = getSession().createCriteria(SupervisionSuggestion.class);
        criteria.add(Restrictions.eq("retired", false));
        criteria.add(Restrictions.eq("providerRole", providerRole));
        if (suggestionType != null) {
            criteria.add(Restrictions.eq("suggestionType", suggestionType));
        }
        return list(criteria, SupervisionSuggestion.class);
    }

    @Override
    public List<SupervisionSuggestion> getAllSupervisionSuggestions(Boolean includeRetired) {
        Criteria criteria = getSession().createCriteria(SupervisionSuggestion.class);
        if (!includeRetired) {
            criteria.add(Restrictions.eq("retired", false));
        }
        return list(criteria, SupervisionSuggestion.class);
    }

    @Override
    public SupervisionSuggestion saveSupervisionSuggestion(SupervisionSuggestion suggestion) {
        getSession().saveOrUpdate(suggestion);
        return suggestion;
    }

    @Override
    public void deleteSupervisionSuggestion(SupervisionSuggestion suggestion) {
        getSession().delete(suggestion);
    }
}