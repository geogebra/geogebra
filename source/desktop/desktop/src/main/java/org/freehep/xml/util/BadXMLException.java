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

package org.freehep.xml.util;

import org.xml.sax.SAXException;

/**
 * A SAXException with an optional nested exception
 * 
 * @author tonyj
 * @version $Id: BadXMLException.java,v 1.3 2008-05-04 12:20:45 murkle Exp $
 */

public class BadXMLException extends SAXException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1493961676061071756L;

	public BadXMLException(String message) {
		super(message);
	}

	public BadXMLException(String message, Throwable detail) {
		super(message);
		initCause(detail);
	}
}
