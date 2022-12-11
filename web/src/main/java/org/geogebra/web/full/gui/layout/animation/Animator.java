package org.geogebra.web.full.gui.layout.animation;

import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.gwtproject.user.client.ui.Widget;

/**
 * Adds two kinds of animations to the animatable Widget.
 * One kind of in and out animation for small screen,
 * and one kind of in and out animation for large screen.
 * The animations need to be updated before animating the animatable widget.
 * The updateAnimateInStyle() method has to be called before animating the animatable widget in,
 * and the updateAnimateOutStyle() method has to be called before animating the widget out.
 */
public class Animator {
	private GeoGebraFrameW frame;
	private Widget animatable;
	protected Animation largeScreenAnimation;
	protected Animation smallScreenAnimation;

	/**
	 * @param frame The frame of the whole app.
	 * @param animatable The view element that we want to animate.
	 */
	protected Animator(GeoGebraFrameW frame, Widget animatable) {
		this.frame = frame;
		this.animatable = animatable;
	}

	/**
	 * The method has to be called before animating the animatable widget in.
	 */
	public void updateAnimateInStyle() {
		setStyle(getAnimateInStyle());
	}

	/**
	 * This method has to be called before animating the animatable widget out.
	 */
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

	/**
	 * @return The in-animation style depending on the screen size.
	 */
	public String getAnimateInStyle() {
		return getAnimation().getAnimateInStyle();
	}

	/**
	 * @return The out-animation style depending on the screen size.
	 */
	public String getAnimateOutStyle() {
		return getAnimation().getAnimateOutStyle();
	}

	public Animation getAnimation() {
		return frame.hasSmallWindowOrCompactHeader() ? smallScreenAnimation : largeScreenAnimation;
	}
}
