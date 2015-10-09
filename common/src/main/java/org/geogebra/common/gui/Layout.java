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
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.InputPositon;
import org.geogebra.common.main.settings.LayoutSettings;

/**
 * @author gabor
 * 
 *         Abstract class for Web and Desktop Layout
 *
 */
public abstract class Layout {

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
		int n = 6;

		defaultPerspectives = new Perspective[n];

		DockPanelData[] dpData;
		DockSplitPaneData[] spData;

		String defToolbar;

		// algebra & graphics (default settings of GeoGebra < 3.2)
		dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, true, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 250, 400),
				"3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,3",
				300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true,
				true, AwtFactory.prototype.newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"1,1,1", 500);
		// dpData[5] = new DockPanelData(App.VIEW_PYTHON, null, false, false,
		// false, AwtFactory.prototype.newRectangle(100, 100, 600, 600), "1,1",
		// 500);

		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", AVpercent,
				GSplitPane.HORIZONTAL_SPLIT);

		defToolbar = ToolBar.getAllToolsNoMacros(app.isHTML5Applet(),
				app.isExam());

		int i = 0; // current perspective

		defaultPerspectives[i] = new Perspective(
				"Perspective.AlgebraAndGraphics", spData, dpData, defToolbar,
				true, false, true, true, true, InputPositon.algebraView);
		defaultPerspectives[i].setIconString("menu_view_algebra.png");

		// basic geometry - just the euclidian view
		dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 250, 400),
				"3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,3",
				300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true,
				true, AwtFactory.prototype.newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
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
		defaultPerspectives[++i] = new Perspective("Perspective.Geometry",
				spData, dpData, defToolbar, true, false, false, false, true,
				InputPositon.algebraView);
		defaultPerspectives[i].setIconString("perspectives_geometry.png");
		defaultPerspectives[i].setUnitAxesRatio(true);

		// Table & Graphics - spreadsheet and euclidian view
		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", 0.45, GSplitPane.HORIZONTAL_SPLIT);

		dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 250, 400),
				"3,3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, true, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"3", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3,1",
				300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true,
				true, AwtFactory.prototype.newRectangle(100, 100, 700, 550),
				"1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"1,1", 500);

		defaultPerspectives[++i] = new Perspective("Perspective.Spreadsheet",
				spData, dpData, defToolbar, true, false, true, false, true,
				InputPositon.algebraView);
		defaultPerspectives[i].setIconString("menu_view_spreadsheet.png");
		App.debug("CAS support: " + app.supportsView(App.VIEW_CAS));
		if (app.supportsView(App.VIEW_CAS)) {
			// CAS & Graphics - cas and euclidian view
			dpData = new DockPanelData[6];
			dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true,
					false, false, AwtFactory.prototype.newRectangle(100, 100,
							600, 400), "1", 500);
			dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
					false,
					AwtFactory.prototype.newRectangle(100, 100, 250, 400),
					"3,3", 200);
			dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false,
					false, false, AwtFactory.prototype.newRectangle(100, 100,
							600, 400), "3,1", 300);
			dpData[3] = new DockPanelData(App.VIEW_CAS, null, true, false,
					false,
					AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3",
					300);
			dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false,
					true, true, AwtFactory.prototype.newRectangle(100, 100,
							700, 550), "1,1,1", 400);
			dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false,
					false, false, AwtFactory.prototype.newRectangle(100, 100,
							600, 400), "1,1", 500);

			defaultPerspectives[++i] = new Perspective("Perspective.CAS",
					spData, dpData, defToolbar, true, false, true, false, true,
					InputPositon.algebraView);
			defaultPerspectives[i].setIconString("menu_view_cas.png");
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
					false, false, AwtFactory.prototype.newRectangle(100, 100,
							600, 400), "1,3", 500);
			dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, true, false,
					false,
					AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3",
					200);
			dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false,
					false, false, AwtFactory.prototype.newRectangle(100, 100,
							600, 400), "1,1", 300);
			dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false,
					false,
					AwtFactory.prototype.newRectangle(100, 100, 600, 400),
					"1,3,3", 300);
			dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false,
					true, true, AwtFactory.prototype.newRectangle(100, 100,
							700, 550), "1,1,1,1", 400);
			dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, true,
					false, false, AwtFactory.prototype.newRectangle(100, 100,
							600, 400), "1", 500);
			// dpData[5] = new DockPanelData(App.VIEW_PYTHON, null, false,
			// false, false, AwtFactory.prototype.newRectangle(100, 100, 600,
			// 600), "1,1", 500);

			// Note: toolbar definition is always for EV1, for 3D we use
			// definition from the 3D dock panel classes

			defaultPerspectives[++i] = new Perspective(
					"Perspective.3DGraphics", spData, dpData, defToolbar, true,
					false, true, true, true, InputPositon.algebraView);
			defaultPerspectives[i]
					.setIconString("perspectives_algebra_3Dgraphics.png");
		} else {
			i++;
		}

		dpData = new DockPanelData[7];
		dpData[5] = new DockPanelData(App.VIEW_EUCLIDIAN, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"3", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 250, 400),
				"1,3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false,
				AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,3,3",
				300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true,
				true, AwtFactory.prototype.newRectangle(100, 100, 700, 550),
				"1,1,1,1", 400);
		dpData[6] = new DockPanelData(App.VIEW_EUCLIDIAN3D, null, false, false,
				false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
				"1,1", 500);
		dpData[0] = new DockPanelData(App.VIEW_PROBABILITY_CALCULATOR, null,
				true, false, false, AwtFactory.prototype.newRectangle(100, 100,
						600, 600), "1", 500);

		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", AVpercent,
				GSplitPane.HORIZONTAL_SPLIT);
		//

		// Note: toolbar definition is always for EV1, for 3D we use definition
		// from the 3D dock panel classes

		defaultPerspectives[++i] = new Perspective("Perspective.Probability",
				spData, dpData, defToolbar, false, false, true, false, true,
				InputPositon.algebraView);
		defaultPerspectives[i].setIconString("menu_view_probability.png");

		// // Python Scripting & Graphics ** Doesn't work **
		// dpData = new DockPanelData[6];
		// dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false,
		// false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1",
		// 500);
		// dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false,
		// false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3",
		// 200);
		// dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false,
		// false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400),
		// "1,1", 300);
		// dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false,
		// false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,3",
		// 300);
		// dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true,
		// false, AwtFactory.prototype.newRectangle(100, 100, 700, 550),
		// "1,1,1", 400);
		// dpData[5] = new DockPanelData(App.VIEW_PYTHON, null, true, false,
		// false, AwtFactory.prototype.newRectangle(100, 100, 600, 600), "3,1",
		// 500);
		//
		// defaultPerspectives[++i] = new Perspective("PythonAndGraphics",
		// spData, dpData, defToolbar, true, false, true, false, true,
		// InputPositon.algebraView);

	}

	protected ArrayList<Perspective> perspectives;

	/**
	 * Layout settings.
	 */
	protected LayoutSettings settings;

	/**
	 * An array with the default perspectives.
	 */
	public static Perspective[] defaultPerspectives;

	/**
	 * Set a list of perspectives as the perspectives of this user and apply the
	 * "tmp" perspective if one was found.
	 * 
	 * @param perspectives
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
			applyPerspective(defaultPerspectives[0]);
		}

	}

	protected boolean setEVsettingsFromPerspective(App app,
			Perspective perspective) {
		boolean changed = false;
		if (!perspective.getId().equals("tmp")) {
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();

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

	public abstract boolean applyPerspective(Perspective perspective);

	public abstract void getXml(StringBuilder sb, boolean asPreference);

	public abstract boolean isOnlyVisible(int viewEuclidian);

	public abstract void applyPerspective(String string);

	public abstract DockManager getDockManager();

}
