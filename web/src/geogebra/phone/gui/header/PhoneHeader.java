package geogebra.phone.gui.header;

import geogebra.html5.gui.FastButton;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.phone.Phone;
import geogebra.phone.gui.views.ViewsContainer.View;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.util.StandardButton;
import geogebra.web.main.AppWapplication;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class PhoneHeader extends FlowPanel implements ResizeListener, FastClickHandler {

	private SimplePanel title;

	SimplePanel openToolBarPanel;
	SimplePanel algebra;
	SimplePanel graphics;
	SimplePanel worksheets;
	SimplePanel options;
	private Label titleLabel;
	
	private PopupPanel panel;
	private FastButton openToolBarButton;
	private FastButton algebraViewButton;
	private FastButton graphicsViewButton;
	private FastButton worksheetsViewButton;
	private FastButton optionsButton;

	public PhoneHeader(AppWapplication app) {
		this.setStyleName("PhoneHeader");
		// FIXME do this with LAF
		this.setPixelSize(Window.getClientWidth(), 43);

		addOpenToolbar(app);
		// addTitleTab();
		addViewTabs();
	}

	private void addOpenToolbar(AppWapplication app) {
		this.openToolBarPanel = new SimplePanel();
		panel = new PopupPanel();
		ScrollPanel content = new ScrollPanel();

		GGWToolBar ggwToolbar = new GGWToolBar();
		ggwToolbar.init(app);

		app.getAppFrame().ggwToolBar = ggwToolbar;

		final ToolBarP toolBar = new ToolBarP(ggwToolbar);
		toolBar.init(app);
		toolBar.buildGui();
		((GuiManagerW)app.getGuiManager()).setToolBarForUpdate(toolBar);

		content.add(toolBar);
		panel.add(content);
		panel.setAutoHideEnabled(true);

		// TODO: set icon of actual tool
		openToolBarButton = new StandardButton(GGWToolBar.getMyIconResourceBundle().mode_point_32(), null, 32);
		openToolBarButton.addStyleName("phoneHeaderButton");
		openToolBarButton.addFastClickHandler(this);
		openToolBarPanel.add(openToolBarButton);
		openToolBarPanel.addStyleName("tabLeft");
		openToolBarPanel.setVisible(false);

		this.add(openToolBarPanel);
	}

	private void addTitleTab() {
		this.title = new SimplePanel();
		this.titleLabel = new Label();
		this.title.add(this.titleLabel);
		this.title.setStyleName("headerTitlePanel");
		this.add(this.title);
	}

	private void addViewTabs() {
		algebraViewButton = new StandardButton(
		        GuiResources.INSTANCE.algebraView());
		algebraViewButton.addStyleName("phoneHeaderButton");
		algebraViewButton.addFastClickHandler(this);

		graphicsViewButton = new StandardButton(
		        GuiResources.INSTANCE.graphicsView());
		graphicsViewButton.addStyleName("phoneHeaderButton");
		graphicsViewButton.addFastClickHandler(this);

		worksheetsViewButton = new StandardButton(
		        GuiResources.INSTANCE.browseView());
		worksheetsViewButton.addStyleName("phoneHeaderButton");
		worksheetsViewButton.addFastClickHandler(this);

		optionsButton = new StandardButton(
		        GuiResources.INSTANCE.options());
		optionsButton.addStyleName("phoneHeaderButton");
		optionsButton.addFastClickHandler(this);

		this.algebra = new SimplePanel();
		this.algebra.setStyleName("tab");
		this.graphics = new SimplePanel();
		this.graphics.setStyleName("tab");
		this.worksheets = new SimplePanel();
		this.worksheets.setStyleName("tab");
		this.options = new SimplePanel();
		this.options.setStyleName("tab");
		this.options.addStyleName("lastTab");

		this.algebra.add(algebraViewButton);
		this.graphics.add(graphicsViewButton);
		this.worksheets.add(worksheetsViewButton);
		this.options.add(optionsButton);

		FlowPanel tabContainer = new FlowPanel();
		tabContainer.setStyleName("tabContainer");

		tabContainer.add(this.algebra);
		tabContainer.add(this.graphics);
		tabContainer.add(this.worksheets);
		tabContainer.add(this.options);

		this.add(tabContainer);
	}

	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(), 43);
	}

	public void changeTitle(String newTitle) {
		if (this.titleLabel != null) {
			this.titleLabel.setText(newTitle);
		}
	}

	void setTabActive(SimplePanel tab) {
		this.algebra.removeStyleName("activeTab");
		this.graphics.removeStyleName("activeTab");
		this.worksheets.removeStyleName("activeTab");
		this.options.removeStyleName("activeTab");
		tab.addStyleName("activeTab");
	}

	public void setTabActive(View view) {
		SimplePanel active;
		switch (view) {
		case Algebra:
			active = this.algebra;
			break;
		case Graphics:
			active = this.graphics;
			break;
		case Worksheets:
			active = this.worksheets;
			break;
		case Options:
			active = this.options;
			break;
		default:
			active = this.worksheets;
		}
		setTabActive(active);
	}

	public void onClick(Widget source) {
		if (source == openToolBarButton) {
			panel.show();

			// FIXME replace with dynamic value
			if(panel.getOffsetHeight() > Window.getClientHeight() - 43){
				panel.setHeight((Window.getClientHeight() - 43)+"px");
			}
		} else if (source == algebraViewButton) {
			openToolBarPanel.setVisible(false);
			Phone.getGUI().scrollTo(View.Algebra);
			setTabActive(PhoneHeader.this.algebra);
		} else if (source == graphicsViewButton) {
			openToolBarPanel.setVisible(true);
			Phone.getGUI().scrollTo(View.Graphics);
			setTabActive(PhoneHeader.this.graphics);
		} else if (source == worksheetsViewButton) {
			openToolBarPanel.setVisible(false);
			Phone.getGUI().scrollTo(View.Worksheets);
			setTabActive(PhoneHeader.this.worksheets);
		} else if (source == optionsButton) {
			openToolBarPanel.setVisible(false);
			Phone.getGUI().scrollTo(View.Options);
			setTabActive(PhoneHeader.this.options);
			// context-sensitive options
		}
	}
}
