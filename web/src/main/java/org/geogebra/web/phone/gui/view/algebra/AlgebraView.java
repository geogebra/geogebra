package org.geogebra.web.phone.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.gui.view.AbstractView;
import org.geogebra.web.phone.gui.view.HeaderPanel;
import org.geogebra.web.phone.gui.view.StyleBar;
import org.geogebra.web.phone.gui.view.ViewPanel;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;

import com.google.gwt.resources.client.ImageResource;

/**
 * @see AbstractView
 */
public class AlgebraView extends AbstractView {

	private AlgebraViewW algebraView;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public AlgebraView(AppW app) {
		super(app);
		this.algebraView = (AlgebraViewW) app.getAlgebraView();
	}

	@Override
	protected ImageResource createViewIcon() {
		return GuiResources.INSTANCE.algebraView();
	}

	public GeoElement getDraggedGeo() {
		return null;
	}

	@Override
	protected ViewPanel createViewPanel() {
		return new AlgebraViewPanel(app, this.algebraView);
	}

	@Override
	protected HeaderPanel createHeaderPanel() {
		return null;
	}

	@Override
	public StyleBar createStyleBar() {
		return new AlgebraStyleBar(algebraView.getStyleBar(true));
	}

}
