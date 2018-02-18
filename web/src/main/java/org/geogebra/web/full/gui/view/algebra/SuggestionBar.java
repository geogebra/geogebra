package org.geogebra.web.full.gui.view.algebra;

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
	/**
	 * suggetion
	 */
	Suggestion suggestion;
	private Label label;

	/**
	 * @param parentItem
	 *            parent tree item
	 */
	public SuggestionBar(final RadioTreeItem parentItem) {
		addStyleName("suggestionBar");
		addStyleName("animating");
		label = new Label();
		add(label);
		ClickStartHandler.init(label, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				AsyncOperation<GeoElementND> run = new AsyncOperation<GeoElementND>() {
					@Override
					public void callback(GeoElementND geo) {
						suggestion.execute(geo);
					}
				};

				parentItem.runAfterGeoCreated(run, suggestion.isAutoSlider());
				parentItem.onEnter(true);

			}
		});

	}

	/**
	 * @param suggestion
	 *            suggestion
	 * @param loc
	 *            - localization
	 */
	public void setSuggestion(Suggestion suggestion, Localization loc) {
		this.suggestion = suggestion;
		label.getElement().removeAllChildren();
		label.setText(suggestion.getCommand(loc));
		label.addStyleName("suggestionButton");
		label.removeStyleName("suggestionButtonIcon");

	}
}
