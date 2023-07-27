package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.spreadsheet.core.CellRenderer;
import org.geogebra.common.spreadsheet.core.LaTeXRenderer;
import org.geogebra.common.spreadsheet.core.CellRendererFactory;
import org.geogebra.common.spreadsheet.core.StringRenderer;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;

public class GeoElementCellRendererFactory implements CellRendererFactory {
	@Override
	public CellRenderer getRenderer(Object geo) {
		if (geo == null) {
			return null;
		}
		if (geo instanceof GeoFunction) {
			TeXFormula tf = new TeXFormula(((GeoElement) geo).toValueString(StringTemplate.latexTemplate));
			TeXIcon ti = tf.createTeXIcon(TeXConstants.STYLE_DISPLAY, 12);
			return new LaTeXRenderer(ti);
		}
		return new StringRenderer(((GeoElement) geo).toValueString(StringTemplate.defaultTemplate));
	}
}
