/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValidExpression;


/**
 * Makes it possible to switch between the Extended form and the Input form (Exactly the way the user entered it)
 * subclasses must call super.toString() and provide the "normal" or extended form with .valueString().
 *
 */
public abstract class GeoUserInputElement extends GeoElement {
	
	private ValidExpression userInput;
	private boolean inputForm;
	private boolean validInputForm;

	/**
	 * Creates new element with user input
	 * @param c construction
	 */
	public GeoUserInputElement(Construction c) {
		super(c);
		
		// moved from GeoElement's constructor
		// must be called from the subclass, see
		//http://benpryor.com/blog/2008/01/02/dont-call-subclass-methods-from-a-superclass-constructor/
		setConstructionDefaults(); // init visual settings

		validInputForm=true;
	}
	
	/**
	 * Creates new element with user input
	 * @param c construction
	 * @param userInput input defining this element
	 */
	public GeoUserInputElement(Construction c,ValidExpression userInput) {
		this(c);
		this.userInput=userInput;
	}
	
	/**
	 * If possible, switches input form on
	 */
	public void setInputForm(){
		inputForm=true&&validInputForm;
	}
	
	/**
	 * Switches input form off
	 */
	public void setExtendedForm(){
		inputForm=false;
	}
	
	/**
	 * @return true iff input form is active
	 */
	public boolean isInputForm() {
		return inputForm;
	}
	
	/**
	 * @param input user input
	 */
	public void setUserInput(ValidExpression input){
		userInput=input;
	}

	@Override
	public String toString(StringTemplate tpl){
		return label+": "+toValueString(tpl);
	}
	
	@Override
	public String toValueString(StringTemplate tpl){
		if (validInputForm&&inputForm&&userInput!=null)
			return userInput.toValueString(tpl);
		
		return toRawValueString(tpl);
		
	}
	
	@Override
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
	
	/**
	 * @param tpl string template
	 * @return raw string
	 */
	protected abstract String toRawValueString(StringTemplate tpl);
	
	/**
	 * @param b true iff input should be considered valid
	 */
	public void setValidInputForm(boolean b){
		validInputForm=b;
		if (!validInputForm){
			inputForm=false;
		}
	}

	/**
	 * 
	 * @return true iff input is valid
	 */
	public boolean isValidInputForm() {
		return validInputForm;
	}
	

}
