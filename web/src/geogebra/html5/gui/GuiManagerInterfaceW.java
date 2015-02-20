package geogebra.html5.gui;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.EuclidianStyleBar;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.GuiManager.Help;
import geogebra.common.gui.Layout;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.GuiManagerInterface;
import geogebra.html5.gui.view.browser.BrowseViewI;

import java.util.ArrayList;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

public interface GuiManagerInterfaceW extends GuiManagerInterface {

	public void showPopupMenu(ArrayList<GeoElement> geos, AlgebraView invoker,
	        GPoint p);

	public void setFocusedPanel(int evID, boolean updatePropertiesView);

	public boolean hasCasView();

	public SpreadsheetViewInterface getSpreadsheetView();

	public void resize(int width, int height);

	public String getToolbarDefinition();

	public String getToolbarDefinition(Integer viewId);

	public void removeFromToolbarDefinition(int mode);

	public void addToToolbarDefinition(int mode);

	public String getCustomToolbarDefinition();

	public Layout getLayout();

	public SetLabels getInputHelpPanel();

	public AlgebraView getAlgebraView();

	public void addAlgebraInput(AlgebraInput ai);

	public AlgebraInput getAlgebraInput();

	public Command getShowAxesAction();

	public Command getShowGridAction();

	public boolean hasProbabilityCalculator();

	public void setActiveToolbarId(int toolbarID);

	public void removePopup();

	public void setToolBarDefinition(String toolBarDefinition);

	public ConstructionProtocolNavigation getConstructionProtocolNavigation();

	public void setActiveView(int evID);

	public boolean isDraggingViews();

	public void setDraggingViews(boolean b, boolean temporary);

	public void refreshDraggingViews();

	public void setGeneralToolBarDefinition(String toolbarDefinition);

	public BrowseViewI getBrowseView(String query);

	public BrowseViewI getBrowseView();

	public void refreshCustomToolsInToolBar();

	public Widget getRootComponent();

	public void showToolBar(boolean show);

	public void showMenuBar(boolean show);

	public void showAlgebraInput(boolean show);

	public EuclidianStyleBar newEuclidianStylebar(EuclidianView ev, int viewID);

	public String getMenuBarHtml(String iconString, String name, boolean b);

	public void recalculateEnvironments();

	public String getHelpURL(Help type, String pageName);

	public void updateStyleBarPositions(boolean menuOpen);

	public void openFilePicker();

	public void listenToLogin();
}
