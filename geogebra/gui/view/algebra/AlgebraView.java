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

package geogebra.gui.view.algebra;

import geogebra.gui.SetLabels;
import geogebra.gui.inputfield.MathTextField;
import geogebra.gui.view.Gridable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.main.Application;

import java.awt.Component;
import java.awt.Font;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * AlgebraView with tree for free and dependent objects.
 * 
 * @author  Markus
 * @version 
 */
public class AlgebraView extends JTree implements View, Gridable, SetLabels{	
		
	private static final long serialVersionUID = 1L;
	
	/**
	 * Tree mode where the objects are categorized by their dependency
	 * (free, dependent, auxiliary) -- default value 
	 */
	public static final int MODE_DEPENDENCY = 0;
	
	/**
	 * Tree mode where the objects are categorized by their type 
	 * (points, circles, ..)
	 */
	public static final int MODE_TYPE = 1;
	
	/**
	 * Tree mode where the objects are categorized by the view on which their value is computed 
	 * (xOyPlane, space, ...)
	 */	
	public static final int MODE_VIEW = 2;

	protected Application app; // parent appame
	private Kernel kernel;
	
	private MyRenderer renderer;
	private MyDefaultTreeCellEditor editor;
	private JTextField editTF;
	
	// store all pairs of GeoElement -> node in the Tree
	private HashMap<GeoElement, DefaultMutableTreeNode> nodeTable = new HashMap<GeoElement, DefaultMutableTreeNode>(500);
	
	/**
	 * The tree model.
	 */
	protected DefaultTreeModel model;

	/**
	 * Root node for tree mode MODE_DEPENDENCY.
	 */
	protected DefaultMutableTreeNode rootDependency;
	
	/**
	 * Nodes for tree mode MODE_DEPENDENCY
	 */
	private DefaultMutableTreeNode depNode, indNode;

	protected DefaultMutableTreeNode auxiliaryNode;

	/**
	 * Root node for tree mode MODE_TYPE.
	 */
	private DefaultMutableTreeNode rootType;
	
	/**
	 * Nodes for tree mode MODE_TYPE
	 */
	private HashMap<String, DefaultMutableTreeNode> typeNodesMap;
	
	/**
	 * The mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	protected int treeMode;
	
	/**
	 * Should we show auxiliary objects?
	 */
	private GeoElement selectedGeoElement;
	private DefaultMutableTreeNode selectedNode;
	
	private AlgebraHelperBar helperBar;
	
	private AlgebraController algebraController;
	public AlgebraController getAlgebraController() {
		return algebraController;
	}

	/**
	 * Flag for LaTeX rendering
	 */
	final private static boolean renderLaTeX = true;


	
	
