package geogebra.common.kernel.locusequ;

import geogebra.common.cas.GeoGebraCAS;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MPReducePolynomialParser {

	public static double[][] parsePolynomial(String result, GeoGebraCAS cas) {
		Map<Integer,Map<Integer, Double>> coeff = new HashMap<Integer,Map<Integer,Double>>();
		//   x =>        y    =>   coeff
		
		String[] xCoeffs = cas.getPolynomialCoeffs(result, "x"); 
		
		for(int x = 0; x < xCoeffs.length; x++) {
			String[] yCoeffs = cas.getPolynomialCoeffs(xCoeffs[x], "y");
			
			for(int y = 0; y < yCoeffs.length; y++) {
				addCoefficients(coeff, x, y, Double.parseDouble(yCoeffs[y]));
			}
			
		}
		
		return mapToArray(coeff);
	}

	private static double[][] mapToArray(
			Map<Integer, Map<Integer, Double>> map) {
		// Find max x
		Integer xMax = Collections.max(map.keySet());
		
		// Find max y
		List<Integer> ys = new ArrayList<Integer>();
		
		for(Integer x : map.keySet()) {
			ys.addAll(map.get(x).keySet());
		}
		Integer yMax = Collections.max(ys);
		
		// Create coeff arrays
		double[][] coeffs = new double[xMax+1][yMax+1];
		
		for(Integer x : map.keySet()) {
			Map<Integer, Double> yMap = map.get(x);
			
			for(Integer y : yMap.keySet()) {
				coeffs[x][y] = yMap.get(y);
				// App.debug("[LocusEqu] coeffs[" + x + "," + y + "]=" + coeffs[x][y]);
			}
		}
		
		return coeffs;
	}

	private static void addCoefficients(Map<Integer,Map<Integer, Double>> map, int x, int y, double parsedCoefficient) {
		Integer xInt = Integer.valueOf(x);
		Integer yInt = Integer.valueOf(y);
		Map<Integer,Double> yMap = map.get(xInt);
		
		if(yMap == null) {
			yMap =  new HashMap<Integer,Double>();
			map.put(xInt, yMap);
		}
		
		Double coef = yMap.get(yInt);
		
		if(coef == null) {
			coef = Double.valueOf(0);
		}
		
		coef = Double.valueOf(coef.doubleValue() + parsedCoefficient);
		
		yMap.put(yInt, coef);
	}
}
