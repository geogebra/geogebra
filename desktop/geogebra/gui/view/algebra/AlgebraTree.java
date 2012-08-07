package geogebra.gui.view.algebra;

import geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.euclidian.EuclidianViewD;
import geogebra.main.AppD;

import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * JTree for objects
 * 
 * @author matthieu
 *
 */
public class AlgebraTree extends JTree {

	private static final long serialVersionUID = 1L;
	
	protected AppD app; // parent appame
	protected Kernel kernel;

	protected MyRendererForAlgebraTree renderer;


	protected AlgebraTreeController algebraController;
	

	/**
	 * The tree model.
	 */
	protected DefaultTreeModel model;
	
	/**
	 * Root node for tree mode MODE_TYPE.
	 */
	protected DefaultMutableTreeNode rootType;

	/**
	 * Nodes for tree mode MODE_TYPE
	 */
	protected HashMap<String, DefaultMutableTreeNode> typeNodesMap;
	
	// store all pairs of GeoElement -> node in the Tree
	protected HashMap<GeoElement, DefaultMutableTreeNode> nodeTable = new HashMap<GeoElement, DefaultMutableTreeNode>(
			500);
	
	/**
	 * Flag for LaTeX rendering
	 */
	final private static boolean renderLaTeX = true;
	
	
	/** Creates new AlgebraView */
	public AlgebraTree(AlgebraTreeController algCtrl) {


		app = (AppD)algCtrl.getApplication();
		kernel = algCtrl.getKernel();
		this.algebraController = algCtrl;
		
		algebraController.setTree(this);


		initTree();


	}
	
