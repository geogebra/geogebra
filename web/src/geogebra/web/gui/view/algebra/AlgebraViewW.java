/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
//
This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.web.gui.view.algebra;

import geogebra.common.awt.GFont;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.algebra.AlgebraController;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.LayerView;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.AlgebraSettings;
import geogebra.common.main.settings.SettingListener;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * AlgebraView with tree for free and dependent objects.
 * 
 */

public class AlgebraViewW extends Tree implements LayerView, SetLabels, geogebra.common.gui.view.algebra.AlgebraView, SettingListener {

	private static final long serialVersionUID = 1L;

	/**
	 */
	//public static final int MODE_VIEW = 2;

	protected AppW app; // parent appame
	private Kernel kernel;

	//private MyRenderer renderer;
	//private MyDefaultTreeCellEditor editor;
	//private MathTextField editTF;

	// store all pairs of GeoElement -> node in the Tree
	private HashMap<GeoElement, TreeItem> nodeTable = new HashMap<GeoElement, TreeItem>(
			500);

	/**
	 * The tree model.
	 */
	//protected DefaultTreeModel model;

	/**
	 * Root node for tree mode MODE_DEPENDENCY.
	 */
	//protected TreeItem rootDependency;

	/**
	 * Nodes for tree mode MODE_DEPENDENCY
	 */
	private TreeItem depNode, indNode;

	protected TreeItem auxiliaryNode;

	/**
	 * Root node for tree mode MODE_TYPE.
	 */
	private TreeItem rootType;

	/**
	 * Nodes for tree mode MODE_TYPE
	 */
	private HashMap<String, TreeItem> typeNodesMap;

	/* for SortMode.ORDER */
	private TreeItem rootOrder;

	/* for SortMode.LAYER */
	private TreeItem rootLayer;
	private HashMap<Integer, TreeItem> layerNodesMap;


	/**
	 * The mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	protected SortMode treeMode;

	private GeoElement selectedGeoElement;
	private TreeItem selectedNode;

	//private AlgebraHelperBar helperBar;

	private AlgebraController algebraController;

	public AlgebraController getAlgebraController() {
		return algebraController;
	}

	/**
	 * Flag for LaTeX rendering
	 */
	final private static boolean renderLaTeX = true;

	/** Creates new AlgebraView */
	public AlgebraViewW(AlgebraController algCtrl) {

		super();

		App.debug("creating Algebra View");

		app = (AppW)algCtrl.getApplication();
		kernel = algCtrl.getKernel();
		algCtrl.setView(this);
		this.algebraController = algCtrl;
		// this is the default value
		treeMode = SortMode.TYPE;

		// cell renderer (tooltips) and editor
		//ToolTipManager.sharedInstance().registerComponent(this);

		// EDITOR
		//setEditable(true);
		//initTreeCellRendererEditor();

		// add listener


		//addMouseDownHandler((AlgebraController)algCtrl);
		//addMouseUpHandler((AlgebraController)algCtrl);
		//addMouseMoveHandler((AlgebraController)algCtrl);


		// add small border
		//setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));

		// initializes the tree model
		initModel();

		setLabels();
		
		getElement().setId("View_"+App.VIEW_ALGEBRA);

		this.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		this.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		this.addKeyPressHandler(this.app.getGlobalKeyDispatcher());

		this.setFocus(true);

		// tree's options
		//setRootVisible(false);
		// show lines from parent to children
		//putClientProperty("JTree.lineStyle", "Angled");
		//setInvokesStopCellEditing(true);
		//setScrollsOnExpand(true);
		//setRowHeight(-1); // to enable flexible height of cells

		//setToggleClickCount(1);

		// enable drag n drop
		//algCtrl.enableDnD();

		// Initialize settings and register listener
		app.getSettings().getAlgebra().addListener(this);

		
		settingsChanged(app.getSettings().getAlgebra());
	}

