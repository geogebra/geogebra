package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.gui.menubar.GeoGebraMenubarW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class GGWMenuBar extends Composite {

	private static GGWMenuBarUiBinder uiBinder = GWT.create(GGWMenuBarUiBinder.class);

	interface GGWMenuBarUiBinder extends UiBinder<HorizontalPanel, GGWMenuBar> {
	}
	
	@UiField
	static HorizontalPanel ggbmenubarwrapper;
	private static GeoGebraMenubarW menubar;

	public GGWMenuBar() {
		initWidget(uiBinder.createAndBindUi(this));
		
		
	}
	
	public static void init(App app) {
		menubar = new GeoGebraMenubarW(app);
		ggbmenubarwrapper.add(menubar);
	}

	public static GeoGebraMenubarW getMenubar(){
		return menubar;
	}
	

	public static void removeMenus(){
		ggbmenubarwrapper.clear();
	}
	
	
}
