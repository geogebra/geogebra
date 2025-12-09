/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.ExtendedBoolean;

/**
 * List of GeoElements
 */
public class GeoScriptAction extends GeoElement {

	private CmdScripting action;
	private Command command;

	/**
	 * Creates new script action
	 * 
	 * @param c
	 *            construction
	 */
	public GeoScriptAction(Construction c) {
		super(c);
	}

	/**
	 * Creates new script action
	 * 
	 * @param cons
	 *            construction
	 * @param cmdScripting
	 *            command processor to be used
	 * @param command
	 *            command to be processed
	 */
	public GeoScriptAction(Construction cons, CmdScripting cmdScripting,
			Command command) {
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
	public void set(GeoElementND geo) {
		if (!(geo instanceof GeoScriptAction)) {
			throw new IllegalArgumentException();
		}
		action = ((GeoScriptAction) geo).action;
		command = ((GeoScriptAction) geo).command;
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
	public String toValueString(StringTemplate tpl) {
		return command.toValueString(tpl);
	}

	@Override
	public boolean showInAlgebraView() {
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		return false;
	}

	@Override
	public ExtendedBoolean isEqualExtended(GeoElementND geo) {
		return ExtendedBoolean.newExtendedBoolean(geo instanceof GeoScriptAction
				&& action == ((GeoScriptAction) geo).action);
	}

	/**
	 * Perform the command
	 */
	public void perform() {
		if (action != null) {
			action.performAndClean(command);
		}
		remove();
	}

	@Override
	final public HitType getLastHitType() {
		return HitType.NONE;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VOID;
	}

}