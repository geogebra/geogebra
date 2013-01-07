package geogebra.gui.view.algebra;

import geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.euclidian.EuclidianViewD;
import geogebra.main.AppD;

import java.awt.Font;
import java.util.ArrayList;
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
	private boolean renderLaTeX = true;
	
	
	/** Creates new AlgebraView */
	public AlgebraTree(AlgebraTreeController algCtrl, boolean renderLaTeX) {


		app = (AppD)algCtrl.getApplication();
		kernel = algCtrl.getKernel();
		this.algebraController = algCtrl;
		this.renderLaTeX = renderLaTeX;
		
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
	 * @param tp tree path
	 * @return geos as childs under this path
	 */
	public static ArrayList<GeoElement> getGeoChildsForPath(TreePath tp) {
		if (tp == null)
			return null;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp
				.getLastPathComponent();

		if (node == null)
			return null;

		ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
		addChilds(ret, node, 0, node.getChildCount());
		return ret;
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
		if (parent != null && parent.getChildCount() == 0) {
			typeNodesMap.remove(typeString);
			model.removeNodeFromParent(parent);
		}

	}
	
	public boolean showAuxiliaryObjects() {
		return true;
	}
	
	
	/**
	 * 
	 * @return current root of the tree
	 */
	public DefaultMutableTreeNode getRoot(){
		return rootType;
	}
	
	/** 
	 * 
	 * @param geo1 one geo
	 * @param geo2 one other geo
	 * @return the indices in the tree corresponding to the two geos
	 */
	protected int[][] getIndices(GeoElement geo1, GeoElement geo2){
		
		int found = 0;
		
		DefaultMutableTreeNode root = getRoot();
		
		int[][] ret = new int[2][];
		for (int i=0; i	< root.getChildCount() && found < 2; i++){
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
			for (int j=0; j	< child.getChildCount()  && found < 2; j++){
				DefaultMutableTreeNode child2 = (DefaultMutableTreeNode) child.getChildAt(j);
				Object ob = child2.getUserObject();
				if (ob==geo1 || ob==geo2){
					ret[found] = new int[] {i,j};
					found++;
				}			
			}
		}
		
		if (found<2)
			return null;

		return ret;
		
		
	}
	
	/**
	 * 
	 * @param geo1
	 * @param geo2
	 * @return geos displayed in the tree between the two geos (included)
	 */
	public ArrayList<GeoElement> getGeosBetween(GeoElement geo1, GeoElement geo2) {
		
		int[][] indices = getIndices(geo1, geo2);
		
		
		if (indices!=null){
			int p1 = indices[0][0]; //parent index of first geo
			int c1 = indices[0][1]; //child index of first geo
			int p2 = indices[1][0]; //parent index of second geo
			int c2 = indices[1][1]; //child index of second geo

			DefaultMutableTreeNode root = getRoot();
			
			if (p1==p2){//same category
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(p1);
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				addChilds(ret, node, c1, c2+1);
				return ret;
			}//else, all geos between the two categories
			
			ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(p1);			
			addChilds(ret, node, c1, node.getChildCount());
			for (int i=p1+1; i<p2; i++){
				node = (DefaultMutableTreeNode) root.getChildAt(i);
				//add childs only if node is expanded
				if(!isCollapsed(new TreePath(node.getPath())))
					addChilds(ret, node, 0, node.getChildCount());
			}
			node = (DefaultMutableTreeNode) root.getChildAt(p2);
			addChilds(ret, node, 0, c2+1);
			
			return ret;
			
		}
		
		
		
		return null;
		
	}
	
	private static void addChilds(ArrayList<GeoElement> list, DefaultMutableTreeNode node, int start, int end){
		Object ob;
		for (int i=start; i<end; i++){
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			if (child != null && (ob = child.getUserObject()) instanceof GeoElement)
				list.add((GeoElement) ob);
		}
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
	
	

	public void updateFonts() {
		Font font = app.getPlainFont();
		setFont(font);
		renderer.setFont(font);
	}
}
