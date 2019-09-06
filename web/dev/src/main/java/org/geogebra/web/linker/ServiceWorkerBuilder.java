package org.geogebra.web.linker;

import java.io.InputStream;

import org.geogebra.common.GeoGebraConstants;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;

/**
 * Generates code of the service worker using worker_template.js
 * 
 * @author Zbynek
 */
public class ServiceWorkerBuilder {

	private static final String CAS_CHUNK_FILENAME = "13.nocache.js";
	private static final String[] SKIP_RESOURCE_SUFFIXES = { "symbolMap",
			".xml.gz", "rpc.log", "gwt.rpc", "manifest.txt",
			"rpcPolicyManifest", "cssmap", "MANIFEST.MF", ".txt", ".php",
			"README", "COPYING", "LICENSE", "oauthWindow.html",
			"windowslive.html", "devmode.js", "js/properties_",
			CAS_CHUNK_FILENAME };
	private LinkerContext context;
	private TreeLogger logger;
	private ArtifactSet artifacts;

	/**
	 * @param context
	 *            linker context
	 * @param artifacts
	 *            artifacts
	 * @param logger
	 *            logger
	 */
	public ServiceWorkerBuilder(LinkerContext context, ArtifactSet artifacts,
			TreeLogger logger) {
		this.context = context;
		this.artifacts = artifacts;
		this.logger = logger;
	}

	/**
	 * @param version
	 *            GeoGebra version in CDN (either "latest" or full version
	 *            number)
	 * @return service worker content
	 */
	public String getWorkerCode(String version) {
		StringBuilder allResoucesSb = new StringBuilder();
		if (artifacts != null) {
			StringBuilder publicSourcesSb = getAllCacheableArtifactsAsPartialJSON(
					artifacts, getModuleUrl(version));

			String[] cacheExtraFiles = AppCacheLinkerSettings
					.otherCachedFiles();
			allResoucesSb.append(publicSourcesSb);
			for (String staticFile : cacheExtraFiles) {
				allResoucesSb.append(",\n    \"");
				allResoucesSb.append(staticFile);
				allResoucesSb.append("\"");
			}
		}

		return buildManifest(allResoucesSb, logger);
	}

	private static StringBuilder getAllCacheableArtifactsAsPartialJSON(
			ArtifactSet artifacts, String moduleUrl) {
		StringBuilder publicSourcesSb = new StringBuilder();
		for (Artifact<?> artifact : artifacts) {
			if (artifact instanceof EmittedArtifact) {
				EmittedArtifact ea = (EmittedArtifact) artifact;
				String pathName = ea.getPartialPath();
				if (!skipResource(pathName)) {
					if (publicSourcesSb.length() > 0) {
						publicSourcesSb.append(",\n    ");
					}
					publicSourcesSb.append("\"");
					publicSourcesSb.append(moduleUrl);
					publicSourcesSb.append(pathName.replace("\\", "/"));
					publicSourcesSb.append("\"");
				}
			}
		}
		return publicSourcesSb;
	}

	private static boolean skipResource(String pathName) {
		for (String suffix : SKIP_RESOURCE_SUFFIXES) {
			if (pathName.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}

	private String getModuleUrl(String version) {
		return "https://www.geogebra.org/apps/" + version + "/"
				+ context.getModuleName() + "/";
	}

	private static String buildManifest(StringBuilder allResoucesSb,
			TreeLogger logger) {
		// we have to generate this unique id because the resources can change
		// but the hashed cache.html files can remain the same. build cache list
		String id = GeoGebraConstants.VERSION_STRING + ":"
				+ System.currentTimeMillis();
		String template = readTemplateAsString(logger);
		String sworkerContent = template
				.replace("%URLS%", allResoucesSb.toString())
				.replace("%ID%", id);
		return sworkerContent;
	}

	private static String readTemplateAsString(TreeLogger logger) {
		StringBuilder sb = new StringBuilder();

		// Create the manifest as a new artifact and return it:
		try {
			InputStream s = AppCacheLinker.class.getResourceAsStream(
					"/org/geogebra/web/worker_template.js");
			byte[] contents = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = s.read(contents)) != -1) {
				sb.append(new String(contents, 0, bytesRead));
			}
			// fbr.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Type.ERROR, e.getMessage());
		}
		return sb.toString();
	}

}
