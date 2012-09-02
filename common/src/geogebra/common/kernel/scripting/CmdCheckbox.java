package geogebra.common.kernel.scripting;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 *Checkbox
 */
public class CmdCheckbox extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCheckbox(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy

		String caption = null;
		GeoList geosToHide = null;
		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[0].isGeoText())
				caption = ((GeoText) arg[0]).getTextString();
			else
				throw argErr(app, c.getName(), arg[0]);
			if (arg[1].isGeoList()) {
				geosToHide = (GeoList) arg[1];
			} else
				throw argErr(app, c.getName(), arg[1]);
			break;
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoText())
				caption = ((GeoText) arg[0]).getTextString();
			else if (arg[0].isGeoList()) {
				geosToHide = (GeoList) arg[0];
			} else
				throw argErr(app, c.getName(), arg[0]);
			break;
		case 0:
			break;

		default:
			throw argNumErr(app, c.getName(), n);
		}
		GeoBoolean gb = new GeoBoolean(app.getKernel().getConstruction());
		gb.setValue(true);
		gb.initLocation();
		gb.setEuclidianVisible(true);
		gb.setLabel(c.getLabel());	
		
		if (caption != null) {
			gb.setLabelVisible(true);		
			gb.setCaption(caption);
			gb.update();				
		}
		try {

			if (geosToHide != null)
				for (int i = 0; i < geosToHide.size(); i++)
					geosToHide.get(i).setShowObjectCondition(gb);
		} catch (CircularDefinitionException e) {
			app.showError("CircularDefinition");
		}
		return new GeoElement[] {gb};
	}
}
