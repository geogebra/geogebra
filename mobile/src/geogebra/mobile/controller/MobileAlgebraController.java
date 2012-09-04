package geogebra.mobile.controller;

import com.google.gwt.event.dom.client.KeyUpEvent;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.view.algebra.AlgebraController;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

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

	/**
	 * @see geogebra.web.gui.inputbar.AlgebraInputW#onKeyUp(KeyUpEvent event)
	 * 
	 * @param input
	 *            the new command
	 */
	public void newInput(String input)
	{
		try
		{
			this.kernel.clearJustCreatedGeosInViews();
			if (input == null || input.length() == 0)
			{
				return;
			}

//			this.app.setScrollToShow(true);
			GeoElement[] geos;
			try
			{
				if (input.startsWith("/"))
				{
					String cmd = input.substring(1);
					this.app.getPythonBridge().eval(cmd);
					geos = new GeoElement[0];
				} else
				{
					geos = this.kernel.getAlgebraProcessor()
							.processAlgebraCommandNoExceptionHandling(input,
									true, false, true);

					// need label if we type just eg
					// lnx
					if (geos.length == 1 && !geos[0].labelSet)
					{
						geos[0].setLabel(geos[0].getDefaultLabel());
					}

				}
			} catch (Exception e)
			{
				e.printStackTrace();
				return;
			} catch (MyError e)
			{
				e.printStackTrace();
				return;
			}

			// create texts in the middle of the visible view
			// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
			if (geos.length > 0 && geos[0] != null && geos[0].isGeoText())
			{
				GeoText text = (GeoText) geos[0];
				if (!text.isTextCommand() && text.getStartPoint() == null)
				{

					Construction cons = text.getConstruction();
					EuclidianViewInterfaceCommon ev = this.app
							.getActiveEuclidianView();

					boolean oldSuppressLabelsStatus = cons
							.isSuppressLabelsActive();
					cons.setSuppressLabelCreation(true);
					GeoPoint p = new GeoPoint(text.getConstruction(), null,
							(ev.getXmin() + ev.getXmax()) / 2,
							(ev.getYmin() + ev.getYmax()) / 2, 1.0);
					cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

					try
					{
						text.setStartPoint(p);
						text.update();
					} catch (CircularDefinitionException e1)
					{
						e1.printStackTrace();
					}
				}
			}
			// this.app.setScrollToShow(false);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// TODO:
	// add event handler

}
