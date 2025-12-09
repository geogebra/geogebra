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
