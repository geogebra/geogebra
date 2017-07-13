package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.AppType;
import org.geogebra.common.gui.toolcategorization.ToolCategorization.ToolsetLevel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.web.html5.main.AppW;
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
	private ToolFilterSubMenu subToolFilter;
	/**
	 * tool panel
	 */
	ToolbarPanel toolbarPanel;
	private int x;

	private int y;

	/** The application */
	AppW app;


	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 * @param toolbarPanel
	 *            toolbar panel
	 */
	ContextMenuTools(AppW app, ToolbarPanel toolbarPanel) {
		this.app = app;
		this.toolbarPanel = toolbarPanel;
		loc = app.getLocalization();
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("matMenu");
		buildGUI();
		}

	private void buildGUI() {
		wrappedPopup.clearItems();
		// addToolItems();
		addToolFilterItem();
		addToolManageItems();
		setToolsetLevel(app.getSettings().getToolbarSettings().getToolsetLevel());
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

	/**
	 * @author csilla
	 *
	 */
	public class ToolFilterSubMenu extends CheckMarkSubMenu
			implements SettingListener {
		private ArrayList<ToolsetLevel> supportedLevels = null;

		/**
		 * @param parentMenu
		 *            - parent menu item
		 */
		public ToolFilterSubMenu(GCollapseMenuItem parentMenu) {
			super(wrappedPopup, parentMenu);
			app.getSettings().getToolbarSettings().addListener(this);
		}

		@Override
		protected void initActions() {
			if (supportedLevels == null) {
				supportedLevels = new ArrayList<ToolsetLevel>();
			}

			supportedLevels.clear();
			supportedLevels.add(ToolsetLevel.ADVANCED);
			supportedLevels.add(ToolsetLevel.STANDARD);
			if (app.getSettings().getToolbarSettings().getType() == AppType.GEOMETRY_CALC) {
				supportedLevels.add(ToolsetLevel.EMPTY_CONSTRUCTION);
			}
			for (int i = 0; i <supportedLevels.size() ; i++) {
				final ToolsetLevel level = supportedLevels.get(i);
				String levelTitle = app.getLocalization()
						.getMenu(level.getLevel());
				boolean isSelected = app.getSettings().getToolbarSettings().getToolsetLevel().equals(level);
				addItem(levelTitle, isSelected, new Command() {

					@Override
					public void execute() {
						setToolsetLevel(level);
						app.getSettings().getToolbarSettings()
								.setToolsetLevel(level);
						toolbarPanel.getTabTools().updateContent();
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

		@Override
		public void settingsChanged(AbstractSettings settings) {
			toolbarPanel.getTabTools().updateContent();
		}
	}

	private void addToolFilterItem() {
		String htmlString = MainMenu
				.getMenuBarHtml(
						MaterialDesignResources.INSTANCE.filter_list_black()
								.getSafeUri().asString(),
						loc.getMenu("Tool.Filter"));
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

	private void addItem(String text, ScheduledCommand cmd) {
		MenuItem mi = new MenuItem(text, true, cmd);
		wrappedPopup.addItem(mi);
	}
	private void addToolManageItems() {
		if (!app.isExam()) {
			addItem(MainMenu.getMenuBarHtml(
					MaterialDesignResources.INSTANCE.tools_customize_black()
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
				MaterialDesignResources.INSTANCE.tools_create_black().getSafeUri()
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
				MaterialDesignResources.INSTANCE.tools_black().getSafeUri().asString(),
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

	@Override
	public void setLabels() {
		buildGUI();
	}

	/**
	 * 
	 * @return Tool type selected.
	 */
	public ToolsetLevel getToolType() {
		return app.getSettings().getToolbarSettings().getToolsetLevel();
	}

	/**
	 * @return collapse/expand check mark submenu to filter tools
	 */
	public ToolFilterSubMenu getSubToolFilter() {
		return subToolFilter;
	}

	/**
	 * 
	 * @param toolsetLevel
	 *            to set.
	 */
	public void setToolsetLevel(ToolsetLevel toolsetLevel) {
		app.getSettings().getToolbarSettings().setToolsetLevel(toolsetLevel);
		subToolFilter.update();
	}
	
}

