package geogebra.gui.dialog.options;

import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.main.AbstractApplication;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.GeoTreeCellRenderer;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.main.Application;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;



/**
 * INNER CLASS JList for displaying GeoElements
 * 
 * @see GeoTreeCellRenderer
 * @author Markus Hohenwarter
 */
public class GeoTree extends JTree implements View,
		MouseMotionListener, MouseListener, SetLabels {

	private Application app;
	private static final long serialVersionUID = 1L;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode root;
	private HashMap<String, DefaultMutableTreeNode> typeNodesMap;

	/*
	 * has to be registered as view for GeoElement
	 */
	public GeoTree(Application app) {
		
		this.app =app;
		
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
		String typeString = geo.getObjectType();
		DefaultMutableTreeNode typeNode = typeNodesMap.get(typeString);
		if (typeNode == null)
			return null;

		int pos = AlgebraView.binarySearchGeo(typeNode, geo.getLabelSimple());
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
		String typeString = geo.getObjectType();
		DefaultMutableTreeNode typeNode = typeNodesMap.get(typeString);
		GeoElementSpreadsheet ges = app.getKernel()
				.getGeoElementSpreadsheet();
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
		int pos = AlgebraView
				.binarySearchGeo(typeNode, geo.getLabelSimple());
		if (pos >= 0)
			return;

		// add geo to type node
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(geo);
		pos = AlgebraView.getInsertPosition(typeNode, geo,
				AlgebraView.SortMode.DEPENDENCY);
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
			//TODO
			// closeDialog();
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
				.getObjectType());
		if (typeNode == null)
			return;

		GeoElementSpreadsheet ges = app.getKernel()
				.getGeoElementSpreadsheet();

		int pos = binarySearch ? AlgebraView.binarySearchGeo(typeNode,
				geo.getLabelSimple()) : AlgebraView.linearSearchGeo(
				typeNode, geo.getLabelSimple());
		if (pos > -1) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) typeNode
					.getChildAt(pos);
			treeModel.removeNodeFromParent(child);

			if (typeNode.getChildCount() == 0) {
				// last child
				typeNodesMap.remove(geo.getObjectType());
				treeModel.removeNodeFromParent(typeNode);
			}
		}
	}

	/**
	 * Returns the tree path of geo
	 * 
	 * @return returns null if geo is not in tree
	 */
	TreePath getTreePath(GeoElement geo) {
		DefaultMutableTreeNode typeNode = typeNodesMap.get(geo
				.getObjectType());
		if (typeNode == null)
			return null;

		// find pos of geo
		int pos = AlgebraView.binarySearchGeo(typeNode, geo.getLabel(StringTemplate.defaultTemplate));
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
		// TODO
		app.geoElementSelected(geo, false);
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

	public void setMode(int mode) {
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
	
	
	
	//========================================
	// Mouse listeners
	//========================================

	public void mouseDragged(MouseEvent arg0) {
		// do nothing
	}

	public void mouseMoved(MouseEvent e) {
		Point loc = e.getPoint();
		GeoElement geo = AlgebraView.getGeoElementForLocation(this, loc.x,
				loc.y);
		EuclidianViewND ev = app.getActiveEuclidianView();

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
		if (Application.isControlDown(e) || e.isShiftDown())
			return;

		// get GeoElement at mouse location
		TreePath tp = getPathForLocation(e.getX(), e.getY());
		GeoElement geo = AlgebraView.getGeoElementForPath(tp);

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
				geo.update();
				app.getKernel().notifyRepaint();

				// update properties dialog by selecting this geo again
				//TODO
				app.geoElementSelected(geo, false);
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
		return AbstractApplication.VIEW_NONE;
	}

} // JTreeGeoElements

