package org.geogebra.desktop.headless;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.geogebra.common.io.XMLParseException;
import org.geogebra.common.jre.io.MyXMLioJre;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;

public class GFileHandler {
	/**
	 * @param app
	 *            app
	 * @param is
	 *            stream
	 * @param isMacroFile
	 *            macro?
	 * @return whether successfully loaded
	 * @throws XMLParseException
	 *             for invalid XML; MyErrors are ignored
	 * @throws IOException if stream is invalid
	 */
	public static boolean loadXML(App app, InputStream is, boolean isMacroFile)
			throws XMLParseException, IOException {
		try {
			if (!isMacroFile) {
				app.setMoveMode();
			}

			// make sure objects are displayed in the correct View
			app.setActiveView(App.VIEW_EUCLIDIAN);

			// reset equation behaviour to app defaults (to clear out any overrides applied
			// from construction defaults in previously opened files)
			// TODO either call this from non-desktop specific code, or add
			app.getKernel().setEquationBehaviour(app.getConfig().getEquationBehaviour());

			// reset unique id (for old files, in case they don't have one)
			app.resetUniqueId();

			BufferedInputStream bis = new BufferedInputStream(is);

			if (bis.markSupported()) {
				bis.mark(Integer.MAX_VALUE);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(bis, StandardCharsets.UTF_8));
				String str = reader.readLine();

				// check if .ggb file is actually a base64 file from 4.2 Chrome
				// App
				if (str != null && str.startsWith("UEs")) {

					StringBuilder sb = new StringBuilder(str);
					sb.append("\n");

					while ((str = reader.readLine()) != null) {
						sb.append(str).append('\n');
					}

					reader.close();
					is.close();
					bis.close();

					byte[] zipFile = Base64.decode(sb.toString());

					return app.loadXML(zipFile);
				}

				bis.reset();
			}

			((MyXMLioJre) app.getXMLio()).readZipFromInputStream(bis,
					isMacroFile);

			is.close();
			bis.close();

			if (!isMacroFile) {
				app.getKernel().initUndoInfo();
				app.setSaved();
				app.resetCurrentFile();
			}

			// command list may have changed due to macros
			app.updateCommandDictionary();
			return true;
		} catch (MyError err) {
			app.resetCurrentFile();
			app.showError(err);
			return false;
		}
	}
}
