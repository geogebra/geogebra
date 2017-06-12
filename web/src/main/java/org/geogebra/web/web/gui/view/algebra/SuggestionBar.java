package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class SuggestionBar extends FlowPanel {
	public SuggestionBar(final GeoElement geo, Localization loc) {
		addStyleName("suggestionBar");
		StandardButton solve = new StandardButton(loc.getCommand("Solve"));
		solve.addStyleName("suggestionButton");
		add(solve);
		solve.addFastClickHandler(new FastClickHandler() {

			public void onClick(Widget source) {
				geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
						"Solve[" + geo.getLabelSimple() + "]", true);

			}
		});
	}
}
