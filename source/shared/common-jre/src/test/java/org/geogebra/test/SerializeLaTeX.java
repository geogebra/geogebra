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
