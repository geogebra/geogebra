package com.himamis.retex.renderer.share.serialize;

/**
 * Transforms brackets in expression
 *
 */
public class ListSerializationAdapter extends DefaultSerializationAdapter {

	@Override
	public String transformBrackets(String left, String base, String right) {
		if ("[".equals(left) && base.contains("...")) {
			String[] parts = base.split(",");
			if (parts.length == 1) {
				parts = base.split("...");
			}
			if (parts.length == 1) {
				return "(" + parts[0] + ")";
			}
			if (parts.length == 4) {
				return "Sequence[" + parts[0] + "," + parts[3] + "," + parts[1]
						+ "-(" + parts[0] + ")]";
			}
			return "(" + parts[0] + "..." + parts[parts.length - 1] + ")";
		}
		return left + base + right;
	}

}
