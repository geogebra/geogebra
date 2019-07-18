package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.commands.selector.CommandNameFilterFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AppConfigCas;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuActionCollection;
import org.geogebra.web.full.gui.view.algebra.contextmenu.AlgebraMenuItemCollectionCAS;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Specific behavior for CAS app
 */
public class CASActivity extends BaseActivity {

	/**
	 * Graphing activity
	 */
	public CASActivity() {
		super(new AppConfigCas());
	}

	@Override
	public SVGResource getIcon() {
		return SvgPerspectiveResources.INSTANCE.menu_icon_cas();
	}

	@Override
	public boolean useValidInput() {
		return false;
	}

	@Override
	public MenuActionCollection<GeoElement> getAVMenuItems(AlgebraViewW view) {
		return new AlgebraMenuItemCollectionCAS(view);
	}

	@Override
	public void start(AppW app) {
		app.getKernel().getGeoGebraCAS().initCurrentCAS();
		app.getKernel().getAlgebraProcessor()
				.addCommandNameFilter(CommandNameFilterFactory.createCasCommandNameFilter());
	}

}
