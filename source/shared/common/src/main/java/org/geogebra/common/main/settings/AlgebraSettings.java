package org.geogebra.common.main.settings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.algebra.AlgebraOutputFormatFilter;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

/**
 * Settings for the algebra view.
 */
public class AlgebraSettings extends AbstractSettings {

	private SortMode treeMode = SortMode.TYPE;

	private boolean showAuxiliaryObjects = false;
	private boolean modeChanged = false;
	private boolean engineeringNotationEnabled = false;
	private final Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters = new HashSet<>();

	private List<Integer> collapsedNodes = null;

	private int style = AlgebraStyle.VALUE;

	private boolean equationChangeByDragRestricted;

	private static final List<Integer> styleModes = Arrays.asList(
			AlgebraStyle.DEFINITION_AND_VALUE,
			AlgebraStyle.VALUE, AlgebraStyle.DEFINITION,
			AlgebraStyle.DESCRIPTION);
	private boolean angleConversionRestricted;

	/**
	 * @param listeners
	 *            settings listeners
	 */
	public AlgebraSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	/**
	 * New AV settings.
	 */
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

	/**
	 * @param val
	 *            tree grouping mode
	 */
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
	 * Enable or disable engineering notation. If enabled, the engineering notation may appear
	 * depending on the expression, if disabled it will never appear.
	 * @param enabled Whether to enable or disable the engineering notation.
	 */
	public void setEngineeringNotationEnabled(boolean enabled) {
		this.engineeringNotationEnabled = enabled;
	}

	/**
	 * @return {@code true} is engineering notation is enabled, {@code false} otherwise
	 */
	public boolean isEngineeringNotationEnabled() {
		return engineeringNotationEnabled;
	}

	/**
	 * Retrieves the set of {@link AlgebraOutputFormatFilter}s.
	 * @return the set of filters
	 */
	public @Nonnull Set<AlgebraOutputFormatFilter> getAlgebraOutputFormatFilters() {
		return algebraOutputFormatFilters;
	}

	/**
	 * Adds the {@link AlgebraOutputFormatFilter} to the set of filters (used to filter
	 * specific formats from the possible output formats of an element in the algebra view).
	 * @param algebraOutputFormatFilter the filter to be added
	 */
	public void addAlgebraOutputFormatFilter(
			@Nonnull AlgebraOutputFormatFilter algebraOutputFormatFilter) {
		algebraOutputFormatFilters.add(algebraOutputFormatFilter);
	}

	/**
	 * Removes the previously added {@link AlgebraOutputFormatFilter},
	 * undoing the effect of {@link AlgebraSettings#addAlgebraOutputFormatFilter}.
	 * @param algebraOutputFormatFilter the filter to be removed
	 */
	public void removeAlgebraOutputFormatFilter(
			@Nonnull AlgebraOutputFormatFilter algebraOutputFormatFilter) {
		algebraOutputFormatFilters.remove(algebraOutputFormatFilter);
	}

	/**
	 * set the collapsed nodes indices
	 * 
	 * @param collapsedNodes
	 *            array of indices
	 */
	public void setCollapsedNodes(List<Integer> collapsedNodes) {
		this.collapsedNodes = collapsedNodes;
		settingChanged();
	}

	public void setCollapsedNodesNoFire(List<Integer> collapsedNodes) {
		this.collapsedNodes = collapsedNodes;
	}

	/**
	 * 
	 * @return list of indices of collapsed nodes
	 */
	public List<Integer> getCollapsedNodes() {
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

	@Override
	public void resetDefaults() {
		super.resetDefaults();
		reset();
		modeChanged = false;
		style = AlgebraStyle.VALUE;
		notifyListeners();
	}

	/**
	 * @return whether description mode changed from XML
	 */
	public boolean isModeChanged() {
		return modeChanged;
	}

	/**
	 * @param modeChanged
	 *            whether tree mode was changed from XML
	 */
	public void setModeChanged(boolean modeChanged) {
		this.modeChanged = modeChanged;
	}

	/**
	 * @param app
	 *            application
	 * @return available description modes
	 */
	public static String[] getDescriptionModes(App app) {
		Localization loc = app.getLocalization();
		return !app.isDesktop()
				? new String[] { loc.getMenu("DefinitionAndValue"),
						loc.getMenu("Value"), loc.getMenu("Definition"),
						loc.getMenu("Description"), }
				: new String[] { loc.getMenu("Value"),
						loc.getMenu("Description"), loc.getMenu("Definition") };
	}

	/**
	 * Gives the algebra style mode of an index used in list-box.
	 * 
	 * @param idx
	 *            index in list-box
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

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	/**
	 * Prints settings to XML
	 * @param sb string builder
	 * @param showAuxiliaryObjects whether aux objects are visible in AV
	 */
	public void getXML(StringBuilder sb, boolean showAuxiliaryObjects) {
		sb.append("<algebraView>\n");
		sb.append("\t<mode val=\"");
		sb.append(getTreeMode().toInt());
		sb.append("\"/>\n");

		// auxiliary objects
		if (showAuxiliaryObjects) {
			sb.append("\t<auxiliary show=\"true\"/>");
		}

		// collapsed nodes
		appendCollapsedNodes(sb);
		sb.append("</algebraView>\n");
	}

	private void appendCollapsedNodes(StringBuilder sbXML) {
		if (collapsedNodes != null && !collapsedNodes.isEmpty()) {
			sbXML.append("\t<collapsed val=\"");
			sbXML.append(collapsedNodes.get(0));
			for (int i = 1; i < collapsedNodes.size(); i++) {
				sbXML.append(",");
				sbXML.append(collapsedNodes.get(i));
			}
			sbXML.append("\"/>\n");
		}
	}

	public boolean isEquationChangeByDragRestricted() {
		return equationChangeByDragRestricted;
	}

	public void setEquationChangeByDragRestricted(boolean equationChangeByDragRestricted) {
		this.equationChangeByDragRestricted = equationChangeByDragRestricted;
	}

	public boolean isAngleConversionRestricted() {
		return this.angleConversionRestricted;
	}

	public void setAngleConversionRestricted(boolean angleConversionRestricted) {
		this.angleConversionRestricted = angleConversionRestricted;
	}
}
