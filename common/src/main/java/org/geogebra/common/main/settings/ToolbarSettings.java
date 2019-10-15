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
	private boolean phoneApp = false;

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
	 * see AppType
	 * 
	 * @return type of app
	 */
	public AppType getType() {
		return type;
	}

	/**
	 * @param type
	 *            - type of the app
	 */
	public void setType(AppType type) {
		this.type = type;
	}

	/**
	 * @return - if app is geometry app
	 * 
	 */
	public boolean isGeometry() {
		return this.type == AppType.GEOMETRY_CALC;
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

	public boolean isPhoneApp() {
		return phoneApp;
	}

	/**
	 * Update toolbar type for app config.
	 * 
	 * @param config
	 *            the application config.
	 * @param phone
	 *            determines if the app runs on phone.
	 */
	public void setFrom(AppConfig config, boolean phone) {
		if (config == null) {
			return;
		}

		type = config.getToolbarType();
		phoneApp = phone;
	}
}
