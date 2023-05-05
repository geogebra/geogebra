package org.geogebra.common.properties.impl.algebra;

import java.util.Arrays;

import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * Property for setting sort by in Algebra view.
 */
public class SortByProperty extends AbstractNamedEnumeratedProperty<AlgebraView.SortMode> {

	private AlgebraView algebraView;

	/**
	 * Constructs a sort by property object.
	 * @param algebraView algebra view
	 * @param localization localization
	 */
	public SortByProperty(AlgebraView algebraView, Localization localization) {
		super(localization, "SortBy");
		this.algebraView = algebraView;
		setupValues();
	}

	private void setupValues() {
		setValues(AlgebraView.SortMode.DEPENDENCY, AlgebraView.SortMode.TYPE,
				AlgebraView.SortMode.ORDER, AlgebraView.SortMode.LAYER);
		setValueNames(Arrays.stream(values).map(v -> v.toString()).toArray(String[]::new));
	}

	@Override
	public AlgebraView.SortMode getValue() {
		return algebraView.getTreeMode();
	}

	@Override
	protected void doSetValue(AlgebraView.SortMode value) {
		algebraView.setTreeMode(value);
	}
}
