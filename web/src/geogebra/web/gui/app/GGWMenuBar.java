package geogebra.web.gui.app;

import geogebra.common.main.App;
import geogebra.web.gui.menubar.GeoGebraMenubarW;
import geogebra.web.gui.menubar.GeoGebraMenubarW2;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class GGWMenuBar extends Composite {

	private static GGWMenuBarUiBinder uiBinder = GWT.create(GGWMenuBarUiBinder.class);

	interface GGWMenuBarUiBinder extends UiBinder<HorizontalPanel, GGWMenuBar> {
	}
	
	@UiField
	HorizontalPanel ggbmenubarwrapper;
	private GeoGebraMenubarW menubar;
	private GeoGebraMenubarW2 menubar2;
	private boolean menubar2Showing = false;
	private FocusPanel clickBox;

	// added shift-click to toggle new menubar for testing
	// TODO: remove temporary code
	
	public GGWMenuBar() {
		initWidget(uiBinder.createAndBindUi(this));
		
	}
	
	public void init(AppW app) {
		menubar = new GeoGebraMenubarW(app);
		menubar2 = new GeoGebraMenubarW2(app);
		
		clickBox = new FocusPanel();
		clickBox.addMouseDownHandler(new MouseDownHandler(){

			@Override
            public void onMouseDown(MouseDownEvent event) {
	            if(event.isShiftKeyDown()){
	            	toggleMenuBar();
	            }
	            
            }});
		
		
		clickBox.add(menubar);
		ggbmenubarwrapper.add(clickBox);
	}

	private void toggleMenuBar(){
		clickBox.clear();
		if(menubar2Showing) {
			clickBox.add(menubar);
		}else{
			clickBox.add(menubar2);
		}
		menubar2Showing = !menubar2Showing;
	}
	
	
	public GeoGebraMenubarW getMenubar(){
		if(menubar2Showing){
			return menubar2;
		}
		return menubar;
	}
	

	public void removeMenus(){
		ggbmenubarwrapper.clear();
	}
	
	
}
