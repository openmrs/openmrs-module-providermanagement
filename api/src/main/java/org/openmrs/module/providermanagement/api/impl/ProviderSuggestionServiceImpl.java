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
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.api.ProviderSuggestionService;
import org.openmrs.module.providermanagement.api.db.ProviderManagementDAO;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.SuggestionEvaluationException;
import org.openmrs.module.providermanagement.suggestion.ProviderSuggestion;
import org.openmrs.module.providermanagement.suggestion.SuggestionEvaluator;

import java.util.*;

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
    public ProviderSuggestion getProviderSuggestion(Integer id) {
        return dao.getProviderSuggestion(id);
    }

    @Override
    public ProviderSuggestion getProviderSuggestionByUuid(String uuid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ProviderSuggestion> getProviderSuggestionsByRelationshipType(RelationshipType relationshipType) {

        if (relationshipType == null) {
            throw new APIException("relationshipType cannot be null");
        }
        else {
            return dao.getProviderSuggestionsByRelationshipType(relationshipType);
        }
    }

    @Override
    public void saveProviderSuggestion(ProviderSuggestion suggestion) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void retireProviderSuggestion(ProviderSuggestion suggestion) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void purgeProviderSuggestion(ProviderSuggestion suggestion) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Person> suggestProvidersForPatient(Patient patient, RelationshipType relationshipType)
            throws InvalidRelationshipTypeException, SuggestionEvaluationException {

        if (patient == null) {
            throw new APIException("Patient cannot be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }

        if (!Context.getService(ProviderManagementService.class).getAllProviderRoleRelationshipTypes().contains(relationshipType)) {
            throw new InvalidRelationshipTypeException("Invalid relationship type: " + relationshipType + " is not a valid provider relationship type");
        }

        // first, see if there are any custom suggestion rules
        // if not, just return all the providers that support the specified relationship type
        List<ProviderSuggestion> suggestions = getProviderSuggestionsByRelationshipType(relationshipType);
        if (suggestions == null || suggestions.size() ==0) {
            return Context.getService(ProviderManagementService.class).getProvidersByRelationshipType(relationshipType);
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
        suggestedProviders.retainAll(Context.getService(ProviderManagementService.class).getProvidersByRelationshipType(relationshipType));

        return new ArrayList<Person>(suggestedProviders);
    }
}
