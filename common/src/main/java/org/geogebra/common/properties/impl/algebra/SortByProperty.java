package org.geogebra.common.properties.impl.algebra;

import static java.util.Map.entry;
import static org.geogebra.common.gui.view.algebra.AlgebraView.SortMode.DEPENDENCY;
import static org.geogebra.common.gui.view.algebra.AlgebraView.SortMode.LAYER;
import static org.geogebra.common.gui.view.algebra.AlgebraView.SortMode.ORDER;
import static org.geogebra.common.gui.view.algebra.AlgebraView.SortMode.TYPE;

import java.util.List;

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
		setNamedValues(List.of(
				entry(DEPENDENCY, DEPENDENCY.name()),
				entry(TYPE, TYPE.name()),
				entry(ORDER, ORDER.name()),
				entry(LAYER, LAYER.name())
		));
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