	/** Creates new AlgebraView */
	public AlgebraView(AlgebraController algCtrl) {	
		
		Application.debug("XXX creating Algebra View XXX",1);
		
		app = algCtrl.getApplication();
		kernel = algCtrl.getKernel();
		algCtrl.setView(this);	
		this.algebraController = algCtrl;
		// this is the default value
		treeMode = MODE_DEPENDENCY;

		// cell renderer (tooltips) and editor
		ToolTipManager.sharedInstance().registerComponent(this);

		// EDITOR	   
		setEditable(true);
		initTreeCellRendererEditor();

		// add listener
		addMouseListener(algCtrl);
		addMouseMotionListener(algCtrl);
		
		// add small border
		setBorder(BorderFactory.createEmptyBorder(2,2,2,0));
		
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
		
		setToggleClickCount(1);
				
        // enable drag n drop
		algCtrl.enableDnD();
			
		//attachView();						
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
		if(treeMode == MODE_DEPENDENCY) {
			// don't re-init anything
			if(rootDependency == null) {
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
			if(app.showAuxiliaryObjects) {
				if(!auxiliaryNode.isNodeChild(rootDependency)) {
					model.insertNodeInto(auxiliaryNode, rootDependency, rootDependency.getChildCount());
				}
			}
		} else {
			// don't re-init anything
			if(rootType == null) {
				rootType = new DefaultMutableTreeNode();
				typeNodesMap = new HashMap<String, DefaultMutableTreeNode>(5);
			}
			
			// always try to remove the auxiliary node
			if(app.showAuxiliaryObjects && auxiliaryNode != null) {
				removeAuxiliaryNode();
			}

			// set the root
			model.setRoot(rootType);
		}
	}
	
	protected void removeAuxiliaryNode(){
		model.removeNodeFromParent(auxiliaryNode);
	}
	
	boolean attached = false;

	public void attachView() {
		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
		attached = true;
	}

	public void detachView() {
		kernel.detach(this);
		clearView();
		attached = false;		
	}
	
	public void updateFonts() {
		Font font = app.getPlainFont();
		setFont(font);
		editor.setFont(font);
		renderer.setFont(font);
		editTF.setFont(font);
	}

	private void initTreeCellRendererEditor() {
		renderer = newMyRenderer(app);		
		editTF = new MathTextField(app);
		editor = new MyDefaultTreeCellEditor(this, renderer, 
									new MyCellEditor(editTF, app));
		
		editor.addCellEditorListener(editor); // self-listening
		setCellRenderer(renderer);
		setCellEditor(editor);
	}
	
	/**
	 * 
	 * @param app
	 * @return new renderer of a cell
	 */
	protected MyRenderer newMyRenderer(Application app){
		return new MyRenderer(app, this);
	}

	public void clearSelection() {
		super.clearSelection();
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
			
			if(getTreeMode() == MODE_DEPENDENCY) {
				model.insertNodeInto(auxiliaryNode, rootDependency, rootDependency.getChildCount());
			}
			
			kernel.notifyAddAll(this);
		} else {
			// if we're listing the auxiliary objects in a single leaf we can
			// just remove that leaf, but for type-based categorization those
			// auxiliary nodes might be scattered across the whole tree,
			// therefore we just rebuild the tree
			if(getTreeMode() == MODE_DEPENDENCY) {
				if(auxiliaryNode.getParent() != null) {
					model.removeNodeFromParent(auxiliaryNode);
				}
			} else {
				clearView();
				kernel.notifyAddAll(this);
			}
		}					
	}
	
	/**
	 * @return The display mode of the tree, see MODE_DEPENDENCY, MODE_TYPE
	 */
	public int getTreeMode() {
		return treeMode;
	}
	
