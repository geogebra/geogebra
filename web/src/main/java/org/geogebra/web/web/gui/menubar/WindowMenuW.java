package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.images.AppResources;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * The "Window" menu.
 */
public class WindowMenuW extends MenuBar{

	private App app;

	/**
	 * Constructs the "Window" menu
	 * 
	 * @param application
	 *            app
	 */
	public WindowMenuW(App application) {

		super(true);
		this.app = application;
		addStyleName("GeoGebraMenuBar");
		initActions();
	}
	
	private void initActions(){
		clearItems();
		
		addItem(MainMenu.getMenuBarHtml(AppResources.INSTANCE
				.empty().getSafeUri().asString(),
				app.getLocalization().getMenu("NewWindow"), true),
		        true, new Command() {
			        @Override
					public void execute() {
			        	((AppW) app).getFileManager().open(Window.Location.getHref(), "_blank", "");
			        }
		        });
		
	}
	
}
