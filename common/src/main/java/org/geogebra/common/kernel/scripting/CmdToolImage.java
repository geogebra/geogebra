package org.geogebra.common.kernel.scripting;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.StringUtil;

/**
 * ToolImage
 */
public class CmdToolImage extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdToolImage(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		arg = resArgs(c);

		GeoPoint corner = null, corner2 = null;

		switch (n) {

		case 2:
		case 3:
			if (arg[1] instanceof GeoPoint) {
				corner = (GeoPoint) arg[1];
			} else {
				throw argErr(app, c.getName(), arg[1]);
			}

			if (n == 3) {
				if (arg[2] instanceof GeoPoint) {
					corner2 = (GeoPoint) arg[2];
				} else {
					throw argErr(app, c.getName(), arg[2]);
				}

			}

			// FALL THROUGH
		case 1:
			if (arg[0].isGeoNumeric()) {

				int mode = (int) ((GeoNumeric) arg[0]).getDouble();

				String modeStr = StringUtil.toLowerCase(EuclidianConstants
						.getModeText(mode));

				if ("".equals(modeStr)) {
					throw argErr(app, c.getName(), arg[0]);
				}

				// TODO Fix me

				GeoImage geoImage = new GeoImage(app.getKernel()
						.getConstruction());
				if (app.getGuiManager() != null) {

					String fileName = app.getGuiManager().getToolImageURL(mode,
							geoImage);
					geoImage.setImageFileName(fileName);

				}
				geoImage.setTooltipMode(GeoElement.TOOLTIP_OFF);

				boolean oldState = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				if (corner == null) {
					corner = new GeoPoint(cons, null, 0, 0, 1);
				}
				cons.setSuppressLabelCreation(oldState);
				try {
					geoImage.setStartPoint(corner);

					if (corner2 != null) {
						geoImage.setCorner(corner2, 1);
					}

				} catch (CircularDefinitionException e) {
					e.printStackTrace();
				}
				geoImage.setLabel(null);

				GeoElement[] ret = {};
				return ret;

			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
