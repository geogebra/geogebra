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

	/**
	 * copies the full file referenced by filenam onto the os (PrintWriter). The
	 * file location is relative to the current class
	 * 
	 * @param object
	 *            from which to refer to resource file
	 * @param fileName
	 *            name of file to be copied
	 * @param os
	 *            output to copy the file to
	 */
	public static void copyResourceTo(Object object, String fileName,
			PrintStream os) {
		copyResourceTo(object, fileName,
				new PrintWriter(new OutputStreamWriter(os)));
	}

	public static void copyResourceTo(Object object, String fileName,
			PrintWriter os) {
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = object.getClass().getResourceAsStream(fileName);
			br = new BufferedReader(new InputStreamReader(is));
			String s;
			while ((s = br.readLine()) != null) {
				os.println(s);
			}
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
