package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.draw.DrawBoolean;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.spreadsheet.core.CellRenderableFactory;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.spreadsheet.rendering.LaTeXRenderer;
import org.geogebra.common.spreadsheet.rendering.SelfRenderable;
import org.geogebra.common.spreadsheet.rendering.StringRenderer;
import org.geogebra.common.util.shape.Rectangle;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;

public final class GeoElementCellRendererFactory implements CellRenderableFactory {

	private final LaTeXRenderer laTeXRenderer = new LaTeXRenderer();
	private final StringRenderer stringRenderer = new StringRenderer();
	private final CheckboxCellRenderer checkboxCellRenderer = new CheckboxCellRenderer();

	@Override
	public SelfRenderable getRenderable(Object geo) {
		if (geo == null) {
			return null;
		}
		if (geo instanceof GeoFunction) {
			TeXFormula tf = new TeXFormula(((GeoElement) geo)
					.toValueString(StringTemplate.latexTemplate));
			return new SelfRenderable(laTeXRenderer,
					tf.createTeXIcon(TeXConstants.STYLE_DISPLAY, 12));
		}
		if (geo instanceof GeoBoolean && ((GeoBoolean) geo).isIndependent()) {
			return new SelfRenderable(checkboxCellRenderer, geo);
		}
		return new SelfRenderable(stringRenderer,
				((GeoElement) geo).toValueString(StringTemplate.defaultTemplate));
	}

	private static class CheckboxCellRenderer implements CellRenderer {
		@Override
		public void draw(Object data, GGraphics2D g2d, Rectangle cellBorder) {
			g2d.translate((int) cellBorder.getMinX(), (int) cellBorder.getMinY());
			g2d.scale(0.5, 0.5);
			DrawBoolean.CheckBoxIcon.paintIcon(
					((GeoBoolean) data).getBoolean(),
					!((GeoBoolean) data).isSelectionAllowed(null), g2d,
					3,
					3);
			g2d.scale(2, 2);
			g2d.translate((int) -cellBorder.getMinX(), (int) -cellBorder.getMinY());
		}

		@Override
		public boolean match(Object renderable) {
			return renderable instanceof GeoBoolean;
		}
	}
}
