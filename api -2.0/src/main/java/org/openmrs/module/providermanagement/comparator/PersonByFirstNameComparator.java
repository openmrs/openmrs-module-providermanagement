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

package org.openmrs.module.providermanagement.comparator;

import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.util.OpenmrsUtil;

import java.util.Comparator;

public class PersonByFirstNameComparator implements Comparator<Person>{

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Person person1, Person person2) {

        return comparePersonsByName(person1, person2);
    }

    /**
     * Compares two person objects by first name
     */
    public static int comparePersonsByName(Person person1, Person person2) {

        // test for null cases (sorting them to be last in a list)
        if (person1 == null || person1.getPersonName() == null) {
            return 1;
        } else if (person2 == null || person2.getPersonName() == null) {
            return -1;
        }

        // if neither are null, do the actual comparison
        PersonName name1 = person1.getPersonName();
        PersonName name2 = person2.getPersonName();

        int ret = OpenmrsUtil.compareWithNullAsGreatest(name1.getGivenName(), name2.getGivenName());

        if (ret == 0) {
            ret = OpenmrsUtil.compareWithNullAsGreatest(name1.getMiddleName(), name2.getMiddleName());
        }

        if (ret == 0) {
            ret = OpenmrsUtil.compareWithNullAsGreatest(name1.getFamilyName(), name2.getFamilyName());
        }

        if (ret == 0) {
            ret = OpenmrsUtil.compareWithNullAsGreatest(name1.getFamilyName2(), name2.getFamilyName2());
        }

        if (ret == 0) {
            ret = OpenmrsUtil.compareWithNullAsGreatest(name1.getFamilyNamePrefix(), name2.getFamilyNamePrefix());
        }

        if (ret == 0) {
            ret = OpenmrsUtil.compareWithNullAsGreatest(name1.getFamilyNameSuffix(), name2.getFamilyNameSuffix());
        }

        return ret;
    }
}
