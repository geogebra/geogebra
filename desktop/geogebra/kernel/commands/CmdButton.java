package geogebra.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 * Button[],Button[caption]
 * @author Zbynek
 *
 */
class CmdButton extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdButton(AbstractKernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy

		
		
		switch (n) {
		case 1:
			
			arg = resArgs(c);
			if (arg[0].isGeoText()){
				String caption = ((GeoText) arg[0]).getTextString();
				GeoButton gb = new GeoButton(cons);
				gb.setLabelVisible(true);
				gb.setLabel(c.getLabel());	
				gb.setCaption(caption);
				gb.updateRepaint();
				return new GeoElement[] {gb};
			}
			else
				throw argErr(app, c.getName(), arg[0]);			
		case 0:			
			GeoButton gb = new GeoButton(cons);		
			gb.setLabelVisible(true);
			gb.setLabel(c.getLabel());	
			gb.updateRepaint();
			return new GeoElement[] {gb};

		default:
			throw argNumErr(app, c.getName(), n);
		}
		
	}
}
