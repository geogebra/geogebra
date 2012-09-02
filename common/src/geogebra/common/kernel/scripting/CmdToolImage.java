package geogebra.common.kernel.scripting;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoImage;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;
import geogebra.common.util.StringUtil;

/**
 *ToolImage
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

		switch (n) {
		case 1:
			if (arg[0].isGeoNumeric()) {

				int mode = (int) ((GeoNumeric) arg[0]).getDouble();

				String modeStr = StringUtil.toLowerCase(kernelA.getModeText(mode));

				if ("".equals(modeStr))
					throw argErr(app, c.getName(), arg[0]);

				String fileName = app.getImageManager().createImage(
				"/geogebra/gui/toolbar/images/mode_" + modeStr
						+ "_32.gif",app);
				GeoImage geoImage = new GeoImage(app.getKernel()
						.getConstruction());
				geoImage.setImageFileName(fileName);
				geoImage.setTooltipMode(GeoElement.TOOLTIP_OFF);

				boolean oldState = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				GeoPoint corner = new GeoPoint(cons, null, 0, 0, 1);
				cons.setSuppressLabelCreation(oldState);
				try {
					geoImage.setStartPoint(corner);
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
