package geogebra.kernel.arithmetic;

import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;

public class MyArbitraryConstant extends MyDouble {
	
	public static final int ARB_INT = 0;
	public static final int ARB_CONST = 1;
	public static final int ARB_COMPLEX = 2;
	
	private String latexString, internalString;
	
	/**
	 * Creates an arbitrary constant coming from Reduce using
	 * 
	 * @param kernel
	 * @param arbID ARB_INT, ARB_CONST, ARB_COMPLEX
	 */
	public MyArbitraryConstant(Kernel kernel, int arbID, String numberStr) {
		super(kernel, 0);
		
		numberStr = numberStr.trim();
		int number = 1;
		try {
			number = Integer.parseInt(numberStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Construction cons = kernel.getConstruction();
		switch (arbID) {
			case ARB_INT:	
				internalString = build("arbint(", numberStr, ")");
				latexString = cons.getIndexLabel("k", number);
				break;
				
			case ARB_CONST:
				internalString = build("arbconst(", numberStr, ")");
				latexString = cons.getIndexLabel("c", number);
				break;
				
			case ARB_COMPLEX:
				internalString = build("arbconst(", numberStr, ")");
				latexString = cons.getIndexLabel("z", number);
				break;
		}			
	}
	
	public String toString() {
		switch (kernel.getCASPrintForm()) {						
			case ExpressionNode.STRING_TYPE_LATEX:
				// return e.g. "k_1" 
				return latexString;
							
			case ExpressionNode.STRING_TYPE_MPREDUCE:			
				// return e.g. "arbint(2)"
				return internalString;
				
			default:
				//case ExpressionNode.STRING_TYPE_GEOGEBRA:
				// treat arbitrary constant as 0, 
				// see #1428 problem with Integral[x^2]
				return "0";
		}					
	}

	private String build(String str1, String str2, String str3) {
		StringBuilder sb = new StringBuilder();
		sb.append(str1);
		sb.append(str2);
		if (str3 != null)
			sb.append(str3);
		return sb.toString();
	}
		
}
