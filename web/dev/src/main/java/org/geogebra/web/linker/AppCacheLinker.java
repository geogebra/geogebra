/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.geogebra.web.linker;

import java.io.InputStream;

import org.geogebra.common.GeoGebraConstants;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.Artifact;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.Shardable;
import com.google.gwt.core.ext.linker.impl.SelectionInformation;

/**
 * AppCacheLinker - linker for public path resources in the Application Cache.
 * <p>
 * To use:
 * <ol>
 * <li>Add {@code manifest="YOURMODULENAME/appcache.nocache.manifest"} to the
 * {@code <html>} tag in your base html file. E.g., {@code 
 * <html manifest="mymodule/appcache.nocache.manifest">}</li>
 * <li>Add a mime-mapping to your web.xml file:
 * <p>
 * 
 * <pre>
 * {@code <mime-mapping>
 * <extension>manifest</extension>
 * <mime-type>text/cache-manifest</mime-type>
 * </mime-mapping>
 * }
 * </pre>
 * 
 * </li>
 * </ol>
 * <p>
 * On every compile, this linker will regenerate the appcache.nocache.manifest
 * file with files from the public path of your module.
 * <p>
 * To obtain a manifest that contains other files in addition to those generated
 * by this linker, create a class that inherits from this one and overrides
 * {@code otherCachedFiles()}, and use it as a linker instead:
 * <p>
 * 
 * <pre>
 * {@code @Shardable}
 * public class MyAppCacheLinker extends AbstractAppCacheLinker {
 *   {@code @Override}
 *   protected String[] otherCachedFiles() {
 *     return new String[] {"/MyApp.html","/MyApp.css"};
 *   }
 * }
 * </pre>
 */
@LinkerOrder(Order.POST)
@Shardable
public class AppCacheLinker extends AbstractLinker {

	private static final String SWORKER = "sworker.js";
	private static final String MANIFEST = "appcache.nocache.manifest";

	@Override
	public String getDescription() {
		return "AppCacheLinker";
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context,
			ArtifactSet artifacts, boolean onePermutation)
			throws UnableToCompleteException {

		ArtifactSet toReturn = new ArtifactSet(artifacts);

		if (onePermutation) {
			return toReturn;
		}

		if (toReturn.find(SelectionInformation.class).isEmpty()) {
			logger.log(TreeLogger.INFO,
					"devmode: generating empty " + MANIFEST);
		} else {
			emitLandingPageCacheManifest(context, logger, artifacts, toReturn);
		}

		// Create the general cache-manifest resource for the landing page:

		return toReturn;
	}


	/**
	 * Creates the cache-manifest resource specific for the landing page.
	 * 
	 * @param context
	 *            the linker environment
	 * @param logger
	 *            the tree logger to record to
	 * @param artifacts
	 *            {@code null} to generate an empty cache manifest
	 * @param toReturn
	 */
	private void emitLandingPageCacheManifest(LinkerContext context,
			TreeLogger logger, ArtifactSet artifacts, ArtifactSet toReturn)
			throws UnableToCompleteException {
		StringBuilder publicSourcesSb = new StringBuilder();
		StringBuilder staticResoucesSb = new StringBuilder();

		if (artifacts != null) {
			// Iterate over all emitted artifacts, and collect all cacheable
			// artifacts
			for (@SuppressWarnings("rawtypes")
			Artifact artifact : artifacts) {
				if (artifact instanceof EmittedArtifact) {
					EmittedArtifact ea = (EmittedArtifact) artifact;
					String pathName = ea.getPartialPath();
					if (pathName.endsWith("symbolMap")
							|| pathName.endsWith(".xml.gz")
							|| pathName.endsWith("rpc.log")
							|| pathName.endsWith("gwt.rpc")
							|| pathName.endsWith("manifest.txt")
							|| pathName.startsWith("rpcPolicyManifest")
							|| pathName.endsWith("cssmap")
							|| pathName.endsWith("MANIFEST.MF")
							|| pathName.endsWith(".txt")
							|| pathName.endsWith(".php")
							|| pathName.endsWith("README")
							|| pathName.endsWith("COPYING")
							|| pathName.endsWith("LICENSE")
							|| pathName.endsWith("oauthWindow.html")
							|| pathName.endsWith("windowslive.html")
							|| pathName.endsWith("devmode.js")
							|| pathName.startsWith("js/properties_")
							|| pathName.endsWith("6.nocache.js")) {
						// skip these resources
					} else {
						publicSourcesSb
								.append("\"https://download.geogebra.org/web/5.0/latest/web3d/"
										+ pathName.replace("\\", "/")
										+ "\",\n");
					}
				}
			}

			String[] cacheExtraFiles = AppCacheLinkerSettings
					.otherCachedFiles();
			for (int i = 0; i < cacheExtraFiles.length; i++) {
				staticResoucesSb.append("\"");
				staticResoucesSb.append(cacheExtraFiles[i]);
				staticResoucesSb.append("\"");
				if (i < cacheExtraFiles.length - 1) {
					staticResoucesSb.append(",\n");
				}

			}
		}

		// build manifest
		String id = GeoGebraConstants.VERSION_STRING + ":"
				+ System.currentTimeMillis();
		// we have to generate this unique id because the resources can change
		// but
		// the hashed cache.html files can remain the same.
		// build cache list
		StringBuilder sb = new StringBuilder();

		// logger.log(
		// TreeLogger.INFO,
		// "Make sure you have the following"
		// + " attribute added to your landing page's <html> tag: <html
		// manifest=\""
		// + context.getModuleFunctionName() + "/" + MANIFEST
		// + "\">");

		// Create the manifest as a new artifact and return it:
		try {
			InputStream s = AppCacheLinker.class.getResourceAsStream(
					"/org/geogebra/web/worker_template.js");
			byte[] contents = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = s.read(contents)) != -1) {
				sb.append(new String(contents, 0, bytesRead)
						.replace("%URLS%",
								publicSourcesSb.toString()
										+ staticResoucesSb.toString())
						.replace("%ID%", id));
			}
			// fbr.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Type.ERROR, e.getMessage());
		}
		// toReturn.add(emitString(logger, sbM.toString(), MANIFEST));
		toReturn.add(emitString(logger, sb.toString(), SWORKER));
		toReturn.add(emitString(logger,
				("{\n" + publicSourcesSb.toString()
						+ staticResoucesSb.toString()).replaceAll("\\n", "\n  ")
						+ "\n}",
				"files.json"));
	}

}
