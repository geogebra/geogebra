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

package org.geogebra.common.gui.view.data;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;

/**
 * common interface for plotpaneleuclidianviews
 * @author gabor
 */
public interface PlotPanelEuclidianViewInterface
		extends EuclidianViewInterfaceCommon {

	/**
	 * @param kernel
	 *            sets the View id
	 */
	public void setViewId(Kernel kernel);

	/**
	 * sets the Evs params
	 */
	public void setEVParams();

	/**
	 * @return get the pixel offset concerning fonts.
	 */
	public double getPixelOffset();

	@MissingDoc
	public void updateSizeKeepDrawables();

}
