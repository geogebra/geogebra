package org.geogebra.common.main.settings;

import java.util.LinkedList;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.gui.toolcategorization.ToolCategorization;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.ToolsetLevel;
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
	 * Update toolbar type for app version.
	 * 
	 * @param version
	 *            app version
	 */
	public void setFrom(Versions version) {
		switch (version) {
		case ANDROID_NATIVE_GRAPHING:
		case ANDROID_CAS:
		case IOS_NATIVE:
		case IOS_CAS:
			type = ToolCategorization.AppType.GRAPHING_CALCULATOR;
			phoneApp = true;
			break;
		case ANDROID_GEOMETRY:
		case IOS_GEOMETRY:
			type = ToolCategorization.AppType.GEOMETRY_CALC;
			phoneApp = true;
			break;
		case ANDROID_NATIVE_3D:
			type = ToolCategorization.AppType.GRAPHER_3D;
			phoneApp = true;
			break;
		case WEB_GRAPHING:
		case WEB_GRAPHING_OFFLINE:
			type = ToolCategorization.AppType.GRAPHING_CALCULATOR;
			break;
		case WEB_GEOMETRY:
		case WEB_GEOMETRY_OFFLINE:
		case WINDOWS_STORE:
			type = ToolCategorization.AppType.GEOMETRY_CALC;
			break;
		case WEB_3D_GRAPHING:
			type = ToolCategorization.AppType.GRAPHER_3D;
			break;
		default:
			type = ToolCategorization.AppType.GRAPHING_CALCULATOR;
			break;
		}
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
