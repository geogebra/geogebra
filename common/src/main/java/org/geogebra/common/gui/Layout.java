package org.geogebra.common.gui;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.layout.DockManager;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.DockSplitPaneData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.main.settings.LayoutSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.util.debug.Log;

/**
 * @author gabor
 * 
 *         Abstract class for Web and Desktop Layout
 *
 */
public abstract class Layout implements SettingListener {
	private static final double PORTRAIT_DIVIDER = 0.45;

	/**
	 * Perspectives used by current file (usually only "tmp")
	 */
	protected ArrayList<Perspective> perspectives;

	/**
	 * Layout settings.
	 */
	protected LayoutSettings settings;

	private boolean isInitialized = false;

	/**
	 * An array with the default perspectives.
	 */
	private static Perspective[] defaultPerspectives;

	/**
	 * Initialize the default perspectives
	 * 
	 * @param app
	 *            app
	 * @param avPercent
	 *            algebra width (relative to screen width, eg 0.2)
	 * 
	 */
	public static void initializeDefaultPerspectives(App app, double avPercent) {
		List<Perspective> perspectives = new ArrayList<>();

		DockSplitPaneData[] spData = getSPData(app, avPercent);
		String defToolbar = ToolBar.getAllToolsNoMacros(app.isHTML5Applet(),
				app.isExam(), app);

		// algebra & graphics (default settings of GeoGebra < 3.2)
		Perspective graphing = createGraphingPerspective(app, spData, defToolbar);
		perspectives.add(graphing);

		// geometry
		Perspective geometry = createGeometryPerspective(app, spData, defToolbar);
		perspectives.add(geometry);

		// Table & Graphics - spreadsheet and euclidian view
		Perspective spreadsheet = createSpreadsheetPerspective(defToolbar);
		perspectives.add(spreadsheet);

		boolean supportsCas = app.supportsView(App.VIEW_CAS);
		Log.debug("CAS support: " + supportsCas);
		Perspective cas = supportsCas ? createCasPerspective(spData, defToolbar) : null;
		perspectives.add(cas);

		boolean supports3D = app.supportsView(App.VIEW_EUCLIDIAN3D);
		Perspective graphing3D = supports3D
				? createGraphing3DPerspective(app, spData, defToolbar)
				: null;

		perspectives.add(graphing3D);

		Perspective probability = createProbabilityPerspective(avPercent, defToolbar);
		perspectives.add(probability);

		if (app.isWhiteboardActive()) {
			Perspective whiteboard = createWhiteboardPerspective(spData);
			perspectives.add(whiteboard);
		}

		Perspective scientific = createScientificPerspective(app, avPercent);
		perspectives.add(scientific);

		Perspective evaluator = createEvaluatorPerspective();
		perspectives.add(evaluator);

		defaultPerspectives = perspectives.toArray(new Perspective[0]);
	}

	private static Perspective createGraphingPerspective(App app, DockSplitPaneData[] spData,
														 String defToolbar) {
		DockPanelData[] dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				app.isPortrait() ? "3" : "1",
				500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, true, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				app.isPortrait() ? "1" : "3",
				200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,3", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, false,
				true,
				AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1,1", 500);

		return new Perspective(Perspective.GRAPHING, spData, dpData, defToolbar,
				true, true, true, true, true, InputPosition.algebraView);
	}

	private static Perspective createGeometryPerspective(App app, DockSplitPaneData[] spData,
														 String defToolbar) {
		DockPanelData[] dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				app.isPortrait() ? "3" : "1",
				500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null,
				app.isUnbundled(),
				false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
				app.isPortrait() ? "1" : "3",
				200).setTabId(getGeometryTabId(app));
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,3", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, false,
				true,
				AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1,1", 500);

		Perspective perspective = new Perspective(Perspective.GEOMETRY, spData,
				dpData, defToolbar, true, false, false, false, true,
				InputPosition.algebraView);
		perspective.setUnitAxesRatio(true);
		perspective.setLabelingStyle(ConstructionDefaults.LABEL_VISIBLE_POINTS_ONLY);

