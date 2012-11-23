package geogebra.common.main.settings;


import java.util.LinkedList;

/**
 * Settings for the algebra view.
 */
public class AlgebraSettings extends AbstractSettings {
	
	private int treeMode = 1;
	
	private boolean showAuxiliaryObjects = false;

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
	}
	
	/**
	 * 
	 * @return if auxiliary objects have to be shown
	 */
	public boolean getShowAuxiliaryObjects() {
		return showAuxiliaryObjects;
	}
	
	
}
