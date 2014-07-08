package geogebra.phone.gui;

import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.browser.BrowseViewPanel;
import geogebra.phone.gui.header.PhoneHeader;
import geogebra.phone.gui.views.ViewsContainer;
import geogebra.phone.gui.views.ViewsContainer.View;
import geogebra.web.main.EuclidianViewPanel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GUI for the phone app
 * 
 * @author geogebra
 *
 */
public class PhoneGUI extends VerticalPanel {

	private final List<ResizeListener> resizeListeners = new ArrayList<ResizeListener>();
	
	private ViewsContainer views;
	private PhoneHeader header;
	private BrowseViewPanel browseViewPanel; //hier war browseViewPanelT
	private FlowPanel graphicsViewPanel;
	private EuclidianViewPanel euclidianViewPanel;
	//FIXME - only needed for setLabels()

	
//	private EuclidianOptions euclidianOptions;
	// TODO add additional option-views

	public PhoneGUI() {
		this.setStyleName("PhoneGUI");
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		initGUIElements();
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				PhoneGUI.this.updateViewSizes();
			}
		});
		//add touch listener and stop propagation if touchstart is on the edges
	}

	void updateViewSizes() {
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize();
		}
		scrollTo(this.views.getCurrentView());
	}
	
	private void initGUIElements() {
		addHeader();
		addContent();
		scrollTo(View.Worksheets);
		this.header.setTabActive(View.Worksheets);
	}
	
	private void addHeader() {
		this.header = new PhoneHeader();
		this.add(this.header);
		this.addResizeListener(this.header);
	}
	
	private void addContent() {
		this.views = new ViewsContainer();
//		
		final int width = Window.getClientWidth();
		//FIXME do this with LAF
		final int height = Window.getClientHeight() - 43;
		
		// *** graphics ***
		this.graphicsViewPanel = new FlowPanel();
//		this.euclidianViewPanel.setStyleName("euclidianViewPanel");
//		this.euclidianViewPanel.setPixelSize(width, height);
//		this.euclidianViewPanel.initEuclidianView(this.touchController);
//		this.addResizeListener(this.euclidianViewPanel);
//
//		//FIXME - can we use styleBar from WEB?
//		this.styleBar = new StyleBar(this.touchModel,
//				this.euclidianViewPanel.getEuclidianView());
//		
//		
//		this.graphicsViewPanel.add(this.euclidianViewPanel);
//		
////		this.touchModel.getGuiModel().setStyleBar(this.styleBar);
//		this.euclidianViewPanel.add(this.styleBar);
//		this.euclidianViewPanel.setWidgetPosition(this.styleBar, this.rtl ? width - STYLEBAR_WIDTH :0, 0);
//		
//		this.algebraViewPanel = new AlgebraViewPanel(this.touchController, this.kernel);
//		this.algebraViewPanel.setPixelSize(width, height);
//		this.algebraViewPanel.setStyleName("algebraViewPanel");
//		
		// *** browseView ***
//		this.browseViewPanel = new BrowseViewPanel(this.app);
//		this.browseViewPanel.setPixelSize(width, height);
//		
//		// *** optionsView ***
////		this.euclidianOptions = new EuclidianOptions();
////		this.euclidianOptions.setPixelSize(width, height);
////		
//		
//		this.views.addView(this.algebraViewPanel);		
//		this.views.addView(this.graphicsViewPanel);
//		this.views.addView(this.browseViewPanel);
////		this.views.addView(this.euclidianOptions);
//		
		this.add(this.views);	
		
		this.addResizeListener(this.views);
//		this.addResizeListener(this.algebraViewPanel);
	}
	
	/**
	 * scrolls to the given {@link View view}
	 * @param view
	 */
	private void scrollTo(View view) {
//		this.views.scrollTo(view);
//		changeTitle();
//		this.header.setTabActive(view);
	}
	
	private void changeTitle() {
		String newTitle = "";
//		Localization loc = this.app.getLocalization();
		switch(this.views.getCurrentView()) {
		case Algebra:
			newTitle = "Algebra";
//			newTitle = loc.getPlain("AlgebraWindow");
			break;
		case Graphics:
			newTitle = "Graphics";
//			newTitle = this.app.getKernel().getConstruction().getTitle();
			break;
		case Worksheets:
			newTitle = "Worksheets";
//			newTitle = loc.getMenu("Worksheets");
			break;
		case Options:
			newTitle = "Options";
//			newTitle = loc.getMenu("Options");
			break;
		default:
			newTitle = "GeoGebra";
		}
		this.header.changeTitle(newTitle);
	}
	
//	private BrowseViewPanel getBrowseViewPanel() {
//		return this.browseViewPanel;
//	}
//	
	private void addResizeListener(ResizeListener rl) {
		this.resizeListeners.add(rl);
	}
	
	private void setLabels() {
//		this.algebraViewPanel.setLabels();
//		this.toolBar.setLabels();
//		changeTitle();
	}
}
