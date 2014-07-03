package geogebra.touch.gui.elements.header;

import geogebra.html5.gui.FastButton;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.PhoneGUI;
import geogebra.touch.gui.views.ViewsContainer.View;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class PhoneHeader extends HorizontalPanel implements ResizeListener {

	private SimplePanel title;
	SimplePanel algebra;
	SimplePanel graphics;
	SimplePanel worksheets;
	SimplePanel options;
	private Label titleLabel;
	
	public PhoneHeader() {
		this.setStyleName("PhoneHeader");
		this.setSize(Window.getClientWidth() + "px", TouchEntryPoint.getLookAndFeel().getHeaderHeight() + "px");
		
		addTitleTab();
		addViewTabs();
	}

	private void addTitleTab() {
		this.title = new SimplePanel();
		this.titleLabel = new Label();
		this.title.add(this.titleLabel);
		this.title.setStyleName("headerTitlePanel");
		this.title.setWidth(Window.getClientWidth()/3 + "px");
		this.add(this.title);
	}
	
	private void addViewTabs() {
		FastButton algebraViewButton = new StandardButton(TouchEntryPoint.getLookAndFeel()
				.getIcons().algebraView());
		algebraViewButton.addStyleName("phoneHeaderButton");
		algebraViewButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				((PhoneGUI) TouchEntryPoint.getLookAndFeel().getGUI()).scrollTo(View.Algebra);
				setTabActive(PhoneHeader.this.algebra);
			}
		});

		FastButton graphicsViewButton = new StandardButton(TouchEntryPoint.getLookAndFeel()
				.getIcons().graphicsView());
		graphicsViewButton.addStyleName("phoneHeaderButton");
		graphicsViewButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				((PhoneGUI) TouchEntryPoint.getLookAndFeel().getGUI()).scrollTo(View.Graphics);
				setTabActive(PhoneHeader.this.graphics);
			}
		});
		
		FastButton worksheetsViewButton = new StandardButton(TouchEntryPoint.getLookAndFeel()
				.getIcons().browseView());
		worksheetsViewButton.addStyleName("phoneHeaderButton");
		worksheetsViewButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				((PhoneGUI) TouchEntryPoint.getLookAndFeel().getGUI()).scrollTo(View.Worksheets);
				((PhoneGUI) TouchEntryPoint.getLookAndFeel().getGUI()).getBrowseViewPanel().loadFeatured();
				setTabActive(PhoneHeader.this.worksheets);
			}
		});
		
		
		FastButton optionsButton = new StandardButton(TouchEntryPoint.getLookAndFeel()
				.getIcons().options());
		optionsButton.addStyleName("phoneHeaderButton");
		optionsButton.addFastClickHandler(new FastClickHandler() {
			
			@Override
			public void onClick() {
				((PhoneGUI) TouchEntryPoint.getLookAndFeel().getGUI()).scrollTo(View.Options);
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
		
		this.add(this.algebra);
		this.add(this.graphics);
		this.add(this.worksheets);
		this.add(this.options);
	}

	@Override
	public void onResize() {
		this.setSize(Window.getClientWidth()+"px", "35px");
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
