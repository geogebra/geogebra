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
