/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.gui.view.algebra;

import geogebra.common.awt.Rectangle;
import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;

import java.util.Iterator;

public class AbstractAlgebraController {

	protected Kernel kernel;
	protected AbstractApplication app;

	protected AlgebraView view;

	//private GeoVector tempVec;
	//private boolean kernelChanged;

	/** Creates new CommandProcessor */
	public AbstractAlgebraController(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();		
	}

	public void setView(AlgebraView view) {
		this.view = view;
	}

	public AbstractApplication getApplication() {
		return app;
	}

	public Kernel getKernel() {
		return kernel;
	}

	protected GeoElement lastSelectedGeo = null;
	protected boolean skipSelection;
}
