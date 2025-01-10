package org.geogebra.common.main.settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.App;

/**
 * Class which contains references to all settings of the application.
 * 
 * To add new setting containers to this class perform the following steps: 1.
 * Add attributes and getters (no setters allowed!) 2. Init in constructor 3.
 * Modify beginBatch() and endBatch()
 * 
 * @author Florian Sonner
 */
public class Settings {

	private Set<Resettable> resettableSettings;

	private final EuclidianSettings[] euclidianSettings;

	private HashMap<String, EuclidianSettings> euclidianSettingsForPlane;

	private AlgebraSettings algebraSettings;

	private SpreadsheetSettings spreadsheetSettings;

	private ConstructionProtocolSettings consProtSettings;

	private LayoutSettings layoutSettings;

	private GeneralSettings generalSettings;

	private AbstractSettings keyboardSettings;

	private CASSettings casSettings;

	private ProbabilityCalculatorSettings probCalcSettings;

	private DataAnalysisSettings daSettings;

	private TableSettings tableSettings;

	private FontSettings fontSettings;

	private LabelSettings labelSettings;

	private StyleSettings styleSettings;
	private PenToolsSettings penToolsSettings;

	/**
	 * Initialize settings using the constructors of the setting container
	 * classes.
	 * 
	 * @param app
	 *            - app
	 * 
	 * @param euclidianLength
	 *            2 or 3 euclidian views
	 */
	public Settings(App app, int euclidianLength) {
		euclidianSettings = new EuclidianSettings[euclidianLength];
		euclidianSettingsForPlane = new HashMap<>();
		resettableSettings = new HashSet<>();
		resetSettings(app);
	}

	private static EuclidianSettings createEuclidanSettings(App app, int i) {
		if (i == 2) { // 3D view
			return new EuclidianSettings3D(app);
		}

		return new EuclidianSettings(app);
	}

	/**
	 * clear settings
	 * 
	 * @param app
	 *            - app
	 */
	public void resetSettings(App app) {
		for (int i = 0; i < euclidianSettings.length; ++i) {
			if (euclidianSettings[i] == null) {
				euclidianSettings[i] = createEuclidanSettings(app, i);
			} else {
				euclidianSettings[i].reset();
			}
		}

		for (EuclidianSettings settings : euclidianSettingsForPlane.values()) {
			settings.reset();
		}

		if (spreadsheetSettings == null) {
			spreadsheetSettings = new SpreadsheetSettings();
		} else {
			spreadsheetSettings = new SpreadsheetSettings(
					spreadsheetSettings.getListeners());
		}

		if (consProtSettings == null) {
			consProtSettings = new ConstructionProtocolSettings();
		} else {
			consProtSettings = new ConstructionProtocolSettings(
					consProtSettings.getListeners());
		}

		if (layoutSettings == null) {
			layoutSettings = new LayoutSettings();
		} else {
			layoutSettings = new LayoutSettings(layoutSettings.getListeners());
		}

		if (generalSettings == null) {
			generalSettings = new GeneralSettings();
		} else {
			generalSettings = new GeneralSettings(
					generalSettings.getListeners());
		}

		if (keyboardSettings == null) {
			keyboardSettings = app.getKeyboardSettings(keyboardSettings);
		}

		if (casSettings == null) {
			casSettings = new CASSettings();
		} else {
			casSettings = new CASSettings(casSettings.getListeners());
		}
		if (!app.getConfig().isCASEnabled()) {
			casSettings.setEnabled(false);
		}

		if (probCalcSettings == null) {
			probCalcSettings = new ProbabilityCalculatorSettings();
		} else {
			probCalcSettings = new ProbabilityCalculatorSettings(
					probCalcSettings.getListeners());
		}

		tableSettings = new TableSettings();
		styleSettings = new StyleSettings();
		penToolsSettings = new PenToolsSettings();

		for (Resettable setting : resettableSettings) {
			setting.resetDefaults();
		}
	}

	/**
	 * Begin batch for all settings at once (helper).
	 * 
	 * Remark: Recommended to be used just for file loading, in other situations
	 * individual setting containers should be used to start batching.
	 */
	public void beginBatch() {
		for (EuclidianSettings settings : euclidianSettings) {
			settings.beginBatch();
		}

		for (EuclidianSettings settings : euclidianSettingsForPlane.values()) {
			settings.beginBatch();
		}

		algebraSettings.beginBatch();
		spreadsheetSettings.beginBatch();
		consProtSettings.beginBatch();
		layoutSettings.beginBatch();
		generalSettings.beginBatch();
		keyboardSettings.beginBatch();
		casSettings.beginBatch();
		probCalcSettings.beginBatch();
		tableSettings.beginBatch();
	}

	/**
	 * End batch for all settings at once (helper).
	 * 
	 * Remark: Recommended to be used just for file loading, in other situations
	 * individual setting containers should be used to end batching.
	 */
	public void endBatch() {
		for (EuclidianSettings settings : euclidianSettings) {
			settings.endBatch();
		}

		for (EuclidianSettings settings : euclidianSettingsForPlane.values()) {
			settings.endBatch();
		}

		algebraSettings.endBatch();
		spreadsheetSettings.endBatch();
		consProtSettings.endBatch();
		layoutSettings.endBatch();
		generalSettings.endBatch();
		keyboardSettings.endBatch();
		casSettings.endBatch();
		tableSettings.endBatch();
		probCalcSettings.endBatch();
	}

