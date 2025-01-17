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

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;

public class SerializeLaTeX {

	@Test
	public void testJavaSerializationShouldKeepAllData() {
		try {
			ByteArrayOutputStream targetStream = new ByteArrayOutputStream();
			MathFormula mf = new Parser(new MetaModel()).parse("()");
			ObjectOutputStream oos = new ObjectOutputStream(targetStream);
			oos.writeObject(mf.getRootComponent());
			InputStream sourceStream = new ByteArrayInputStream(targetStream.toByteArray());
			ObjectInputStream inputStream = new ObjectInputStream(sourceStream);
			Object back = inputStream.readObject();
			assertThat(back, instanceOf(MathSequence.class));
		} catch (ParseException | IOException | ClassNotFoundException e) {
			fail("Can't parse: " + e);
		}
	}
}
