package org.geogebra.common.kernel.batch;

import org.geogebra.common.kernel.CheckBeforeUpdateView;

/**
 * Interface for views that can be wrapped in BatchedUpdateWrapper
 */
public interface WrappableView extends CheckBeforeUpdateView {

	/**
	 * set if the view is currently wrapped
	 * 
	 * @param flag
	 *            flag
	 */
	void setIsWrapped(boolean flag);

	/**
	 * 
	 * @return if the view is currently wrapped
	 */
	boolean getIsWrapped();
}
