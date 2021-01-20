package org.geogebra.web.full.gui.browser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.operations.NetworkOperation;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SharedResources;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel for Search in GeoGebraTube
 *
 * @author Stefanie Bogner
 *
 */

public class SearchPanel extends FlowPanel
		implements BooleanRenderable, SetLabels {

	private TextBox query;
	private StandardButton cancelButton;
	private final List<SearchListener> listeners;
	private NetworkOperation op;
	private final Localization loc;

	/**
	 * Handles search queries
	 */
	public interface SearchListener {
		/**
		 * @param query
		 *            search query
		 */
		void onSearch(String query);
	}

	/**
	 * @param app
	 *            application
	 */
	public SearchPanel(AppW app) {
		this.setStyleName("searchDiv");
		this.listeners = new ArrayList<>();
		this.loc = app.getLocalization();
		this.op = app.getNetworkOperation();

		this.query = new GTextBox();

		this.query.addStyleName("searchTextBox");
		this.query.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(final KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
					event.preventDefault();
					return;
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					doSearch();
				}
			}
		});

		this.query.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(final FocusEvent event) {
				onFocusQuery();
			}
		});

		this.query.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(final BlurEvent event) {
				onBlurQuery();
			}
		});

		StandardButton searchButton = new StandardButton(
				BrowseResources.INSTANCE.search());
		searchButton.addStyleName("searchButton");
		searchButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				doSearch();
			}
		});

		this.cancelButton = new StandardButton(
				SharedResources.INSTANCE.dialog_cancel());
		this.cancelButton.addStyleName("cancelButton");
		this.cancelButton.setVisible(false);
		this.cancelButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				onCancel();
			}
		});

		this.add(searchButton);
		this.add(this.query);
		this.add(this.cancelButton);

		if (this.op != null) {
			this.op.getView().add(this);
		}

		setLabels();
	}

	/**
	 * Handle search start
	 */
	void doSearch() {
		fireSearchEvent();
		if (this.query.getText().equals("")) {
			this.cancelButton.setVisible(false);
		}
		this.query.setFocus(false);
	}

	/**
	 * Handle cancel click
	 */
	void onCancel() {
		this.query.setFocus(false);
		this.cancelButton.setVisible(false);
		this.query.setText("");
		this.query.getElement().setAttribute("placeholder",
				loc.getMenu("search_geogebra_materials"));
	}

	/**
	 * Handle query field focus
	 */
	void onFocusQuery() {
		this.query.setFocus(true);
		this.cancelButton.setVisible(true);
	}

	/**
	 * Handle query field blur
	 */
	void onBlurQuery() {
		if (this.query.getText().equals("")) {
			this.query.setFocus(false);
			this.cancelButton.setVisible(false);
		}
	}

	private void fireSearchEvent() {
		for (final SearchListener s : this.listeners) {
			s.onSearch(this.query.getText());
		}
	}

	@Override
	public void render(boolean b) {
		/*this.setText("Infotext" + (b ? "" : " (Offline)"));*/

	}

	@Override
	public void setLabels() {
		this.query.getElement().setAttribute("placeholder",
				loc.getMenu("search_geogebra_materials"));
		render(this.op.isOnline());
	}

	/**
	 * @param searchListener
	 *            search listener
	 */
	public void addSearchListener(SearchListener searchListener) {
		listeners.add(searchListener);
	}
}