	/**
	 * Method to initialize the tree model of the current tree mode. This method
	 * should be called whenever the tree mode is changed, it won't initialize
	 * anything if not necessary.
	 * 
	 * This method will also actually change the model of the tree.
	 */
	protected void initModel() {
		// build default tree structure
		switch (treeMode) {
		case DEPENDENCY:
			// don't re-init anything
			if (depNode == null || indNode == null || auxiliaryNode == null) {
				//rootDependency = new TreeItem();
				depNode = new TreeItem(); // dependent objects
				indNode = new TreeItem();
				auxiliaryNode = new TreeItem();

			}

			// set the root
			clear();
			addItem(indNode);
			addItem(depNode);

			// add auxiliary node if neccessary
			if (app.showAuxiliaryObjects) {
				if (auxiliaryNode.getTree() != this) {
					addItem(auxiliaryNode);
				}
			}
			break;
		case ORDER:
			if (rootOrder == null) {
				// both rootOrder and AlgebraView will have the Tree items
				rootOrder = new TreeItem();
			}
			setUserObject(rootOrder, "");

			// always try to remove the auxiliary node
			if (app.showAuxiliaryObjects && auxiliaryNode != null) {
				removeAuxiliaryNode();
			}

			// set the root
			clear();
			//addItem(rootOrder);
			break;

		case TYPE:
			// don't re-init anything
			if (rootType == null) {
				rootType = new TreeItem();
				//setUserObject(rootType, "");
				typeNodesMap = new HashMap<String, TreeItem>(5);
			}

			// always try to remove the auxiliary node
			if (app.showAuxiliaryObjects && auxiliaryNode != null) {
				removeAuxiliaryNode();
			}

			// set the root
			clear();
			//addItem(rootType);
			break;
		case LAYER:
			// don't re-init anything
			if (rootLayer == null) {
				rootLayer = new TreeItem();
				layerNodesMap = new HashMap<Integer, TreeItem>(10);
			}

			// always try to remove the auxiliary node
			if (app.showAuxiliaryObjects && auxiliaryNode != null) {
				removeAuxiliaryNode();
			}

			// set the root
			clear();
			//addItem(rootLayer);
			break;
		}
		
	}

	protected void removeAuxiliaryNode() {
		removeItem(auxiliaryNode);
	}

	boolean attached = false;

	public void attachView() {

		if (attached)
			return;

		clearView();
		kernel.notifyAddAll(this);
		applySettings();
		kernel.attach(this);
		attached = true;
		/*
		if (treeMode == SortMode.DEPENDENCY) {
			indNode.setState(true);
			depNode.setState(true);
			if (auxiliaryNode.getParentItem() != null) {
				auxiliaryNode.setState(true);
			}
		}*/
	}

	public void detachView() {
		kernel.detach(this);
		clearView();
		attached = false;
	}

	public void updateFonts() {
		GFont font = app.getPlainFontCommon();
		getStyleElement().getStyle().setFontStyle(Style.FontStyle.valueOf(font.isItalic()?"ITALIC":"NORMAL"));
		getStyleElement().getStyle().setFontSize(font.getSize(), Style.Unit.PX);
		getStyleElement().getStyle().setFontWeight(Style.FontWeight.valueOf(font.isBold()?"BOLD":"NORMAL"));
		//setFont(font);
		//editor.setFont(font);
		//renderer.setFont(font);
		//editTF.setFont(font);
	}

	/*
	private void initTreeCellRendererEditor() {
		renderer = newMyRenderer(app);
		editTF = new MathTextField(app);
		editTF.enableColoring(true);
		editTF.setShowSymbolTableIcon(true);
		editor = new MyDefaultTreeCellEditor(this, renderer, new MyCellEditor(
				editTF, app));

		// add focus listener to the editor text field so that editing is 
		// canceled on a focus lost event
		editTF.addFocusListener(new FocusListener(){
			
			public void focusGained(FocusEvent e) {				
			}
			public void focusLost(FocusEvent e) {
				if(e.getSource() == editTF)
					cancelEditing();
			}
		});
		
		
		editor.addCellEditorListener(editor); // self-listening
		setCellRenderer(renderer);
		setCellEditor(editor);
	}*/

	/**
	 * 
	 * @param app
	 * @return new renderer of a cell
	 */
	/*protected MyRenderer newMyRenderer(Application app) {
		return new MyRenderer(app, this);
	}*/

	//@Override
	public void clearSelection() {

		// deselecting this causes a bug; it maybe fixed
		// by changing the repaintView method too,
		// adding setSelectedItem( some TreeItem ),
		// but which TreeItem should be that if more are selected?
		// that's why Arpad choosed to comment this out instead
		//super.setSelectedItem(null);

		for (int i = 0; i < getItemCount(); i++) {
			if (!(getItem(i).getUserObject() instanceof GeoElement))
				for (int j = 0; j < getItem(i).getChildCount(); j++) {
					getItem(i).getChild(j).setSelected(false);
				}
		}
		selectedGeoElement = null;
	}

	public GeoElement getSelectedGeoElement() {
		return selectedGeoElement;
	}

	public boolean showAuxiliaryObjects() {
		return app.showAuxiliaryObjects;
	}

	public void setShowAuxiliaryObjects(boolean flag) {

		app.showAuxiliaryObjects = flag;

		cancelEditing();

		if (flag) {
			clearView();

			switch (getTreeMode()) {
			case DEPENDENCY:
				addItem(auxiliaryNode);
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
				if (auxiliaryNode.getParentItem() != null) {
					removeItem(auxiliaryNode);
				}
				break;
			default:
			
				clearView();
				kernel.notifyAddAll(this);
			}
		}
	}

