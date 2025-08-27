package org.geogebra.common.main;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;

/**
 * Collects and serializes application view flags, GUI settings, and thumbnail data.
 *
 * <p>This utility class queries the current {@link App} and optional
 * {@link GuiManagerInterface} to determine which views (e.g., Algebra,
 * Spreadsheet) are active, what boolean settings are enabled,
 * and the current thumbnail URL.</p>
 *
 * <p>It provides methods to output these parameters as a JavaScript-style
 * dictionary string, an HTML snippet, or a {@link JSONObject}.</p>
 */
public final class MaterialParameters {
	private final App app;
	private GuiManagerInterface gui;
	private final Map<String, Boolean> views = new LinkedHashMap<>();
	private final Map<String, Boolean> boolSettings = new LinkedHashMap<>();
	private String thumbnail = null;

	/**
	 * Constructs parameters for the given application, without GUI context.
	 *
	 * @param app the {@link App} instance from which to extract state
	 */
	public MaterialParameters(App app) {
		this(app, null);
	}

	/**
	 * Constructs parameters for the given application and GUI manager.
	 *
	 * <p>If {@code gui} is {@code null}, the constructor attempts
	 * to retrieve the GUI manager via {@link App#getGuiManager()}.</p>
	 *
	 * @param app the {@link App} instance from which to extract state
	 * @param gui the {@link GuiManagerInterface} or {@code null} to fetch from {@code app}
	 */
	public MaterialParameters(App app, GuiManagerInterface gui) {
		this.app = app;
		this.gui = gui;
		update();
	}

	void update() {
		views.clear();
		Construction construction = app.getKernel().getConstruction();
		views.put("is3D", construction.requires3D());

		if (gui == null) {
			gui = app.getGuiManager();
		}

		if (gui != null) {
			views.put("AV", gui.hasAlgebraView() && gui.getAlgebraView().isShowing());
			views.put("CP", gui.isUsingConstructionProtocol());
			views.put("CV", gui.hasCasView());
			views.put("DA", gui.hasDataAnalysisView());
			views.put("EV2", app.hasEuclidianView2(1));
			views.put("FI", app.getDialogManager() != null
					&& app.getDialogManager().hasFunctionInspector());
			views.put("PC", gui.hasProbabilityCalculator());
			views.put("SV", gui.hasSpreadsheetView() && gui.getSpreadsheetView().isShowing());
			views.put("PV", false);
		}

		views.put("macro", app.getKernel().hasMacros());
		thumbnail = app.getGgbApi().getThumbnailDataURL();
		updateSettingsMap();
	}

	/**
	 * Returns the data URL of the application’s current thumbnail.
	 *
	 * @return the thumbnail image as a Base64-encoded data URL, or {@code null}
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	private void updateSettingsMap() {
		boolSettings.clear();
		boolSettings.put("enableLabelDrags", false);
		boolSettings.put("enableRightClick", false);
		boolSettings.put("enableUndoRedo", false);
		boolSettings.put("enableShiftDragZoom", true);
		boolSettings.put("allowStyleBar", false);
		boolSettings.put("showZoomButtons", false);
		boolSettings.put("showAlgebraInput", app.showAlgebraInput);
		boolSettings.put("showMenuBar", app.showMenuBar);
		boolSettings.put("showResetIcon", false);
		boolSettings.put("showToolBar", app.showToolBar);
	}

	/**
	 * Returns a JavaScript-style dictionary of view flags.
	 *
	 * @return a String in the form {'key': 1, 'otherKey': 0, …}
	 */
	public String viewsToDictionary() {
		return toDictionary(views);
	}

	/**
	 * Returns GUI boolean settings and optional custom toolbar definition.
	 *
	 * <p>The resulting string includes toolbar customizations if the toolbar is shown,
	 * followed by window height, width, and scale.</p>
	 *
	 * @return a comma-separated list of quoted keys and values suitable for embedding in a
	 * <script></script> tag
	 */
	public String settingsToHtml() {
		StringBuilder sb = new StringBuilder();
		toHtml(boolSettings, sb);
		sb.append(",\n");
		if (app.showToolBar) {
			if (gui != null) {
				sb.append("\"customToolBar\": \"");
				sb.append(gui.getToolbarDefinition());
				sb.append("\",\n");
			}
			sb.append("\"showToolBarHelp\": ").append(app.showToolBarHelp).append(",\n");

		}
		sb.append("\"height\" : ").append(app.getHeight()).append(",\n");
		sb.append("\"width\" : ").append(app.getWidth()).append(",\n");
		sb.append("\"scale\" : 1");

		return sb.toString();
	}

	/**
	 * Returns GUI boolean settings and dimensions as a {@link JSONObject}.
	 *
	 * <p>Always includes "height", "width", and "appName" fields.</p>
	 *
	 * @return a {@link JSONObject} mapping setting names to values
	 */
	public JSONObject settingsToJSON() {
		JSONObject settings = toJSON(boolSettings);

		try {
			settings.put("height", app.getHeight());
			settings.put("width", app.getWidth());
			settings.put("appName", app.getConfig().getAppCode());
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		return settings;
	}

	private String toDictionary(Map<String, Boolean> booleanMap) {
		StringBuilder sb = new StringBuilder();
		String separator = "";
		sb.append("{");
		for (Entry<String, Boolean> entry: booleanMap.entrySet()) {
			sb.append(separator);
			sb.append("'");
			sb.append(entry.getKey());
			sb.append("': ");
			sb.append(entry.getValue() ? "1" : "0");
			separator = ", ";
		}
		sb.append("}");
		return sb.toString();
	}

	private void toHtml(Map<String, Boolean> booleanMap, StringBuilder sb) {
		String separator = "";
		for (Entry<String, Boolean> entry: booleanMap.entrySet()) {
			sb.append(separator);
			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");
			sb.append(entry.getValue());
			separator = ",\n";
		}
	}

	/**
	 * Returns view flags as a {@link JSONObject}.
	 *
	 * @return a {@link JSONObject} mapping view identifiers to boolean values
	 */
	public JSONObject viewsToJSON() {
		return toJSON(views);
	}

	private JSONObject toJSON(Map<String, ?> map) {
		JSONObject views = new JSONObject();
		try {
			for (Entry<String, ?> entry: map.entrySet()) {
				views.put(entry.getKey(), entry.getValue());
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return views;
	}
}
