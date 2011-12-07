package geogebra.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoImage;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoPoint2;
import geogebra.util.ImageManager;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Locale;

/**
 *ToolImage
 */
class CmdToolImage extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdToolImage(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((ok[0] = (arg[0].isGeoNumeric()))) {

				int mode = (int) ((GeoNumeric) arg[0]).getDouble();

				String modeStr = kernel.getModeText(mode)
						.toLowerCase(Locale.US);

				if ("".equals(modeStr))
					throw argErr(app, c.getName(), arg[0]);

				Image im = app.getImageManager().getImageResource(
						"/geogebra/gui/toolbar/images/mode_" + modeStr
								+ "_32.gif");

				BufferedImage image = ImageManager.toBufferedImage(im);
				String fileName = app.createImage(image, "tool.png");

				GeoImage geoImage = new GeoImage(app.getKernel()
						.getConstruction());
				geoImage.setImageFileName(fileName);
				geoImage.setTooltipMode(GeoElement.TOOLTIP_OFF);

				boolean oldState = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				GeoPoint2 corner = new GeoPoint2(cons, null, 0, 0, 1);
				cons.setSuppressLabelCreation(oldState);
				try {
					geoImage.setStartPoint(corner);
				} catch (CircularDefinitionException e) {
				}
				geoImage.setLabel(null);

				GeoElement[] ret = {};
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
