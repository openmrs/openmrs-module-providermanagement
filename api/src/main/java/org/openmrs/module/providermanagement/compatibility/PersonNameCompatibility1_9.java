/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.providermanagement.compatibility;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.layout.web.name.NameSupport;
import org.openmrs.ui.framework.fragment.FragmentModel;
import org.springframework.stereotype.Component;

@Component("providermanagement.PersonNameCompatibilitySupport")
@OpenmrsProfile(openmrsPlatformVersion = "1.9.9 - 1.12.*")
public class PersonNameCompatibility1_9 implements PersonNameCompatibilitySupport {

	@Override
	public void controller(FragmentModel model) {
		model.addAttribute("layoutTemplate", NameSupport.getInstance().getDefaultLayoutTemplate());
	}

}
