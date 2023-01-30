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

import java.lang.reflect.*;

import org.openmrs.ui.framework.fragment.FragmentModel;
import org.openmrs.module.*;

public class PersonNameFragmentController {

	/**
	 * @param model
	 */


	public void controller(FragmentModel model) {

		try {
			Class<?> cls = Class.forName("org.openmrs.layout.name.NameSupport");
			Method meth = cls.getDeclaredMethod("getInstance");
			meth.setAccessible(true);
			Object obj = cls.newInstance();

			System.out.println(
					"8###################******************does this work******************######################## "
							+ meth.invoke(obj));

			model.addAttribute("layoutTemplate", meth.invoke(obj));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		;
	}

}
