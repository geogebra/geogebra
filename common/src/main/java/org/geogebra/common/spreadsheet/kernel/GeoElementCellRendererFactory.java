package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.spreadsheet.core.CellRenderableFactory;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;

public class GeoElementCellRendererFactory implements CellRenderableFactory {
	@Override
	public Object getRenderable(Object geo) {
		if (geo == null) {
			return null;
		}
		if (geo instanceof GeoFunction) {
			TeXFormula tf = new TeXFormula(((GeoElement) geo)
					.toValueString(StringTemplate.latexTemplate));
			return tf.createTeXIcon(TeXConstants.STYLE_DISPLAY, 12);
		}
		if (geo instanceof GeoBoolean && ((GeoBoolean) geo).isIndependent()) {
			return ((GeoBoolean) geo).getBoolean();
		}
		return ((GeoElement) geo).toValueString(StringTemplate.defaultTemplate);
	}
}
