package org.geogebra.desktop.gui.view.algebra;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.settings.AlgebraStyle;
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
			switch (kernel.getAlgebraStyle()) {
			default:
			case AlgebraStyle.VALUE:
				text = node.getAlgebraDescription();
				break;

			case AlgebraStyle.DESCRIPTION:
				IndexHTMLBuilder builder = new IndexHTMLBuilder(true);
				geo.addLabelTextOrHTML(geo.getDefinitionDescription(
						StringTemplate.defaultTemplate), builder);
				text = builder.toString();
				break;

			case AlgebraStyle.DEFINITION:
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
