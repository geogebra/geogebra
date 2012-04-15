package geogebra.web.gui.app;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

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
