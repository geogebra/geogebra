package geogebra.touch.gui.elements.ggt;

import geogebra.common.euclidian.event.FocusListener;
import geogebra.common.main.Localization;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.dialogs.InputDialog;
import geogebra.touch.gui.elements.AuxiliaryHeaderPanel;
import geogebra.touch.gui.elements.StandardImageButton;
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
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchBar extends AuxiliaryHeaderPanel {
	public interface SearchListener {
		void onSearch(String query);
	}

	Panel underline;
	TextBox query;
	private StandardImageButton searchButton;
	private StandardImageButton cancelButton;
	private List<SearchListener> listeners;

	public SearchBar(Localization loc) {
		super(loc.getMenu("Worksheets"), loc);
		this.setStyleName("headerbar");
		
		this.searchPanel = new HorizontalPanel();

		this.listeners = new ArrayList<SearchListener>();
		
		this.query = new TextBox();
		this.query.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					SearchBar.this.fireSearchEvent();
				}
			}
		});
		this.query.addFocusHandler(new FocusHandler()
		{
			@Override
			public void onFocus(FocusEvent event)
			{
				SearchBar.this.query.setFocus(true);
				SearchBar.this.underline.removeStyleName("inactive");
				SearchBar.this.underline.addStyleName("active");
			}
		});
		this.query.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				SearchBar.this.query.setFocus(false);
				SearchBar.this.underline.removeStyleName("active");
				SearchBar.this.underline.addStyleName("inactive");
			}
		});

		this.searchButton = new StandardImageButton(getLaf().getIcons().search());
		this.searchButton.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SearchBar.this.fireSearchEvent();
			}
		}, ClickEvent.getType());
		
		this.cancelButton = new StandardImageButton(getLaf().getIcons().dialog_cancel());
		
		this.cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				//TODO: Clear search, when cancel is clicked
			}
		});
		
		//Input Underline for Android
		this.underline = new LayoutPanel();
		this.underline.setStyleName("inputUnderline");
		this.underline.addStyleName("inactive");

		this.searchPanel.add(this.searchButton);
		this.searchPanel.add(this.query);
		this.searchPanel.add(this.cancelButton);
		
		this.queryPanel.add(this.searchPanel);
		this.queryPanel.add(this.underline);
	}

	public boolean addSearchListener(SearchListener l) {
		return this.listeners.add(l);
	}

	public boolean removeSearchListener(SearchListener l) {
		return this.listeners.remove(l);
	}

	void fireSearchEvent() {
		for (SearchListener s : this.listeners) {
			s.onSearch(this.query.getText());
		}
	}

	public void setWidth(int width) {
		//this.query.setWidth(width/2 - 100 + "px");
		//TODO: Do we need this? It destroys my design
	}

	public void onResize(ResizeEvent event) {
		this.setWidth(event.getWidth());
	}
	
	private static LookAndFeel getLaf() {
		return TouchEntryPoint.getLookAndFeel();
	}
}
