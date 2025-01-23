package org.geogebra.keyboard;

import static org.geogebra.test.OrderingComparison.greaterThanOrEqualTo;
import static org.geogebra.test.OrderingComparison.lessThanOrEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.keyboard.KeyboardRowDefinitionProvider;
import org.geogebra.common.util.lang.Language;
import org.geogebra.keyboard.base.KeyboardFactory;
import org.geogebra.keyboard.base.impl.DefaultKeyboardFactory;
import org.geogebra.keyboard.base.impl.TemplateKeyProvider;
import org.geogebra.keyboard.base.model.KeyboardModel;
import org.geogebra.keyboard.base.model.Row;
import org.geogebra.keyboard.base.model.WeightedButton;
import org.junit.Test;

public class KeyboardLayoutTest {
	@Test
	public void testSpecialTab() {
		KeyboardFactory kbf = new DefaultKeyboardFactory();
		KeyboardModel kb = kbf.createSpecialSymbolsKeyboard().getModel();
		StringBuilder actions = new StringBuilder();
		StringBuilder resources = new StringBuilder();
		for (Row row : kb.getRows()) {
			for (WeightedButton button : row.getButtons()) {
				resources.append(button.getResourceName()).append(",");
				actions.append(button.getPrimaryActionName()).append(",");
			}
		}

		assertEquals("∞,≟,≠,∧,∨,→,¬,⊗,∥,⟂,∈,⊂,⊆,∠,FLOOR,CEIL,[,],:,&,@,#,"
						+ "Translate.currency,BACKSPACE_DELETE,;,',\",′,"
						+ "″,LEFT_ARROW,RIGHT_ARROW,RETURN_ENTER,",
				resources.toString());
		assertEquals("∞,≟,≠,∧,∨,→,¬,⊗,∥,⟂,∈,⊂,⊆,∠,⌊,⌈,[,],:,&,@,#,Translate.currency,"
						+ "BACKSPACE_DELETE,;,',\",′,″,LEFT_CURSOR,RIGHT_CURSOR,RETURN_ENTER,",
				actions.toString());
	}

	@Test
	public void testSpecialTabWithTemplateButtons() {
		KeyboardFactory kbf = new DefaultKeyboardFactory(new TemplateKeyProvider() {
			@Override
			public String getPointFunction() {
				return "$point:2";
			}
		});
		KeyboardModel kb = kbf.createSpecialSymbolsKeyboard().getModel();
		StringBuilder actions = new StringBuilder();
		StringBuilder resources = new StringBuilder();
		for (Row row : kb.getRows()) {
			for (WeightedButton button : row.getButtons()) {
				resources.append(button.getResourceName()).append(",");
				actions.append(button.getPrimaryActionName()).append(",");
			}
		}

		assertEquals("∞,≟,≠,∧,∨,¬,⊗,[,],∥,⟂,∈,⊂,⊆,∠,→,CEIL,FLOOR,"
						+ "POINT_TEMPLATE,VECTOR_TEMPLATE,MATRIX_TEMPLATE,\\,&,@,#,"
						+ "Translate.currency,BACKSPACE_DELETE,;,:,',\",′,"
						+ "″,LEFT_ARROW,RIGHT_ARROW,RETURN_ENTER,",
				resources.toString()); // TODO fix test
		assertEquals("∞,≟,≠,∧,∨,¬,⊗,[,],∥,⟂,∈,⊂,⊆,∠,→,⌈,⌊,"
						+ "$point:2,vector,matrix,\\,&,@,#,"
						+ "Translate.currency,BACKSPACE_DELETE,;,:,',\",′,"
						+ "″,LEFT_CURSOR,RIGHT_CURSOR,RETURN_ENTER,",
				actions.toString());
	}

	@Test
	public void letterTabTest() {
		LocalizationCommon localization =
				(LocalizationCommon) AppCommonFactory.create().getLocalization();
		KeyboardRowDefinitionProvider latinProvider = new KeyboardRowDefinitionProvider(
				localization);
		for (Language lang : Language.values()) {
			localization.setLocale(localization.convertToLocale(lang));
			String[] rows = latinProvider.getLowerKeys();
			List<Integer> lengths = Arrays.stream(rows).map(String::length)
					.collect(Collectors.toList());
			switch (lang.toLanguageTag()) {
			case "ne":
				assertThat(lengths, is(Arrays.asList(18, 16, 14)));
				break;
			case "ta":
				assertThat(lengths, is(Arrays.asList(15, 13, 8)));
				break;
			case "si":
				assertThat(lengths, is(Arrays.asList(12, 14, 10)));
				break;
			default:
				String loc = " (" + lang.toLanguageTag() + ")";
				// 13 used for Icelandic, Thai and Yiddish
				assertThat(rows[0] + loc, lengths.get(0), lessThanOrEqualTo(13));
				assertThat(rows[1] + loc, lengths.get(1), lessThanOrEqualTo(lengths.get(0)));
				assertThat(rows[2] + loc, lengths.get(2), lessThanOrEqualTo(lengths.get(0)));
				assertThat(rows[2] + loc, lengths.get(2), greaterThanOrEqualTo(7));
				break;
			}
		}
	}
}
