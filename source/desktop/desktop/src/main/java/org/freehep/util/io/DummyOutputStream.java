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

package org.freehep.util.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Equivalent to writing to /dev/nul
 * 
 * @author tonyj
 * @version $Id: DummyOutputStream.java,v 1.3 2008-05-04 12:21:00 murkle Exp $
 */
public class DummyOutputStream extends OutputStream {
	/**
	 * Creates a Dummy output steram.
	 */
	public DummyOutputStream() {
	}

	@Override
	public void write(int b) throws IOException {
	}

	@Override
	public void write(byte[] b) throws IOException {
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
	}
}