	/**
	 * @return The display mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	public SortMode getTreeMode() {
		return treeMode;
	}

	/**
	 * @param value
	 *            Either AlgebraView.MODE_DEPDENCY or AlgebraView.MODE_TYPE
	 */
	public void setTreeMode(SortMode value) {
		if (getTreeMode().equals(value)) {
			return;
		}

		clearView();

		this.treeMode = value;
		initModel();

		kernel.notifyAddAll(this);
		setLabels();
	}

	/**
	 * @return The helper bar for this view.
	 */
	/*public AlgebraHelperBar getHelperBar() {
		if (helperBar == null) {
			helperBar = newAlgebraHelperBar();
		}

		return helperBar;
	}*/

	/**
	 * 
	 * @return new algebra helper bar
	 */
	/*protected AlgebraHelperBar newAlgebraHelperBar() {
		return new AlgebraHelperBar(this, app);
	}*/

	public Object getPathForLocation(int x, int y) {
		// TODO: auto-generated method stub
		return null;
	}

	/*
	public static GeoElement getGeoElementForLocation(JTree tree, int x, int y) {
		TreePath tp = tree.getPathForLocation(x, y);
		return getGeoElementForPath(tp);
	}

	public static GeoElement getGeoElementForPath(TreePath tp) {
		if (tp == null)
			return null;

		Object ob;
		TreeItem node = (TreeItem) tp
				.getLastPathComponent();
		if (node != null && (ob = node.getUserObject()) instanceof GeoElement)
			return (GeoElement) ob;
		else
			return null;
	}*/

	/*@Override
	public void setToolTipText(String text) {
		renderer.setToolTipText(text);
	}*/

	public boolean editing = false;

	/**
	 * Open Editor textfield for geo.
	 */
	public void startEditing(GeoElement geo, boolean shiftDown) {
		if (geo == null)
			return;

		// open Object Properties for eg GeoImages
		if (!geo.isAlgebraViewEditable()) {
			//FIXMEWEB ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
			//FIXMEWEB geos.add(geo);
			//FIXMEWEB app.getDialogManager().showPropertiesDialog(geos);
			return;
		}

		if (!shiftDown || !geo.isPointOnPath() && !geo.isPointInRegion()) {
			if (!geo.isIndependent() || !attached) // needed for F2 when Algebra
				// View closed
			{
				if (geo.isRedefineable()) {
					app.getDialogManager().showRedefineDialog(geo, true);
				}
				return;
			}

			if (!geo.isChangeable()) {
				if (geo.isFixed()) {
					app.showMessage(app.getError("AssignmentToFixed"));
				} else if (geo.isRedefineable()) {
					app.getDialogManager().showRedefineDialog(geo, true);
				}
				return;
			}
		}

		TreeItem node = nodeTable.get(geo);

		if (node != null) {
			cancelEditing();
			//FIXMEWEB select and show node
			Widget wi = node.getWidget();
			editing = true;
			setAnimationEnabled(false);
			if (wi instanceof RadioButtonTreeItem)
				((RadioButtonTreeItem)wi).startEditing();
		}
	}

	/**
	 * resets all fix labels of the View. This method is called by the
	 * application if the language setting is changed.
	 */
	public void setLabels() {

		setTreeLabels();

		/*if (helperBar != null) {
			helperBar.updateLabels();
		}*/
	}

	/**
	 * set labels on the tree
	 */
	protected void setTreeLabels() {
		TreeItem node;
		switch(getTreeMode()) {
		case DEPENDENCY:
			setUserObject(indNode, app.getPlain("FreeObjects") );
			setUserObject(depNode, app.getPlain("DependentObjects") );
			setUserObject(auxiliaryNode, app.getPlain("AuxiliaryObjects") );
			break;
		case TYPE:
			for (String key : typeNodesMap.keySet()) {
				node = typeNodesMap.get(key);
				setUserObject(node, app.getPlain(key) );
			}
			break;
		case LAYER:
			for (Integer key : layerNodesMap.keySet()) {
				node = layerNodesMap.get(key);
				setUserObject(node, app.getPlain("LayerA",key.toString())+"TODO"+key );
			}
			break;
		case ORDER:
			break;
		}
	}

