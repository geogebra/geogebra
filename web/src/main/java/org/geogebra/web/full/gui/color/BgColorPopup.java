package org.geogebra.web.full.gui.color;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianStyleBarSelection;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.ButtonPopupMenu;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BgColorPopup extends ColorPopupMenuButton implements SetLabels {
	private FormLabel noColLbl;
	private Localization localization;
	private final EuclidianStyleBarSelection selection;

	/**
	 * @param app          {@link AppW}
	 * @param colorSetType {@code int}
	 * @param hasSlider    {@code boolean}
	 * @param selection selected geos
	 */
	public BgColorPopup(AppW app, int colorSetType, boolean hasSlider,
			EuclidianStyleBarSelection selection) {
		super(app, colorSetType, hasSlider);
		localization = app.getLocalization();
		this.selection = selection;
		buildGUI();
	}

	private void buildGUI() {
		VerticalPanel panel = ((ButtonPopupMenu) getMyPopup()).getPanel();
		panel.clear();
		addFirstPanel(panel);
		panel.add(getMyTable());
		setLabels();
	}

	/**
	 * adds panel above the color chooser panel
	 * @param panel - popup panel
	 */
	public void addFirstPanel(VerticalPanel panel) {
		FlowPanel noColBtn = new FlowPanel();
		noColBtn.addStyleName("noColBtn");
		noColBtn.add(new NoDragImage(MaterialDesignResources.INSTANCE.no_color(), 24));
		noColLbl = new FormLabel();
		noColBtn.add(noColLbl);
		noColBtn.addDomHandler(event -> {
			List<GeoElement> geos = selection.getGeos();
			boolean needUndo = EuclidianStyleBarStatic.applyBgColor(geos, null, 1);
			if (needUndo) {
				app.storeUndoInfo();
			}
			closePopup();
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

	/**
	 * selected geos
	 * @return euclidian selection
	 */
	public EuclidianStyleBarSelection getSelection() {
		return selection;
	}
}
