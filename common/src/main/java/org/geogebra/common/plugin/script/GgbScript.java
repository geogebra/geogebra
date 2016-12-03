package org.geogebra.common.plugin.script;

import java.util.ArrayList;

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.ScriptError;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.util.StringUtil;

/**
 * @author arno Script class for GgbScript scripts
 */
public class GgbScript extends Script {

	private AlgebraProcessor proc;

	/**
	 * @param app
	 *            the script's application
	 * @param scriptText
	 *            the script's source code
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
	public boolean run(Event evt) throws ScriptError {
		String scriptText;
		if (text == null) {
			return true;
		}
		if (evt.argument == null) {
			scriptText = text;
		} else {
			scriptText = text.replaceAll("%0", evt.argument);
		}
		String[] lines = scriptText.split("\n");
		boolean success = true;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.equals("") || line.charAt(0) == '#') {
				continue;
			}
			try {
				GeoElementND[] res = proc
						.processAlgebraCommandNoExceptionHandling(line, false,
						new ScriptErrorHandler(app, evt, i), false, null);
				success = success && res != null;
			} catch (Throwable e) {

			}
		}
		return success;
	}

	public static String script2LocalizedScript(App app, String st) {
		final String[] starr = splitScriptByCommands(st);
		boolean isAssignment = false;
		for (String string : starr) {
			if (string.equals(":=")) {
				isAssignment = true;
				break;
			}
		}
		final StringBuilder retone = new StringBuilder();
		for (int i = 0; i < starr.length; i++) {
			if ((i % 2) == 0 || isFunction(starr, i, app)) {
				retone.append(starr[i]);
				// app.getFunction("nroot")
			} else if (app.getParserFunctions().isFunction(starr[i])) {
				retone.append(app.getLocalization().getFunction(starr[i]));
			} else {
				// Log.debug("NOT FUNCTION" + starr[i]);
				// do not translate name of function
				// see #4391
				if (i == 1 && isAssignment) {
					retone.append(starr[i]);
				} else {
					retone.append(app.getLocalization().getCommand(starr[i]));
				}
			}
		}
		return retone.toString();
	}

	private static boolean isFunction(String[] starr, int i, App app) {
		if (i >= starr.length - 1 || starr[i + 1].startsWith("["))
			return false;
		if (app.getKernel().lookupLabel(starr[i]) != null)
			return true;
		return false;
	}

	/**
	 * Delocalize a script
	 * 
	 * @param app
	 *            the application
	 * @param st
	 *            the script text
	 * @return the text of the delocalized script
	 */
	public static String localizedScript2Script(App app, String st) {
		final String[] starr = splitScriptByCommands(st);
		boolean isAssignment = false;
		for (String string : starr) {
			if (string.equals(":=")) {
				isAssignment = true;
				break;
			}
		}
		final StringBuilder retone = new StringBuilder();
		for (int i = 0; i < starr.length; i++) {
			if ((i % 2) == 0) {
				retone.append(starr[i]);
			} else {
				// allow English language command in French scripts
				if (isFunction(starr, i, app)) {
					retone.append(starr[i]);
				}
				// do not translate function name
				// e.g. for German panel in Gerade(t)
				// do not translate Gerade to Line
				// needed for #4391
				else if (app.getInternalCommand(starr[i]) != null) {
					if (i == 1 && isAssignment) {
						retone.append(starr[i]);
					} else {
						retone.append(app.getInternalCommand(starr[i]));
					}
				} else if (app.getParserFunctions()
						.getInternal(app.getLocalization(), starr[i]) != null) {
					retone.append(app.getParserFunctions()
							.getInternal(app.getLocalization(),
							starr[i]));
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
		if (MyDouble.isOdd(countapo)) {
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
				} else if (!bracketAt(st, i) && (st.charAt(i) != ' ')) {
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
					} else if (bracketAt(st, i)) {
						just_before_bracket = true;
					}
				}
			} else {
				if (st.charAt(i) == '"') {
					in_string = true;
				} else if (bracketAt(st, i)) {
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
	 * The text of this script is modified by changing every whole word oldLabel
	 * to newLabel.
	 * 
	 * @return whether any renaming happened
	 */
	public boolean renameGeo(String oldLabel, String newLabel) {
		if (oldLabel == null || "".equals(oldLabel) || newLabel == null
				|| "".equals(newLabel)) {
			return false;
		}
		ArrayList<String> work = StringUtil.wholeWordTokenize(text);
		boolean ret = false;
		int numChars = 0, lengthChars;
		String forLength1, forLength2;
		for (int i = 1; i < work.size(); i += 2) {
			if (work.get(i - 1) != null) {
				// this is even, so will not be changed,
				// because only odd places are checked and replaced
				numChars += work.get(i - 1).length();
			}
			if (oldLabel.equals(work.get(i))) {
				// in theory, i+1 is always less than work.size(),
				// because it is an odd number, and in theory,
				// there is at least a non-null element there
				// but better to check...
				if (i + 1 < work.size() && work.get(i + 1) != null) {
					if ((work.get(i + 1).length() > 0)
							&& '[' == work.get(i + 1).charAt(0)) {
						// Now it's still possible that oldLabel
						// is used as a command name here,
						// so we have to rule out that possibility first.
						// Luckily, command names are always followed
						// by a [, as far as we know, so it is easy.
						numChars += oldLabel.length();
						continue;
					}
				}

				// We also have to rule out the case when
				// the string is used as a "string",
				// or used as e.g. "blabla !!* string ---+ ".
				// The easiest way to do that is to count the
				// number of " signs in the string until our
				// string, except the \" signs, and if it's odd,
				// then we're probably in a string.
				// For this computation, we use numChars.

				forLength1 = text.substring(0, numChars).replaceAll("\\\"", "");// String:
																				// /"
				forLength2 = forLength1.replaceAll("\"", "");// String: "
				lengthChars = forLength1.length() - forLength2.length();

				if (MyDouble.isOdd(lengthChars)) {
					// there is an odd number of living " signs before this
					// whole-word token, and this means that we're in a
					// string, so this whole-word token doesn't matter
					numChars += oldLabel.length();
					continue;
				}

				// If the string equals to "e" or "i",
				// and we get here, then they mean their
				// geo labels, but only after they get
				// them, and if they get them in the script,
				// then some original "e" or "i" constants
				// may be renamed as well... however, we
				// guess it's no problem as in the second
				// running of the script they should all mean
				// the geos, and it's the bug of the code elsewhere

				// numChars should be reassigned after it is used,
				// but still before "work" is redefined
				if (work.get(i) != null) {
					numChars += work.get(i).length();
				}
				// this is really something to be renamed!
				// ...or not? TODO: check
				work.set(i, newLabel);
				ret = true;
			} else if (work.get(i) != null) {
				numChars += work.get(i).length();
			}
		}
		text = StringUtil.joinTokens(work, null);
		return ret;
	}
}
