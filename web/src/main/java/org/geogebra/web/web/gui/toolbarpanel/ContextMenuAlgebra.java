package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.web.javax.swing.GCollapseMenuItem;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Algebra tab 3-dot menu.
 * 
 * @author laszlo
 *
 */
public class ContextMenuAlgebra implements SetLabels {
	private GPopupMenuW wrappedPopup;

	/** Localization */
	Localization loc;

	/** Application */
	AppW app;
	private DescriptionSubMenu subDescription;
	private SortSubMenu subSort;

	private abstract class SubMenu {
		private List<GCheckmarkMenuItem> items;
		private String checkmarkUrl;
		private boolean expanded = true;
		public SubMenu() {
			// super(true, "", app);
			checkmarkUrl = MaterialDesignResources.INSTANCE.check_black()
					.getSafeUri().asString();
			// addStyleName("GeoGebraMenuBar");
			// addStyleName("floating-Popup");
			// addStyleName("dotSubMenu");
			items = new ArrayList<GCheckmarkMenuItem>();
			initActions();
		}

		/**
		 * Adds a menu item with checkmark
		 * 
		 * @param text
		 *            of the item
		 * @param selected
		 *            if checkmark should be shown or not
		 * @param command
		 *            to execute when selected.
		 */
		public void addItem(String text, boolean selected, Command command) {
			GCheckmarkMenuItem cm = new GCheckmarkMenuItem(text,
					checkmarkUrl,
					selected, command);
			wrappedPopup.addItem(cm.getMenuItem());
			items.add(cm);

		}

		public int itemCount() {
			return items.size();
		}

		public GCheckmarkMenuItem itemAt(int idx) {
			return items.get(idx);
		}

		/**
		 * Collapse submenu
		 */
		public void collapse() {
			expanded = false;
			for (GCheckmarkMenuItem cm : items) {
				cm.getMenuItem().removeStyleName("expanded");
				cm.getMenuItem().addStyleName("collapsed");

			}
		}

		/**
		 * Expand submenu
		 */
		public void expand() {
			expanded = true;
			for (GCheckmarkMenuItem cm : items) {
				cm.getMenuItem().addStyleName("expanded");
				cm.getMenuItem().removeStyleName("collapsed");

			}
		}

		/**
		 * Toggle submenu
		 */
		public void toggle() {
			if (expanded) {
				collapse();
			} else {
				expand();
			}
		}
		public abstract void update();
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
				addItem(avModes[i],
						false,
						new Command() {
					@Override
					public void execute() {
						app.getKernel().setAlgebraStyle(avMode);

						if (app.getGuiManager().hasPropertiesView()) {
							app.getGuiManager().getPropertiesView()
											.repaintView();
						}
						app.getKernel().updateConstruction();
								update();
					}
						});
			}
			collapse();
		}

		@Override
		public void update() {
			int selectedMode = app.getKernel().getAlgebraStyle();
			for (int i = 0; i < itemCount(); i++) {
				GCheckmarkMenuItem item = itemAt(i);
				item.setChecked(
						selectedMode == AlgebraSettings.getStyleModeAt(i));
			}
		}

	}

	private class SortSubMenu extends SubMenu {
		private ArrayList<SortMode> supportedModes = null;

		public SortSubMenu() {
			super();
		}

		@Override
		protected void initActions() {
			if (supportedModes == null) {
				supportedModes = new ArrayList<SortMode>();
			}

			supportedModes.clear();
			supportedModes.add(SortMode.DEPENDENCY);
			supportedModes.add(SortMode.TYPE);
			supportedModes.add(SortMode.ORDER);
			supportedModes.add(SortMode.LAYER);
			for (int i = 0; i < supportedModes.size(); i++) {
				final SortMode sortMode = supportedModes.get(i);
				String sortTitle = loc.getMenu(sortMode.toString());
				addItem(sortTitle, false, new Command() {

					@Override
					public void execute() {
						app.getSettings().getAlgebra().setTreeMode(sortMode);
						update();
					}
				});
			}
			collapse();
		}

		private SortMode sortModeAt(int idx) {
			switch (idx) {
			case 0:
				return SortMode.DEPENDENCY;
			case 1:
				return SortMode.TYPE;
			case 2:
				return SortMode.ORDER;
			case 3:
				return SortMode.LAYER;
			default:
				return SortMode.ORDER;
			}
		}

		@Override
		public void update() {
			SortMode selectedMode = app.getSettings().getAlgebra()
					.getTreeMode();
			for (int i = 0; i < itemCount(); i++) {
				GCheckmarkMenuItem item = itemAt(i);
				item.setChecked(selectedMode == sortModeAt(i));
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
		addSortItem();
		addPropertiesItem();
	}
	
	/**
	 * Shows Algebra Context menu
	 * 
	 * @param p
	 *            point to show the menu.
	 */
	public void show(GPoint p) {

		wrappedPopup.show(p);
	}

	/**
	 * Shows Algebra Context menu
	 * 
	 * @param x
	 *            x coordinate to show the menu.
	 * @param y
	 *            y coordinate to show the menu.
	 */
	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
	}

	private void addDescriptionItem() {
		final GCollapseMenuItem ci = new GCollapseMenuItem(
				loc.getPlain("AlgebraDescriptions"),
				MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString(),
				MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString(),
				false, new Command() {

					public void execute() {
						subDescription.toggle();
					}
				});
		wrappedPopup.addItem(ci.getMenuItem(), false);

		subDescription = new DescriptionSubMenu();
		subDescription.update();
	}

	private void addSortItem() {
		final GCollapseMenuItem ci = new GCollapseMenuItem(
				loc.getPlain("SortBy"),
				MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString(),
				MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString(),
				false, new Command() {
					public void execute() {
						subSort.toggle();
			}
		});
		wrappedPopup.addItem(ci.getMenuItem(), false);
		subSort = new SortSubMenu();
		subSort.update();
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

