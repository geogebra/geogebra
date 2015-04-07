/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.common.kernel.scripting;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.main.MyError;

/**
 * ShowAxex[] ShowAxes[<Boolean>] ShowAxes[<View ID>, <Boolean]
 */
public class CmdShowAxes extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShowAxes(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		EuclidianViewInterfaceCommon ev = null;

		arg = resArgs(c);
		switch (n) {
		case 0:
			ev = app.getActiveEuclidianView();
			ev.setShowAxis(true);
			ev.repaintView();
			break;
		case 1:
			if (!(arg[0] instanceof BooleanValue))
				throw argErr(app, c.getName(), arg[0]);

			boolean show = ((BooleanValue) arg[0]).getBoolean();
			ev = app.getActiveEuclidianView();
			ev.setShowAxis(show);
			ev.repaintView();

			break;
		case 2:
			if (!(arg[0] instanceof NumberValue))
				throw argErr(app, c.getName(), arg[0]);
			if (!(arg[1] instanceof BooleanValue))
				throw argErr(app, c.getName(), arg[1]);

			show = ((BooleanValue) arg[1]).getBoolean();

			switch ((int) (((NumberValue) arg[0]).getDouble())) {
			case 2:
				if (app.hasEuclidianView2(1)) {
					ev = app.getEuclidianView2(1);
				}
				break;
			case 3:
				if (app.hasEuclidianView3D()) {
					ev = app.getEuclidianView3D();
				}
				break;
			default:
				ev = app.getEuclidianView1();
			}
			if (ev != null) {
				ev.setShowAxis(show);
				ev.repaintView();
			}
			break;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
