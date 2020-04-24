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

import java.lang.reflect.Method;

import org.openmrs.api.context.Context;
import org.openmrs.layout.web.name.NameSupport;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.util.OpenmrsConstants;

public class PersonNameFragmentController {

	public void controller(FragmentModel model) {
    	
		// use a later API NameSupport class from 2.0+, so we need to access it via
		// reflection
    if(OpenmrsConstants.OPENMRS_VERSION_SHORT.startsWith("1.9.*")||OpenmrsConstants.OPENMRS_VERSION_SHORT.startsWith("1.10.*")||OpenmrsConstants.OPENMRS_VERSION_SHORT.startsWith("1.11.*")||OpenmrsConstants.OPENMRS_VERSION_SHORT.startsWith("1.12"))
	{
        model.addAttribute("layoutTemplate", NameSupport.getInstance().getDefaultLayoutTemplate());
	}else
		
	{
		 try {
				// load the NameSupport with the openmrsClass loader from the fully qualified class name
				Object nameSupport = Context.loadClass("org.openmrs.layout.name.NameSupport");

				// invoke the getInstance method of the NameSupport class being loaded at runtime
				Method method = nameSupport.getClass().getMethod("getInstance", null);
				method.invoke(nameSupport, null);
				// invoke the getDefaultLayoutTemplate method of the NameSupport class being loaded at runtime
				Method method1 = nameSupport.getClass().getMethod("getDefaultLayoutTemplate", null);
				method1.invoke(nameSupport, null);

				model.addAttribute("layoutTemplate", nameSupport);

			} catch (Exception ex) {
				throw new RuntimeException("Unable to access NameSupport via reflection", ex);
			}
		
		}
		
    }
}
