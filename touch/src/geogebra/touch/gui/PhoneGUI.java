package geogebra.touch.gui;

import geogebra.common.main.Localization;
import geogebra.html5.gui.ResizeListener;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.algebra.AlgebraViewPanel;
import geogebra.touch.gui.elements.header.PhoneHeader;
import geogebra.touch.gui.elements.stylebar.StyleBar;
import geogebra.touch.gui.elements.toolbar.ToolBar;
import geogebra.touch.gui.views.BrowseViewPanelT;
import geogebra.touch.gui.views.ViewsContainer;
import geogebra.touch.gui.views.ViewsContainer.View;
import geogebra.touch.gui.views.options.EuclidianOptions;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Coordinates the GUI of phones.
 * 
 */
public class PhoneGUI extends TouchGUI {

	private ViewsContainer views;
	private PhoneHeader header;
	private BrowseViewPanelT browseViewPanel;
	private FlowPanel graphicsViewPanel;
	
	private EuclidianOptions euclidianOptions;
	// TODO add additional option-views

	public PhoneGUI() {
		super();
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(final ResizeEvent event) {
				PhoneGUI.this.updateViewSizes();
			}
		});
		//add touch listener and stop propagation if touchstart is on the edges
	}

	@Override
	public void updateViewSizes() {
//		this.touchModel.getGuiModel().closeOptions();
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize();
		}
		scrollTo(this.views.getCurrentView());
	}
	
	@Override
	public void initGUIElements() {
		addHeader();
		addContent();
		scrollTo(View.Worksheets);
		this.header.setTabActive(View.Worksheets);
	}
	
	@Override
	public boolean isAlgebraShowing() {
		return false;
	}
	
	private void addHeader() {
		this.header = new PhoneHeader();
		this.add(this.header);
		this.addResizeListener(this.header);
	}
	
	private void addContent() {
		this.views = new ViewsContainer();
		
		final int width = Window.getClientWidth();
		final int height = TouchEntryPoint.getLookAndFeel().getCanvasHeight();
		
		// *** euclidian & algebra ***
		this.graphicsViewPanel = new FlowPanel();
		this.euclidianViewPanel.setStyleName("euclidianViewPanel");
		this.euclidianViewPanel.setPixelSize(width, height);
		this.euclidianViewPanel.initEuclidianView(this.touchController);
		this.addResizeListener(this.euclidianViewPanel);

		this.styleBar = new StyleBar(this.touchModel,
				this.euclidianViewPanel.getEuclidianView());
		
		
		this.toolBar = new ToolBar(this.touchModel, this.app);
		this.addResizeListener(this.toolBar);
		
		this.graphicsViewPanel.add(this.euclidianViewPanel);
//		this.graphicsViewPanel.add(this.toolBar);
		
		this.touchModel.getGuiModel().setStyleBar(this.styleBar);
		this.euclidianViewPanel.add(this.styleBar);
		this.euclidianViewPanel.setWidgetPosition(this.styleBar, this.rtl ? width - STYLEBAR_WIDTH :0, 0);
		
		this.algebraViewPanel = new AlgebraViewPanel(this.touchController, this.kernel);
		this.algebraViewPanel.setPixelSize(width, height);
		this.algebraViewPanel.setStyleName("algebraViewPanel");
		
		// *** browseView ***
		this.browseViewPanel = new BrowseViewPanelT(this.app);
		this.browseViewPanel.setPixelSize(width, height);
		
		// *** optionsView ***
		this.euclidianOptions = new EuclidianOptions();
		this.euclidianOptions.setPixelSize(width, height);
		
		
		this.views.addView(this.algebraViewPanel);		
		this.views.addView(this.graphicsViewPanel);
		this.views.addView(this.browseViewPanel);
		this.views.addView(this.euclidianOptions);
		
		this.add(this.views);	
		
		this.addResizeListener(this.views);
		this.addResizeListener(this.algebraViewPanel);
	}
	
	/**
	 * scrolls to the given {@link View view}
	 * @param view
	 */
	public void scrollTo(View view) {
		this.views.scrollTo(view);
		changeTitle();
		this.header.setTabActive(view);
	}
	
	private void changeTitle() {
		String newTitle = "";
		Localization loc = this.app.getLocalization();
		switch(this.views.getCurrentView()) {
		case Algebra:
			newTitle = loc.getPlain("AlgebraWindow");
			break;
		case Graphics:
			newTitle = this.app.getKernel().getConstruction().getTitle();
			break;
		case Worksheets:
			newTitle = loc.getMenu("Worksheets");
			break;
		case Options:
			newTitle = loc.getMenu("Options");
			break;
		default:
			newTitle = "GeoGebra";
		}
		this.header.changeTitle(newTitle);
	}
	
	public BrowseViewPanelT getBrowseViewPanel() {
		return this.browseViewPanel;
	}
	
	@Override
	public void setLabels() {
		this.algebraViewPanel.setLabels();
		this.toolBar.setLabels();
		changeTitle();
	}
}