	/**
	 * adds a new node to the tree
	 */
	public void add(GeoElement geo) {
		add(geo,-1);
	}
	private void add(GeoElement geo,int forceLayer) {
		cancelEditing();

		if (geo.isLabelSet() && geo.showInAlgebraView()
				&& geo.isSetAlgebraVisible()) {
			// don't add auxiliary objects if the tree is categorized by type
			if (!getTreeMode().equals(SortMode.DEPENDENCY) && !showAuxiliaryObjects()
					&& geo.isAuxiliaryObject()) {
				return;
			}

			TreeItem parent, node;
			node = new TreeItem();

			parent = getParentNode(geo,forceLayer);

			// add node to model (alphabetically ordered)
			int pos = getInsertPosition(parent, geo, treeMode);

			if (pos == parent.getChildCount()) {
				parent.addItem(node);
				if (parent.equals(rootOrder))
					addItem(node);
			} else try {
				parent.insertItem(pos, node);
				if (parent.equals(rootOrder))
					insertItem(pos, node);
			} catch (IndexOutOfBoundsException e) {
				parent.addItem(node);
				if (parent.equals(rootOrder))
					addItem(node);
			}

			setUserObject(node, geo);
			nodeTable.put(geo, node);

			// ensure that the leaf with the new object is visible
			parent.setState(true);
		}
	}

