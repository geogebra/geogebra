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

package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.full.gui.layout.animation.Animator;

/**
 * A panel with show and hide animations.
 */
public abstract class AnimatingPanel extends MyHeaderPanel {

	private Animator animator;

	/**
	 * @param animator The animator that controls the animations.
	 */
	public void setAnimator(Animator animator) {
		this.animator = animator;
	}

	/**
	 * This method has to be called before animating the panel in.
	 */
	public void updateAnimateInStyle() {
		animator.updateAnimateInStyle();
	}

	/**
	 * This method has to be called before animating the panel out.
	 */
	public void updateAnimateOutStyle() {
		animator.updateAnimateOutStyle();
	}

	/**
	 * @return The current in-animation style
	 * (which depends on window size and the layout type of the frame).
	 */
	public String getAnimateInStyle() {
		return animator.getAnimateInStyle();
	}

	/**
	 * @return The current out-animation style
	 * (which depends on window size and the layout type of the frame).
	 */
	public String getAnimateOutStyle() {
		return animator.getAnimateOutStyle();
	}

	/**
	 * @return Whether the current animation is fade animation or not
	 * (which depends on window size and the layout type of the frame).
	 */
	public boolean willUseFadeAnimation() {
		return animator != null && animator.getAnimation().isFadeAnimation();
	}
}
