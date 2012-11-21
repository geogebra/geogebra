package geogebra3D.kernel3D;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoAxisND;
import geogebra.common.main.App;

/**
 * @author ggb3D
 * 
 * Construction for 3D stuff
 *
 */
public class Construction3D extends Construction {

	
	// axis objects
	private GeoAxis3D //xAxis3D, yAxis3D, 
	zAxis3D;
	private GeoPlane3DConstant xOyPlane;
	private GeoSpace space;
	private GeoClippingCube3D clippingCube;
	private String //xAxis3DLocalName, yAxis3DLocalName, 
	zAxis3DLocalName, xOyPlaneLocalName, spaceLocalName;

	
	/** default constructor
	 * @param kernel3D current kernel
	 */
	public Construction3D(Kernel3D kernel3D) {
		super(kernel3D);
		
		/*
		Application.debug("geoTable :\n"+geoTable);
		
		GeoElement ret = (GeoElement) geoTable.get("xAxis3D");
		Application.debug("get xAxis3D : "+ret);
		*/
		
	}
	
	
	@Override
	protected void initAxis(){
		super.initAxis();
		
		
		zAxis3D = new GeoAxis3D(this,GeoAxisND.Z_AXIS_3D);
		
		xOyPlane = new GeoPlane3DConstant(this,GeoPlane3DConstant.XOY_PLANE);
		
		space = new GeoSpace(this);
		
		clippingCube = new GeoClippingCube3D(this);
		
	}
	
	
	public GeoAxisND getXAxis3D(){
		return super.getXAxis();
	}
	public GeoAxisND getYAxis3D(){
		return super.getYAxis();
	}
	public GeoAxis3D getZAxis3D(){
		return zAxis3D;
	}
	public GeoPlane3DConstant getXOYPlane(){
		return xOyPlane;
	}
	public GeoSpace getSpace(){
		return space;
	}
	public GeoClippingCube3D getClippingCube(){
		return clippingCube;
	}

	/**
	 * creates the ConstructionDefaults consDefaults
	 */
	@Override
	public void newConstructionDefaults(){
		consDefaults = new ConstructionDefaults3D(this);
	}
	
	
	
	
	@Override
	protected void initGeoTables() {		
		super.initGeoTables();
		

		// add axes labels both in English and current language
		if (GeoGebraConstants.IS_PRE_RELEASE){
			geoTable.put("xAxis3D", getXAxis());
			geoTable.put("yAxis3D", getYAxis());
			geoTable.put("zAxis3D", zAxis3D);
		}
		geoTable.put("zAxis", zAxis3D);
		geoTable.put("xOyPlane", xOyPlane);
		geoTable.put("space", space);
		
		if (zAxis3DLocalName != null) {
			geoTable.put(zAxis3DLocalName, zAxis3D);
			geoTable.put(xOyPlaneLocalName, xOyPlane);
			geoTable.put(spaceLocalName, space);
		}	
			
	}

	@Override
	public void updateLocalAxesNames() {	
		
		super.updateLocalAxesNames();
		
		
		geoTable.remove(zAxis3DLocalName);
		geoTable.remove(xOyPlaneLocalName);
		geoTable.remove(spaceLocalName);

		App app = getKernel().getApplication();
		zAxis3DLocalName = app.getPlain("zAxis");
		xOyPlaneLocalName = app.getPlain("xOyPlane");
		spaceLocalName = app.getPlain("space");
		
		geoTable.put(zAxis3DLocalName, zAxis3D);	
		geoTable.put(xOyPlaneLocalName, xOyPlane);	
		geoTable.put(spaceLocalName, space);	
		
		
	}
	
	
	@Override
	public Constants isConstantElement(GeoElement geo){
		if (geo==zAxis3D)
			return Constants.Z_AXIS;		
		if (geo==xOyPlane)
			return Constants.XOY_PLANE;
		if (geo==space)
			return Constants.SPACE;
			
		
		return super.isConstantElement(geo);
	}
}
