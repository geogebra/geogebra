package org.geogebra.common.gui.dialog.options;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;

public abstract class OptionsAlgebra {
	protected static final List<SortMode> supportedModes = Arrays.asList(
			SortMode.DEPENDENCY,
			SortMode.TYPE, SortMode.ORDER, SortMode.LAYER);
}
