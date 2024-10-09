package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoTableText;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * TableText[&lt;Matrix&gt;]
 * 
 * TableText[&lt;Matrix&gt;,&lt;Point&gt;]
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
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 0:
			throw argNumErr(c);

		case 1:
			if (arg[0].isGeoList()) {
				GeoList list = wrapInListIfNeeded(arg, 1);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), arg, list, null, null) };
					return ret;
				}
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[0]);

		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoText())) {
				GeoList list = wrapInListIfNeeded(arg, 1);
				if (list != null) {
					GeoElement[] ret = {
							tableText(c.getLabel(), arg, list, (GeoText) arg[1], null) };
					return ret;
				}
				throw argErr(c, arg[0]);
			}

			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())) {
				// two lists, no alignment
				GeoList list = wrapInListIfNeeded(arg, 2);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), arg, list, null, null) };
					return ret;
				}
			}

			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1] instanceof GeoNumberValue)) {
				int maxSize = (int) arg[1].evaluateDouble();

				if (maxSize <= 0) {
					throw argErr(c, arg[1]);
				}

				GeoList list = convertToMatrix((GeoList) arg[0], maxSize);

				GeoElement[] ret = { tableText(c.getLabel(), arg, list, null, null) };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		case 3:
			if (arg[0].isGeoList() && arg[1].isGeoText() && arg[2] instanceof GeoNumberValue) {
				GeoList first = (GeoList) arg[0];
				if (first.size() == 0 || first.get(0).isGeoList()) {
					GeoNumberValue[] minWidthHeight = {(GeoNumberValue) arg[2]};
					GeoElement[] ret = { tableText(c.getLabel(), arg, first, (GeoText) arg[1],
							minWidthHeight) };
					return ret;
				}
				int maxSize = (int) arg[2].evaluateDouble();

				if (maxSize <= 0) {
					throw argErr(c, arg[2]);
				}

				GeoList list = convertToMatrix((GeoList) arg[0], maxSize);

				GeoElement[] ret = { tableText(c.getLabel(), arg, list, (GeoText) arg[1], null) };
				return ret;
			}

		// fallthrough
		default:
			// try to create list of numbers
			GeoList list;
			if (arg[arg.length - 1].isGeoText()) {
				list = wrapInListIfNeeded(arg, arg.length - 1);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), arg, list,
							(GeoText) arg[arg.length - 1], null) };
					return ret;
				}
			} else if (arg[arg.length - 1] instanceof GeoNumberValue) { //min width (and height)
				int amountNumerics = arg[arg.length - 2] instanceof GeoNumberValue ? 2 : 1;
				GeoNumberValue[] minWidthHeight = new GeoNumberValue[amountNumerics];
				minWidthHeight[0] = (GeoNumberValue) arg[arg.length - 1];
				if (amountNumerics == 2) {
					minWidthHeight[0] = (GeoNumberValue) arg[arg.length - 2];
					minWidthHeight[1] = (GeoNumberValue) arg[arg.length - 1];
				}

				if (arg[arg.length - amountNumerics - 1].isGeoText()) {
					list = wrapInListIfNeeded(arg, arg.length - amountNumerics - 1);
					if (list != null) {
						GeoElement[] ret = { tableText(c.getLabel(), arg, list,
								(GeoText) arg[arg.length - amountNumerics - 1], minWidthHeight) };
						return ret;
					}
				} else {
					list = wrapInListIfNeeded(arg, arg.length - amountNumerics);
					if (list != null) {
						GeoElement[] ret = {tableText(c.getLabel(), arg, list, null,
								minWidthHeight)};
						return ret;
					}
				}
			} else {
				list = wrapInListIfNeeded(arg, arg.length);
				if (list != null) {
					GeoElement[] ret = { tableText(c.getLabel(), arg, list, null, null) };
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

	private GeoList wrapInListIfNeeded(GeoElement[] arg, int length) {
		if (length == 1 && arg[0].isGeoList()) {
			GeoList first = (GeoList) arg[0];
			if (first.size() == 0 || first.get(0).isGeoList()) {
				return first;
			}
		}
		return wrapInList(kernel, arg, length, GeoClass.DEFAULT);
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
	final public GeoText tableText(String label, GeoElement[] arg, GeoList list, GeoText args,
			GeoNumberValue[] minWidthHeight) {
		AlgoTableText algo = new AlgoTableText(cons, arg, label, list, args, minWidthHeight);
		GeoText text = algo.getResult();
		return text;
	}
}
