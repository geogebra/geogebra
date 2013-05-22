package geogebra3D.euclidian3D;

import geogebra.common.euclidian.Previewable;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoLine3D;

import java.util.ArrayList;

import javax.media.opengl.GL;

/**
 * Class for drawing lines
 * @author matthieu
 *
 */
public class DrawLine3D extends DrawCoordSys1D implements Previewable {

	
	
	/**
	 * common constructor
	 * @param a_view3D
	 * @param line
	 */
	public DrawLine3D(EuclidianView3D a_view3D, GeoLineND line){
		
		super(a_view3D, (GeoElement) line);
	}	
	
	
	
	
	@Override
	protected boolean updateForItSelf(){
		

		updateForItSelf(true);
		return true;

	}
	
	/**
	 * update the drawable when the element changes
	 * @param updateDrawMinMax update min and max values
	 */
	protected void updateForItSelf(boolean updateDrawMinMax){
		

		if (updateDrawMinMax)
			updateDrawMinMax();
		
		super.updateForItSelf();

	}

	/**
	 *  update min and max values
	 */
	protected void updateDrawMinMax(){
		
		GeoLineND line = (GeoLineND) getGeoElement();
				
		Coords o = line.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords v = line.getPointInD(3, 1).getInhomCoordsInSameDimension().sub(o);
	
		double[] minmax = getView3D().getIntervalClipped(
				new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY}, o, v);
		
		setDrawMinMax(minmax[0], minmax[1]);
	}
	
	
	

	@Override
	protected void updateForView(){
		if (getView3D().viewChangedByZoom())
			updateForItSelf();
	}
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	

	/**
	 * constructor for previewable
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoLine3D(a_view3D.getKernel().getConstruction()));
		
		
	}	


	
	@Override
	protected void setLineTextureNotHidden(Renderer renderer){
		renderer.getTextures().setDashFromLineType(getGeoElement().getLineType(), GL.GL_TEXTURE0); 
		//renderer.getTextures().loadTextureLinear(Textures.LINE_FADING, GL.GL_TEXTURE1); 
	}
	
	@Override
	protected void doSetLineTextureHidden(Renderer renderer){
		renderer.getTextures().setDashFromLineTypeHidden(getGeoElement().getLineType()); 
		//renderer.getTextures().loadTextureLinear(Textures.LINE_FADING, GL.GL_TEXTURE1); 
	}
	

	@Override
	protected void setTexture(PlotterBrush brush, double[] minmax){
		//brush.setFadingTexture( (float) ((0.5-minmax[0])/(minmax[1]-minmax[0])),  0.25f);
		brush.setAffineTexture( (float) ((0.5-minmax[0])/(minmax[1]-minmax[0])),  0.25f);
		
	}

}
