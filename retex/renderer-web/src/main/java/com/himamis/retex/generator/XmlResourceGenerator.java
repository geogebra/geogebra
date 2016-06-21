package com.himamis.retex.generator;

import java.net.URL;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.dev.util.Util;
import com.google.gwt.resources.ext.AbstractResourceGenerator;
import com.google.gwt.resources.ext.ResourceContext;
import com.google.gwt.resources.ext.ResourceGeneratorUtil;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.StringSourceWriter;
import com.himamis.retex.renderer.web.resources.xml.XmlResource;

public class XmlResourceGenerator extends AbstractResourceGenerator
{

	@Override
	public String createAssignment(TreeLogger logger, ResourceContext context,
			JMethod method) throws UnableToCompleteException {

		// Extract the SVG name from the @Source annotation
		URL[] resources = ResourceGeneratorUtil.findResources(logger, context,
				method);
		if (resources.length != 1) {
			logger.log(TreeLogger.ERROR,
					"Exactly one resource must be specified", null);
			throw new UnableToCompleteException();
		}
		URL resource = resources[0];

		String xml = Util.readURLAsString(resource);
		int commentStart = 1;
		do {
			commentStart = xml.indexOf("<!--");
			if (commentStart >= 0) {
				int commentEnd = xml.indexOf("-->", commentStart + 1);

				if (commentEnd > 0) {
					xml = xml.substring(0, commentStart - 1)
							+ xml.substring(commentEnd + 3);
				}
			}
		} while (commentStart >= 0);

		SourceWriter sw = new StringSourceWriter();
		sw.println("new " + XmlResource.class.getName() + "() {");
		sw.indent();

		// Convenience when examining the generated code.
		sw.println("// " + resource.toExternalForm());

		sw.println("@Override");
		sw.println("public String getName() {");
		sw.indent();
		sw.println("return \"" + method.getName() + "\";");
		sw.outdent();
		sw.println("}");

		sw.println("@Override");
		sw.println("public String getText() {");
		sw.indent();
		sw.println("return \"" + Generator.escape(xml) + "\";");
		sw.outdent();
		sw.println("}");

		sw.outdent();
		sw.println("}");

		return sw.toString();
	}
}