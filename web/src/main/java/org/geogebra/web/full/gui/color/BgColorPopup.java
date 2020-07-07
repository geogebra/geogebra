package org.geogebra.web.full.gui.color;

import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.ButtonPopupMenu;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BgColorPopup extends ColorPopupMenuButton implements SetLabels {
	private FormLabel noColLbl;
	private Localization localization;

	/**
	 * @param app          {@link AppW}
	 * @param colorSetType {@code int}
	 * @param hasSlider    {@code boolean}
	 */
	public BgColorPopup(AppW app, int colorSetType, boolean hasSlider) {
		super(app, colorSetType, hasSlider);
		localization = app.getLocalization();
		// rearrange the content
		VerticalPanel panel = ((ButtonPopupMenu) getMyPopup()).getPanel();
		panel.clear();
		addNoColorButton(panel);
		panel.add(getMyTable());
		setLabels();
	}

	private void addNoColorButton(VerticalPanel panel) {
		FlowPanel noColBtn = new FlowPanel();
		noColBtn.addStyleName("noColBtn");
		noColBtn.add(new NoDragImage(MaterialDesignResources.INSTANCE.no_color(), 24));
		noColLbl = new FormLabel();
		noColBtn.add(noColLbl);
		noColBtn.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boolean needUndo = EuclidianStyleBarStatic.applyBgColor(app.getSelectionManager()
						 .getSelectedGeos(), null, 1);
				if (needUndo) {
					app.storeUndoInfo();
				}
				closePopup();
			}
		}, ClickEvent.getType());
		panel.add(noColBtn);
	}

	private void closePopup() {
		getMyPopup().hide();
	}

	@Override
	public void setLabels() {
		noColLbl.setText(localization.getMenu("noColor"));
	}
}
