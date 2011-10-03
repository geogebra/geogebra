package geogebra.gui.menubar;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.util.CopyPaste;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

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
		pasteAction
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
		pasteItem
	;
	
	private JSeparator
		selectionSeparator,
		deleteSeparator
	;
	
	public EditMenu(Application app) {
		super(app, app.getMenu("Edit"));

		initActions();
		initItems();
		
		update();
	}
	
	/**
	 * Initialize the items.
	 */
	private void initItems()
	{
		JMenuItem mi;
		
		if (app.isUndoActive()) {
			mi = add(app.getGuiManager().getUndoAction());
			setMenuShortCutAccelerator(mi, 'Z');
			mi = add(app.getGuiManager().getRedoAction());
			if (Application.MAC_OS)
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

			if (Application.MAC_OS) {
				deleteItem.setAccelerator(KeyStroke.getKeyStroke(
						KeyEvent.VK_BACK_SPACE, 0));
			} else {
				deleteItem
						.setAccelerator(KeyStroke.getKeyStroke(
								KeyEvent.VK_DELETE, 0));
			}
		}
	}
	
	/**
	 * Initialize the actions.
	 */
	private void initActions()
	{
		propertiesAction = new AbstractAction(app.getPlain("Properties")
				+ " ...", app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showPropertiesDialog();
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

				int layer = getSelectedLayer();
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

	/*
	 * Michael Borcherds 2008-03-03 return -1 if nothing selected return -2 if
	 * objects from more than one layer selected return layer number if objects
	 * from exactly one layer are selected
	 */
	private int getSelectedLayer() {
		Object[] geos = app.getSelectedGeos().toArray();
		if (geos.length == 0)
			return -1; // return -1 if nothing selected

		int layer = ((GeoElement) geos[0]).getLayer();

		for (int i = 1; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (geo.getLayer() != layer)
				return -2; // return -2 if more than one layer selected
		}
		return layer;
	}
	
	@Override
	public void update() {
		updateSelection();
		
		// TODO update labels
	}
	
	/**
	 * Called if the user changes the selected items.
	 */
	public void updateSelection() {
		int layer = getSelectedLayer();
		
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
		selectCurrentLayerAction.setEnabled(getSelectedLayer() >= 0);
		selectCurrentLayerItem.setVisible(getSelectedLayer() >= 0);
		
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
}
