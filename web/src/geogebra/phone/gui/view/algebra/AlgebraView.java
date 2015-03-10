package geogebra.phone.gui.view.algebra;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.AbstractView;
import geogebra.phone.gui.view.HeaderPanel;
import geogebra.phone.gui.view.StyleBar;
import geogebra.phone.gui.view.ViewPanel;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.view.algebra.AlgebraViewW;

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
		return new AlgebraStyleBar(algebraView.getStyleBar());
	}

}
