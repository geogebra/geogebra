package geogebra.touch.gui.laf;

import geogebra.common.main.SavedStateListener;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.WorksheetHeader;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;

public interface LookAndFeel extends SavedStateListener {

    public void buildHeader(TabletGUI gui, TouchModel touchModel);

    public int getAppBarHeight();

    public int getToolBarHeight();

    public DefaultResources getIcons();

    public int getPaddingLeftOfDialog();

    public int getPanelsHeight();

    public TabletHeaderPanel getTabletHeaderPanel();

    public boolean isMouseDownIgnored();

    public void setTitle(String title);

    public boolean isShareSupported();

    public void updateUndoSaveButtons();
    
    public StandardImageButton setStyleBarButtonHandler(StandardImageButton button, StyleBar styleBar, String process);

    public StandardImageButton setOptionalButtonHandler(StandardImageButton button, StyleBar styleBar, OptionType captionstyle);

    public StandardImageButton setStyleBarShowHideHandler(StandardImageButton button, StyleBar styleBar);

    public StandardImageButton setAlgebraButtonHandler(StandardImageButton arrow, TabletGUI gui);

	public WorksheetHeader buildWorksheetHeader(WorksheetGUI worksheetGUI, TabletGUI tabletGUI);
}
