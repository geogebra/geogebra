/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel.geos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.commands.CmdScriptingInterface;

/**
 * List of GeoElements
 */
public class GeoScriptAction extends GeoElement  {

	
	private CmdScriptingInterface action;
	private Command command;
	public GeoScriptAction(Construction c) {
		super(c);
		// TODO Auto-generated constructor stub
	}
	public GeoScriptAction(Construction cons, CmdScriptingInterface cmdScripting,Command command) {
		this(cons);
		action = cmdScripting;
		this.command = command;
	}
	

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.DEFAULT;
	}

	@Override
	public GeoElement copy() {
		GeoScriptAction n = new GeoScriptAction(cons);
		n.set(this);
		return n;
		
	}

	@Override
	public void set(GeoElement geo) {
		if(!(geo instanceof GeoScriptAction))
				throw new IllegalArgumentException();
		action = ((GeoScriptAction)geo).action;
		command = ((GeoScriptAction)geo).command;
	}

	@Override
	public boolean isDefined() {
		return action != null;
	}

	@Override
	public void setUndefined() {
		action = null;
	}

	@Override
	public String toValueString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		return false;
	}

	@Override
	protected String getTypeString() {
		return "ScriptAction";
	}

	@Override
	public boolean isEqual(GeoElement geo) {
		if(!(geo instanceof GeoScriptAction))
			return false;
		return action == ((GeoScriptAction)geo).action;
	}

	@Override
	public String getClassName() {
		return "GeoScriptAction";
	}
	public void perform() {
		if(action!=null)
			action.perform(command);
	}
		
}