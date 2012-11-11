package geogebra.common.main.settings;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

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
	private final EuclidianSettings[] euclidianSettings;
	
	private HashMap<String, EuclidianSettings> euclidianSettingsForPlane;

	private AlgebraSettings algebraSettings;

	private SpreadsheetSettings spreadsheetSettings;

	private ConstructionProtocolSettings consProtSettings;

	private LayoutSettings layoutSettings;

	private ApplicationSettings applicationSettings;

	private KeyboardSettings keyboardSettings;

	private CASSettings casSettings;

	private ProbabilityCalculatorSettings probCalcSettings;

	/**
	 * Initialize settings using the constructors of the setting container
	 * classes.
	 */
	public Settings() {
		euclidianSettings = new EuclidianSettings[2];
		
		euclidianSettingsForPlane = new HashMap<String, EuclidianSettings>();

		resetSettings();
	}

	public void resetSettings() {
		for (int i = 0; i < euclidianSettings.length; ++i) {
			if (euclidianSettings[i] == null) {
				euclidianSettings[i] = new EuclidianSettings(i == 0 ? null
						: euclidianSettings[0]);
			} else {
				LinkedList<SettingListener> ls = euclidianSettings[i]
						.getListeners();
				euclidianSettings[i] = new EuclidianSettings(i == 0 ? null
						: euclidianSettings[0]);
				Iterator<SettingListener> lsi = ls.iterator();
				while (lsi.hasNext()) {
					SettingListener a = lsi.next();
					euclidianSettings[i].addListener(a);
				}
			}
		}
		
		for (EuclidianSettings settings : euclidianSettingsForPlane.values()){
			settings.reset();			
		}

		if (algebraSettings == null) {
			algebraSettings = new AlgebraSettings();
		} else {
			algebraSettings = new AlgebraSettings(
					algebraSettings.getListeners());
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

		if (applicationSettings == null) {
			applicationSettings = new ApplicationSettings();
		} else {
			applicationSettings = new ApplicationSettings(
					applicationSettings.getListeners());
		}

		if (keyboardSettings == null) {
			keyboardSettings = new KeyboardSettings();
		} else {
			keyboardSettings = new KeyboardSettings(
					keyboardSettings.getListeners());
		}

		if (casSettings == null) {
			casSettings = new CASSettings();
		} else {
			casSettings = new CASSettings(algebraSettings.getListeners());
		}

		if (probCalcSettings == null) {
			probCalcSettings = new ProbabilityCalculatorSettings();
		} else {
			probCalcSettings = new ProbabilityCalculatorSettings(
					probCalcSettings.getListeners());
		}

	}

	/**
	 * Begin batch for all settings at once (helper).
	 * 
	 * @remark Recommended to be used just for file loading, in other situations
	 *         individual setting containers should be used to start batching.
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
		applicationSettings.beginBatch();
		keyboardSettings.beginBatch();
		casSettings.beginBatch();
		probCalcSettings.beginBatch();
	}

	/**
	 * End batch for all settings at once (helper).
	 * 
	 * @remark Recommended to be used just for file loading, in other situations
	 *         individual setting containers should be used to end batching.
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
		applicationSettings.endBatch();
		keyboardSettings.endBatch();
		casSettings.endBatch();
		probCalcSettings.endBatch();
	}

	/**
	 * @param number
	 *            Number of euclidian view to return settings for. Starts with
	 *            1.
	 * @return Settings of euclidian view.
	 */
	public final EuclidianSettings getEuclidian(int number) {
		return euclidianSettings[number - 1];
	}
	
	
	/**
	 * 
	 * @param plane name of the plane creator
	 * @return settings of view for this plane
	 */
	public final EuclidianSettings getEuclidianForPlane(String plane){
		return euclidianSettingsForPlane.get(plane);
	}

	
	/**
	 * 
	 * @param plane name of the plane creator
	 * @return settings of view for this plane
	 */
	public final EuclidianSettings createEuclidianForPlane(String plane){
		EuclidianSettings settings = new EuclidianSettings(null);
		euclidianSettingsForPlane.put(plane, settings);
		return settings;
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

	public final LayoutSettings getLayout() {
		return layoutSettings;
	}

	/**
	 * @return General settings of the application.
	 */
	public final ApplicationSettings getApplication() {
		return applicationSettings;
	}

	public final KeyboardSettings getKeyboard() {
		return keyboardSettings;
	}

	public final CASSettings getCasSettings() {
		return casSettings;
	}
}