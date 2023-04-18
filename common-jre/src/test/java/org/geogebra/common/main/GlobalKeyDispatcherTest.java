package org.geogebra.common.main;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.EventAcumulator;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.KeyCodes;

public class GlobalKeyDispatcherTest extends BaseUnitTest {

	private GlobalKeyDispatcherHeadless dispatcher;

	@Before
	public void setupDispatcher() {
		this.dispatcher = new GlobalKeyDispatcherHeadless(getApp());
	}

	@Test
	public void moveWithArrowPoints() {
		GeoElement pt = add("(1,1)");
		GeoElement list = add("{(1,1)}");
		getApp().getSelectionManager().addSelectedGeo(pt);
		handleKey(KeyCodes.UP, Arrays.asList(pt, list));
		assertThat(pt, hasValue("(1, 1.1)"));
		assertThat(list, hasValue("{(1, 1.1)}"));
		list.setFixed(true);
		handleKey(KeyCodes.DOWN, Arrays.asList(pt, list));
		assertThat(pt, hasValue("(1, 1)"));
		assertThat(list, hasValue("{(1, 1.1)}"));
	}

	@Test
	public void moveWithArrowRandom() {
		getApp().setRandomSeed(42);
		List<GeoElement> geos = Arrays.asList(add("num=random()"),
			add("pt=(random(),random())"),
			add("norm=RandomNormal(0,1)"),
			add("list=Shuffle(1..50)"));
		List<String> oldVals = geos.stream()
				.map(g -> g.toValueString(StringTemplate.defaultTemplate))
				.collect(Collectors.toList());
		EventAcumulator listener = new EventAcumulator();
		getApp().getEventDispatcher().addEventListener(listener);
		handleKey(KeyCodes.LEFT, geos);
		// each update fired exactly once
		assertThat(listener.getEvents(), is(Arrays.asList("UPDATE num",
				"UPDATE pt", "UPDATE norm", "UPDATE list")));
		// values actually changed
		for (int i = 0; i < 4; i++) {
			assertThat(geos.get(i).toValueString(StringTemplate.defaultTemplate),
					not(oldVals.get(i)));
		}
	}

	private void handleKey(KeyCodes keyCodes, List<GeoElement> selection) {
		dispatcher.handleSelectedGeosKeys(
				keyCodes, selection,
				false, false, false, false);
	}
}
