package org.geogebra.common.main.settings;

import java.util.LinkedList;

/**
 * Settings for the construction protocol.
 */
public class ConstructionProtocolSettings extends AbstractSettings {
	/**
	 * Show construction protocol.
	 */
	private boolean showConstructionProtocol;

	/**
	 * Delay between changes of items while playing.
	 */
	private double playDelay = 2; // in seconds

	/**
	 * Show the play button.
	 */
	private boolean showPlayButton = true;

	/**
	 * Show the construction protocol button.
	 */
	private boolean showConsProtButton = true;

	/**
	 * Show the visibility of construction protocol's columns
	 */
	private static final boolean[] defaultCpColumnsVisible = { true, true,
			false, true, false, true, true, false };
	private boolean[] cpColumnsVisible = defaultCpColumnsVisible;

	/**
	 * @param listeners
	 *            settings listeners
	 */
	public ConstructionProtocolSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	/**
	 * Default constructor.
	 */
	public ConstructionProtocolSettings() {
		super();
	}

	/**
	 * @return the showConsProtButton
	 */
	public boolean showConsProtButton() {
		return showConsProtButton;
	}

	/**
	 * @param flag
	 *            the showConsProtButton to set
	 */
	public void setShowConsProtButton(boolean flag) {
		if (showConsProtButton != flag) {
			this.showConsProtButton = flag;
			settingChanged();
		}
	}

	/**
	 * @return the showPlayButton
	 */
	public boolean showPlayButton() {
		return showPlayButton;
	}

	/**
	 * @param flag
	 *            the showPlayButton to set
	 */
	public void setShowPlayButton(boolean flag) {
		if (showPlayButton != flag) {
			this.showPlayButton = flag;
			settingChanged();
		}
	}

	/**
	 * @return the playDelay
	 */
	public double getPlayDelay() {
		return playDelay;
	}

	/**
	 * @param playDelay
	 *            the playDelay to set
	 */
	public void setPlayDelay(double playDelay) {
		if (this.playDelay != playDelay) {
			this.playDelay = playDelay;
			settingChanged();
		}
	}

	/**
	 * @return the showConstructionProtocol
	 */
	public boolean showConstructionProtocol() {
		return showConstructionProtocol;
	}

	/**
	 * @param flag
	 *            the showConstructionProtocol to set
	 */
	public void setShowConstructionProtocol(boolean flag) {
		if (showConstructionProtocol != flag) {
			this.showConstructionProtocol = flag;
			settingChanged();
		}
	}

	/**
	 * @return column visibility
	 */
	public boolean[] getColsVisibility() {
		return cpColumnsVisible;
	}

	/**
	 * Copy column visibility settings.
	 * 
	 * @param cpColumnsVisible
	 *            columns visibility
	 */
	public void setColsVisibility(boolean[] cpColumnsVisible) {

		this.cpColumnsVisible = new boolean[defaultCpColumnsVisible.length];

		for (int i = 0; i < defaultCpColumnsVisible.length; i++) {
			if (cpColumnsVisible == null) {
				this.cpColumnsVisible[i] = defaultCpColumnsVisible[i];
			} else if (cpColumnsVisible.length <= i) {
				this.cpColumnsVisible[i] = defaultCpColumnsVisible[i];
			} else {
				this.cpColumnsVisible[i] = cpColumnsVisible[i];
			}
		}

		settingChanged();

	}
}
