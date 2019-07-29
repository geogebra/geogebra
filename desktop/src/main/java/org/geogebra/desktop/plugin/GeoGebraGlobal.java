package org.geogebra.desktop.plugin;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.plugin.GgbAPI;
import org.geogebra.common.util.debug.Log;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionCall;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/*
 * @author Joel Duffin
 */

public class GeoGebraGlobal implements IdFunctionCall {

	final App app;
	final Localization loc;

	GeoGebraGlobal(App app) {
		this.app = app;
		this.loc = app.getLocalization();
	}

	public static void init(App app, Scriptable scope, boolean sealed) {
		GeoGebraGlobal obj = new GeoGebraGlobal(app);

		for (int id = 1; id <= LAST_SCOPE_FUNCTION_ID; ++id) {
			String name;
			int arity = 1;
			switch (id) {
			case Id_alert:
				name = "alert";
				break;
			case Id_prompt:
				name = "prompt";
				break;
			case Id_setTimeout:
				name = "setTimeout";
				break;
			case Id_setInterval:
				name = "setInterval";
				break;
			case Id_clearTimeout:
				name = "clearTimeout";
				break;
			case Id_clearInterval:
				name = "clearInterval";
				break;
			default:
				throw Kit.codeBug();
			}
			IdFunctionObject f = new IdFunctionObject(obj, FTAG, id, name,
					arity, scope);
			if (sealed) {
				f.sealObject();
			}
			f.exportAsScopeProperty();
		}
	}

	@Override
	public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
			Scriptable thisObj, Object[] args) {
		if (f.hasTag(FTAG)) {
			int methodId = f.methodId();
			switch (methodId) {
			case Id_alert: {

				if (args.length > 1) {
					String error = argNumError(args.length,
							"alert( <String> )");
					app.showError(error);
					throw new Error(error);
				}

				String value = getElementAsString(args, 0);
				((GgbAPID) app.getGgbApi()).alert(value);

				return "";
			}
			case Id_prompt: {
				Object value0 = getElementAsString(args, 0);
				Object value1 = getElementAsString(args, 1);
				/*
				 * String s = (String)JOptionPane.showInputDialog(
				 * app.getFrame(), value0, "GeoGebra",
				 * JOptionPane.PLAIN_MESSAGE, null, null, value1);
				 */
				return ((GgbAPID) app.getGgbApi()).prompt(value0, value1);
			}
			case Id_clearInterval:
			case Id_clearTimeout:
			case Id_setInterval:
			case Id_setTimeout:
				Log.debug("ignored in desktop");
				return null;
			}
		}
		throw f.unknown();
	}

	private static String getElementAsString(Object[] args, int i) {
		Object value = args.length > i ? args[i] : "";
		if (value instanceof NativeJavaObject) {
			value = ((NativeJavaObject) value).unwrap();
		}
		return value.toString();
	}

	private StringBuilder sb;
	int[] linep = new int[1];

	private String argNumError(int argNumber, String syntax) {
		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}
		Context.getSourcePositionFromStack(linep); // line number of error
		sb.append(loc.getPlain("ErrorInJavaScriptAtLineA", linep[0] + ""));
		sb.append("\n");
		sb.append(Errors.IllegalArgumentNumber.getError(loc));
		sb.append(": ");
		sb.append(argNumber);
		sb.append("\n\n");
		sb.append(loc.getMenu("Syntax"));
		sb.append(":\n");
		sb.append(syntax);
		return sb.toString();
	}

	public static void initStandardObjects(App app, Scriptable scope,
			String arg, boolean sealed) {
		GgbAPI ggbApi = app.getGgbApi();
		Object wrappedOut = Context.javaToJS(ggbApi, scope);
		ScriptableObject.putProperty(scope, "ggbApplet", wrappedOut);

		if (arg != null) {
			Object wrappedArg = Context.javaToJS(arg, scope);
			ScriptableObject.putProperty(scope, "arg", wrappedArg);
		}

		// add geogebra methods as top level js methods
		init(app, scope, sealed);
	}

	private static final Object FTAG = "Global";

	private static final int Id_alert = 1, Id_prompt = 2, Id_setTimeout = 3,
			Id_setInterval = 4, Id_clearTimeout = 5, Id_clearInterval = 6,
			LAST_SCOPE_FUNCTION_ID = 6;
}
