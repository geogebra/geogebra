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

package org.geogebra.desktop.main;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.util.logging.Level;

/**
 * Class defining 2 new Logging levels, one for STDOUT, one for STDERR, used
 * when multiplexing STDOUT and STDERR into the same rolling log file via the
 * Java Logging APIs.
 * 
 * http://blogs.sun.com/nickstephen/entry/java_redirecting_system_out_and
 */
public class StdOutErrLevel extends Level {
	private static final long serialVersionUID = 1L;

	/**
	 * Private constructor
	 */
	private StdOutErrLevel(String name, int value) {
		super(name, value);
	}

	/**
	 * Level for STDOUT activity.
	 */
	public static final Level STDOUT = new StdOutErrLevel("STDOUT",
			Level.INFO.intValue() + 53);
	/**
	 * Level for STDERR activity
	 */
	public static final Level STDERR = new StdOutErrLevel("STDERR",
			Level.INFO.intValue() + 54);

	/**
	 * Method to avoid creating duplicate instances when deserializing the
	 * object.
	 * 
	 * @return the singleton instance of this <code>Level</code> value in this
	 *         classloader
	 * @throws ObjectStreamException
	 *             If unable to deserialize
	 */
	protected Object readResolve() throws ObjectStreamException {
		if (this.intValue() == STDOUT.intValue()) {
			return STDOUT;
		}
		if (this.intValue() == STDERR.intValue()) {
			return STDERR;
		}
		throw new InvalidObjectException("Unknown instance :" + this);
	}

}
