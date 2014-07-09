package geogebra.phone.gui.header;

import geogebra.html5.css.GuiResources;
import geogebra.html5.gui.FastButton;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.phone.Phone;
import geogebra.phone.gui.views.ViewsContainer.View;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class PhoneHeader extends FlowPanel implements ResizeListener {

	private SimplePanel title;

	
	SimplePanel algebra;
	SimplePanel graphics;
	SimplePanel worksheets;
	SimplePanel options;
	private Label titleLabel;
	
	public PhoneHeader() {
		this.setStyleName("PhoneHeader");
		//FIXME do this with LAF
		this.setPixelSize(Window.getClientWidth(), 43);
		
		addTitleTab();
		addViewTabs();
	}

	private void addTitleTab() {
		this.title = new SimplePanel();
		this.titleLabel = new Label();
		this.title.add(this.titleLabel);
		this.title.setStyleName("headerTitlePanel");
		this.add(this.title);
	}
	
	private void addViewTabs() {
		FastButton algebraViewButton = new StandardButton(GuiResources.INSTANCE.algebraView());
		algebraViewButton.addStyleName("phoneHeaderButton");
		algebraViewButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				Phone.getGUI().scrollTo(View.Algebra);
				setTabActive(PhoneHeader.this.algebra);
			}
		});

		FastButton graphicsViewButton = new StandardButton(GuiResources.INSTANCE.graphicsView());
		graphicsViewButton.addStyleName("phoneHeaderButton");
		graphicsViewButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				Phone.getGUI().scrollTo(View.Graphics);
				setTabActive(PhoneHeader.this.graphics);
			}
		});
		
		FastButton worksheetsViewButton = new StandardButton(GuiResources.INSTANCE.browseView());
		worksheetsViewButton.addStyleName("phoneHeaderButton");
		worksheetsViewButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				Phone.getGUI().scrollTo(View.Worksheets);
				Phone.getGUI().getBrowseViewPanel().loadFeatured();
				setTabActive(PhoneHeader.this.worksheets);
			}
		});
		
		
		FastButton optionsButton = new StandardButton(GuiResources.INSTANCE.options());
		optionsButton.addStyleName("phoneHeaderButton");
		optionsButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				Phone.getGUI().scrollTo(View.Options);
				setTabActive(PhoneHeader.this.options);
				// context-sensitive options
			}
		});
		
		
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
		this.titleLabel.setText(newTitle);
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
}
