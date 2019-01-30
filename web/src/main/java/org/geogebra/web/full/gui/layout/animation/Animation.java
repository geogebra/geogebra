package org.geogebra.web.full.gui.layout.animation;

public class Animation {
	private String animateInStyle;
	private String animateOutStyle;

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
}
