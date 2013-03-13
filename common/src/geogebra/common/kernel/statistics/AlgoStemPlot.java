/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Creates stem and leaf plot for given list of numbers,
 * output is a LaTeX table 
 * @author Michael
 */
public class AlgoStemPlot extends AlgoElement {

	private GeoList geoList; //input
	private GeoNumeric scaleAdjustment; //input
	private GeoText text; //output	
	private StringBuilder low, high;

	private StringBuilder sb = new StringBuilder();

	/**
	 * @param cons construction
	 * @param label label for output
	 * @param geoList list of numbers
	 * @param scaleAdjustment decimal point shift (+1/0/-1)
	 */
	public AlgoStemPlot(Construction cons, String label, GeoList geoList, GeoNumeric scaleAdjustment) {
		this(cons, geoList, scaleAdjustment);
		text.setLabel(label);
	}

	/**
	 * @param cons construction
	 * @param geoList list of numbers
	 * @param scaleAdjustment decimal point shift (+1/0/-1)
	 */
	public AlgoStemPlot(Construction cons, GeoList geoList, GeoNumeric scaleAdjustment) {
		super(cons);
		this.geoList = geoList;
		this.scaleAdjustment = scaleAdjustment;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text

		setInputOutput();
		compute();

		// set sans-serif LaTeX default
		text.setSerifFont(false);
	}

	@Override
	public Commands getClassName() {
		return Commands.StemPlot;
	}

	@Override
	protected void setInputOutput(){

		input = new GeoElement[scaleAdjustment == null ? 1 : 2];
		input[0] = geoList;

		if (scaleAdjustment != null)
			input[1] = scaleAdjustment;

		super.setOutputLength(1);
        super.setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}
	/**
	 * @return resulting LaTeX table
	 */
	public GeoText getResult() {
		return text;
	}

