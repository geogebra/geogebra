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

package org.geogebra.common.gui.view.consprotocol;

import org.geogebra.common.kernel.ConstructionStepper;
import org.geogebra.common.main.App;

/**
 * Navigation buttons for the construction protocol
 */
public abstract class ConstructionProtocolNavigation {

	protected ConstructionProtocolView prot;

	protected App app;

	/**
	 * True if play button visible on navigation bar.
	 */
	protected boolean showPlayButton = true;

	/**
	 * Delay in seconds
	 */
	public double playDelay = 2;
	/**
	 * True if the button for opening construction protocol is visible.
	 */
	protected boolean showConsProtButton = true;

	/** Indicates whether animation is on or off */
	private boolean isPlaying;

	private int viewID;

	/**
	 * @param app
	 *            application
	 * @param viewID
	 *            view ID
	 */
	public ConstructionProtocolNavigation(App app, int viewID) {
		this.app = app;
		this.viewID = viewID;
	}

	/**
	 * Sets the visibility of the navigation bar.
	 * 
	 * @param visible
	 *            if true, the navigation bar will be visible
	 */
	public abstract void setVisible(boolean visible);

	/**
	 * @return whether play button is visible
	 */
	public boolean isPlayButtonVisible() {
		return showPlayButton;
	}

	/**
	 * Returns delay between frames of automatic construction protocol playing
	 * in seconds.
	 * 
	 * @return delay in seconds
	 */
	public double getPlayDelay() {
		return playDelay;
	}

	/**
	 * @return whether button to show construction protocol is visible
	 */
	public boolean isConsProtButtonVisible() {
		return showConsProtButton && viewID != App.VIEW_CONSTRUCTION_PROTOCOL;
	}

	/**
	 * Sets delay for the value given in parameter.
	 * 
	 * @param delay
	 *            expected delay in seconds
	 */
	public abstract void setPlayDelay(double delay);

	/**
	 * Sets the construction protocol button visible or invisible.
	 * 
	 * @param flag
	 *            if true, the construction protocol button will be visible
	 */
	public abstract void setConsProtButtonVisible(boolean flag);

	/**
	 * Sets the labels of protocol navigation bar's button.
	 */
	public abstract void setLabels();

	/**
	 * Update the UI
	 */
	public void update() {
		// overridden in platforms
	}

	/**
	 * @param flag
	 *            true to make play button visible
	 */
	public void setPlayButtonVisible(boolean flag) {
		showPlayButton = flag;
		setPlayPanelVisible(flag);
	}

	protected abstract void setPlayPanelVisible(boolean flag);

	/**
	 * Registers this navigation bar at its protocol to be informed about
	 * updates.
	 * 
	 * @param constructionProtocolView
	 *            CP view
	 */
	public final void register(
			ConstructionProtocolView constructionProtocolView) {
		if (prot == null) {
			initGUI();
		}

		if (constructionProtocolView != null) {
			prot = constructionProtocolView;
			prot.registerNavigationBar(this);
		}
	}

	/**
	 * Initialize the UI
	 */
	protected abstract void initGUI();

	/**
	 * set button to "play" aspect
	 */
	abstract public void setButtonPlay();

	/**
	 * set button to "pause" aspect
	 */
	abstract public void setButtonPause();

	protected boolean isPlaying() {
		return isPlaying;
	}

	protected void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	protected ConstructionStepper getProt() {
		return prot != null ? prot : app.getKernel();
	}

}
