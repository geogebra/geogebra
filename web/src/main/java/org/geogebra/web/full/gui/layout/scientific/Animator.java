package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.gui.layout.animation.Animation;
import org.geogebra.web.html5.gui.GeoGebraFrameW;

import com.google.gwt.user.client.ui.Widget;

public class Animator extends org.geogebra.web.full.gui.layout.animation.Animator {

	public Animator(GeoGebraFrameW frame, Widget animatable) {
		super(frame, animatable);

		largeScreenAnimation =
				new Animation(
						"panelFadeIn",
						"panelFadeOut");
		smallScreenAnimation =
				new Animation(
						"animateInFromRight",
						"animateOutToRight");
	}
}
