package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.geogebra.desktop.gui.dialog.ToolCreationDialog;
import org.geogebra.desktop.gui.dialog.ToolManagerDialog;
import org.geogebra.desktop.main.AppD;

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
		super(app, app.getMenu("Tools"));

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
				app.getMenu("Toolbar.Customize") + " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getDialogManager().showToolbarConfigDialog();
			}
		};

		showCreateToolsAction = new AbstractAction(
				app.getMenu("Tool.CreateNew") + " ...",
				app.getMenuIcon("tool.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ToolCreationDialog tcd = new ToolCreationDialog(app);
				tcd.setVisible(true);
			}
		};

		showManageToolsAction = new AbstractAction(app.getMenu("Tool.Manage")
				+ " ...", app.getMenuIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ToolManagerDialog tmd = new ToolManagerDialog(app);
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
