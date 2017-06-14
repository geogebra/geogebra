package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.util.ClickStartHandler;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Suggestion bar for each AV item
 * 
 * @author Zbynek
 */
public class SuggestionBar extends FlowPanel {
	private Suggestion suggestion;

	/**
	 * @param loc
	 *            localization
	 * @param parentItem
	 *            parent tree item
	 */
	public SuggestionBar(Localization loc,
			final RadioTreeItem parentItem) {
		addStyleName("suggestionBar");
		Label solve = new Label(loc.getCommand("Solve"));
		solve.addStyleName("suggestionButton");
		add(solve);
		ClickStartHandler.init(solve, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				AsyncOperation<GeoElementND> run = new AsyncOperation<GeoElementND>() {
					@Override
					public void callback(GeoElementND geo) {
						executeSuggestion(geo);
					}
				};
				parentItem.runAfterGeoCreated(run);
				parentItem.onEnter(true);

			}
		});

	}

	/**
	 * Run this suggestion
	 * 
	 * @param geo
	 *            newly created geo
	 */
	void executeSuggestion(GeoElementND geo) {
		geo.getKernel().getAlgebraProcessor().processAlgebraCommand(
				"Solve[" + suggestion.getLabels(geo) + "]", true);

	}

	/**
	 * @param suggestion
	 *            suggestion
	 */
	public void setSuggestion(Suggestion suggestion) {
		this.suggestion = suggestion;
	}

}
