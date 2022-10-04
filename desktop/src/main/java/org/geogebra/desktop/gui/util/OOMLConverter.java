package org.geogebra.desktop.gui.util;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.geogebra.common.util.debug.Log;

public class OOMLConverter {

	/**
	 * @param oomlRaw OOML
	 * @return MATHML
	 */
	public static String oomlToMathml(String oomlRaw) {
		TransformerFactory factory = TransformerFactory.newInstance();
		String ooml = oomlRaw.replaceAll("<i[^>]*>", "")
				.replaceAll("<span[^>]*>", "")
				.replace("</i>", "").replace("</span>", "")
				.replace("<m:r>", "<m:r><m:t>")
				.replace("</m:r>", "</m:t></m:r>");
		Log.debug(ooml);
		Source xmlFile = new StreamSource(new StringReader(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						+ "<w:document xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\" "
						+ "xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\""
						+ "><w:body><w:p>" + ooml
						+ "</w:p></w:body></w:document>"));

		try {
			File ssFile = new File(
					"C:\\Program Files\\Microsoft Office 15\\root\\office15\\OMML2MML.XSL");
			if (!ssFile.exists()) {
				ssFile = new File(
						"C:\\Program Files\\Microsoft Office\\Office14\\OMML2MML.XSL");
			}
			Source stylesheet = new StreamSource(ssFile);
			Transformer transformer = factory.newTransformer(stylesheet);
			StringWriter writer = new StringWriter();
			Result output = new StreamResult(writer);
			transformer.transform(xmlFile, output);
			String xml = writer.toString();
			return xml.substring(xml.indexOf('>') + 1).replace("mml:", "");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
