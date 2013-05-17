package geogebra.web.gui.view.algebra;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.LayerView;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.AlgebraSettings;
import geogebra.html5.main.AppWeb;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.images.AppResources;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public abstract class AlgebraViewWeb extends Tree implements LayerView,
        SetLabels, AlgebraView, OpenHandler<TreeItem> {

	protected final AppWeb app; // parent appame
	protected final Localization loc;
	protected final Kernel kernel;
	private AnimationScheduler repaintScheduler = AnimationScheduler.get();
	
	private AnimationScheduler.AnimationCallback repaintCallback = new AnimationScheduler.AnimationCallback() {
		public void execute(double ts) {
			doRepaint2();
		}
	};

	/**
	 * The mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	protected SortMode treeMode = SortMode.TYPE;

	private boolean showAuxiliaryObjectsSettings = false;

	private boolean settingsChanged = false;

	/**
	 * Nodes for tree mode MODE_DEPENDENCY
	 */
	private TreeItem depNode, indNode;
	private TreeItem auxiliaryNode;

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
	
	private GeoElement lastSelectedGeo = null;

	public AlgebraViewWeb(AppWeb app) {
		this.app = app;
		this.loc = app.getLocalization();
		this.kernel = app.getKernel();
		this.addOpenHandler(this);
	}

	protected HashMap<GeoElement, TreeItem> nodeTable = new HashMap<GeoElement, TreeItem>(
	        500);

	public final void repaint() {

		// no need to repaint that which is not showing
		// (but take care of repainting if it appears!)
		if (!isShowing())
			return;

		app.getTimerSystem().viewRepaint(this);
	}
	
	public void doRepaint() {
		repaintScheduler.requestAnimationFrame(repaintCallback);
}

	public final void repaintView() {
		repaint();
	}
	
	/**
	 * Make sure we repaint all updated objects in nodes that were collapsed before
	 */
	public void onOpen(OpenEvent<TreeItem> event){
		this.doRepaint();
	}

	public final int getViewID() {
		return App.VIEW_ALGEBRA;
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
			/*
			 * occasional exception when animating Exception in thread
			 * "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: 1 >=
			 * 1 at java.util.Vector.elementAt(Vector.java:432) at
			 * javax.swing.tree
			 * .DefaultMutableTreeNode.getChildAt(DefaultMutableTreeNode
			 * .java:230) at
			 * javax.swing.tree.VariableHeightLayoutCache.treeNodesChanged
			 * (VariableHeightLayoutCache.java:412) at
			 * javax.swing.plaf.basic.BasicTreeUI$Handler
			 * .treeNodesChanged(BasicTreeUI.java:3669) at
			 * javax.swing.tree.DefaultTreeModel
			 * .fireTreeNodesChanged(DefaultTreeModel.java:466) at
			 * javax.swing.tree
			 * .DefaultTreeModel.nodesChanged(DefaultTreeModel.java:328) at
			 * javax
			 * .swing.tree.DefaultTreeModel.nodeChanged(DefaultTreeModel.java
			 * :261) at
			 * geogebra.gui.view.algebra.AlgebraView.update(AlgebraView.
			 * java:726) at
			 * geogebra.kernel.Kernel.notifyUpdate(Kernel.java:2082) at
			 * geogebra.kernel.GeoElement.update(GeoElement.java:3269) at
			 * geogebra.kernel.GeoPoint.update(GeoPoint.java:1169) at
			 * geogebra.kernel.GeoElement.updateCascade(GeoElement.java:3313) at
			 * geogebra.kernel.GeoElement.updateCascade(GeoElement.java:3369) at
			 * geogebra
			 * .kernel.AnimationManager.actionPerformed(AnimationManager.
			 * java:179)
			 */
			try {
				// it may be enough that clicking selects an item,
				// we want to avoid every item selected on changing algebra
				// descriptions
				// node.setSelected(true);
				// ensureSelectedItemVisible();

				((RadioButtonTreeItem) node.getWidget()).updateOnNextRepaint();
				repaint();
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*
			 * Cancel editing if the updated geo element has been edited, but
			 * not otherwise because editing geos while animation is running
			 * won't work then (ticket #151).
			 */
			if (isEditing()) {
				if (((RadioButtonTreeItem) node.getWidget()).isThisEdited()) {
					((RadioButtonTreeItem) node.getWidget()).cancelEditing();
				}
			}
		}
	}

	/**
	 * Only call this method if you really know what you're doing. Otherwise
	 * call repaint() instead.
	 */
	public void doRepaint2() {
		app.getTimerSystem().viewRepainting(this);
		Object geo;
		// suppose that the add operations have been already done elsewhere
		for (int i = 0; i < getItemCount(); i++) {
			geo = getItem(i).getUserObject();
			if (geo instanceof GeoElement) {
				((RadioButtonTreeItem) getItem(i).getWidget()).repaint();
				getItem(i).setSelected(((GeoElement) geo).doHighlighting());
			} else {
				((InlineLabelTreeItem) getItem(i).getWidget()).setText(getItem(
				        i).getUserObject().toString());
				if (getItem(i).getState()) {
					repaintChildren(getItem(i));
				}

			}
		}
		app.getTimerSystem().viewRepainted(this);
	}

	private void repaintChildren(TreeItem item) {
		for (int j = 0; j < item.getChildCount(); j++) {
			if (item.getChild(j).getWidget() instanceof RadioButtonTreeItem) {
				((RadioButtonTreeItem) item.getChild(j).getWidget()).repaint();
			}
			Object geo = item.getChild(j).getUserObject();
			if (geo instanceof GeoElement)
				item.getChild(j).setSelected(
				        ((GeoElement) geo).doHighlighting());
		}
	}

	private StringBuilder sbXML;

	private void updateCollapsedNodesIndices() {

		// no collapsed nodes
		if (getTreeMode() == SortMode.ORDER) {
			collapsedNodes = null;
			return;
		}

		if (collapsedNodes == null)
			collapsedNodes = new ArrayList<Integer>();
		else
			collapsedNodes.clear();

		for (int i = 0; i < getItemCount(); i++) {
			TreeItem node = getItem(i);
			if (!node.getState())
				collapsedNodes.add(i);
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
	 * @return int value for tree mode (used in XML)
	 */
	public int getTreeModeValue() {
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
		switch (mode) {
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
	 * returns settings in XML format
	 */
	public void getXML(StringBuilder sb, boolean asPreference) {

		if (sbXML == null)
			sbXML = new StringBuilder();
		else
			sbXML.setLength(0);

		// tree mode
		if (getTreeMode() != SortMode.TYPE) {
			sbXML.append("\t<mode ");
			sbXML.append("val=\"");
			sbXML.append(getTreeModeValue());
			sbXML.append("\"");
			sbXML.append("/>\n");
		}

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
		if (collapsedNodes == null)
			return;

		if (this.collapsedNodes == null)
			this.collapsedNodes = new ArrayList<Integer>();
		else
			this.collapsedNodes.clear();

		for (int i = 0; i < collapsedNodes.length; i++)
			this.collapsedNodes.add(collapsedNodes[i]);
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

		attachView(); // TODO implement applyPerspective correctly

		// collapsed nodes
		if (collapsedNodes == null)
			return;

		for (int i : collapsedNodes) {
			TreeItem node = getItem(i);
			node.setState(false);
		}

	}

	public void settingsChanged(AbstractSettings settings) {

		AlgebraSettings algebraSettings = (AlgebraSettings) settings;
		setTreeMode(algebraSettings.getTreeMode());
		showAuxiliaryObjectsSettings = algebraSettings
		        .getShowAuxiliaryObjects();
		setCollapsedNodes(algebraSettings.getCollapsedNodes());

		settingsChanged = true;

	}

	boolean attached = false;

	public void attachView() {

		if (attached)
			return;
		attached = true;
		clearView();
		kernel.notifyAddAll(this);
		applySettings();
		kernel.attach(this);

		/*
		 * if (treeMode == SortMode.DEPENDENCY) { indNode.setState(true);
		 * depNode.setState(true); if (auxiliaryNode.getParentItem() != null) {
		 * auxiliaryNode.setState(true); } }
		 */
	}

	public void detachView() {
		kernel.detach(this);
		clearView();
		attached = false;
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
				// rootDependency = new TreeItem();
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
			// addItem(rootOrder);
			break;

		case TYPE:
			// don't re-init anything
			if (rootType == null) {
				rootType = new TreeItem();
				// setUserObject(rootType, "");
				typeNodesMap = new HashMap<String, TreeItem>(5);
			}

			// always try to remove the auxiliary node
			if (app.showAuxiliaryObjects && auxiliaryNode != null) {
				removeAuxiliaryNode();
			}

			// set the root
			clear();
			// addItem(rootType);
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
			// addItem(rootLayer);
			break;
		}

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

	/**
	 * set labels on the tree
	 */
	protected void setTreeLabels() {
		TreeItem node;
		switch (getTreeMode()) {
		case DEPENDENCY:
			setUserObject(indNode, loc.getPlain("FreeObjects"));
			setUserObject(depNode, loc.getPlain("DependentObjects"));
			setUserObject(auxiliaryNode, loc.getPlain("AuxiliaryObjects"));
			break;
		case TYPE:
			for (String key : typeNodesMap.keySet()) {
				node = typeNodesMap.get(key);
				setUserObject(node, loc.getPlain(key));
			}
			break;
		case LAYER:
			for (Integer key : layerNodesMap.keySet()) {
				node = layerNodesMap.get(key);
				setUserObject(node, loc.getPlain("LayerA", key.toString())
				        + "TODO" + key);
			}
			break;
		case ORDER:
			break;
		}
	}

	/**
	 * 
	 * @param geo
	 * @return parent node of this geo
	 */
	protected TreeItem getParentNode(GeoElement geo, int forceLayer) {
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
				String transTypeString = geo
				        .translatedTypeStringForAlgebraView();
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
			int layer = forceLayer > -1 ? forceLayer : geo.getLayer();
			parent = layerNodesMap.get(layer);

			// do we have to create the parent node?
			if (parent == null) {
				String layerStr = loc.getPlain("LayerA", layer + "");
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

	public void setUserObject(TreeItem ti, Object ob) {
		ti.setUserObject(ob);
		if (ob instanceof GeoElement) {
			ti.setWidget(new RadioButtonTreeItem((GeoElement) ob,
			        AppResources.INSTANCE.shown().getSafeUri(),
			        AppResources.INSTANCE.hidden().getSafeUri()));
			ti.getElement().getStyle().setPadding(0, Unit.PX);

			// Workaround to make treeitem visual selection available
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti
			        .getElement().getFirstChildElement(), "display",
			        "-moz-inline-box");
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti
			        .getElement().getFirstChildElement(), "display",
			        "inline-block");
		} else {
			ti.setWidget(new InlineLabelTreeItem(app.getSelectionManager(), ti,
			        ob.toString()));
		}
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
			String typeString = ((GeoElement) node.getUserObject())
			        .getTypeStringForAlgebraView();
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
		if ((parent != null) && parent.getChildCount() == 0) {
			layerNodesMap.remove(i);
			parent.remove();
		}

	}

	/**
	 * adds a new node to the tree
	 */
	public void add(GeoElement geo) {
		add(geo, -1);
	}

	private void add(GeoElement geo, int forceLayer) {
		cancelEditing();

		if (geo.isLabelSet() && geo.showInAlgebraView()
		        && geo.isSetAlgebraVisible()) {
			// don't add auxiliary objects if the tree is categorized by type
			if (!getTreeMode().equals(SortMode.DEPENDENCY)
			        && !showAuxiliaryObjects() && geo.isAuxiliaryObject()) {
				return;
			}

			TreeItem parent, node;
			node = new TreeItem();

			parent = getParentNode(geo, forceLayer);

			// add node to model (alphabetically ordered)
			int pos = getInsertPosition(parent, geo, treeMode);

			if (pos == parent.getChildCount()) {
				parent.addItem(node);
				if (parent.equals(rootOrder))
					addItem(node);
			} else
				try {
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

	public void changeLayer(GeoElement g, int oldLayer, int newLayer) {
		if (this.treeMode.equals(SortMode.LAYER)) {
			TreeItem node = nodeTable.get(g);

			if (node != null) {
				node.remove();
				nodeTable.remove(node.getUserObject());
				removeFromLayer(oldLayer);
			}

			this.add(g, newLayer);

		}
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
	 * renames an element and sorts list
	 */
	public void rename(GeoElement geo) {
		remove(geo);
		add(geo);
	}

	protected void removeAuxiliaryNode() {
		removeItem(auxiliaryNode);
	}

	/**
	 * Gets the insert position for newGeo to insert it in alphabetical order in
	 * parent node. Note: all children of parent must have instances of
	 * GeoElement as user objects.
	 * 
	 * @param mode
	 */
	final public static int getInsertPosition(TreeItem parent,
	        GeoElement newGeo, SortMode mode) {
		// label of inserted geo
		// String newLabel = newGeo.getLabel();

		// standard case: binary search
		int left = 0;
		int right = parent.getChildCount();
		if (right == 0)
			return right;

		// bigger then last?
		TreeItem node = parent.getChild(parent.getChildCount() - 1);
		// String nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
		GeoElement geo2 = ((GeoElement) node.getUserObject());
		if (compare(newGeo, geo2, mode))
			return right;

		// binary search
		while (right > left) {
			int middle = (left + right) / 2;
			node = parent.getChild(middle);
			// nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
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
	final public static int binarySearchGeo(TreeItem parent, String geoLabel) {
		int left = 0;
		int right = parent.getChildCount() - 1;
		if (right == -1 || geoLabel == null)
			return -1;

		// binary search for geo's label
		while (left <= right) {
			int middle = (left + right) / 2;
			TreeItem node = parent.getChild(middle);
			String nodeLabel = ((GeoElement) node.getUserObject())
			        .getLabelSimple();

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
	final public static int linearSearchGeo(TreeItem parent, String geoLabel) {
		if (geoLabel == null)
			return -1;
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			TreeItem node = parent.getChild(i);
			GeoElement g = (GeoElement) node.getUserObject();
			if (geoLabel.equals(g.getLabel(StringTemplate.defaultTemplate)))
				return i;
		}
		return -1;
	}

	/**
	 * Reset the algebra view if the mode changed.
	 */
	public void setMode(int mode, ModeSetter m) {
		reset();
	}

	private static boolean compare(GeoElement geo1, GeoElement geo2,
	        SortMode mode) {
		switch (mode) {

		case ORDER:

			return geo1.getConstructionIndex() > geo2.getConstructionIndex();

		default: // alphabetical

			return GeoElement.compareLabels(
			        geo1.getLabel(StringTemplate.defaultTemplate),
			        geo2.getLabel(StringTemplate.defaultTemplate)) > 0;

		}

	}

	public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	final public void updateAuxiliaryObject(GeoElement geo) {
		remove(geo);
		add(geo);
	}

	public void reset() {
		cancelEditing();
		repaint();
		ensureSelectedItemVisible();
	}

	/**
	 * resets all fix labels of the View. This method is called by the
	 * application if the language setting is changed.
	 */
	public void setLabels() {

		setTreeLabels();

		/*
		 * if (helperBar != null) { helperBar.updateLabels(); }
		 */
	}
	
	public final GeoElement getLastSelectedGeo() {
	    return lastSelectedGeo;
    }

	public final void setLastSelectedGeo(GeoElement geo) {
	    lastSelectedGeo = geo;	    
    }

}
