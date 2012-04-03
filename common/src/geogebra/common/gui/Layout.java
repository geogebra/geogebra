package geogebra.common.gui;

import geogebra.common.factories.AwtFactory;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.io.layout.DockSplitPaneData;
import geogebra.common.io.layout.Perspective;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.settings.LayoutSettings;

import java.util.ArrayList;

import javax.swing.JSplitPane;

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
	protected static void initializeDefaultPerspectives() {
		defaultPerspectives = new Perspective[5];
		
		DockPanelData[] dpData;
		DockSplitPaneData[] spData;
		
		String defToolbar;
	
		// algebra & graphics (default settings of GeoGebra < 3.2)
		dpData = new DockPanelData[4];
		dpData[0] = new DockPanelData(AbstractApplication.VIEW_EUCLIDIAN, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(AbstractApplication.VIEW_ALGEBRA, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3", 200);
		dpData[2] = new DockPanelData(AbstractApplication.VIEW_SPREADSHEET, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,1", 300);
		dpData[3] = new DockPanelData(AbstractApplication.VIEW_CAS, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,3", 300);
	
		spData = new DockSplitPaneData[1];
		//TODO: use JSplitPane.HORIZONTAL_SPLIT as earlier
		//spData[0] = new DockSplitPaneData("", 0.25, JSplitPane.HORIZONTAL_SPLIT);
		spData[0] = new DockSplitPaneData("", 0.25, 1);
	
		defToolbar = ToolBar.getAllToolsNoMacros();
	
		defaultPerspectives[0] = new Perspective("AlgebraAndGraphics", spData, dpData, defToolbar, true, false, true, true, true, false);
		
		// basic geometry - just the euclidian view
		dpData = new DockPanelData[4];
		dpData[0] = new DockPanelData(AbstractApplication.VIEW_EUCLIDIAN, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(AbstractApplication.VIEW_ALGEBRA, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3", 200);
		dpData[2] = new DockPanelData(AbstractApplication.VIEW_SPREADSHEET, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,1", 300);
		dpData[3] = new DockPanelData(AbstractApplication.VIEW_CAS, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1,3", 300);
		
		defaultPerspectives[1] = new Perspective("BasicGeometry", spData, dpData, "0 | 40 | 1 | 19 | 15 | 2 | 10 | 3 | 4 | 5 | 16 | 64 | 70 | 51 | 17 | 36 | 30 | 32 ", true, false, false, false, false, false);
		defaultPerspectives[1].setUnitAxesRatio(true);
		
		// geometry - like basic geometry but with less toolbar entries
		defaultPerspectives[2] = new Perspective("Geometry", spData, dpData, defToolbar, true, false, false, false, true, false);
		
		// Table & Graphics - spreadsheet and euclidian view
		spData = new DockSplitPaneData[1];
		//TODO: use JSplitPane.HORIZONTAL_SPLIT as earlier
		//spData[0] = new DockSplitPaneData("", 0.45, JSplitPane.HORIZONTAL_SPLIT);
		spData[0] = new DockSplitPaneData("", 0.45, 1);
		
		dpData = new DockPanelData[4];
		dpData[0] = new DockPanelData(AbstractApplication.VIEW_EUCLIDIAN, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(AbstractApplication.VIEW_ALGEBRA, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3,3", 200);
		dpData[2] = new DockPanelData(AbstractApplication.VIEW_SPREADSHEET, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3", 300);
		dpData[3] = new DockPanelData(AbstractApplication.VIEW_CAS, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3,1", 300);
		
		defaultPerspectives[3] = new Perspective("TableAndGraphics", spData, dpData, defToolbar, true, false, true, false, true, false);
		
		// CAS & Graphics - cas and euclidian view
		dpData = new DockPanelData[4];
		dpData[0] = new DockPanelData(AbstractApplication.VIEW_EUCLIDIAN, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "1", 500);
		dpData[1] = new DockPanelData(AbstractApplication.VIEW_ALGEBRA, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 250, 400), "3,3", 200);
		dpData[2] = new DockPanelData(AbstractApplication.VIEW_SPREADSHEET, null, false, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3,1", 300);
		dpData[3] = new DockPanelData(AbstractApplication.VIEW_CAS, null, true, false, false, AwtFactory.prototype.newRectangle(100, 100, 600, 400), "3", 300);
		
		defaultPerspectives[4] = new Perspective("CASAndGraphics", spData, dpData, defToolbar, true, false, true, false, true, false);
	
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

}
