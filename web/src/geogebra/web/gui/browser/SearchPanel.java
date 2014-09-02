package geogebra.web.gui.browser;

import geogebra.common.main.Localization;
import geogebra.common.move.operations.NetworkOperation;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.html5.gui.FastButton;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.main.AppWeb;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Panel for Search in GeoGebraTube
 * 
 * @author Stefanie Bogner
 * 
 */

public class SearchPanel extends FlowPanel implements BooleanRenderable {
	
	public interface SearchListener {
		void onSearch(String query);
	}

//	private FlowPanel searchPanel;
	private TextBox query;
	private Label info;
	private final FastButton searchButton;
	private FastButton cancelButton;
	private final List<SearchListener> listeners;
	private NetworkOperation op;
	protected final Localization loc;

	public SearchPanel(AppWeb app) {
		this.setStyleName("searchDiv");
		this.listeners = new ArrayList<SearchListener>();
		this.loc = app.getLocalization();
		this.op = app.getNetworkOperation();

		this.query = new TextBox();
		//TODO translate Search material
		this.query.getElement().setAttribute("placeholder",loc.getMenu("search_geogebra_materials"));
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

		this.searchButton = new StandardButton(BrowseResources.INSTANCE.search());
		this.searchButton.addStyleName("searchButton");
		this.searchButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				doSearch();
			}
		});

		this.cancelButton = new StandardButton(BrowseResources.INSTANCE.dialog_cancel());
		this.cancelButton.addStyleName("cancelButton");
		this.cancelButton.setVisible(false);
		this.cancelButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onCancel();
			}
		});

		this.add(this.searchButton);
		this.add(this.query);
		this.add(this.cancelButton);
		
		this.info = new Label();

		if (this.op != null) {
			this.op.getView().add(this);
		}
		
		
		setLabels();
	}


	void doSearch() {
		fireSearchEvent();
		if (this.query.getText().equals("")) {
			this.cancelButton.setVisible(false);
		}
		this.query.setFocus(false);
	}

	void onCancel() {
		this.query.setFocus(false);
		this.cancelButton.setVisible(false);
		this.query.setText("");
		this.query.getElement().setAttribute("placeholder",loc.getMenu("search_geogebra_materials"));
	}

	void onFocusQuery() {
		this.query.setFocus(true);
		this.cancelButton.setVisible(true);
	}

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

//	private void setText(String string) {
//	    info.setText(string);
//    }

	public void setLabels() {
		render(this.op.getOnline());
	}

	public boolean addSearchListener(SearchListener searchListener) {
	    return this.listeners.add(searchListener);
    }

}
