package geogebra.common.gui;

import geogebra.common.factories.AwtFactory;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.DockSplitPaneData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.javax.swing.GSplitPane;
import geogebra.common.main.App;
import geogebra.common.main.settings.LayoutSettings;

import java.util.ArrayList;

/**
 * @author gabor
 * 
 * Abstract class for Web and Desktop Layout
 *
 */
public abstract class Layout {

	/**
	 * Initialize the default perspectives
	 * 	 
	 */
	protected static void initializeDefaultPerspectives(boolean showAllTools, boolean html5) {
		defaultPerspectives = new Perspective[5];
		
		DockPanelData[] dpData;
		DockSplitPaneData[] spData;
		
		String defToolbar;
	
		// algebra & graphics (default settings of GeoGebra < 3.2)
		dpData = new DockPanelData[6];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,3", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true, true, AwtFactory.prototype.newRectangle(100, 100, 700, 550), "1,1,1", 400);
		dpData[5] = new DockPanelData(App.VIEW_PYTHON, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 600), "1,1", 500);
		
		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", 0.25, GSplitPane.HORIZONTAL_SPLIT);
	
		defToolbar = ToolBar.getAllToolsNoMacros(showAllTools, html5);
	
		defaultPerspectives[0] = new Perspective("AlgebraAndGraphics", spData, dpData, defToolbar, true, false, true, true, true, false);
		defaultPerspectives[0].setIconString("menu_view_algebra.png");
		
		// basic geometry - just the euclidian view
		dpData = new DockPanelData[5];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,3", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true, true, AwtFactory.prototype.newRectangle(100, 100, 700, 550), "1,1,1", 400);
		
		defaultPerspectives[1] = new Perspective("BasicGeometry", spData, dpData, "0 | 40 | 1 | 19 | 15 | 2 | 10 | 3 | 4 | 5 | 16 | 64 | 70 | 51 | 17 | 36 | 30 | 32 ", true, false, false, false, false, false);
		defaultPerspectives[1].setUnitAxesRatio(true);
		defaultPerspectives[1].setIconString("menu_view_geometry.png");
		
		// geometry - like basic geometry but with less toolbar entries
		defaultPerspectives[2] = new Perspective("Geometry", spData, dpData, defToolbar, true, false, false, false, true, false);
		defaultPerspectives[2].setIconString("menu_view_graphics.png");
		
		// Table & Graphics - spreadsheet and euclidian view
		spData = new DockSplitPaneData[1];
		spData[0] = new DockSplitPaneData("", 0.45, GSplitPane.HORIZONTAL_SPLIT);
		
		dpData = new DockPanelData[5];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3,3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3,1", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true, true, AwtFactory.prototype.newRectangle(100, 100, 700, 550), "1,1", 400);
		
		defaultPerspectives[3] = new Perspective("TableAndGraphics", spData, dpData, defToolbar, true, false, true, false, true, false);
		defaultPerspectives[3].setIconString("menu_view_spreadsheet.png");
		
		// CAS & Graphics - cas and euclidian view
		dpData = new DockPanelData[5];
		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3,3", 200);
		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3,1", 300);
		dpData[3] = new DockPanelData(App.VIEW_CAS, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3", 300);
		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true, true, AwtFactory.prototype.newRectangle(100, 100, 700, 550), "1,1", 400);
		
		defaultPerspectives[4] = new Perspective("CASAndGraphics", spData, dpData, defToolbar, true, false, true, false, true, false);
		defaultPerspectives[4].setIconString("menu_view_cas.png");
		
//		// Python Scripting & Graphocs ** Doesn't work **
//		dpData = new DockPanelData[6];
//		dpData[0] = new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1", 500);
//		dpData[1] = new DockPanelData(App.VIEW_ALGEBRA, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3", 200);
//		dpData[2] = new DockPanelData(App.VIEW_SPREADSHEET, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,1", 300);
//		dpData[3] = new DockPanelData(App.VIEW_CAS, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,3", 300);
//		dpData[4] = new DockPanelData(App.VIEW_PROPERTIES, null, false, true, false, AwtFactory.prototype.newRectangle(100, 100, 700, 550), "1,1,1", 400);
//		dpData[5] = new DockPanelData(App.VIEW_PYTHON, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 600), "3,1", 500);
//
//		defaultPerspectives[5] = new Perspective("PythonAndGraphics", spData, dpData, defToolbar, true, false, true, false, true, false);

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
	 * Set a list of perspectives as the perspectives of this user and
	 * apply the "tmp" perspective if one was found.
	 * 
	 * @param perspectives
	 */
	public void setPerspectives(ArrayList<Perspective> perspectives) {
		boolean foundTmp = false;
		
		if(perspectives != null) {
			this.perspectives = perspectives;
			
			for(Perspective perspective : perspectives) {
				if(perspective.getId().equals("tmp")) {
					perspectives.remove(perspective);
					applyPerspective(perspective);
					foundTmp = true;
					break;
				}
			}
		} else {
			this.perspectives = new ArrayList<Perspective>();
		}
		
		if(!foundTmp) {
			applyPerspective(defaultPerspectives[0]);
		}
	}

	abstract protected void applyPerspective(Perspective perspective);

	public abstract void getXml(StringBuilder sb, boolean asPreference);

	public abstract boolean isOnlyVisible(int viewEuclidian);

	public abstract void applyPerspective(String string);

}
