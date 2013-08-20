package geogebra.touch.gui.laf;

import geogebra.common.main.SavedStateListener;
import geogebra.touch.gui.BrowseGUI;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.header.BrowseHeaderPanel;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.header.WorksheetHeader;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;

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

	public boolean isMouseDownIgnored();

	public void setTitle(String title);

	public boolean isShareSupported();

	public void updateUndoSaveButtons();

	public FastButton setStyleBarButtonHandler(FastButton button,
			StyleBar styleBar, String process);

	public FastButton setOptionalButtonHandler(FastButton button,
			StyleBar styleBar, OptionType captionstyle);

	public FastButton setStyleBarShowHideHandler(FastButton showHideButton,
			StyleBar styleBar);

	public FastButton setAlgebraButtonHandler(FastButton arrow, TabletGUI gui);
}
