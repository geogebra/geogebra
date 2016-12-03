package org.geogebra.common.gui;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.layout.DockManager;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.DockSplitPaneData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.GSplitPane;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.LayoutSettings;
import org.geogebra.common.util.debug.Log;

/**
 * @author gabor
 * 
 *         Abstract class for Web and Desktop Layout
 *
 */
public abstract class Layout {
	/**
	 * Perspectives used by current file (usually only "tmp")
	 */
	protected ArrayList<Perspective> perspectives;

	/**
	 * Layout settings.
	 */
	protected LayoutSettings settings;

	/**
	 * An array with the default perspectives.
	 */
	private static Perspective[] defaultPerspectives;

	/**
	 * Initialize the default perspectives
	 * 
	 * @param app
	 *            app
	 * @param AVpercent
	 *            algebra width (relative to screen width, eg 0.2)
	 * 
	 */
	public static void initializeDefaultPerspectives(App app,
			double AVpercent) {
		int n = 7;

		defaultPerspectives = new Perspective[n];

		DockPanelData[] dpData;
		DockSplitPaneData[] spData;

		String defToolbar;

		// algebra & graphics (default settings of GeoGebra < 3.2)
		dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, true, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
				"3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400), "1,3",
				300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true,
				true, AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1,1", 500);
		// dpData[5] = new DockPanelData(App.VIEW_PYTHON, null, false, false,
		// false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 600), "1,1",
		// 500);

		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", AVpercent,
				GSplitPane.HORIZONTAL_SPLIT);

		defToolbar = ToolBar.getAllToolsNoMacros(app.isHTML5Applet(),
				app.isExam(), app);

		int i = 0; // current perspective

		defaultPerspectives[i] = new Perspective(1, spData, dpData, defToolbar,
				true, false, true, true, true, InputPosition.algebraView);


		// basic geometry - just the euclidian view
		dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
				"3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400), "1,3",
				300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true,
				true, AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1,1", 500);

		/*
		 * defaultPerspectives[1] = new Perspective("BasicGeometry", spData,
		 * dpData,
		 * "0 | 1 501 5 19 | 2 15 45 , 18 65 | 4 3 , 8 9 | 16 | 51 | 10 53 , 24 20 , 21 | 36 46 , 38 49 | 30 32 31 33 | 26 17 62 | 25 | 40 41 42 27 , 6"
		 * , true, false, false, false, false, false);
		 * defaultPerspectives[1].setUnitAxesRatio(true);
		 * defaultPerspectives[1].
		 * setIconString("perspectives_basic_geometry.png");
		 */

		// geometry - like basic geometry but with less toolbar entries
		defaultPerspectives[++i] = new Perspective(2,
				spData, dpData, defToolbar, true, false, false, false, true,
				InputPosition.algebraView);

		defaultPerspectives[i].setUnitAxesRatio(true);

		// Table & Graphics - spreadsheet and euclidian view
		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", 0.45, GSplitPane.HORIZONTAL_SPLIT);

		dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
				"3,3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, true, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"3", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400), "3,1",
				300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true,
				true, AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 500);

		defaultPerspectives[++i] = new Perspective(3,
				spData, dpData, defToolbar, true, false, true, false, true,
				InputPosition.algebraView);

		Log.debug("CAS support: " + app.supportsView(App.VIEW_CAS));
		if (app.supportsView(App.VIEW_CAS)) {
			// CAS & Graphics - cas and euclidian view
			dpData = new DockPanelData[6];
			dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true,
					false, false, AwtFactory.getPrototype().newRectangle(100, 100,
							600, 400), "1", 500);
			dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
					false,
					AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
					"3,3", 200);
			dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false,
					false, false, AwtFactory.getPrototype().newRectangle(100, 100,
							600, 400), "3,1", 300);
			dpData[3] = new DockPanelData(App.VIEW_CAS, null, true, false,
					false,
					AwtFactory.getPrototype().newRectangle(100, 100, 600, 400), "3",
					300);
			dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false,
					true, true, AwtFactory.getPrototype().newRectangle(100, 100,
							700, 550), "1,1,1", 400);
			dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false,
					false, false, AwtFactory.getPrototype().newRectangle(100, 100,
							600, 400), "1,1", 500);

			defaultPerspectives[++i] = new Perspective(4,
					spData, dpData, defToolbar, true, false, true, false, true,
					InputPosition.algebraView);

		} else {
			i++;
		}

		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", AVpercent,
				GSplitPane.HORIZONTAL_SPLIT);

		if (app.supportsView(App.VIEW_EUCLIDIAN3D)) {
			// algebra & 3D graphics
			dpData = new DockPanelData[6];
			dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN, null, false,
					false, false, AwtFactory.getPrototype().newRectangle(100, 100,
							600, 400), "1,3", 500);
			dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, true, false,
					false,
					AwtFactory.getPrototype().newRectangle(100, 100, 250, 400), "3",
					200);
			dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false,
					false, false, AwtFactory.getPrototype().newRectangle(100, 100,
							600, 400), "1,1", 300);
			dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false,
					false,
					AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
					"1,3,3", 300);
			dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false,
					true, true, AwtFactory.getPrototype().newRectangle(100, 100,
							700, 550), "1,1,1,1", 400);
			dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, true,
					false, false, AwtFactory.getPrototype().newRectangle(100, 100,
							600, 400), "1", 500);
			// dpData[5] = new DockPanelData(App.VIEW_PYTHON, null, false,
			// false, false, AwtFactory.getPrototype().newRectangle(100, 100, 600,
			// 600), "1,1", 500);

			// Note: toolbar definition is always for EV1, for 3D we use
			// definition from the 3D dock panel classes

			defaultPerspectives[++i] = new Perspective(5, spData, dpData,
					defToolbar, true, false, true, true, true,
					InputPosition.algebraView);
		} else {
			i++;
		}

		dpData = new DockPanelData[7];
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"3", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 250, 400),
				"1,3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.getPrototype().newRectangle(100, 100, 600, 400), "1,3,3",
				300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true,
				true, AwtFactory.getPrototype().newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[6] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false, AwtFactory.getPrototype().newRectangle(100, 100, 600, 400),
				"1,1", 500);
		dpData[0] = new DockPanelData(App.VIEW_PROBABILITY_CALCULATOR, null,
				true, false, false, AwtFactory.getPrototype().newRectangle(100, 100,
						600, 600), "1", 500);

		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", AVpercent,
				GSplitPane.HORIZONTAL_SPLIT);
		//

		// Note: toolbar definition is always for EV1, for 3D we use definition
		// from the 3D dock panel classes

		defaultPerspectives[++i] = new Perspective(6,
				spData, dpData, defToolbar, false, false, true, false, true,
				InputPosition.algebraView);
		if (app.has(Feature.WHITEBOARD_APP)) {
			dpData = new DockPanelData[6];
			dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
					false,
					AwtFactory.getPrototype().newRectangle(100, 100, 600, 400), "1",
					500);
			dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
					false,
					AwtFactory.getPrototype().newRectangle(100, 100, 250, 400), "3",
					200);
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

			// tring wbToolbar = "0 | 1 501 5 19 | 2 15 45 , 18 65 | 4 3 , 8 9 |
			// 16 | 51 | 10 53 , 24 20 , 21 | 36 46 , 38 49 | 30 32 31 33 | 26
			// 17 62 | 25 | 40 41 42 27 , 6";
			//String wbToolbar = "0 | 62 73 6 110 | 2 16 51 10 55 | 102 103 104 105 106 107 108 109 101| 17 26";
			String wbToolbar = ToolBar.getWBToolBarDefString();
			Perspective whiteboard = new Perspective(7, spData, dpData,
					wbToolbar, true, false, false, false, true,
					InputPosition.algebraView);
			whiteboard.setToolBarPosition(SwingConstants.SOUTH);
			// whiteboard
			defaultPerspectives[++i] = whiteboard;
		}

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
			this.perspectives = new ArrayList<Perspective>();
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
	protected Perspective getDefaultPerspective(){
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
		if (!perspective.getId().equals("tmp")) {
			Log.debug("App is" + app);
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
			Log.debug("Ev is" + ev);
			if (app.getEuclidianView1() == ev)
				changed |= app
						.getSettings()
						.getEuclidian(1)
						.setShowAxes(perspective.getShowAxes(),
								perspective.getShowAxes());

			else if (app.hasEuclidianView2EitherShowingOrNot(1)
					&& app.getEuclidianView2(1) == ev)
				changed |= app
						.getSettings()
						.getEuclidian(2)
						.setShowAxes(perspective.getShowAxes(),
								perspective.getShowAxes());
			else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == ev)
				changed |= app.getSettings().getEuclidian(3)
						.setShowAxes(perspective.getShowAxes());

			else
				changed |= ev.setShowAxes(perspective.getShowAxes(), false);

			if (app.getEuclidianView1() == ev)
				changed |= app.getSettings().getEuclidian(1)
						.showGrid(perspective.getShowGrid());

			else if (app.hasEuclidianView2EitherShowingOrNot(1)
					&& app.getEuclidianView2(1) == ev)
				changed |= app.getSettings().getEuclidian(2)
						.showGrid(perspective.getShowGrid());
			else if (app.hasEuclidianView3D() && app.getEuclidianView3D() == ev)
				changed |= app.getSettings().getEuclidian(3)
						.showGrid(perspective.getShowGrid());

			else
				changed |= ev.showGrid(perspective.getShowGrid());

			// ev.setUnitAxesRatio(perspective.isUnitAxesRatio());
		}

		return changed;
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
		if (tmpPerspective != null)
			sb.append(tmpPerspective.getXml());

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

	public abstract Perspective createPerspective(String string);

	public abstract boolean applyPerspective(Perspective perspective);

	public abstract boolean isOnlyVisible(int viewEuclidian);

	public abstract void applyPerspective(String string);

	public abstract DockManager getDockManager();

}
