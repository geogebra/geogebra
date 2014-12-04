package geogebra.gui.view.algebra;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.util.IndexHTMLBuilder;
import geogebra.main.AppD;

/**
 * Renderer for algebra view. Add changeable description.
 * 
 * @author mathieu
 * 
 */
public class MyRendererForAlgebraView extends MyRendererForAlgebraTree {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 *            application
	 * @param view
	 *            view
	 */
	public MyRendererForAlgebraView(AppD app, AlgebraTree view) {
		super(app, view);
	}

	@Override
	protected String getDescription(GeoElement geo) {

		String text = null;
		if (geo.isIndependent()) {
			text = getAlgebraDescriptionTextOrHTML(geo);
		} else {
			switch (kernel.getAlgebraStyle()) {
			case Kernel.ALGEBRA_STYLE_VALUE:
				text = getAlgebraDescriptionTextOrHTML(geo);
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				IndexHTMLBuilder builder = new IndexHTMLBuilder(true);
				geo.addLabelTextOrHTML(
						geo.getDefinitionDescription(StringTemplate.defaultTemplate),
						builder);
				text = builder.toString();
				break;

			case Kernel.ALGEBRA_STYLE_COMMAND:
				builder = new IndexHTMLBuilder(true);
				geo.addLabelTextOrHTML(geo
						.getCommandDescription(StringTemplate.defaultTemplate),
						builder);
				text = builder.toString();
				break;

			}

		}

		return text;
	}
}
