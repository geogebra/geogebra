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

package org.geogebra.common.gui.layout;

import org.geogebra.common.annotation.MissingDoc;

/**
 * @author judit interface for geogebra.gui.layout.DockPanel
 * 
 */
public interface DockPanel {

	@MissingDoc
	String getToolbarString();

	@MissingDoc
	String getDefaultToolbarString();

	@MissingDoc
	int getViewId();

	/**
	 * Close this panel permanently.
	 */
	public void closePanel();

	/**
	 * change the visibility of the DockPanel
	 * 
	 * @param visible
	 *            visibility
	 */
	public void setVisible(boolean visible);

	/**
	 * 
	 * @return true if set visible
	 */
	public boolean isVisible();

	@MissingDoc
	void deferredOnResize();

	/**
	 * update navigation bar
	 */
	public void updateNavigationBar();
}
