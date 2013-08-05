package geogebra.touch.gui.laf;

import geogebra.common.main.SavedStateListener;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.gui.elements.stylingbar.StylingBar;
import geogebra.touch.gui.euclidian.EuclidianViewPanel;
import geogebra.touch.model.TouchModel;
import geogebra.touch.utils.OptionType;

import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventHandler;

public interface LookAndFeel extends SavedStateListener {

	public void buildHeader(TabletGUI gui, TouchModel touchModel);

	public int getAppBarHeight();

	public DefaultResources getIcons();

	public EventHandler getOptionalButtonHandler(StylingBar stylingBar, StandardImageButton standardImageButton, OptionType captionstyle);

	public int getPaddingLeftOfDialog();

	public int getPanelsHeight();

	public Type<EventHandler> getStylBarEventType();

	public EventHandler getStyleBarButtonHandler(StylingBar stylingBar, StandardImageButton newButton, String process);

	public TabletHeaderPanel getTabletHeaderPanel();

	public int getToolBarHeight();

	public boolean isMouseDownIgnored();

	public void setApp(TouchApp app);

	public void setTitle(String title);

	public boolean supportsShare();

	public void updateUndoSaveButtons();

	public EventHandler getStyleBarHandlerShowHide(StylingBar stylingBar, EuclidianViewPanel euclidianViewPanel);

	public MouseDownHandler getAlgebraButtonClickHandler(TabletGUI gui);

	public TouchStartHandler getAlgebraButtonTouchStartHandler(final TabletGUI gui);
}
