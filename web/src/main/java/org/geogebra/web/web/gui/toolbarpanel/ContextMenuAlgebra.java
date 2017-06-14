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
import org.geogebra.web.web.gui.images.StyleBarResources;
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

	private int x;

	private int y;

	private Command cmdReposition = new Command() {
		public void execute() {
			reposition();
		}
	};
	private abstract class SubMenu {
		private List<GCheckmarkMenuItem> items;
		private String checkmarkUrl;
		protected GCollapseMenuItem parentMenu;

		public SubMenu(GCollapseMenuItem parentMenu) {
			this.parentMenu = parentMenu;
			// super(true, "", app);
			checkmarkUrl = MaterialDesignResources.INSTANCE.check_black()
					.getSafeUri().asString();
			// addStyleName("GeoGebraMenuBar");
			// addStyleName("floating-Popup");
			// addStyleName("dotSubMenu");
			items = new ArrayList<GCheckmarkMenuItem>();
			initActions();
			parentMenu.collapse();
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
			parentMenu.addItem(cm.getMenuItem());
		}

		public int itemCount() {
			return items.size();
		}

		public GCheckmarkMenuItem itemAt(int idx) {
			return items.get(idx);
		}

		public abstract void update();
		protected abstract void initActions();
	}

	private class DescriptionSubMenu extends SubMenu {
		public DescriptionSubMenu(GCollapseMenuItem parentMenu) {
			super(parentMenu);
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

		public SortSubMenu(GCollapseMenuItem parentMenu) {
			super(parentMenu);
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
		this.x = x;
		this.y = y;
		wrappedPopup.show(new GPoint(x, y));
	}

	private void reposition() {
		if (x + wrappedPopup.getPopupPanel().getOffsetWidth() > app
				.getWidth()) {
			x = (int) (app.getWidth()
					- wrappedPopup.getPopupPanel().getOffsetWidth());
		}
		wrappedPopup.show(new GPoint(x, y));
	}

	private void addDescriptionItem() {
		String htmlString = MainMenu
				.getMenuBarHtml(
						StyleBarResources.INSTANCE.description().getSafeUri()
								.asString(),
						loc.getPlain("AlgebraDescriptions"));
		final GCollapseMenuItem ci = new GCollapseMenuItem(htmlString,
				MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString(),
				MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString(),
				false, cmdReposition);
		wrappedPopup.addItem(ci.getMenuItem(), false);
		subDescription = new DescriptionSubMenu(ci);
		subDescription.update();

	}

	private void addSortItem() {
		String htmlString = MainMenu
				.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.sort_black()
								.getSafeUri().asString(),
						loc.getPlain("SortBy"));
		final GCollapseMenuItem ci = new GCollapseMenuItem(htmlString,
				MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString(),
				MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString(),
				false, cmdReposition);
		wrappedPopup.addItem(ci.getMenuItem(), false);
		subSort = new SortSubMenu(ci);
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

