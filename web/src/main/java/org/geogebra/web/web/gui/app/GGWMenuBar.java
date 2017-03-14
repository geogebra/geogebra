package org.geogebra.web.web.gui.app;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.menubar.MainMenu;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Wrap MainMenu in composite
 *
 */
public class GGWMenuBar extends Composite {
	
	private HorizontalPanel ggbmenubarwrapper;
	private MainMenu menubar;

	/**
	 * Create new menu wrapper
	 */
	public GGWMenuBar() {
		ggbmenubarwrapper = new HorizontalPanel();
		ggbmenubarwrapper.addStyleName("ggbmenubarwrapper");
		initWidget(ggbmenubarwrapper);
		
	}
	
	/**
	 * @param app
	 *            application to init menus
	 */
	public void init(AppW app) {
		menubar = (MainMenu) app.getLAF().getMenuBar(app);

		ggbmenubarwrapper.add(menubar);
	}
	
	/**
	 * @return wrapped menu
	 */
	public MainMenu getMenubar() {
		return menubar;
	}

	/**
	 * Clear the menu
	 */
	public void removeMenus(){
		ggbmenubarwrapper.clear();
	}

	@Override
	public void setPixelSize(int w, int h) {
		super.setPixelSize(w, h);
		if (menubar != null && menubar.getElement().getParentElement() != null) {
			menubar.getElement().getParentElement().getStyle()
				.setHeight(h, Unit.PX);
		}
	}

	/**
	 * Focus the menu
	 */
	public void focus() {
		menubar.focus();

	}

	/**
	 * @param height
	 *            menu height
	 */
	public void updateHeight(int height) {
		menubar.updateHeight(height);
	}
}
