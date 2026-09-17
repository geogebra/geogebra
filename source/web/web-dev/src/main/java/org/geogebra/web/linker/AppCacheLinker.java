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

import org.geogebra.common.GeoGebraConstants;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
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

	private static final String SWORKER_LOCKED = "sworker-locked.js";
	private static final String MANIFEST = "appcache.nocache.manifest";

	@Override
	public String getDescription() {
		return "AppCacheLinker";
	}

	@Override
	public ArtifactSet link(
			TreeLogger logger, LinkerContext context, ArtifactSet artifacts, boolean onePermutation)
			throws UnableToCompleteException {

		ArtifactSet toReturn = new ArtifactSet(artifacts);
		if (onePermutation) {
			return toReturn;
		}

		if (toReturn.find(SelectionInformation.class).isEmpty()) {
			logger.log(TreeLogger.INFO, "devmode: generating empty " + MANIFEST);
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
	 * @param toReturn output artifact set
	 */
	private void emitLandingPageCacheManifest(
			LinkerContext context, TreeLogger logger, ArtifactSet artifacts, ArtifactSet toReturn)
			throws UnableToCompleteException {

		ServiceWorkerBuilder serviceWorkerBuilder =
				new ServiceWorkerBuilder(context, artifacts, logger);

		String sworkerContentLocked =
				serviceWorkerBuilder.getWorkerCode(GeoGebraConstants.VERSION_STRING);
		toReturn.add(emitString(logger, sworkerContentLocked, SWORKER_LOCKED));
	}
}
