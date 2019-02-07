package org.geogebra.web.full.gui.layout.animation;

/**
 * In- and out-animation.
 */
public class Animation {
	private String animateInStyle;
	private String animateOutStyle;
	private boolean isFadeAnimation;

	/**
	 *
	 * @param animateInStyle The in-animation style.
	 * @param animateOutStyle The out-animation style.
	 */
	public Animation(String animateInStyle, String animateOutStyle) {
		this.animateInStyle = animateInStyle;
		this.animateOutStyle = animateOutStyle;
	}

	String getAnimateInStyle() {
		return animateInStyle;
	}

	String getAnimateOutStyle() {
		return animateOutStyle;
	}

	public void setFadeAnimation(boolean fadeAnimation) {
		isFadeAnimation = fadeAnimation;
	}

	public boolean isFadeAnimation() {
		return isFadeAnimation;
	}
}
