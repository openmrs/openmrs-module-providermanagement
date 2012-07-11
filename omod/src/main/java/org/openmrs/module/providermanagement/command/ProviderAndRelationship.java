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

package org.openmrs.module.providermanagement.command;

import org.openmrs.Relationship;
import org.openmrs.module.providermanagement.Provider;


// command object for adding a supervisee and a relationship to the model as a unit
public class ProviderAndRelationship {

    private Integer id;

    private Provider provider;

    private Relationship relationship;

    public ProviderAndRelationship() {
    }

    public ProviderAndRelationship(Provider provider, Relationship relationship) {
        this.provider = provider;
        this.relationship = relationship;
        this.id = relationship.getId();
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }
}
