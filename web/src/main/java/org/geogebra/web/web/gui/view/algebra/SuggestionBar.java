package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class SuggestionBar extends FlowPanel {
	private Suggestion suggestion;

	public SuggestionBar(final Kernel kernel, Localization loc,
			final RadioTreeItem callback) {
		addStyleName("suggestionBar");
		StandardButton solve = new StandardButton(loc.getCommand("Solve"));
		solve.addStyleName("suggestionButton");
		add(solve);
		solve.addFastClickHandler(new FastClickHandler() {

			public void onClick(Widget source) {
				AsyncOperation<GeoElementND> run = new AsyncOperation<GeoElementND>() {
					@Override
					public void callback(GeoElementND geo) {
						Log.debug("RUN WITH" + suggestion.getLabels(geo));
						kernel.getAlgebraProcessor().processAlgebraCommand(
								"Solve[" + suggestion.getLabels(geo) + "]",
								true);

					}
				};
				callback.runAfterGeoCreated(run);
			}
		});

	}

	public void setSuggestion(Suggestion suggestion) {
		this.suggestion = suggestion;
	}

}
