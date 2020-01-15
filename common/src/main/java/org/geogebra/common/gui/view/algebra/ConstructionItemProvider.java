package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.gui.inputfield.HasLastItem;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.util.StringUtil;

/**
 * Serializes construction items for keyboard input.
 */
final class ConstructionItemProvider implements HasLastItem {
	private final Construction cons;

	/**
	 * @param cons
	 *            construction
	 */
	ConstructionItemProvider(Construction cons) {
		this.cons = cons;
	}

	@Override
	public String getLastItem() {
		String text = cons.getLastCasEvaluableGeoElement()
				.toOutputValueString(StringTemplate.algebraTemplate);
		if (StringUtil.isSimpleNumber(text)) {
			return text;
		}
		return "(" + text + ")";
	}
}