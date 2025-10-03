// Copyright 2000-2002 FreeHEP
package org.freehep.graphicsio;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.freehep.graphics2d.AbstractVectorGraphics;

/**
 * This class provides specifies added methods for VectorGraphicsIO. All added
 * methods are declared abstract.
 * 
 * @author Charles Loomis
 * @author Mark Donszelmann
 * @version $Id: VectorGraphicsIO.java,v 1.4 2009-08-17 21:44:45 murkle Exp $
 */
public abstract class VectorGraphicsIO extends AbstractVectorGraphics {

	public VectorGraphicsIO() {
		super();
	}

	public VectorGraphicsIO(VectorGraphicsIO graphics) {
		super(graphics);
	}

	public abstract Dimension getSize();

	@Override
	public abstract void printComment(String comment);

}
