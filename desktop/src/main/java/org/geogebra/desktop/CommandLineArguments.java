package org.geogebra.desktop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.geogebra.common.main.App;
import org.geogebra.desktop.main.AppD;

/**
 * Class to parse command line arguments. A list of possible arguments for
 * GeoGebra is available online at http://www.geogebra.org/wiki.
 * 
 * Arguments are accepted in the following format: --key1=value1 --key2=value2
 * ... file1 file2 ... filen
 * 
 * The last arguments have no "--key=" prefix and specifies the files to load.
 * The value of these arguments are stored with "file0", "file1", etc as the
 * keys.
 * 
 * If no value is specified (ie "--key=" or "--key") an empty string is regarded
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
		args = new HashMap<String, String>();

		if (cmdArgs == null)
			return;

		// loop through arguments
		for (int i = 0; i < cmdArgs.length; ++i) {
			// check if argument has the required "--" prefix
			if (cmdArgs[i].startsWith("--")) {
				int equalSignIndex = cmdArgs[i].lastIndexOf('=');

				if (equalSignIndex != -1) {
					args.put(cmdArgs[i].substring(2, equalSignIndex)
							.toLowerCase(Locale.US), cmdArgs[i]
							.substring(equalSignIndex + 1));
				} else {
					args.put(cmdArgs[i].substring(2).toLowerCase(Locale.US), "");
				}
			} else if (!cmdArgs[i].startsWith("-")) { // make sure we don't
														// process -open from eg
				// javaws -open "file1.ggb,file2.ggb"
				// http://jars.geogebra.org/webstart/4.2/jnlp/geogebra-42.jnlp
				// no -- or - prefix, therefore a filename

				if (cmdArgs[i].indexOf(',') > -1 && AppD.isWebstart()) {
					// process multiple files from eg
					// javaws -open "language=en,file1.ggb,file2.ggb"
					// http://jars.geogebra.org/webstart/4.2/jnlp/geogebra-42.jnlp
					String[] files = cmdArgs[i].split(",");
					for (int j = 0; j < files.length; j++) {
						if (files[j].indexOf('=') > -1) { // check for eg
															// language=de
							int equalSignIndex = files[j].lastIndexOf('=');

							if (equalSignIndex > -1) {
								args.put(files[j].substring(0, equalSignIndex),
										files[j].substring(equalSignIndex + 1));
							} else {
								args.put(files[j], "");
							}
						} else {
							addFile(files[j]);

						}
					}
				} else {
					cmdArgs[i] = cmdArgs[i].replaceAll("%20", " ");
					addFile(cmdArgs[i]);
				}
			} else {
				App.debug("unknown argument " + cmdArgs[i]);
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
		String strValue = args.get(name.toLowerCase(Locale.US));
		return (strValue == null ? "" : strValue);
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
		String strValue = args.get(name.toLowerCase(Locale.US));

		if (strValue == null || !isBoolean(name)) {
			return defaultValue;
		}
		return strValue.toLowerCase().equals("true");
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
		String strValue = args.get(name.toLowerCase(Locale.US));

		if (strValue == null) {
			return false;
		}
		strValue = strValue.toLowerCase();
		return strValue.equals("true") || strValue.equals("false");
	}

	/**
	 * Check if the arguments contain a certain key
	 * 
	 * @param name
	 *            the name of the key
	 * @return whether the args contain the key
	 */
	public boolean containsArg(String name) {
		return args.containsKey(name.toLowerCase(Locale.US));
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
		Iterator<String> it = args.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = args.get(key);
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
		Iterator<String> it = args.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!key.startsWith("file")) {
				String value = args.get(key);
				ret.args.put(key, value);
			}
		}
		return ret;
	}
}
