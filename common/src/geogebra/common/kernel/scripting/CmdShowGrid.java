/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.common.kernel.scripting;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.main.MyError;

/**
 * ShowGrid[]
 * ShowGrid[<Boolean>]
 * ShowGrid[<View ID>, <Boolean]
 */
public class CmdShowGrid extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShowGrid(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		
		EuclidianViewInterfaceCommon ev = null;

		arg = resArgs(c);
		switch (n) {
		case 0:
			ev = app.getActiveEuclidianView();
			ev.showGrid(true);
			ev.repaintView();
			break;
		case 1:
			if (!arg[0].isBooleanValue())
				throw argErr(app, c.getName(), arg[0]);
			
			boolean show = ((BooleanValue)arg[0]).getBoolean();
			ev = app.getActiveEuclidianView();
			ev.showGrid(show);
			ev.repaintView();
			
			break;
		case 2:
			if (!arg[0].isNumberValue())
				throw argErr(app, c.getName(), arg[0]);
			if (!arg[1].isBooleanValue())
				throw argErr(app, c.getName(), arg[1]);
			
			show = ((BooleanValue)arg[1]).getBoolean();

			
			switch ((int)(((NumberValue)arg[0]).getDouble())) {
			case 2: 
				if (app.hasEuclidianView2()) {
					ev = app.getEuclidianView2();
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
			
			ev.showGrid(show);
			ev.repaintView();
			break;


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

