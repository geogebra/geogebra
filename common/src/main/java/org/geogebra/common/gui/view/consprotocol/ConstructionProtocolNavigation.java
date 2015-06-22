package org.geogebra.common.gui.view.consprotocol;

import org.geogebra.common.javax.swing.GPanel;

/**
 * Navigation buttons for the construction protocol
 */
public abstract class ConstructionProtocolNavigation{

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
	public boolean isPlaying;

	protected GPanel playPanel;

	/**
	 * Sets the visibility of the navigation bar.
	 * @param visible if true, the navigation bar will be visible
	 */
	public abstract void setVisible(boolean visible);

	/**
	 * @return whether play button is visible
	 */
	public boolean isPlayButtonVisible() {
		return showPlayButton;
	}

	/**
	 * Returns delay between frames of automatic construction protocol
	 * playing in seconds.
	 * @return delay in seconds
	 */
	public double getPlayDelay() {
		return playDelay;
	}

	/**
	 * @return whether button to show construction protocol is visible
	 */
	public boolean isConsProtButtonVisible() {
		return showConsProtButton;
	}

	/**
	 * Sets delay for the value given in parameter.
	 * @param delay expected delay in seconds
	 */
	public abstract void setPlayDelay(double delay);
	
	/**
	 * Sets the construction protocol button visible or invisible.
	 * @param flag if true, the construction protocol button will be visible
	 */
	public abstract void setConsProtButtonVisible(boolean flag);

	/**
	 * Sets the labels of protocol navigation bar's button.
	 */
	public abstract void setLabels();

	public void update() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param flag true to make play button visible
	 */
	public void setPlayButtonVisible(boolean flag) {
		showPlayButton = flag;
		if (playPanel != null) {
			playPanel.setVisible(flag);
		}
	}

	/**
	 * Registers this navigation bar at its protocol to be informed about
	 * updates.
	 * 
	 * @param constructionProtocolView
	 *            CP view
	 */
	abstract public void register(ConstructionProtocolView cpv);
	
	/**
	 * set button to "play" aspect
	 */
	abstract public void setButtonPlay();

	/**
	 * set button to "pause" aspect
	 */
	abstract public void setButtonPause();

}
