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

	private static final String SWORKER = "sworker.js";
	private static final String SWORKER_LOCKED = "sworker-locked.js";
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

		ServiceWorkerBuilder serviceWorkerBuilder = new ServiceWorkerBuilder(
				context, artifacts, logger);
		String sworkerContent = serviceWorkerBuilder.getWorkerCode("latest");
		toReturn.add(emitString(logger, sworkerContent, SWORKER));

		String sworkerContentLocked = serviceWorkerBuilder
				.getWorkerCode(GeoGebraConstants.VERSION_STRING);
		toReturn.add(emitString(logger, sworkerContentLocked, SWORKER_LOCKED));
	}

}
