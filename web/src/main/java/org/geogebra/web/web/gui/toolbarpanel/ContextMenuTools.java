package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.dialog.ToolCreationDialogW;
import org.geogebra.web.web.gui.dialog.ToolManagerDialogW;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.javax.swing.GCheckmarkMenuItem;
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
	private GPopupMenuW wrappedPopup;
	private Localization loc;
	private List<GCheckmarkMenuItem> checkmarkItems;

	/** The application */
	AppW app;

	private enum ToolType {
		BASIC, STANDARD, ALL
	}

	private ToolType toolType = ToolType.STANDARD;
	private String checkmarkUrl;

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
		addToolItems();
		addToolManageItems();
		setToolType(ToolType.STANDARD);
	}

	private void addToolItems() {
		checkmarkUrl = MaterialDesignResources.INSTANCE.check_black()
				.getSafeUri().asString();

		addCheckmarkItem(loc.getPlain("Basic.Tools"), false, new Command() {

			@Override
			public void execute() {
				setToolType(ToolType.BASIC);
			}
		});

		addCheckmarkItem(loc.getPlain("Standard.Tools"), false, new Command() {

			@Override
			public void execute() {
				setToolType(ToolType.STANDARD);
			}
		});

		addCheckmarkItem(loc.getPlain("All.Tools"), false, new Command() {
			@Override
			public void execute() {
				setToolType(ToolType.ALL);
			}
		});

	}

	private void updateToolItems() {
		for (int i = 0; i < checkmarkItems.size(); i++) {
			GCheckmarkMenuItem cm = checkmarkItems.get(i);
			cm.setChecked(ToolType.values()[i] == getToolType());
		}
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

		wrappedPopup.showAtPoint(p);
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
		wrappedPopup.showAtPoint(new GPoint(x, y));
	}

	@Override
	public void setLabels() {
		buildGUI();
	}

	/**
	 * 
	 * @return Tool type selected.
	 */
	public ToolType getToolType() {
		return toolType;
	}

	/**
	 * 
	 * @param toolType
	 *            to set.
	 */
	public void setToolType(ToolType toolType) {
		this.toolType = toolType;
		updateToolItems();
	}
	
}

