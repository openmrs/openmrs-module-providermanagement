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
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

public class GlobalPropertyUpdaterFragmentController {

    public void controller(FragmentModel model,
                           @FragmentParam("propertyName") GlobalProperty property) {

        List<String> values = new ArrayList<String>();
        String value = property.getPropertyValue();

        if (StringUtils.isNotBlank(value)) {
            for (String v : value.split("\\|")) {
                values.add(v);
            }
        }

        model.addAttribute("values", values);
        model.addAttribute("property", property);
    }

    public void saveGlobalProperty(@RequestParam("propertyName") GlobalProperty property,
                                   @RequestParam("values") String[] values) {

        // concatenate the values
        String value = StringUtils.join(values,'|');

        property.setValue(value);
        Context.getAdministrationService().saveGlobalProperty(property);
    }
}
