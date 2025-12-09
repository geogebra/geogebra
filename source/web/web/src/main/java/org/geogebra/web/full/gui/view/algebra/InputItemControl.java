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

package org.geogebra.web.full.gui.view.algebra;

/**
 * Interface for control of the input row.
 * @author laszo
 *
 */
public interface InputItemControl {
	/**
	 * Shows/hides 3Dot menu by demand.
	 */
	void ensureInputMoreMenu();
	
	/**
	 * Shows item control if exits
	 */
	void ensureControlVisibility();
	
	/**
	 * Hides 3Dot menu.
	 */
	void hideInputMoreButton();
	
	/**
	 * Adds the old delete button if no 3Dot feature.
	 */
	void addClearButtonIfSupported();
	
	/**
	 * Adds the control to the AV item
	 */
	void addInputControls();
	
	/** 
	 * @return if item has 3Dot menu or not.
	 */
	boolean hasMoreMenu();
}
