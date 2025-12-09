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

package org.geogebra.web.linker;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.core.ext.linker.SyntheticArtifact;
import com.google.gwt.core.linker.CrossSiteIframeLinker;

@LinkerOrder(LinkerOrder.Order.PRIMARY)
@Shardable
public class StandardIframeLinker extends CrossSiteIframeLinker {
	private SyntheticArtifact module;

	@Override
	protected EmittedArtifact emitSelectionScript(TreeLogger logger,
			final LinkerContext context, ArtifactSet artifacts)
			throws UnableToCompleteException {
		/*
		 * Last modified is important to keep Development Mode refreses from
		 * clobbering Production Mode compiles. We set the timestamp on the
		 * Development Mode selection script to the same mod time as the module (to
		 * allow updates). For Production Mode, we just set it to now.
		 */
		long lastModified;
		if (permutationsUtil.getPermutationsMap().isEmpty()) {
			lastModified = context.getModuleLastModified();
		} else {
			lastModified = System.currentTimeMillis();
		}
		String selectionScript = generateSelectionScript(logger, context, artifacts);
		String selectionScriptES6 = generateSelectionScriptModule(logger, context, artifacts);
		this.module = emitString(logger, selectionScriptES6, context.getModuleName()
				+ ".nocache.mjs", lastModified);
		return emitString(logger, selectionScript, context.getModuleName()
				+ ".nocache.js", lastModified);
	}

	protected String generateSelectionScriptModule(TreeLogger logger,
			LinkerContext context, ArtifactSet artifacts)
			throws UnableToCompleteException {
		if (getExportFilename(context).isEmpty()) {
			return "";
		}
		StringBuffer buffer = readFileToStringBuffer(
				"org/geogebra/web/linker/IframeModuleTemplate.js", logger);
		StringBuilder exports = new StringBuilder();
		for (String submodule: getExportFilename(context)) {
			String[] parts = submodule.split(":");
			exports.append("export const %0 = createSubmoduleAPI(\"%0\", \"%1\");\n"
					.replace("%0", parts[0]).replace("%1", parts[1]));
		}
		Optional<String> beforeRenderJS = getBeforeRenderJS(context);
		String beforeRenderContent = beforeRenderJS.isPresent()
				? readFileToStringBuffer(beforeRenderJS.get(), logger).toString()
				: "resolve(options)";

		replaceAll(buffer, "__BEFORE_RENDER__", beforeRenderContent);

		replaceAll(buffer, "__EXPORT_SUBMODULES__", exports.toString());
		return fillSelectionScriptTemplate(
				buffer, logger, context, artifacts, null);
	}

	private List<String> getExportFilename(LinkerContext context) {
		return getConfigurationProperty(context, "es6export").collect(Collectors.toList());
	}

	private Stream<String> getConfigurationProperty(LinkerContext context, String name) {
		return context.getConfigurationProperties().stream()
				.filter(prop -> prop.getName().equals(name) && !prop.getValues().isEmpty())
				.flatMap(prop -> Arrays.stream(prop.getValues().get(0).split(",")));
	}

	private Optional<String> getBeforeRenderJS(LinkerContext context) {
		return getConfigurationProperty(context, "beforeRenderJS")
				.findAny();
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context,
			ArtifactSet artifacts, boolean onePermutation)
			throws UnableToCompleteException {
		ArtifactSet base = super.link(logger, context, artifacts, onePermutation);
		if (module != null) {
			base.add(module);
		}
		return base;
	}

	@Override
	protected String getSourceMapUrl(LinkerContext context, String strongName, int fragmentId) {
		String mapUrl = super.getSourceMapUrl(context, strongName, fragmentId);
		return mapUrl == null ? null : mapUrl.replace("__BASE_PATH__", getSourceMapBasePath());
	}

	private String getSourceMapBasePath() {
		String host = System.getenv("GWT_SOURCE_MAP_PATH");
		return host == null ? "" : host;
	}

}
