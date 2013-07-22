package geogebra.touch.gui.laf;

import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.stylingbar.StylingBar;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;

import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;

public interface LookAndFeel
{
	public void buildHeader(TabletGUI gui, TouchApp application, TouchModel touchModel, FileManagerM fm);

	public void setTitle(String title);

	public int getPanelsHeight();

	public int getAppBarHeight();

	public DefaultResources getIcons();

	public TabletHeaderPanel getTabletHeaderPanel();

	public boolean isMouseDownIgnored();

	public Type<EventHandler> getStylBarEventType();

	public EventHandler getStyleBarButtonHandler(StylingBar stylingBar, StandardImageButton newButton, String process);

	public EventHandler getOptionalButtonHandler(StylingBar stylingBar, StandardImageButton standardImageButton, OptionType captionstyle);
}
