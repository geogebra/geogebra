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
				proc.processAlgebraCommandNoExceptionHandling(line, false, false, true);
			} catch (Throwable e) {
				throw new ScriptError( 
					app.getPlain(
						"ErrorInScriptAtLineAFromObjectB",
						(i + 1) + "",
						evt.target.getLabel(StringTemplate.defaultTemplate)
					) 
					+ "\n" + e.getLocalizedMessage()
				);
			}
		}
	}

	private static String script2LocalizedScript(App app, String st) {
		final String[] starr = splitScriptByCommands(st);
		final StringBuilder retone = new StringBuilder();
		for (int i = 0; i < starr.length; i++) {
			if ((i % 2) == 0 || isFunction(starr,i,app)) {
				retone.append(starr[i]);
			} else {
				retone.append(app.getCommand(starr[i]));
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

}
