package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * The "Edit" menu.
 */
public class EditMenuD extends BaseMenu {
	private static final long serialVersionUID = -2649808771324470803L;
	SelectionManager selection;
	private AbstractAction deleteAction, invertAction, showhideAction,
			showhideLabelsAction, propertiesAction, selectAllAction,
			selectAllAncestorsAction, selectAllDescendantsAction,
			selectCurrentLayerAction, copyToClipboardAction, copyAction,
			pasteAction, insertImageFromClipboardAction,
			insertImageFromFileAction;

	private JMenuItem deleteItem, invertItem, showhideItem, showhideLabelsItem,
			selectAllItem, selectAllAncestorsItem, selectAllDescendantsItem,
			selectCurrentLayerItem, copyToClipboardItem, copyItem, pasteItem,
			clipboardMenu;

	private JSeparator selectionSeparator, deleteSeparator;

	public EditMenuD(AppD app) {
		super(app, "Edit");
		selection = app.getSelectionManager();
		// items are added to the menu when it's opened, see BaseMenu:
		// addMenuListener(this);

	}

	/**
	 * Initialize the items.
	 */
	@Override
	protected void initItems() {
		removeAll();

		JMenuItem mi;

		if (app.isUndoActive()) {
			mi = add(((GuiManagerD) app.getGuiManager()).getUndoAction());
			mi.setIcon(app.getMenuIcon(GuiResourcesD.MENU_EDIT_UNDO));
			setMenuShortCutAccelerator(mi, 'Z');
			mi = add(((GuiManagerD) app.getGuiManager()).getRedoAction());
			mi.setIcon(app.getMenuIcon(GuiResourcesD.MENU_EDIT_REDO));
			if (AppD.MAC_OS) {
				// Command-Shift-Z
				setMenuShortCutShiftAccelerator(mi, 'Z');
			} else {
				// Ctrl-Y
				setMenuShortCutAccelerator(mi, 'Y');
			}
			addSeparator();
		}

		copyItem = add(copyAction);
		setMenuShortCutAccelerator(copyItem, 'C');

		pasteItem = add(pasteAction);
		setMenuShortCutAccelerator(pasteItem, 'V');

		copyToClipboardItem = add(copyToClipboardAction);
		// ctrl-shift-c is also handled in MyKeyListener
		setMenuShortCutShiftAccelerator(copyToClipboardItem, 'C');

		addSeparator();

		// insert image from...
		JMenu submenu = new JMenu(loc.getMenu("InsertImageFrom"));
		submenu.addMenuListener(this);
		submenu.setIcon(app.getEmptyIcon());
		add(submenu);

		submenu.add(insertImageFromFileAction);
		clipboardMenu = submenu.add(insertImageFromClipboardAction);

		addSeparator();

		if (app.letShowPropertiesDialog()) {
			mi = add(propertiesAction);
			setMenuShortCutAccelerator(mi, 'E');
			addSeparator();
		}

		selectAllItem = add(selectAllAction);
		setMenuShortCutAccelerator(selectAllItem, 'A');

		selectCurrentLayerItem = add(selectCurrentLayerAction);
		setMenuShortCutAccelerator(selectCurrentLayerItem, 'L');

		selectAllDescendantsItem = add(selectAllDescendantsAction);
		setMenuShortCutShiftAccelerator(selectAllDescendantsItem, 'J');

		selectAllAncestorsItem = add(selectAllAncestorsAction);
		setMenuShortCutAccelerator(selectAllAncestorsItem, 'J');

		selectionSeparator = new JSeparator();
		add(selectionSeparator);

		invertItem = add(invertAction);
		setMenuShortCutAccelerator(invertItem, 'I');

		showhideItem = add(showhideAction);
		setMenuShortCutAccelerator(showhideItem, 'G');

		showhideLabelsItem = add(showhideLabelsAction);
		setMenuShortCutShiftAccelerator(showhideLabelsItem, 'G');

		if (app.letDelete()) {
			deleteSeparator = new JSeparator();
			add(deleteSeparator);

			deleteItem = add(deleteAction);

			if (AppD.MAC_OS) {
				deleteItem.setAccelerator(
						KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));
			} else {
				deleteItem.setAccelerator(
						KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
			}
		}

		// support for right-to-left languages
		app.setComponentOrientation(this);

	}

	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions() {
		propertiesAction = new AbstractAction(
				loc.getMenu("Properties") + " ...",
				app.getMenuIcon(GuiResourcesD.VIEW_PROPERTIES_16)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS,
						null);
			}
		};

		selectAllAction = new AbstractAction(loc.getMenu("SelectAll"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				selection.selectAll(-1); // pass "-1" to select all
			}
		};

		selectCurrentLayerAction = new AbstractAction(
				loc.getMenu("SelectCurrentLayer"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				int layer = selection.getSelectedLayer();
				if (layer != -1)
				 {
					selection.selectAll(layer); // select all objects in layer
				}

			}
		};

		selectAllAncestorsAction = new AbstractAction(
				loc.getMenu("SelectAncestors"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				selection.selectAllPredecessors();
			}
		};

		selectAllDescendantsAction = new AbstractAction(
				loc.getMenu("SelectDescendants"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				selection.selectAllDescendants();
			}
		};

		showhideAction = new AbstractAction(loc.getMenu("ShowHide"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				selection.showHideSelection();

			}
		};

		showhideLabelsAction = new AbstractAction(loc.getMenu("ShowHideLabels"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				selection.showHideSelectionLabels();
			}
		};

		copyAction = new AbstractAction(loc.getMenu("Copy"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.getCopyPaste().copyToXML(app, selection.getSelectedGeos(),
						false);
				app.updateMenubar();
				app.setDefaultCursor();
			}
		};

		pasteAction = new AbstractAction(loc.getMenu("Paste"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.getCopyPaste().pasteFromXML(app, false);
				app.setDefaultCursor();
			}
		};

		copyToClipboardAction = new AbstractAction(
				loc.getMenu("DrawingPadToClipboard"),
				app.getMenuIcon(GuiResourcesD.MENU_EDIT_COPY)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.copyGraphicsViewToClipboard();
				app.setDefaultCursor();
			}
		};

		insertImageFromClipboardAction = new AbstractAction(
				loc.getMenu("Clipboard"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				((GuiManagerD) app.getGuiManager()).loadImage(null, true);
				app.setDefaultCursor();
			}
		};

		insertImageFromFileAction = new AbstractAction(loc.getMenu("File"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				((GuiManagerD) app.getGuiManager()).loadImage(null, false);
				app.setDefaultCursor();
			}
		};

		deleteAction = new AbstractAction(loc.getMenu("Delete"),
				app.getMenuIcon(GuiResourcesD.DELETE_SMALL)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.deleteSelectedObjects(false);
			}
		};

		invertAction = new AbstractAction(loc.getMenu("InvertSelection"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				selection.invertSelection();
			}
		};
	}

	@Override
	public void update() {
		updateSelection();
	}

	/**
	 * Called if the user changes the selected items.
	 */
	public void updateSelection() {

		if (!initialized) {
			return;
		}

		int layer = selection.getSelectedLayer();

		/*
		 * layer values: -1 means nothing selected -2 means different layers
		 * selected
		 */

		boolean justCreated = !(app.getActiveEuclidianView()
				.getEuclidianController().getJustCreatedGeos().isEmpty());

		copyAction.setEnabled(!selection.getSelectedGeos().isEmpty());
		pasteAction.setEnabled(!app.getCopyPaste().isEmpty());

		deleteAction.setEnabled(layer != -1 || justCreated);
		deleteItem.setVisible(layer != -1 || justCreated);
		deleteSeparator.setVisible(layer != -1 || justCreated);

		showhideAction.setEnabled(layer != -1);
		showhideItem.setVisible(layer != -1);

		showhideLabelsAction.setEnabled(layer != -1);
		showhideLabelsItem.setVisible(layer != -1);

		// exactly one layer selected
		selectCurrentLayerAction.setEnabled(selection.getSelectedLayer() >= 0);
		selectCurrentLayerItem.setVisible(selection.getSelectedLayer() >= 0);

		boolean haveSelection = !selection.getSelectedGeos().isEmpty();
		invertAction.setEnabled(haveSelection);
		invertItem.setVisible(haveSelection);
		selectAllDescendantsAction.setEnabled(haveSelection);
		selectAllDescendantsItem.setVisible(haveSelection);
		selectAllAncestorsAction.setEnabled(haveSelection);
		selectAllAncestorsItem.setVisible(haveSelection);
		selectionSeparator.setVisible(haveSelection);

		Kernel kernel = app.getKernel();
		propertiesAction.setEnabled(!kernel.isEmpty());
		selectAllAction.setEnabled(!kernel.isEmpty());
	}

	@Override
	public void menuSelected(MenuEvent e) {

		// build menu if necessary
		super.menuSelected(e);

		if (!e.getSource().equals(this)) { // ie submenu opened

			// check if there's an image on the clipboard
			String[] fileName = ((GuiManagerD) app.getGuiManager())
					.getImageFromTransferable(null);
			clipboardMenu.setEnabled(fileName != null && fileName.length > 0);
		}

	}

}
