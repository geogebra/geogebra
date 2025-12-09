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
