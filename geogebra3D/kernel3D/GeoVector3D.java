package geogebra3D.kernel3D;

import geogebra.kernel.AlgoDependentVector;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Locateable;
import geogebra.kernel.PathParameter;
import geogebra.kernel.Matrix.CoordMatrix;
import geogebra.kernel.Matrix.CoordMatrix4x4;
import geogebra.kernel.Matrix.Coords;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;


/**
 * 3D vector class
 * @author ggb3D
 *
 */
public class GeoVector3D extends GeoVec4D
implements GeoVectorND, Locateable, Vector3DValue{
	
	
	private GeoPointND startPoint;
	
	private CoordMatrix matrix;
	
	private Coords labelPosition;

	/** simple constructor
	 * @param c
	 */
	public GeoVector3D(Construction c) {
		super(c);
		matrix = new CoordMatrix(4,2);
	}

	/** simple constructor with (x,y,z) coords
	 * @param c
	 * @param x
	 * @param y
	 * @param z
	 */
	public GeoVector3D(Construction c, double x, double y, double z) {
		super(c,x,y,z,0);
		matrix = new CoordMatrix(4,2);
	}
	
	
	public void setCoords(double[] vals){
		super.setCoords(vals);
		
		if (matrix == null) matrix = new CoordMatrix(4,2);
		
		//sets the drawing matrix 
		matrix.set(getCoords(), 1);
		
		

		
		
		setDrawingMatrix(new CoordMatrix4x4(matrix));
		
	}
	
	
	/**
	 * update the start point position
	 */
	public void updateStartPointPosition(){
		
		if(startPoint!=null)
			matrix.set(startPoint.getCoordsInD(3),2);
		else{
			for(int i=1;i<4;i++)
				matrix.set(i, 2, 0.0);
			matrix.set(4, 2, 1.0);
		}
		
		setDrawingMatrix(new CoordMatrix4x4(matrix));
		labelPosition = matrix.getOrigin().add(matrix.getVx().mul(0.5));
	}

	
	public Coords getLabelPosition(){
		return labelPosition;
	}



	public GeoElement copy() {
		GeoVector3D ret = new GeoVector3D(getConstruction());
		ret.set(this);
		return ret;
	}


	public int getGeoClassType() {
		return GEO_CLASS_VECTOR3D;		
	}


	protected String getTypeString() {
		return "Vector3D";
	}



    public boolean isDefined() {
    	return (!(Double.isNaN(getX()) || Double.isNaN(getY()) || Double.isNaN(getZ()) || Double.isNaN(getW())));        
    }

	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}


	public void set(GeoElement geo) {
		if (geo.isGeoVector()) {
	    	GeoVectorND v = (GeoVectorND) geo;  
	    	setCoords(v.getCoordsInD(3).get());
	    	try {//TODO see GeoVector
				setStartPoint(v.getStartPoint());
			} catch (CircularDefinitionException e) {
				e.printStackTrace();
			}
			
    	}

	}


    public void setUndefined() {     
    	setCoords(Double.NaN, Double.NaN, Double.NaN, Double.NaN);        
    } 
    


	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}


	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}



	
	public String getClassName() {
		return "GeoVector3D";
	}

	public boolean isVector3DValue() {
		return true;
	}

	
	
	// for properties panel
	public boolean isPath(){
		return true;
	}
	
	public boolean isGeoVector(){
		return true;
	}
	
	
	

	///////////////////////////////////////////////
	// TO STRING
	///////////////////////////////////////////////


	final public String toString() {            
		sbToString.setLength(0);
		sbToString.append(label);

		switch (kernel.getCoordStyle()) {
		case Kernel.COORD_STYLE_FRENCH:
			// no equal sign
			sbToString.append(": ");

		case Kernel.COORD_STYLE_AUSTRIAN:
			// no equal sign
			break;

		default: 
			sbToString.append(" = ");
		}

		sbToString.append(buildValueString());
		return sbToString.toString();
	}
	
	private StringBuilder sbToString = new StringBuilder(50); 

	final public String toValueString() {
		return buildValueString().toString();
	}

	private StringBuilder buildValueString() {
		sbBuildValueString.setLength(0);
		
		/*
		switch (toStringMode) {

		
		case Kernel.COORD_POLAR:                	
			sbBuildValueString.append("(");		
		sbBuildValueString.append(kernel.format(GeoVec2D.length(x, y)));
		sbBuildValueString.append("; ");
		sbBuildValueString.append(kernel.formatAngle(Math.atan2(y, x)));
		sbBuildValueString.append(")");
			break;

		case Kernel.COORD_COMPLEX:              	
			sbBuildValueString.append(kernel.format(x));
			sbBuildValueString.append(" ");
			sbBuildValueString.append(kernel.formatSigned(y));
			sbBuildValueString.append("i");
            break;                                

			default: // CARTESIAN
				sbBuildValueString.append("(");		
			sbBuildValueString.append(kernel.format(x));
			switch (kernel.getCoordStyle()) {
				case Kernel.COORD_STYLE_AUSTRIAN:
					sbBuildValueString.append(" | ");
					break;

				default:
					sbBuildValueString.append(", ");												
			}
			sbBuildValueString.append(kernel.format(y));
			sbBuildValueString.append(")");
				break;       
		}
		 */

		sbBuildValueString.append("(");		
		sbBuildValueString.append(kernel.format(getX()));
		setCoordSep();
		sbBuildValueString.append(kernel.format(getY()));
		setCoordSep();
		sbBuildValueString.append(kernel.format(getZ()));
		sbBuildValueString.append(")");


		return sbBuildValueString;
		}


		private void setCoordSep(){
			switch (kernel.getCoordStyle()) {
			case Kernel.COORD_STYLE_AUSTRIAN:
				sbBuildValueString.append(" | ");
				break;

			default:
				sbBuildValueString.append(", ");	
			}
		}

		private StringBuilder sbBuildValueString = new StringBuilder(50);
		
		
		
		private StringBuilder sb;
		
	    public String toLaTeXString(boolean symbolic) {
	    	if (sb == null) sb = new StringBuilder();
	    	else sb.setLength(0);
	    	
	    	
	    	String[] inputs;
	    	if (symbolic && getParentAlgorithm() instanceof AlgoDependentVector) {
	    		AlgoDependentVector algo = (AlgoDependentVector)getParentAlgorithm();
	    		String symbolicStr = algo.toString();
	    		inputs = symbolicStr.substring(1, symbolicStr.length() - 1).split(",");
	    	} else {
	    		inputs = new String[3];
	    		inputs[0] = kernel.format(getX());
	    		inputs[1] = kernel.format(getY());
	    		inputs[2] = kernel.format(getZ());
	    	}
	    	
	    	boolean alignOnDecimalPoint = true;
			for (int i = 0 ; i < inputs.length ; i++) {
		    	if (inputs[i].indexOf('.') == -1) {
		    		alignOnDecimalPoint = false;
		    		continue;
		    	}
			}
			
			if (alignOnDecimalPoint) {
				sb.append("\\left( \\begin{tabular}{r@{.}l}");
				for (int i = 0 ; i < inputs.length ; i++) {
					inputs[i] = inputs[i].replace('.', '&');
				}
			} else {			
				sb.append("\\left( \\begin{tabular}{r}");
			}
			
			
			for (int i = 0 ; i < inputs.length ; i++) {
		    	sb.append(inputs[i]);
		    	sb.append(" \\\\ ");    			
			}
	    	
	    	sb.append("\\end{tabular} \\right)"); 
	    	return sb.toString();
	    }     
	    
		
	    /**
	     * returns all class-specific xml tags for saveXML
	     */
		protected void getXMLtags(StringBuilder sb) {
	        super.getXMLtags(sb);
			//	line thickness and type  
			getLineStyleXML(sb);
			
			//	startPoint of vector
			if (startPoint != null) {
				sb.append(startPoint.getStartPointXML());
			}

		}
		
		///////////////////////////////////////////////
		// LOCATEABLE INTERFACE
		///////////////////////////////////////////////

		

		public GeoPointND getStartPoint() {
			return startPoint;
		}

		public void setStartPoint(GeoPointND p)	throws CircularDefinitionException {
			

			//Application.debug("point : "+((GeoElement) pI).getLabel());
			
	    	//GeoPoint3D p = (GeoPoint3D) pI;
	    	
	    	if (startPoint == p) return;
	    	
	    	// macro output uses initStartPoint() only
			//TODO if (isAlgoMacroOutput()) return; 				
	    	
			// check for circular definition
			if (isParentOf((GeoElement) p))
				throw new CircularDefinitionException();

			// remove old dependencies
			if (startPoint != null) startPoint.getLocateableList().unregisterLocateable(this);	
		
			// set new location	
			startPoint = p;		
			
			//	add new dependencies
			if (startPoint != null) startPoint.getLocateableList().registerLocateable(this);	


			// update position matrix
			//updateStartPointPosition();		
			
		}

		public GeoPointND[] getStartPoints() {
			if (startPoint == null)
				return null;
		
			GeoPointND [] ret = new GeoPointND[1];
			ret[0] = startPoint;
			return ret;	
		}

		public boolean hasAbsoluteLocation() {
			return startPoint == null; //TODO || startPoint.isAbsoluteStartPoint();
		}

		public void initStartPoint(GeoPointND p, int number) {
			startPoint = (GeoPoint3D) p;
			
		}

		public boolean isAlwaysFixed() {
			return false;
		}

		public void removeStartPoint(GeoPointND p) {
			if (startPoint == p) {
				try {
					setStartPoint(null);
				} catch(Exception e) {}
			}
			
		}

		public void setStartPoint(GeoPointND p, int number) throws CircularDefinitionException {
			setStartPoint(p);
			
		}

		public void setWaitForStartPoint() {
			// TODO Auto-generated method stub
			
		}

		public Geo3DVec get3DVec() {
			return new Geo3DVec(kernel, v.getX(), v.getY(), v.getZ());
		}

		public double[] getPointAsDouble() {
			double[] ret = {v.getX(), v.getY(), v.getZ()};
			return ret;
		} 


		public Coords getCoordsInD(int dimension){
			Coords ret = new Coords(dimension+1);
			switch(dimension){
			case 3:
				ret.setW(getW());
			case 2:
				ret.setX(getX());
				ret.setY(getY());
				ret.setZ(getZ());
			}
			return ret;
		}

		
		
		public boolean getTrace() {
			// TODO Auto-generated method stub
			return false;
		}
		
		

		public Coords getDirectionInD3(){
			return getCoordsInD(3);
		}
		


}
