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