	/**
	 * @param number
	 *            Number of euclidian view to return settings for. Starts with
	 *            1.
	 * @return Settings of euclidian view.
	 */
	public final EuclidianSettings getEuclidian(int number) {
		return euclidianSettings[number == -1 ? 2 : number - 1];
	}
	
	/**
	 * @return - if support 3d
	 */
	public final boolean supports3D() {
		if (euclidianSettings.length <= 2) {
			return false;
		}
		return getEuclidian(-1).isEnabled();
	}

	/**
	 * 
	 * @param plane
	 *            name of the plane creator
	 * @return settings of view for this plane
	 */
	public final EuclidianSettings getEuclidianForPlane(String plane) {
		return euclidianSettingsForPlane.get(plane);
	}

	/**
	 * map the plane/settings
	 * 
	 * @param plane
	 *            name of the plane creator
	 * @param settings
	 *            settings
	 */
	public final void setEuclidianSettingsForPlane(String plane,
			EuclidianSettings settings) {
		euclidianSettingsForPlane.put(plane, settings);
	}

	/**
	 * clear all settings for plane
	 */
	public final void clearEuclidianSettingsForPlane() {
		euclidianSettingsForPlane.clear();
	}

	/**
	 * remove settings for this plane
	 * 
	 * @param plane
	 *            name of the plane creator
	 */
	public final void removeEuclidianSettingsForPlane(String plane) {
		euclidianSettingsForPlane.remove(plane);
	}

	/**
	 * @return Settings of the algebra view.
	 */
	public final AlgebraSettings getAlgebra() {
		return algebraSettings;
	}

	/**
	 * @return Settings of the spreadsheet view.
	 */
	public final SpreadsheetSettings getSpreadsheet() {
		return spreadsheetSettings;
	}

	/**
	 * @return Settings of the spreadsheet view.
	 */
	public final TableSettings getTable() {
		return this.tableSettings;
	}

	/**
	 * @return style settings (e.g. for buttons)
	 */
	public StyleSettings getStyle() {
		return styleSettings;
	}

	/**
	 * Restores spreadsheet defaults
	 */
	public void restoreDefaultSpreadsheetSettings() {
		if (spreadsheetSettings == null) {
			spreadsheetSettings = new SpreadsheetSettings();
		} else {
			spreadsheetSettings = new SpreadsheetSettings(
					spreadsheetSettings.getListeners());
		}
	}

	/**
	 * @return Settings of the probability calculator view.
	 */
	public final ProbabilityCalculatorSettings getProbCalcSettings() {
		return probCalcSettings;
	}

	/**
	 * @return Settings of the construction protocol.
	 */
	public final ConstructionProtocolSettings getConstructionProtocol() {
		return consProtSettings;
	}

	/**
	 * @return layout settings
	 */
	public final LayoutSettings getLayout() {
		return layoutSettings;
	}

	/**
	 * @return General settings of the application.
	 */
	public final GeneralSettings getGeneral() {
		return generalSettings;
	}

	/**
	 * @return desktop keyboard settings
	 */
	public final AbstractSettings getKeyboard() {
		return keyboardSettings;
	}

	/**
	 * @return CAS settings
	 */
	public final CASSettings getCasSettings() {
		return casSettings;
	}

	/**
	 * @param ev
	 *            - euclidian view
	 * @param app
	 *            - app
	 * @return -
	 */
	public EuclidianSettings getEuclidianForView(
			EuclidianViewInterfaceCommon ev, App app) {
		if (app.getEuclidianView1() == ev) {
			return getEuclidian(1);
		} else if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == ev) {
			return getEuclidian(2);
		} else if (app.isEuclidianView3D(ev)) {
			return getEuclidian(3);
		} else if (ev.isViewForPlane()) {
			return ev.getSettings();
		} else {
			return null;
		}
	}

	/**
	 * @return data analysis settings
	 */
	public DataAnalysisSettings getDataAnalysis() {
		if (this.daSettings == null) {
			daSettings = new DataAnalysisSettings();
		}
		return daSettings;
	}

	/**
	 * Reset euclidian settings for all views, no notifications.
	 */
	public void resetNoFireEuclidianSettings() {
		if (euclidianSettings == null) {
			return;
		}
		for (EuclidianSettings s : euclidianSettings) {
			if (s != null) {
				s.resetNoFire();
			}
		}
	}

	public FontSettings getFontSettings() {
		return fontSettings;
	}

	void setFontSettings(FontSettings fontSettings) {
		this.fontSettings = fontSettings;
	}

	public LabelSettings getLabelSettings() {
		return labelSettings;
	}

	void setLabelSettings(LabelSettings labelSettings) {
		this.labelSettings = labelSettings;
	}

	void setAlgebraSettings(AlgebraSettings algebraSettings) {
		this.algebraSettings = algebraSettings;
		resettableSettings.add(algebraSettings);
	}

	public PenToolsSettings getPenTools() {
		return penToolsSettings;
	}
}