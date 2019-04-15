package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.Kernel;

/**
 * Hosts the euclidian views.
 */
public interface EuclidianHost {

	/**
	 * @return euclidian view; if not present yet, new one is created
	 */
	EuclidianView createEuclidianView();

	/**
	 * @return active euclidian view (may be EV, EV2 or 3D)
	 */
	EuclidianView getActiveEuclidianView();

	/**
	 * @return whether EV2 was initialized
	 */
	boolean hasEuclidianView2EitherShowingOrNot(int idx);

	/**
	 * @return whether EV2 is visible
	 */
	boolean isShowingEuclidianView2(int idx);

	/**
	 * @param kernel kernel
	 * @return euclidian controller
	 */
	EuclidianController newEuclidianController(Kernel kernel);

	/**
	 * @return DrawEquation instance
	 */
	DrawEquation getDrawEquation();
}
