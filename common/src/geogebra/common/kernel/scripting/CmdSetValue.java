package geogebra.common.kernel.scripting;

import geogebra.common.kernel.CmdScripting;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.SetRandomValue;
import geogebra.common.kernel.algos.AlgoDependentNumber;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.plugin.Operation;

import java.util.Iterator;

/**
 *SetValue
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
	protected
	final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		arg = resArgs(c);
		boolean ok;
		

		switch (n) {
		case 2:
			if (arg[0].isGeoFunction() && arg[0].isGeoFunctionable()) {
				// eg f(x)=x^2
				// SetValue[f,1]
				GeoFunction fun = (GeoFunction)arg[0];
				GeoFunctionable val = (GeoFunctionable) arg[1];
				fun.set(val.getGeoFunction());
				fun.updateRepaint();
			} else if (arg[0].isIndependent() || arg[0].isMoveable()) {
				if (arg[0].isGeoNumeric() && arg[1].isNumberValue()) {
					NumberValue num = (NumberValue) arg[1];
					((GeoNumeric) arg[0]).setValue(num.getDouble());
				} else {
					arg[0].set(arg[1]);
				}
				arg[0].updateRepaint();
			} else if (arg[1].isNumberValue() && arg[0].isGeoNumeric() && arg[0].getParentAlgorithm() instanceof SetRandomValue) {
				// eg a = RandomBetween[0,10]
				SetRandomValue algo = (SetRandomValue) arg[0].getParentAlgorithm();
				algo.setRandomValue(((NumberValue)arg[1]).getDouble());
			} else if (arg[1].isNumberValue() && arg[0].getParentAlgorithm() instanceof AlgoDependentNumber) {
				// eg a = random()
				double val = ((NumberValue)arg[1]).getDouble();
				if (val >= 0 && val <= 1) {
					AlgoDependentNumber al = (AlgoDependentNumber)arg[0].getParentAlgorithm();
					ExpressionNode en = al.getExpression();
					if (en.getOperation().equals(Operation.RANDOM)) {
						GeoNumeric num = ((GeoNumeric)al.getOutput()[0]);
						num.setValue(val);
						num.updateRepaint();
					}
				}
			}
			return;
		case 3:
			if ((ok = (arg[0].isGeoList() && arg[0].isIndependent())) && arg[1].isNumberValue()) {
				GeoList list = (GeoList) arg[0];
				int nn = (int) ((NumberValue) arg[1]).getDouble();

				if (nn < 1 || nn > list.size() + 1)
					throw argErr(app, c.getName(), arg[1]);
				if(nn > list.size()){
					list.add((GeoElement)arg[2].deepCopy(kernelA));
					list.updateRepaint();
					return;
				}
				GeoElement geo = list.get(nn - 1);
				if (geo.isIndependent()) {
					if (geo.isGeoNumeric() && arg[2].isNumberValue()) {
						NumberValue num = (NumberValue) arg[2];
						((GeoNumeric) geo).setValue(num.getDouble());
					} else {
						geo.set(arg[2]);						
					}
				}
				else App.debug(geo.getParentAlgorithm());

				geo.updateRepaint();

				// update the list too if necessary
				if (!geo.isLabelSet()) { // eg like first element of {1,2,a}
					Iterator<GeoElement> it = kernelA.getConstruction()
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
			} else
				throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			return;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
