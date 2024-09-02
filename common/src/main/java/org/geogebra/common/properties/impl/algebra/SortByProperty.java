package org.geogebra.common.properties.impl.algebra;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.geogebra.common.gui.view.algebra.AlgebraView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * Property for setting sort by in Algebra view.
 */
public class SortByProperty extends AbstractNamedEnumeratedProperty<AlgebraView.SortMode> {

	private AlgebraSettings algebraSettings;

	/**
	 * Constructs a sort by property object.
	 * @param algebraSettings algebra view
	 * @param localization localization
	 */
	public SortByProperty(AlgebraSettings algebraSettings, Localization localization) {
		super(localization, "SortBy");
		this.algebraSettings = algebraSettings;
		setNamedValues(Arrays.stream(AlgebraView.SortMode.values()).collect(Collectors.toMap(
				mode -> mode, Enum::toString)));
	}

	@Override
	public AlgebraView.SortMode getValue() {
		return algebraSettings.getTreeMode();
	}

	@Override
	protected void doSetValue(AlgebraView.SortMode value) {
		algebraSettings.setTreeMode(value);
	}
}
