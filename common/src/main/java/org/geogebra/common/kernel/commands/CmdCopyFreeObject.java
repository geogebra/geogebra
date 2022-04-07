package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

/**
 * CopyFreeObject
 */
public class CmdCopyFreeObject extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCopyFreeObject(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		// FunctionalNVar
		case 1:
			GeoElement geo;
			String label = c.getLabel();
			if (arg[0] instanceof FunctionalNVar) {

				return copyFunction(arg[0], c, label);

			}

			if (arg[0] instanceof GeoSegmentND) {

				geo = ((GeoSegmentND) arg[0]).copyFreeSegment();

			} else if (arg[0] instanceof GeoRayND) {

				geo = ((GeoRayND) arg[0]).copyFreeRay();

			} else {
				// changed to deepCopyGeo() so that it works for lists
				// https://help.geogebra.org/topic/copyfreeobject-a1-a3-not-free
				geo = arg[0].deepCopyGeo();
			}

			geo.setVisualStyle(arg[0]);
			geo.setLabel(label);
			GeoElement[] ret = { geo };
			if (!arg[0].isLabelSet()) {
				arg[0].remove();
			}
			return ret;

		// more than one argument
		default:
			throw argNumErr(c);
		}
	}

	private GeoElement[] copyFunction(GeoElement geoElement, Command c,
			String label) {
		FunctionalNVar f = (FunctionalNVar) geoElement;
		StringBuilder command = new StringBuilder();

		// eg f(x,y)=
		if (label != null) {
			command.append(label);
		} else {
			// add label explicitly to make sure this works for f(t) or f(a,b)
			command.append(geoElement.getFreeLabel(null));
		}
		command.append('(');
		command.append(f.getVarString(StringTemplate.defaultTemplate));
		command.append(")=");

		StringTemplate highPrecision = StringTemplate.maxPrecision;
		if (f.getFunctionExpression().isSecret()) {
			command.append(geoElement.getParentAlgorithm().getClassName());
			command.append("[");
			command.append(geoElement.getParentAlgorithm().getInput(0)
					.toOutputValueString(highPrecision));
			command.append("]");
		} else {

			command.append(geoElement.toOutputValueString(highPrecision));
		}

		try {
			GeoElementND[] ret = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptions(command.toString(),
							true);
			ret[0].setVisualStyle(geoElement);
			if (!geoElement.isLabelSet()) {
				geoElement.remove();
			}
			return ret[0].toGeoElement().asArray();

		} catch (Exception e) {
			if (!geoElement.isLabelSet()) {
				geoElement.remove();
			}
			Log.debug(e);
			throw argErr(geoElement, c);
		}

	}
}