	/**
	 * Tests an array of doubles for outliers by the 1.5 IQR rule. Returns a two
	 * value array of index values that define a sub array of non-outlier
	 * values.
	 */
	private static int[] getOutlierIndex(double[] data){
		
		// init the outlier indices using the data array bounds
		int size = data.length;
		int[] outlierIndex = {0,size};

		if(size <=1) return outlierIndex;

		// find Q1 and Q3
		double Q1;
		switch (size % 4)
		{
		case 0:
			Q1 = (data[(size)/4-1]+data[(size+4)/4-1])/2;  
			break;
		case 1:
			Q1 = (data[(size-1)/4-1]+data[(size+3)/4-1])/2;  
			break;
		case 2:
			Q1 = data[(size+2)/4-1];  
			break;
		default:
			Q1 = data[(size+1)/4-1];  
			break;
		}

		double Q3;
		switch (size % 4)
		{
		case 0:
			Q3 = (data[(3*size)/4-1]+data[(3*size+4)/4-1])/2;  
			break;
		case 1:
			Q3 = (data[(3*size+1)/4-1]+data[(3*size+5)/4-1])/2;  
			break;
		case 2:
			Q3 = data[(3*size+2)/4-1];  
			break;
		default:
			Q3 = data[(3*size+3)/4-1];  
			break;
		}

		// test for outliers and adjust the indicies accordingly
		double IQRplus = 1.5 * (Q3 - Q1) ;

		for(int i=0; i< data.length && data[i] <  Q1 - IQRplus - Kernel.STANDARD_PRECISION; i++)
			outlierIndex[0]++;
		for(int i=data.length-1; i>=0 && data[i] > Q3 + IQRplus + Kernel.STANDARD_PRECISION; i--)
			outlierIndex[1]--;

		return outlierIndex;
	}

	
	/**
	 * Processes an array of doubles to create a stem & leaf plot as a list of
	 * ArrayLists. Each ArrayList stores a stem and associated leaf values.
	 * 
	 * Each data value is converted into a stem/leaf pair by multiplying the
	 * data value by the parameter stemFactor (a power of ten), rounding this to
	 * an integer and then using the tens part for the stem and the unit for the
	 * leaf. The data array is adjusted to exclude outliers by applying the index
	 * values stored in the parameter outlierIndex.
	 */
	private static ArrayList<ArrayList<Integer>> createStemPlotArray(double[] data, double stemFactor, int[] outlierIndex){
	
		ArrayList<ArrayList<Integer>> lines = new ArrayList<ArrayList<Integer>>() ;
		int size = outlierIndex[1];
		int startIndex = outlierIndex[0];

		
		//===========================================
		// load first stem/leaf pair
		
		int n  = (int) Math.round(data[startIndex] * stemFactor);
		int stem = n / 10;
		int leaf = Math.abs(n % 10);
		int currentStem = stem;

		lines.add(new ArrayList<Integer>());
		lines.get(lines.size()-1).add(currentStem);
		
		// for negative values we need two 0 stems
		if(currentStem == 0 && n < 0){
			lines.add(new ArrayList<Integer>());
			lines.get(lines.size()-1).add(currentStem);
			lines.get(lines.size()-2).add(new Integer(leaf));
			
		}else{
			lines.get(lines.size()-1).add(new Integer(leaf));
		}

		
		//===========================================
		// load remaining stem/leaf pairs
		
		for (int i = startIndex + 1 ; i < size ; i++) {

			// get the stem and leaf for this number
			n  = (int) Math.round(data[i] * stemFactor);
			stem = n / 10;
			leaf = Math.abs(n % 10);
			// if our stem is not the current one, add stems until we reach it
			while(currentStem < stem){
				currentStem++;
				lines.add(new ArrayList<Integer>());
				lines.get(lines.size()-1).add(currentStem);
				if(currentStem == 0 && n < 0){
					lines.add(new ArrayList<Integer>());
					lines.get(lines.size()-1).add(currentStem);
				}
			}

			// now add our leaf to the stem
			if(stem == 0 && n < 0)
				lines.get(lines.size()-2).add(Integer.valueOf(leaf));
			else
				lines.get(lines.size()-1).add(Integer.valueOf(leaf));
		}

		return lines;   	
	}
	
