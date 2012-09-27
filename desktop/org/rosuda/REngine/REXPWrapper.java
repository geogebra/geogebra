package org.rosuda.REngine ;

/**
 * Utility class to wrap an Object into a REXP object. 
 *
 * This facilitates wrapping native java objects and arrays 
 * into REXP objects that can be pushed to R
 *
 * @author Romain Francois <francoisromain@free.fr>
 */
public class REXPWrapper {

	/* various classes */
	private static Class byte_ARRAY ;
	private static Class short_ARRAY ;
	private static Class int_ARRAY ;
	private static Class long_ARRAY ;
	private static Class float_ARRAY ;
	private static Class double_ARRAY ;
	private static Class boolean_ARRAY ;
	
	private static Class String_ARRAY ;
	
	private static Class Byte_ARRAY ;
	private static Class Short_ARRAY;
	private static Class Integer_ARRAY ;
	private static Class Long_ARRAY ;
	private static Class Float_ARRAY ;
	private static Class Double_ARRAY ;
	private static Class Boolean_ARRAY ;
		
 	static{
		try{
			byte_ARRAY     = Class.forName("[B") ;
			short_ARRAY    = Class.forName("[S" ); 
			int_ARRAY      = Class.forName("[I" ); 
			long_ARRAY     = (new long[1]).getClass() ; /* FIXME */
			float_ARRAY    = Class.forName("[F" ) ;
			double_ARRAY   = Class.forName("[D" );
			boolean_ARRAY  = Class.forName("[Z" ) ;
			
			String_ARRAY   = Class.forName( "[Ljava.lang.String;") ;
			
			Byte_ARRAY     = Class.forName( "[Ljava.lang.Byte;" ) ;
			Short_ARRAY    = Class.forName( "[Ljava.lang.Short;" ) ;
			Integer_ARRAY  = Class.forName( "[Ljava.lang.Integer;" ) ;
			Long_ARRAY     = Class.forName( "[Ljava.lang.Long;" ) ;
			Float_ARRAY    = Class.forName( "[Ljava.lang.Float;" ) ;
			Double_ARRAY   = Class.forName( "[Ljava.lang.Double;" ) ;
			Boolean_ARRAY  = Class.forName( "[Ljava.lang.Boolean;" ) ;
			
			
		} catch( Exception e){
			// should never happen
			e.printStackTrace(); 
			System.err.println( "problem while initiating the classes" ) ;
		}
	}
	
