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

package org.openmrs.module.providermanagement.controller;

import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.ProviderManagementConstants;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.web.controller.PortletController;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Overrides the standard relationship portlet controller so that the relationships
 * managed by the provider management module are not shown within this portlet
 */
public class CustomPersonRelationshipsPortletController extends PortletController {

    protected void populateModel(HttpServletRequest request, Map<String, Object> model) {

        // relationship types to display are all the relationship types MINUS the provider relationship types
        List<RelationshipType> relationshipTypes = Context.getPersonService().getAllRelationshipTypes(false);
        List<RelationshipType> providerRoleRelationshipTypes = Context.getService(ProviderManagementService.class).getAllProviderRoleRelationshipTypes(false);
        RelationshipType supervisorRelationshipType = Context.getPersonService().getRelationshipTypeByUuid(ProviderManagementConstants.SUPERVISOR_RELATIONSHIP_TYPE_UUID);
        relationshipTypes.removeAll(providerRoleRelationshipTypes);
        relationshipTypes.remove(supervisorRelationshipType);
        model.put("relationshipTypes", relationshipTypes);

        // TODO: note that we are currently NOT filtering the relationships displayed in the relationship portlet--so
        // TODO: show provider/patient relationships here; we just want to restrict the user from CREATING provider
        // TODO: see https://tickets.openmrs.org/browse/PROV-24

        // relationships to display are all relationships MINUS the provider relationships
        // first fetch the relationships currently in the model (which the relationship portlet would display by default)
        //List<Relationship> personRelationships = (List<Relationship>) model.get("personRelationships");

        // iterate through all these relationships and remove any of the provider role type
        /**
        Iterator<Relationship> i = personRelationships.iterator();
        while (i.hasNext()) {
            Relationship relationship = i.next();
            if (providerRoleRelationshipTypes.contains(relationship.getRelationshipType()) || supervisorRelationshipType.equals(relationship.getRelationshipType())) {
                i.remove();
            }
        }
         */

        // overwrite the relationship types in the model with the new types
        //model.put("personRelationships", personRelationships);
    }
}
