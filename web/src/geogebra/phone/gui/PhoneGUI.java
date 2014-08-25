package geogebra.phone.gui;

import geogebra.html5.gui.ResizeListener;
import geogebra.phone.gui.header.PhoneHeader;
import geogebra.phone.gui.views.AlgebraViewPanel;
import geogebra.phone.gui.views.EuclidianViewPanel;
import geogebra.phone.gui.views.ViewsContainer;
import geogebra.phone.gui.views.ViewsContainer.View;
import geogebra.phone.gui.views.browseView.MaterialListPanelP;
import geogebra.web.main.AppWapplication;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
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
	private MaterialListPanelP materialListPanel;
	private EuclidianViewPanel euclidianViewPanel;
	private AlgebraViewPanel algebraViewPanel;
	private AppWapplication app;

	
//	private EuclidianOptions euclidianOptions;
	// TODO add additional option-views

	public PhoneGUI(AppWapplication app) {
		this.app = app;
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
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
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
		this.header = new PhoneHeader(app);
		this.add(this.header);
		this.addResizeListener(this.header);
	}
	
	private void addContent() {
		this.views = new ViewsContainer();
//		
		final int width = Window.getClientWidth();
		//FIXME do this with LAF
		final int height = Window.getClientHeight() - 43;
		
		// *** euclidian ***
		this.euclidianViewPanel = new EuclidianViewPanel(app);
		this.euclidianViewPanel.setStyleName("euclidianViewPanel");
		this.euclidianViewPanel.setPixelSize(width, height);
//		this.euclidianViewPanel.initEuclidianView(this.touchController);
		this.addResizeListener(this.euclidianViewPanel);
//
//		//FIXME - can we use styleBar from WEB?
//		this.styleBar = new StyleBar(this.touchModel,
//				this.euclidianViewPanel.getEuclidianView());
////		this.touchModel.getGuiModel().setStyleBar(this.styleBar);
//		this.euclidianViewPanel.add(this.styleBar);
//		this.euclidianViewPanel.setWidgetPosition(this.styleBar, this.rtl ? width - STYLEBAR_WIDTH :0, 0);
//		
		this.algebraViewPanel = new AlgebraViewPanel(app);
		this.algebraViewPanel.setPixelSize(width, height);
		this.algebraViewPanel.setStyleName("algebraViewPanel");
//		
		// *** browseView ***
		this.materialListPanel = new MaterialListPanelP(app);
		this.materialListPanel.setPixelSize(width, height);
//		
//		// *** optionsView ***
////		this.euclidianOptions = new EuclidianOptions();
////		this.euclidianOptions.setPixelSize(width, height);
////		
//		
		this.views.addView(this.algebraViewPanel);		
		this.views.addView(this.euclidianViewPanel);
		this.views.addView(this.materialListPanel);
////		this.views.addView(this.euclidianOptions);
//		
		this.add(this.views);	
		
		this.addResizeListener(this.views);
		this.addResizeListener(this.algebraViewPanel);
		this.addResizeListener(this.materialListPanel);
		this.addResizeListener(this.euclidianViewPanel);
		
		this.materialListPanel.loadFeatured();
	}
	
	/**
	 * scrolls to the given {@link View view} and updates the header
	 * @param view
	 */
	public void scrollTo(View view) {
		this.views.scrollTo(view);
		changeTitle();
		this.header.setTabActive(view);
	}
	
	public void showLastView() {
		scrollTo(this.views.getLastView());
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
	
	public MaterialListPanelP getMaterialListPanel() {
		return this.materialListPanel;
	}
	
	private void addResizeListener(ResizeListener rl) {
		this.resizeListeners.add(rl);
	}
	
	private void setLabels() {
//		this.algebraViewPanel.setLabels();
//		this.toolBar.setLabels();
//		changeTitle();
	}
}
