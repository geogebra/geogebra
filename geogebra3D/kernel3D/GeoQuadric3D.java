package geogebra3D.kernel3D;

import java.awt.geom.AffineTransform;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.RegionParameters;
import geogebra.kernel.Matrix.CoordMatrix;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic.Functional2Var;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.kernel.kernelND.GeoSegmentND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.kernel.kernelND.Region3D;
import geogebra.main.Application;
import geogebra3D.euclidian3D.Drawable3D;



/** class describing quadric for 3D space
 * @author matthieu
 *
 *           ( A[0]  A[4]  A[5]  A[7])
 *  matrix = ( A[4]  A[1]  A[6]  A[8])
 *           ( A[5]  A[6]  A[2]  A[9])
 *           ( A[7]  A[8]  A[9]  A[3])
 *
 */
public class GeoQuadric3D extends GeoQuadricND
implements GeoElement3DInterface, Functional2Var, Region3D{
	
	

	
	private static String[] vars3D = { "x\u00b2", "y\u00b2",  "z\u00b2", "x y", "x z", "y z", "x", "y" , "z" };
	
	private CoordMatrix4x4 eigenMatrix = CoordMatrix4x4.Identity();
	

	public GeoQuadric3D(Construction c) {
		super(c,3);
		
		//TODO merge with 2D eigenvec
		eigenvecND = new Coords[3];
		for (int i=0;i<3;i++){
			eigenvecND[i] = new Coords(4);
			eigenvecND[i].set(i+1,1);
		}
		
		//diagonal (diagonalized matrix)
		diagonal = new double[4];
		
	}
	
	public GeoQuadric3D(GeoQuadric3D quadric){
		this(quadric.getConstruction());
		set(quadric);
	}
	
	

	 public Coords getMidpointND(){
		 return getMidpoint3D();
	 }

	

	
	

	
	
	////////////////////////////////
	// SPHERE
	
	protected void setSphereNDMatrix(Coords M, double r){
		super.setSphereNDMatrix(M, r);
		
		
		//eigen matrix
		eigenMatrix = new CoordMatrix4x4();
		eigenMatrix.setOrigin(getMidpoint3D());
		eigenMatrix.setVx(new Coords(getHalfAxis(0),0,0,0));
		eigenMatrix.setVy(new Coords(0,getHalfAxis(1),0,0));
		eigenMatrix.setVz(new Coords(0,0,getHalfAxis(2),0));
	}
	
	public void setSphereND(GeoPointND M, GeoSegmentND segment){
		//TODO
	}
	
	public void setSphereND(GeoPointND M, GeoPointND P){
		//TODO do this in GeoQuadricND, implement degenerate cases
		setSphereNDMatrix(M.getInhomCoordsInD(3), M.distance(P));
	}
	
	
	////////////////////////////////
	// CONE
	
	public void setCone(GeoPointND origin, GeoVectorND direction, double angle){
		
		// check midpoint
		defined = ((GeoElement) origin).isDefined() && !origin.isInfinite(); 
		
		// check direction
		
		
		
		// check angle
		double r;
		double c = Math.cos(angle);
		double s = Math.sin(angle);

		if (c<0 || s<0) 
			defined = false;
		else if (Kernel.isZero(c))
			defined = false;//TODO if c=0 then draws a plane
		else if (Kernel.isZero(s))
			defined = false;//TODO if s=0 then draws a line
		else{
			r=s/c;
			setCone(origin.getCoordsInD(3), direction.getCoordsInD(3), r);
		} 
		

	}
	
	public void setCone(Coords origin, Coords direction, double r){
		 
		// set center
		setMidpoint(origin.get());
		
		// set direction
		eigenvecND[2] = direction;
		
		// set others eigen vecs
		Coords[] ee = direction.completeOrthonormal();
		eigenvecND[0] = ee[0];
		eigenvecND[1] = ee[1];
		
		// set halfAxes = radius	
		for (int i=0;i<2;i++)
			halfAxes[i] = r;
		
		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = -r*r;
		diagonal[3] = 0;
		
		// set matrix
		setMatrixFromEigen();
		
		//eigen matrix
		eigenMatrix = new CoordMatrix4x4();
		eigenMatrix.setOrigin(getMidpoint3D());

		eigenMatrix.setVx(eigenvecND[0].mul(getHalfAxis(0)));
		eigenMatrix.setVy(eigenvecND[1].mul(getHalfAxis(1)));
		eigenMatrix.setVz(eigenvecND[2]);
			
		// set type
		type = QUADRIC_CONE;
	}
	
	

	////////////////////////////////
	// CONE
	
	public void setCylinder(GeoPointND origin, Coords direction, double r){
		
		// check midpoint
		defined = ((GeoElement) origin).isDefined() && !origin.isInfinite(); 
		
		// check direction
		
		
		
		// check radius
		if (kernel.isZero(r)) {
			r = 0;
		} 
		else if (r < 0) {
			defined = false;
		}					

		if (defined) {
			setCylinder(origin.getCoordsInD(3), direction, r);
		} 
		

	}
	
	public void setCylinder(Coords origin, Coords direction, double r){
		 
		// set center
		setMidpoint(origin.get());
		
		// set direction
		eigenvecND[2] = direction;
		

		// set others eigen vecs
		Coords[] ee = direction.completeOrthonormal();
		eigenvecND[0] = ee[0];
		eigenvecND[1] = ee[1];
		
		// set halfAxes = radius	
		for (int i=0;i<2;i++)
			halfAxes[i] = r;
		
		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = 0;
		diagonal[3] = -r*r;
		
		// set matrix
		setMatrixFromEigen();
		
		//eigen matrix
		eigenMatrix = new CoordMatrix4x4();
		eigenMatrix.setOrigin(getMidpoint3D());

		eigenMatrix.setVx(eigenvecND[0].mul(getHalfAxis(0)));
		eigenMatrix.setVy(eigenvecND[1].mul(getHalfAxis(1)));
		eigenMatrix.setVz(eigenvecND[2]);
		
		
		// set type
		type = QUADRIC_CYLINDER;
	}

	
	

	
	
	

	///////////////////////////////
	// GeoElement
	
	
    public GeoElement copy() {

        return new GeoQuadric3D(this);

    }

    public int getGeoClassType() {

        return GeoElement3D.GEO_CLASS_QUADRIC;

    }
    
    


    protected String getTypeString() {
		switch (type) {
		case GeoQuadric3D.QUADRIC_SPHERE: 
			return "Sphere";
		case GeoQuadric3D.QUADRIC_CYLINDER: 
			return "Cylinder";
		case GeoQuadric3D.QUADRIC_CONE: 
			return "Cone";
 		default:
			return "Quadric";
		}                       


    }

    public boolean isEqual(GeoElement Geo) {

        return false;

    }

    public void set(GeoElement geo) {

    	GeoQuadric3D quadric = (GeoQuadric3D) geo;
    	
    	// copy everything
		toStringMode = quadric.toStringMode;
		type = quadric.type;
		for (int i = 0; i < 10; i++)
			matrix[i] = quadric.matrix[i]; // flat matrix A   
		
		for (int i=0; i<3; i++){
			eigenvecND[i].set(quadric.eigenvecND[i]);
			halfAxes[i] = quadric.halfAxes[i];
		}

		setMidpoint(quadric.getMidpoint().get());
		
		eigenMatrix = new CoordMatrix4x4();
		eigenMatrix.set(quadric.eigenMatrix);

		defined = quadric.defined;		
    	
    }



    public boolean showInAlgebraView() {

        return true;

    }

    protected boolean showInEuclidianView() {

        return true;

    }


    
    protected StringBuilder buildValueString() {
    	
    	sbToValueString().setLength(0);	
    	
    	switch (type) {					
    	case QUADRIC_SPHERE :	
    		buildSphereNDString();
    		break;
    	case QUADRIC_CONE:
    	case QUADRIC_CYLINDER:
    		double[] coeffs = new double[10];
    		coeffs[0] = matrix[0]; // x²
    		coeffs[1] = matrix[1]; // y²
    		coeffs[2] = matrix[2]; // z²
    		coeffs[9] = matrix[3]; // constant
    		
    		coeffs[3] = 2 * matrix[4]; // xy        
    		coeffs[4] = 2 * matrix[5]; // xz
    		coeffs[5] = 2 * matrix[6]; // yz
    		coeffs[6] = 2 * matrix[7]; // x      
    		coeffs[7] = 2 * matrix[8]; // y
    		coeffs[8] = 2 * matrix[9]; // z
    		
    		return kernel.buildImplicitEquation(coeffs, vars3D, false, true, '=');
    	}
    	
    	return sbToValueString;
    }

    public String getClassName() {

        return "GeoQuadric";

    }


    
	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}



	public boolean isGeoElement3D(){
		return true;
	}


	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}


	
	

	/////////////////////////////////////////
	// SURFACE (u,v)->(x,y,z) INTERFACE
	/////////////////////////////////////////
	
	public Coords evaluatePoint(double u, double v){
		
		Coords eigenRet;
		
		switch (type){
		case QUADRIC_SPHERE :
			eigenRet = new Coords(Math.cos(u)*Math.cos(v),Math.sin(u)*Math.cos(v),Math.sin(v),1);
			break;
		case QUADRIC_CONE :
			double v2 = Math.abs(v);
			eigenRet = new Coords(Math.cos(u)*v2,Math.sin(u)*v2,v,1);
			break;
		case QUADRIC_CYLINDER :
			eigenRet = new Coords(Math.cos(u),Math.sin(u),v,1);
			break;
		default:
			eigenRet = null;
			break;
		}
		
		return eigenMatrix.mul(eigenRet);
	}
	

	public Coords evaluateNormal(double u, double v){
				
		Coords n;
		
		switch (type){
		case QUADRIC_SPHERE :
			return new Coords(
					Math.cos(u)*Math.cos(v),
					Math.sin(u)*Math.cos(v),
					Math.sin(v),0);
			
		case QUADRIC_CONE :

			double r = getHalfAxis(0);
			double r2 = Math.sqrt(1+r*r);
			if (v<0)
				r=-r;
			
			n = (Coords) 
			getEigenvec3D(1).mul(Math.sin(u)/r2).add(
					getEigenvec3D(0).mul(Math.cos(u)/r2).add(
							getEigenvec3D(2).mul(-r/r2)
					)
			);
			
			return n;
			
			
		case QUADRIC_CYLINDER :
			
			n = (Coords) 
			getEigenvec3D(1).mul(Math.sin(u)).add(
					getEigenvec3D(0).mul(Math.cos(u))
			);
			
			return n;
			
		default:
			return null;
		}
		
		
	}
	
	
	
	
	
	public double getMinParameter(int index) {
		
		switch (type){
		case QUADRIC_SPHERE :
			switch(index){
			case 0: //u
			default:
				return 0;
			case 1: //v
				return -Math.PI/2;
			}
		case QUADRIC_CONE :
		case QUADRIC_CYLINDER :
			switch(index){
			case 0: //u
			default:
				return 0;
			case 1: //v
				return Double.NEGATIVE_INFINITY;
			}
			
			
		default:
			return 0;
		}

		
	}
	

	public double getMaxParameter(int index) {
		
		switch (type){
		case QUADRIC_SPHERE :
			switch(index){
			case 0: //u
			default:
				return 2*Math.PI; 
			case 1: //v
				return Math.PI/2;
			}
			
		case QUADRIC_CONE :
		case QUADRIC_CYLINDER :
			switch(index){
			case 0: //u
			default:
				return 2*Math.PI; 
			case 1: //v
				return Double.POSITIVE_INFINITY;
			}
		default:
			return 0;
		}
	}
	
	
	
	
	
	///////////////////////////////////////////
	// GEOELEMENT3D INTERFACE
	///////////////////////////////////////////


	private Drawable3D drawable3D = null;

	public Drawable3D getDrawable3D() {
		return drawable3D;
	}
	
	public void setDrawable3D(Drawable3D d){
		drawable3D = d;
	}
	
	





	public CoordMatrix4x4 getDrawingMatrix() {
		// TODO Auto-generated method stub
		return null;
	}





	public GeoElement getGeoElement2D() {
		// TODO Auto-generated method stub
		return null;
	}




	

	public Coords getLabelPosition(){
		return new Coords(4); //TODO
	}





	public Coords getMainDirection() {
		// TODO create with parameter coord where is looked at
		return new Coords(0,0,1,0);
	}





	public boolean hasGeoElement2D() {
		// TODO Auto-generated method stub
		return false;
	}













	public void setDrawingMatrix(CoordMatrix4x4 aDrawingMatrix) {
		// TODO Auto-generated method stub
		
	}





	public void setGeoElement2D(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	
	
	///////////////////////////////////////////////////
	// REGION 3D INTERFACE
	///////////////////////////////////////////////////
	
	
	public boolean isRegion(){
		return true;
	}

	protected Coords getNormalProjectionParameters(Coords coords){
		
		Coords eigenCoords = eigenMatrix.solve(coords);
		double x = eigenCoords.getX();
		double y = eigenCoords.getY();
		double z = eigenCoords.getZ();
		
		double u,v,r;
		Coords parameters;
		
		switch(getType()){
		case QUADRIC_SPHERE:
			u = Math.atan2(y, x);
			r = Math.sqrt(x*x+y*y);
			v = Math.atan2(z, r);

			parameters = new Coords(u,v);
			return parameters;

		case QUADRIC_CONE:	
		case QUADRIC_CYLINDER:
			u = Math.atan2(y, x);
			parameters = new Coords(u,z);
			return parameters;
			
		default:
			Application.printStacktrace("TODO -- type: "+getType());
			return null;
		}
	}

	public Coords[] getNormalProjection(Coords coords) {
		
		Coords parameters = getNormalProjectionParameters(coords);

		if (parameters==null)
			return null;
		else
			return new Coords[]{getPoint(parameters.getX(),parameters.getY()), parameters};


	}



	public Coords getPoint(double u, double v) {
		return evaluatePoint(u,v);
	}
	


	public Coords[] getProjection(Coords oldCoords, Coords willingCoords, Coords willingDirection) {
		// TODO Auto-generated method stub
		return null;
	}



	public boolean isInRegion(GeoPointND P) {
		
		Coords coords = P.getCoordsInD(3);
		//calc tP.S.P
		return Kernel.isZero(coords.transposeCopy().mul(getSymetricMatrix().mul(coords)).get(1,1));
	}



	public boolean isInRegion(double x0, double y0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 
	 * @param p
	 * @return direction from p to center (midpoint, or axis for cone, cylinder...)
	 */
	private Coords getDirectionToCenter(Coords p){
		switch (getType()){
		case QUADRIC_SPHERE:
			return getMidpoint3D().sub(p);
		case QUADRIC_CONE:
		case QUADRIC_CYLINDER:
			Coords eigenCoords = eigenMatrix.solve(p);
			//project on eigen xOy plane
			Coords eigenDir = new Coords(eigenCoords.getX(),eigenCoords.getY(),0,0);
			return eigenMatrix.mul(eigenDir).normalized().mul(-1);
		default:
			return null;
		}
	}



	public void pointChangedForRegion(GeoPointND P) {
		
		GeoPoint3D p = (GeoPoint3D) P;
		
		Coords willingCoords = p.getWillingCoords();
		if (willingCoords==null)
			willingCoords = P.getCoordsInD(3);
		else
			p.setWillingCoords(null);

		Coords willingDirection = p.getWillingDirection();
		if (willingDirection==null)
			willingDirection = getDirectionToCenter(willingCoords);
		else{
			//willingDirection = willingDirection.mul(-1); //to get the point closest to the eye
			p.setWillingDirection(null);
		}
		//Application.debug("direction=\n"+willingDirection+"\ncoords=\n"+willingCoords);
		
		//compute intersection
		CoordMatrix qm = getSymetricMatrix();
    	CoordMatrix pm = new CoordMatrix(4,2);
    	pm.setVx(willingDirection);
    	pm.setOrigin(willingCoords);
    	CoordMatrix pmt = pm.transposeCopy();
    	
    	//sets the solution matrix from line and quadric matrix
    	CoordMatrix sm = pmt.mul(qm).mul(pm);
    	
    	//Application.debug("sm=\n"+sm);
    	double a=sm.get(1, 1);
    	double b=sm.get(1, 2);
    	double c=sm.get(2,2);
    	double Delta=b*b-a*c;
    	
    	double t;
    	if (Delta>=0){
       		double t1=(-b-Math.sqrt(Delta))/a;
       		double t2=(-b+Math.sqrt(Delta))/a;
       		t=Math.min(t1, t2);//gets the point closer to the willing coords
       		
    	}else{
    		t=-b/a; //get closer point (in some "eigen coord sys")
    	}
    		
    	Coords[] coords = getNormalProjection(willingCoords.add(willingDirection.mul(t)));
    	
		RegionParameters rp = p.getRegionParameters();
    	rp.setT1(coords[1].get(1));rp.setT2(coords[1].get(2));
    	rp.setNormal(evaluateNormal(coords[1].get(1),coords[1].get(2)));
    	p.setCoords(coords[0], false);
		p.updateCoords();
		
	}


	
	
	

	public void regionChanged(GeoPointND P) {
		
		GeoPoint3D p = (GeoPoint3D) P;
		RegionParameters rp = p.getRegionParameters();
    	Coords coords = getPoint(rp.getT1(),rp.getT2());
    	p.setCoords(coords, false);
		p.updateCoords();
		
		
		
	}

	






}
