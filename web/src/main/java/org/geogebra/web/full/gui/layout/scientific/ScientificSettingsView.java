package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.gui.MyHeaderPanel;
import org.geogebra.web.html5.main.AppW;

/**
 * @author Csilla
 * 
 *         Settings view of scientific calculator
 *
 */
public class ScientificSettingsView extends MyHeaderPanel {

	private boolean isOpen = false;

	/**
	 * Build and style settings view for sci calc
	 */
	public ScientificSettingsView() {
		this.addStyleName("scientificSettingsView");
		isOpen = true;
	}

	@Override
	public AppW getApp() {
		return null;
	}

	/**
	 * @return true if settings view is open
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * @param isOpen
	 *            true if open settings, false otherwise
	 */
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	@Override
	public void resizeTo(int width, int height) {
		// TODO handle resize
	}

}
