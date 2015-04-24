package org.geogebra.common.kernel.scripting;

import java.util.Iterator;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * SetValue
 */
public class CmdSetValue extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetValue(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);
		boolean ok;

		switch (n) {
		case 2:
			setValue2(arg[0],arg[1]);
			return arg;
		case 3:
			if ((ok = (arg[0].isGeoList() && arg[0].isIndependent()))
					&& arg[1] instanceof NumberValue) {

				boolean success = setValue3(kernelA, (GeoList) arg[0],
						(int) ((NumberValue) arg[1]).getDouble(), arg[2]);

				if (!success) {
					throw argErr(app, c.getName(), arg[1]);
				}

			} else
				throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			return arg;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * sets a value of a list (or extends the list if you set element n+1)
	 * 
	 * @param kernel
	 * @param list
	 * @param nn
	 * @param arg2
	 * @return
	 */
	public static boolean setValue3(Kernel kernel, GeoList list, int nn,
			GeoElement arg2) {

		if (nn < 1 || nn > list.size() + 1) {
			return false;
		}
		if (nn > list.size()) {
			list.add((GeoElement) arg2.deepCopy(kernel));
			list.updateRepaint();
			return true;
		}
		GeoElement geo = list.get(nn - 1);
		if (geo.isIndependent()) {
			if (geo.isGeoNumeric() && arg2 instanceof NumberValue) {
				NumberValue num = (NumberValue) arg2;
				((GeoNumeric) geo).setValue(num.getDouble());
			} else {
				geo.set(arg2);
			}
		} else {
			Log.debug(geo.getParentAlgorithm());
		}

		geo.updateRepaint();

		// update the list too if necessary
		if (!geo.isLabelSet()) { // eg like first element of {1,2,a}
			Iterator<GeoElement> it = kernel.getConstruction()
					.getGeoSetConstructionOrder().iterator();
			while (it.hasNext()) {
				GeoElement geo2 = it.next();
				if (geo2.isGeoList()) {
					GeoList gl = (GeoList) geo2;
					for (int i = 0; i < gl.size(); i++) {
						if (gl.get(i) == geo)
							gl.updateRepaint();
					}
				}
			}
		}
		return true;
	}

	/**
	 * sets arg[0] to arg[1]
	 * 
	 * @param arg
	 */
	public static void setValue2(GeoElement arg0, GeoElement arg1) {
		if (arg0.isGeoFunction() && arg1.isGeoFunctionable()) {
			// eg f(x)=x^2
			// SetValue[f,1]
			GeoFunction fun = (GeoFunction) arg0;
			GeoFunctionable val = (GeoFunctionable) arg1;
			fun.set(val.getGeoFunction());
			fun.updateRepaint();
		} else if (arg0.isGeoList() && arg1.isNumberValue()) {
			((GeoList) arg0).setSelectedIndex(
					(int) Math.round(arg1.evaluateDouble()) - 1, true);

		} else if (arg0.isIndependent() || arg0.isMoveable()) {
			if (arg0.isGeoNumeric() && arg1 instanceof NumberValue) {
				NumberValue num = (NumberValue) arg1;
				((GeoNumeric) arg0).setValue(num.getDouble());
			} else {
				if (arg1.isGeoNumeric()
						&& Double.isNaN(arg1.evaluateDouble())) {
					// eg SetValue[a,?] for line
					arg0.setUndefined();
				} else {
					arg0.set(arg1);
				}
			}
			arg0.updateRepaint();
		} else if (arg1 instanceof NumberValue && arg0.isGeoNumeric()
				&& arg0.getParentAlgorithm() instanceof SetRandomValue) {
			// eg a = RandomBetween[0,10]
			SetRandomValue algo = (SetRandomValue) arg0.getParentAlgorithm();
			algo.setRandomValue(((NumberValue) arg1).getDouble());
		} else if (arg1 instanceof NumberValue
				&& arg0.getParentAlgorithm() instanceof AlgoDependentNumber) {
			// eg a = random()
			double val = ((NumberValue) arg1).getDouble();
			if (val >= 0 && val <= 1) {
				AlgoDependentNumber al = (AlgoDependentNumber) arg0
						.getParentAlgorithm();
				ExpressionNode en = al.getExpression();
				if (en.getOperation().equals(Operation.RANDOM)) {
					GeoNumeric num = ((GeoNumeric) al.getOutput()[0]);
					num.setValue(val);
					num.updateRepaint();
				}
			}
		}
	}
}
