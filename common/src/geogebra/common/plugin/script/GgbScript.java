package geogebra.common.plugin.script;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.main.App;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.ScriptError;
import geogebra.common.plugin.ScriptType;
import geogebra.common.util.StringUtil;

import java.util.ArrayList;

/**
 * @author arno
 * Script class for GgbScript scripts
 */
public class GgbScript extends Script {
	
	private AlgebraProcessor proc;
	
	/**
	 * @param app the script's application
	 * @param scriptText the script's source code
	 */
	public GgbScript(App app, String scriptText) {
		super(app, scriptText);
		this.proc = app.getKernel().getAlgebraProcessor();
	}
	
	@Override
	public String getText() {
		return script2LocalizedScript(app, text);
	}
	
	@Override
	public void run(Event evt) throws ScriptError {
		String scriptText;
		if (text == null) {
			return;
		}
		if (evt.argument == null) {
			scriptText = text;
		} else {
			scriptText = text.replaceAll("%0", evt.argument);
		}
		String[] lines = scriptText.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.equals("") || line.charAt(0) == '#') {
				continue;
			}
			try {
				proc.processAlgebraCommandNoExceptionHandling(line, false, false, true, false);
			} catch (Throwable e) {
				throw new ScriptError( 
					app.getLocalization().getPlain(
						"ErrorInScriptAtLineAFromObjectB",
						(i + 1) + "",
						evt.target.getLabel(StringTemplate.defaultTemplate)
					) 
					+ "\n" + e.getLocalizedMessage()
				);
			}
		}
	}

	public static String script2LocalizedScript(App app, String st) {
		final String[] starr = splitScriptByCommands(st);
		final StringBuilder retone = new StringBuilder();
		for (int i = 0; i < starr.length; i++) {
			if ((i % 2) == 0 || isFunction(starr,i,app)) {
				retone.append(starr[i]);
			} else {
				retone.append(app.getLocalization().getCommand(starr[i]));
			}
		}
		return retone.toString();
	}

	private static boolean isFunction(String[] starr, int i,App app) {
		if(i>=starr.length-1 || starr[i+1].startsWith("["))
			return false;
		if(app.getKernel().lookupLabel(starr[i])!=null)
			return true;		
		return false;
	}

	/**
	 * Delocalize a script
	 * @param app the application
	 * @param st the script text
	 * @return the text of the delocalized script
	 */
	public static String localizedScript2Script(App app, String st) {
		final String[] starr = splitScriptByCommands(st);
		final StringBuilder retone = new StringBuilder();
		for (int i = 0; i < starr.length; i++) {
			if ((i % 2) == 0) {
				retone.append(starr[i]);
			} else {
				// allow English language command in French scripts
				if (!isFunction(starr,i,app) && app.getInternalCommand(starr[i]) != null) {
					retone.append(app.getInternalCommand(starr[i]));
				} else {
					// fallback for wrong call in English already
					// or if someone writes an English command into an
					// other language script
					retone.append(starr[i]);
				}
			}
		}
		return retone.toString();
	}

	/**
	 * This method should split a GeoGebra script into the following format: ""
	 * or "something"; "command"; "something"; "command"; "something"; ...
	 * 
	 * @param st
	 *            String GeoGebra script
	 * @return String [] the GeoGebra script split into and array
	 */
	private static String[] splitScriptByCommands(final String st) {

		StringBuilder retone = new StringBuilder();
		final ArrayList<String> ret = new ArrayList<String>();

		// as the other algorithms would be too complicated,
		// just go from the end of the string and advance character by character

		// at first count the number of "s to decide how to start the algorithm
		int countapo = 0;
		for (int j = 0; j < st.length(); j++) {
			if (st.charAt(j) == '"') {
				countapo++;
			}
		}

		boolean in_string = false;
		if ((countapo % 2) == 1) {
			in_string = true;
		}

		boolean before_bracket = false;
		boolean just_before_bracket = false;
		for (int i = st.length() - 1; i >= 0; i--) {
			if (in_string) {
				if (st.charAt(i) == '"') {
					in_string = false;
				}
			} else if (just_before_bracket) {
				if (StringUtil.isLetterOrDigitOrUnderscore(st.charAt(i))) {
					ret.add(0, retone.toString());
					retone = new StringBuilder();
					just_before_bracket = false;
					before_bracket = true;
				} else if (!bracketAt(st,i) && (st.charAt(i) != ' ')) {
					just_before_bracket = false;
					before_bracket = false;
					if (st.charAt(i) == '"') {
						in_string = true;
					}
				}
			} else if (before_bracket) {
				if (!StringUtil.isLetterOrDigitOrUnderscore(st.charAt(i))) {
					ret.add(0, retone.toString());
					retone = new StringBuilder();
					before_bracket = false;
					if (st.charAt(i) == '"') {
						in_string = true;
					} else if (bracketAt(st,i)) {
						just_before_bracket = true;
					}
				}
			} else {
				if (st.charAt(i) == '"') {
					in_string = true;
				} else if (bracketAt(st,i)) {
					just_before_bracket = true;
				}
			}
			retone.insert(0, st.charAt(i));
		}
		ret.add(0, retone.toString());
		if (before_bracket) {
			ret.add(0, "");
		}
		final String[] ex = { "" };
		return ret.toArray(ex);
	}

	private static boolean bracketAt(String st, int i) {
		return (st.charAt(i) == '[') || (st.charAt(i) == '(');		
	}

	@Override
	public ScriptType getType() {
		return ScriptType.GGBSCRIPT;
	}

	@Override
	public Script copy() {
		return new GgbScript(app, text);
	}

	/**
	 * The text of this script is modified by changing every
	 * whole word oldLabel to newLabel.
	 * 
	 * @return whether any renaming happened
	 */
	public boolean renameGeo(String oldLabel, String newLabel) {
		if (oldLabel == null || "".equals(oldLabel) ||
			newLabel == null || "".equals(newLabel)) {
			return false;
		}
		ArrayList<String> work = StringUtil.wholeWordTokenize(text);
		boolean ret = false;
		for (int i = 1; i < work.size(); i += 2) {
			if (oldLabel.equals(work.get(i))) {
				if (i+1 < work.size() &&
					work.get(i+1) != null &&
					work.get(i+1).length() > 0) {

					if ("[".equals(work.get(i+1).charAt(0))) {
						// Now it's still possible that oldLabel
						// is used as a command name here,
						// so we have to rule out that possibility first.
						// Luckily, command names are always followed
						// by a [, as far as we know, so it is easy.
						continue;
					} else if ("\"".equals(work.get(i+1).charAt(0)) &&
						work.get(i-1) != null &&
						work.get(i-1).length() > 0 &&
						"\"".equals(work.get(i-1).charAt(0))) {
						// We also have to rule out the case when
						// the string is used as a "string"
						continue;
					}
				}
				// this is really something to be renamed!
				// ...or not? (pi, e, i, etc) TODO: check
				work.set(i, newLabel);
				ret = true;
			}
		}
		text = StringUtil.joinTokens(work, null);
		return ret;
	}
}
