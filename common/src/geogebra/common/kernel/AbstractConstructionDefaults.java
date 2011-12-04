package geogebra.common.kernel;

import geogebra.common.kernel.geos.GeoElementInterface;

public abstract class AbstractConstructionDefaults {
	public static final float DEFAULT_POLYGON_ALPHA = 0.1f;
	
	// PLEASE DON'T USE RANGE 3000-3999 (used by GeoGebra 3D)
		public static final int DEFAULT_NONE = -1;
		public static final int DEFAULT_POINT_FREE = 10;
		public static final int DEFAULT_POINT_DEPENDENT = 11;
		public static final int DEFAULT_POINT_ON_PATH = 12;
		public static final int DEFAULT_POINT_IN_REGION = 13;
		public static final int DEFAULT_POINT_COMPLEX =  14;
		
		public static final int DEFAULT_LINE = 20;			
		public static final int DEFAULT_SEGMENT = 21;			
		public static final int DEFAULT_INEQUALITY = 23; 
		public static final int DEFAULT_INEQUALITY_1VAR = 24;
		public static final int DEFAULT_VECTOR = 30;	
		public static final int DEFAULT_CONIC = 40;
		public static final int DEFAULT_CONIC_SECTOR = 41;
			
		public static final int DEFAULT_NUMBER = 50;	
		public static final int DEFAULT_ANGLE = 52;			
		
		public static final int DEFAULT_FUNCTION = 60;		
		public static final int DEFAULT_POLYGON = 70;
		public static final int DEFAULT_LOCUS = 80;
		
		public static final int DEFAULT_TEXT = 100;
		public static final int DEFAULT_IMAGE = 110;
		public static final int DEFAULT_BOOLEAN = 120;
		
		public static final int DEFAULT_LIST = 130;
		
	public abstract void setDefaultVisualStyles(GeoElementInterface geoElement, boolean b);
	public abstract GeoElementInterface getDefaultGeo(int defaultInequality1var);
	

}
