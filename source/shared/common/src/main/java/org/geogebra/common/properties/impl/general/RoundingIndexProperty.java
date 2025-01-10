package org.geogebra.common.properties.impl.general;

import static java.util.Map.entry;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.geogebra.common.gui.menubar.RoundingOptions;
import org.geogebra.common.kernel.Kernel;
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
	private final RoundingOptions roundingOptions;
	private int figuresIndex;

	/**
	 * Constructs a rounding property.
	 * @param app app
	 * @param localization localization
	 */
	public RoundingIndexProperty(App app, Localization localization) {
		super(localization, "Rounding");
		this.app = app;
		this.roundingOptions = new RoundingOptions(localization);
		setupValues(localization);
	}

	private void setupValues(Localization localization) {
		String[] roundingMenu = localization.getRoundingMenu();

		LinkedList<String> valueNames = new LinkedList<>(Arrays.asList(roundingMenu));
		figuresIndex = valueNames.indexOf(Localization.ROUNDING_MENU_SEPARATOR);
		valueNames.remove(figuresIndex);

		setNamedValues(IntStream.range(0, valueNames.size()).boxed()
				.map(index -> entry(index, valueNames.get(index)))
				.collect(Collectors.toList()));
	}

	@Override
	public Integer getValue() {
		return getMenuDecimalPosition(app.getKernel(), true);
	}

	@Override
	protected void doSetValue(Integer value) {
		boolean figures = value >= figuresIndex;
		setRounding(app, figures ? value + 1 : value, figures);
	}

	@Override
	public String[] getValueNames() {
		setupValues(app.getLocalization());
		return super.getValueNames();
	}

	/**
	 *
	 * @param kernel
	 *            kernel
	 * @param skipSeparator
	 *            whether to skip the separator between DP and SF
	 * @return position in rounding menu regarding current kernel settings
	 */
	final public int getMenuDecimalPosition(Kernel kernel,
			boolean skipSeparator) {
		int pos = -1;

		if (kernel.useSignificantFigures) {
			int figures = kernel.getPrintFigures();
			if (figures > 0 && figures < roundingOptions.figuresLookupLength()) {
				pos = roundingOptions.figuresLookup(figures) - (skipSeparator ? 1 : 0);
			}
		} else {
			int decimals = kernel.getPrintDecimals();

			if (decimals >= 0 && decimals < roundingOptions.decimalsLookupLength()) {
				pos = roundingOptions.decimalsLookup(decimals);
			}
		}

		return pos;
	}

	private void setRounding(App app, int id, boolean figures) {
		Kernel kernel = app.getKernel();
		int rounding = roundingOptions.roundingMenuLookup(id);
		if (figures) {
			kernel.setPrintFigures(rounding);
		} else {
			kernel.setPrintDecimals(rounding);
		}

		kernel.updateConstruction(false);
		app.refreshViews();

		// see https://geogebra-jira.atlassian.net/browse/TRAC-2575
		kernel.updateConstruction(false);

		app.setUnsaved();
	}
}
