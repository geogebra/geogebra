/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyBoolean;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.MyStringBuffer;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.main.App;

/**
 * List expression, e.g. with L1 = {3, 2, 1}, L2 = {5, 1, 7}
 * such an expression could be L1 + L2
 */
public class AlgoDependentListExpression extends AlgoElement implements DependentAlgo {
   
	private ExpressionNode root;  // input
    private GeoList list;     // output              
        
    /**
     * Creates new dependent list algo. 
     * @param cons construction
     * @param label label for the list
     * @param root expression deining the list
     */
    public AlgoDependentListExpression(Construction cons, String label, ExpressionNode root) {
    	this(cons, root);     
        list.setLabel(label);
    }   
    
    public AlgoDependentListExpression(Construction cons, ExpressionNode root) {
    	super(cons);
        this.root = root;  
        
        list = new GeoList(cons);       
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent list
        compute();      
    }   
    
	@Override
	public Algos getClassName() {
		return Algos.AlgoDependentListExpression;
	}
    
    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = root.getGeoElementVariables();
        
        setOutputLength(1);        
        setOutput(0,list);        
        setDependencies(); // done by AlgoElement
    }    
    /**
     * Returns the resulting list
     * @return resulting list
     */
    public GeoList getList() { return list; }
    /**
     * Returns the input expression
     * @return input expression
     */
    ExpressionNode getExpression() { return root; }
    
    // evaluate the current value of the arithmetic tree
    @Override
	public final void compute() {    
		// get resulting list of ExpressionNodes		    	
    	ExpressionValue evlist = root.evaluate(StringTemplate.defaultTemplate);
    	MyList myList = (evlist instanceof MyList)? (MyList)evlist: ((GeoList)evlist).getMyList();
    	
    	if (!myList.isDefined()) {
    		list.setUndefined();
    		return;
    	}
    	
    	list.setDefined(true);

		int evalListSize = myList.size();
		int cachedListSize = list.getCacheSize();    		
		
		list.clear();
		for (int i=0; i < evalListSize; i++) {
			ExpressionValue element = myList.getListElement(i).evaluate(StringTemplate.defaultTemplate);    			    			
			GeoElement geo = null;	

			// number result
			if (element.isNumberValue()) {	
				double val = ((NumberValue) element).getDouble();    				
				
				// try to use cached element of same type
				if (i < cachedListSize) {
					GeoElement cachedGeo = list.getCached(i);
					
					// the cached element is a number: set value
					if (cachedGeo.isGeoNumeric()) {
						((GeoNumeric) cachedGeo).setValue(val);
						geo = cachedGeo;
					}     			
				}
				
				// no cached number: create new one
				if (geo == null) {
					geo = new GeoNumeric(cons, val);
				}
				
				// add number to list
				list.add(geo);					    				
			} 
			
			// point 
			else if (element.isVectorValue()) {
				GeoVec2D vec = ((VectorValue) element).getVector();   				
				
				// try to use cached element of same type
				if (i < cachedListSize) {
					GeoElement cachedGeo = list.getCached(i);
					
					// the cached element is a point: set value
					if (cachedGeo.isGeoPoint()) {
						((GeoPoint) cachedGeo).setCoords(vec);
						geo = cachedGeo;
					}     			
				}
				
				// no cached point: create new one
				if (geo == null) {
					GeoPoint point = new GeoPoint(cons);
					point.setCoords(vec);
					geo = point;
				}
				
				// add point to list
				list.add(geo);	
			}				
			
			// needed for matrix multiplication 
			// eg {{1,3,5},{2,4,6}}*{{11,14},{12,15},{13,a}}
			else if (element instanceof MyList) {
				MyList myList2 = (MyList)element;
				GeoList list2 = new GeoList(cons);
				list2.clear();
				
				/* removed Michael Borcherds 20080602
				 * bug: 9PointCubic.ggb (matrix multiplication)
				// try to use cached element of  type GeoList
				GeoList list2 = null;
				if (i < cachedListSize) {
					GeoElement cachedGeo = list.getCached(i);
					
					// the cached element is a number: set value
					if (cachedGeo.isGeoList()) {
						list2 = (GeoList) cachedGeo;						
					}     			
				}				
				
				if (list2 == null) {
					list2 = new GeoList(cons);
				} */
				
				for (int j=0 ; j < myList2.size() ; j++)
				{		
					ExpressionValue en = myList2.getListElement(j);
					ExpressionValue ev = en.evaluate(StringTemplate.defaultTemplate);
					
					if (ev instanceof MyDouble) {
						GeoNumeric geo2 = new GeoNumeric(cons);
						geo2.setValue(((NumberValue)ev).getDouble());
						list2.add(geo2);
					} 					
				}
				
				list.add(list2);
			}
			else if (element instanceof MyStringBuffer) {
				MyStringBuffer str = (MyStringBuffer)element;
				// try to use cached element of same type
				if (i < cachedListSize) {
					GeoElement cachedGeo = list.getCached(i);
					
					// the cached element is a point: set value
					if (cachedGeo.isGeoText()) {
						((GeoText) cachedGeo).setTextString(str.toValueString(StringTemplate.defaultTemplate));
						geo = cachedGeo;
					}     			
				}
				
				// no cached point: create new one
				if (geo == null) {
					GeoText text = new GeoText(cons);
					text.setTextString(str.toValueString(StringTemplate.defaultTemplate));
					geo = text;
				}
				
				// add point to list
				list.add(geo);	
			}
			else if (element instanceof MyBoolean) {
				MyBoolean bool = (MyBoolean)element;
				// try to use cached element of same type
				if (i < cachedListSize) {
					GeoElement cachedGeo = list.getCached(i);
					
					// the cached element is a point: set value
					if (cachedGeo.isGeoBoolean()) {
						((GeoBoolean) cachedGeo).setValue(bool.getBoolean());
						geo = cachedGeo;
					}     			
				}
				
				// no cached point: create new one
				if (geo == null) {
					GeoBoolean geoBool = new GeoBoolean(cons);
					geoBool.setValue(bool.getBoolean());					
					geo = geoBool;
				}
				
				// add point to list
				list.add(geo);	
			}else if (element instanceof GeoFunction){
				GeoFunction fun = (GeoFunction)element;
				if (i < cachedListSize) {
					GeoElement cachedGeo = list.getCached(i);
					
					// the cached element is a point: set value
					if (cachedGeo.isGeoFunction()) {
						((GeoFunction) cachedGeo).set(fun);
						geo = cachedGeo;
					}     			
				}
				
				// no cached point: create new one
				if (geo == null) {
					GeoFunction geoFun = new GeoFunction(cons);
					geoFun.set(fun);					
					geo = geoFun;
				}
				list.add(geo);		
			}else if (element instanceof GeoText){
				GeoText text = (GeoText)element;
				if (i < cachedListSize) {
					GeoElement cachedGeo = list.getCached(i);
					
					// the cached element is a point: set value
					if (cachedGeo.isGeoText()) {
						((GeoText) cachedGeo).set(text);
						geo = cachedGeo;
					}     			
				}
				
				// no cached text: create new one
				if (geo == null) {
					GeoText geoFun = new GeoText(cons);
					geoFun.set(text);					
					geo = geoFun;
				}
				list.add(geo);
				
			}else if (element instanceof GeoElement){
				GeoElement geo0 = (GeoElement)element;
				if (i < cachedListSize) {
					GeoElement cachedGeo = list.getCached(i);
					
					// the cached element is the same type: set value
					if (cachedGeo.getGeoClassType().equals(geo0.getGeoClassType())) {
						cachedGeo.set(geo0);
						geo = cachedGeo;
					}     			
				}
				
				// no cached object: create new one
				if (geo == null) {
					geo = geo0.copy();					
				}
				list.add(geo);
				
			}
			else {
				App.debug("unsupported list operation: "+element.getClass()+"");
			}
		}
    }   
    
    @Override
	final public String toString(StringTemplate tpl) {
        // was defined as e.g.  L = 3 * {a, b, c}  
        return root.toString(tpl);
    }

	// TODO Consider locusequability
}
