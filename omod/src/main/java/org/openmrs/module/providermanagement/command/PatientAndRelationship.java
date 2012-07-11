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

import org.openmrs.Patient;
import org.openmrs.Relationship;

// command object for adding a patient and a relationship to the model as a unit
public class PatientAndRelationship {

    private Integer id;

    private Patient patient;

    private Relationship relationship;

    public PatientAndRelationship() {
    }

    public PatientAndRelationship(Patient patient, Relationship relationship) {
        this.patient = patient;
        this.relationship = relationship;
        this.id = relationship.getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

}
