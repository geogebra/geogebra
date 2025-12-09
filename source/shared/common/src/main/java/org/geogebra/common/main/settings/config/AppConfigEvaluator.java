/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.main.settings.config;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;

/**
 * Config for Evaluator
 *
 */
public class AppConfigEvaluator extends AppConfigDefault {

	public AppConfigEvaluator() {
		super(GeoGebraConstants.EVALUATOR_APPCODE);
	}

	@Override
	public String getForcedPerspective() {
		return Perspective.EVALUATOR + "";
	}

	@Override
	public String getAppTitle() {
		return "Evaluator";
	}

	@Override
	public String getTutorialKey() {
		return "evaluator_tutorials";
	}

	@Override
	public boolean isCASEnabled() {
		return false;
	}

	@Override
	public CommandFilter createCommandFilter() {
		return CommandFilterFactory.createNoCasCommandFilter();
	}

	@Override
	public CommandArgumentFilter getCommandArgumentFilter() {
		return null;
	}

	@Override
	public boolean shouldHideEquations() {
		return false;
	}

	@Override
	public boolean sendKeyboardEvents() {
		return true;
	}

	@Override
	public boolean hasLabelForDescription() {
		return true;
	}
}