		return perspective;
	}

	private static DockPanelData.TabIds getGeometryTabId(App app) {
		return app.isUnbundled() ? DockPanelData.TabIds.TOOLS : DockPanelData.TabIds.ALGEBRA;
	}

	private static Perspective createSpreadsheetPerspective(String defToolbar) {
		// Table & Graphics - spreadsheet and euclidian view
		DockSplitPaneData[] spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", 0.45,
				SwingConstants.HORIZONTAL_SPLIT);

		DockPanelData[] dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400), "1",
				500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
				"3,3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, true, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400), "3",
				300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"3,1", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, false,
				true,
				AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 500);

		return new Perspective(Perspective.SPREADSHEET, spData, dpData,
				defToolbar, true, false, true, false, true,
				InputPosition.algebraView);
	}

	private static Perspective createCasPerspective(DockSplitPaneData[] spData, String defToolbar) {
		DockPanelData[] dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
				"3,3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false,
				false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"3,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, true, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"3", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false,
				true, true,
				AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false,
				false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 500);

		return new Perspective(Perspective.CAS, spData, dpData,
				defToolbar, true, false, true, false, true,
				InputPosition.algebraView);
	}

	private static Perspective createGraphing3DPerspective(App app, DockSplitPaneData[] spData,
														   String defToolbar) {
		DockPanelData[] dpData = new DockPanelData[6];
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN, null, false,
				false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,3", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, true, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
				app.isPortrait() ? "1" : "3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false,
				false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,3,3", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false,
				true, true,
				AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, true,
				false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				app.isPortrait() ? "3" : "1", 500);

		// Note: toolbar definition is always for EV1, for 3D we use
		// definition from the 3D dock panel classes
		return new Perspective(Perspective.GRAPHER_3D, spData, dpData,
				defToolbar, true, false, true, true, true,
				InputPosition.algebraView);
	}

	private static Perspective createProbabilityPerspective(double avPercent, String defToolbar) {
		DockPanelData[] dpData = new DockPanelData[7];
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400), "3",
				500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
				"1,3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,3,3", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, false,
				true,
				AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[6] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 500);
		dpData[0] = new DockPanelData(App.VIEW_PROBABILITY_CALCULATOR, null,
				true, false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 600), "1",
				500);

		DockSplitPaneData[] spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", avPercent,
				SwingConstants.HORIZONTAL_SPLIT);

		return new Perspective(Perspective.PROBABILITY, spData, dpData,
				defToolbar, false, false, true, false, true,
				InputPosition.algebraView);
	}

	private static Perspective createWhiteboardPerspective(DockSplitPaneData[] spData) {
		DockPanelData[] dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
				"3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false,
				false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,3", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false,
				true, true,
				AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false,
				false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1,1", 500);

		String wbToolbar = ToolBar.getWBToolBarDefString();
		Perspective whiteboard = new Perspective(Perspective.NOTES, spData, dpData,
				wbToolbar, true, false, false, false, true,
				InputPosition.algebraView);
		whiteboard.setToolBarPosition(SwingConstants.SOUTH);

		return whiteboard;
	}

	private static Perspective createScientificPerspective(App app, double avPercent) {
		DockPanelData[] dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400), "3",
				500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, true, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 250, 400), "1",
				200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,3", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, false,
				true,
				AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1,1", 500);

		DockSplitPaneData[] spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", avPercent,
				SwingConstants.HORIZONTAL_SPLIT);

		String defToolbar = ToolBar.getAllToolsNoMacros(app.isHTML5Applet(),
				app.isExam(), app);

		return new Perspective(Perspective.SCIENTIFIC, spData, dpData,
				defToolbar, true, true, true, true, true,
				InputPosition.algebraView);
	}

	private static Perspective createEvaluatorPerspective() {
		return new Perspective(Perspective.EVALUATOR,
				new DockSplitPaneData[0], new DockPanelData[0], "", false, false,
				false, true, false,
				InputPosition.algebraView);
	}

	/**
	 * Set a list of perspectives as the perspectives of this user and apply the
	 * "tmp" perspective if one was found.
	 * 
	 * @param perspectives
	 *            default perspectives
	 * @param customPerspective
	 *            user defined perspective (in xml saved as tmp)
	 * 
	 */
	public void setPerspectives(ArrayList<Perspective> perspectives,
			Perspective customPerspective) {
		boolean foundTmp = false;

		if (perspectives != null) {
			this.perspectives = perspectives;

			for (Perspective perspective : perspectives) {
				if (perspective.getId().equals("tmp")) {
					perspectives.remove(perspective);
					if (customPerspective == null) {
						applyPerspective(perspective);
					}
					foundTmp = true;
					break;
				}
			}
		} else {
			this.perspectives = new ArrayList<>();
		}

		if (customPerspective != null) {
			applyPerspective(customPerspective);
			return;
		}
		if (!foundTmp) {
			applyPerspective(getDefaultPerspective());
		}

	}

	/**
	 * @return default perspective 0 (graphing)
	 */
	protected Perspective getDefaultPerspective() {
		return defaultPerspectives[0];
	}

	/**
	 * @param i
	 *            index
	 * @return default perspective at given index
	 */
	public static Perspective getDefaultPerspectives(int i) {
		return defaultPerspectives[i];
	}

	/**
	 * @return number of default perspectives
	 */
	public static int getDefaultPerspectivesLength() {
		return defaultPerspectives.length;
	}

	/**
	 * Sets the grid and axes from perspective
	 * 
	 * @param app
	 *            app to receive the settings
	 * @param perspective
	 *            perspective
	 * @return true if changed
	 */
	protected boolean setEVsettingsFromPerspective(App app,
			Perspective perspective) {
		boolean changed = false;
		if (!"tmp".equals(perspective.getId())) {
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			EuclidianSettings euclidianSettings = getEuclidianSettings(app);
			if (euclidianSettings != null) {
				changed =
						euclidianSettings.setShowAxes(
								perspective.getShowAxes(),
								perspective.getShowAxes());
				changed |= euclidianSettings.showGrid(perspective.getShowGrid());

				if (app.isEuclidianView3D(ev)) {
					changed |= ((EuclidianSettings3D) euclidianSettings).setHasColoredAxes(true);
				}

				euclidianSettings.setDefaultLabelingStyle(perspective.getLabelingStyle());
			} else {
				changed = ev.setShowAxes(perspective.getShowAxes(), false);
				changed |= ev.showGrid(perspective.getShowGrid());
			}
		}

		return changed;
	}

	private EuclidianSettings getEuclidianSettings(App app) {
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		Settings settings = app.getSettings();
		if (app.getEuclidianView1() == ev) {
			return settings.getEuclidian(1);
		} else if (app.hasEuclidianView2EitherShowingOrNot(1)
				&& app.getEuclidianView2(1) == ev) {
			return settings.getEuclidian(2);
		} else if (app.isEuclidianView3D(ev)) {
			return settings.getEuclidian(3);
		} else {
			return null;
		}
	}

	/**
	 * Append current perspective XML to builder
	 * 
	 * @param sb
	 *            xml builder
	 */
	public void getCurrentPerspectiveXML(StringBuilder sb) {
		/**
		 * Create a temporary perspective which is used to store the layout of
		 * the document at the moment. This perspective isn't accessible through
		 * the menu and will be removed as soon as the document was saved with
		 * another perspective.
		 */
		Perspective tmpPerspective = createPerspective("tmp");
		// save the current perspective
		if (tmpPerspective != null) {
			sb.append(tmpPerspective.getXml());
		}

	}

	/**
	 * Return the layout as XML.
	 * 
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            If the collected data is used for the preferences
	 */
	public final void getXml(StringBuilder sb, boolean asPreference) {

		sb.append("\t<perspectives>\n");

		// save the current perspective
		getCurrentPerspectiveXML(sb);

		// save all custom perspectives as well
		for (Perspective perspective : perspectives) {
			// skip old temporary perspectives
			if (perspective.getId().equals("tmp")) {
				continue;
			}

			sb.append(perspective.getXml());
		}

		sb.append("\t</perspectives>\n");

		/**
		 * Certain user elements should be just saved as preferences and not if
		 * a document is saved normally as they just depend on the preferences
		 * of the user.
		 */
		if (asPreference) {
			sb.append("\t<settings ignoreDocument=\"");
			sb.append(settings.isIgnoringDocumentLayout());
			sb.append("\" showTitleBar=\"");
			sb.append(settings.showTitleBar());
			sb.append("\" allowStyleBar=\"");
			sb.append(settings.isAllowingStyleBar());
			sb.append("\" />\n");
		}

	}

	/**
	 * @param app
	 *            application
	 * @return whether initialization was needed
	 */
	protected boolean initializeCommon(App app) {
		if (isInitialized) {
			return false;
		}
		isInitialized = true;

		this.settings = app.getSettings().getLayout();
		this.settings.addListener(this);
		return true;
	}

	/**
	 * @param string
	 *            perspective ID
	 * @return perspective
	 */
	public abstract Perspective createPerspective(String string);

	/**
	 * @param perspective
	 *            perspective
	 * @return whether EV settings changed
	 */
	public abstract boolean applyPerspective(Perspective perspective);

	/**
	 * @param viewID
	 *            view id
	 * @return whether given view is the only visible one
	 */
	public abstract boolean isOnlyVisible(int viewID);

	/**
	 * @param string
	 *            perspective ID
	 */
	public abstract void applyPerspective(String string);

	/**
	 * @return dock manager
	 */
	public abstract DockManager getDockManager();

	private static DockSplitPaneData[] getSPData(App app, double avPercent) {
		DockSplitPaneData[] spData = new DockSplitPaneData[1];
		if (app.isPortrait()) {
			spData[0] = new DockSplitPaneData("", PORTRAIT_DIVIDER,
					SwingConstants.VERTICAL_SPLIT);

		} else {
			spData[0] = new DockSplitPaneData("", avPercent,
					SwingConstants.HORIZONTAL_SPLIT);
		}
		return spData;

	}
}
