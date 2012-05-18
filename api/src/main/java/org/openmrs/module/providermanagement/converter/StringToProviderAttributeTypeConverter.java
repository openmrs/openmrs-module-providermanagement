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

package org.openmrs.module.providermanagement.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ProviderAttributeType;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToProviderAttributeTypeConverter  implements Converter<String, ProviderAttributeType> {

    /**
     * Treats the string as the integer primary key of the Provider Attribute Type
     */
    @Override
    public ProviderAttributeType convert(String id) {
        if (StringUtils.isBlank(id))
            return null;
        return Context.getProviderService().getProviderAttributeType(Integer.valueOf(id));
    }
}
