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

import org.apache.commons.lang3.reflect.MethodUtils;
import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.fragment.FragmentModel;

public class PersonAddressFragmentController {

    public void controller(FragmentModel model) {
    	try {
	    	Class<?> addressSupportClass = null;
	        try {
	        	addressSupportClass = Context.loadClass("org.openmrs.layout.web.address.AddressSupport");
	        }
	        catch (ClassNotFoundException ex) {
	        	addressSupportClass = Context.loadClass("org.openmrs.layout.address.AddressSupport");
	        }
	        
	        Object addressSupport = addressSupportClass.getMethod("getInstance").invoke(null);
	        model.addAttribute("layoutTemplate", MethodUtils.invokeExactMethod(addressSupport, "getDefaultLayoutTemplate", null));
    	}
    	catch (Exception e) {
            throw new RuntimeException("Error while getting patient address", e);
        }
    }
}
