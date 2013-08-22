package geogebra.touch.gui.elements.header;

import geogebra.common.main.Localization;
import geogebra.common.move.operations.NetworkOperation;
import geogebra.common.move.views.BooleanRenderable;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.BrowseGUI;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.algebra.events.FastClickHandler;
import geogebra.touch.gui.elements.FastButton;
import geogebra.touch.gui.elements.StandardButton;
import geogebra.touch.gui.laf.LookAndFeel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

public class BrowseHeaderPanel extends AuxiliaryHeaderPanel implements
		ResizeListener, BooleanRenderable {

	public interface SearchListener {
		void onSearch(String query);
	}

	private static LookAndFeel getLaf() {
		return TouchEntryPoint.getLookAndFeel();
	}

	private Panel underline;
	private TextBox query;
	private final FastButton searchButton;
	private FastButton cancelButton;
	private final List<SearchListener> listeners;
	private BrowseGUI browseGUI;
	private NetworkOperation op;

	public BrowseHeaderPanel(final Localization loc, final BrowseGUI browseGUI,
			NetworkOperation op) {
		super(loc);

		this.backButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				TouchEntryPoint.goBack();
			}
		});

		this.browseGUI = browseGUI;
		this.searchPanel = new HorizontalPanel();
		this.listeners = new ArrayList<SearchListener>();

		this.query = new TextBox();
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

		this.searchButton = new StandardButton(getLaf().getIcons()
				.search());
		this.searchButton.addStyleName("searchButton");
		this.searchButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				doSearch();
			}
		});

		this.cancelButton = new StandardButton(getLaf().getIcons()
				.dialog_cancel());
		this.cancelButton.addStyleName("cancelButton");
		this.cancelButton.setVisible(false);
		this.cancelButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onCancel();
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

		this.op = op;
		op.getView().add(this);
		setLabels();
	}

	protected void doSearch() {
		if (!this.query.getText().equals("")) {
			fireSearchEvent();
		} else {
			this.cancelButton.setVisible(false);
		}
		this.query.setFocus(false);
		this.underline.removeStyleName("active");
		this.underline.addStyleName("inactive");
	}

	protected void onCancel() {
		this.query.setFocus(false);
		this.query.setText("");
		this.underline.removeStyleName("active");
		this.underline.addStyleName("inactive");
		this.cancelButton.setVisible(false);
		this.browseGUI.loadFeatured();
	}

	protected void onFocusQuery() {
		this.query.setFocus(true);
		this.cancelButton.setVisible(true);
		this.underline.removeStyleName("inactive");
		this.underline.addStyleName("active");
	}

	protected void onBlurQuery() {
		if (this.query.getText().equals("")) {
			this.query.setFocus(false);
			this.underline.removeStyleName("active");
			this.underline.addStyleName("inactive");
			this.cancelButton.setVisible(false);
		}
	}

	public boolean addSearchListener(final SearchListener l) {
		return this.listeners.add(l);
	}

	public boolean removeSearchListener(final SearchListener l) {
		return this.listeners.remove(l);
	}

	private void fireSearchEvent() {
		for (final SearchListener s : this.listeners) {
			s.onSearch(this.query.getText());
		}
	}

	@Override
	public void onResize() {
		this.setWidth(Window.getClientWidth() + "px");
	}

	@Override
	public void render(boolean b) {
		this.setText(this.loc.getMenu("Worksheets") + (b ? "" : " (Offline)"));

	}

	@Override
	public void setLabels() {
		super.setLabels();
		render(this.op.getOnline());
	}
}
