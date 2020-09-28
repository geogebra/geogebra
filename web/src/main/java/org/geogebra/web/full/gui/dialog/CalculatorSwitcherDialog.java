package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Persistable;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Calculator chooser for suite
 */
public class CalculatorSwitcherDialog extends GPopupPanel implements Persistable, ResizeHandler {

	private StandardButton selectedBtn;

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

		SvgPerspectiveResources res = SvgPerspectiveResources.INSTANCE;
		StandardButton btnGraphing = buildCalcButton(res.menu_icon_algebra_transparent(),
				"GraphingCalculator.short");
		contentPanel.add(btnGraphing);
		selectedBtn = btnGraphing;

		StandardButton btn3D = buildCalcButton(res.menu_icon_graphics3D_transparent(),
				"GeoGebra3DGrapher.short");
		contentPanel.add(btn3D);

		StandardButton btnGeometry = buildCalcButton(res.menu_icon_geometry_transparent(),
				"Geometry");
		contentPanel.add(btnGeometry);

		StandardButton btnCAS = buildCalcButton(res.cas_white_bg(),
				"CAS");
		contentPanel.add(btnCAS);

		add(contentPanel);
	}

	private StandardButton buildCalcButton(SVGResource icon, String appNameKey) {
		 StandardButton button =  new StandardButton(app, 72, icon,
				 app.getLocalization().getMenu(appNameKey));
		button.setStyleName("calcBtn");
		button.addFastClickHandler(source -> {
			selectedBtn.removeStyleName("selected");
			button.addStyleName("selected");
			selectedBtn = button;
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