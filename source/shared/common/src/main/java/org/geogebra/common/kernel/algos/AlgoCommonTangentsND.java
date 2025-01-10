/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCommonTangents.java, dsun48 [6/26/2011]
 *
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Two tangents through point P to conic section c
 */
public abstract class AlgoCommonTangentsND extends AlgoElement
		implements TangentAlgo {
	protected GeoConicND c;
	protected GeoConicND d;

	/**
	 * @param c
	 *            construction
	 */
	public AlgoCommonTangentsND(Construction c) {
		super(c);
	}

	@Override
	public final String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("CommonTangentOfCirclesAandB",
				"Common tangent of conics %0 and %1",
				c.getLabel(tpl), d.getLabel(tpl));
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Tangent;
	}

}