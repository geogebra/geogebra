package geogebra.common.kernel.scripting;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoDependentList;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

import java.util.ArrayList;

/**
 *SetDynamicColor
 */
public class CmdSetDynamicColor extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetDynamicColor(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg2;
		switch (n) {
		case 4:
			boolean[] ok = new boolean[n];
			arg2 = resArgs(c);
			if ((ok[1] = arg2[1].isNumberValue())
					&& (ok[2] = arg2[2].isNumberValue())
					&& (ok[3] = arg2[3].isNumberValue())) {

				GeoElement geo = arg2[0];

				ArrayList<GeoElement> listItems = new ArrayList<GeoElement>();
				listItems.add(arg2[1]); 
				listItems.add(arg2[2]); 
				listItems.add(arg2[3]); 
				//listItems.add((GeoElement) arg2[4]); // no opacity 
				AlgoDependentList algo = new AlgoDependentList(cons, listItems, false);
				kernelA.getConstruction().removeFromConstructionList(algo);
				GeoList list = algo.getGeoList();

				geo.setColorFunction(list);
				geo.updateRepaint();

				
				return;

			} else if (!ok[1])
				throw argErr(app, c.getName(), arg2[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg2[2]);
			else
				throw argErr(app, c.getName(), arg2[3]);
		case 5:
			ok = new boolean[n];
			arg2 = resArgs(c);
			if ((ok[1] = arg2[1].isNumberValue())
					&& (ok[2] = arg2[2].isNumberValue())
					&& (ok[3] = arg2[3].isNumberValue())
					&& (ok[4] = arg2[4].isNumberValue())) {

				GeoElement geo = arg2[0];
				
				ArrayList<GeoElement> listItems = new ArrayList<GeoElement>();
				listItems.add(arg2[1]); 
				listItems.add(arg2[2]); 
				listItems.add(arg2[3]); 
				listItems.add(arg2[4]); // opacity 
				AlgoDependentList algo = new AlgoDependentList(cons, listItems, false);
				kernelA.getConstruction().removeFromConstructionList(algo);
				GeoList list = algo.getGeoList();

				geo.setColorFunction(list);
				geo.updateRepaint();

				
				return;

			} else if (!ok[1])
				throw argErr(app, c.getName(), arg2[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg2[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg2[3]);
			else
				throw argErr(app, c.getName(), arg2[4]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
