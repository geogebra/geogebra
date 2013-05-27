package geogebra.common.gui.view.consprotocol;


public abstract class ConstructionProtocolNavigation{

	protected boolean showPlayButton = true;
	/** Delay in seconds */
	protected double playDelay = 2;
	protected boolean showConsProtButton = true;

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

	public abstract void setPlayDelay(double delay);
	public abstract void setPlayButtonVisible(boolean flag);
	public abstract void setConsProtButtonVisible(boolean flag);
	public abstract void setLabels();


}
