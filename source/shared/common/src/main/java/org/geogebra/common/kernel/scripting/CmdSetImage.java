package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.properties.FillType;

public class CmdSetImage extends CmdScripting {

	/**
	 * Create new command processor
	 * @param kernel kernel
	 */
	public CmdSetImage(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] perform(Command c) {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);

		switch (n) {
		case 2:
			if (arg[0].isFillable()) {
				if (arg[1] instanceof GeoImage) {
					String fileName = arg[1].getImageFileName();
					if (fileName != null && !fileName.isEmpty()) {
						arg[0].setFillType(FillType.IMAGE);
						arg[0].setFillImage(fileName);
						arg[0].updateVisualStyleRepaint(GProperty.HATCHING);
					}
				} else if (arg[1] instanceof GeoText) {
					app.getImageManager().setImageForFillable(kernel, (GeoText) arg[1], arg[0]);
				} else {
					throw argErr(c, arg[1]);
				}
			} else {
				throw argErr(c, arg[0]);
			}
			return arg;
		default:
			throw argNumErr(c);
		}
	}
}
