package geogebra3D.gui.view.algebra;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.gui.view.algebra.AlgebraControllerD;
import geogebra.gui.view.algebra.AlgebraViewD;
import geogebra3D.App3D;

import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Algebra view for 3D : change display regarding graphic view selected
 * 
 * @author mathieu
 *
 */
public class AlgebraView3D extends AlgebraViewD {

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
	 * @param algCtrl algebra controller
	 */
	public AlgebraView3D(AlgebraControllerD algCtrl) {
		super(algCtrl);
	}
	
	
	@Override
	protected DefaultMutableTreeNode getParentNode(GeoElement geo,int forceLayer){
		
		if(!treeMode.equals(SortMode.VIEW))
			return super.getParentNode(geo,forceLayer);
		
		DefaultMutableTreeNode parent;
		
		// get view node
		EuclidianViewInterfaceCommon view = (EuclidianViewInterfaceCommon)geo.getViewForValueString();
		if (view==null){
			if (geo.isGeoElement3D())
				view = ((App3D) app).getEuclidianView3D();
			else
				view = app.getActiveEuclidianView();
		}
		
		String viewString = view.getFromPlaneString();
		
		parent = viewNodesMap.get(viewString);

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

	@Override
	protected void clearTree() {
		
		if(!treeMode.equals(SortMode.VIEW)){
			super.clearTree();
			return;
		}


		rootView.removeAllChildren();
		viewNodesMap.clear();

	}
	
	@Override
	protected void initModel() {	
		
		if(!treeMode.equals(SortMode.VIEW)){
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
	
	@Override
	protected void removeAuxiliaryNode(){
		if(auxiliaryNode.isNodeChild(rootDependency))
			super.removeAuxiliaryNode();
	}
	
	@Override
	protected void setTreeLabels(){
		
		if(!treeMode.equals(SortMode.VIEW)){
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
