package org.geogebra.web.full.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.text.InlineTextController;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.contextmenu.FontSubMenu;
import org.geogebra.web.full.gui.dialog.HyperlinkDialog;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.javax.swing.InlineTextToolbar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;

public class InlineTextItems {
	private final Localization loc;
	private App app;
	private GeoElement geo;
	private GPopupMenuW wrappedPopup;
	private List<DrawInlineText> inlines;

	public InlineTextItems(App app, GeoElement geo, GPopupMenuW wrappedPopup) {
		this.app = app;
		this.loc = app.getLocalization();
		this.geo = geo;
		this.wrappedPopup = wrappedPopup;
		inlines = getInlineTexts();
	}

	void addItems() {
		if (inlines.isEmpty()) {
			return;
		}

		addInlineTextToolbar();
		addInlineTextSubmenu();
		addHyperlinkItems();
	}

	private void addInlineTextToolbar() {
		InlineTextToolbar toolbar = new InlineTextToolbar(inlines, app);
		wrappedPopup.addItem(toolbar, false);
	}

	private List<DrawInlineText> getInlineTexts() {
		if (!geo.hasGroup()) {
			return Collections.singletonList(getDrawableInlineText(geo));
		}

		Group group = geo.getParentGroup();
		List<DrawInlineText> inlines = new ArrayList<>();
		for (GeoElement geo: group.getGroupedGeos()) {
			DrawInlineText drawInlineText = getDrawableInlineText(geo);
			if (drawInlineText != null) {
				inlines.add(drawInlineText);
			}
		}

		return inlines;
	}

	private DrawInlineText getDrawableInlineText(GeoElement geo) {
		return geo instanceof GeoInlineText ? (DrawInlineText) app.getActiveEuclidianView()
				.getDrawableFor(geo) : null;
	}

	private void addInlineTextSubmenu() {
		AriaMenuItem item = new AriaMenuItem(loc.getMenu("ContextMenu.Font"),
				false,
				new FontSubMenu((AppW) app, getTextController()));
		item.addStyleName("no-image");
		wrappedPopup.addItem(item);
	}

	private void addItem(String text, Command command) {
		AriaMenuItem menuItem = new AriaMenuItem(loc.getMenu(text), false,
				command);
		menuItem.getElement().getStyle()
				.setPaddingLeft(16, Style.Unit.PX);
		wrappedPopup.addItem(menuItem);
	}

	private void addHyperlinkItems() {
		if (inlines.size() != 1) {
			return;
		}

		if (StringUtil.emptyOrZero(inlines.get(0).getHyperLinkURL())) {
			addHyperlinkItem("Link");
		} else {
			addHyperlinkItem("editLink");
			addRemoveHyperlinkItem();
		}
	}


	private void addHyperlinkItem(String labelTransKey) {
		Command addHyperlinkCommand = new Command() {
			@Override
			public void execute() {
				openHyperlinkDialog();
			}
		};

		addItem(labelTransKey, addHyperlinkCommand);
	}

	private void  openHyperlinkDialog() {
		HyperlinkDialog hyperlinkDialog = new HyperlinkDialog((AppW) app,
				getTextController());
		hyperlinkDialog.center();
	}

	private InlineTextController getTextController() {
		DrawInlineText inlineText = (DrawInlineText) app.getActiveEuclidianView()
				.getDrawableFor(geo);
		return inlineText.getTextController();
	}

	private void addRemoveHyperlinkItem() {
		Command addRemoveHyperlinkCommand = new Command() {
			@Override
			public void execute() {
				getTextController().setHyperlinkUrl(null);
			}
		};

		addItem("removeLink", addRemoveHyperlinkCommand);
	}

}
