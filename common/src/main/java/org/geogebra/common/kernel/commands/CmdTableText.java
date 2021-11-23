package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoTableText;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * TableText[&lt;Matrix>]
 * 
 * TableText[&lt;Matrix>,&lt;Point>]
 */

public class CmdTableText extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTableText(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 0:
			throw argNumErr(c);

		case 1:
			if (arg[0].isGeoList()) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0 || list.get(0).isGeoList()) { // list of
																	// lists: no
																	// need to
																	// wrap
					GeoElement[] ret = {
							tableText(c.getLabel(), arg, (GeoList) arg[0], null) };
					return ret;
				}
				list = wrapInList(kernel, arg, arg.length, GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), arg, list, null) };
					return ret;
				}
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[0]);

		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoText())) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0 || list.get(0).isGeoList()) { // list of
																	// lists: no
																	// need to
																	// wrap
					GeoElement[] ret = { tableText(c.getLabel(), arg,
							(GeoList) arg[0], (GeoText) arg[1]) };
					return ret;
				}
				list = wrapInList(kernel, arg, arg.length - 1,
						GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = {
							tableText(c.getLabel(), arg, list, (GeoText) arg[1]) };
					return ret;
				}
				throw argErr(c, arg[0]);
			}

			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())) {
				// two lists, no alignment
				GeoList list = wrapInList(kernel, arg, arg.length,
						GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), arg, list, null) };
					return ret;
				}
			}

			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoNumeric())) {
				int maxSize = (int) arg[1].evaluateDouble();

				if (maxSize <= 0) {
					throw argErr(c, arg[1]);
				}

				GeoList list = convertToMatrix((GeoList) arg[0], maxSize);

				GeoElement[] ret = { tableText(c.getLabel(), arg, list, null) };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		case 3:
			if (arg[0].isGeoList() && arg[1].isGeoText() && arg[2].isGeoNumeric()) {
				int maxSize = (int) arg[2].evaluateDouble();

				if (maxSize <= 0) {
					throw argErr(c, arg[2]);
				}

				GeoList list = convertToMatrix((GeoList) arg[0], maxSize);

				GeoElement[] ret = { tableText(c.getLabel(), arg, list, (GeoText) arg[1]) };
				return ret;
			}

		// fallthrough
		default:
			// try to create list of numbers
			GeoList list;
			if (arg[arg.length - 1].isGeoText()) {
				list = wrapInList(kernel, arg, arg.length - 1,
						GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), arg, list,
							(GeoText) arg[arg.length - 1]) };
					return ret;
				}
			} else {
				list = wrapInList(kernel, arg, arg.length, GeoClass.DEFAULT);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), arg, list, null) };
					return ret;
				}
			}
			throw argErr(c, arg[0]);
		}
	}

	private GeoList convertToMatrix(GeoList list, int maxSize) {
		GeoList matrix = new GeoList(cons);
		GeoList current = new GeoList(cons);
		for (int i = 0; i < list.size(); i++) {
			if (i != 0 && i % maxSize == 0) {
				matrix.add(current);
				current = new GeoList(cons);
			}
			current.add(list.get(i));
		}
		if (current.size() != 0) {
			matrix.add(current);
		}

		return matrix;
	}

	/**
	 * Table[list] Michael Borcherds
	 * 
	 * @param label
	 *            label for output
	 * @param arg
	 *            arguments
	 * @param list
	 *            input matrix
	 * @param args
	 *            matrix parameters
	 * @return table text
	 */
	final public GeoText tableText(String label, GeoElement[] arg, GeoList list, GeoText args) {
		AlgoTableText algo = new AlgoTableText(cons, arg, label, list, args);
		GeoText text = algo.getResult();
		return text;
	}
}
