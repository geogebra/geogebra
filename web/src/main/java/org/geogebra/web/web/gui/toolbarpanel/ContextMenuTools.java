package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.ToolsetLevel;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.dialog.ToolCreationDialogW;
import org.geogebra.web.web.gui.dialog.ToolManagerDialogW;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.CheckMarkSubMenu;
import org.geogebra.web.web.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.web.javax.swing.GCollapseMenuItem;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Algebra tab 3-dot menu.
 * 
 * @author laszlo
 *
 */
public class ContextMenuTools implements SetLabels {
	/** popup menu by clicking on 3dot button */
	public GPopupMenuW wrappedPopup;
	private Localization loc;
	private List<GCheckmarkMenuItem> checkmarkItems;
	private ToolFilterSubMenu subToolFilter;
	private int x;

	private int y;

	/** The application */
	AppW app;

	private ToolsetLevel toolsetLevel = ToolsetLevel.ADVANCED;
	private String checkmarkUrl = null;

	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 */
	ContextMenuTools(AppW app) {
		this.app = app;
		loc = app.getLocalization();
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("matMenu");
		checkmarkItems = new ArrayList<GCheckmarkMenuItem>();
		buildGUI();
		}

	private void buildGUI() {
		wrappedPopup.clearItems();
		// addToolItems();
		addToolFilterItem();
		addToolManageItems();
		setToolsetLevel(ToolsetLevel.EMPTY_CONSTRUCTION);
	}

	private Command cmdReposition = new Command() {
		@Override
		public void execute() {
			reposition();
		}
	};

	/** update position */
	void reposition() {
		if (x + wrappedPopup.getPopupPanel().getOffsetWidth() > app
				.getWidth()) {
			x = (int) (app.getWidth()
					- wrappedPopup.getPopupPanel().getOffsetWidth());
		}
		wrappedPopup.show(new GPoint(x, y));
	}

	private class ToolFilterSubMenu extends CheckMarkSubMenu {
		private ArrayList<ToolsetLevel> supportedLevels = null;

		public ToolFilterSubMenu(GCollapseMenuItem parentMenu) {
			super(wrappedPopup, parentMenu);
		}

		@Override
		protected void initActions() {
			if (supportedLevels == null) {
				supportedLevels = new ArrayList<ToolsetLevel>();
			}

			supportedLevels.clear();
			supportedLevels.add(ToolsetLevel.EMPTY_CONSTRUCTION);
			supportedLevels.add(ToolsetLevel.STANDARD);
			supportedLevels.add(ToolsetLevel.ADVANCED);
			for (int i = 0; i < supportedLevels.size(); i++) {
				final ToolsetLevel level = supportedLevels.get(i);
				String sortTitle = app.getLocalization()
						.getMenu(level.toString());
				addItem(sortTitle, false, new Command() {

					@Override
					public void execute() {
						//app.getSettings().getAlgebra().setTreeMode(sortMode);
						update();
					}
				});
			}
		}

		@Override
		public void update() {
			for (int i = 0; i < itemCount(); i++) {
				GCheckmarkMenuItem cm = itemAt(i);
				cm.setChecked(ToolsetLevel.values()[i] == getToolType());
			}
		}
	}

	private void addToolFilterItem() {
		String htmlString = MainMenu
				.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.filter_list_black()
								.getSafeUri().asString(),
						loc.getPlain("ToolFilter"));
		final GCollapseMenuItem ci = new GCollapseMenuItem(htmlString,
				MaterialDesignResources.INSTANCE.expand_black().getSafeUri()
						.asString(),
				MaterialDesignResources.INSTANCE.collapse_black().getSafeUri()
						.asString(),
				false, cmdReposition);
		wrappedPopup.addItem(ci.getMenuItem(), false);
		subToolFilter = new ToolFilterSubMenu(ci);
		subToolFilter.update();
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
	public void addCheckmarkItem(String text, boolean selected,
			Command command) {
		GCheckmarkMenuItem cm = new GCheckmarkMenuItem(text,
				checkmarkUrl,
				selected, command);
		wrappedPopup.addItem(cm.getMenuItem());
		checkmarkItems.add(cm);

	}

	private void addItem(String text, ScheduledCommand cmd) {
		MenuItem mi = new MenuItem(text, true, cmd);
		wrappedPopup.addItem(mi);
	}
	private void addToolManageItems() {
		if (!app.isExam()) {
			addItem(MainMenu.getMenuBarHtml(
					GuiResources.INSTANCE.menu_icon_tools_customize()
							.getSafeUri().asString(),
					loc.getMenu("Toolbar.Customize"), true),
					new Command() {

						@Override
						public void execute() {
							wrappedPopup.hide();
							app.showCustomizeToolbarGUI();
						}
					});
		}

		addItem(MainMenu.getMenuBarHtml(
				GuiResources.INSTANCE.menu_icon_tools_new().getSafeUri()
						.asString(),
				loc.getMenu(app.isToolLoadedFromStorage() ? "Tool.SaveAs"
						: "Tool.CreateNew"),
				true), new Command() {

					@Override
					public void execute() {
						ToolCreationDialogW toolCreationDialog = new ToolCreationDialogW(
								app);
						toolCreationDialog.center();
					}
				});

		addItem(MainMenu.getMenuBarHtml(
				GuiResources.INSTANCE.menu_icon_tools().getSafeUri().asString(),
				loc.getMenu("Tool.Manage"), true), new Command() {

					@Override
					public void execute() {
						ToolManagerDialogW toolManageDialog = new ToolManagerDialogW(
								app);
						toolManageDialog.center();
					}
				});

	}

	/**
	 * Show Tools Context menu
	 * 
	 * @param p
	 *            point to show the menu.
	 */
	public void show(GPoint p) {

		wrappedPopup.show(p);
	}

	/**
	 * Show Tools Context menu
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

	@Override
	public void setLabels() {
		buildGUI();
	}

	/**
	 * 
	 * @return Tool type selected.
	 */
	public ToolsetLevel getToolType() {
		return toolsetLevel;
	}

	/**
	 * 
	 * @param toolsetLevel
	 *            to set.
	 */
	public void setToolsetLevel(ToolsetLevel toolsetLevel) {
		this.toolsetLevel = toolsetLevel;
		subToolFilter.update();
	}
	
}

