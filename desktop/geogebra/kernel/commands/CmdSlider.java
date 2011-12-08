package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoAngle;

/**
 * 
 * Slider[<Min>,<Max>,<Increment>,<Speed>,<Width>,<Angle>,<Horizontal>,<
 * Animating>,<Random>]
 * 
 */
class CmdSlider extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSlider(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n < 2 || n > 9)
			throw argNumErr(app, c.getName(), n);
		for (int i = 0; i < Math.min(n, 5); i++)
			if (!arg[i].isNumberValue())
				throw argErr(app, c.getName(), arg[i]);
		for (int i = 5; i < n; i++)
			if (!arg[i].isBooleanValue())
				throw argErr(app, c.getName(), arg[i]);
		GeoNumeric slider;
		if (n > 5 && ((BooleanValue) arg[5]).getBoolean())
			slider = new GeoAngle(kernel.getConstruction());
		else
			slider = new GeoNumeric(kernel.getConstruction());
		slider.setIntervalMin((NumberValue) arg[0]);
		slider.setIntervalMax((NumberValue) arg[1]);
		if (n > 2)
			slider.setAnimationStep((NumberValue) arg[2]);
		if (n > 3)
			slider.setAnimationSpeedObject((NumberValue) arg[3]);
		if (n > 4)
			slider.setSliderWidth(((NumberValue) arg[4]).getDouble());
		if (n > 6)
			slider.setSliderHorizontal(((BooleanValue) arg[6]).getBoolean());
		if (n > 7)
			slider.setAnimating(((BooleanValue) arg[7]).getBoolean());
		if (n > 8)
			slider.setRandom(((BooleanValue) arg[8]).getBoolean());
		slider.setEuclidianVisible(true);
		slider.setLabel(c.getLabel());
		return new GeoElement[] { slider };

	}
}
