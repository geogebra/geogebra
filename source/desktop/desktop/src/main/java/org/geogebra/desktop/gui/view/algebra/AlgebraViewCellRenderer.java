package org.geogebra.desktop.gui.view.algebra;

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
public class AlgebraViewCellRenderer extends AlgebraTreeCellRenderer {

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
	public AlgebraViewCellRenderer(AppD app, AlgebraTree view) {
		super(app, view);
	}

	@Override
	protected String getDescription(GeoElement geo, GeoMutableTreeNode node) {

		String text = null;
		if (geo.isIndependent() && geo.getDefinition() == null) {
			text = node.getAlgebraDescription();
		} else {
			switch (app.getAlgebraStyle()) {
			default:
			case VALUE:
				text = node.getAlgebraDescription();
				break;

			case DESCRIPTION:
				IndexHTMLBuilder builder = new IndexHTMLBuilder(true);
				geo.addLabelTextOrHTML(geo.getDefinitionDescription(
						StringTemplate.defaultTemplate), builder);
				text = builder.toString();
				break;

			case DEFINITION:
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
