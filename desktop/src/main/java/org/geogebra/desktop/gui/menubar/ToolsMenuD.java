package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.geogebra.desktop.gui.dialog.ToolCreationDialogD;
import org.geogebra.desktop.gui.dialog.ToolManagerDialogD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * The "Tools" menu.
 */
class ToolsMenuD extends BaseMenu {
	private static final long serialVersionUID = -2012951866084095682L;

	private AbstractAction toolbarConfigAction, showCreateToolsAction,
			showManageToolsAction;

	/**
	 * Creates tools menu
	 * 
	 * @param app
	 *            application
	 */
	public ToolsMenuD(AppD app) {
		super(app, "Tools");

		// items are added to the menu when it's opened, see BaseMenu:
		// addMenuListener(this);
	}

	/**
	 * Initialize the menu items.
	 */
	private void updateItems() {
		if (!initialized) {
			// menus not created yet, so nothing to do
			return;
		}

		removeAll();

		add(toolbarConfigAction);
		addSeparator();
		add(showCreateToolsAction);
		add(showManageToolsAction);

		// support for right-to-left languages
		app.setComponentOrientation(this);

	}

	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions() {
		toolbarConfigAction = new AbstractAction(
				loc.getMenu("Toolbar.Customize") + " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.getDialogManager().showToolbarConfigDialog();
			}
		};

		showCreateToolsAction = new AbstractAction(
				loc.getMenu("Tool.CreateNew") + " ...",
				app.getMenuIcon(GuiResourcesD.TOOL)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				ToolCreationDialogD tcd = new ToolCreationDialogD(app);
				tcd.setVisible(true);
			}
		};

		showManageToolsAction = new AbstractAction(
				loc.getMenu("Tool.Manage") + " ...",
				app.getMenuIcon(GuiResourcesD.DOCUMENT_PROPERTIES)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				ToolManagerDialogD tmd = new ToolManagerDialogD(app);
				tmd.setVisible(true);
			}
		};
	}

	@Override
	public void update() {
		updateItems();
	}

	@Override
	protected void initItems() {
		//
	}
}
