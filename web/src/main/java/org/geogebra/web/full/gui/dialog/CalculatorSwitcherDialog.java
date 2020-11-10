package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Persistable;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Calculator chooser for suite
 */
public class CalculatorSwitcherDialog extends GPopupPanel implements Persistable, ResizeHandler {

	/**
	 * constructor
	 * @param app see {@link AppW}
	 */
	public CalculatorSwitcherDialog(AppW app) {
		super(true, app.getPanel(), app);
		setGlassEnabled(true);
		addStyleName("calcChooser");
		buildGUI();
		Window.addResizeHandler(this);
	}

	private void buildGUI() {
		FlowPanel contentPanel = new FlowPanel();
		Label title = new Label(app.getLocalization().getMenu("ChooseCalculator"));
		title.addStyleName("title");
		contentPanel.add(title);

		StandardButton btnGraphing = buildCalcButton(GeoGebraConstants.GRAPHING_APPCODE);
		contentPanel.add(btnGraphing);

		StandardButton btn3D = buildCalcButton(GeoGebraConstants.G3D_APPCODE);
		contentPanel.add(btn3D);

		StandardButton btnGeometry = buildCalcButton(GeoGebraConstants.GEOMETRY_APPCODE);
		contentPanel.add(btnGeometry);

		StandardButton btnCAS = buildCalcButton(GeoGebraConstants.CAS_APPCODE);
		contentPanel.add(btnCAS);

		add(contentPanel);
	}

	private StandardButton buildCalcButton(String subAppCode) {
		AppDescription description = AppDescription.get(subAppCode) ;
		String appNameKey = description.getNameKey();
		StandardButton button =  new StandardButton(app, 72, description.getIcon(),
				 app.getLocalization().getMenu(appNameKey));
		button.setStyleName("calcBtn");
		if (subAppCode.equals(app.getConfig().getSubAppCode())) {
			button.addStyleName("selected");
		}

		button.addFastClickHandler(source -> {
			hide();
			((AppWFull) app).setSuiteHeaderButton(subAppCode);
			((AppWFull) app).switchToSubapp(subAppCode);
		});
		return button;
	}

	@Override
	public void show() {
		super.show();
		super.center();
	}

	@Override
	public void onResize(ResizeEvent event) {
		if (isShowing()) {
			super.center();
		}
	}
}
