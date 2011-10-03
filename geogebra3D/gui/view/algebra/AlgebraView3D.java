package geogebra3D.gui.view.algebra;

import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.view.algebra.AlgebraController;
import geogebra.gui.view.algebra.AlgebraHelperBar;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.gui.view.algebra.MyRenderer;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra3D.Application3D;


/**
 * Algebra view for 3D : change display regarding graphic view selected
 * 
 * @author mathieu
 *
 */
public class AlgebraView3D extends AlgebraView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Nodes for tree mode MODE_VIEW
	 */
	private HashMap<String, DefaultMutableTreeNode> viewNodesMap;
	
	/**
	 * Root node for tree mode MODE_VIEW.
	 */
	private DefaultMutableTreeNode rootView;


	/**
	 * @param algCtrl
	 */
	public AlgebraView3D(AlgebraController algCtrl) {
		super(algCtrl);
	}
	
	
	protected AlgebraHelperBar newAlgebraHelperBar(){
		return new AlgebraHelperBar3D(this, app);
	}
	
	
	protected DefaultMutableTreeNode getParentNode(GeoElement geo){
		
		if(treeMode != MODE_VIEW)
			return super.getParentNode(geo);
		
		DefaultMutableTreeNode parent;
		
		// get view node
		EuclidianViewInterface view = geo.getViewForValueString();
		if (view==null){
			if (geo.isGeoElement3D())
				view = ((Application3D) app).getEuclidianView3D();
			else
				view = app.getEuclidianView();
		}
		
		String viewString = view.getFromPlaneString();
		
		parent = (DefaultMutableTreeNode) viewNodesMap.get(viewString);

		// do we have to create the parent node?
		if (parent == null) {
			String transTypeString = view.getTranslatedFromPlaneString();
			parent = new DefaultMutableTreeNode(transTypeString);									
			viewNodesMap.put(viewString, parent);

			// find insert pos
			int pos = rootView.getChildCount();
			for (int i=0; i < pos; i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootView.getChildAt(i);
				if (transTypeString.compareTo(child.toString()) < 0) {
					pos = i;
					break;
				}
			}

			model.insertNodeInto(parent, rootView, pos);				
		}
		
		
		return parent;
	}

	protected void clearTree() {
		
		if(treeMode != MODE_VIEW){
			super.clearTree();
			return;
		}


		rootView.removeAllChildren();
		viewNodesMap.clear();

	}
	
	protected void initModel() {	
		
		if(treeMode != MODE_VIEW){
			super.initModel();
			return;
		}

		// don't re-init anything
		if(rootView == null) {
			rootView = new DefaultMutableTreeNode();
			viewNodesMap = new HashMap<String, DefaultMutableTreeNode>(5);
		}

		// always try to remove the auxiliary node
		if(app.showAuxiliaryObjects && auxiliaryNode != null) {
			removeAuxiliaryNode();
		}

		// set the root
		model.setRoot(rootView);
		
	}
	
	protected void removeAuxiliaryNode(){
		if(auxiliaryNode.isNodeChild(rootDependency))
			super.removeAuxiliaryNode();
	}
	

	protected void setTreeLabels(){
		
		if(treeMode != MODE_VIEW){
			super.setTreeLabels();
			return;
		}
		
		DefaultMutableTreeNode node;
		for (String key : viewNodesMap.keySet()) {
			node = viewNodesMap.get(key);
			node.setUserObject(app.getPlain(key));
			model.nodeChanged(node);
		}
		
	}
	

}
