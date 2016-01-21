package org.geogebra.common.main.settings;

import java.util.LinkedList;

import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;

/**
 * Settings for the algebra view.
 */
public class AlgebraSettings extends AbstractSettings {

	private SortMode treeMode = SortMode.TYPE;

	private boolean showAuxiliaryObjects = false;
	private boolean modeChanged = false;
	private int[] collapsedNodes = null;

	public AlgebraSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	public AlgebraSettings() {
		super();
	}

	/**
	 * set tree mode (as int value)
	 * 
	 * @param val
	 *            value
	 */
	public void setTreeMode(int val) {
		treeMode = SortMode.fromInt(val);
		settingChanged();
	}

	public void setTreeMode(SortMode val) {
		treeMode = val;
		settingChanged();
	}

	/**
	 * 
	 * @return tree mode (as int value)
	 */
	public SortMode getTreeMode() {
		return treeMode;
	}

	/**
	 * set if auxiliary objects have to be shown
	 * 
	 * @param flag
	 *            flag
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
	 * 
	 * @param collapsedNodes
	 *            array of indices
	 */
	public void setCollapsedNodes(int[] collapsedNodes) {
		this.collapsedNodes = collapsedNodes;
		settingChanged();
	}

	/**
	 * 
	 * @return array of indices of collapsed nodes
	 */
	public int[] getCollapsedNodes() {
		return this.collapsedNodes;
	}

	/**
	 * reset the settings
	 */
	public void reset() {
		treeMode = SortMode.TYPE;
		showAuxiliaryObjects = false;
		collapsedNodes = null;
	}

	public boolean isModeChanged() {
		return modeChanged;
	}

	public void setModeChanged(boolean modeChanged) {
		this.modeChanged = modeChanged;
	}

}
