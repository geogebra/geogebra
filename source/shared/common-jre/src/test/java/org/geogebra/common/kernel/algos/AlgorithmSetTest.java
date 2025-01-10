package org.geogebra.common.kernel.algos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Iterator;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AlgorithmSetTest extends BaseUnitTest {

	private AlgorithmSet algoSet;

	@Before
	public void setUp() {
		algoSet = new AlgorithmSet();
	}

	@Test
	public void testAdd() {
		AlgoElement element = createAlgoElement(0);
		algoSet.add(element);
		assertThat(algoSet.contains(element), is(true));
		assertThat(algoSet.getSize(), is(1));
		assertThat(algoSet.isEmpty(), is(false));
	}

	@Test
	public void testAddAllSorted() {
		AlgoElement first = createAlgoElement(1);
		AlgoElement second = createAlgoElement(20);
		AlgoElement third = createAlgoElement(30);
		AlgoElement fourth = createAlgoElement(25);
		AlgorithmSet set = new AlgorithmSet();
		set.add(third);
		set.add(second);
		set.add(first);
		set.add(fourth);

		algoSet.addAllSorted(set);

		Iterator<AlgoElement> iterator = algoSet.iterator();
		assertThat(iterator.hasNext(), is(true));
		AlgoElement element = iterator.next();
		assertThat(element.getID(), is(1L));

		assertThat(iterator.hasNext(), is(true));
		element = iterator.next();
		assertThat(element.getID(), is(20L));

		assertThat(iterator.hasNext(), is(true));
		element = iterator.next();
		assertThat(element.getID(), is(25L));

		assertThat(iterator.hasNext(), is(true));
		element = iterator.next();
		assertThat(element.getID(), is(30L));
	}

	private AlgoElement createAlgoElement(long id) {
		AlgoElement element = Mockito.mock(AlgoElement.class);
		Mockito.when(element.getID()).thenReturn(id);
		return element;
	}
}
