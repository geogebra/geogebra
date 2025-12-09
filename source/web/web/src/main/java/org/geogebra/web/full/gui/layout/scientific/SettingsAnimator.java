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

package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.gui.layout.animation.Animation;
import org.geogebra.web.full.gui.layout.animation.Animator;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.gwtproject.user.client.ui.Widget;

/**
 * Animator for the settings panel.
 * Uses fade-in fade-out animations for large screen
 * and slide-in slide-out animations for small screen.
 */
public class SettingsAnimator extends Animator {

	/**
	 * @param frame The frame of the whole app.
	 * @param animatable The view element that we want to animate.
	 */
	public SettingsAnimator(GeoGebraFrameW frame, Widget animatable) {
		super(frame, animatable);

		largeScreenAnimation =
				new Animation(
						"panelFadeIn",
						"panelFadeOut");
		largeScreenAnimation.setFadeAnimation(true);

		smallScreenAnimation =
				new Animation(
						"animateInFromRight",
						"animateOutToRight");
	}
}
