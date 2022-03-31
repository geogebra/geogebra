package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LogoAndName implements IsWidget, SetLabels {

	private static final int LOGO_MARGIN = 72; // 24px top + 48px bottom
	private final Widget panel;
	private final Label name;
	private final App app;

	/**
	 * @param app application
	 */
	public LogoAndName(App app) {
		this.app = app;
		name = new Label();
		NoDragImage icon = new NoDragImage(((AppWFull) app).getActivity().getIcon(),
				24);
		panel = LayoutUtilW.panelRow(icon, name);
		panel.addStyleName("avNameLogo");
		setLabels();
	}

	@Override
	public void setLabels() {
		name.setText(app.getLocalization().getMenu(
				app.getConfig().getAppTransKey()));
	}

	@Override
	public Widget asWidget() {
		return panel;
	}

	/**
	 * @param aView algebra view
	 * @param parentHeight parent panel height in pixels
	 */
	public void onResize(AlgebraViewW aView, int parentHeight) {
		AppW app = aView.getApp();
		boolean showLogo = !app.getAppletFrame().isKeyboardShowing();
		panel.setVisible(showLogo);
		if (showLogo) {
			int minHeight = parentHeight - panel.getOffsetHeight() - LOGO_MARGIN;
			aView.getElement().getStyle().setProperty("minHeight", minHeight + "px");
		} else {
			aView.getElement().getStyle().clearProperty("minHeight");
		}
	}
}
