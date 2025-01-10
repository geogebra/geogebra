package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public abstract class MultipleGeosModel extends MultipleOptionsModel {
	private List<String> choices;

	public MultipleGeosModel(App app) {
		super(app);
		choices = new ArrayList<>();
	}

	/**
	 * @param loc
	 *            localization
	 * @return list of points
	 */
	public List<GeoElement> getGeoChoices(Localization loc) {
		TreeSet<GeoElement> points = app.getKernel().getPointSet();
		List<GeoElement> choices2 = new ArrayList<>();
		choices2.add(null);
		int count = 0;
		for (GeoElement p: points) {
			if (++count > MAX_CHOICES) {
				break;
			}
			choices2.add(p);
		}
		return choices2;
	}

	@Override
	public List<String> getChoices(Localization loc) {
		TreeSet<GeoElement> points = app.getKernel().getPointSet();
		choices.clear();
		choices.add("");
		int count = 0;
		for (GeoElement p: points) {
			if (++count > MAX_CHOICES) {
				break;
			}
			choices.add(p.getLabel(StringTemplate.editTemplate));
		}
		return choices;
	}

}
