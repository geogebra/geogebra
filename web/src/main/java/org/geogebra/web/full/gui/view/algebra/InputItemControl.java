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
