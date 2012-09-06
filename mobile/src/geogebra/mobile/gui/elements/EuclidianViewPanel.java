package geogebra.mobile.gui.elements;

import geogebra.mobile.controller.MobileEuclidianController;
import geogebra.mobile.euclidian.EuclidianViewM;

import com.google.gwt.canvas.client.Canvas;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

/**
 * Extends from {@link LayoutPanel}.
 * Holds the instances of the canvas and the euclidianView.
 */

public class EuclidianViewPanel extends LayoutPanel
{
	private Canvas canvas; 	
	private EuclidianViewM euclidianView; 
	
	/**
	 * Creates the canvas.
	 * @see com.google.gwt.canvas.client.Canvas Canvas
	 */
	public EuclidianViewPanel()
	{
		this.addStyleName("euclidianview");		
		this.canvas = Canvas.createIfSupported(); 
	}
	
	//for tests only
	public EuclidianViewM getEuclidianViewM()
	{
		return this.euclidianView;
	}
	//end
	
	
	/**
	 * Creates the {@link EuclidianViewM euclidianView} and initializes the canvas on it.
	 * 
	 * @param ec MobileEuclidianController
	 */
	public void initEuclidianView(MobileEuclidianController ec){
		this.euclidianView = new EuclidianViewM(ec); 
		this.euclidianView.initCanvas(this.canvas); 

		ec.setView(this.euclidianView); 
		
		add(this.canvas); 		
	}
	
	/**
	 * @return euclidianView
	 */
	public EuclidianViewM getEuclidianView(){
		return this.euclidianView; 
	}
}
