/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.dialog;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.ConstructionDefaults;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.gui.GeoTreeCellRenderer;
import geogebra.gui.color.GeoGebraColorChooser;
import geogebra.gui.view.algebra.AlgebraTree;
import geogebra.gui.view.algebra.AlgebraViewD;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Markus Hohenwarter
 */
public class PropertiesDialog extends JDialog implements WindowListener,
		WindowFocusListener, TreeSelectionListener, KeyListener,
		GeoElementSelectionListener, SetLabels {

	// private static final int MAX_OBJECTS_IN_TREE = 500;
	private static final int MAX_GEOS_FOR_EXPAND_ALL = 15;
	private static final int MAX_COMBOBOX_ENTRIES = 200;

	private static final long serialVersionUID = 1L;
	private AppD app;
	private Kernel kernel;
	private JTreeGeoElements geoTree;
	private JButton closeButton, defaultsButton, delButton;
	private PropertiesPanel propPanel;
	private GeoGebraColorChooser colChooser;

	// stop slider increment being less than 0.00000001
	public final static int TEXT_FIELD_FRACTION_DIGITS = 8;
	public final static int SLIDER_MAX_WIDTH = 170;

	final private static int MIN_WIDTH = 500;
	final private static int MIN_HEIGHT = 300;

	/**
	 * Creates new PropertiesDialog.
	 * 
	 * @param app
	 *            parent frame
	 */
	public PropertiesDialog(AppD app) {
		super(app.getFrame(), false);
		this.app = app;
		kernel = app.getKernel();

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(true);

		addWindowListener(this);
		geoTree = new JTreeGeoElements();
		geoTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// some textfields are updated when they lose focus
				// give them a chance to do that before we change the selection
				requestFocusInWindow();
			}
		});
		geoTree.addTreeSelectionListener(this);
		geoTree.addKeyListener(this);

		// build GUI
		initGUI();
	}

	/**
	 * inits GUI with labels of current language
	 */
	public void initGUI() {
		geoTree.setFont(app.getPlainFont());

		boolean wasShowing = isShowing();
		if (wasShowing) {
			setVisible(false);
		}

		// LIST PANEL
		JScrollPane listScroller = new JScrollPane(geoTree);
		listScroller.setMinimumSize(new Dimension(120, 200));
		listScroller.setBackground(geoTree.getBackground());
		listScroller.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

		// delete button
		delButton = new JButton(app.getImageIcon("delete_small.gif"));
		delButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelectedGeos();
			}
		});

		// apply defaults button
		defaultsButton = new JButton();
		defaultsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyDefaults();
			}
		});

		closeButton = new JButton();
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});

		// build button panel with some buttons on the left
		// and some on the right
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(rightButtonPanel, app.borderEast());
		buttonPanel.add(leftButtonPanel, app.borderWest());

		// left buttons
		if (app.letDelete())
			leftButtonPanel.add(delButton);

		leftButtonPanel.add(defaultsButton);

		// right buttons
		rightButtonPanel.add(closeButton);

		// PROPERTIES PANEL
		if (colChooser == null) {
			// init color chooser
			colChooser = new GeoGebraColorChooser(app);
		}

		// check for null added otherwise you get two listeners for the
		// colChooser
		// when a file is loaded
		if (propPanel == null) {
			propPanel = new PropertiesPanel(app, colChooser, false);
			propPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		}
		selectionChanged(); // init propPanel

		// put it all together
		Container contentPane = getContentPane();
		contentPane.removeAll();
		// contentPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(listScroller);
		splitPane.setRightComponent(propPanel);

		contentPane.setLayout(new BorderLayout());
		contentPane.add(splitPane, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);

		if (wasShowing) {
			setVisible(true);
		}

		setLabels();
		
		app.setComponentOrientation(this);
	}

	public PropertiesPanel getPropertiesPanel() {
		return propPanel;
	}

	public void showSliderTab() {
		if (propPanel != null)
			propPanel.showSliderTab();
	}

	/**
	 * Update the labels of this dialog.
	 * 
	 * TODO Create "Apply Defaults" phrase (F.S.)
	 */
	public void setLabels() {
		setTitle(app.getPlain("Properties"));
		geoTree.root.setUserObject(app.getPlain("Objects"));

		delButton.setText(app.getPlain("Delete"));
		closeButton.setText(app.getMenu("Close"));
		defaultsButton.setText(app.getMenu("ApplyDefaults"));

		geoTree.setLabels();
		propPanel.setLabels();
	}

	/*
	 * public void cancel() {
	 * setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	 * kernel.detach(geoTree);
	 * 
	 * // remember current construction step int consStep =
	 * kernel.getConstructionStep();
	 * 
	 * // restore old construction state app.restoreCurrentUndoInfo();
	 * 
	 * // go to current construction step ConstructionProtocol cp =
	 * app.getConstructionProtocol(); if (cp != null) {
	 * cp.setConstructionStep(consStep); }
	 * 
	 * setCursor(Cursor.getDefaultCursor()); setVisible(false); }
	 * 
	 * public void apply() {
	 * setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	 * app.storeUndoInfo(); setCursor(Cursor.getDefaultCursor());
	 * setVisible(false); }
	 */

	public void cancel() {
		setVisible(false);
	}

	public void closeDialog() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		app.storeUndoInfo();
		setCursor(Cursor.getDefaultCursor());
		setVisible(false);
	}

	/**
	 * Reset the visual style of the selected elements.
	 * 
	 * TODO Does not work with lists (F.S.)
	 */
	private void applyDefaults() {
		GeoElement geo;
		ConstructionDefaults defaults = kernel.getConstruction()
				.getConstructionDefaults();

		for (int i = 0; i < selectionList.size(); ++i) {
			geo = (GeoElement) selectionList.get(i);
			defaults.setDefaultVisualStyles(geo, true);
			geo.updateRepaint();
		}

		propPanel.updateSelection(selectionList.toArray());
	}

	/**
	 * shows this dialog and select GeoElement geo at screen position location
	 */
	public void setVisibleWithGeos(ArrayList<GeoElement> geos) {
		kernel.clearJustCreatedGeosInViews();

		setViewActive(true);

		if (kernel.getConstruction().getGeoSetConstructionOrder().size() < MAX_GEOS_FOR_EXPAND_ALL)
			geoTree.expandAll();
		else
			geoTree.collapseAll();

		geoTree.setSelected(geos, false);
		if (!isShowing()) {
			// pack and center on first showing
			if (firstTime) {
				pack();
				setLocationRelativeTo(app.getMainComponent());
				firstTime = false;
			}

			// ensure min size
			Dimension dim = getSize();
			if (dim.width < MIN_WIDTH) {
				dim.width = MIN_WIDTH;
				setSize(dim);
			}
			if (dim.height < MIN_HEIGHT) {
				dim.height = MIN_HEIGHT;
				setSize(dim);
			}

			super.setVisible(true);
		}
	}

	private boolean firstTime = true;

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			setVisibleWithGeos(null);
		} else {
			super.setVisible(false);
			setViewActive(false);
		}
	}

	private void setViewActive(boolean flag) {
		if (flag == viewActive)
			return;
		viewActive = flag;

		if (flag) {
			geoTree.clear();
			kernel.attach(geoTree);

			// // only add objects if there are less than 200
			// int geoSize =
			// kernel.getConstruction().getGeoSetConstructionOrder().size();
			// if (geoSize < MAX_OBJECTS_IN_TREE)
			kernel.notifyAddAll(geoTree);

			app.setSelectionListenerMode(this);
			addWindowFocusListener(this);
		} else {
			kernel.detach(geoTree);

			removeWindowFocusListener(this);
			app.setSelectionListenerMode(null);
		}
	}

	private boolean viewActive = false;

	/**
	 * handles selection change
	 */
	private void selectionChanged() {
		updateSelectedGeos(geoTree.getSelectionPaths());

		GeoElement[] geos = (GeoElement[]) selectionList.toArray();
		propPanel.updateSelection(geos);
		// Util.addKeyListenerToAll(propPanel, this);

		// update selection of application too
		if (app.getMode() == EuclidianConstants.MODE_SELECTION_LISTENER)
			app.setSelectedGeos(selectionList);
	}

	private ArrayList<?> updateSelectedGeos(TreePath[] selPath) {
		selectionList.clear();

		if (selPath != null) {
			// add all selected paths
			for (int i = 0; i < selPath.length; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath[i]
						.getLastPathComponent();

				if (node == node.getRoot()) {
					// root: add all objects
					selectionList.clear();
					selectionList.addAll(kernel.getConstruction()
							.getGeoSetLabelOrder());
					i = selPath.length;
				} else if (node.getParent() == node.getRoot()) {
					// type node: select all children
					for (int k = 0; k < node.getChildCount(); k++) {
						DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
								.getChildAt(k);
						selectionList.add((GeoElement) child.getUserObject());
					}
				} else {
					// GeoElement
					selectionList.add((GeoElement) node.getUserObject());
				}
			}
		}

		return selectionList;
	}

	private ArrayList<GeoElement> selectionList = new ArrayList<GeoElement>();

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (geo == null)
			return;
		tempArrayList.clear();
		tempArrayList.add(geo);
		geoTree.setSelected(tempArrayList, addToSelection);
		// requestFocus();
	}

	private ArrayList<GeoElement> tempArrayList = new ArrayList<GeoElement>();

	/**
	 * deletes all selected GeoElements from Kernel
	 */
	private void deleteSelectedGeos() {
		ArrayList<GeoElement> selGeos = selectionList;

		if (selGeos.size() > 0) {
			GeoElement[] geos = (GeoElement[]) selGeos.toArray();
			for (int i = 0; i < geos.length - 1; i++) {
				geos[i].removeOrSetUndefinedIfHasFixedDescendent();
			}

			// select element above last to delete
			GeoElement geo = geos[geos.length - 1];
			TreePath tp = geoTree.getTreePath(geo);
			if (tp != null) {
				int row = geoTree.getRowForPath(tp);
				tp = geoTree.getPathForRow(row - 1);
				geo.removeOrSetUndefinedIfHasFixedDescendent();
				if (tp != null)
					geoTree.setSelectionPath(tp);
			}
		}
	}

	/**
	 * renames first selected GeoElement
	 * 
	 * private void rename() { ArrayList selGeos = selectionList; if
	 * (selGeos.size() > 0) { GeoElement geo = (GeoElement) selGeos.get(0);
	 * app.showRenameDialog(geo, false, null);
	 * 
	 * selectionList.clear(); selectionList.add(geo);
	 * geoTree.setSelected(selectionList, false); } }
	 */

	/**
	 * redefines first selected GeoElement
	 * 
	 * private void redefine() { ArrayList selGeos = selectionList;
	 * geoTree.clearSelection(); if (selGeos.size() > 0)
	 * app.showRedefineDialog((GeoElement) selGeos.get(0)); }
	 */

	/*
	 * Window Listener
	 */
	public void windowActivated(WindowEvent e) {
		/*
		 * if (!isModal()) { geoTree.setSelected(null, false);
		 * //selectionChanged(); } repaint();
		 */
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		// cancel();
		closeDialog();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	/**
	 * INNER CLASS JList for displaying GeoElements
	 * 
	 * @see GeoTreeCellRenderer
	 * @author Markus Hohenwarter
	 */
	public class JTreeGeoElements extends JTree implements View,
			MouseMotionListener, MouseListener, SetLabels {

		private static final long serialVersionUID = 1L;
		private DefaultTreeModel treeModel;
		private DefaultMutableTreeNode root;
		private HashMap<String, DefaultMutableTreeNode> typeNodesMap;

		/*
		 * has to be registered as view for GeoElement
		 */
		public JTreeGeoElements() {
			// build default tree structure
			root = new DefaultMutableTreeNode();

			// create model from root node
			treeModel = new DefaultTreeModel(root);
			setModel(treeModel);
			setLargeModel(true);
			typeNodesMap = new HashMap<String, DefaultMutableTreeNode>();

			getSelectionModel().setSelectionMode(
					TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			GeoTreeCellRenderer renderer = new GeoTreeCellRenderer(app);
			setCellRenderer(renderer);
			setRowHeight(-1); // to enable flexible height of cells

			// tree's options
			setRootVisible(true);
			// show lines from parent to children
			// putClientProperty("JTree.lineStyle", "None");
			setInvokesStopCellEditing(true);
			setScrollsOnExpand(true);

			addMouseMotionListener(this);
			addMouseListener(this);
		}

		public void setLabels() {
			root.setUserObject(app.getPlain("Objects"));

			// iterate through all type nodes and update the labels
			for (String key : typeNodesMap.keySet()) {
				typeNodesMap.get(key).setUserObject(app.getPlain(key));
			}
		}

		@Override
		protected void setExpandedState(TreePath path, boolean state) {
			// Ignore all collapse requests of root
			if (path != getPathForRow(0)) {
				super.setExpandedState(path, state);
			}
		}

		public void expandAll() {
			int row = 0;
			while (row < getRowCount()) {
				expandRow(row);
				row++;
			}
		}

		public void collapseAll() {
			int row = 1;
			while (row < getRowCount()) {
				collapseRow(row);
				row++;
			}
		}

		/**
		 * selects object geo in the list of GeoElements
		 * 
		 * @param addToSelection
		 *            false => clear old selection
		 */
		public void setSelected(ArrayList<GeoElement> geos,
				boolean addToSelection) {
			TreePath tp = null;

			TreeSelectionModel lsm = getSelectionModel();
			if (geos == null || geos.size() == 0) {
				lsm.clearSelection();
				selectFirstElement();
			} else {
				// make sure geos are in list, this is needed when
				// MAX_OBJECTS_IN_TREE was
				// exceeded in setViewActive(true)
				// for (int i=0; i < geos.size(); i++) {
				// GeoElement geo = (GeoElement) geos.get(i);
				// add(geo);
				// }

				if (!addToSelection)
					lsm.clearSelection();

				// get paths for all geos
				ArrayList<TreePath> paths = new ArrayList<TreePath>();
				for (int i = 0; i < geos.size(); i++) {
					TreePath result = getGeoPath(geos.get(i));
					if (result != null) {
						tp = result;
						expandPath(result);
						paths.add(result);
					}
				}

				// select geo paths
				TreePath[] selPaths = new TreePath[paths.size()];
				for (int i = 0; i < selPaths.length; i++) {
					selPaths[i] = paths.get(i);
				}
				lsm.addSelectionPaths(selPaths);

				if (tp != null && geos.size() == 1) {
					scrollPathToVisible(tp);
				}
			}
		}

		private void selectFirstElement() {
			// select all if list is not empty
			if (root.getChildCount() > 0) {
				DefaultMutableTreeNode typeNode = (DefaultMutableTreeNode) root
						.getFirstChild();
				TreePath tp = new TreePath(
						((DefaultMutableTreeNode) typeNode.getFirstChild())
								.getPath());
				setSelectionPath(tp); // select
			}
		}

		/**
		 * returns geo's TreePath
		 */
		private TreePath getGeoPath(GeoElement geo) {
			String typeString = geo.getTypeString();
			DefaultMutableTreeNode typeNode = typeNodesMap.get(typeString);
			if (typeNode == null)
				return null;

			int pos = AlgebraViewD.binarySearchGeo(typeNode,
					geo.getLabelSimple());
			if (pos == -1) {
				return null;
			}
			// add to selection
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) typeNode
					.getChildAt(pos);

			// expand typenode
			TreePath tp = new TreePath(node.getPath());

			return tp;
		}

		@Override
		public void clearSelection() {
			getSelectionModel().clearSelection();
		}

		/**
		 * Clears the list.
		 */
		public void clear() {
			root.removeAllChildren();
			treeModel.reload();
			typeNodesMap.clear();
		}

		/* ********************* */
		/* VIEW IMPLEMENTATION */
		/* ********************* */

		/**
		 * adds a new element to the list
		 */
		final public void add(GeoElement geo) {
			if (!geo.isLabelSet() || !geo.hasProperties())
				return;

			// get type node
			String typeString = geo.getTypeString();
			DefaultMutableTreeNode typeNode = typeNodesMap.get(typeString);
			GeoElementSpreadsheet ges = kernel.getGeoElementSpreadsheet();
			// init type node
			boolean initing = typeNode == null;
			if (initing) {
				String transTypeString = geo.translatedTypeString();
				typeNode = new DefaultMutableTreeNode(transTypeString);
				typeNodesMap.put(typeString, typeNode);

				// find insert pos
				int pos = root.getChildCount();
				for (int i = 0; i < pos; i++) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) root
							.getChildAt(i);
					if (transTypeString.compareTo(child.toString()) < 0) {
						pos = i;
						break;
					}
				}

				treeModel.insertNodeInto(typeNode, root, pos);
			}

			// check if already present in type node
			int pos = AlgebraViewD.binarySearchGeo(typeNode,
					geo.getLabelSimple());
			if (pos >= 0)
				return;

			// add geo to type node
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(geo);
			pos = AlgebraTree.getInsertPosition(typeNode, geo,
					AlgebraViewD.SortMode.DEPENDENCY);
			treeModel.insertNodeInto(newNode, typeNode, pos);

			// make sure something is selected
			if (getSelectionModel().isSelectionEmpty()) {
				selectFirstElement();
			}
		}

		/**
		 * removes an element from the list
		 */
		public void remove(GeoElement geo) {
			remove(geo, true);

			// close dialog if no elements left
			if (root.getChildCount() == 0) {
				closeDialog();
				return;
			}

			// make sure something is selected
			if (getSelectionModel().isSelectionEmpty()) {
				selectFirstElement();
			}
		}

		/**
		 * 
		 * @param binarySearch
		 *            true for binary, false for linear search
		 */
		public void remove(GeoElement geo, boolean binarySearch) {
			// get type node
			DefaultMutableTreeNode typeNode = typeNodesMap.get(geo
					.getTypeString());
			if (typeNode == null)
				return;

			GeoElementSpreadsheet ges = kernel.getGeoElementSpreadsheet();

			int pos = binarySearch ? AlgebraViewD.binarySearchGeo(typeNode,
					geo.getLabelSimple()) : AlgebraViewD.linearSearchGeo(
					typeNode, geo.getLabelSimple());
			if (pos > -1) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) typeNode
						.getChildAt(pos);
				treeModel.removeNodeFromParent(child);

				if (typeNode.getChildCount() == 0) {
					// last child
					typeNodesMap.remove(geo.getTypeString());
					treeModel.removeNodeFromParent(typeNode);
				}
			}
		}

		/**
		 * Returns the tree path of geo
		 * 
		 * @return returns null if geo is not in tree
		 */
		private TreePath getTreePath(GeoElement geo) {
			DefaultMutableTreeNode typeNode = typeNodesMap.get(geo
					.getTypeString());
			if (typeNode == null)
				return null;

			// find pos of geo
			int pos = AlgebraViewD.binarySearchGeo(typeNode,
					geo.getLabel(StringTemplate.defaultTemplate));
			if (pos == -1)
				return null;

			return new TreePath(
					((DefaultMutableTreeNode) typeNode.getChildAt(pos))
							.getPath());
		}

		/**
		 * renames an element and sorts list
		 */
		public void rename(GeoElement geo) {
			// the rename destroyed the alphabetical order,
			// so we have to use linear instead of binary search
			remove(geo, false);
			add(geo);
			geoElementSelected(geo, false);
		}

		/**
		 * updates a list of elements
		 */
		public void update(GeoElement geo) {
			repaint();
		}

		final public void updateVisualStyle(GeoElement geo) {
			update(geo);
		}

		public void updateAuxiliaryObject(GeoElement geo) {
			repaint();
		}

		public void setMode(int mode,ModeSetter m) {
			// don't react..
		}

		public void reset() {
			repaint();
		}

		public void clearView() {
			clear();
		}

		final public void repaintView() {
			repaint();
		}

		public void mouseDragged(MouseEvent arg0) {
		}

		public void mouseMoved(MouseEvent e) {
			Point loc = e.getPoint();
			GeoElement geo = AlgebraTree.getGeoElementForLocation(this, loc.x,
					loc.y);
			EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();

			// tell EuclidianView to handle mouse over
			ev.mouseMovedOver(geo);
			if (geo != null) {
				app.setTooltipFlag();
				setToolTipText(geo.getLongDescriptionHTML(true, true));
				app.clearTooltipFlag();
			} else {
				setToolTipText(null);
			}
		}

		/**
		 * Handles clicks on the show/hide icon to toggle the show-object
		 * status.
		 */
		public void mouseClicked(MouseEvent e) {
			if (AppD.isControlDown(e) || e.isShiftDown())
				return;

			// get GeoElement at mouse location
			TreePath tp = getPathForLocation(e.getX(), e.getY());
			GeoElement geo = AlgebraTree.getGeoElementForPath(tp);

			if (geo != null) {
				// check if we clicked on the 16x16 show/hide icon
				Rectangle rect = getPathBounds(tp);
				boolean iconClicked = rect != null && e.getX() - rect.x < 13; // distance
																				// from
																				// left
																				// border
				if (iconClicked) {
					// icon clicked: toggle show/hide
					geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
					geo.updateVisualStyle();
					kernel.notifyRepaint();

					// update properties dialog by selecting this geo again
					geoElementSelected(geo, false);
				}
			}
		}

		public void mouseEntered(MouseEvent arg0) {

		}

		public void mouseExited(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent arg0) {
		}

		public void mouseReleased(MouseEvent arg0) {
		}

		public int getViewID() {
			return App.VIEW_NONE;
		}

	} // JTreeGeoElements

	/*
	 * Keylistener implementation of PropertiesDialog
	 * 
	 * 
	 * public void keyPressed(KeyEvent e) { int code = e.getKeyCode(); switch
	 * (code) { case KeyEvent.VK_ESCAPE : //cancel(); closeDialog(); break;
	 * 
	 * case KeyEvent.VK_ENTER : // needed for input fields
	 * //applyButton.doClick(); break; } }
	 * 
	 * public void keyReleased(KeyEvent e) { }
	 * 
	 * public void keyTyped(KeyEvent e) { }
	 */

	public void windowGainedFocus(WindowEvent arg0) {
		// make sure this dialog is the current selection listener
		if (app.getMode() != EuclidianConstants.MODE_SELECTION_LISTENER
				|| app.getCurrentSelectionListener() != this) {
			app.setSelectionListenerMode(this);
			selectionChanged();
		}
	}

	public void windowLostFocus(WindowEvent arg0) {
	}

	// Tree selection listener
	public void valueChanged(TreeSelectionEvent e) {
		selectionChanged();
	}

	/*
	 * KeyListener
	 */
	public void keyPressed(KeyEvent e) {
		Object src = e.getSource();

		if (src instanceof JTreeGeoElements) {
			if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				deleteSelectedGeos();
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	// ignore if the view is dragged around (can't be dragged at all)
	public void beginDrag() {
	}

	public void endDrag() {
	}

	public JTreeGeoElements getGeoTree() {
		return geoTree;
	}

} // PropertiesDialog