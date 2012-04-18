package geogebra.web.gui.app;

import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MySplitLayoutPanel extends SplitLayoutPanel {
	
	private GGWGraphicsView ggwGraphicView;
	private GGWViewWrapper ggwViewWrapper;
	private AbstractApplication application;

	public MySplitLayoutPanel(){
		super();
		addWest(ggwViewWrapper = new GGWViewWrapper(), GeoGebraAppFrame.GGWVIewWrapper_WIDTH);
		add(ggwGraphicView = new GGWGraphicsView());
    }
	
	@Override
    public void onResize() {
		super.onResize();
		Element wrapper = getWidgetContainerElement(ggwGraphicView);
		((Application) application).ggwGraphicsViewWidthChanged(wrapper.getOffsetWidth());
		
	}

	public SplitLayoutPanel getSplitLayoutPanel() {
	    return this;
    }

	public GGWGraphicsView getGGWGraphicsView() {
	    // TODO Auto-generated method stub
	    return ggwGraphicView;
    }

	public void attachApp(AbstractApplication app) {
	   this.application = app;
	   ggwViewWrapper.attachApp(app);
    }

}
