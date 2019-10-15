package org.geogebra.common.main.settings;

import java.util.LinkedList;

import org.geogebra.common.gui.toolcategorization.AppType;
import org.geogebra.common.gui.toolcategorization.ToolsetLevel;
import org.geogebra.common.main.AppConfig;

/**
 * settings for the toolbar
 * 
 * @author csilla
 *
 */
public class ToolbarSettings extends AbstractSettings {

	private ToolsetLevel toolsetLevel = ToolsetLevel.STANDARD;
	private AppType type = AppType.GRAPHING_CALCULATOR;

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
	
	/**
	 * @param type
	 *            - type of the app
	 */
	public void setType(AppType type) {
		this.type = type;
	}

	/**
	 * @return whether empty level is supported
	 */
	public boolean hasEmptyConstruction() {
		return this.type == AppType.GEOMETRY_CALC;
	}

	/**
	 * @return - if app is 3d app
	 * 
	 */
	public boolean is3D() {
		return this.type == AppType.GRAPHER_3D;
	}

	/**
	 * Update toolbar type for app config.
	 * 
	 * @param config
	 *            the application config.
	 */
	public void setFrom(AppConfig config) {
		if (config == null) {
			return;
		}

		type = config.getToolbarType();
	}
}
