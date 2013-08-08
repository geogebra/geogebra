package geogebra.touch.gui.elements.ggt;

import geogebra.common.main.Localization;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.BrowseGUI;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.header.AuxiliaryHeaderPanel;
import geogebra.touch.gui.laf.LookAndFeel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

public class SearchBar extends AuxiliaryHeaderPanel {
	public interface SearchListener {
		void onSearch(String query);
	}

	private static LookAndFeel getLaf() {
		return TouchEntryPoint.getLookAndFeel();
	}

	Panel underline;
	TextBox query;
	private final StandardImageButton searchButton;
	StandardImageButton cancelButton;
	private final List<SearchListener> listeners;

	BrowseGUI browseGUI;

	public SearchBar(Localization loc, BrowseGUI browseGUI) {
		super(loc.getMenu("Worksheets"), loc);
		super.backPanel.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TouchEntryPoint.goBack();
			}
		}, ClickEvent.getType());
		this.browseGUI = browseGUI;
		this.searchPanel = new HorizontalPanel();
		this.listeners = new ArrayList<SearchListener>();

		this.query = new TextBox();
		this.query.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
					event.preventDefault();
					return;
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					SearchBar.this.fireSearchEvent();
					SearchBar.this.query.setFocus(false);
					if (SearchBar.this.query.getText().equals("")) {
						SearchBar.this.cancelButton.setVisible(false);
					}
					SearchBar.this.underline.removeStyleName("active");
					SearchBar.this.underline.addStyleName("inactive");
				}
			}
		});

		this.query.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				SearchBar.this.query.setFocus(true);
				SearchBar.this.cancelButton.setVisible(true);
				SearchBar.this.underline.removeStyleName("inactive");
				SearchBar.this.underline.addStyleName("active");
			}
		});

		this.query.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if (SearchBar.this.query.getText().equals("")) {
					SearchBar.this.query.setFocus(false);
					SearchBar.this.underline.removeStyleName("active");
					SearchBar.this.underline.addStyleName("inactive");
					SearchBar.this.cancelButton.setVisible(false);
				}
			}
		});

		this.searchButton = new StandardImageButton(getLaf().getIcons()
				.search());
		this.searchButton.addStyleName("searchButton");
		this.searchButton.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SearchBar.this.fireSearchEvent();
				SearchBar.this.query.setFocus(false);
				if (SearchBar.this.query.getText().equals("")) {
					SearchBar.this.cancelButton.setVisible(false);
				}
				SearchBar.this.underline.removeStyleName("active");
				SearchBar.this.underline.addStyleName("inactive");
			}
		}, ClickEvent.getType());

		this.cancelButton = new StandardImageButton(getLaf().getIcons()
				.dialog_cancel());
		this.cancelButton.addStyleName("cancelButton");
		this.cancelButton.setVisible(false);
		this.cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SearchBar.this.query.setFocus(false);
				SearchBar.this.query.setText("");
				SearchBar.this.underline.removeStyleName("active");
				SearchBar.this.underline.addStyleName("inactive");
				SearchBar.this.cancelButton.setVisible(false);
				SearchBar.this.browseGUI.loadFeatured();

				// gewünschtes verhalten für searchbar erst mit neuer version
				// von gwt
				// möglich - focus
				// if (SearchBar.this.query.getText().equals(""))
				// {
				// SearchBar.this.query.setFocus(false);
				// SearchBar.this.underline.removeStyleName("active");
				// SearchBar.this.underline.addStyleName("inactive");
				// SearchBar.this.cancelButton.setVisible(false);
				// }
				// else
				// {
				// SearchBar.this.query.setText("");
				// SearchBar.this.query.setFocus(true);
				// SearchBar.this.underline.removeStyleName("inactive");
				// SearchBar.this.underline.addStyleName("active");
				// SearchBar.this.browseGUI.loadFeatured();
				// }

			}
		});

		// Input Underline for Android
		this.underline = new LayoutPanel();
		this.underline.setStyleName("inputUnderline");
		this.underline.addStyleName("inactive");

		this.searchPanel.add(this.searchButton);
		this.searchPanel.add(this.query);
		this.searchPanel.add(this.cancelButton);

		this.rightPanel.add(this.searchPanel);
		this.rightPanel.add(this.underline);
	}

	public boolean addSearchListener(SearchListener l) {
		return this.listeners.add(l);
	}

	void fireSearchEvent() {
		for (final SearchListener s : this.listeners) {
			s.onSearch(this.query.getText());
		}
	}

	public void onResize() {

	}

	public boolean removeSearchListener(SearchListener l) {
		return this.listeners.remove(l);
	}
}
