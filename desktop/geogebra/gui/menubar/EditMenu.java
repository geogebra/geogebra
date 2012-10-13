package geogebra.gui.menubar;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.OptionType;
import geogebra.common.util.CopyPaste;
import geogebra.gui.GuiManagerD;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;

/**
 * The "Edit" menu.
 */
public class EditMenu extends BaseMenu {
	private static final long serialVersionUID = -2649808771324470803L;

	private AbstractAction
		deleteAction,
		invertAction,
		showhideAction,
		showhideLabelsAction,
		propertiesAction,
		selectAllAction,
		selectAllAncestorsAction,
		selectAllDescendantsAction,
		selectCurrentLayerAction,
		copyToClipboardAction,
		copyAction,
		pasteAction,
		insertImageFromClipboardAction,
		insertImageFromFileAction
	;
	
	private JMenuItem
		deleteItem,
		invertItem,
		showhideItem,
		showhideLabelsItem,
		selectAllItem,
		selectAllAncestorsItem,
		selectAllDescendantsItem,
		selectCurrentLayerItem,
		copyToClipboardItem,
		copyItem,
		pasteItem,
		clipboardMenu
	;
	
	private JSeparator
		selectionSeparator,
		deleteSeparator
	;
	
	public EditMenu(AppD app) {
		super(app, app.getMenu("Edit"));

		// items are added to the menu when it's opened, see BaseMenu: addMenuListener(this);

	}
	
	/**
	 * Initialize the items.
	 */
	@Override
	protected void initItems()
	{
		removeAll();
		
		JMenuItem mi;
		
		if (app.isUndoActive()) {
			mi = add(((GuiManagerD) app.getGuiManager()).getUndoAction());
			setMenuShortCutAccelerator(mi, 'Z');
			mi = add(((GuiManagerD) app.getGuiManager()).getRedoAction());
			if (AppD.MAC_OS)
				// Command-Shift-Z
				setMenuShortCutShiftAccelerator(mi, 'Z');
			else
				// Ctrl-Y
				setMenuShortCutAccelerator(mi, 'Y');
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
		JMenu submenu = new JMenu(app.getMenu("InsertImageFrom"));
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
				deleteItem.setAccelerator(KeyStroke.getKeyStroke(
						KeyEvent.VK_BACK_SPACE, 0));
			} else {
				deleteItem
						.setAccelerator(KeyStroke.getKeyStroke(
								KeyEvent.VK_DELETE, 0));
			}
		}
		
		// support for right-to-left languages
		app.setComponentOrientation(this);

	}
	
	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions()
	{
		propertiesAction = new AbstractAction(app.getPlain("Properties")
				+ " ...", app.getImageIcon("view-properties16.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, null);
			}
		};

		selectAllAction = new AbstractAction(app.getMenu("SelectAll"), app
				.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.selectAll(-1); // Michael Borcherds 2008-03-03 pass "-1" to
									// select all
			}
		};

		selectCurrentLayerAction = new AbstractAction(app
				.getMenu("SelectCurrentLayer"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				int layer = app.getSelectedLayer();
				if (layer != -1)
					app.selectAll(layer); // select all objects in layer

			}
		};

		selectAllAncestorsAction = new AbstractAction(app
				.getMenu("SelectAncestors"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				app.selectAllPredecessors();
			}
		};

		selectAllDescendantsAction = new AbstractAction(app
				.getMenu("SelectDescendants"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				app.selectAllDescendants();
			}
		};

		showhideAction = new AbstractAction(app
				.getMenu("ShowHide"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				app.showHideSelection();

			}
		};

		showhideLabelsAction = new AbstractAction(app
				.getMenu("ShowHideLabels"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				app.showHideSelectionLabels();
			}
		};

		copyAction = new AbstractAction(
				app.getMenu("Copy"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.setWaitCursor();
				CopyPaste.copyToXML(app, app.getSelectedGeos());
				app.updateMenubar();
				app.setDefaultCursor();
			}
		};
		
		pasteAction = new AbstractAction(
				app.getMenu("Paste"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				CopyPaste.pasteFromXML(app);
				app.setDefaultCursor();
			}
		};
		
		copyToClipboardAction = new AbstractAction(
				app.getMenu("DrawingPadToClipboard"),
				app.getImageIcon("edit-copy.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.setWaitCursor();
				app.copyGraphicsViewToClipboard();	
				app.setDefaultCursor();
			}
		};
		
		insertImageFromClipboardAction = new AbstractAction(
				app.getMenu("Clipboard"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.setWaitCursor();
				((GuiManagerD)app.getGuiManager()).loadImage(null, true);
				app.setDefaultCursor();
			}
		};
		
		insertImageFromFileAction = new AbstractAction(
				app.getMenu("File"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.setWaitCursor();
				((GuiManagerD)app.getGuiManager()).loadImage(null, false);
				app.setDefaultCursor();
			}
		};
		
		deleteAction = new AbstractAction(app.getPlain("Delete"), app
				.getImageIcon("delete_small.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.deleteSelectedObjects();
			}
		};
	
	
	invertAction = new AbstractAction(app.getMenu("InvertSelection"), app.getEmptyIcon()) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			app.invertSelection();
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
		
		int layer = app.getSelectedLayer();
		
		/* layer values:
		 *  -1 means nothing selected
		 *  -2 means different layers selected
		 */
		
		boolean justCreated = !(app.getActiveEuclidianView().getEuclidianController().getJustCreatedGeos().isEmpty());

		copyAction.setEnabled(!app.getSelectedGeos().isEmpty());
		pasteAction.setEnabled(!CopyPaste.isEmpty());

		deleteAction.setEnabled(layer != -1 || justCreated);
		deleteItem.setVisible(layer != -1 || justCreated);
		deleteSeparator.setVisible(layer != -1 || justCreated);
		
		showhideAction.setEnabled(layer != -1);
		showhideItem.setVisible(layer != -1);
		
		showhideLabelsAction.setEnabled(layer != -1);
		showhideLabelsItem.setVisible(layer != -1);
		
		// exactly one layer selected
		selectCurrentLayerAction.setEnabled(app.getSelectedLayer() >= 0);
		selectCurrentLayerItem.setVisible(app.getSelectedLayer() >= 0);
		
		boolean haveSelection = !app.getSelectedGeos().isEmpty();
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
			String[] fileName = ((GuiManagerD) app.getGuiManager()).getImageFromTransferable(null);
			clipboardMenu.setEnabled(fileName.length > 0);
		}
		
	}

}
