package org.geogebra.common.kernel.scripting;

import java.util.ArrayList;
import java.util.Iterator;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoInputBox;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;
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
			setValue2(arg[0], arg[1]);
			return arg;
		case 3:
			if ((ok = (arg[0].isGeoList() && arg[0].isIndependent()))
					&& arg[1].isNumberValue()) {

				boolean success = setValue3(kernel, (GeoList) arg[0],
						(int) arg[1].evaluateDouble(), arg[2]);

				if (!success) {
					throw argErr(c, arg[1]);
				}

			} else {
				throw argErr(c, ok ? arg[1] : arg[0]);
			}

			return arg;

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * sets a value of a list (or extends the list if you set element n+1)
	 *
	 * @param kernel
	 *            kernel
	 * @param list
	 *            list
	 * @param nn
	 *            index (1 based)
	 * @param arg2
	 *            value
	 * @return success
	 */
	public static boolean setValue3(Kernel kernel, GeoList list, int nn,
			GeoElement arg2) {

		if (nn < 1 || nn > list.size() + 1) {
			return false;
		}
		if (nn > list.size()) {
			list.add((GeoElement) arg2.deepCopy(kernel));
			if (list.getDefinition() != null) {
				ExpressionValue root = list.getDefinition().unwrap();
				if (root instanceof MyList && arg2.getDefinition() != null
						&& arg2.isIndependent()) {
					((MyList) root).addListElement(arg2.getDefinition());
				} else {
					list.setDefinition(null);
				}
			}
			list.updateRepaint();
			return true;
		}
		GeoElement geo = list.get(nn - 1);
		if (geo.isIndependent()) {
			if (geo.isGeoNumeric() && arg2.isNumberValue()) {
				((GeoNumeric) geo).setValue(arg2.evaluateDouble());
			} else {
				geo.set(arg2);
			}
			if (list.getDefinition() != null) {
				ExpressionValue root = list.getDefinition().unwrap();
				// sizes different == something went wrong
				if (root instanceof MyList && arg2.getDefinition() != null
						&& ((MyList) root).size() == list.size()
						&& arg2.isIndependent()) {
					((MyList) root).setListElement(nn - 1,
							arg2.getDefinition());
				} else {
					list.setDefinition(null);
				}
			}
		} else {
			Log.debug(geo.getParentAlgorithm());
		}

		geo.updateRepaint();

		// update the list too if necessary
		if (!geo.isLabelSet()) { // eg like first element of {1,2,a}
			Iterator<GeoElement> it = kernel.getConstruction()
					.getGeoSetConstructionOrder().iterator();
			ArrayList<GeoList> lists = new ArrayList<>();
			while (it.hasNext()) {
				GeoElement geo2 = it.next();
				if (geo2.isGeoList()) {
					final GeoList gl = (GeoList) geo2;
					for (int i = 0; i < gl.size(); i++) {
						if (gl.get(i) == geo) {
							lists.add(gl);
							break;
						}
					}
				}
			}
			for (GeoList depList : lists) {
				depList.updateCascade();
			}
			kernel.notifyRepaint();
		}
		return true;
	}

	/**
	 * sets arg[0] to arg[1]
	 *
	 * @param arg0
	 *            target
	 * @param arg1
	 *            value
	 */
	public static void setValue2(GeoElement arg0, GeoElement arg1) {
		if (arg0.isGeoFunction() && arg1.isRealValuedFunction()) {
			// eg f(x)=x^2
			// SetValue[f,1]
			GeoFunction fun = (GeoFunction) arg0;
			GeoFunctionable val = (GeoFunctionable) arg1;
			// for GeoFunction set() supports all functionables
			fun.set(val);
			if (!fun.validate(true)) {
				fun.set(arg0);
				fun.setUndefined();
			}
			fun.updateRepaint();
		} else if (arg0.isGeoList() && arg1.isNumberValue()) {
			((GeoList) arg0).setSelectedIndex(
					(int) Math.round(arg1.evaluateDouble()) - 1, true);

		} else if (arg0.isIndependent() || arg0.isMoveable()) {
			if (arg0.isGeoNumeric() && arg1.isNumberValue()) {
				((GeoNumeric) arg0).setValue(arg1.evaluateDouble());
			} else {
				if (arg1.isGeoNumeric()
						&& Double.isNaN(arg1.evaluateDouble())) {
					// eg SetValue[a,?] for line
					arg0.setUndefined();
					arg0.resetDefinition();
				} else {
					// copy() needed for eg
					// rnd = {1,2,3,4}
					// SetValue[rnd, Shuffle[rnd]]
					arg0.set(arg1.isGeoList() ? arg1.copy() : arg1);
					if (arg1.isChildOf(arg0)) {
						arg0.resetDefinition();
					}
				}
			}
			arg0.updateRepaint();
		} else if (arg0.getParentAlgorithm() instanceof SetRandomValue) {
			// eg a = RandomBetween[0,10]
			SetRandomValue algo = (SetRandomValue) arg0.getParentAlgorithm();
			if (algo.setRandomValue(arg1)) {
				arg0.updateRepaint();
			}
		} else if (arg0.isGeoInputBox() && arg1.isGeoText()) {
			String textString = ((GeoText) arg1).getTextString();
			GeoInputBox geoInputBox = (GeoInputBox) arg0;
			geoInputBox.updateLinkedGeo(textString);
		}
		resetInputboxes(arg0);
	}

	private static void resetInputboxes(GeoElement arg0) {
		if (arg0.getAlgoUpdateSet() != null) {
			for (AlgoElement childAlgo : arg0.getAlgoUpdateSet()) {
				if (childAlgo instanceof AlgoInputBox) {
					((AlgoInputBox) childAlgo).getResult().clearTempUserInput();
				}
			}
		}
	}
}
