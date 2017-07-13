package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;

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
import org.geogebra.web.web.javax.swing.CheckMarkSubMenu;
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
	/**
	 * popup menu
	 */
	GPopupMenuW wrappedPopup;

	/** Localization */
	Localization loc;

	/** Application */
	AppW app;
	private DescriptionSubMenu subDescription;
	private SortSubMenu subSort;

	private int x;

	private int y;

	private Command cmdReposition = new Command() {
		@Override
		public void execute() {
			reposition();
		}
	};
	
	private class DescriptionSubMenu extends CheckMarkSubMenu {
		public DescriptionSubMenu(GCollapseMenuItem parentMenu) {
			super(wrappedPopup, parentMenu);
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

	private class SortSubMenu extends CheckMarkSubMenu {
		private ArrayList<SortMode> supportedModes = null;

		public SortSubMenu(GCollapseMenuItem parentMenu) {
			super(wrappedPopup, parentMenu);
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
	 * @param x1
	 *            x coordinate to show the menu.
	 * @param y1
	 *            y coordinate to show the menu.
	 */
	public void show(int x1, int y1) {
		this.x = x1;
		this.y = y1;
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
						loc.getMenu("AlgebraDescriptions"));
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
						loc.getMenu("SortBy"));
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

