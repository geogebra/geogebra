/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * ShowAxex[] / ShowGrid[]
 * 
 * ShowAxes[Boolean] / ShowGrid[Boolean]
 * 
 * ShowAxes[View ID, Boolean] / ShowGrid[View ID/Boolean]
 */
public class CmdShowAxesOrGrid extends CmdScripting {

	private final Commands cmd;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShowAxesOrGrid(Kernel kernel, Commands cmd) {
		super(kernel);
		this.cmd = cmd;
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		EuclidianSettings evs;

		GeoElement[] arg = resArgs(c);
		switch (n) {
		case 0:
			evs = app.getActiveEuclidianView().getSettings();
			setAndRepaint(true, evs);
			break;
		case 1:
			if (!(arg[0] instanceof BooleanValue)) {
				throw argErr(c, arg[0]);
			}

			boolean show = ((BooleanValue) arg[0]).getBoolean();
			evs = app.getActiveEuclidianView().getSettings();
			setAndRepaint(show, evs);

			break;
		case 2:
			if (!(arg[0] instanceof NumberValue)) {
				throw argErr(c, arg[0]);
			}
			if (!(arg[1] instanceof BooleanValue)) {
				throw argErr(c, arg[1]);
			}

			show = ((BooleanValue) arg[1]).getBoolean();
			int evNo = (int) arg[0].evaluateDouble();
			if (evNo >= 1 && evNo <= 3 || evNo == -1) {
				evs = app.getSettings().getEuclidian(evNo);
				if (evs != null) {
					setAndRepaint(show, evs);
				}
			}
			break;

		default:
			throw argNumErr(c);
		}
		return arg;
	}

	private void setAndRepaint(boolean show, EuclidianSettings evs) {
		if (this.cmd == Commands.ShowAxes) {
			evs.setShowAxes(show);
		} else {
			evs.showGrid(show);
		}
		kernel.notifyRepaint();
	}
}
