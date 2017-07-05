package org.geogebra.common.main.settings;

import java.util.LinkedList;

import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.ToolsetLevel;

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
}
