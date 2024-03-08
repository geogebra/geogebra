package org.geogebra.common.kernel.scripting;

import java.util.ArrayList;
import java.util.Iterator;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoInputBox;
import org.geogebra.common.kernel.algos.DependentAlgo;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Evaluate2Var;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
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
import org.geogebra.common.plugin.GeoClass;
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
	 * @param target
	 *            target
	 * @param from
	 *            value
	 */
	public static void setValue2(GeoElement target, GeoElement from) {
		if (target.isGeoList() && from.isNumberValue()
				&& !Double.isNaN(from.evaluateDouble())) {
			int selectIdx = (int) Math.round(from.evaluateDouble()) - 1;
			if (((GeoList) target).getSelectedIndex() != selectIdx) {
				((GeoList) target).setSelectedIndex(selectIdx);
				target.updateRepaint();
			}
		} else if (shouldCopyOnlyValuesForLists(target, from)) {
			setValueForLists((GeoList) target, (GeoList) from);
		} else if (target.isIndependent() || target.isMoveable()) {
			setValueIndependent(target, from);
		} else if (target.getParentAlgorithm() instanceof SetRandomValue) {
			setRandomValue(target, from);
		} else if (target.getParentAlgorithm() instanceof DependentAlgo) {
			if (from.isGeoNumeric()
					&& Double.isNaN(from.evaluateDouble())) {
				// eg SetValue[a,?] for line
				undefine(target);
				target.updateRepaint();
			}
		} else if (target.isGeoInputBox() && from.isGeoText()) {
			String textString = ((GeoText) from).getTextString();
			GeoInputBox geoInputBox = (GeoInputBox) target;
			geoInputBox.updateLinkedGeo(textString);
		}
		resetInputboxes(target);
	}

	private static boolean shouldCopyOnlyValuesForLists(GeoElement target, GeoElement from) {
		if (!target.isGeoList() || !from.isGeoList()) {
			return false;
		}
		GeoClass elementType = ((GeoList) from).getElementType();
		return elementType == GeoClass.SEGMENT || elementType == GeoClass.SEGMENT3D
				|| elementType == GeoClass.CONICPART || elementType == GeoClass.CONICSECTION
				|| elementType == GeoClass.RAY || elementType == GeoClass.RAY3D
				|| elementType == GeoClass.POLYGON || elementType == GeoClass.POLYLINE
				|| elementType == GeoClass.POLYLINE3D || elementType == GeoClass.TURTLE;
	}

	private static void setValueForLists(GeoList target, GeoList from) {
		target.clear();
		from.elements().forEach(element -> target.add(element.copy()));
		target.copyAttributesFromOtherList(from);
		if (from.isChildOf(target)) {
			target.resetDefinition();
		}
		target.updateRepaint();
	}

	private static void setValueIndependent(GeoElement target, GeoElement from) {
		if (target.isGeoNumeric() && from.isNumberValue()) {
			((GeoNumeric) target).setValue(from.evaluateDouble());
		} else {
			if (from.isGeoNumeric()
					&& Double.isNaN(from.evaluateDouble())) {
				// eg SetValue[a,?] for line
				if (target.isGeoList() && ((GeoList) target).isMatrix()) {
					undefine(target);
				} else {
					target.setUndefined();
					target.resetDefinition();
				}
			} else if (target.isGeoFunction() && from.isRealValuedFunction()) {
				// eg f(x)=x^2
				// SetValue[f,1]
				GeoFunction fun = (GeoFunction) target;
				GeoFunctionable val = (GeoFunctionable) from;
				// for GeoFunction set() supports all functionables
				fun.set(val);
				if (!fun.validate(true)) {
					fun.set(target);
					fun.setUndefined();
				}
				fun.updateRepaint();
			} else {
				// copy() needed for eg
				// rnd = {1,2,3,4}
				// SetValue[rnd, Shuffle[rnd]]
				target.set(from.isGeoList() ? from.copy() : from);
				if (from.isChildOf(target)) {
					target.resetDefinition();
				}
			}
		}
		target.updateRepaint();
	}

	private static void setRandomValue(GeoElement target, GeoElement from) {
		// eg a = RandomBetween[0,10]
		SetRandomValue algo = (SetRandomValue) target.getParentAlgorithm();
		if (algo.setRandomValue(from)) {
			target.updateRepaint();
		} else if (from.isGeoNumeric()
				&& Double.isNaN(from.evaluateDouble()) && algo instanceof DependentAlgo) {
			// eg SetValue[a,?] for number
			undefine(target);
			target.updateRepaint();
		}
	}

	private static void undefine(GeoElement geo) {
		Kernel kernel = geo.getKernel();
		if (geo.isGeoList()) {
			if (((GeoList) geo).getElementType() != GeoClass.LIST) {
				geo.setUndefined();
				((GeoList) geo).clear();
				geo.setDefinition(new MyList(kernel).wrap());
			} else {
				geo.resetDefinition();
				geo.setUndefined();
				geo.setDefinition(geo.toValidExpression().wrap());
			}
		} else if (geo.isGeoFunction() || geo.isGeoFunctionNVar()) {
			((Evaluate2Var) geo).getFunction().setExpression(new ExpressionNode(kernel,
					Double.NaN));
			geo.setUndefined();
		} else {
			geo.setDefinition(geo.getUndefinedCopy(kernel)
					.toValidExpression().wrap());
			geo.setUndefined();
		}
	}

	private static void resetInputboxes(GeoElement geo) {
		if (geo.getAlgoUpdateSet() != null) {
			for (AlgoElement childAlgo : geo.getAlgoUpdateSet()) {
				if (childAlgo instanceof AlgoInputBox) {
					((AlgoInputBox) childAlgo).getResult().clearTempUserInput();
				}
			}
		}
	}
}
