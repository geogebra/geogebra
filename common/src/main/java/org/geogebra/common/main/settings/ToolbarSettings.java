package org.geogebra.common.main.settings;

import java.util.LinkedList;

import org.geogebra.common.gui.toolcategorization.ToolCategorization.ToolsetLevel;

/**
 * settings for the toolbar
 * 
 * @author csilla
 *
 */
public class ToolbarSettings extends AbstractSettings {

	private static ToolsetLevel toolsetLevel = ToolsetLevel.EMPTY_CONSTRUCTION;

	/**
	 * @param listeners
	 *            - listeners
	 */
	public ToolbarSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	/**
	 * 
	 */
	public ToolbarSettings() {
		super();
	}

	/**
	 * @param level
	 *            - advanced or standard
	 */
	public void setToolsetLevel(ToolsetLevel level) {
		toolsetLevel = level;
		settingChanged();
	}

	/**
	 * @return toolset level
	 */
	public ToolsetLevel getToolsetLevel() {
		return toolsetLevel;
	}

}
