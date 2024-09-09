package org.geogebra.desktop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Class to parse command line arguments. A list of possible arguments is included in
 * <a href="https://geogebra.github.io/docs/reference/en/Command_Line_Arguments/">the reference</a>.
 *
 * <p>Arguments are accepted in the following format: --key1=value1 --key2=value2
 * ... file1 file2 ... filen
 *
 * <p>The last arguments have no "--key=" prefix and specifies the files to load.
 * The value of these arguments are stored with "file0", "file1", etc as the
 * keys.
 *
 * <p>If no value is specified (ie "--key=" or "--key") an empty string is regarded
 * as value.
 */
public class CommandLineArguments {
	/**
	 * Hash map to store the options.
	 */
	private HashMap<String, String> args;
	private int noOfFiles = 0;
	private int noOfTools;

	/**
	 * Parse the argument array created by Java.
	 * 
	 * @param cmdArgs
	 *            arguments
	 */
	public CommandLineArguments(String[] cmdArgs) {
		args = new HashMap<>();

		if (cmdArgs == null) {
			return;
		}

		// loop through arguments
		for (int i = 0; i < cmdArgs.length; ++i) {
			// check if argument has the required "--" prefix
			if (cmdArgs[i].startsWith("--")) {
				int equalSignIndex = cmdArgs[i].lastIndexOf('=');

				if (equalSignIndex != -1) {
					args.put(
							StringUtil.toLowerCaseUS(
									cmdArgs[i].substring(2, equalSignIndex)),
							cmdArgs[i].substring(equalSignIndex + 1));
				} else {
					args.put(StringUtil.toLowerCaseUS(cmdArgs[i].substring(2)),
							"");
				}
			} else if (!cmdArgs[i].startsWith("-")) { // make sure we don't
														// process -open from eg
				// javaws -open "file1.ggb,file2.ggb"
				// http://jars.geogebra.org/webstart/4.2/jnlp/geogebra-42.jnlp
				// no -- or - prefix, therefore a filename

				cmdArgs[i] = cmdArgs[i].replaceAll("%20", " ");
				addFile(cmdArgs[i]);

			} else {
				Log.debug("unknown argument " + cmdArgs[i]);
			}
		}

	}

	/*
	 * private CommandLineArguments put(String key, String value) {
	 * args.put(key, value); return args; }
	 */

	private void addFile(String string) {
		String filename = string.replaceAll("%20", " ");
		args.put("file" + (noOfFiles++), filename);
		if (filename.endsWith(".ggt")) {
			noOfTools++;
		}
	}

	/**
	 * returns number of files, eg geogebra.jar file1.ggb file2.ggb will return
	 * 2
	 * 
	 * @return the number of files
	 */
	public int getNoOfFiles() {
		return noOfFiles;
	}

	public int getNoOfTools() {
		return noOfTools;
	}

	/**
	 * Returns the string value of the requested argument.
	 * 
	 * @param name
	 *            argument name
	 * @return The string value of the specified argument (or empty string)
	 */
	public String getStringValue(String name) {
		String strValue = args.get(StringUtil.toLowerCaseUS(name));
		return strValue == null ? "" : strValue;
	}

	/**
	 * Returns the boolean value of the requested argument.
	 * 
	 * @param name
	 *            the argument
	 * @param defaultValue
	 *            default value if not defined
	 * @return The boolean value or "default" in case this argument is missing
	 *         or has an invalid format.
	 */
	public boolean getBooleanValue(String name, boolean defaultValue) {
		String strValue = args.get(StringUtil.toLowerCaseUS(name));

		if (strValue == null || !isBoolean(name)) {
			return defaultValue;
		}
		return strValue.equalsIgnoreCase("true");
	}

	/**
	 * Check if the requested argument is a boolean ie the value is "true" or
	 * "false" (lettercase ignored).
	 * 
	 * @param name
	 *            the argument
	 * @return true for valid booleans
	 */
	public boolean isBoolean(String name) {
		String strValue = args.get(StringUtil.toLowerCaseUS(name));

		if (strValue == null) {
			return false;
		}
		strValue = strValue.toLowerCase();
		return "true".equals(strValue) || "false".equals(strValue);
	}

	/**
	 * Check if the arguments contain a certain key
	 * 
	 * @param name
	 *            the name of the key
	 * @return whether the args contain the key
	 */
	public boolean containsArg(String name) {
		return args.containsKey(StringUtil.toLowerCaseUS(name));
	}

	/**
	 * Adds a new key/value pair into the command line arguments.
	 * 
	 * @param newKey
	 *            the new key
	 * @param newValue
	 *            the new value
	 * @return the new command line arguments
	 */
	public CommandLineArguments add(String newKey, String newValue) {
		CommandLineArguments ret = new CommandLineArguments(null);
		Iterator<Entry<String, String>> it = args.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			ret.args.put(key, value);
		}
		ret.args.put(newKey, newValue);
		if (newKey.startsWith("file")) {
			++(ret.noOfFiles);
		}
		if (newValue.endsWith(".ggt")) {
			++(ret.noOfTools);
		}

		return ret;
	}

	/**
	 * Removes non-global arguments from the command line arguments.
	 * 
	 * @return the global arguments
	 * 
	 */
	public CommandLineArguments getGlobalArguments() {
		CommandLineArguments ret = new CommandLineArguments(null);
		Iterator<Entry<String, String>> it = args.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			if (!key.startsWith("file")) {
				String value = entry.getValue();
				ret.args.put(key, value);
			}
		}
		return ret;
	}
}
