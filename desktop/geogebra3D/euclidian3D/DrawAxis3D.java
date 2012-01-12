package geogebra3D.euclidian3D;

import geogebra.common.factories.FormatFactory;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.util.NumberFormatAdapter;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;

import java.awt.Color;
import java.util.TreeMap;

/**
 * Class for drawing axis (Ox), (Oy), ...
 * 
 * @author matthieu
 *
 */
public class DrawAxis3D extends DrawLine3D {
	
	private TreeMap<String, DrawLabel3D>  labels;
	
	

	/** distance between two ticks */
    //private double distance = 1;
    


	/** min/max when the update is finished */
	private double drawMinFinal, drawMaxFinal;
	
	/**
	 * common constructor
	 * @param view3D
	 * @param axis3D
	 */
	public DrawAxis3D(EuclidianView3D view3D, GeoAxisND axis3D){
		
		super(view3D, axis3D);
		
		super.setDrawMinMax(-2, 2);
		
		labels = new TreeMap<String, DrawLabel3D>();
		
	}	
	
	
	/**
	 * drawLabel is used here for ticks
	 */
    public void drawLabel(Renderer renderer){



    	//Application.debug(getGeoElement()+": "+getGeoElement().isLabelVisible());
    	
		if(!getGeoElement().isEuclidianVisible())
			return;
		
    	if (!getGeoElement().isLabelVisible())
    		return;

    	
    	
    	for(DrawLabel3D label : labels.values())
    		label.draw(renderer);
    		
    	
    	super.drawLabel(renderer);
    	
    	
    }
    	

    
    public void setWaitForReset(){
    	super.setWaitForReset();
    	for(DrawLabel3D label : labels.values())
    		label.setWaitForReset();
    }

    	
    protected void updateLabel(){
    	
  		//draw numbers
  		GeoAxisND axis = (GeoAxisND) getGeoElement();
  		
		NumberFormatAdapter numberFormat = axis.getNumberFormat();
		double distance = axis.getNumbersDistance();
		
		//Application.debug("drawMinMax="+getDrawMin()+","+getDrawMax());
		double[] minmax = getDrawMinMax(); 
		
    	int iMin = (int) (minmax[0]/distance);
    	int iMax = (int) (minmax[1]/distance);
    	int nb = iMax-iMin+1;
    	
    	//Application.debug("iMinMax="+iMin+","+iMax);
    	
    	if (nb<1){
    		Application.debug("nb="+nb);
    		//labels = null;
    		return;
    	}
    	
    	
    	//sets all already existing labels not visible
    	for(DrawLabel3D label : labels.values())
    		label.setIsVisible(false);
    	
    	
    	for(int i=iMin;i<=iMax;i++){
    		double val = i*distance;
    		Coords origin = ((GeoAxisND) getGeoElement()).getPointInD(3,val);
    		
    		//draw numbers
    		String strNum = getView3D().getKernel().formatPiE(val,numberFormat);

    		//check if the label already exists
    		DrawLabel3D label = labels.get(strNum);
    		if (label!=null){
    			//sets the label visible
    			label.setIsVisible(true);
    			label.update(strNum, getView3D().getApplication().getPlainFont(), 
    					geogebra.awt.Color.getAwtColor(getGeoElement().getObjectColor()),
    					origin.copyVector(),
    					axis.getNumbersXOffset(),axis.getNumbersYOffset());
    			//TODO optimize this
    		}else{
    			//creates new label
    			label = new DrawLabel3D(getView3D());
    			label.setAnchor(true);
    			label.update(strNum, getView3D().getApplication().getPlainFont(), 
    					geogebra.awt.Color.getAwtColor(getGeoElement().getObjectColor()),
    					origin.copyVector(),
    					axis.getNumbersXOffset(),axis.getNumbersYOffset());
    			labels.put(strNum, label);
    		}
       		
    	}
    	
		
		// update end of axis label
    	label.setAnchor(true);
		label.update(axis.getAxisLabel(), getView3D().getApplication().getPlainFont(), 
				geogebra.awt.Color.getAwtColor(getGeoElement().getObjectColor()),
				((GeoAxisND) getGeoElement()).getPointInD(3,minmax[1]),
				getGeoElement().labelOffsetX,//-4,
				getGeoElement().labelOffsetY//-6
		);

		
    	
    }
    


    
    protected boolean updateForItSelf(){
    	
    	//updateColors();
    	
    	setLabelWaitForUpdate();
    	
    	double[] minmax = getDrawMinMax(); 
    	
    	PlotterBrush brush = getView3D().getRenderer().getGeometryManager().getBrush();
       	brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);
       	brush.setTicks(PlotterBrush.TICKS_ON);
       	brush.setTicksDistance( (float) ((GeoAxisND) getGeoElement()).getNumbersDistance());
       	brush.setTicksOffset((float) (-minmax[0]/(minmax[1]-minmax[0])));
       	super.updateForItSelf(false);
       	brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
       	brush.setTicks(PlotterBrush.TICKS_OFF);
       	
       	
       	return timesUpForUpdate();
       	
    }
    
    

    
    
    /**
     * update values for ticks and labels
     */
    public void updateDecorations(){
    	

		
		//update decorations
		GeoAxisND axis = (GeoAxisND) getGeoElement();
		

    	//gets the direction vector of the axis as it is drawn on screen
		Coords v = getView3D().getToScreenMatrix().mul(axis.getDirectionInD3());
    	v.set(3, 0); //set z-coord to 0
    	//double vScale = v.norm(); //axis scale, used for ticks distance
    	double vScale = getView3D().getScale(); //TODO use different scales for x/y/z
    	//Application.debug("vScale="+vScale);
    	
    	//calc orthogonal offsets
    	int vx = (int) (v.get(1)*1.5*axis.getTickSize()/vScale);
    	int vy = (int) (v.get(2)*1.5*axis.getTickSize()/vScale);
    	int xOffset = -vy;
    	int yOffset = vx;
    	
    	
    	//if (yOffset>0){
    	if (axis.getType()==GeoAxisND.X_AXIS){
    		xOffset = -xOffset;
    		yOffset = -yOffset;
    	}
    	
    	
    	//interval between two ticks
    	//Application.debug("vscale : "+vScale);
    	double maxPix = 100; // only one tick is allowed per maxPix pixels
		double units = maxPix / vScale;
		
		NumberFormatAdapter numberFormat = FormatFactory.prototype.getNumberFormat();
		//TODO see EuclidianView::setAxesIntervals	and Kernel::axisNumberDistance	
		double distance = getView3D().getKernel().axisNumberDistance(units, numberFormat);

		axis.updateDecorations(distance, numberFormat, 
				xOffset, yOffset,
				((-vx-xOffset)*3)/2,//-vx,//-2*xOffset,
				((-vy-yOffset)*3)/2//-vy//-2*yOffset
				);
		
    	
    }
    
    
    
    /**
     * @return distance between two ticks
     */
    public double getNumbersDistance(){
    	/*
    	double dt = (System.currentTimeMillis()-(time+TIME_WAIT))*TIME_FACTOR;
    	
    	//update the distance from the geo only if rotation has ended
    	if (dt>0)
    		distance = ((GeoAxis3D) getGeoElement()).getNumbersDistance();

    	return distance;
    	*/
    	return ((GeoAxisND) getGeoElement()).getNumbersDistance();
    }
    
    
	protected void updateForView(){

	}
	
	
	// depth is not used in extended way
	public void updateDrawMinMax(){
		updateDrawMinMax(false);
	}
	
	
	
	
	
	
	
	public void setDrawMinMax(double drawMin, double drawMax){
		setTime();
		drawMinFinal = drawMin;
		drawMaxFinal = drawMax;
	}
	
	public void setDrawMinMaxImmediatly(double drawMin, double drawMax){
		drawMinFinal = drawMin;
		drawMaxFinal = drawMax;
		super.setDrawMinMax(drawMinFinal, drawMaxFinal);
	}
	
	public double[] getDrawMinMax(){
		long deltaT = getDeltaT();

		if (deltaT>0){
			if (deltaT>TIME_DURATION){
				super.setDrawMinMax(drawMinFinal, drawMaxFinal);
			}else{
				double[] minmaxIni = super.getDrawMinMax();
				double[] minmax = new double[2];
				double dt = (double) deltaT*TIME_FACTOR;
				minmax[0]=drawMinFinal*dt+minmaxIni[0]*(1-dt);
				minmax[1]=drawMaxFinal*dt+minmaxIni[1]*(1-dt);
				return minmax;
			}
		}

		return super.getDrawMinMax();
	}
	

	
	
}
