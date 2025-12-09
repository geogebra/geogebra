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

package org.geogebra.test;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.io.latex.ParseException;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.SequenceNode;
import org.junit.Test;

public class SerializeLaTeX {

	@Test
	public void testJavaSerializationShouldKeepAllData() {
		try {
			ByteArrayOutputStream targetStream = new ByteArrayOutputStream();
			Formula mf = new Parser(new TemplateCatalog()).parse("()");
			ObjectOutputStream oos = new ObjectOutputStream(targetStream);
			oos.writeObject(mf.getRootNode());
			InputStream sourceStream = new ByteArrayInputStream(targetStream.toByteArray());
			ObjectInputStream inputStream = new ObjectInputStream(sourceStream);
			Object back = inputStream.readObject();
			assertThat(back, instanceOf(SequenceNode.class));
		} catch (ParseException | IOException | ClassNotFoundException e) {
			fail("Can't parse: " + e);
		}
	}
}
