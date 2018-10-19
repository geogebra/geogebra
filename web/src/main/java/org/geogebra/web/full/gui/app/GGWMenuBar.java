package org.geogebra.web.full.gui.app;

import org.geogebra.common.GeoGebraConstants.Versions;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.ToolbarSettings;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Wrap MainMenu in composite
 *
 */
public class GGWMenuBar extends Composite {
	
	private FlowPanel ggbmenubarwrapper;
	private MainMenu menubar;

	/**
	 * Create new menu wrapper
	 */
	public GGWMenuBar() {
		ggbmenubarwrapper = new FlowPanel();
		ggbmenubarwrapper.addStyleName("ggbmenubarwrapper");
		initWidget(ggbmenubarwrapper);
		
	}
	
	/**
	 * @param app
	 *            application to init menus
	 */
	public void init(AppW app) {
		ToolbarSettings set = app.getSettings().getToolbarSettings();
		Versions ver = app.getVersion();
		if (app.has(Feature.TOOLBAR_FROM_APPCONFIG)) {
			set.setFrom(app.getConfig(), ver.isPhone());
		} else {
			set.setFrom(ver);
		}
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
	public void removeMenus() {
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
		menubar.focusFirst();
	}

	/**
	 * Focus menu in the deferred way.
	 */
	public void focusDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				focus();
			}
		});
	}

	/**
	 * @param height
	 *            menu height
	 */
	public void updateHeight(int height) {
		menubar.updateHeight(height);
	}

	@Override
	public void setVisible(boolean b) {
		menubar.setVisible(b);
	}

}
