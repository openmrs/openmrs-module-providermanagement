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

package org.openmrs.module.providermanagement.rules;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

public class Rule {

    private String criteria;

    /* The evaluator to use when evaluating the rule */
    /* (Name of the class, stored as a string) */
    private String evaluator;

    public Rule() {

    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(String evaluator) {
        this.evaluator = evaluator;
    }

    public RuleEvaluator instantiateEvaluator() {
        if (evaluator != null) {
            try {
                return (RuleEvaluator) Context.loadClass(evaluator).newInstance();
            }
            catch (Exception e) {
                throw new APIException("Unable to instantiate RuleEvaluator " + evaluator, e);
            }
        }
        else {
            throw new APIException("RuleEvaluator is null");
        }
    }

}
