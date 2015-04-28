package org.geogebra.web.web.gui;

import java.util.ArrayList;
import java.util.Date;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.Language;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class LanguageGUI extends MyHeaderPanel implements SetLabels {

	final AppW app;
	private LanguageHeaderPanel header;
	private Label activeLanguage = new Label();
	private FlowPanel fp = new FlowPanel();
	private ArrayList<Label> labels;
	private int cols;

	public LanguageGUI(AppW app) {
		this.app = app;
		this.setStyleName("languageGUI");
		addHeader();
		addContent();
	}

	private void addContent() {
		fp.setStyleName("contentPanel");

		labels = new ArrayList<Label>();
		cols = estimateCols();
		for (Language l : Language.values()) {
			if (!l.fullyTranslated && app.has(Feature.ALL_LANGUAGES)) {
				continue;
			}

			StringBuilder sb = new StringBuilder();

			String text = l.name;

			if (text != null) {

				char ch = text.toUpperCase().charAt(0);
				if (ch == Unicode.LeftToRightMark
				        || ch == Unicode.RightToLeftMark) {
					ch = text.charAt(1);
				} else {
					// make sure brackets are correct in Arabic, ie not )US)
					sb.setLength(0);
					sb.append(Unicode.LeftToRightMark);
					sb.append(text);
					sb.append(Unicode.LeftToRightMark);
					text = sb.toString();
				}

				final Label label = new Label(text);
				final Language current = l;

				if (current.localeGWT.equals(app.getLocalization()
				        .getLocaleStr())) {
					this.activeLanguage = label;
					activeLanguage.addStyleName("activeLanguage");
				}
				label.addClickHandler(getHandler(current, label));
				labels.add(label);
			}
		}
		placeLabels();

		this.setContentWidget(fp);
	}

	private void placeLabels() {
		int rows = labels.size() / cols;
		for (int i = 0; i < rows * cols; i++) {
			int col = i % cols;
			int row = i / cols;
			fp.add(labels.get(col * rows + row));
		}
		for (int i = 0; i < labels.size(); i++) {
			if (labels.get(i).getParent() == fp) {
				continue;
			}
			fp.add(labels.get(i));
		}
		FlowPanel clear = new FlowPanel();
		clear.setStyleName("clear");
		fp.add(clear);

	}

	public void onResize() {
		int newCols = estimateCols();
		if (newCols != cols) {
			cols = newCols;
			fp.clear();
			placeLabels();
		}
		super.onResize();
	}

	private int estimateCols() {
		return Math.max(1, (int) (app.getWidth() - 20) / 350);
	}

	private ClickHandler getHandler(final Language current, final Label label) {
		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boolean newDirRTL = Localization
				        .rightToLeftReadingOrder(current.localeGWT);
				Date exp = new Date(System.currentTimeMillis() + 1000 * 60 * 60
				        * 24 * 365);
				Cookies.setCookie("GeoGebraLangUI", current.localeGWT, exp,
				        "geogebra.org", "/", false);
				if (app.getLoginOperation().isLoggedIn()) {
					app.getLoginOperation()
					        .getGeoGebraTubeAPI()
					        .setUserLanguage(
					                current.localeGWT,
					                app.getLoginOperation().getModel()
					                        .getLoginToken());
				}

				app.setUnsaved();

				// On changing language from LTR/RTL the page will
				// reload.
				// The current workspace will be saved, and load
				// back after page reloading.
				// Otherwise only the language will change, and the
				// setting related with language.
				if (newDirRTL != app.getLocalization().rightToLeftReadingOrder) {
					// TODO change direction
				}
				app.setLanguage(current.localeGWT);
				LanguageGUI.this.setActiveLabel(label);
				LanguageGUI.this.close();
			}
		};
	}

	protected void setActiveLabel(Label label) {
		activeLanguage.removeStyleName("activeLanguage");
		activeLanguage = label;
		activeLanguage.addStyleName("activeLanguage");

	}

	native static JavaScriptObject saveBase64ToLocalStorage() /*-{
		return function(base64) {
			try {
				localStorage.setItem("reloadBase64String", base64);
				@org.geogebra.web.web.gui.app.GeoGebraAppFrame::removeCloseMessage()();
			} catch (e) {
				@org.geogebra.common.main.App::debug(Ljava/lang/String;)("Base64 sting not saved in local storage");
			} finally {
				$wnd.location.reload();
			}
		}
	}-*/;

	private void addHeader() {
		this.header = new LanguageHeaderPanel(app.getLocalization(), this);

		this.setHeaderWidget(this.header);
		// this.addResizeListener(this.header);

	}

	@Override
	public void setLabels() {
		if (this.header != null) {
			this.header.setLabels();
		}
	}

	@Override
	public AppW getApp() {
		return app;
	}
}
