package org.geogebra.common.properties.impl.general;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.IntStream;

import org.geogebra.common.gui.menubar.OptionsMenu;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

import com.google.j2objc.annotations.Weak;

/**
 * Property for setting the rounding.
 */
public class RoundingIndexProperty extends AbstractNamedEnumeratedProperty<Integer> {

	@Weak
	private App app;
	private OptionsMenu optionsMenu;
	private int figuresIndex;

	/**
	 * Constructs a rounding property.
	 * @param app app
	 * @param localization localization
	 */
	public RoundingIndexProperty(App app, Localization localization) {
		super(localization, "Rounding");
		this.app = app;
		this.optionsMenu = new OptionsMenu(localization);
		setupValues(localization);
	}

	private void setupValues(Localization localization) {
		String[] roundingMenu = localization.getRoundingMenu();

		LinkedList<String> valueNames = new LinkedList<>(Arrays.asList(roundingMenu));
		figuresIndex = valueNames.indexOf(Localization.ROUNDING_MENU_SEPARATOR);
		valueNames.remove(figuresIndex);

		setValues(IntStream.range(0, valueNames.size()).boxed().toArray(Integer[]::new));
		setValueNames(valueNames.toArray(new String[0]));
	}

	@Override
	public Integer getValue() {
		return optionsMenu.getMenuDecimalPosition(app.getKernel(), true);
	}

	@Override
	protected void doSetValue(Integer value) {
		boolean figures = value >= figuresIndex;
		optionsMenu.setRounding(app, figures ? value + 1 : value, figures);
	}
}
