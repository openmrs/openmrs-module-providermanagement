package org.openmrs.module.providermanagement.relationship;

import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;

/**
 *  Utility domain object for use by the coreapps providermanagement functionality.
 *  The object holds information that describes the relationship to a Provider.
 */
public class ProviderPersonRelationship {

    Person person = null;
    String identifier = null;
    Integer objectId = null;
    Relationship relationship= null;
    RelationshipType relationshipType = null;

    public ProviderPersonRelationship() {}

    public ProviderPersonRelationship(Person person,
                                      String identifier,
                                      Integer objectId,
                                      Relationship relationship,
                                      RelationshipType relationshipType) {
        this.person = person;
        this.identifier = identifier;
        this.objectId = objectId;
        this.relationship = relationship;
        this.relationshipType = relationshipType;
    }

}
