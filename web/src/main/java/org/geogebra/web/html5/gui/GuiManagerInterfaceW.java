package org.geogebra.web.html5.gui;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.GuiManager.Help;
import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetViewInterface;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.html5.gui.view.browser.BrowseViewI;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;
import org.geogebra.web.html5.util.keyboard.VirtualKeyboard;

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

	public ConstructionProtocolNavigation getConstructionProtocolNavigation(
			int id);

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

	public VirtualKeyboard getOnScreenKeyboard(MathKeyboardListener textField,
			UpdateKeyBoardListener listener);

	public void setOnScreenKeyboardTextField(MathKeyboardListener textField);
}