	@Override
	public final void compute() {
		int size = geoList.size();
		if (!geoList.isDefined() ||  size == 0) {
			text.setTextString("");
			return;
			//throw new MyError(app, app.getError("InvalidInput"));   		
		}

		// extract data from the list, exit if non-numeric is in list
		double[] data = new double[size];
		for (int i = 0 ; i < size ; i++) {
			GeoElement geo = geoList.get(i);
			if (!geo.isGeoNumeric() || !geo.isDefined()) {
				text.setTextString("");
				return;
			}    		
			data[i] = ((GeoNumeric)geo).getDouble();
		}

		// sort numbers, adjust for outliers and then set max/min
		Arrays.sort(data);
		int outlierIndex[] = getOutlierIndex(data);
		double max = data[outlierIndex[1] - 1];
		double min = data[outlierIndex[0]];

		// find the plot magnitude (power of ten)
		int magnitude;

		// find next power of 10 above max eg 76 -> 100, 100->1000
		// also allow for negative values: find the maximum of either max or abs(min)
		double maxTemp = Math.max(max, Math.abs(min));
		magnitude = (int) Math.floor(Math.log10(maxTemp*1.00000001));

		// increment/decrement magnitude with user input
		// don't adjust by more than 1 order 
		if(input.length == 2){
			int s = Math.abs(scaleAdjustment.getDouble()) > 1 ? 0: (int)scaleAdjustment.getDouble();
			magnitude = magnitude + s;
		}

		double factor = Math.pow(10.0, 1 - magnitude); // factor for creating the stem plot
		double multUnit = Math.pow(10.0, magnitude-1); // factor for building the key
		
		// create stemLines -- a list of ArrayLists that stores the stem & leaf values for each line of the plot
		ArrayList<ArrayList<Integer>> stemLines = createStemPlotArray(data, factor, outlierIndex) ;

		// find the maximum length of the stem lines (used to create the LaTeX)
		int maxSize = 0;	
		for (int i = 0 ; i < stemLines.size() ; i++) {
			maxSize = Math.max(maxSize, stemLines.get(i).size());
		}


		//=============================================
		// create LaTex for the stemplot body

		StringBuffer body = new StringBuffer();
		body.setLength(0);   	
		body.append("\\begin{array}{");

		// set alignments
		body.append("r|");  // right align the stem and add divider bar
		for (int i = 0 ; i < maxSize ; i++) {
			// left align all leaf digits
			body.append('l'); 
		}
		body.append("}");

		// populate the body array
		ArrayList<Integer> currentLine = new ArrayList<Integer>();
		int stem;
		for (int r = 0 ; r < stemLines.size() ; r++) {
			currentLine = stemLines.get(r);
			
			// add the stem and handle the case of -0
			stem = currentLine.get(0);
			if(stem == 0 && (r<stemLines.size()-2 && stemLines.get(r+1).get(0)==0))
				body.append("-" + stem + ""); 
			else
				body.append(stem + ""); 
			body.append("&"); // column separator

			// add the leaf values
			for (int c = 1 ; c < maxSize; c++) {
				body.append(currentLine.size() > c ? currentLine.get(c) + "" : " " );
				if (c < maxSize - 1) body.append("&"); // column separator
			}
			body.append(" \\\\ "); // newline in LaTeX ie \\
		}   
		body.append("\\end{array}");
		body.append(" \\\\ "); // newline in LaTeX ie \\


		//==========================================
		// create LaTeX for the key
		StringBuffer key = new StringBuffer();
		key.setLength(0);
		key.append("\\fbox{\\text{");

		// calculate the key string,  avoid eg 31.0
		String keyCode =  (multUnit >= 1) ? ""+ 31 *(int)multUnit : "" + 31.0 * multUnit ;
		key.append(loc.getPlain("StemPlot.KeyAMeansB", "3|1", keyCode));
		key.append("}}");
		key.append(" \\\\ "); // newline in LaTeX ie \\


		//==========================================
		// create LaTeX for the outliers

		low = StringUtil.resetStringBuilder(low);
		low.append("\\text{");
		low.append(loc.getPlain("StemPlot.low"));
		low.append(": ");
		for(int i=0; i< outlierIndex[0];i++){
			low.append((i < outlierIndex[0]-1) ? data[i] + "," : data[i]);
		}
		low.append("} \\\\ "); // newline in LaTeX ie \\

		high = StringUtil.resetStringBuilder(high);
		high.append(loc.getPlain("\\text{"));
		high.append(loc.getPlain("StemPlot.high"));
		high.append(": ");
		for(int i = outlierIndex[1]; i< data.length; i++){
			high.append((i < data.length-1) ? data[i] + "," : data[i]);
		}
		high.append("} \\\\ "); // newline in LaTeX ie \\


		//==========================================
		// create the stemplot LaTeX

		sb.setLength(0);

		// surround in { } to make eg this work:
		// FormulaText["\bgcolor{ff0000}"+TableText[matrix1]]

		sb.append('{');
		sb.append("\\begin{tabular}{ll}");  	
		if(outlierIndex[0] > 0)
			sb.append(low);
		// see http://code.google.com/p/google-web-toolkit/issues/detail?id=4097
		sb.append((CharSequence)body);
		if(outlierIndex[1] < data.length)
			sb.append(high);
		sb.append((CharSequence)key);
		sb.append("\\end{tabular}");
		sb.append('}');

		//Application.debug(sb.toString());


		//==========================================
		// set to LaTeX
		text.setTextString(sb.toString());
		text.setLaTeX(true,false);
	}
	
	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	// TODO Consider locusequability

}
