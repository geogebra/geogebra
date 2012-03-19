package geogebra.common.kernel.arithmetic;

import java.util.Map;
import java.util.TreeMap;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.main.AbstractApplication;

/**
 * Arbitrary constant comming from reduce
 *
 */
public class MyArbitraryConstant extends MyDouble {
	/** arbitrary integer*/
	public static final int ARB_INT = 0;
	/** arbitrary double*/
	public static final int ARB_CONST = 1;
	/** arbitrary complex number*/
	public static final int ARB_COMPLEX = 2;
	
	private String latexString, internalString;
	
	private static Map<Integer,String> consts= new TreeMap<Integer,String>(), ints= new TreeMap<Integer,String>(), complexNumbers = new TreeMap<Integer,String>();
	
	private static String latexStr(String prefix,Map<Integer,String> map,Integer number,Construction cons){
		String s = map.get(number);
		AbstractApplication.debug("varname ="+s);
		if(s!=null)
			return s;
		s = cons.getIndexLabel(prefix, number);
		map.put(number, s);
		return s;
	}
	/**
	 * Creates an arbitrary constant coming from Reduce using
	 * 
	 * @param kernel kernel
	 * @param arbID ARB_INT, ARB_CONST, ARB_COMPLEX
	 * @param numberStrRaw string representation of the number
	 */
	public MyArbitraryConstant(Kernel kernel, int arbID, String numberStrRaw) {
		super(kernel, 0);
		
		String numberStr = numberStrRaw.trim();
		Integer number = 1;
		try {
			number = (int) Math.round(Double.parseDouble(numberStr));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Construction cons = kernel.getConstruction();
		switch (arbID) {
			case ARB_INT:	
				internalString = build("arbint(", numberStr, ")");
				latexString = latexStr("k",ints, number,cons);
				break;
				
			case ARB_CONST:
				internalString = build("arbconst(", numberStr, ")");
				latexString = latexStr("c",consts, number,cons);
				break;
				
			case ARB_COMPLEX:
				internalString = build("arbconst(", numberStr, ")");
				latexString = latexStr("z",complexNumbers, number,cons);
				break;
		}			
	}
	
	@Override
	public String toString(StringTemplate tpl) {
		if(tpl.isSymbolicArbConst())
			return latexString;
		switch (tpl.getStringType()) {						
			case LATEX:
				// return e.g. "k_1" 
				return latexString;
							
			case MPREDUCE:			
				// return e.g. "arbint(2)"
				return internalString;
				
			default:
				//case GEOGEBRA:
				// treat arbitrary constant as 0, 
				// see #1428 problem with Integral[x^2]
				return "0";
		}					
	}

	private static String build(String str1, String str2, String str3) {
		StringBuilder sb = new StringBuilder();
		sb.append(str1);
		sb.append(str2);
		if (str3 != null)
			sb.append(str3);
		return sb.toString();
	}
		
}
