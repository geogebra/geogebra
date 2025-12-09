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

package org.geogebra.common.io;

import static org.junit.Assert.fail;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.util.debug.Log;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlTestUtil {

	private static final String GEOGEBRA_XSD_URL = "https://www.geogebra.org/apps/xsd/ggb.xsd";
	private static Validator VALIDATOR;

	/**
	 * Validate app's state against XML schema.
	 *
	 * @param application
	 *            app
	 */
	public static void checkCurrentXML(AppCommon application) {
		String xml = application.getXML();
		try {
			Validator validator = getValidator();
			if (validator == null) {
				return;
			}
			Source xmlFile = new StreamSource(new StringReader(xml));
			validator.validate(xmlFile);
		} catch (SAXParseException se) {
			int l = se.getLineNumber();
			String[] rows = xml.split("\\n");
			for (int i = l - 2; i < l + 3 && i > 0 && i < rows.length; i++) {
				Log.debug(rows[i]);
			}
			fail(se.getLocalizedMessage());
		} catch (Exception e) {
			Log.debug(e);
			fail(e.getLocalizedMessage());
		}
	}

	private static Validator getValidator() throws MalformedURLException, SAXException {
		if (VALIDATOR != null) {
			return VALIDATOR;
		}
		URL schemaFile = new URL(GEOGEBRA_XSD_URL);
		try {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaFile);
			VALIDATOR = schema.newValidator();
		} catch (IllegalArgumentException exception) {
			// Ignore
		}
		return VALIDATOR;
	}
}