	/**
	 * @param value Either AlgebraView.MODE_DEPDENCY or AlgebraView.MODE_TYPE
	 */
	public void setTreeMode(int value) {
		if(getTreeMode() == value) {
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
	public AlgebraHelperBar getHelperBar() {
		if(helperBar == null) {
			helperBar = newAlgebraHelperBar();
		}
		
		return helperBar;
	}
	
	/**
	 * 
	 * @return new algebra helper bar
	 */
	protected AlgebraHelperBar newAlgebraHelperBar(){
		return new AlgebraHelperBar(this, app);
	}

	public static GeoElement getGeoElementForLocation(JTree tree, int x, int y) {
		TreePath tp = tree.getPathForLocation(x, y);
		return getGeoElementForPath(tp);
	}
		
	public static GeoElement getGeoElementForPath(TreePath tp) {
		if (tp == null)
			return null;

		Object ob;
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) tp.getLastPathComponent();
		if (node != null
			&& (ob = node.getUserObject()) instanceof GeoElement)
			return (GeoElement) ob;
		else
			return null;
	}
	
	public void setToolTipText(String text) {
		renderer.setToolTipText(text);
	}

	/**
	 * Open Editor textfield for geo.
	 */
	public void startEditing(GeoElement geo, boolean shiftDown) {
		if (geo == null) return;
		
		// open Object Properties for eg GeoImages
		if (!geo.isAlgebraViewEditable()) {
			ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
			geos.add(geo);
			app.getGuiManager().showPropertiesDialog(geos);
			return;
		}

		if (!shiftDown || !geo.isPointOnPath() && !geo.isPointInRegion()) {
			if (!geo.isIndependent()
			|| !attached )		// needed for F2 when Algebra View closed
			 {
				if (geo.isRedefineable()) { 
					app.getGuiManager().showRedefineDialog(geo, true);
				}
				return;
			}
			
			if (!geo.isChangeable()) {
				if (geo.isFixed()) {
					//app.showMessage(app.getError("AssignmentToFixed"));
				} 
				else if (geo.isRedefineable()) { 
					app.getGuiManager().showRedefineDialog(geo, true);
				}
				return;
			}
		}
	
		
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) nodeTable.get(geo);

		if (node != null) {
			cancelEditing();
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
	 * resets all fix labels of the View. This method is called
	 * by the application if the language setting is changed.
	 */
	public void setLabels() {
		
		setTreeLabels();
		
		if(helperBar != null) {
			helperBar.updateLabels();
		}
	}
	
	/**
	 * set labels on the tree
	 */
	protected void setTreeLabels(){
		if(getTreeMode() == MODE_DEPENDENCY) {
			indNode.setUserObject(app.getPlain("FreeObjects"));
			model.nodeChanged(indNode);
			
			depNode.setUserObject(app.getPlain("DependentObjects"));
			model.nodeChanged(depNode);
			
			auxiliaryNode.setUserObject(app.getPlain("AuxiliaryObjects"));
			model.nodeChanged(auxiliaryNode);
		} else {
			DefaultMutableTreeNode node;
			for (String key : typeNodesMap.keySet()) {
				node = typeNodesMap.get(key);
				node.setUserObject(app.getPlain(key));
				model.nodeChanged(node);
			}
		}
	}

	/**
	 * adds a new node to the tree
	 */
	public void add(GeoElement geo) {	
		cancelEditing();	
		
		if (geo.isLabelSet() && geo.showInAlgebraView() && geo.isSetAlgebraVisible()) {
			// don't add auxiliary objects if the tree is categorized by type
			if(getTreeMode() != MODE_DEPENDENCY && !showAuxiliaryObjects() && geo.isAuxiliaryObject()) {
				return;
			}
			
			DefaultMutableTreeNode parent, node;
			node = new DefaultMutableTreeNode(geo);
			parent = getParentNode(geo);
			

			// add node to model (alphabetically ordered)
			int pos = getInsertPosition(parent, geo);
			
			model.insertNodeInto(node, parent, pos);
			nodeTable.put(geo, node);
			
			// ensure that the leaf with the new obect is visible
			expandPath(new TreePath(new Object[] { model.getRoot(), parent }));
		}
	}

	/**
	 * 
	 * @param geo
	 * @return parent node of this geo
	 */
	protected DefaultMutableTreeNode getParentNode(GeoElement geo){
		DefaultMutableTreeNode parent;
		
		if(treeMode == MODE_DEPENDENCY) {				
			if (geo.isAuxiliaryObject()) {
				parent = auxiliaryNode;
			}				
			else if (geo.isIndependent()) {			
				parent = indNode;				
			} 
			else {				
				parent = depNode;
			}
		} else {
			// get type node
			String typeString = geo.getObjectType();
			parent = (DefaultMutableTreeNode) typeNodesMap.get(typeString);
			
			// do we have to create the parent node?
			if (parent == null) {
				String transTypeString = geo.translatedTypeString();
				parent = new DefaultMutableTreeNode(transTypeString);									
				typeNodesMap.put(typeString, parent);
				
				// find insert pos
				int pos = rootType.getChildCount();
				for (int i=0; i < pos; i++) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootType.getChildAt(i);
					if (transTypeString.compareTo(child.toString()) < 0) {
						pos = i;
						break;
					}
				}
				
				model.insertNodeInto(parent, rootType, pos);				
			}
		}
		
		return parent;
	}
	
	/**
	 * Gets the insert position for newGeo to insert it in alphabetical
	 * order in parent node. Note: all children of parent must have instances of GeoElement 
	 * as user objects.
	 */
	final public static int getInsertPosition(DefaultMutableTreeNode parent, GeoElement newGeo) { 							
		// label of inserted geo
		String newLabel = newGeo.getLabel();
		
		// standard case: binary search		
		int left = 0;
		int right = parent.getChildCount();	
		if (right == 0) return right;
		
		// bigger then last?
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastChild();	
		String nodeLabel = ((GeoElement) node.getUserObject()).getLabel();	
		if (GeoElement.compareLabels(newLabel, nodeLabel) > 0) 
			return right;				
		
		// binary search
		while (right > left) {							
			int middle = (left + right) / 2;
			node = (DefaultMutableTreeNode) parent.getChildAt(middle);
			nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
			
			if (GeoElement.compareLabels(newLabel, nodeLabel) < 0) {
				right = middle;
			} else {
				left = middle + 1;
			}
		}													
		
		// insert at correct position
		return right;				
	}
	
	/**
	 * Performs a binary search for geo among the children of parent. All children of parent
	 * have to be instances of GeoElement sorted alphabetically by their names.
	 * @return -1 when not found
	 */
	final public static int binarySearchGeo(DefaultMutableTreeNode parent, String geoLabel) { 				
		int left = 0;
		int right = parent.getChildCount()-1;
		if (right == -1) return -1;
	
		// binary search for geo's label
		while (left <= right) {							
			int middle = (left + right) / 2;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(middle);
			String nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
			
			int compare = GeoElement.compareLabels(geoLabel, nodeLabel);
			if (compare < 0)
				right = middle -1;
		    else if (compare > 0)
		    	left = middle + 1;	
		    else
		    	return middle;
		}												  
		
		return -1;				
	}		
	
	/**
	 * Performs a linear search for geo among the children of parent.
	 * @return -1 when not found
	 */
	final public static int linearSearchGeo(DefaultMutableTreeNode parent, String geoLabel) { 												
		int childCount = parent.getChildCount();	
		for (int i = 0; i < childCount; i++) {			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
			GeoElement g = (GeoElement) node.getUserObject();
			if (geoLabel.equals(g.getLabel()))
				return i;
		}
		return -1;
	}

	/**
	 * removes a node from the tree
	 */
	public void remove(GeoElement geo) {		
		cancelEditing();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeTable.get(geo);
		
		if (node != null) 
		{
			removeFromModel(node, ((DefaultTreeModel)getModel()));		
		}
	}
	
	public void clearView() {
		nodeTable.clear();
		
		clearTree();
		
		model.reload();
	}
	
	/**
	 * remove all from the tree
	 */
	protected void clearTree() {
		if(getTreeMode() == MODE_DEPENDENCY) {
			indNode.removeAllChildren();
			depNode.removeAllChildren();
			auxiliaryNode.removeAllChildren();
		} else {
			rootType.removeAllChildren();
			typeNodesMap.clear();
		}
	}
	
	
	final public void repaintView() {
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
	public void setMode(int mode) {
		reset();
	}
	
	public void reset() {
	  	cancelEditing();
	  repaint();
	}

	/**
	 * Remove this node from the model.
	 * @param node
	 * @param model
	 */
	private void removeFromModel(DefaultMutableTreeNode node, DefaultTreeModel model) {
		model.removeNodeFromParent(node);
		nodeTable.remove(node.getUserObject());
		
		// remove the type branch if there are no more children
		if(treeMode == MODE_TYPE) {
			String typeString = ((GeoElement)node.getUserObject()).getObjectType();
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) typeNodesMap.get(typeString);
			
			// this has been the last node
			if(parent.getChildCount() == 0) {
				typeNodesMap.remove(typeString);
				model.removeNodeFromParent(parent);
			}
		}            
	}

	/**
	   * updates node of GeoElement geo (needed for highlighting)
	   * @see EuclidianView.setHighlighted()
	   */
	final public void update(GeoElement geo) {			
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) nodeTable.get(geo);
		
		if (node != null) {
			((DefaultTreeModel)getModel()).nodeChanged(node);
			
			/*
			 * Cancel editing if the updated geo element has been edited, but
			 * not otherwise because editing geos while animation is running
			 * won't work then (ticket #151). 
			 */
			if (isEditing()) {
				if(getEditingPath().equals(new TreePath(node.getPath()))) {
					cancelEditing();
				}
			}
		} 	
	}
	


	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}
	
	
	final public void updateAuxiliaryObject(GeoElement geo) {
		remove(geo);
		add(geo);
	}


	/**
	 * Returns true if rendering is done with LaTeX
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
	private class MyDefaultTreeCellEditor
		extends DefaultTreeCellEditor
		implements CellEditorListener {

		public MyDefaultTreeCellEditor(AlgebraView tree, DefaultTreeCellRenderer renderer,
								DefaultCellEditor editor) {
			super(tree, renderer, editor);						
		}

		/*
		 * CellEditorListener implementation 
		*/
		public void editingCanceled(ChangeEvent event) {
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
			
			GeoElement geo = kernel.getAlgebraProcessor().changeGeoElement(selectedGeoElement, newValue, redefine, true);			
			if (geo != null) {				
				selectedGeoElement = geo;
				selectedNode.setUserObject(selectedGeoElement);
			}			
			
			((DefaultTreeModel)getModel()).nodeChanged(selectedNode); // refresh display        
		}

		/*
		 * OVERWRITE SOME METHODS TO ONLY ALLOW EDITING OF GeoElements
		 */

		public boolean isCellEditable(EventObject event) {
			
			if (event == null) return true;
			
			return false;		
		}

		//
		// TreeSelectionListener
		//

		/**
		 * Resets lastPath.
		 */
		public void valueChanged(TreeSelectionEvent e) {
			if (tree != null) {
				if (tree.getSelectionCount() == 1)
					lastPath = tree.getSelectionPath();
				else
					lastPath = null;
				/***** ADDED by Markus Hohenwarter ***********/
				storeSelection(lastPath);
				/********************************************/
			}
			if (timer != null) {
				timer.stop();
			}
		}

		/** stores currently selected GeoElement and node.
		 *  selectedNode, selectedGeoElement are private members of AlgebraView
		 */
		private void storeSelection(TreePath tp) {
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
		}

	}  // MyEditor

	public int getViewID() {
		return Application.VIEW_ALGEBRA;
	}

	public Application getApplication() {
		return app;
	}

	public int[] getGridColwidths() {
		return new int[]{getWidth()};
	}

	public int[] getGridRowHeights() {
//		Object root=model.getRoot();
//		ArrayList<Integer> heights=new ArrayList<Integer>();
//		for (int i=0;i<model.getChildCount(root);i++){
//			Object folder=model.getChild(root, i);
//			if (model.)
//		}
////		m.getChildCount(root);
//		
//		return new int[]{getHeight()};
		int[] heights=new int[getRowCount()];
		for (int i=0;i<heights.length;i++){
			heights[i]=getRowBounds(i).height;
		}
		heights[0]+=2;
		return heights;
	}

	public Component[][] getPrintComponents() {
		return new Component[][]{{this}};
	}
	
	/**
	 * returns settings in XML format
	 *
	public void getXML(StringBuilder sb) {
		
		sb.append("<algebraView>\n");
			sb.append("\t<useLaTeX ");
			sb.append(" value=\"");
			sb.append(isRenderLaTeX());
			sb.append("\"");
			sb.append("/>\n");
		sb.append("</algebraView>\n");
	}*/

} // AlgebraView
