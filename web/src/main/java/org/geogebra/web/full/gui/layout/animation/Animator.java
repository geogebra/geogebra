package org.geogebra.web.full.gui.layout.animation;

import org.geogebra.web.html5.gui.GeoGebraFrameW;

import com.google.gwt.user.client.ui.Widget;

public abstract class Animator {
	private GeoGebraFrameW frame;
	private Widget animatable;
	protected Animation largeScreenAnimation;
	protected Animation smallScreenAnimation;

	public Animator(GeoGebraFrameW frame, Widget animatable) {
		this.frame = frame;
		this.animatable = animatable;
	}

	public void updateAnimateInStyle() {
		setStyle(getAnimateInStyle());
	}

	public void updateAnimateOutStyle() {
		setStyle(getAnimateOutStyle());
	}

	private void setStyle(String style) {
		removePreviousStyles();
		animatable.addStyleName(style);
	}

	private void removePreviousStyles() {
		animatable.removeStyleName(largeScreenAnimation.getAnimateInStyle());
		animatable.removeStyleName(largeScreenAnimation.getAnimateOutStyle());
		animatable.removeStyleName(smallScreenAnimation.getAnimateInStyle());
		animatable.removeStyleName(smallScreenAnimation.getAnimateOutStyle());
	}

	public String getAnimateInStyle() {
		return getAnimation().getAnimateInStyle();
	}

	public String getAnimateOutStyle() {
		return getAnimation().getAnimateOutStyle();
	}

	private Animation getAnimation() {
		return frame.hasSmallWindowOrCompactHeader() ? smallScreenAnimation : largeScreenAnimation;
	}
}
