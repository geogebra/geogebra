package org.geogebra.web.phone.gui.view.algebra;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.gui.view.AbstractViewPanel;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;

import com.google.gwt.user.client.ui.ScrollPanel;

public class AlgebraViewPanel extends AbstractViewPanel {

	private AlgebraViewW algebraView;
	private ScrollPanel content;

	/**
	 * @param app
	 *            {@link AppW}
	 * @param algebraView
	 *            {@link AlgebraViewW}
	 */
	public AlgebraViewPanel(AppW app, AlgebraViewW algebraView) {
		super(app);
		this.algebraView = algebraView;
		this.content = new ScrollPanel(this.algebraView);
		this.content.setStyleName("algebraView");
		add(this.content);
	}

	@Override
	protected String getViewPanelStyleName() {
		return "algebraViewPanel";
	}
}
