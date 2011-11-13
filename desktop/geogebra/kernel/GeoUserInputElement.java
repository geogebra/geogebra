/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ValidExpression;

/**
 * Makes it possible to switch between the Extended form and the Input form (Exactly the way the user entered it)
 * subclasses must call super.toString() and provide the "normal" or extended form with .valueString().
 *
 */
public abstract class GeoUserInputElement extends GeoElement {
	
	private ValidExpression userInput;
	private boolean inputForm;
	private boolean validInputForm;

	public GeoUserInputElement(Construction c) {
		super(c);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		validInputForm=true;
	}
	
	public GeoUserInputElement(Construction c,ValidExpression userInput) {
		this(c);
		this.userInput=userInput;
	}
	
	public void setInputForm(){
		inputForm=true&&validInputForm;
	}
	
	public void setExtendedForm(){
		inputForm=false;
	}
	
	public boolean isInputForm() {
		return inputForm;
	}
	
	public void setUserInput(ValidExpression input){
		userInput=input;
	}

	public String toString(){
		return label+": "+toValueString();
	}
	
	public String toValueString(){
		if (validInputForm&&inputForm&&userInput!=null){
			return userInput.toValueString();
		}else{			
			return toRawValueString();
		}
	}
	
	public void set(GeoElement geo){
		if (!(geo instanceof GeoUserInputElement))
			return;
		userInput=((GeoUserInputElement)geo).userInput;
	}

	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		sb.append("\t<userinput show=\"");
		sb.append(inputForm);
		if (isIndependent()){ //if dependent we save the expression somewhere else anyway
			sb.append("\" value=\"");
			sb.append(userInput);
		}
		sb.append("\" valid=\"");
		sb.append(validInputForm);
		sb.append("\" />\n");
	}
	
	protected abstract String toRawValueString();
	
	public void setValidInputForm(boolean b){
		validInputForm=b;
		if (!validInputForm){
			inputForm=false;
		}
	}

	public boolean isValidInputForm() {
		return validInputForm;
	}
	

}
