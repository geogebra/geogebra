/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgebraView.java
 *
 * Created on 27. September 2001, 11:30
 */

package org.geogebra.desktop.gui.view.algebra;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.DefaultCellEditor;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.kernel.LayerView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.gui.inputfield.MathTextField;
import org.geogebra.desktop.gui.view.Gridable;
import org.geogebra.desktop.main.AppD;

/**
 * AlgebraView with tree for free and dependent objects.
 * 
 * @author Markus
 */
public class AlgebraViewD extends AlgebraTree
		implements LayerView, Gridable, AlgebraView, SettingListener {

	private static final long serialVersionUID = 1L;

	/**
	 */
	// public static final int MODE_VIEW = 2;

	private MyDefaultTreeCellEditor editor;
	private MathTextField editTF;

	/**
	 * Root node for tree mode MODE_DEPENDENCY.
	 */
	protected DefaultMutableTreeNode rootDependency;

	/**
	 * Nodes for tree mode MODE_DEPENDENCY
	 */
	private DefaultMutableTreeNode depNode, indNode;

	protected DefaultMutableTreeNode auxiliaryNode;

	/* for SortMode.ORDER */
	private DefaultMutableTreeNode rootOrder;

	/* for SortMode.LAYER */
	private DefaultMutableTreeNode rootLayer;
	private HashMap<Integer, DefaultMutableTreeNode> layerNodesMap;

	/**
	 * The mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	protected SortMode treeMode;

	private GeoElement selectedGeoElement;
	private DefaultMutableTreeNode selectedNode;

	private AlgebraHelperBar helperBar;

	public AlgebraControllerD getAlgebraController() {
		return (AlgebraControllerD) algebraController;
	}

	/** Creates new AlgebraView */
	public AlgebraViewD(AlgebraControllerD algCtrl) {

		super(algCtrl, true);

		// Initialize settings and register listener
		app.getSettings().getAlgebra().addListener(this);

		settingsChanged(app.getSettings().getAlgebra());

	}

	@Override
	protected void initTree() {

		// this is the default value
		treeMode = SortMode.TYPE;

		// cell renderer (tooltips) and editor
		ToolTipManager.sharedInstance().registerComponent(this);

		// EDITOR
		setEditable(true);

		super.initTree();

		// enable drag n drop
		((AlgebraControllerD) algebraController).enableDnD();

		// attachView();

	}

	@Override
	protected MyRendererForAlgebraTree newMyRenderer(AppD app) {
		return new MyRendererForAlgebraView(app, this);
	}

	@Override
	protected void initModel() {
		// build default tree structure
		switch (treeMode) {
		default:
		case DEPENDENCY:
			// don't re-init anything
			if (rootDependency == null) {
				rootDependency = new DefaultMutableTreeNode();
				depNode = new DefaultMutableTreeNode(); // dependent objects
				indNode = new DefaultMutableTreeNode();
				auxiliaryNode = new DefaultMutableTreeNode();

				// independent objects
				rootDependency.add(indNode);
				rootDependency.add(depNode);
			}

			// set the root
			model.setRoot(rootDependency);

			// add auxiliary node if neccessary
			if (app.showAuxiliaryObjects) {
				if (!auxiliaryNode.isNodeChild(rootDependency)) {
					model.insertNodeInto(auxiliaryNode, rootDependency,
							rootDependency.getChildCount());
				}
			}
			break;

		case ORDER:
			if (rootOrder == null) {
				rootOrder = new DefaultMutableTreeNode();
			}

			checkRemoveAuxiliaryNode();

			// set the root
			model.setRoot(rootOrder);
			break;

		case TYPE:
			super.initModel();
			break;
		case LAYER:
			// don't re-init anything
			if (rootLayer == null) {
				rootLayer = new DefaultMutableTreeNode();
				layerNodesMap = new HashMap<>(
						10);
			}

			checkRemoveAuxiliaryNode();

			// set the root
			model.setRoot(rootLayer);
			break;
		}
	}

	@Override
	protected void checkRemoveAuxiliaryNode() {
		// always try to remove the auxiliary node
		if (app.showAuxiliaryObjects && auxiliaryNode != null) {
			removeAuxiliaryNode();
		}
	}

	protected void removeAuxiliaryNode() {
		if (auxiliaryNode.getParent() != null) {
			model.removeNodeFromParent(auxiliaryNode);
		}
	}

	boolean attached = false;

	public void attachView() {
		// AbstractApplication.printStacktrace("");

		if (attached) {
			return;
		}

		clearView();
		kernel.notifyAddAll(this);
		applySettings();
		kernel.attach(this);
		attached = true;

	}

	public void detachView() {
		// does nothing : view may be used in object properties
		/*
		 * kernel.detach(this); clearView(); attached = false;
		 */
	}

	@Override
	public void updateFonts() {
		super.updateFonts();
		Font font = app.getPlainFont();
		editor.setFont(font);
		editTF.setFont(font);
		if (helperBar != null) {
			helperBar.update();
		}
		renderer.update();
	}

	@Override
	protected void initTreeCellRendererEditor() {
		super.initTreeCellRendererEditor();
		editTF = new MathTextField(app);
		editTF.enableColoring(true);
		editTF.setShowSymbolTableIcon(true);
		editor = new MyDefaultTreeCellEditor(this, renderer,
				new MyCellEditorD(editTF, app));

		// add focus listener to the editor text field so that editing is
		// canceled on a focus lost event
		editTF.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				// only handle focus lost
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (e.getSource() == editTF) {
					cancelEditItem();
				}
			}
		});

		editor.addCellEditorListener(editor); // self-listening
		// setCellRenderer(renderer);
		setCellEditor(editor);
	}

	@Override
	public void clearSelection() {
		super.clearSelection();
		selectedGeoElement = null;
	}

	public GeoElement getSelectedGeoElement() {
		return selectedGeoElement;
	}

	@Override
	public boolean showAuxiliaryObjects() {
		return app.showAuxiliaryObjects;
	}

	public void setShowAuxiliaryObjects(boolean flag) {

		app.showAuxiliaryObjects = flag;

		cancelEditItem();

		if (flag) {
			clearView();

			switch (getTreeMode()) {
			default:
				// do nothing
				break;
			case DEPENDENCY:
				model.insertNodeInto(auxiliaryNode, rootDependency,
						rootDependency.getChildCount() - 1);
				break;
			}

			kernel.notifyAddAll(this);
		} else {
			// if we're listing the auxiliary objects in a single leaf we can
			// just remove that leaf, but for type-based categorization those
			// auxiliary nodes might be scattered across the whole tree,
			// therefore we just rebuild the tree
			switch (getTreeMode()) {
			case DEPENDENCY:
				if (auxiliaryNode.getParent() != null) {
					model.removeNodeFromParent(auxiliaryNode);
				}
				break;
			default:

				clearView();
				kernel.notifyAddAll(this);
			}
		}
	}

	@Override
	public SortMode getTreeMode() {
		return treeMode;
	}

	public void setTreeMode(int mode) {
		switch (mode) {
		default:
		case 0:
			setTreeMode(SortMode.DEPENDENCY);
			break;
		case 1:
			setTreeMode(SortMode.TYPE);
			break;
		case 2:
			setTreeMode(SortMode.LAYER);
			break;
		case 3:
			setTreeMode(SortMode.ORDER);
			break;
		}
	}

	/**
	 * @param value
	 *            Either AlgebraView.MODE_DEPDENCY or AlgebraView.MODE_TYPE
	 */
	@Override
	public void setTreeMode(SortMode value) {
		if (getTreeMode().equals(value)) {
			return;
		}
		clearView();

		this.treeMode = value;
		initModel();

		kernel.notifyAddAll(this);
		setLabels();
		// shake the tree so it looks good after the change
		SwingUtilities.updateComponentTreeUI(this);
	}

	/**
	 * @return The helper bar for this view.
	 */
	public AlgebraHelperBar getHelperBar() {
		if (helperBar == null) {
			helperBar = newAlgebraHelperBar();
		}

		return helperBar;
	}

	/**
	 * 
	 * @return new algebra helper bar
	 */
	final protected AlgebraHelperBar newAlgebraHelperBar() {
		return new AlgebraHelperBar(this, app);
	}

	/**
	 * Open Editor textfield for geo.
	 */
	@Override
	public void startEditItem(GeoElement geo) {
		if (geo == null) {
			return;
		}

		// open Object Properties for eg GeoImages
		// also for GeoPenStroke
		if (!geo.isAlgebraViewEditable()) {
			ArrayList<GeoElement> geos = new ArrayList<>();
			geos.add(geo);
			app.getDialogManager().showPropertiesDialog(geos);
			return;
		}

		if (!geo.isPointOnPath() && !geo.isPointInRegion()) {
			if (!geo.isIndependent() || !attached) // needed for F2 when Algebra
			// View closed
			{
				if (geo.isRedefineable()) {
					app.getDialogManager().showRedefineDialog(geo, true);
				}
				return;
			}

			if (!geo.isChangeable()) {
				if (geo.isProtected(EventType.UPDATE)) {
					// app.showMessage(app.getError("AssignmentToFixed"));
				} else if (geo.isRedefineable()) {
					app.getDialogManager().showRedefineDialog(geo, true);
				}
				return;
			}
		}

		DefaultMutableTreeNode node = nodeTable.get(geo);

		if (node != null) {
			cancelEditItem();
			// select and show node
			TreePath tp = new TreePath(node.getPath());
			setSelectionPath(tp); // select
			expandPath(tp);
			makeVisible(tp);
			scrollPathToVisible(tp);
			startEditingAtPath(tp); // opend editing text field
		}
	}

	/**
	 * resets all fix labels of the View. This method is called by the
	 * application if the language setting is changed.
	 */
	@Override
	public void setLabels() {

		super.setLabels();

		if (helperBar != null) {
			helperBar.updateLabels();
		}
	}

	/**
	 * set labels on the tree
	 */
	@Override
	protected void setTreeLabels() {
		Localization loc = app.getLocalization();
		switch (getTreeMode()) {
		default:
		case DEPENDENCY:

			indNode.setUserObject(loc.getMenu("FreeObjects"));
			model.nodeChanged(indNode);

			depNode.setUserObject(loc.getMenu("DependentObjects"));
			model.nodeChanged(depNode);

			auxiliaryNode.setUserObject(loc.getMenu("AuxiliaryObjects"));
			model.nodeChanged(auxiliaryNode);
			break;
		case TYPE:
			super.setTreeLabels();
			break;
		case LAYER:
			DefaultMutableTreeNode node;
			for (Entry<Integer, DefaultMutableTreeNode> entry : layerNodesMap
					.entrySet()) {
				Integer key = entry.getKey();
				node = entry.getValue();
				node.setUserObject(key);
				model.nodeChanged(node);
			}
			break;
		case ORDER:
			model.nodeChanged(rootOrder);
			break;
		}
	}

	/**
	 * 
	 * @param geo
	 * @return parent node of this geo
	 */
	@Override
	protected DefaultMutableTreeNode getParentNode(GeoElement geo,
			int forceLayer) {
		DefaultMutableTreeNode parent;

		switch (treeMode) {
		case DEPENDENCY:
			if (geo.isAuxiliaryObject()) {
				parent = auxiliaryNode;
			} else if (geo.isIndependent()) {
				parent = indNode;
			} else {
				parent = depNode;
			}
			break;
		case TYPE:
			parent = super.getParentNode(geo, forceLayer);
			break;
		case LAYER:
			// get type node
			int layer = forceLayer > -1 ? forceLayer : geo.getLayer();
			parent = layerNodesMap.get(layer);

			// do we have to create the parent node?
			if (parent == null) {
				String layerStr = layer + "";
				parent = new DefaultMutableTreeNode(layer);
				layerNodesMap.put(layer, parent);

				// find insert pos
				int pos = rootLayer.getChildCount();
				for (int i = 0; i < pos; i++) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootLayer
							.getChildAt(i);
					if (layerStr.compareTo(child.toString()) < 0) {
						pos = i;
						break;
					}
				}

				model.insertNodeInto(parent, rootLayer, pos);
			}
			break;
		case ORDER:
			parent = rootOrder;

			break;
		default:
			parent = null;
		}

		return parent;
	}

	/**
	 * Performs a binary search for geo among the children of parent. All
	 * children of parent have to be instances of GeoElement sorted
	 * alphabetically by their names.
	 * 
	 * @return -1 when not found
	 */
	final public static int binarySearchGeo(DefaultMutableTreeNode parent,
			String geoLabel) {
		int left = 0;
		int right = parent.getChildCount() - 1;
		if (right == -1 || geoLabel == null) {
			return -1;
		}

		// binary search for geo's label
		while (left <= right) {
			int middle = (left + right) / 2;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent
					.getChildAt(middle);
			String nodeLabel = ((GeoElement) node.getUserObject())
					.getLabelSimple();

			int compare = GeoElement.compareLabels(geoLabel, nodeLabel);
			if (compare < 0) {
				right = middle - 1;
			} else if (compare > 0) {
				left = middle + 1;
			} else {
				return middle;
			}
		}

		return -1;
	}

	/**
	 * Performs a linear search for geo among the children of parent.
	 * 
	 * @return -1 when not found
	 */
	final public static int linearSearchGeo(DefaultMutableTreeNode parent,
			String geoLabel) {
		if (geoLabel == null) {
			return -1;
		}
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent
					.getChildAt(i);
			GeoElement g = (GeoElement) node.getUserObject();
			if (geoLabel.equals(g.getLabel(StringTemplate.defaultTemplate))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * remove all from the tree
	 */
	@Override
	protected void clearTree() {
		switch (getTreeMode()) {
		default:
		case DEPENDENCY:
			indNode.removeAllChildren();
			depNode.removeAllChildren();
			auxiliaryNode.removeAllChildren();
			break;
		case TYPE:
			super.clearTree();
			break;
		case LAYER:
			rootLayer.removeAllChildren();
			layerNodesMap.clear();
			break;
		case ORDER:
			rootOrder.removeAllChildren();
		}
	}

	@Override
	public DefaultMutableTreeNode getRoot() {
		switch (getTreeMode()) {
		case DEPENDENCY:
			return rootDependency;
		case TYPE:
		default:
			return super.getRoot();
		case LAYER:
			return rootLayer;
		case ORDER:
			return rootOrder;
		}
	}

	@Override
	public ArrayList<GeoElement> getGeosBetween(GeoElement geo1,
			GeoElement geo2) {

		// specific case for ORDER mode
		if (getTreeMode() == SortMode.ORDER) {
			int found = 0;
			ArrayList<GeoElement> ret = new ArrayList<>();
			DefaultMutableTreeNode root = getRoot();
			for (int i = 0; i < root.getChildCount() && found < 2; i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) root
						.getChildAt(i);
				Object ob = child.getUserObject();
				if (ob == geo1 || ob == geo2) {
					found++;
				}
				if (found > 0) {
					ret.add((GeoElement) ob);
				}
			}
			return ret;
		}

		// other cases
		return super.getGeosBetween(geo1, geo2);
	}

	@Override
	public void repaintView() {
		repaint();
	}

	/**
	 * Reset the algebra view if the mode changed.
	 */
	@Override
	public void setMode(int mode, ModeSetter m) {
		reset();
	}

	@Override
	public void reset() {
		cancelEditItem();
		repaint();
	}

	/**
	 * Remove this node from the model.
	 */
	@Override
	protected void removeFromModelForMode(DefaultMutableTreeNode node,
			DefaultTreeModel algebraModel) {

		// remove the type branch if there are no more children
		switch (treeMode) {
		default:
			// do nothing
			break;
		case TYPE:
			super.removeFromModelForMode(node, algebraModel);
			break;
		case LAYER:
			removeFromLayer(((GeoElement) node.getUserObject()).getLayer());
			break;
		}
	}

	private void removeFromLayer(int i) {
		DefaultMutableTreeNode parent = layerNodesMap.get(i);

		// this has been the last node
		if ((parent != null) && parent.getChildCount() == 0) {
			layerNodesMap.remove(i);
			model.removeNodeFromParent(parent);
		}

	}

	/**
	 * inner class MyEditor handles editing of tree nodes
	 * 
	 * Created on 28. September 2001, 12:36
	 */
	private class MyDefaultTreeCellEditor extends DefaultTreeCellEditor
			implements CellEditorListener {

		public MyDefaultTreeCellEditor(AlgebraViewD tree,
				DefaultTreeCellRenderer renderer, DefaultCellEditor editor) {
			super(tree, renderer, editor);
			// editor container that expands to fill the width of the tree's
			// enclosing panel
			editingContainer = new WideEditorContainer();
		}

		/*
		 * CellEditorListener implementation
		 */
		@Override
		public void editingCanceled(ChangeEvent event) {
			// nothing to do
		}

		@Override
		public void editingStopped(ChangeEvent event) {

			// get the entered String
			String newValue = getCellEditorValue().toString();

			// the userObject was changed to this String
			// reset it to the old userObject, which we stored
			// in selectedGeoElement (see valueChanged())
			// only nodes with a GeoElement as userObject can be edited!
			selectedNode.setUserObject(selectedGeoElement);

			// change this GeoElement in the Kernel

			// allow shift-double-click on a PointonPath in Algebra View to
			// change without redefine
			boolean redefine = !selectedGeoElement.isPointOnPath();
			AsyncOperation<GeoElementND> callback = new AsyncOperation<GeoElementND>() {

				@Override
				public void callback(GeoElementND geo) {
					if (geo != null) {
						selectedGeoElement = geo.toGeoElement();
						selectedNode.setUserObject(selectedGeoElement);
					}

					((DefaultTreeModel) getModel()).nodeChanged(selectedNode);

				}
			};
			kernel.getAlgebraProcessor().changeGeoElement(selectedGeoElement,
					newValue, redefine, true, app.getDefaultErrorHandler(),
					callback);

		}

		/*
		 * OVERWRITE SOME METHODS TO ONLY ALLOW EDITING OF GeoElements
		 */

		@Override
		public boolean isCellEditable(EventObject event) {

			if (event == null) {
				return true;
			}

			return false;
		}

		//
		// TreeSelectionListener
		//

		/**
		 * Resets lastPath.
		 */
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if (tree != null) {
				if (tree.getSelectionCount() == 1) {
					lastPath = tree.getSelectionPath();
				} else {
					lastPath = null;
				}
				/***** ADDED by Markus Hohenwarter ***********/
				storeSelection(lastPath);
				/********************************************/
			}
			if (timer != null) {
				timer.stop();
			}
		}

		/**
		 * stores currently selected GeoElement and node. selectedNode,
		 * selectedGeoElement are private members of AlgebraView
		 */
		private void storeSelection(TreePath tp) {
			if (tp == null) {
				return;
			}

			Object ob;
			selectedNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
			if (selectedNode != null && (ob = selectedNode
					.getUserObject()) instanceof GeoElement) {
				selectedGeoElement = (GeoElement) ob;
			} else {
				selectedGeoElement = null;
			}
		}

		/**
		 * Overrides getTreeCellEditor so that a custom
		 * DefaultTreeCellEditor.EditorContainer class can be called to adjust
		 * the container width.
		 */
		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row) {

			Component c = super.getTreeCellEditorComponent(tree, value,
					isSelected, expanded, leaf, row);
			((WideEditorContainer) editingContainer).updateContainer(tree,
					lastPath, offset, editingComponent);
			return c;

		}

		/**
		 * Extends DefaultTreeCellEditor.EditorContainer to allow full-width
		 * editor fields.
		 */
		class WideEditorContainer
				extends DefaultTreeCellEditor.EditorContainer {

			private static final long serialVersionUID = 1L;

			JTree tree;
			TreePath lastPath;
			int offset;
			Component editingComponent;

			/**
			 * Overrides doLayout so that the editor component width is resized
			 * to extend the full width of the tree's enclosing panel
			 */
			@Override
			public void doLayout() {
				if (editingComponent != null) {
					// get component preferred size
					Dimension eSize = editingComponent.getPreferredSize();

					// expand component width to extend to the enclosing
					// container bounds
					int n = lastPath.getPathCount();
					Rectangle r = tree.getParent().getBounds();
					eSize.width = r.width - (offset * n);

					// only show the symbol table icon if the editor is wide
					// enough
					((MathTextField) editingComponent)
							.setShowSymbolTableIcon(eSize.width > 100);

					// set the component size and location
					editingComponent.setSize(eSize);
					editingComponent.setLocation(offset, 0);
					editingComponent.setBounds(offset, 0, eSize.width,
							eSize.height);
					setSize(new Dimension(eSize.width + offset, eSize.height));
				}
			}

			/**
			 * Overrides getPreferredSize to prevent extra large heights when
			 * other tree nodes contain tall LaTeX images
			 */
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				if (editingComponent != null) {
					d.height = editingComponent.getHeight();
				}
				return d;
			}

			void updateContainer(JTree tree, TreePath lastPath, int offset,
					Component editingComponent) {
				this.tree = tree;
				this.lastPath = lastPath;
				this.offset = offset;
				this.editingComponent = editingComponent;
			}
		}

	} // MyDefaultTreeCellEditor

	@Override
	public int getViewID() {
		return App.VIEW_ALGEBRA;
	}

	@Override
	public AppD getApplication() {
		return app;
	}

	@Override
	public int[] getGridColwidths() {
		return new int[] { getWidth() };
	}

	@Override
	public int[] getGridRowHeights() {
		// Object root=model.getRoot();
		// ArrayList<Integer> heights=new ArrayList<Integer>();
		// for (int i=0;i<model.getChildCount(root);i++){
		// Object folder=model.getChild(root, i);
		// if (model.)
		// }
		// // m.getChildCount(root);
		//
		// return new int[]{getHeight()};

		int[] heights;
		if (getRowCount() > 0) {
			heights = new int[getRowCount()];
			for (int i = 0; i < heights.length; i++) {
				heights[i] = getRowBounds(i).height;
			}
		} else {
			heights = new int[1];
		}

		heights[0] += 2;
		return heights;
	}

	@Override
	public Component[][] getPrintComponents() {
		return new Component[][] { { this } };
	}

	@Override
	public void changeLayer(GeoElement g, int oldLayer, int newLayer) {
		if (this.treeMode.equals(SortMode.LAYER)) {
			DefaultMutableTreeNode node = nodeTable.get(g);

			if (node != null) {
				((DefaultTreeModel) getModel()).removeNodeFromParent(node);
				nodeTable.remove(node.getUserObject());
				removeFromLayer(oldLayer);
			}

			this.add(g, newLayer);

		}
	}

	
	// returns settings in XML format
	// 
	// public void getXML(StringBuilder sb) {
	// 
	// sb.append("<algebraView>\n");
	// sb.append("\t<useLaTeX ");
	// sb.append(" value=\""); 
	// sb.append(isRenderLaTeX());
	// sb.append("\"");
	// sb.append("/>\n");
	// sb.append("</algebraView>\n");


	// temporary proxies for the temporary implementation of AlgebraController
	// in common
	public GeoElement getGeoElementForPath(Object tp) {
		return getGeoElementForPath((TreePath) tp);
	}

	public GeoElement getGeoElementForLocation(Object tree, int x, int y) {
		return getGeoElementForLocation((JTree) tree, x, y);
	}

	public Object getPathBounds(Object tp) {
		return getPathBounds((TreePath) tp);
	}

	// temporary proxies end

	@Override
	protected boolean show(GeoElement geo) {
		return super.show(geo) && geo.showInAlgebraView()
				&& geo.isSetAlgebraVisible();
	}

	private StringBuilder sbXML;

	private void updateCollapsedNodesIndices() {

		// no collapsed nodes
		if (getTreeMode() == SortMode.ORDER) {
			collapsedNodes = null;
			return;
		}

		if (collapsedNodes == null) {
			collapsedNodes = new ArrayList<>();
		} else {
			collapsedNodes.clear();
		}

		DefaultMutableTreeNode root = getRoot();
		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) root
					.getChildAt(i);
			if (isCollapsed(new TreePath(node.getPath()))) {
				collapsedNodes.add(i);
			}
		}

	}

	/**
	 * returns settings in XML format
	 */
	public void getXML(StringBuilder sb, boolean asPreference) {

		if (sbXML == null) {
			sbXML = new StringBuilder();
		} else {
			sbXML.setLength(0);
		}

		sbXML.append("\t<mode ");
		sbXML.append("val=\"");
		sbXML.append(getTreeMode().toInt());
		sbXML.append("\"");
		sbXML.append("/>\n");

		// auxiliary objects
		boolean flag = showAuxiliaryObjects();
		if (flag) {
			sbXML.append("\t<auxiliary ");
			sbXML.append("show=\"");
			sbXML.append(flag);
			sbXML.append("\"");
			sbXML.append("/>\n");
		}

		// collapsed nodes
		updateCollapsedNodesIndices();
		if (collapsedNodes != null && collapsedNodes.size() > 0) {
			sbXML.append("\t<collapsed ");
			sbXML.append("val=\"");
			sbXML.append(collapsedNodes.get(0));
			for (int i = 1; i < collapsedNodes.size(); i++) {
				sbXML.append(",");
				sbXML.append(collapsedNodes.get(i));
			}
			sbXML.append("\"");
			sbXML.append("/>\n");
		}

		if (sbXML.length() > 0) {
			sb.append("<algebraView>\n");
			sb.append(sbXML);
			sb.append("</algebraView>\n");
		}

	}

	private ArrayList<Integer> collapsedNodes;

	private void setCollapsedNodes(int[] collapsedNodes) {
		if (collapsedNodes == null) {
			return;
		}

		if (this.collapsedNodes == null) {
			this.collapsedNodes = new ArrayList<>();
		} else {
			this.collapsedNodes.clear();
		}

		for (int i = 0; i < collapsedNodes.length; i++) {
			this.collapsedNodes.add(collapsedNodes[i]);
		}
	}

	/**
	 * apply the settings
	 */
	public void applySettings() {

		if (!settingsChanged) {
			// that means that no settings were stored in the file: reset
			// settings to have default
			AlgebraSettings settings = app.getSettings().getAlgebra();
			settings.reset();
			settingsChanged(settings);
		}

		settingsChanged = false;

		// auxilliary objects
		setShowAuxiliaryObjects(showAuxiliaryObjectsSettings);

		// collapsed nodes
		if (collapsedNodes == null) {
			return;
		}

		DefaultMutableTreeNode root = getRoot();
		for (int i : collapsedNodes) {
			// validate i, #4346
			if (i >= root.getChildCount()) {
				continue;
			}
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) root
					.getChildAt(i);
			collapsePath(new TreePath(node.getPath()));
		}

	}

	private boolean showAuxiliaryObjectsSettings = false;

	private boolean settingsChanged = false;

	@Override
	public void settingsChanged(AbstractSettings settings) {

		AlgebraSettings algebraSettings = (AlgebraSettings) settings;
		setTreeMode(algebraSettings.getTreeMode());
		showAuxiliaryObjectsSettings = algebraSettings
				.getShowAuxiliaryObjects();
		setCollapsedNodes(algebraSettings.getCollapsedNodes());

		settingsChanged = true;

	}

	@Override
	public void setFocus(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public GeoElement getLastSelectedGeo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastSelectedGeo(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean suggestRepaint() {
		return false;
		// only for web
	}

	@Override
	public boolean isAttachedToKernel() {
		return attached;
	}

	@Override
	public GeoElement getDraggedGeo() {
		// temporary change to fix it because it did not compile
		return null;
	}

	@Override
	public void setShowAlgebraInput(boolean b) {
		// only used in web
	}

	@Override
	public void resetItems(boolean clear) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelEditItem() {
		cancelEditing();
	}

	@Override
	public boolean isEditItem() {
		return isEditing();
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		// nothing to do here
	}
} // AlgebraView
