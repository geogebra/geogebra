package geogebra.mobile.controller;

import geogebra.common.gui.view.algebra.AlgebraController;
import geogebra.common.kernel.Kernel;

/**
 * 
 * @author Thomas Krismayer
 * @see geogebra.common.gui.view.algebra.AlgebraController AlgebraController
 * 
 */
public class MobileAlgebraController extends AlgebraController
{

	public MobileAlgebraController(Kernel kernel)
	{
		super(kernel);
	}

	public void newInput(String text)
	{
		try
		{
//			this.app.getKernel()
//					.getAlgebraProcessor()
//					.processAlgebraCommandNoExceptionHandling(text, true,
//							false, true);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// TODO:
	// add event handler

}
