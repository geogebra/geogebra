package geogebra.html5.gui;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.Layout;
import geogebra.common.gui.layout.DockPanel;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.GuiManagerInterface;
import geogebra.web.cas.view.CASTableW;
import geogebra.web.cas.view.RowHeaderPopupMenuW;
import geogebra.web.cas.view.RowHeaderWidget;
import geogebra.web.gui.ContextMenuGeoElementW;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.inputbar.InputBarHelpPanelW;
import geogebra.web.gui.layout.panels.Euclidian2DockPanelW;
import geogebra.web.gui.toolbar.ToolBarW;
import geogebra.web.gui.view.algebra.AlgebraContextMenuW;
import geogebra.web.gui.view.algebra.AlgebraViewW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;

import java.util.ArrayList;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

public interface GuiManagerInterfaceW extends GuiManagerInterface {

	public void showPopupMenu(ArrayList<GeoElement> geos, AlgebraView invoker,
	        GPoint p);

	public AlgebraContextMenuW getAlgebraContextMenu();

	public RowHeaderPopupMenuW getCASContextMenu(RowHeaderWidget rowHeader, CASTableW table);

	public ContextMenuGeoElementW getPopupMenu(ArrayList<GeoElement> geos,
	        GPoint location);

	public void setFocusedPanel(int evID, boolean updatePropertiesView);

	public void setFocusedPanel(DockPanel panel, boolean updatePropertiesView);
	
	public boolean hasCasView();

	public SpreadsheetViewW getSpreadsheetView();

	public void resize(int width, int height);

	public ToolBarW getGeneralToolbar();

	public String getToolbarDefinition();

	public String getToolbarDefinition(Integer viewId);

	public void removeFromToolbarDefinition(int mode);

	public void addToToolbarDefinition(int mode);

	public String getCustomToolbarDefinition();

	public Layout getLayout();

	public GGWToolBar getToolbarPanel();

	public InputBarHelpPanelW getInputHelpPanel();

	public AlgebraViewW getAlgebraView();

	public void addAlgebraInput(AlgebraInputW ai);

	public AlgebraInputW getAlgebraInput();

	public Command getShowAxesAction();

	public Command getShowGridAction();

	public Euclidian2DockPanelW getEuclidianView2DockPanel(int idx);
	
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

	public MyHeaderPanel getBrowseGUI();

	public Widget getRootComponent();
}
