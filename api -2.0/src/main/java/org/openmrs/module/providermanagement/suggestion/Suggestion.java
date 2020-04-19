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

package org.openmrs.module.providermanagement.suggestion;

// TODO: make criteria be a file link for security purposes?

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;

abstract public class Suggestion extends BaseOpenmrsMetadata {

    // the stores the actual "rule" used to make suggestions (currently must be a groovy script)
    private String criteria;

    /* The evaluator to use when evaluating the rule */
    private String evaluator;

    public Suggestion() {

    }

    public SuggestionEvaluator instantiateEvaluator() {
        if (evaluator != null) {
            try {
                return (SuggestionEvaluator) Context.loadClass(evaluator).newInstance();
            }
            catch (Exception e) {
                throw new APIException("Unable to instantiate RuleEvaluator " + evaluator, e);
            }
        }
        else {
            throw new APIException("RuleEvaluator is null");
        }
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "name='" + getName() + '\'' +
                ", criteria='" + criteria + '\'' +
                '}';
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

}
