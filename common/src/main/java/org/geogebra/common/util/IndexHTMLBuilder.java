package org.geogebra.common.util;

/**
 * Builds HTML code of string with indices.
 */
public class IndexHTMLBuilder {
	private StringBuilder sb;
	private boolean needsTag;

	/**
	 * @param addTag
	 *            whether to add &lt;html&gt; tag around
	 */
	public IndexHTMLBuilder(boolean addTag) {
		this.sb = new StringBuilder();
		if (addTag) {
			this.needsTag = true;
			sb.append("<html>");
		}
	}

	public void append(String s) {
		sb.append(s);
	}

	public void startIndex() {
		sb.append("<sub><font size=\"-1\">");
	}

	public void endIndex() {
		sb.append("</font></sub>");
	}

	@Override
	public String toString() {

		if (needsTag) {
			needsTag = false;
			sb.append("</html>");
		}
		return sb.toString();
	}

	public void clear() {
		sb.setLength(needsTag ? "<html>".length() : 0);
	}

	public boolean canAppendRawHtml() {
		return true;
	}

	public void appendHTML(String str) {
		sb.append(StringUtil.toHTMLString(str));
	}

	/**
	 * @param str
	 *            string with indices
	 */
	public final void indicesToHTML(final String str) {

		clear();
		if (str == null) {
			return;
		}
		int depth = 0;
		int startPos = 0;
		final int length = str.length();
		for (int i = 0; i < length; i++) {
			switch (str.charAt(i)) {
			case '_':
				// write everything before _
				if (i > startPos) {
					appendHTML(str.substring(startPos, i));
				}
				startPos = i + 1;
				depth++;

				// check if next character is a '{' (beginning of index with
				// several chars)
				if ((startPos < length) && (str.charAt(startPos) != '{')) {
					startIndex();
					appendHTML(str.substring(startPos, startPos + 1));
					endIndex();
					depth--;
				} else {
					startIndex();
				}
				i++;
				startPos++;
				break;

			case '}':
				if (depth > 0) {
					if (i > startPos) {
						appendHTML(str.substring(startPos, i));
					}
					endIndex();
					startPos = i + 1;
					depth--;
				}
				break;
			default:
				//
				break;
			}
		}

		if (startPos < length) {
			appendHTML(str.substring(startPos));
		}
	}

	/**
	 * Converts indices to HTML &lt;sub&gt; tags if necessary.
	 * 
	 * @param text
	 *            GGB string
	 * @param builder
	 *            indexed HTML builder
	 */
	public static void convertIndicesToHTML(final String text,
			IndexHTMLBuilder builder) {
		// check for index
		if (text.indexOf('_') > -1) {
			builder.indicesToHTML(text);
			return;
		}
		builder.clear();
		builder.appendHTML(text);
	}
}
