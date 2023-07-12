package org.geogebra.web.linker;

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
	protected String getJsInstallLocation(LinkerContext context) {
		return "org/geogebra/web/linker/installLocationIframe.js";
	}

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
		StringBuffer buffer = readFileToStringBuffer(
				"org/geogebra/web/linker/IframeModuleTemplate.js", logger);
		return fillSelectionScriptTemplate(
				buffer, logger, context, artifacts, null);
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

}
