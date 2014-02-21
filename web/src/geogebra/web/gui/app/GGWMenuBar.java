package geogebra.web.gui.app;

import geogebra.web.gui.menubar.GeoGebraMenuW;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GGWMenuBar extends Composite {

	private static GGWMenuBarUiBinder uiBinder = GWT.create(GGWMenuBarUiBinder.class);

	interface GGWMenuBarUiBinder extends UiBinder<HorizontalPanel, GGWMenuBar> {
	}
	
	@UiField
	HorizontalPanel ggbmenubarwrapper;
	private GeoGebraMenuW menubar;
	private boolean menubar2Showing = false;
	private FocusPanel clickBox;

	// added shift-click to toggle new menubar for testing
	// TODO: remove temporary code
	
	public GGWMenuBar() {
		initWidget(uiBinder.createAndBindUi(this));
		
	}
	
	public void init(AppW app) {
		menubar = app.getLAF().getMenuBar(app);
		ggbmenubarwrapper.add((Widget) menubar);
	}
	
	public GeoGebraMenuW getMenubar() {
		return menubar;
	}

	public void removeMenus(){
		ggbmenubarwrapper.clear();
	}
	
}
