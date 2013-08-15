package geogebra.web.gui;

import geogebra.common.awt.GPoint;
import geogebra.common.gui.GuiManagerInterface;
import geogebra.common.gui.layout.DockPanel;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.web.cas.view.RowHeaderPopupMenuW;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.inputbar.InputBarHelpPanelW;
import geogebra.web.gui.layout.LayoutW;
import geogebra.web.gui.layout.panels.Euclidian2DockPanelW;
import geogebra.web.gui.toolbar.ToolBarW;
import geogebra.web.gui.view.algebra.AlgebraContextMenuW;
import geogebra.web.gui.view.algebra.AlgebraViewW;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;

import java.util.ArrayList;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Command;

public interface GuiManagerInterfaceW extends GuiManagerInterface {

	public void showPopupMenu(ArrayList<GeoElement> geos, AlgebraView invoker,
	        GPoint p);

	public AlgebraContextMenuW getAlgebraContextMenu();

	public RowHeaderPopupMenuW getCASContextMenu();

	public ContextMenuGeoElementW getPopupMenu(ArrayList<GeoElement> geos,
	        GPoint location);

	public void setFocusedPanel(NativeEvent e, boolean updatePropertiesView);

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

	public LayoutW getLayout();

	public GGWToolBar getToolbarPanel();

	public InputBarHelpPanelW getInputHelpPanel();

	public AlgebraViewW getAlgebraView();

	public void addAlgebraInput(AlgebraInputW ai);

	public AlgebraInputW getAlgebraInput();

	public Command getShowAxesAction();

	public Command getShowGridAction();

	public Euclidian2DockPanelW getEuclidianView2DockPanel();

	public void setActiveToolbarId(int toolbarID);

	public void openFromGoogleDrive();

	public void openFromSkyDrive();

	public void removePopup();

	public void signIn();

	public void setToolBarDefinition(String toolBarDefinition);
}
