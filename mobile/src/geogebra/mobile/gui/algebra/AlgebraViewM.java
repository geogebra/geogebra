package geogebra.mobile.gui.algebra;

import geogebra.common.awt.GFont;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.algebra.AlgebraController;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.LayerView;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.web.euclidian.EuclidianViewW;

import java.util.HashMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * AlgebraView with tree for free and dependent objects.
 * 
 * Taken from the web-project.
 * 
 */
public class AlgebraViewM extends Tree implements LayerView, SetLabels,
		AlgebraView
{

	protected App app; // parent appame
	private Kernel kernel;

	// store all pairs of GeoElement -> node in the Tree
	private HashMap<GeoElement, TreeItem> nodeTable = new HashMap<GeoElement, TreeItem>(
			500);

	/**
	 * Nodes for tree mode MODE_DEPENDENCY.
	 */
	private TreeItem depNode, indNode;

	protected TreeItem auxiliaryNode;

	/**
	 * Root node for tree mode MODE_TYPE.
	 */
	private TreeItem rootType;

	/**
	 * Nodes for tree mode MODE_TYPE.
	 */
	private HashMap<String, TreeItem> typeNodesMap;

	/* for SortMode.ORDER */
	private TreeItem rootOrder;

	/* for SortMode.LAYER */
	private TreeItem rootLayer;
	private HashMap<Integer, TreeItem> layerNodesMap;

	/**
	 * The mode of the tree, see MODE_DEPENDENCY, MODE_TYPE.
	 */
	protected SortMode treeMode;

	private GeoElement selectedGeoElement;
	// private TreeItem selectedNode;

	// private AlgebraHelperBar helperBar;

	private AlgebraController algebraController;

	public AlgebraController getAlgebraController()
	{
		return this.algebraController;
	}

	// /**
	// * Flag for LaTeX rendering. 
	// */
	// final private static boolean renderLaTeX = true;

	/**
	 * Creates new AlgebraView.
	 * 
	 * @param algCtrl
	 *            : AlgebraController
	 * 
	 */
	public AlgebraViewM(AlgebraController algCtrl)
	{
		super();

		App.debug("creating Algebra View");

		this.app = algCtrl.getApplication();
		this.kernel = algCtrl.getKernel();
		algCtrl.setView(this);
		this.algebraController = algCtrl;
		// this is the default value
		this.treeMode = SortMode.TYPE;

		// initializes the tree model
		initModel();

		setLabels();

		getElement().setId("View_" + App.VIEW_ALGEBRA);

		// TODO
		// this.addKeyDownHandler(this.app.getGlobalKeyDispatcher());
		// this.addKeyUpHandler(this.app.getGlobalKeyDispatcher());
		// this.addKeyPressHandler(this.app.getGlobalKeyDispatcher());

	}

	/**
	 * Method to initialize the tree model of the current tree mode. This method
	 * should be called whenever the tree mode is changed, it won't initialize
	 * anything if not necessary.
	 * 
	 * This method will also actually change the model of the tree.
	 */
	protected void initModel()
	{
		// build default tree structure
		switch (this.treeMode)
		{
		case DEPENDENCY:
			// don't re-init anything
			if (this.depNode == null || this.indNode == null
					|| this.auxiliaryNode == null)
			{
				// rootDependency = new TreeItem();
				this.depNode = new TreeItem(); // dependent objects
				this.indNode = new TreeItem();
				this.auxiliaryNode = new TreeItem();

			}

			// set the root
			clear();
			addItem(this.indNode);
			addItem(this.depNode);

			// add auxiliary node if neccessary
			if (this.app.showAuxiliaryObjects)
			{
				if (this.auxiliaryNode.getTree() != this)
				{
					addItem(this.auxiliaryNode);
				}
			}
			break;
		case ORDER:
			if (this.rootOrder == null)
			{
				// both rootOrder and AlgebraView will have the Tree items
				this.rootOrder = new TreeItem();
			}
			setUserObject(this.rootOrder, "");

			// always try to remove the auxiliary node
			if (this.app.showAuxiliaryObjects && this.auxiliaryNode != null)
			{
				removeAuxiliaryNode();
			}

			// set the root
			clear();
			// addItem(rootOrder);
			break;

		case TYPE:
			// don't re-init anything
			if (this.rootType == null)
			{
				this.rootType = new TreeItem();
				// setUserObject(rootType, "");
				this.typeNodesMap = new HashMap<String, TreeItem>(5);
			}

			// always try to remove the auxiliary node
			if (this.app.showAuxiliaryObjects && this.auxiliaryNode != null)
			{
				removeAuxiliaryNode();
			}

			// set the root
			clear();
			// addItem(rootType);
			break;
		case LAYER:
			// don't re-init anything
			if (this.rootLayer == null)
			{
				this.rootLayer = new TreeItem();
				this.layerNodesMap = new HashMap<Integer, TreeItem>(10);
			}

			// always try to remove the auxiliary node
			if (this.app.showAuxiliaryObjects && this.auxiliaryNode != null)
			{
				removeAuxiliaryNode();
			}

			// set the root
			clear();
			// addItem(rootLayer);
			break;
		default:
			break;
		}

	}

	protected void removeAuxiliaryNode()
	{
		removeItem(this.auxiliaryNode);
	}

	boolean attached = false;

	public void attachView()
	{
		clearView();
		this.kernel.notifyAddAll(this);
		this.kernel.attach(this);
		this.attached = true;
	}

	public void detachView()
	{
		this.kernel.detach(this);
		clearView();
		this.attached = false;
	}

	public void updateFonts()
	{
		GFont font = this.app.getPlainFontCommon();
		getStyleElement().getStyle().setFontStyle(
				Style.FontStyle.valueOf(font.isItalic() ? "ITALIC" : "NORMAL"));
		getStyleElement().getStyle().setFontSize(font.getSize(), Style.Unit.PX);
		getStyleElement().getStyle().setFontWeight(
				Style.FontWeight.valueOf(font.isBold() ? "BOLD" : "NORMAL"));
	}

	public void clearSelection()
	{

		// deselecting this causes a bug; it maybe fixed
		// by changing the repaintView method too,
		// adding setSelectedItem( some TreeItem ),
		// but which TreeItem should be that if more are selected?
		// that's why Arpad choosed to comment this out instead
		// super.setSelectedItem(null);

		for (int i = 0; i < getItemCount(); i++)
		{
			if (!(getItem(i).getUserObject() instanceof GeoElement))
				for (int j = 0; j < getItem(i).getChildCount(); j++)
				{
					getItem(i).getChild(j).setSelected(false);
				}
		}
		this.selectedGeoElement = null;
	}

	public GeoElement getSelectedGeoElement()
	{
		return this.selectedGeoElement;
	}

	public boolean showAuxiliaryObjects()
	{
		return this.app.showAuxiliaryObjects;
	}

	public void setShowAuxiliaryObjects(boolean flag)
	{

		this.app.showAuxiliaryObjects = flag;

		cancelEditing();

		if (flag)
		{
			clearView();

			switch (getTreeMode())
			{
			case DEPENDENCY:
				addItem(this.auxiliaryNode);
				break;
			default:
				break;
			}

			this.kernel.notifyAddAll(this);
		} else
		{
			// if we're listing the auxiliary objects in a single leaf we can
			// just remove that leaf, but for type-based categorization those
			// auxiliary nodes might be scattered across the whole tree,
			// therefore we just rebuild the tree
			switch (getTreeMode())
			{
			case DEPENDENCY:
				if (this.auxiliaryNode.getParentItem() != null)
				{
					removeItem(this.auxiliaryNode);
				}
				break;
			default:

				clearView();
				this.kernel.notifyAddAll(this);
			}
		}
	}

	/**
	 * @return The display mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	public SortMode getTreeMode()
	{
		return this.treeMode;
	}

	/**
	 * @param value
	 *            Either AlgebraView.MODE_DEPDENCY or AlgebraView.MODE_TYPE
	 */
	public void setTreeMode(SortMode value)
	{
		if (getTreeMode().equals(value))
		{
			return;
		}

		clearView();

		this.treeMode = value;
		initModel();

		this.kernel.notifyAddAll(this);
		setLabels();
	}

	@Override
	public Object getPathForLocation(int x, int y)
	{
		return null;
	}

	public boolean editing = false;

	/**
	 * Open Editor textfield for geo.
	 */
	@Override
	public void startEditing(GeoElement geo, boolean shiftDown)
	{
		if (geo == null)
			return;

		// open Object Properties for eg GeoImages
		if (!geo.isAlgebraViewEditable())
		{
			// FIXMEWEB ArrayList<GeoElement> geos = new
			// ArrayList<GeoElement>();
			// FIXMEWEB geos.add(geo);
			// FIXMEWEB app.getDialogManager().showPropertiesDialog(geos);
			return;
		}

		if (!shiftDown || !geo.isPointOnPath() && !geo.isPointInRegion())
		{
			if (!geo.isIndependent() || !this.attached) // needed for F2 when
														// Algebra
			// View closed
			{
				if (geo.isRedefineable())
				{
					this.app.getDialogManager().showRedefineDialog(geo, true);
				}
				return;
			}

			if (!geo.isChangeable())
			{
				if (geo.isFixed())
				{
					// TODO
					// app.showMessage(app.getError("AssignmentToFixed"));
				} else if (geo.isRedefineable())
				{
					this.app.getDialogManager().showRedefineDialog(geo, true);
				}
				return;
			}
		}

		TreeItem node = this.nodeTable.get(geo);

		if (node != null)
		{
			cancelEditing();
			// FIXMEWEB select and show node
			Widget wi = node.getWidget();
			this.editing = true;
			setAnimationEnabled(false);
			if (wi instanceof RadioButtonTreeItem)
				((RadioButtonTreeItem) wi).startEditing();
		}
	}

	/**
	 * resets all fix labels of the View. This method is called by the
	 * application if the language setting is changed.
	 */
	@Override
	public void setLabels()
	{

		setTreeLabels();

		/*
		 * if (helperBar != null) { helperBar.updateLabels(); }
		 */
	}

	/**
	 * set labels on the tree
	 */
	protected void setTreeLabels()
	{
		TreeItem node;
		switch (getTreeMode())
		{
		case DEPENDENCY:
			setUserObject(this.indNode, this.app.getPlain("FreeObjects"));
			setUserObject(this.depNode, this.app.getPlain("DependentObjects"));
			setUserObject(this.auxiliaryNode,
					this.app.getPlain("AuxiliaryObjects"));
			break;
		case TYPE:
			for (String key : this.typeNodesMap.keySet())
			{
				node = this.typeNodesMap.get(key);
				setUserObject(node, this.app.getPlain(key));
			}
			break;
		case LAYER:
			for (Integer key : this.layerNodesMap.keySet())
			{
				node = this.layerNodesMap.get(key);
				setUserObject(node, this.app.getPlain("LayerA", key.toString())
						+ "TODO" + key);
			}
			break;
		case ORDER:
			break;
		default:
			break;
		}
	}

	/**
	 * adds a new node to the tree
	 */
	@Override
	public void add(GeoElement geo)
	{
		add(geo, -1);
	}

	private void add(GeoElement geo, int forceLayer)
	{
		cancelEditing();

		if (geo.isLabelSet() && geo.showInAlgebraView()
				&& geo.isSetAlgebraVisible())
		{
			// don't add auxiliary objects if the tree is categorized by type
			if (!getTreeMode().equals(SortMode.DEPENDENCY)
					&& !showAuxiliaryObjects() && geo.isAuxiliaryObject())
			{
				return;
			}

			TreeItem parent, node;
			node = new TreeItem();

			parent = getParentNode(geo, forceLayer);

			// add node to model (alphabetically ordered)
			int pos = getInsertPosition(parent, geo, this.treeMode);

			if (pos == parent.getChildCount())
			{
				parent.addItem(node);
				if (parent.equals(this.rootOrder))
					addItem(node);
			} else
				try
				{
					parent.insertItem(pos, node);
					if (parent.equals(this.rootOrder))
						insertItem(pos, node);
				} catch (IndexOutOfBoundsException e)
				{
					parent.addItem(node);
					if (parent.equals(this.rootOrder))
						addItem(node);
				}

			setUserObject(node, geo);
			this.nodeTable.put(geo, node);

			// ensure that the leaf with the new object is visible
			parent.setState(true);
		}
	}

	/**
	 * 
	 * @param geo
	 * @return parent node of this geo
	 */
	protected TreeItem getParentNode(GeoElement geo, int forceLayer)
	{
		TreeItem parent;

		switch (this.treeMode)
		{
		case DEPENDENCY:
			if (geo.isAuxiliaryObject())
			{
				parent = this.auxiliaryNode;
			} else if (geo.isIndependent())
			{
				parent = this.indNode;
			} else
			{
				parent = this.depNode;
			}
			break;
		case TYPE:
			// get type node
			String typeString = geo.getTypeStringForAlgebraView();
			parent = this.typeNodesMap.get(typeString);

			// do we have to create the parent node?
			if (parent == null)
			{
				String transTypeString = geo
						.translatedTypeStringForAlgebraView();
				parent = new TreeItem(transTypeString);
				setUserObject(parent, transTypeString);
				this.typeNodesMap.put(typeString, parent);

				// find insert pos
				int pos = getItemCount();
				for (int i = 0; i < pos; i++)
				{
					// TODO
					// TreeItem child = getItem(i);
					// if (transTypeString.compareTo(child.toString()) < 0) {
					// pos = i;
					// break;
					// }
				}

				insertItem(pos, parent);
			}
			break;
		case LAYER:
			// get type node
			int layer = forceLayer > -1 ? forceLayer : geo.getLayer();
			parent = this.layerNodesMap.get(layer);

			// do we have to create the parent node?
			if (parent == null)
			{
				String layerStr = this.app.getPlain("LayerA", layer + "");
				parent = new TreeItem(layerStr);

				setUserObject(parent, layerStr);

				this.layerNodesMap.put(layer, parent);

				// find insert pos
				int pos = getItemCount();
				for (int i = 0; i < pos; i++)
				{
					TreeItem child = getItem(i);
					if (layerStr.compareTo(child.toString()) < 0)
					{
						pos = i;
						break;
					}
				}

				insertItem(pos, parent);
			}
			break;
		case ORDER:
			parent = this.rootOrder;

			break;
		default:
			parent = null;
		}

		return parent;
	}

	private static boolean compare(GeoElement geo1, GeoElement geo2,
			SortMode mode)
	{
		switch (mode)
		{

		case ORDER:

			return geo1.getConstructionIndex() > geo2.getConstructionIndex();

		default: // alphabetical

			return GeoElement.compareLabels(
					geo1.getLabel(StringTemplate.defaultTemplate),
					geo2.getLabel(StringTemplate.defaultTemplate)) > 0;

		}

	}

	/**
	 * Gets the insert position for newGeo to insert it in alphabetical order in
	 * parent node. Note: all children of parent must have instances of
	 * GeoElement as user objects.
	 * @param parent 
	 * @param newGeo  
	 * @param mode
	 * @return 
	 */
	final public static int getInsertPosition(TreeItem parent,
			GeoElement newGeo, SortMode mode)
	{
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
		while (right > left)
		{
			int middle = (left + right) / 2;
			node = parent.getChild(middle);
			// nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
			geo2 = ((GeoElement) node.getUserObject());

			if (!compare(newGeo, geo2, mode))
			{
				right = middle;
			} else
			{
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
	 * @param parent 
	 * @param geoLabel 
	 * 
	 * @return -1 when not found
	 */
	final public static int binarySearchGeo(TreeItem parent, String geoLabel)
	{
		int left = 0;
		int right = parent.getChildCount() - 1;
		if (right == -1 || geoLabel == null)
			return -1;

		// binary search for geo's label
		while (left <= right)
		{
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
	 * @param parent 
	 * @param geoLabel 
	 * 
	 * @return -1 when not found
	 */
	final public static int linearSearchGeo(TreeItem parent, String geoLabel)
	{
		if (geoLabel == null)
			return -1;
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++)
		{
			TreeItem node = parent.getChild(i);
			GeoElement g = (GeoElement) node.getUserObject();
			if (geoLabel.equals(g.getLabel(StringTemplate.defaultTemplate)))
				return i;
		}
		return -1;
	}

	/**
	 * removes a node from the tree
	 */
	@Override
	public void remove(GeoElement geo)
	{
		cancelEditing();
		TreeItem node = this.nodeTable.get(geo);

		if (node != null)
		{
			removeFromModel(node);
		}
	}

	@Override
	public void clearView()
	{
		this.nodeTable.clear();
		clearTree();
	}

	/**
	 * remove all from the tree
	 */
	protected void clearTree()
	{
		switch (getTreeMode())
		{
		case DEPENDENCY:
			this.indNode.removeItems();
			this.depNode.removeItems();
			this.auxiliaryNode.removeItems();
			break;
		case TYPE:
			removeItems();
			this.typeNodesMap.clear();
			break;
		case LAYER:
			removeItems();
			this.layerNodesMap.clear();
			break;
		case ORDER:
			this.rootOrder.removeItems();
			removeItems();
			break;
		default:
			break;
		}
	}

	@Override
	public void repaintView()
	{

		Object geo;
		// suppose that the add operations have been already done elsewhere
		for (int i = 0; i < getItemCount(); i++)
		{
			geo = getItem(i).getUserObject();
			if (geo instanceof GeoElement)
			{
				((RadioButtonTreeItem) getItem(i).getWidget()).update();
				getItem(i).setSelected(((GeoElement) geo).doHighlighting());
			} else
			{
				((InlineLabelTreeItem) getItem(i).getWidget()).setText(getItem(
						i).getUserObject().toString());
				for (int j = 0; j < getItem(i).getChildCount(); j++)
				{
					geo = getItem(i).getChild(j).getUserObject();
					if (geo instanceof GeoElement)
						getItem(i).getChild(j).setSelected(
								((GeoElement) geo).doHighlighting());
				}
			}
		}
	}

	/**
	 * renames an element and sorts list
	 */
	@Override
	public void rename(GeoElement geo)
	{
		remove(geo);
		add(geo);
	}

	/**
	 * Reset the algebra view if the mode changed.
	 */
	@Override
	public void setMode(int mode)
	{
		reset();
	}

	@Override
	public void reset()
	{
		cancelEditing();
		// repaint();
		ensureSelectedItemVisible();
	}

	/**
	 * Remove this node from the model.
	 * 
	 * @param node
	 * @param model
	 */
	private void removeFromModel(TreeItem node)
	{
		node.remove();
		this.nodeTable.remove(node.getUserObject());

		// remove the type branch if there are no more children
		switch (this.treeMode)
		{
		case TYPE:
			String typeString = ((GeoElement) node.getUserObject())
					.getTypeStringForAlgebraView();
			TreeItem parent = this.typeNodesMap.get(typeString);

			// this has been the last node
			if (parent.getChildCount() == 0)
			{
				this.typeNodesMap.remove(typeString);
				parent.remove();
			}
			break;
		case LAYER:
			removeFromLayer(((GeoElement) node.getUserObject()).getLayer());

			break;
		case ORDER:
			this.rootOrder.removeItem(node);
			break;
		default:
			break;
		}
	}

	private void removeFromLayer(int i)
	{
		TreeItem parent = this.layerNodesMap.get(i);

		// this has been the last node
		if ((parent != null) && parent.getChildCount() == 0)
		{
			this.layerNodesMap.remove(i);
			parent.remove();
		}

	}

	// TODO EuclidianView#setHighlighted() doesn't exist
	/**
	 * updates node of GeoElement geo (needed for highlighting)
	 * 
	 * @see EuclidianViewW#setHighlighted()
	 */
	@Override
	public void update(GeoElement geo)
	{
		TreeItem node = this.nodeTable.get(geo);

		if (node != null)
		{
			try
			{
				// it may be enough that clicking selects an item,
				// we want to avoid every item selected on changing algebra
				// descriptions
				// node.setSelected(true);
				// ensureSelectedItemVisible();
				((RadioButtonTreeItem) node.getWidget()).update();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			/*
			 * Cancel editing if the updated geo element has been edited, but
			 * not otherwise because editing geos while animation is running
			 * won't work then (ticket #151).
			 */
			if (isEditing())
			{
				if (((RadioButtonTreeItem) node.getWidget()).isThisEdited())
				{
					((RadioButtonTreeItem) node.getWidget()).cancelEditing();
				}
			}
		}
	}

	@Override
	public void updateVisualStyle(GeoElement geo)
	{
		update(geo);
	}

	@Override
	final public void updateAuxiliaryObject(GeoElement geo)
	{
		remove(geo);
		add(geo);
	}

	// TODO
	// /**
	// * Returns true if rendering is done with LaTeX
	// *
	// * @return
	// */
	// public boolean isRenderLaTeX() {
	// return renderLaTeX;
	// }

	@Override
	public int getViewID()
	{
		return App.VIEW_ALGEBRA;
	}

	// TODO
	// public App getApplication() {
	// return this.app;
	// }

	public int[] getGridColwidths()
	{
		return new int[] { getElement().getOffsetWidth() };
	}

	public int[] getGridRowHeights()
	{
		int[] heights = new int[getItemCount()];
		for (int i = 0; i < heights.length; i++)
		{
			heights[i] = getItem(i).getElement().getOffsetHeight();
		}
		heights[0] += 2;
		return heights;
	}

	@Override
	public void changeLayer(GeoElement g, int oldLayer, int newLayer)
	{
		if (this.treeMode.equals(SortMode.LAYER))
		{
			TreeItem node = this.nodeTable.get(g);

			if (node != null)
			{
				node.remove();
				this.nodeTable.remove(node.getUserObject());
				removeFromLayer(oldLayer);
			}

			this.add(g, newLayer);

		}
	}

	// temporary proxies for the temporary implementation of AlgebraController
	// in
	// common
	@Override
	public GeoElement getGeoElementForPath(Object tp)
	{
		// return getGeoElementForPath((TreePath)tp);
		return null;
	}

	@Override
	public GeoElement getGeoElementForLocation(Object tree, int x, int y)
	{
		// return getGeoElementForLocation((JTree)tree, x, y);
		return null;
	}

	@Override
	public Object getPathBounds(Object tp)
	{
		// return getPathBounds((TreePath)tp);
		return null;
	}

	// temporary proxies end

	@Override
	public void cancelEditing()
	{
		this.editing = false;
		setAnimationEnabled(true);
	}

	@Override
	public boolean isEditing()
	{
		return this.editing;
	}

	@Override
	protected boolean isKeyboardNavigationEnabled(TreeItem ti)
	{
		// keys should move the geos in the EV
		// if (isEditing())
		return false;
		// return super.isKeyboardNavigationEnabled(ti);
	}

	public void setUserObject(TreeItem ti, Object ob)
	{
		ti.setUserObject(ob);
		if (ob instanceof GeoElement)
		{
			ti.setWidget(new RadioButtonTreeItem((GeoElement) ob,
					AlgebraViewM.this));
			// Workaround to make treeitem visual selection available
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti
					.getElement().getFirstChildElement(), "display",
					"-moz-inline-box");
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti
					.getElement().getFirstChildElement(), "display",
					"inline-block");
		} else
		{
			ti.setWidget(new InlineLabelTreeItem(this.app, ti, ob.toString()));
		}
	}

	public GeoElement lastSelectedGeo = null;

	@Override
	public void onBrowserEvent(Event event)
	{
		if (!this.editing)
			super.onBrowserEvent(event);
	}

	@Override
	public boolean hasFocus()
	{
		App.debug("unimplemented");
		return false;
	}

	@Override
	public void repaint()
	{
	}

	@Override
	public boolean isShowing()
	{
		return false;
	}
}
