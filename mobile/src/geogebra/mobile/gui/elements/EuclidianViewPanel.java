package geogebra.mobile.gui.elements;

import geogebra.common.euclidian.EuclidianController;
import geogebra.mobile.euclidian.EuclidianViewM;

import com.google.gwt.canvas.client.Canvas;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

public class EuclidianViewPanel extends LayoutPanel
{
	private Canvas canvas; 	
	private EuclidianViewM euclidianView; 
	
	public EuclidianViewPanel()
	{
		this.addStyleName("euclidianview");		
		this.canvas = Canvas.createIfSupported(); 
	}
	
	public void initEuclidianView(EuclidianController ec){
		this.euclidianView = new EuclidianViewM(ec); 
		this.euclidianView.initCanvas(this.canvas); 
	
		add(this.canvas); 		
	}
}
