package geogebra.phone.gui.view.algebra;

import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.AbstractViewPanel;
import geogebra.web.gui.view.algebra.AlgebraViewW;

import com.google.gwt.user.client.ui.ScrollPanel;

public class AlgebraViewPanel extends AbstractViewPanel {

	private AlgebraViewW algebraView;
	private ScrollPanel content;

	public AlgebraViewPanel(AppW app) {
		super(app);
		algebraView = (AlgebraViewW) app.getGuiManager().getAlgebraView();
		content = new ScrollPanel(algebraView);
		content.setStyleName("algebraView");
		add(content);
	}

	@Override
	protected String getViewPanelStyleName() {
		return "algebraViewPanel";
	}
}
