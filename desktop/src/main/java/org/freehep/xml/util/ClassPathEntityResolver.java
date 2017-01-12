// Copyright 2000-2003, SLAC, Stanford, California, U.S.A.
package org.freehep.xml.util;

import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An implementation of an EntityResolver which can be used to locate DTD files
 * located on the current java class path
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ClassPathEntityResolver.java,v 1.4 2009-06-22 02:18:23
 *          hohenwarter Exp $
 */
public class ClassPathEntityResolver implements EntityResolver {
	private Class root;
	private String name;
	private String prefix;

	/**
	 * Create a ClassPathEntityResolver that will handle a single DTD.
	 * 
	 * @param DTDName
	 *            The DTDName to resolve (e.g. myStuff.dtd)
	 * @param root
	 *            A class in the same package as the DTD
	 */
	public ClassPathEntityResolver(String DTDName, Class root) {
		this.name = DTDName;
		this.root = root;
	}

	/**
	 * Create a ClassPathEntityResolver that can handle a collection of DTD's
	 * 
	 * @param root
	 *            A class in the package to be used as the root of the DTD path
	 * @param DTDPrefix
	 *            A string containing the prefix for the DTD to match
	 */
	public ClassPathEntityResolver(Class root, String DTDPrefix) {
		this.prefix = DTDPrefix;
		this.root = root;
	}

	/*
	 * Implementation of resolveEntity method
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException {
		if (name != null && systemId.endsWith(name)) {
			InputStream in = root.getResourceAsStream(name);
			if (in == null) {
				throw new SAXException(systemId + " not found");
			}
			return new InputSource(in);
		}
		if (prefix != null && systemId.startsWith(prefix)) {
			String postfix = systemId.substring(prefix.length());
			InputStream in = root.getResourceAsStream(postfix);
			if (in == null) {
				throw new SAXException(systemId + " not found");
			}
			return new InputSource(in);
		}
		return null;
	}
}
