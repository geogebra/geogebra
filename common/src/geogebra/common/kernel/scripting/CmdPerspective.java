package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;

public class CmdPerspective extends CmdScripting {
	public CmdPerspective(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void perform(Command c) {
		GeoElement[] args = resArgs(c);
		if(args.length != 1){
			throw this.argNumErr(app, c.getName(),args.length);
		}
		if(args[0] instanceof GeoText){
			String code  = ((GeoText)args[0]).getTextString();
			app.getGgbApi().openViews(code);
			return;
		}

		throw this.argErr(app, c.getName(),args[0]);
	}

	

}