	/**
	 * Wraps an Object into a REXP
	 *
	 * <p>Conversion :</p>
	 *
	 * <ul>
	 * <li>Byte (byte) : REXPRaw </li>
	 * <li>Short (short) : REXPInteger </li>
	 * <li>Integer (int) : REXPInteger </li>
	 * <li>Long (long) : REXPInteger</li>
	 * <li>Float (float) : REXPDouble</li>
	 * <li>Double (double) : REXPDouble </li>
	 * <li>Boolean (boolean) : REXPLogical</li>
	 * <li>--</li>
	 * <li>String : REXPString </li>
	 * <li>String[] : REXPString </li>
	 * <li>--</li>
	 * <li>byte[] or Byte[] : REXPRaw</li>
	 * <li>short[] or Short[] : REXPInteger</li>
	 * <li>int[] or Integer[] : REXPInteger</li>
	 * <li>long[] or Long[] : REXPInteger</li>
	 * <li>float[] or Float[] : REXPDouble</li>
	 * <li>double[] or Double[] : REXPDouble </li>
	 * <li>boolean[] or Boolean[]: REXPLogical</li>
	 * <li>--</li>
	 * <li>null for anything else</li>
	 * </ul>
	 * 
	 * @param o object to wrap
	 * @return REXP object that represents o or null if the conversion is not possible
	 */
	public static REXP wrap( Object o ) {
		
		/* nothing to do in that case */
		if( o instanceof REXP){
			return (REXP)o; 
		} 
		
		Class clazz = o.getClass() ;
		
		/* primitives */
		
		if( clazz == Byte.class ){
			byte[] load = new byte[1]; 
			load[0] = ((Byte)o).byteValue() ;
			return new REXPRaw( load ); 
		} 
		
		if( clazz == Short.class ){
			return new REXPInteger( ((Short)o).intValue() ) ;
		} 
		
		if( clazz == Integer.class ){
			return new REXPInteger( ((Integer)o).intValue() ) ;
		} 
		
		if( clazz == Long.class ){
			return new REXPInteger( ((Long)o).intValue() ) ;
		} 
		
		if( clazz == Float.class ){
			return new REXPDouble( ((Float)o).doubleValue() ) ;
		}
		
		if( clazz == Double.class ){
			return new REXPDouble( ((Double)o).doubleValue() ) ;
		}
		
		if( clazz == Boolean.class ){
			return new REXPLogical( ((Boolean)o).booleanValue() ) ;
		}
		
		
		/* Strings -> REXPString */
		
		if( clazz == String.class ){
			return new REXPString( (String)o ) ;
		} 
		
		if( clazz == String_ARRAY ){ /* String[] */
			return new REXPString( (String[])o ); 
		} 
		
		/* array of byte or Bytes -> REXPRaw */
		
		if( clazz == byte_ARRAY ){ /* byte[] */
			return new REXPRaw( (byte[])o ) ; 
		} 
		
		if( clazz == Byte_ARRAY ){ /* Byte[] */
			Byte[] b = (Byte[])o;
			int n = b.length ;
			byte[] bytes = new byte[b.length];
			for( int i=0; i<n; i++){
				bytes[i] = b[i].byteValue() ;
			}
			return new REXPRaw( bytes ); 
		}
		
		/* arrays of short or Short  -> REXPInteger */ 
		
		if( clazz == short_ARRAY ){ /* short[] */
			short[] shorts = (short[])o ;
			int[] ints = new int[ shorts.length ];
			int n = ints.length; 
			for( int i=0; i<n; i++ ){
				ints[i] = shorts[i]; 
			}
			return new REXPInteger( ints ) ;
		}
		
		if( clazz == Short_ARRAY ){ /* Short[] */
			Short[] shorts = (Short[])o;
			int n = shorts.length ;
			int[] ints = new int[shorts.length];
			for( int i=0; i<n; i++){
				ints[i] = shorts[i].intValue() ;
			}
			return new REXPInteger( ints ); 
		} 
		
		
		/* arrays of int or Integer ->  REXPInteger */
		
		if( clazz == int_ARRAY ){ /* int[] */
			return new REXPInteger( (int[])o ) ; 
		} 
		
		if( clazz == Integer_ARRAY ){ /* Integer[] */
			Integer[] integers = (Integer[])o;
			int n = integers.length ;
			int[] ints = new int[integers.length];
			for( int i=0; i<n; i++){
				ints[i] = integers[i].intValue() ;
			}
			return new REXPInteger( ints ); 
		} 
		
		/* arrays of long or Long -> REXPInteger */
		
		if( clazz == long_ARRAY ){ /* long[] */
			long[] longs = (long[])o;
			int n = longs.length ;
			int[] ints = new int[longs.length];
			for( int i=0; i<n; i++){
				ints[i] = (int)longs[i] ;
			}
			return new REXPInteger( ints ); 
		} 
		
		if( clazz == Long_ARRAY ){ /* Long[] */
			Long[] longs = (Long[])o;
			int n = longs.length ;
			int[] ints = new int[longs.length];
			for( int i=0; i<n; i++){
				ints[i] = longs[i].intValue() ;
			}
			return new REXPInteger( ints ); 
		} 
		
		/* float or Float arrays -> REXPDouble */
		
		if( clazz == float_ARRAY ){ /* float[] */
			float[] floats = (float[])o;
			int n = floats.length ;
			double[] doubles = new double[floats.length];
			for( int i=0; i<n; i++){
				doubles[i] = (double)floats[i] ;
			}
			return new REXPDouble( doubles ); 
		} 
		
		if( clazz == Float_ARRAY ){ /* Float[] */
			Float[] floats = (Float[])o;
			int n = floats.length ;
			double[] doubles = new double[floats.length];
			for( int i=0; i<n; i++){
				doubles[i] = floats[i].doubleValue() ;
			}
			return new REXPDouble( doubles ); 
		}
		
		
		/* double or Double arrays -> REXPDouble */
		
		if(clazz == double_ARRAY ) { /* double[] */
			return new REXPDouble( (double[])o ) ;
		}
		
		if( clazz == Double_ARRAY ){ /* Double[] */
			Double[] doubles = (Double[])o;
			double n = doubles.length ;
			double[] d = new double[doubles.length];
			for( int i=0; i<n; i++){
				d[i] = doubles[i].doubleValue() ;
			}
			return new REXPDouble( d ); 
		} 
		
		
		/* boolean arrays -> REXPLogical */
		
		if( clazz == boolean_ARRAY ){ /* boolean[] */
			return new REXPLogical( (boolean[])o ) ; 
		} 
		
		if( clazz == Boolean_ARRAY ){ /* Boolean[] */
			Boolean[] booleans = (Boolean[])o;
			int n = booleans.length ;
			boolean[] b = new boolean[booleans.length];
			for( int i=0; i<n; i++){
				b[i] = booleans[i].booleanValue() ;
			}
			return new REXPLogical( b ); 
		} 
		
		/* give up and return null */
		
		return null ; 
	}
	
}

