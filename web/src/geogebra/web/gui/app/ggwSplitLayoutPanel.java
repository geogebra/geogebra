package geogebra.web.gui.app;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ggwSplitLayoutPanel extends Composite {

	private static ggwSplitLayoutPanelUiBinder uiBinder = GWT
	        .create(ggwSplitLayoutPanelUiBinder.class);

	interface ggwSplitLayoutPanelUiBinder extends
	        UiBinder<SplitLayoutPanel, ggwSplitLayoutPanel> {
	}
	        
	@UiField GGWViewWrapper ggwViewWrapper;
	@UiField GGWGraphicsView ggwGraphicsView;
	@UiField SplitLayoutPanel ggwSplitLayoutPanel;
	        
	SplitLayoutPanel outer;

	public ggwSplitLayoutPanel() {
		initWidget(outer = uiBinder.createAndBindUi(this));
	}
	
	public GGWViewWrapper getGGWViewWrapper() {
		return ggwViewWrapper;
	}
	
	public GGWGraphicsView getGGWGraphicsView() {
		return ggwGraphicsView;
	}

	public SplitLayoutPanel getSplitLayoutPanel() {
	    return outer;
    }
}