	/**
	 * init the tree
	 */
	protected void initTree(){
		

		initTreeCellRendererEditor();

		// add listener
		addMouseListener(algebraController);
		addMouseMotionListener(algebraController);

		// add small border
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));

		// initializes the tree model
		model = new DefaultTreeModel(null);
		initModel();
		setModel(model);

		setLargeModel(true);
		setLabels();

		// tree's options
		setRootVisible(false);
		// show lines from parent to children
		putClientProperty("JTree.lineStyle", "Angled");
		setInvokesStopCellEditing(true);
		setScrollsOnExpand(true);
		setRowHeight(-1); // to enable flexible height of cells

		setToggleClickCount(-1);
	}
	
	protected void initTreeCellRendererEditor() {
		renderer = newMyRenderer(app);
		setCellRenderer(renderer);
	}
	
	/**
	 * 
	 * @param app
	 * @return new renderer of a cell
	 */
	protected MyRendererForAlgebraTree newMyRenderer(AppD app) {
		return new MyRendererForAlgebraTree(app, this);
	}
	
	public boolean isRenderLaTeX() {
		return renderLaTeX;
	}
	
	/**
	 * @return The display mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	public SortMode getTreeMode() {
		return SortMode.TYPE;
	}

	
	/**
	 * Method to initialize the tree model of the current tree mode. This method
	 * should be called whenever the tree mode is changed, it won't initialize
	 * anything if not necessary.
	 * 
	 * This method will also actually change the model of the tree.
	 */
	protected void initModel() {

		// don't re-init anything
		if (rootType == null) {
			rootType = new DefaultMutableTreeNode();
			typeNodesMap = new HashMap<String, DefaultMutableTreeNode>(5);
		}

		checkRemoveAuxiliaryNode();

		// set the root
		model.setRoot(rootType);
	}
	
	/**
	 * check if application ask for remove
	 */
	protected void checkRemoveAuxiliaryNode(){
		//not removed here
	}
	
	/**
	 * resets all fix labels of the View. This method is called by the
	 * application if the language setting is changed.
	 */
	public void setLabels() {

		setTreeLabels();

	}

	/**
	 * set labels on the tree
	 */
	protected void setTreeLabels() {
		DefaultMutableTreeNode node;
		for (String key : typeNodesMap.keySet()) {
			node = typeNodesMap.get(key);
			node.setUserObject(app.getPlain(key));
			model.nodeChanged(node);
		}
	}
	
	@Override
	public void setToolTipText(String text) {
		renderer.setToolTipText(text);
	}
	
	
	
	/**
	 * @param tree tree
	 * @param x x coord
	 * @param y y coord
	 * @return geo at this location on the tree
	 */
	public static GeoElement getGeoElementForLocation(JTree tree, int x, int y) {
		TreePath tp = tree.getPathForLocation(x, y);
		return getGeoElementForPath(tp);
	}

	/**
	 * @param tp tree path
	 * @return geo at this path
	 */
	public static GeoElement getGeoElementForPath(TreePath tp) {
		if (tp == null)
			return null;

		Object ob;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp
				.getLastPathComponent();
		if (node != null && (ob = node.getUserObject()) instanceof GeoElement)
			return (GeoElement) ob;
		
		return null;
	}
	
	/**
	 * adds a new node to the tree
	 */
	public void add(GeoElement geo) {
		add(geo,-1);
	}
	
	/**
	 * do we show this geo here ?
	 * @param geo geo
	 * @return true if geo has to be shown
	 */
	protected boolean show(GeoElement geo){
		return geo.isLabelSet();
	}
	
	protected void add(GeoElement geo,int forceLayer) {
		cancelEditing();

		if (show(geo)) {
			// don't add auxiliary objects if the tree is categorized by type
			if (!getTreeMode().equals(SortMode.DEPENDENCY) && !showAuxiliaryObjects()
					&& geo.isAuxiliaryObject()) {
				return;
			}

			DefaultMutableTreeNode parent, node;
			node = new DefaultMutableTreeNode(geo);
			parent = getParentNode(geo,forceLayer);
			
			// add node to model (alphabetically ordered)
			int pos = getInsertPosition(parent, geo, getTreeMode());

			model.insertNodeInto(node, parent, pos);
			nodeTable.put(geo, node);

			// ensure that the leaf with the new object is visible
			expandPath(new TreePath(new Object[] { model.getRoot(), parent }));
		}
	}
	
	
	/**
	 * removes a node from the tree
	 */
	public void remove(GeoElement geo) {
		cancelEditing();
		DefaultMutableTreeNode node = nodeTable
				.get(geo);

		if (node != null) {
			removeFromModel(node, ((DefaultTreeModel) getModel()));
		}
	}
	
	public void clearView() {
		nodeTable.clear();

		clearTree();

		model.reload();
	}
	
	/**
	 * renames an element and sorts list
	 */
	public void rename(GeoElement geo) {
		remove(geo);
		add(geo);
	}


	

	// TODO EuclidianView#setHighlighted() doesn't exist
	/**
	 * updates node of GeoElement geo (needed for highlighting)
	 * 
	 * @see EuclidianViewD#setHighlighted()
	 */
	 public void update(GeoElement geo) {
		DefaultMutableTreeNode node = nodeTable
				.get(geo);

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
				((DefaultTreeModel)getModel()).nodeChanged(node);
			} catch (Exception e) {
				e.printStackTrace();
			}
			/*
			 * Cancel editing if the updated geo element has been edited, but
			 * not otherwise because editing geos while animation is running
			 * won't work then (ticket #151).
			 */
			if (isEditing()) {
				if (getEditingPath().equals(new TreePath(node.getPath()))) {
					cancelEditing();
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
	 * remove all from the tree
	 */
	protected void clearTree() {
		rootType.removeAllChildren();
		typeNodesMap.clear();
	}
	
	
	/**
	 * Remove this node from the model.
	 * 
	 * @param node
	 * @param model
	 */
	protected void removeFromModel(DefaultMutableTreeNode node,
			DefaultTreeModel model) {
		model.removeNodeFromParent(node);
		nodeTable.remove(node.getUserObject());

		removeFromModelForMode(node, model);
	}
	
	
	/**
	 * Remove this node from the model.
	 * 
	 * @param node
	 * @param model
	 */
	protected void removeFromModelForMode(DefaultMutableTreeNode node,
			DefaultTreeModel model) {
		
		String typeString = ((GeoElement) node.getUserObject()).getTypeStringForAlgebraView();
		DefaultMutableTreeNode parent = typeNodesMap.get(typeString);

		// this has been the last node
		if (parent.getChildCount() == 0) {
			typeNodesMap.remove(typeString);
			model.removeNodeFromParent(parent);
		}

	}
	
	public boolean showAuxiliaryObjects() {
		return true;
	}

	protected DefaultMutableTreeNode getParentNode(GeoElement geo,int forceLayer) {
		DefaultMutableTreeNode parent;

		// get type node
		String typeString = geo.getTypeStringForAlgebraView();
		
		
		parent = typeNodesMap.get(typeString);

		// do we have to create the parent node?
		if (parent == null) {
			String transTypeString = geo.translatedTypeStringForAlgebraView();
			parent = new DefaultMutableTreeNode(transTypeString);
			typeNodesMap.put(typeString, parent);

			// find insert pos
			int pos = rootType.getChildCount();
			for (int i = 0; i < pos; i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootType
						.getChildAt(i);
				if (transTypeString.compareTo(child.toString()) < 0) {
					pos = i;
					break;
				}
			}

			model.insertNodeInto(parent, rootType, pos);
		}


		return parent;
	}
	

	/**
	 * Gets the insert position for newGeo to insert it in alphabetical order in
	 * parent node. Note: all children of parent must have instances of
	 * GeoElement as user objects.
	 * @param mode 
	 */
	final public static int getInsertPosition(DefaultMutableTreeNode parent,
			GeoElement newGeo, SortMode mode) {
		// label of inserted geo
		//String newLabel = newGeo.getLabel();

		// standard case: binary search
		int left = 0;
		int right = parent.getChildCount();
		if (right == 0)
			return right;

		// bigger then last?
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent
				.getLastChild();
		//String nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
		GeoElement geo2 = ((GeoElement) node.getUserObject());
		if (compare(newGeo, geo2, mode))
			return right;

		// binary search
		while (right > left) {
			int middle = (left + right) / 2;
			node = (DefaultMutableTreeNode) parent.getChildAt(middle);
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
	

	private static boolean compare(GeoElement geo1, GeoElement geo2, SortMode mode) {
		switch (mode) {

		case ORDER:

			return geo1.getConstructionIndex() > geo2.getConstructionIndex();

		default: // alphabetical

			return GeoElement.compareLabels(geo1.getLabel(StringTemplate.defaultTemplate),
					geo2.getLabel(StringTemplate.defaultTemplate)) > 0;

		}

	}
}
