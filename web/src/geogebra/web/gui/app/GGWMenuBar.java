package geogebra.web.gui.app;

import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.menubar.GeoGebraMenubar;
import geogebra.web.main.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GGWMenuBar extends Composite {

	private static GGWMenuBarUiBinder uiBinder = GWT.create(GGWMenuBarUiBinder.class);

	interface GGWMenuBarUiBinder extends UiBinder<HorizontalPanel, GGWMenuBar> {
	}
	
	@UiField
	static HorizontalPanel ggbmenubarwrapper;
	private static GeoGebraMenubar menubar;

	public GGWMenuBar() {
		initWidget(uiBinder.createAndBindUi(this));
		
		
	}
	
	public void init(AbstractApplication app) {
		menubar = new GeoGebraMenubar(app);
		ggbmenubarwrapper.add(menubar);
	}

	public static GeoGebraMenubar getMenubar(){
		return menubar;
	}
	

}
