package geogebra.common.main.settings;


import geogebra.common.main.App;

import java.util.LinkedList;

/**
 * Settings for the algebra view.
 */
public class AlgebraSettings extends AbstractSettings {
	
	private int treeMode = 1;
	
	private boolean showAuxiliaryObjects = false;
	
	private int[] collapsedNodes = null;

	public AlgebraSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	public AlgebraSettings() {
		super();
	}

	/**
	 * set tree mode (as int value)
	 * @param val value
	 */
	public void setTreeMode(int val) {
		treeMode = val;
		settingChanged();
	}
	
	/**
	 * 
	 * @return tree mode (as int value)
	 */
	public int getTreeMode(){
		return treeMode;
	}
	
	/**
	 * set if auxiliary objects have to be shown
	 * @param flag flag
	 */
	public void setShowAuxiliaryObjects(boolean flag) {
		showAuxiliaryObjects = flag;
		settingChanged();
	}
	
	/**
	 * 
	 * @return if auxiliary objects have to be shown
	 */
	public boolean getShowAuxiliaryObjects() {
		return showAuxiliaryObjects;
	}
	
	/**
	 * set the collapsed nodes indices
	 * @param collapsedNodes array of indices
	 */
	public void setCollapsedNodes(int[] collapsedNodes){
		this.collapsedNodes=collapsedNodes;
		settingChanged();
	}
	
	/**
	 * 
	 * @return array of indices of collapsed nodes
	 */
	public int[] getCollapsedNodes(){
		return this.collapsedNodes;
	}

	
	
}
