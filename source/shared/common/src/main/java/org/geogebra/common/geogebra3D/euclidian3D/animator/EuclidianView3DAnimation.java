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

package org.geogebra.common.geogebra3D.euclidian3D.animator;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.animator.EuclidianView3DAnimator.AnimationType;

/**
 * class for 3D view animations
 *
 */
public abstract class EuclidianView3DAnimation {

	/**
	 * it will take about 1/2s to achieve
	 */
	static final double ANIMATION_DURATION = 0.005;

	/**
	 * 3D view
	 */
	protected EuclidianView3D view3D;
	/**
	 * 3D view animator
	 */
	protected EuclidianView3DAnimator animator;

	private boolean storeUndo;

	/**
	 * constructor
	 *
	 * @param view3D
	 *            3D view
	 * @param animator
	 *            3D view animator
	 */
	EuclidianView3DAnimation(EuclidianView3D view3D, EuclidianView3DAnimator animator) {
		this(view3D, animator, false);
	}

	/**
	 * constructor
	 * 
	 * @param view3D
	 *            3D view
	 * @param animator
	 *            3D view animator
	 * @param storeUndo
	 *            if undo point will be stored at the end
	 */
	EuclidianView3DAnimation(EuclidianView3D view3D, EuclidianView3DAnimator animator,
			boolean storeUndo) {
		this.view3D = view3D;
		this.animator = animator;
		this.storeUndo = storeUndo;
	}

	/**
	 * setup values for start
	 */
	abstract public void setupForStart();

	/**
	 * 
	 * @return animation type
	 */
	abstract public AnimationType getType();

	/**
	 * process animation
	 */
	abstract public void animate();

	/**
	 * end animation
	 */
	public void end() {
		if (storeUndo) {
			view3D.getApplication().storeUndoInfo();
		}
		animator.endAnimation();
	}

	protected final static double getMillisecondTime() {
		return UtilFactory.getPrototype().getMillisecondTime();
	}

	protected boolean animationAllowed() {
		return false;
	}
}
