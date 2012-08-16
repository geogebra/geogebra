/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.plugin.GeoClass;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math.stat.Frequency;

public class AlgoFrequency extends AlgoElement {

	private GeoList dataList; //input
	private GeoList classList; //input
	private GeoBoolean isCumulative; //input
	private GeoBoolean useDensity; //input
	private GeoNumeric density; //input

	private GeoList frequency; //output	

	// for compute
	private GeoList value = new GeoList(cons);

	public AlgoFrequency(Construction cons, String label, GeoBoolean isCumulative, GeoList classList, GeoList dataList) {
		this(cons, label, isCumulative, classList, dataList, null, null);	
	}
	
	AlgoFrequency(Construction cons, GeoBoolean isCumulative, GeoList classList, GeoList dataList) {
		this(cons, isCumulative, classList, dataList, null, null);	
	}
	
	public AlgoFrequency(Construction cons, String label, GeoBoolean isCumulative, GeoList classList, GeoList dataList, 
			GeoBoolean useDensity, GeoNumeric density) {
		this(cons,isCumulative,classList,dataList,useDensity,density);
		frequency.setLabel(label);
	}
	
	AlgoFrequency(Construction cons, GeoBoolean isCumulative, GeoList classList, GeoList dataList, 
			GeoBoolean useDensity, GeoNumeric density) {
		super(cons);
		
		this.classList = classList;
		this.dataList = dataList;
		this.isCumulative = isCumulative;
		this.useDensity = useDensity;
		this.density = density;
		
		frequency = new GeoList(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoFrequency;
	}

	@Override
	protected void setInputOutput(){

		ArrayList<GeoElement> tempList = new ArrayList<GeoElement>();

		if(isCumulative !=null)
			tempList.add(isCumulative);
		
		if(classList !=null)
			tempList.add(classList);

		tempList.add(dataList);
		
		if(useDensity !=null)
			tempList.add(useDensity);
		
		if(density !=null)
			tempList.add(density);
		
		input = new GeoElement[tempList.size()];
		input = tempList.toArray(input);

		
		setOutputLength(1);
		setOutput(0,frequency);
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return frequency;
	}
	public GeoList getValue(){
		return value;
	}

	@Override
	public final void compute() {

		// Validate input arguments
		//=======================================================

		if (!dataList.isDefined() || dataList.size() == 0) {
			frequency.setUndefined();		
			return; 		
		}

		if( !( dataList.getElementType() .equals(GeoClass.TEXT) 
				|| dataList.getElementType() .equals(GeoClass.NUMERIC) )){
			frequency.setUndefined();		
			return;
		}

		if(classList != null){
			if(!classList.getElementType().equals(GeoClass.NUMERIC) || classList.size() < 2){
				frequency.setUndefined();		
				return; 		
			}
		}

		if(density != null){
			if(density.getDouble() <=0 ){
				frequency.setUndefined();		
				return; 		
			}
		}

		
		frequency.setDefined(true);
		frequency.clear();
		if(value != null) value.clear();

		double numMax = 0, numMin = 0;
		boolean doCumulative = isCumulative != null && isCumulative.getBoolean();

		
		// Load the data into f, an instance of Frequency class 
		//=======================================================

		Frequency f = new Frequency();
		for (int i=0 ; i < dataList.size() ; i++){
			if(dataList.getElementType() .equals(GeoClass.TEXT))
				f.addValue(((GeoText)dataList.get(i)).toValueString(StringTemplate.defaultTemplate));
			if(dataList.getElementType() .equals(GeoClass.NUMERIC))
				f.addValue(((GeoNumeric)dataList.get(i)).getDouble());
		}

		
		// If classList does not exist, 
		// get the unique value list and compute frequencies for this list  
		//=======================================================

		// handle string data
		if(dataList.getElementType() .equals(GeoClass.TEXT)){

			Iterator<Comparable<?>> itr = f.valuesIterator();
			String strMax = (String) itr.next();
			String strMin = strMax;
			itr = f.valuesIterator();

			while(itr.hasNext()) {		
				String s = (String) itr.next();
				if( s.compareTo(strMax) > 0) strMax = s;
				if( s.compareTo(strMin) < 0) strMin = s;
				GeoText text = new GeoText(cons);
				text.setTextString(s);
				value.add(text);
				if(classList == null) {
					if( doCumulative) {
						frequency.add(new GeoNumeric(cons,f.getCumFreq(s )));
					} else {
						frequency.add(new GeoNumeric(cons,f.getCount(s )));
					}
				}
			}
		}

		// handle numeric data
		else
		{
			Iterator<Comparable<?>> itr = f.valuesIterator();
			numMax = (Double) itr.next();
			numMin = numMax;
			itr = f.valuesIterator();

			while(itr.hasNext()) {		
				Double n = (Double) itr.next();
				if( n > numMax) numMax = n.doubleValue();
				if( n < numMin) numMin = n.doubleValue();
				value.add(new GeoNumeric(cons,n));

				if(classList == null)
					if( doCumulative)
						frequency.add(new GeoNumeric(cons,f.getCumFreq(n )));
					else
						frequency.add(new GeoNumeric(cons,f.getCount(n )));
			}
		} 

		
		// If classList exists, compute frequencies using the classList
		//=======================================================

		if(classList != null) {

			double lowerClassBound = 0 ;
			double upperClassBound = 0 ;
			double classFreq = 0;

			
			//set density conditions
			boolean hasDensity = false;
			if(useDensity != null)
				hasDensity = useDensity.getBoolean();
			
			double densityValue = 1;  // default density
			if(density != null){
				densityValue = density.getDouble();
			}
			
			double cumulativeClassFreq = 0;
			double swap;
			int length = classList.size();
			for(int i=1; i < length; i++) {

				lowerClassBound = ((GeoNumeric)classList.get(i-1)).getDouble();
				upperClassBound = ((GeoNumeric)classList.get(i)).getDouble();
				boolean increasing = true;
				if(lowerClassBound>upperClassBound){
					swap = upperClassBound;
					upperClassBound = lowerClassBound;
					lowerClassBound = swap;
					increasing = false;
				}
					classFreq = f.getCumFreq(upperClassBound) 
					- f.getCumFreq(lowerClassBound) 
					+ f.getCount(lowerClassBound);
				if((i!=length -1 && increasing) ||
					(i!=1 && !increasing))
					classFreq -= f.getCount(upperClassBound);
				

			//	System.out.println(" =================================");
			//	System.out.println("class freq: " + classFreq + "   " + density);
				if(hasDensity){
					classFreq = densityValue *classFreq/(upperClassBound - lowerClassBound );
				}
				if(doCumulative)
					cumulativeClassFreq += classFreq;
			//	System.out.println("class freq: " + classFreq);
				
				// add the frequency to the output GeoList
				frequency.add(new GeoNumeric(cons, doCumulative?cumulativeClassFreq:classFreq));
			}

			// handle the last (highest) class frequency specially
			// it must also count values equal to the highest class bound  
			
		}
	}

	// TODO Consider locusequability

}
