package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterBrush;
import geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoAngle;
import geogebra.common.kernel.algos.AlgoAnglePoints;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.plugin.EuclidianStyleConstants;

/**
 * @author ggb3D
 * 
 * Drawable for GeoConic3D
 *
 */
public class DrawAngle3D extends Drawable3DCurves {
	
	
	
	private Coords labelCenter;
	
	
	
	/**
	 * @param view3d the 3D view where the conic is drawn
	 * @param geo the angle to draw
	 */
	public DrawAngle3D(EuclidianView3D view3d, GeoAngle geo) {
		super(view3d,geo);
	}

	

	@Override
	protected void updateColors(){
		updateAlpha();
		setColorsOutlined();
	}
	
	
	


	@Override
	public void drawGeometry(Renderer renderer) {


		renderer.getGeometryManager().draw(getGeometryIndex());


	}

	


	
	

	// method used only if surface is not transparent
	@Override
	public void drawNotTransparentSurface(Renderer renderer){
		
		if(!isVisible()){
			return;
		}
		

		if (getAlpha()<1)
			return;

		setSurfaceHighlightingColor();
		
		drawSurfaceGeometry(renderer);


		
	}
	
	
	private boolean angleVisible;
	
    @Override
	protected boolean isLabelVisible(){
    	return angleVisible && super.isLabelVisible();
    }


	
	private Coords[] drawCoords = new Coords[3];
	

	@Override
	protected boolean updateForItSelf(){

		//update alpha value
		updateColors();
    	
		Renderer renderer = getView3D().getRenderer();
		
		GeoAngle angle = (GeoAngle) getGeoElement();
		double a = angle.getDouble();
		double size = angle.getArcSize() / getView3D().getScale();
		double labelRadius = 1;
		
		angleVisible = true;
		
		AlgoElement algo = angle.getDrawAlgorithm();
		
		if (algo instanceof AlgoAngle) {
			
			if( !((AlgoAngle) algo).getCoordsInD3(drawCoords)){
				setGeometryIndex(-1);
				setSurfaceIndex(-1);
				angleVisible = false;
				return true;
			}
			
			
			Coords center = drawCoords[0];
			Coords v1 = drawCoords[1];
			v1.calcNorm(); 
			double l1 = v1.getNorm();
			v1=v1.mul(1/l1);
			Coords v2 = drawCoords[2];
			v2.calcNorm(); 
			double l2 = v2.getNorm();
			v2=v2.mul(1/l2);
			Coords vn = ((AlgoAngle) algo).getVn();
			
			
			switch (angle.getAngleStyle()) {
			
			case NOTREFLEX:
				if (angle.getRawAngle()>Math.PI)
					vn = vn.mul(-1);
				break;
				
			case ISREFLEX:
				if (angle.getRawAngle()<Math.PI)
					vn = vn.mul(-1);
				break;
			}		
			
			Coords vn2 = vn.crossProduct4(v1);
			double a2 = a/2;
			labelCenter = v1.mul(Math.cos(a2)).add(vn2.mul(Math.sin(a2)));


			//size < points distances / 2
			if (algo instanceof AlgoAnglePoints){
				double l=Math.min(l1, l2)/2;
				if (size>l)
					size=l;
			}
			

			labelRadius=size/1.7;
			
			//90°
			boolean show90degrees = getView3D().getApplication().rightAngleStyle != EuclidianStyleConstants.RIGHT_ANGLE_STYLE_NONE &&
					angle.isEmphasizeRightAngle() &&  
					Kernel.isEqual(a, Kernel.PI_HALF);
			
			// outline
			PlotterBrush brush = renderer.getGeometryManager().getBrush();	
			brush.start(8);
			brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());	
			//arc
			if (show90degrees){
				size *= 0.7071067811865;
				brush.setAffineTexture(0.5f,  0.25f);
				brush.segment(center.add(v1.mul(size)),center.add(v1.mul(size)).add(v2.mul(size)));
				brush.segment(center.add(v2.mul(size)),center.add(v1.mul(size)).add(v2.mul(size)));
			}else{
				brush.setAffineTexture(0f,0f);
				brush.arc(center, v1, vn2, size, 0,a, 60);
				brush.setAffineTexture(0.5f,  0.25f);
			}
			//segments	
			brush.segment(center, center.add(v1.mul(size)));
			brush.segment(center, center.add(v2.mul(size)));
			setGeometryIndex(brush.end());

			// surface
			PlotterSurface surface = renderer.getGeometryManager().getSurface();
			surface.start();
			if (show90degrees){
				surface.parallelogram(center, v1, vn2, size,size);
			}else{
				surface.ellipsePart(center, v1, vn2, size,size, 0,a);
			}
			setSurfaceIndex(surface.end());
			
			//label
			labelCenter = center.add(labelCenter.mul(labelRadius));

		
		}
		
		return true;
	}
	
	
	protected double getStart(){
		return 0;
	}
	
	protected double getExtent(){
		return 2*Math.PI;
	}
	
	
	

	@Override
	protected void updateForView(){
		if (getView3D().viewChangedByZoom()) //update only if zoom occurred
			updateForItSelf();


	}
	
	
	
	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}



	@Override
	public void addToDrawable3DLists(Drawable3DLists lists){
		super.addToDrawable3DLists(lists);
		addToDrawable3DLists(lists,DRAW_TYPE_SURFACES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists){
		super.removeFromDrawable3DLists(lists);
		removeFromDrawable3DLists(lists,DRAW_TYPE_SURFACES);

	}
    

	private void drawSurfaceGeometry(Renderer renderer){

		renderer.setLayer(getLayer()+1f); //+1f to avoid z-fighting with planes and polygons
		renderer.getGeometryManager().draw(getSurfaceIndex());
		renderer.setLayer(0);

	}
    

    @Override
	public void drawTransp(Renderer renderer){
    	if(!isVisible()){
    		return;
    	}


    	if (!hasTransparentAlpha())
    		return;

    	setSurfaceHighlightingColor();

    	drawSurfaceGeometry(renderer);

    }
    
    


	@Override
	public void drawHiding(Renderer renderer){
		if(!isVisible())
			return;

		if (!hasTransparentAlpha())
			return;
		
		
		drawSurfaceGeometry(renderer);
		
	}
	
	@Override
	public Coords getLabelPosition(){
  		return labelCenter;
  	}
	
	
	@Override
	protected void updateLabel(){//TODO remove this and implement all angle cases
		if (labelCenter!=null)
			super.updateLabel();
	}
	

	@Override
	protected float getLabelOffsetX(){
		return super.getLabelOffsetX()-3;
	}
	
	
	@Override
	protected float getLabelOffsetY(){
		return super.getLabelOffsetY()+5;
	}

	 
	
	


}
