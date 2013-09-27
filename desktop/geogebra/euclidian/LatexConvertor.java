package geogebra.euclidian;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.geos.GeoElement;

import org.scilab.forge.jlatexmath.dynamic.ExternalConverter;

public class LatexConvertor implements ExternalConverter {

	AlgebraProcessor env;
	Construction cons;

	public LatexConvertor(AlgebraProcessor env, Construction cons) {
		this.env = env;
		this.cons = cons;
	}

	public String getLaTeXString(String externalCode) {
		boolean oldLabelMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		GeoElement[] geos;
		try {
			geos = env.processAlgebraCommandNoExceptionHandling(externalCode,
					false, false, true, false);
		} catch (Exception e) {
			cons.setSuppressLabelCreation(oldLabelMode);
			// Application.debug(e.getLocalizedMessage());
			return e.getLocalizedMessage();
		} finally {
			cons.setSuppressLabelCreation(oldLabelMode);
		}
		if (geos != null) {
			GeoElement geo = geos[0];
			return geo.getLaTeXdescription();
		}
		return "";
	}
}
