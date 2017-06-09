package org.geogebra.web.web.gui.toolbarpanel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Algebra tab 3-dot menu.
 * 
 * @author laszlo
 *
 */
public class ContextMenuAlgebra implements SetLabels {
	protected GPopupMenuW wrappedPopup;
	protected Localization loc;
	AppW app;

	private abstract class SubMenu extends MenuBar {
		public SubMenu() {
			super(true);
			addStyleName("GeoGebraMenuBar");
			addStyleName("floating-Popup");
			initActions();
		}

		protected abstract void initActions();
	}

	private class DescriptionSubMenu extends SubMenu {
		public DescriptionSubMenu() {

		}

		@Override
		protected void initActions() {
			String avModes[] = AlgebraSettings.getDescriptionModes(app);

			for (int i = 0; i < avModes.length; i++) {
				final int avMode = AlgebraSettings.getStyleModeAt(i);
				addItem(avModes[i], new Command() {
					public void execute() {
						app.getKernel().setAlgebraStyle(avMode);

						if (app.getGuiManager().hasPropertiesView()) {
							app.getGuiManager().getPropertiesView()
									.repaintView();
						}
						app.getKernel().updateConstruction();

					}
				});
			}
		}

	}

	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 */
	ContextMenuAlgebra(AppW app) {
		this.app = app;
		loc = app.getLocalization();
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("matMenu");
		buildGUI();
		}

	private void buildGUI() {
		wrappedPopup.clearItems();
		addDescriptionItem();
		addPropertiesItem();
	}
	
	public void show(GPoint p) {
		wrappedPopup.show(p);
	}

	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
	}

	private void addDescriptionItem() {
		
		MenuItem mi = new MenuItem(loc.getMenu("Description"),
				new DescriptionSubMenu());

		wrappedPopup.addItem(mi);
	}
	private void addPropertiesItem() {
		String img = MaterialDesignResources.INSTANCE.settings_black()
				.getSafeUri()
				.asString();
		MenuItem mi = new MenuItem(MainMenu.getMenuBarHtml(img,
				loc.getMenu("Settings"), true), true,
				new Command() {
					
					@Override
					public void execute() {
						app.getDialogManager().showPropertiesDialog(OptionType.ALGEBRA, null);
					}
				});

		wrappedPopup.addItem(mi);
	}

	@Override
	public void setLabels() {
		buildGUI();
	}
	
}

