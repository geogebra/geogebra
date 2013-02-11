package geogebra.mobile.gui.algebra;

import geogebra.common.awt.GFont;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.gui.SetLabels;
import geogebra.common.gui.view.algebra.AlgebraView;
import geogebra.common.kernel.LayerView;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

import java.util.HashMap;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * AlgebraView with tree for free and dependent objects.
 * 
 * Taken from the web-project.
 * 
 */
public class AlgebraViewM extends Tree implements LayerView, SetLabels, AlgebraView
{

	protected App app; 
	
	// store all pairs of GeoElement -> node in the Tree
	private HashMap<GeoElement, TreeItem> nodeTable = new HashMap<GeoElement, TreeItem>(500);

	/**
	 * Nodes for tree mode MODE_DEPENDENCY.
	 */
	private TreeItem depNode, indNode;

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

	private EuclidianController controller;

	/**
	 * Creates new AlgebraView.
	 * 
	 * @param algCtrl
	 *          : AlgebraController
	 * 
	 */
	public AlgebraViewM(EuclidianController ctr)
	{
		super();

		this.app = ctr.getApplication();
//		algCtrl.setView(this);
		this.controller = ctr;
		// this is the default value
		this.treeMode = SortMode.TYPE;

		// initializes the tree model
		initModel();

		setLabels();

		getElement().setId("View_" + App.VIEW_ALGEBRA);
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
			if (this.depNode == null || this.indNode == null)
			{
				// rootDependency = new TreeItem();
				this.depNode = new TreeItem(); // dependent objects
				this.indNode = new TreeItem();
			}

			// set the root
			clear();
			addItem(this.indNode);
			addItem(this.depNode);

			break;
		case ORDER:
			if (this.rootOrder == null)
			{
				// both rootOrder and AlgebraView will have the Tree items
				this.rootOrder = new TreeItem();
			}
			setUserObject(this.rootOrder, "");

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

			// set the root
			clear();
			// addItem(rootLayer);
			break;
		default:
			break;
		}

	}

	public void updateFonts()
	{
		GFont font = this.app.getPlainFontCommon();
		getStyleElement().getStyle().setFontStyle(Style.FontStyle.valueOf(font.isItalic() ? "ITALIC" : "NORMAL"));
		getStyleElement().getStyle().setFontSize(font.getSize(), Style.Unit.PX);
		getStyleElement().getStyle().setFontWeight(Style.FontWeight.valueOf(font.isBold() ? "BOLD" : "NORMAL"));
	}

	/**
	 * @return The display mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	public SortMode getTreeMode()
	{
		return this.treeMode;
	}

	@Override
	public Object getPathForLocation(int x, int y)
	{
		return null;
	}

	public boolean editing = false;

	@Override
	public void startEditing(GeoElement geo, boolean shiftDown)
	{
	}

	/**
	 * resets all fix labels of the View. This method is called by the application
	 * if the language setting is changed.
	 */
	@Override
	public void setLabels()
	{
		setTreeLabels();
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
				setUserObject(node, this.app.getPlain("LayerA", key.toString()) + "TODO" + key);
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

		if (geo.isLabelSet() && geo.showInAlgebraView() && geo.isSetAlgebraVisible())
		{
			// don't add auxiliary objects if the tree is categorized by type
			// if (!getTreeMode().equals(SortMode.DEPENDENCY) && geo.isAuxiliaryObject())
			// {
			//	return;
			// }

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
			}
			else
				try
				{
					parent.insertItem(pos, node);
					if (parent.equals(this.rootOrder))
						insertItem(pos, node);
				}
				catch (IndexOutOfBoundsException e)
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
			if (geo.isIndependent())
			{
				parent = this.indNode;
			}
			else
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
				String transTypeString = geo.translatedTypeStringForAlgebraView();
				parent = new TreeItem(transTypeString);
				parent.addStyleName("algebraView-heading");

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
			Integer layer = new Integer(forceLayer > -1 ? forceLayer : geo.getLayer());
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

	private static boolean compare(GeoElement geo1, GeoElement geo2, SortMode mode)
	{
		switch (mode)
		{

		case ORDER:

			return geo1.getConstructionIndex() > geo2.getConstructionIndex();

		default: // alphabetical

			return GeoElement.compareLabels(geo1.getLabel(StringTemplate.defaultTemplate), geo2.getLabel(StringTemplate.defaultTemplate)) > 0;

		}

	}

	/**
	 * Gets the insert position for newGeo to insert it in alphabetical order in
	 * parent node. Note: all children of parent must have instances of GeoElement
	 * as user objects.
	 * 
	 * @param parent
	 *          parent node
	 * @param newGeo
	 *          geoElement to get a new position
	 * @param mode
	 *          the SortMode
	 * @return position
	 */
	final public static int getInsertPosition(TreeItem parent, GeoElement newGeo, SortMode mode)
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
			}
			else
			{
				left = middle + 1;
			}
		}

		// insert at correct position
		return right;
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
				((AlgebraViewTreeItem) getItem(i).getWidget()).update();
				getItem(i).setSelected(((GeoElement) geo).doHighlighting());
			}
			else
			{
				((InlineLabelTreeItem) getItem(i).getWidget()).setText(getItem(i).getUserObject().toString());
				for (int j = 0; j < getItem(i).getChildCount(); j++)
				{
					geo = getItem(i).getChild(j).getUserObject();
					if (geo instanceof GeoElement)
						getItem(i).getChild(j).setSelected(((GeoElement) geo).doHighlighting());
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
	public void setMode(int mode, ModeSetter modeSetter)
	{
		reset();
	}

	@Override
	public void reset()
	{
		cancelEditing();
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
			String typeString = ((GeoElement) node.getUserObject()).getTypeStringForAlgebraView();
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
		Integer j = new Integer(i);

		TreeItem parent = this.layerNodesMap.get(j);

		// this has been the last node
		if ((parent != null) && parent.getChildCount() == 0)
		{
			this.layerNodesMap.remove(j);
			parent.remove();
		}

	}

	// EuclidianView#setHighlighted()
	/**
	 * updates node of GeoElement geo (needed for highlighting)
	 * 
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
				((AlgebraViewTreeItem) node.getWidget()).update();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			/*
			 * Cancel editing if the updated geo element has been edited, but not
			 * otherwise because editing geos while animation is running won't work
			 * then (ticket #151).
			 */
			if (isEditing())
			{
				if (((AlgebraViewTreeItem) node.getWidget()).isThisEdited())
				{
					((AlgebraViewTreeItem) node.getWidget()).cancelEditing();
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

	@Override
	public int getViewID()
	{
		return App.VIEW_ALGEBRA;
	}

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
			ti.setWidget(new AlgebraViewTreeItem((GeoElement) ob, AlgebraViewM.this, this.controller));
			// Workaround to make treeitem visual selection available
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti.getElement().getFirstChildElement(), "display", "-moz-inline-box");
			DOM.setStyleAttribute((com.google.gwt.user.client.Element) ti.getElement().getFirstChildElement(), "display", "inline-block");
		}
		else
		{
			ti.setWidget(new InlineLabelTreeItem(this.app, ti, ob.toString()));
		}
	}

	@Override
	public void onBrowserEvent(Event event)
	{
		if (!this.editing)
			super.onBrowserEvent(event);
	}

	@Override
	public boolean hasFocus()
	{
		return false;
	}

	@Override
	public boolean isShowing()
	{
		return false;
	}

	@Override
  public void setTreeMode(int mode)
  {
	  // TODO Auto-generated method stub
	  
  }
}
