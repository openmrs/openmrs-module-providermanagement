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

package org.openmrs.module.providermanagement.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.api.ProviderSuggestionService;
import org.openmrs.module.providermanagement.api.db.ProviderManagementDAO;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.exception.SuggestionEvaluationException;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.module.providermanagement.suggestion.SuggestionEvaluator;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestion;
import org.openmrs.module.providermanagement.suggestion.SupervisionSuggestionType;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProviderSuggestionServiceImpl implements ProviderSuggestionService {

    protected final Log log = LogFactory.getLog(this.getClass());

    private ProviderManagementDAO dao;

    public ProviderManagementDAO getDao() {
        return dao;
    }

    public void setDao(ProviderManagementDAO dao) {
        this.dao = dao;
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderSuggestion getProviderSuggestion(Integer id) {
        return dao.getProviderSuggestion(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderSuggestion getProviderSuggestionByUuid(String uuid) {
       return dao.getProviderSuggestionByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderSuggestion> getProviderSuggestionsByRelationshipType(RelationshipType relationshipType) {

        if (relationshipType == null) {
            throw new APIException("relationshipType cannot be null");
        }
        else {
            return dao.getProviderSuggestionsByRelationshipType(relationshipType);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderSuggestion> getAllProviderSuggestions(Boolean includeRetired) {
        return dao.getAllProviderSuggestions(includeRetired);
    }

    @Override
    @Transactional
    public ProviderSuggestion saveProviderSuggestion(ProviderSuggestion suggestion) {
        return dao.saveProviderSuggestion(suggestion);
    }

    @Override
    @Transactional
    public void retireProviderSuggestion(ProviderSuggestion suggestion, String reason) {
        // BaseRetireHandler handles retiring the object
        dao.saveProviderSuggestion(suggestion);
    }

    @Override
    @Transactional
    public void unretireProviderSuggestion(ProviderSuggestion suggestion) {
        // BaseRetireHandler handles retiring the object
        dao.saveProviderSuggestion(suggestion);
    }

    @Override
    @Transactional
    public void purgeProviderSuggestion(ProviderSuggestion suggestion) {
        dao.deleteProviderSuggestion(suggestion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> suggestProvidersForPatient(Patient patient, RelationshipType relationshipType)
            throws InvalidRelationshipTypeException, SuggestionEvaluationException {

        if (patient == null) {
            throw new APIException("Patient cannot be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }

        if (!Context.getService(ProviderManagementService.class).getAllProviderRoleRelationshipTypes(false).contains(relationshipType)) {
            throw new InvalidRelationshipTypeException("Invalid relationship type: " + relationshipType + " is not a valid provider relationship type");
        }

        // first, see if there are any suggestion rules if not, just return null
        List<ProviderSuggestion> suggestions = getProviderSuggestionsByRelationshipType(relationshipType);
        if (suggestions == null || suggestions.size() ==0) {
            return null;
        }

        // otherwise, get all the providers that match the suggestion rules
        Collection<Person> suggestedProviders = new HashSet<Person>();
        for (ProviderSuggestion suggestion : suggestions) {
            try {
                SuggestionEvaluator evaluator = suggestion.instantiateEvaluator();
                Set<Person> p = evaluator.evaluate(suggestion, patient, relationshipType);
                 if (p != null) {
                    // note that we are doing union, not intersection, here if there are multiple rules
                    suggestedProviders.addAll(p);
                }
            }
            catch (Exception e) {
                throw new SuggestionEvaluationException("Unable to evaluate suggestion " + suggestion, e);
            }
        }

        // only keep those providers that are valid (ie, support the specified relationship type
        // TODO: might want to test the performance of this
        suggestedProviders.retainAll(Context.getService(ProviderManagementService.class).getProvidersAsPersonsByRelationshipType(relationshipType));

        // finally, remove any providers that are already assigned to this patient
        suggestedProviders.removeAll(Context.getService(ProviderManagementService.class).getProvidersAsPersonsForPatient(patient, relationshipType, new Date()));

        return new ArrayList<Person>(suggestedProviders);
    }

    @Override
    @Transactional(readOnly = true)
    public SupervisionSuggestion getSupervisionSuggestion(Integer id) {
        return dao.getSupervisionSuggestion(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SupervisionSuggestion getSupervisionSuggestionByUuid(String uuid) {
        return dao.getSupervisionSuggestionByUuid(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupervisionSuggestion> getSupervisionSuggestionsByProviderRoleAndSuggestionType(ProviderRole providerRole, SupervisionSuggestionType suggestionType) {

        if (providerRole == null) {
            throw new APIException("providerRole cannot be null");
        }

        return dao.getSupervisionSuggestionsByProviderRoleAndSuggestionType(providerRole, suggestionType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupervisionSuggestion> getSupervisionSuggestionsByProviderRole(ProviderRole providerRole) {
        return getSupervisionSuggestionsByProviderRoleAndSuggestionType(providerRole, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupervisionSuggestion> getAllSupervisionSuggestions(Boolean includeRetired) {
        return dao.getAllSupervisionSuggestions(includeRetired);
    }

    @Override
    @Transactional
    public SupervisionSuggestion saveSupervisionSuggestion(SupervisionSuggestion suggestion) {
        return dao.saveSupervisionSuggestion(suggestion);
    }

    @Override
    @Transactional
    public void retireSupervisionSuggestion(SupervisionSuggestion suggestion, String reason) {
        // BaseRetireHandler handles retiring the object
        dao.saveSupervisionSuggestion(suggestion);
    }

    @Override
    @Transactional
    public void unretireSupervisionSuggestion(SupervisionSuggestion suggestion) {
        // BaseRetireHandler handles retiring the object
        dao.saveSupervisionSuggestion(suggestion);
    }

    @Override
    @Transactional
    public void purgeSupervisionSuggestion(SupervisionSuggestion suggestion) {
        dao.deleteSupervisionSuggestion(suggestion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> suggestSupervisorsForProvider(Person provider)
            throws PersonIsNotProviderException, SuggestionEvaluationException {
        return suggestSupervisionForProviderHelper(provider, SupervisionSuggestionType.SUPERVISOR_SUGGESTION);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> suggestSuperviseesForProvider(Person provider)
            throws PersonIsNotProviderException, SuggestionEvaluationException {
        return suggestSupervisionForProviderHelper(provider, SupervisionSuggestionType.SUPERVISEE_SUGGESTION);
    }

    private List<Person> suggestSupervisionForProviderHelper(Person provider, SupervisionSuggestionType type)      throws PersonIsNotProviderException, SuggestionEvaluationException {

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        // fail if the person is not a provider
        if (!Context.getService(ProviderManagementService.class).isProvider(provider)) {
            throw new PersonIsNotProviderException(provider + " is not a provider");
        }

        // first, get all the roles for this provider
        List<ProviderRole> roles = Context.getService(ProviderManagementService.class).getProviderRoles(provider);

        // if the provider has no roles, return null
        if (roles == null || roles.size() == 0) {
            return null;
        }

        // now get all the roles that this provider can supervise or be supervisors by (depending on type)
        List<ProviderRole> validRoles;

        if (type.equals(SupervisionSuggestionType.SUPERVISEE_SUGGESTION)) {
            validRoles = Context.getService(ProviderManagementService.class).getProviderRolesThatProviderCanSupervise(provider);
        }
        else {
            validRoles = Context.getService(ProviderManagementService.class).getProviderRolesThatCanSuperviseThisProvider(provider);
        }

        // get any suggestions based on the provider roles
        Set<SupervisionSuggestion> suggestions = new HashSet<SupervisionSuggestion>();
        for (ProviderRole role : roles) {
            List<SupervisionSuggestion> s = getSupervisionSuggestionsByProviderRoleAndSuggestionType(role, type);
            if (s != null && s.size() > 0) {
                suggestions.addAll(s);
            }
        }

        // if there are no suggestions, or no valid roles, just return null
        if (suggestions.size() == 0 || validRoles == null || validRoles.size() == 0) {
            return null;
        }

        // otherwise, get all the providers that match the suggestion rules
        Collection<Person> suggestedProviders = new HashSet<Person>();
        for (SupervisionSuggestion suggestion : suggestions) {
            try {
                SuggestionEvaluator evaluator = suggestion.instantiateEvaluator();
                Set<Person> p = evaluator.evaluate(suggestion, provider);
                if (p != null) {
                    // note that we are doing union, not intersection, here if there are multiple rules
                    suggestedProviders.addAll(p);
                }
            }
            catch (Exception e) {
                throw new SuggestionEvaluationException("Unable to evaluate suggestion " + suggestion, e);
            }
        }

        // only keep providers that are valid for this provider to supervise or be supervised by
        suggestedProviders.retainAll(Context.getService(ProviderManagementService.class).getProvidersAsPersonsByRoles(validRoles));

        // finally, remove any providers that this provider is already supervising or being supervised by
        if (type.equals(SupervisionSuggestionType.SUPERVISEE_SUGGESTION)) {
            suggestedProviders.removeAll(Context.getService(ProviderManagementService.class).getSuperviseesForSupervisor(provider,new Date()));
        }
        else {
            suggestedProviders.removeAll(Context.getService(ProviderManagementService.class).getSupervisorsForProvider(provider, new Date()));
        }

        // return the result set
        return new ArrayList<Person>(suggestedProviders);
    }
}
