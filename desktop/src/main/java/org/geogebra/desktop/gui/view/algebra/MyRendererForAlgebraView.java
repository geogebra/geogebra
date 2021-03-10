package org.geogebra.desktop.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.desktop.main.AppD;

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
	protected String getDescription(GeoElement geo,	GeoMutableTreeNode node) {

		String text = null;
		if (geo.isIndependent() && geo.getDefinition() == null) {
			text = node.getAlgebraDescription();
		} else {
			switch (kernel.getAlgebraStyle()) {
			default:
			case Kernel.ALGEBRA_STYLE_VALUE:
				text = node.getAlgebraDescription();
				break;

			case Kernel.ALGEBRA_STYLE_DESCRIPTION:
				IndexHTMLBuilder builder = new IndexHTMLBuilder(true);
				geo.addLabelTextOrHTML(geo.getDefinitionDescription(
						StringTemplate.defaultTemplate), builder);
				text = builder.toString();
				break;

			case Kernel.ALGEBRA_STYLE_DEFINITION:
				builder = new IndexHTMLBuilder(true);
				geo.addLabelTextOrHTML(
						geo.getDefinition(StringTemplate.defaultTemplate),
						builder);
				text = builder.toString();
				break;

			}

		}

		return text;
	}
}
