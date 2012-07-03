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

package org.openmrs.module.providermanagement.fragment.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

public class GlobalPropertyUpdaterFragmentController {

    // TODO: (PROV-13) Improve GlobalPropertyUpdater so that all global properties on a page can be handled by a single submit button

    public void controller(FragmentModel model,
                           @FragmentParam("propertyName") GlobalProperty property,
                           @FragmentParam("type") String type) {

        String currentValue = property.getPropertyValue();

        // handle select list
        if (type.equalsIgnoreCase("selectList")) {
            List<String> values = new ArrayList<String>();

            if (StringUtils.isNotBlank(currentValue)) {
                for (String v : currentValue.split("\\|")) {
                    values.add(v);
                }
            }
            model.addAttribute("values", values);
        }
        // handle text area
        else if (type.equalsIgnoreCase("text")) {
            model.addAttribute("value", currentValue);
        }
        else {
            throw new RuntimeException("Invalid global property type: must be select list or text");
        }

        model.addAttribute("property", property);
    }

    public void saveGlobalProperty(@RequestParam("propertyName") GlobalProperty property,
                                   @RequestParam(value = "value", required = false) String value,
                                   @RequestParam(value = "values", required = false) String[] values) {

        // concatenate the values
        String updatedValue;

        if (values != null && values.length > 0) {
            updatedValue = StringUtils.join(values,'|');
        }
        else {
            updatedValue = value;
        }

        property.setPropertyValue(updatedValue);
        Context.getAdministrationService().saveGlobalProperty(property);
    }
}
