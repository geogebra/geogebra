package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menu.action.DefaultMenuActionHandler;
import org.geogebra.web.full.gui.view.algebra.MenuAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;

/**
 * Clears construction and initializes a new one
 */
public class FileNewAction extends MenuAction<Void> {
	private DefaultMenuActionHandler actionHandler;

	/**
	 * @param app application
	 */
	public FileNewAction(AppW app) {
		super(app.getVendorSettings().getMenuLocalizationKey("New"),
				((AppWFull) app).getActivity().getResourceIconProvider().newFileMenu());
		this.actionHandler = new DefaultMenuActionHandler((AppWFull) app);
	}

	@Override
	public void execute(Void geo, AppWFull appW) {
		actionHandler.clearConstruction();
	}
}
