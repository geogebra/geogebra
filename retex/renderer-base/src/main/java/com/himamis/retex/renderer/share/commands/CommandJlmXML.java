package com.himamis.retex.renderer.share.commands;

import java.util.Map;

import com.himamis.retex.renderer.share.TeXParser;

public class CommandJlmXML extends Command {

	@Override
	public boolean init(TeXParser tp) {
		final Map<String, String> map = tp.getXMLMap();
		String str = tp.getArgAsString();
		final StringBuffer buffer = new StringBuffer();
		int start = 0;
		int pos;
		while ((pos = str.indexOf("$")) != -1) {
			if (pos < str.length() - 1) {
				start = pos;
				while (++start < str.length()
						&& Character.isLetter(str.charAt(start)))
					;
				String key = str.substring(pos + 1, start);
				String value = map.get(key);
				if (value != null) {
					buffer.append(str.substring(0, pos));
					buffer.append(value);
				} else {
					buffer.append(str.substring(0, start));
				}
				str = str.substring(start);
			} else {
				buffer.append(str);
				str = "";
			}
		}
		buffer.append(str);
		str = buffer.toString();

		tp.addString(str);

		return false;
	}

}
