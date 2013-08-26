package geogebra.touch.gui.laf;

import geogebra.common.main.SavedStateListener;
import geogebra.touch.gui.BrowseGUI;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.elements.header.BrowseHeaderPanel;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.header.WorksheetHeader;
import geogebra.touch.gui.euclidian.EuclidianViewT;
import geogebra.touch.model.TouchModel;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;

public interface LookAndFeel extends SavedStateListener {

	public void buildTabletHeader(TouchModel touchModel);

	public WorksheetHeader buildWorksheetHeader(WorksheetGUI worksheetGUI);

	public BrowseHeaderPanel buildBrowseHeader(BrowseGUI browseGUI);

	public int getTabletHeaderHeight();

	public int getBrowseHeaderHeight();

	public int getToolBarHeight();

	public int getContentWidgetHeight();

	public DefaultResources getIcons();

	public int getPaddingLeftOfDialog();

	public TabletHeaderPanel getTabletHeaderPanel();

	public boolean receivesDoubledEvents();

	public void setTitle(String title);

	public boolean isShareSupported();

	public void updateUndoSaveButtons();

	public void attachExternalEvents(EuclidianViewT euclidianViewT,
			Element element);

	public boolean useClickHandlerForOpenClose();
	
	public void resetNativeHandlers();

	public int getPopupLeft(PopupPanel inputDialog);

	public int getPopupTop(PopupPanel inputDialog);
}
