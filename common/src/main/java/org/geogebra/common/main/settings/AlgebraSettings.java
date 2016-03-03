package org.geogebra.common.main.settings;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

/**
 * Settings for the algebra view.
 */
public class AlgebraSettings extends AbstractSettings {

	private SortMode treeMode = SortMode.TYPE;

	private boolean showAuxiliaryObjects = false;
	private boolean modeChanged = false;
	private int[] collapsedNodes = null;

	private static List<Integer> styleModes = Arrays.asList(
			Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE,
			Kernel.ALGEBRA_STYLE_VALUE, Kernel.ALGEBRA_STYLE_DEFINITION,
			Kernel.ALGEBRA_STYLE_DESCRIPTION);

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

	public static String[] getDescriptionModes(App app) {
		return app.has(Feature.AV_DEFINITION_AND_VALUE)
				? new String[] { app.getPlain("DefinitionAndValue"),
						app.getPlain("Value"), app.getPlain("Definition"),
						app.getPlain("Description"), }
				: new String[] { app.getPlain("Value"),
						app.getPlain("Definition"), app.getPlain("Command") };
	}

	/**
	 * Gives the algebra style mode of an index used in listboxs.
	 * 
	 * @param idx
	 *            listbox index
	 * @return The style mode of that index.
	 */
	public static int getStyleModeAt(int idx) {
		if (idx < 0 || idx > styleModes.size()) {
			return -1;
		}
		return styleModes.get(idx);
	}

	/**
	 * Gives the index of an algebra style mode within a listbox;
	 * 
	 * @param mode
	 *            The algebra style mode.
	 * @return The index of the mode.
	 */
	public static int indexOfStyleMode(int mode) {
		return styleModes.indexOf(mode);
	}
}
