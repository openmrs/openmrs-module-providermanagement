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

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.api.APIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.providermanagement.ProviderManagementConstants;
import org.openmrs.module.providermanagement.ProviderManagementUtils;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.api.db.ProviderManagementDAO;
import org.openmrs.module.providermanagement.exception.PatientAlreadyAssignedToProviderException;
import org.openmrs.module.providermanagement.exception.PatientNotAssignedToProviderException;
import org.openmrs.module.providermanagement.exception.ProviderDoesNotSupportRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.ProviderNotAssociatedWithPersonException;

import java.util.*;

/**
 * It is a default implementation of {@link ProviderManagementService}.
 */
public class ProviderManagementServiceImpl extends BaseOpenmrsService implements ProviderManagementService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private ProviderManagementDAO dao;
    
    private static ProviderAttributeType providerRoleAttributeType = null;
	
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
    public ProviderAttributeType getProviderRoleAttributeType() {
        // TODO: error handling?

        if (providerRoleAttributeType == null) {
            providerRoleAttributeType = Context.getProviderService().getProviderAttributeTypeByUuid(ProviderManagementConstants.PROVIDER_ROLE_ATTRIBUTE_TYPE_UUID);
        }

        return providerRoleAttributeType;
    }

    @Override
    public List<ProviderRole> getAllProviderRoles() {
        return dao.getAllProviderRoles(false);
    }

    @Override
    public List<ProviderRole> getAllProviderRoles(boolean includeRetired) {
        return dao.getAllProviderRoles(includeRetired);
    }

    @Override
    public ProviderRole getProviderRole(Integer id) {
        return dao.getProviderRole(id);
    }

    @Override
    public ProviderRole getProviderRoleByUuid(String uuid) {
        return dao.getProviderRoleByUuid(uuid);
    }

    @Override
    public List<ProviderRole> getProviderRolesByRelationshipType(RelationshipType relationshipType) {
        if (relationshipType == null) {
            throw new APIException("relationshipType cannot be null");
        }
        else {
            return dao.getProviderRolesByRelationshipType(relationshipType);
        }
    }

    @Override
    public List<ProviderRole> getProviderRolesBySuperviseeProviderRole(ProviderRole providerRole) {
        if (providerRole == null) {
            throw new APIException("providerRole cannot be null");
        }
        else {
            return dao.getProviderRolesBySuperviseeProviderRole(providerRole);
        }
    }

    @Override
    public void saveProviderRole(ProviderRole role) {
        dao.saveProviderRole(role);
    }

    @Override
    public void retireProviderRole(ProviderRole role, String reason) {
        // BaseRetireHandler handles retiring the object
        dao.saveProviderRole(role);
    }

    @Override
    public void unretireProviderRole(ProviderRole role) {
        // BaseUnretireHandler handles unretiring the object
        dao.saveProviderRole(role);
    }

    @Override
    public void purgeProviderRole(ProviderRole role) {
        dao.deleteProviderRole(role);
    }

    @Override
    public void setProviderRole(Provider provider, ProviderRole role) {
        // TODO: make sure this syncs properly!

        if (provider == null) {
            throw new APIException("Cannot set provider role: provider is null");
        }
        else {
            // first, void the existing provider role for this provider (if it existing)
            List<ProviderAttribute> attrs = provider.getActiveAttributes(getProviderRoleAttributeType());
            if (attrs.size() > 1) {
                throw new APIException("Provider should never have more than one Provider Role");
            }
            else if (attrs.size() == 1) {
                ProviderAttribute roleAttributeToVoid = attrs.get(0);
                roleAttributeToVoid.setVoided(true);
                roleAttributeToVoid.setVoidedBy(Context.getAuthenticatedUser());
                roleAttributeToVoid.setVoidReason("voided while setting a new provider role");
            }

            if (role != null) {
                // now create the new attribute (if one has been specified)
                ProviderAttribute providerRoleAttribute = new ProviderAttribute();
                providerRoleAttribute.setAttributeType(getProviderRoleAttributeType());
                providerRoleAttribute.setValue(role);
                provider.setAttribute(providerRoleAttribute);
            }

            // save the provider
            Context.getProviderService().saveProvider(provider);
        }
    }

    @Override
    public List<Provider> getProvidersByRoles(List<ProviderRole> roles) {

        // TODO: this won't distinguish between retired and unretired providers until TRUNK-3170 is implemented

        // not allowed to pass null or empty set here
        if (roles == null || roles.isEmpty()) {
            throw new APIException("Roles cannot be null or empty");
        }

        // TODO: figure out if we want to sort results here

        List<Provider> providers = new ArrayList<Provider>();

        // iterate through each role and fetch the matching providers for each role
        // note that since a provider can only have one role, we don't
        // have to worry about duplicates, ie. fetching the same provider twice

        // TODO: but duplicate Persons could be a possibility...?

        for (ProviderRole role : roles) {
            // create the attribute type to add to the query
            Map<ProviderAttributeType, Object> attributeValueMap = new HashMap<ProviderAttributeType, Object>();
            attributeValueMap.put(getProviderRoleAttributeType(), role);
            // find all providers with that role
            providers.addAll(Context.getProviderService().getProviders(null, null, null, attributeValueMap));
        }

        return providers;
    }

    @Override
    public List<Provider> getProvidersByRole(ProviderRole role) {

        // TODO: this won't distinguish between retired and unretired providers until TRUNK-3170 is implemented

        // not allowed to pass null here
        if (role == null) {
            throw new APIException("Role cannot be null");
        }

        List<ProviderRole> roles = new ArrayList<ProviderRole>();
        roles.add(role);
        return getProvidersByRoles(roles);
    }

    @Override
    public List<Provider> getProvidersByRelationshipType(RelationshipType relationshipType) {

        if (relationshipType == null) {
            throw new  APIException("Relationship type cannot be null");
        }

        // first fetch the roles that support this relationship type, then fetch all the providers with those roles
        List<ProviderRole> providerRoles = getProviderRolesByRelationshipType(relationshipType);
        if (providerRoles == null || providerRoles.size() == 0) {
            return new ArrayList<Provider>();  // just return an empty list
        }
        else {
            return getProvidersByRoles(providerRoles);
        }
    }

    @Override
    public List<Provider> getProvidersBySuperviseeProviderRole(ProviderRole role) {
       
        if (role == null) {
            throw new APIException("Provider role cannot be null");
        }
        
        // first fetch the roles that can supervise this relationship type, then fetch all providers with those roles
        List<ProviderRole> providerRoles = getProviderRolesBySuperviseeProviderRole(role);
        if (providerRoles == null || providerRoles.size() == 0) {
            return new ArrayList<Provider>();  // just return an empty list
        }
        else {
            return getProvidersByRoles(providerRoles);
        }
    }

    @Override
    public void assignPatientToProvider(Patient patient, Provider provider, RelationshipType relationshipType, Date date)
            throws ProviderDoesNotSupportRelationshipTypeException, ProviderNotAssociatedWithPersonException,
            PatientAlreadyAssignedToProviderException {

        if (patient == null) {
            throw new APIException("Patient cannot be null");
        }

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }

        if (patient.isVoided()) {
            throw new APIException("Patient cannot be voided");
        }

        if (provider.getPerson() == null) {
           throw new ProviderNotAssociatedWithPersonException("Provider " + provider + " is not associated with a person");
        }

       if (!ProviderManagementUtils.supportsRelationshipType(provider, relationshipType)) {
           throw new ProviderDoesNotSupportRelationshipTypeException(provider + " cannot support " + relationshipType);
       }

        // use current date if no date specified
        if (date == null) {
            date = new Date();
        }

        // TODO: what about voided relationships?  does the get relationships method ignore voided?

        // test to mark sure the relationship doesn't already exist
        List<Relationship> relationships = Context.getPersonService().getRelationships(provider.getPerson(), patient, relationshipType, date);
        if (relationships != null && relationships.size() > 0) {
            throw new PatientAlreadyAssignedToProviderException("Provider " + provider + " is already assigned to " + patient + " with a " + relationshipType + "relationship");
        }
        
        // go ahead and create the relationship
        Relationship relationship = new Relationship();
        relationship.setPersonA(provider.getPerson());
        relationship.setPersonB(patient);
        relationship.setRelationshipType(relationshipType);
        relationship.setStartDate(ProviderManagementUtils.clearTimeComponent(date));
        Context.getPersonService().saveRelationship(relationship);
    }

    @Override
    public void assignPatientToProvider(Patient patient, Provider provider, RelationshipType relationshipType)
            throws ProviderDoesNotSupportRelationshipTypeException, ProviderNotAssociatedWithPersonException,
            PatientAlreadyAssignedToProviderException {
        assignPatientToProvider(patient, provider, relationshipType, new Date());
    }

    @Override
    public void unassignPatientFromProvider(Patient patient, Provider provider, RelationshipType relationshipType, Date date)
        throws ProviderNotAssociatedWithPersonException, ProviderDoesNotSupportRelationshipTypeException,
        PatientNotAssignedToProviderException {

        if (patient == null) {
            throw new APIException("Patient cannot be null");
        }

        if (provider == null) {
            throw new APIException("Provider cannot be null");
        }

        if (relationshipType == null) {
            throw new APIException("Relationship type cannot be null");
        }

        if (patient.isVoided()) {
            throw new APIException("Patient cannot be voided");
        }

        if (provider.getPerson() == null) {
            throw new ProviderNotAssociatedWithPersonException("Provider " + provider + " is not associated with a person");
        }

        if (!ProviderManagementUtils.supportsRelationshipType(provider, relationshipType)) {
            throw new ProviderDoesNotSupportRelationshipTypeException(provider + " cannot support " + relationshipType);
        }

        // use current date if no date specified
        if (date == null) {
            date = new Date();
        }

        // find the existing relationship
        List<Relationship> relationships = Context.getPersonService().getRelationships(provider.getPerson(), patient, relationshipType, date);
        if (relationships == null || relationships.size() == 0) {
            throw new PatientNotAssignedToProviderException("Provider " + provider + " is not assigned to " + patient + " with a " + relationshipType + " relationship");
        }
        if (relationships.size() > 1) {
            // TODO: handle this better? maybe void all but one automatically?
            throw new APIException("Duplicate " + relationshipType + " between " + provider + " and " + patient);
        }

        // go ahead and set the end date of the relationship
        Relationship relationship = relationships.get(0);
        relationship.setEndDate(ProviderManagementUtils.clearTimeComponent(date));
        Context.getPersonService().saveRelationship(relationship);
    }

    @Override
    public void unassignPatientFromProvider(Patient patient, Provider provider, RelationshipType relationshipType) throws ProviderNotAssociatedWithPersonException, ProviderDoesNotSupportRelationshipTypeException, PatientNotAssignedToProviderException {
        unassignPatientFromProvider(patient, provider, relationshipType, new Date());
    }
}