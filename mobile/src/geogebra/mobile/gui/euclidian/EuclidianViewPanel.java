package geogebra.mobile.gui.euclidian;

import geogebra.mobile.controller.MobileController;

import com.google.gwt.canvas.client.Canvas;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

/**
 * Extends from {@link LayoutPanel}. Holds the instances of the canvas and the
 * euclidianView.
 */
public class EuclidianViewPanel extends LayoutPanel
{
	private Canvas canvas;
	private EuclidianViewM euclidianView;

	/**
	 * Creates the canvas.
	 * 
	 * @see com.google.gwt.canvas.client.Canvas Canvas
	 */
	public EuclidianViewPanel()
	{
		this.addStyleName("euclidianview");
		this.canvas = Canvas.createIfSupported();
	}

	/**
	 * Creates the {@link EuclidianViewM euclidianView} and initializes the canvas
	 * on it.
	 * 
	 * @param ec
	 *          MobileEuclidianController
	 */
	public void initEuclidianView(MobileController ec)
	{
		this.euclidianView = new EuclidianViewM(ec);
		this.euclidianView.initCanvas(this.canvas);

		ec.setView(this.euclidianView);

		add(this.canvas);
	}

	/**
	 * @return euclidianView
	 */
	public EuclidianViewM getEuclidianView()
	{
		return this.euclidianView;
	}

	public void repaint()
	{

		this.euclidianView.repaint();
	}
}