	/**
	 * 
	 * @param geo
	 * @return parent node of this geo
	 */
	protected TreeItem getParentNode(GeoElement geo,int forceLayer) {
		TreeItem parent;

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
			// get type node
			String typeString = geo.getTypeStringForAlgebraView();
			parent = typeNodesMap.get(typeString);

			// do we have to create the parent node?
			if (parent == null) {
				String transTypeString = geo.translatedTypeStringForAlgebraView();
				parent = new TreeItem(transTypeString);
				setUserObject(parent, transTypeString);
				typeNodesMap.put(typeString, parent);

				// find insert pos
				int pos = getItemCount();
				for (int i = 0; i < pos; i++) {
					TreeItem child = getItem(i);
					if (transTypeString.compareTo(child.toString()) < 0) {
						pos = i;
						break;
					}
				}

				insertItem(pos, parent);
			}
			break;
		case LAYER:
			// get type node
			int layer = forceLayer > -1 ? forceLayer:geo.getLayer();
			parent = layerNodesMap.get(layer);

			// do we have to create the parent node?
			if (parent == null) {
				String layerStr = app.getPlain("LayerA", layer + "");
				parent = new TreeItem(layerStr);

				setUserObject(parent, layerStr);

				layerNodesMap.put(layer, parent);

				// find insert pos
				int pos = getItemCount();
				for (int i = 0; i < pos; i++) {
					TreeItem child = getItem(i);
					if (layerStr.compareTo(child.toString()) < 0) {
						pos = i;
						break;
					}
				}

				insertItem(pos, parent);
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

	private static boolean compare(GeoElement geo1, GeoElement geo2, SortMode mode) {
		switch (mode) {

		case ORDER:

			return geo1.getConstructionIndex() > geo2.getConstructionIndex();

		default: // alphabetical

			return GeoElement.compareLabels(geo1.getLabel(StringTemplate.defaultTemplate),
					geo2.getLabel(StringTemplate.defaultTemplate)) > 0;

		}

	}

	/**
	 * Gets the insert position for newGeo to insert it in alphabetical order in
	 * parent node. Note: all children of parent must have instances of
	 * GeoElement as user objects.
	 * @param mode 
	 */
	final public static int getInsertPosition(TreeItem parent,
			GeoElement newGeo, SortMode mode) {
		// label of inserted geo
		//String newLabel = newGeo.getLabel();

		// standard case: binary search
		int left = 0;
		int right = parent.getChildCount();
		if (right == 0)
			return right;

		// bigger then last?
		TreeItem node = parent.getChild( parent.getChildCount() - 1 );
		//String nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
		GeoElement geo2 = ((GeoElement) node.getUserObject());
		if (compare(newGeo, geo2, mode))
			return right;

		// binary search
		while (right > left) {
			int middle = (left + right) / 2;
			node = parent.getChild(middle);
			//nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
			geo2 = ((GeoElement) node.getUserObject());

			if (!compare(newGeo, geo2, mode)) {
				right = middle;
			} else {
				left = middle + 1;
			}
		}

		// insert at correct position
		return right;
	}

	/**
	 * Performs a binary search for geo among the children of parent. All
	 * children of parent have to be instances of GeoElement sorted
	 * alphabetically by their names.
	 * 
	 * @return -1 when not found
	 */
	final public static int binarySearchGeo(TreeItem parent,
			String geoLabel) {
		int left = 0;
		int right = parent.getChildCount() - 1;
		if (right == -1 || geoLabel == null)
			return -1;

		// binary search for geo's label
		while (left <= right) {
			int middle = (left + right) / 2;
			TreeItem node = (TreeItem) parent.getChild(middle);
			String nodeLabel = ((GeoElement) node.getUserObject()).getLabelSimple();

			int compare = GeoElement.compareLabels(geoLabel, nodeLabel);
			if (compare < 0)
				right = middle - 1;
			else if (compare > 0)
				left = middle + 1;
			else
				return middle;
		}

		return -1;
	}

	/**
	 * Performs a linear search for geo among the children of parent.
	 * 
	 * @return -1 when not found
	 */
	final public static int linearSearchGeo(TreeItem parent,
			String geoLabel) {
		if(geoLabel == null)
			return -1;
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			TreeItem node = (TreeItem) parent.getChild(i);
			GeoElement g = (GeoElement) node.getUserObject();
			if (geoLabel.equals(g.getLabel(StringTemplate.defaultTemplate)))
				return i;
		}
		return -1;
	}

	/**
	 * removes a node from the tree
	 */
	public void remove(GeoElement geo) {
		cancelEditing();
		TreeItem node = nodeTable.get(geo);

		if (node != null) {
			removeFromModel(node);
		}
	}

	public void clearView() {
		nodeTable.clear();
		clearTree();
	}

	/**
	 * remove all from the tree
	 */
	protected void clearTree() {
		switch (getTreeMode()) {
		case DEPENDENCY:
			indNode.removeItems();
			depNode.removeItems();
			auxiliaryNode.removeItems();
			break;
		case TYPE:
			removeItems();
			typeNodesMap.clear();
			break;
		case LAYER:
			removeItems();
			layerNodesMap.clear();
			break;
		case ORDER:
			rootOrder.removeItems();
			removeItems();
		}
	}

	public void repaintView() {
		repaint();
	}

	/**
	 * renames an element and sorts list
	 */
	public void rename(GeoElement geo) {
		remove(geo);
		add(geo);
	}

	/**
	 * Reset the algebra view if the mode changed.
	 */
	public void setMode(int mode,ModeSetter m) {
		reset();
	}

	public void reset() {
		cancelEditing();
		repaint();
		ensureSelectedItemVisible();
	}

	/**
	 * Remove this node from the model.
	 * 
	 * @param node
	 * @param model
	 */
	private void removeFromModel(TreeItem node) {
		node.remove();
		nodeTable.remove(node.getUserObject());

		// remove the type branch if there are no more children
		switch (treeMode) {
		case TYPE:
			String typeString = ((GeoElement) node.getUserObject()).getTypeStringForAlgebraView();
			TreeItem parent = typeNodesMap.get(typeString);

			// this has been the last node
			if (parent != null && parent.getChildCount() == 0) {
				typeNodesMap.remove(typeString);
				parent.remove();
			}
			break;
		case LAYER:
			removeFromLayer(((GeoElement) node.getUserObject()).getLayer());

			break;
		case ORDER:
			rootOrder.removeItem(node);
		}
	}

	private void removeFromLayer(int i) {
		TreeItem parent = layerNodesMap.get(i);

		// this has been the last node
		if ((parent!=null) && parent.getChildCount() == 0) {
			layerNodesMap.remove(i);
			parent.remove();
		}
		
	}

	// TODO EuclidianView#setHighlighted() doesn't exist
	/**
	 * updates node of GeoElement geo (needed for highlighting)
	 * 
	 * @see EuclidianViewW#setHighlighted()
	 */
	 public void update(GeoElement geo) {
		TreeItem node = nodeTable.get(geo);

		if (node != null) {
			/* occasional exception when animating
			 * Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: 1 >= 1
			 * at java.util.Vector.elementAt(Vector.java:432)
			 * at javax.swing.tree.DefaultMutableTreeNode.getChildAt(DefaultMutableTreeNode.java:230)
			 * at javax.swing.tree.VariableHeightLayoutCache.treeNodesChanged(VariableHeightLayoutCache.java:412)
			 * at javax.swing.plaf.basic.BasicTreeUI$Handler.treeNodesChanged(BasicTreeUI.java:3669)
			 * at javax.swing.tree.DefaultTreeModel.fireTreeNodesChanged(DefaultTreeModel.java:466)
			 * at javax.swing.tree.DefaultTreeModel.nodesChanged(DefaultTreeModel.java:328)
			 * at javax.swing.tree.DefaultTreeModel.nodeChanged(DefaultTreeModel.java:261)
			 * at geogebra.gui.view.algebra.AlgebraView.update(AlgebraView.java:726)
			 * at geogebra.kernel.Kernel.notifyUpdate(Kernel.java:2082)
			 * at geogebra.kernel.GeoElement.update(GeoElement.java:3269)
			 * at geogebra.kernel.GeoPoint.update(GeoPoint.java:1169)
			 * at geogebra.kernel.GeoElement.updateCascade(GeoElement.java:3313)
			 * at geogebra.kernel.GeoElement.updateCascade(GeoElement.java:3369)
			 * at geogebra.kernel.AnimationManager.actionPerformed(AnimationManager.java:179)

			 */
			try {
				// it may be enough that clicking selects an item,
				// we want to avoid every item selected on changing algebra descriptions
				//node.setSelected(true);
				//ensureSelectedItemVisible();

				((RadioButtonTreeItem)node.getWidget()).update();
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*
			 * Cancel editing if the updated geo element has been edited, but
			 * not otherwise because editing geos while animation is running
			 * won't work then (ticket #151).
			 */
			if (isEditing()) {
				if (((RadioButtonTreeItem)node.getWidget()).isThisEdited()) {
					((RadioButtonTreeItem)node.getWidget()).cancelEditing();
				}
			}
		}
	}

	public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	final public void updateAuxiliaryObject(GeoElement geo) {
		remove(geo);
		add(geo);
	}

	/**
	 * Returns true if rendering is done with LaTeX
	 * 
	 * @return
	 */
	public boolean isRenderLaTeX() {
		return renderLaTeX;
	}



	/**
	 * inner class MyEditor handles editing of tree nodes
	 * 
	 * Created on 28. September 2001, 12:36
	 */
	/*private class MyDefaultTreeCellEditor extends DefaultTreeCellEditor
	implements CellEditorListener {

		public MyDefaultTreeCellEditor(AlgebraView tree,
				DefaultTreeCellRenderer renderer, DefaultCellEditor editor) {
			super(tree, renderer, editor);
			// editor container that expands to fill the width of the tree's enclosing panel
			editingContainer = new WideEditorContainer();
		}*/

		/*
		 * CellEditorListener implementation
		 */
		/*public void editingCanceled(ChangeEvent event) {
		}

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

			GeoElement geo = kernel.getAlgebraProcessor().changeGeoElement(
					selectedGeoElement, newValue, redefine, true);
			if (geo != null) {
				selectedGeoElement = geo;
				selectedNode.setUserObject(selectedGeoElement);
			}

			((DefaultTreeModel) getModel()).nodeChanged(selectedNode); // refresh
			// display
		}*/

		/*
		 * OVERWRITE SOME METHODS TO ONLY ALLOW EDITING OF GeoElements
		 */

		/*@Override
		public boolean isCellEditable(EventObject event) {

			if (event == null)
				return true;

			return false;
		}*/

		//
		// TreeSelectionListener
		//

		/**
		 * Resets lastPath.
		 */
		/*@Override
		public void valueChanged(TreeSelectionEvent e) {
			if (tree != null) {
				if (tree.getSelectionCount() == 1)
					lastPath = tree.getSelectionPath();
				else
					lastPath = null;*/
				/***** ADDED by Markus Hohenwarter ***********/
				/*storeSelection(lastPath);*/
				/********************************************/
			/*}
			if (timer != null) {
				timer.stop();
			}
		}*/

		/**
		 * stores currently selected GeoElement and node. selectedNode,
		 * selectedGeoElement are private members of AlgebraView
		 */
		/*private void storeSelection(TreePath tp) {
			if (tp == null)
				return;

			Object ob;
			selectedNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
			if (selectedNode != null
					&& (ob = selectedNode.getUserObject()) instanceof GeoElement) {
				selectedGeoElement = (GeoElement) ob;
			} else {
				selectedGeoElement = null;
			}
		}*/


		/**
		 * Overrides getTreeCellEditor so that a custom
		 * DefaultTreeCellEditor.EditorContainer class can be called to adjust
		 * the container width.
		 */
		/*@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row) {

			Component c = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
			((WideEditorContainer) editingContainer).updateContainer(tree,
					lastPath, offset, editingComponent);
			return c;

		}*/


		/**
		 * Extends DefaultTreeCellEditor.EditorContainer to allow full-width editor fields.
		 */
		/*class WideEditorContainer extends DefaultTreeCellEditor.EditorContainer {

			private static final long serialVersionUID = 1L;

			JTree tree;
			TreePath lastPath;
			int offset;
			Component editingComponent;*/


			/**
			 * Overrides doLayout so that the editor component width is resized
			 * to extend the full width of the tree's enclosing panel
			 */
			/*@Override
			public void doLayout() {
				if (editingComponent != null) {
					// get component preferred size
					Dimension eSize = editingComponent.getPreferredSize();

					// expand component width to extend to the enclosing container bounds
					int n = lastPath.getPathCount();
					Rectangle r = new Rectangle();
					r = tree.getParent().getBounds();
					eSize.width = r.width - (offset * n);

					// only show the symbol table icon if the editor is wide enough
					((MathTextField)editingComponent).setShowSymbolTableIcon(eSize.width > 100);

					// set the component size and location
					editingComponent.setSize(eSize);
					editingComponent.setLocation(offset, 0);
					editingComponent.setBounds(offset, 0, eSize.width, eSize.height);
					setSize(new Dimension(eSize.width + offset, eSize.height));
				}
			}*/

			/**
			 * Overrides getPreferredSize to prevent extra large heights when
			 * other tree nodes contain tall LaTeX images
			 */
			/*@Override
			public Dimension getPreferredSize(){
				Dimension d = super.getPreferredSize();
				if(editingComponent != null)
					d.height = editingComponent.getHeight();
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


	} // MyDefaultTreeCellEditor*/




	public int getViewID() {
		return App.VIEW_ALGEBRA;
	}

	public AppW getApplication() {
		return app;
	}

	public int[] getGridColwidths() {
		return new int[] { getElement().getOffsetWidth() };
	}

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
		int[] heights = new int[getItemCount()];
		for (int i = 0; i < heights.length; i++) {
			heights[i] = getItem(i).getElement().getOffsetHeight();
		}
		heights[0] += 2;
		return heights;
	}

	/*public Component[][] getPrintComponents() {
		return null;
		//return new Component[][] { { this } };
	}*/

	public void changeLayer(GeoElement g, int oldLayer, int newLayer) {
		if(this.treeMode.equals(SortMode.LAYER)){
			TreeItem node = nodeTable
					.get(g);

			if (node != null) {
				node.remove();
				nodeTable.remove(node.getUserObject());
				removeFromLayer(oldLayer);
			}

			this.add(g,newLayer);
			
		}
	}

	private StringBuilder sbXML;
	
	private void updateCollapsedNodesIndices(){
		


		//no collapsed nodes
		if (getTreeMode()==SortMode.ORDER){
			collapsedNodes = null;
			return;
		}
		
		
		
		if (collapsedNodes==null)
			collapsedNodes = new ArrayList<Integer>();
		else
			collapsedNodes.clear();


		for (int i=0; i<getItemCount(); i++){
			TreeItem node = getItem(i);
			if (!node.getState())
				collapsedNodes.add(i);
		}

	}
	
	/**
	 * returns settings in XML format
	 */
	public void getXML(StringBuilder sb, boolean asPreference) {
	

		if (sbXML==null)
			sbXML = new StringBuilder();
		else
			sbXML.setLength(0);
		
		//tree mode
		if (getTreeMode()!=SortMode.TYPE){
			sbXML.append("\t<mode ");
			sbXML.append("val=\"");
			sbXML.append(getTreeModeValue());
			sbXML.append("\"");
			sbXML.append("/>\n");
		}

		
		//auxiliary objects
		boolean flag = showAuxiliaryObjects();
		if (flag){
			sbXML.append("\t<auxiliary ");
			sbXML.append("show=\"");
			sbXML.append(flag);
			sbXML.append("\"");
			sbXML.append("/>\n");
		}
		
		//collapsed nodes
		updateCollapsedNodesIndices();
		if (collapsedNodes!=null && collapsedNodes.size()>0){
			sbXML.append("\t<collapsed ");
			sbXML.append("val=\"");
			sbXML.append(collapsedNodes.get(0));
			for (int i=1; i<collapsedNodes.size();i++){
				sbXML.append(",");
				sbXML.append(collapsedNodes.get(i));
			}
			sbXML.append("\"");
			sbXML.append("/>\n");
		}

		if (sbXML.length()>0){
			sb.append("<algebraView>\n");
			sb.append(sbXML);
			sb.append("</algebraView>\n");
		}


	}


	// temporary proxies for the temporary implementation of AlgebraController in common
	public GeoElement getGeoElementForPath(Object tp) {
		//return getGeoElementForPath((TreePath)tp);
		return null;
	}

	public GeoElement getGeoElementForLocation(Object tree, int x, int y) {
		//return getGeoElementForLocation((JTree)tree, x, y);
		return null;
	}

	public Object getPathBounds(Object tp) {
		//return getPathBounds((TreePath)tp);
		return null;
	}
	// temporary proxies end

	public void cancelEditing() {
		editing = false;
		setAnimationEnabled(true);
	}

	public boolean isEditing() {
		return editing;
	}

	protected boolean isKeyboardNavigationEnabled(TreeItem ti) {
		//keys should move the geos in the EV
		//if (isEditing())
			return false;
		//return super.isKeyboardNavigationEnabled(ti);
	}

	public void setUserObject(TreeItem ti, Object ob) {
		ti.setUserObject(ob);
		if (ob instanceof GeoElement) {
			ti.setWidget(new RadioButtonTreeItem((GeoElement)ob));

			// Workaround to make treeitem visual selection available
			DOM.setStyleAttribute(
				(com.google.gwt.user.client.Element)
				ti.getElement().getFirstChildElement(), "display", "-moz-inline-box");
			DOM.setStyleAttribute(
				(com.google.gwt.user.client.Element)
				ti.getElement().getFirstChildElement(), "display", "inline-block");
		} else {
			ti.setWidget(new InlineLabelTreeItem(app, ti, ob.toString()));
		}
	}

	public GeoElement lastSelectedGeo = null;

	@Override
	public void onBrowserEvent(Event event) {
		// as arrow keys are prevented in super.onBrowserEvent,
		// we need to handle arrow key events before that
		switch (DOM.eventGetType(event)) {
			case Event.ONKEYUP:
				switch (DOM.eventGetKeyCode(event)) {
					case KeyCodes.KEY_UP:
					case KeyCodes.KEY_DOWN:
					case KeyCodes.KEY_LEFT:
					case KeyCodes.KEY_RIGHT:
					// this may be enough for Safari too, because it is not onkeypress
						app.getGlobalKeyDispatcher().handleSelectedGeosKeysNative(event);
						event.stopPropagation();
						event.preventDefault();
						return;
				}
		}
		if (!editing)
			super.onBrowserEvent(event);
	}

	public boolean hasFocus() {
	    App.debug("unimplemented");
	    return false;
    }

	protected void onLoad() {
		// this may be important if the view is added/removed from the DOM
		super.onLoad();
		repaint();
	}

	public void repaint() {

		// no need to repaint that which is not showing
		// (but take care of repainting if it appears!)
		if (!isShowing())
			return;

		app.getGuiManager().getTimerSystem().viewRepaint(this);
    }

	public boolean isShowing() {
		return isVisible() && isAttached();
    }

	/**
	 * Only call this method if you really know what you're doing.
	 * Otherwise call repaint() instead.
	 */
	public void doRepaint() {

		app.getGuiManager().getTimerSystem().viewRepainting(this);

		Object geo;
		// suppose that the add operations have been already done elsewhere
		for (int i = 0; i < getItemCount(); i++) {
			geo = getItem(i).getUserObject();
			if (geo instanceof GeoElement) {
				((RadioButtonTreeItem)getItem(i).getWidget()).update();
				getItem(i).setSelected(
					((GeoElement)geo).doHighlighting());
			} else {
				((InlineLabelTreeItem)getItem(i).getWidget()).setText(
					getItem(i).getUserObject().toString());
				for (int j = 0; j < getItem(i).getChildCount(); j++) {
					geo = getItem(i).getChild(j).getUserObject();
					if (geo instanceof GeoElement)
						getItem(i).getChild(j).setSelected(
							((GeoElement)geo).doHighlighting());
				}
			}
		}

		app.getGuiManager().getTimerSystem().viewRepainted(this);
	}

	/**
	 * @return int value for tree mode (used in XML)
	 */
	public int getTreeModeValue(){
		switch (getTreeMode()) {
		case DEPENDENCY:
			return 0;
		case TYPE:
		default:
			return 1;
		case LAYER:
			return 2;
		case ORDER:
			return 3;
		}
	}

	public void setTreeMode(int mode) {
		switch(mode){
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

	private ArrayList<Integer> collapsedNodes;

	private void setCollapsedNodes(int[] collapsedNodes){
		if (collapsedNodes == null)
			return;

		if (this.collapsedNodes==null)
			this.collapsedNodes = new ArrayList<Integer>();
		else
			this.collapsedNodes.clear();

		for (int i=0; i<collapsedNodes.length; i++)
			this.collapsedNodes.add(collapsedNodes[i]);
	}


	/**
	 * apply the settings
	 */
	public void applySettings(){
		
		if (!settingsChanged){
			//that means that no settings were stored in the file: reset settings to have default
			AlgebraSettings settings = app.getSettings().getAlgebra();
			settings.reset();
			settingsChanged(settings);
		}
		

		settingsChanged = false;
		
		//auxilliary objects
		setShowAuxiliaryObjects(showAuxiliaryObjectsSettings);
		
		attachView(); //TODO implement applyPerspective correctly
		
		//collapsed nodes
		if (collapsedNodes == null)
			return;


		for (int i : collapsedNodes){
			TreeItem node = getItem(i);
			node.setState(false);
		}
		
		
	}


	private boolean showAuxiliaryObjectsSettings = false;
	
	private boolean settingsChanged = false;

	public void settingsChanged(AbstractSettings settings) {
	
		AlgebraSettings algebraSettings = (AlgebraSettings) settings;
		setTreeMode(algebraSettings.getTreeMode());
		showAuxiliaryObjectsSettings = algebraSettings.getShowAuxiliaryObjects();
		setCollapsedNodes(algebraSettings.getCollapsedNodes());
		
		settingsChanged = true;
				
	}
} // AlgebraView
