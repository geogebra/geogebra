package org.geogebra.common.kernel.interval;

import static java.lang.Math.PI;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.geogebra.common.kernel.interval.IntervalTest.uninvertedInterval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TanSamplerTest extends SamplerTest {
	@Test
	public void tanX() {
		IntervalTupleList tuples = functionValuesWithSampleCount("tan(x)",
				0, PI, -8, 8, 100);
		assertEquals(interval(-4.9E-324, 0.03142626604335116), tuples.valueAt(0));
		assertEquals(interval(0.03142626604335114, 0.06291466725364978), tuples.valueAt(1));
		assertEquals(interval(0.06291466725364975, 0.09452783117928207), tuples.valueAt(2));
		assertEquals(interval(0.09452783117928204, 0.1263293784461082), tuples.valueAt(3));
		assertEquals(interval(0.12632937844610814, 0.1583844403245363), tuples.valueAt(4));
		assertEquals(interval(0.15838444032453625, 0.19076020221856677), tuples.valueAt(5));
		assertEquals(interval(0.1907602022185667, 0.2235264828971491), tuples.valueAt(6));
		assertEquals(interval(0.22352648289714905, 0.2567563603677268), tuples.valueAt(7));
		assertEquals(interval(0.2567563603677267, 0.29052685673191647), tuples.valueAt(8));
		assertEquals(interval(0.29052685673191636, 0.3249196962329063), tuples.valueAt(9));
		assertEquals(interval(0.3249196962329062, 0.3600221530957566), tuples.valueAt(10));
		assertEquals(interval(0.36002215309575647, 0.3959280087977212), tuples.valueAt(11));
		assertEquals(interval(0.3959280087977211, 0.43273864224742586), tuples.valueAt(12));
		assertEquals(interval(0.43273864224742575, 0.4705642812122514), tuples.valueAt(13));
		assertEquals(interval(0.4705642812122513, 0.5095254494944288), tuples.valueAt(14));
		assertEquals(interval(0.5095254494944286, 0.5497546521927701), tuples.valueAt(15));
		assertEquals(interval(0.5497546521927699, 0.5913983513994712), tuples.valueAt(16));
		assertEquals(interval(0.591398351399471, 0.6346192975441483), tuples.valueAt(17));
		assertEquals(interval(0.634619297544148, 0.6795992982245267), tuples.valueAt(18));
		assertEquals(interval(0.6795992982245265, 0.7265425280053611), tuples.valueAt(19));
		assertEquals(interval(0.7265425280053609, 0.7756795110496134), tuples.valueAt(20));
		assertEquals(interval(0.7756795110496132, 0.827271945972476), tuples.valueAt(21));
		assertEquals(interval(0.8272719459724758, 0.8816185923631896), tuples.valueAt(22));
		assertEquals(interval(0.8816185923631894, 0.939062505817493), tuples.valueAt(23));
		assertEquals(interval(0.9390625058174927, 1.0000000000000009), tuples.valueAt(24));
		assertEquals(interval(1.0000000000000004, 1.064891840324793), tuples.valueAt(25));
		assertEquals(interval(1.0648918403247924, 1.1342773492554066), tuples.valueAt(26));
		assertEquals(interval(1.1342773492554061, 1.2087923504096103), tuples.valueAt(27));
		assertEquals(interval(1.2087923504096099, 1.2891922317850681), tuples.valueAt(28));
		assertEquals(interval(1.2891922317850677, 1.3763819204711751), tuples.valueAt(29));
		assertEquals(interval(1.3763819204711747, 1.471455315819971), tuples.valueAt(30));
		assertEquals(interval(1.4714553158199706, 1.575747859968653), tuples.valueAt(31));
		assertEquals(interval(1.5757478599686525, 1.6909076557850136), tuples.valueAt(32));
		assertEquals(interval(1.6909076557850131, 1.818993247281069), tuples.valueAt(33));
		assertEquals(interval(1.8189932472810686, 1.962610505505154), tuples.valueAt(34));
		assertEquals(interval(1.9626105055051535, 2.125108173157207), tuples.valueAt(35));
		assertEquals(interval(2.125108173157206, 2.3108636538824157), tuples.valueAt(36));
		assertEquals(interval(2.3108636538824148, 2.525711689447311), tuples.valueAt(37));
		assertEquals(interval(2.52571168944731, 2.7776068539149823), tuples.valueAt(38));
		assertEquals(interval(2.7776068539149814, 3.0776835371752624), tuples.valueAt(39));
		assertEquals(interval(3.0776835371752616, 3.4420225766692303), tuples.valueAt(40));
		assertEquals(interval(3.4420225766692294, 3.894742854929875), tuples.valueAt(41));
		assertEquals(interval(3.894742854929874, 4.4737428292115755), tuples.valueAt(42));
		assertEquals(interval(4.473742829211574, 5.242183581113206), tuples.valueAt(43));
		assertEquals(interval(5.2421835811132045, 6.313751514675087), tuples.valueAt(44));
		assertEquals(interval(6.3137515146750856, 7.915815088305898), tuples.valueAt(45));
		assertEquals(interval(7.915815088305896, 10.578894993405767), tuples.valueAt(46));
		assertEquals(interval(10.578894993405763, 15.894544843865607), tuples.valueAt(47));
		assertEquals(interval(15.894544843865603, 31.820515953775207), tuples.valueAt(48));
		assertEquals(invertedInterval(-1.0E-4, 1.0E-4), tuples.valueAt(49));
		assertEquals(interval(-7.176175255700845E14, -31.8205159537725), tuples.valueAt(50));
		assertEquals(interval(-31.820515953772507, -15.894544843864926), tuples.valueAt(51));
		assertEquals(interval(-15.89454484386493, -10.578894993405461), tuples.valueAt(52));
		assertEquals(interval(-10.578894993405465, -7.9158150883057266), tuples.valueAt(53));
		assertEquals(interval(-7.915815088305728, -6.313751514674976), tuples.valueAt(54));
		assertEquals(interval(-6.313751514674978, -5.242183581113129), tuples.valueAt(55));
		assertEquals(interval(-5.242183581113131, -4.473742829211518), tuples.valueAt(56));
		assertEquals(interval(-4.47374282921152, -3.894742854929831), tuples.valueAt(57));
		assertEquals(interval(-3.8947428549298317, -3.442022576669195), tuples.valueAt(58));
		assertEquals(interval(-3.442022576669196, -3.077683537175234), tuples.valueAt(59));
		assertEquals(interval(-3.077683537175235, -2.7776068539149583), tuples.valueAt(60));
		assertEquals(interval(-2.777606853914959, -2.5257116894472906), tuples.valueAt(61));
		assertEquals(interval(-2.5257116894472915, -2.310863653882398), tuples.valueAt(62));
		assertEquals(interval(-2.310863653882399, -2.1251081731571912), tuples.valueAt(63));
		assertEquals(interval(-2.125108173157192, -1.9626105055051415), tuples.valueAt(64));
		assertEquals(interval(-1.962610505505142, -1.818993247281059), tuples.valueAt(65));
		assertEquals(interval(-1.8189932472810595, -1.6909076557850053), tuples.valueAt(66));
		assertEquals(interval(-1.6909076557850058, -1.5757478599686463), tuples.valueAt(67));
		assertEquals(interval(-1.5757478599686467, -1.4714553158199652), tuples.valueAt(68));
		assertEquals(interval(-1.4714553158199657, -1.3763819204711707), tuples.valueAt(69));
		assertEquals(interval(-1.3763819204711711, -1.2891922317850644), tuples.valueAt(70));
		assertEquals(interval(-1.2891922317850648, -1.2087923504096076), tuples.valueAt(71));
		assertEquals(interval(-1.208792350409608, -1.1342773492554044), tuples.valueAt(72));
		assertEquals(interval(-1.1342773492554048, -1.0648918403247911), tuples.valueAt(73));
		assertEquals(interval(-1.0648918403247916, -0.9999999999999998), tuples.valueAt(74));
		assertEquals(interval(-1.0, -0.9390625058174925), tuples.valueAt(75));
		assertEquals(interval(-0.9390625058174927, -0.8816185923631896), tuples.valueAt(76));
		assertEquals(interval(-0.8816185923631898, -0.8272719459724763), tuples.valueAt(77));
		assertEquals(interval(-0.8272719459724766, -0.7756795110496141), tuples.valueAt(78));
		assertEquals(interval(-0.7756795110496143, -0.7265425280053621), tuples.valueAt(79));
		assertEquals(interval(-0.7265425280053623, -0.6795992982245279), tuples.valueAt(80));
		assertEquals(interval(-0.6795992982245281, -0.6346192975441497), tuples.valueAt(81));
		assertEquals(interval(-0.6346192975441499, -0.5913983513994728), tuples.valueAt(82));
		assertEquals(interval(-0.591398351399473, -0.549754652192772), tuples.valueAt(83));
		assertEquals(interval(-0.5497546521927722, -0.5095254494944309), tuples.valueAt(84));
		assertEquals(interval(-0.5095254494944311, -0.4705642812122538), tuples.valueAt(85));
		assertEquals(interval(-0.4705642812122539, -0.43273864224742836), tuples.valueAt(86));
		assertEquals(interval(-0.4327386422474285, -0.3959280087977239), tuples.valueAt(87));
		assertEquals(interval(-0.395928008797724, -0.36002215309575936), tuples.valueAt(88));
		assertEquals(interval(-0.36002215309575947, -0.3249196962329092), tuples.valueAt(89));
		assertEquals(interval(-0.32491969623290934, -0.29052685673191947), tuples.valueAt(90));
		assertEquals(interval(-0.2905268567319196, -0.2567563603677299), tuples.valueAt(91));
		assertEquals(interval(-0.25675636036773003, -0.22352648289715243), tuples.valueAt(92));
		assertEquals(interval(-0.2235264828971525, -0.1907602022185702), tuples.valueAt(93));
		assertEquals(interval(-0.19076020221857026, -0.1583844403245399), tuples.valueAt(94));
		assertEquals(interval(-0.15838444032453997, -0.12632937844611195), tuples.valueAt(95));
		assertEquals(interval(-0.126329378446112, -0.09452783117928597), tuples.valueAt(96));
		assertEquals(interval(-0.094527831179286, -0.06291466725365384), tuples.valueAt(97));
		assertEquals(interval(-0.06291466725365387, -0.031426266043355404), tuples.valueAt(98));
		assertEquals(interval(-0.03142626604335542, -4.440892098500625E-15), tuples.valueAt(99));
	}

	@Test
	public void tanXHiRes() {
		for (IntervalTuple tuple: hiResFunction("tan(x)")) {
			assertEquals(IntervalConstants.whole().invert(), tuple.y());
		}
	}

	@Test
	public void minusTanX() {
		IntervalTupleList tuples = functionValuesWithSampleCount("-tan(x)",
				0, PI, -8, 8, 100);
		assertEquals(interval(-0.03142626604335116, 4.9E-324), tuples.valueAt(0));
		assertEquals(interval(-0.06291466725364978, -0.03142626604335114), tuples.valueAt(1));
		assertEquals(interval(-0.09452783117928207, -0.06291466725364975), tuples.valueAt(2));
		assertEquals(interval(-0.1263293784461082, -0.09452783117928204), tuples.valueAt(3));
		assertEquals(interval(-0.1583844403245363, -0.12632937844610814), tuples.valueAt(4));
		assertEquals(interval(-0.19076020221856677, -0.15838444032453625), tuples.valueAt(5));
		assertEquals(interval(-0.2235264828971491, -0.1907602022185667), tuples.valueAt(6));
		assertEquals(interval(-0.2567563603677268, -0.22352648289714905), tuples.valueAt(7));
		assertEquals(interval(-0.29052685673191647, -0.2567563603677267), tuples.valueAt(8));
		assertEquals(interval(-0.3249196962329063, -0.29052685673191636), tuples.valueAt(9));
		assertEquals(interval(-0.3600221530957566, -0.3249196962329062), tuples.valueAt(10));
		assertEquals(interval(-0.3959280087977212, -0.36002215309575647), tuples.valueAt(11));
		assertEquals(interval(-0.43273864224742586, -0.3959280087977211), tuples.valueAt(12));
		assertEquals(interval(-0.4705642812122514, -0.43273864224742575), tuples.valueAt(13));
		assertEquals(interval(-0.5095254494944288, -0.4705642812122513), tuples.valueAt(14));
		assertEquals(interval(-0.5497546521927701, -0.5095254494944286), tuples.valueAt(15));
		assertEquals(interval(-0.5913983513994712, -0.5497546521927699), tuples.valueAt(16));
		assertEquals(interval(-0.6346192975441483, -0.591398351399471), tuples.valueAt(17));
		assertEquals(interval(-0.6795992982245267, -0.634619297544148), tuples.valueAt(18));
		assertEquals(interval(-0.7265425280053611, -0.6795992982245265), tuples.valueAt(19));
		assertEquals(interval(-0.7756795110496134, -0.7265425280053609), tuples.valueAt(20));
		assertEquals(interval(-0.827271945972476, -0.7756795110496132), tuples.valueAt(21));
		assertEquals(interval(-0.8816185923631896, -0.8272719459724758), tuples.valueAt(22));
		assertEquals(interval(-0.939062505817493, -0.8816185923631894), tuples.valueAt(23));
		assertEquals(interval(-1.0000000000000009, -0.9390625058174927), tuples.valueAt(24));
		assertEquals(interval(-1.064891840324793, -1.0000000000000004), tuples.valueAt(25));
		assertEquals(interval(-1.1342773492554066, -1.0648918403247924), tuples.valueAt(26));
		assertEquals(interval(-1.2087923504096103, -1.1342773492554061), tuples.valueAt(27));
		assertEquals(interval(-1.2891922317850681, -1.2087923504096099), tuples.valueAt(28));
		assertEquals(interval(-1.3763819204711751, -1.2891922317850677), tuples.valueAt(29));
		assertEquals(interval(-1.471455315819971, -1.3763819204711747), tuples.valueAt(30));
		assertEquals(interval(-1.575747859968653, -1.4714553158199706), tuples.valueAt(31));
		assertEquals(interval(-1.6909076557850136, -1.5757478599686525), tuples.valueAt(32));
		assertEquals(interval(-1.818993247281069, -1.6909076557850131), tuples.valueAt(33));
		assertEquals(interval(-1.962610505505154, -1.8189932472810686), tuples.valueAt(34));
		assertEquals(interval(-2.125108173157207, -1.9626105055051535), tuples.valueAt(35));
		assertEquals(interval(-2.3108636538824157, -2.125108173157206), tuples.valueAt(36));
		assertEquals(interval(-2.525711689447311, -2.3108636538824148), tuples.valueAt(37));
		assertEquals(interval(-2.7776068539149823, -2.52571168944731), tuples.valueAt(38));
		assertEquals(interval(-3.0776835371752624, -2.7776068539149814), tuples.valueAt(39));
		assertEquals(interval(-3.4420225766692303, -3.0776835371752616), tuples.valueAt(40));
		assertEquals(interval(-3.894742854929875, -3.4420225766692294), tuples.valueAt(41));
		assertEquals(interval(-4.4737428292115755, -3.894742854929874), tuples.valueAt(42));
		assertEquals(interval(-5.242183581113206, -4.473742829211574), tuples.valueAt(43));
		assertEquals(interval(-6.313751514675087, -5.2421835811132045), tuples.valueAt(44));
		assertEquals(interval(-7.915815088305898, -6.3137515146750856), tuples.valueAt(45));
		assertEquals(interval(-10.578894993405767, -7.915815088305896), tuples.valueAt(46));
		assertEquals(interval(-15.894544843865607, -10.578894993405763), tuples.valueAt(47));
		assertEquals(interval(-31.820515953775207, -15.894544843865603), tuples.valueAt(48));
		assertEquals(invertedInterval(-1.0E-4, 1.0E-4), tuples.valueAt(49));
		assertEquals(interval(31.8205159537725, 7.176175255700845E14), tuples.valueAt(50));
		assertEquals(interval(15.894544843864926, 31.820515953772507), tuples.valueAt(51));
		assertEquals(interval(10.578894993405461, 15.89454484386493), tuples.valueAt(52));
		assertEquals(interval(7.9158150883057266, 10.578894993405465), tuples.valueAt(53));
		assertEquals(interval(6.313751514674976, 7.915815088305728), tuples.valueAt(54));
		assertEquals(interval(5.242183581113129, 6.313751514674978), tuples.valueAt(55));
		assertEquals(interval(4.473742829211518, 5.242183581113131), tuples.valueAt(56));
		assertEquals(interval(3.894742854929831, 4.47374282921152), tuples.valueAt(57));
		assertEquals(interval(3.442022576669195, 3.8947428549298317), tuples.valueAt(58));
		assertEquals(interval(3.077683537175234, 3.442022576669196), tuples.valueAt(59));
		assertEquals(interval(2.7776068539149583, 3.077683537175235), tuples.valueAt(60));
		assertEquals(interval(2.5257116894472906, 2.777606853914959), tuples.valueAt(61));
		assertEquals(interval(2.310863653882398, 2.5257116894472915), tuples.valueAt(62));
		assertEquals(interval(2.1251081731571912, 2.310863653882399), tuples.valueAt(63));
		assertEquals(interval(1.9626105055051415, 2.125108173157192), tuples.valueAt(64));
		assertEquals(interval(1.818993247281059, 1.962610505505142), tuples.valueAt(65));
		assertEquals(interval(1.6909076557850053, 1.8189932472810595), tuples.valueAt(66));
		assertEquals(interval(1.5757478599686463, 1.6909076557850058), tuples.valueAt(67));
		assertEquals(interval(1.4714553158199652, 1.5757478599686467), tuples.valueAt(68));
		assertEquals(interval(1.3763819204711707, 1.4714553158199657), tuples.valueAt(69));
		assertEquals(interval(1.2891922317850644, 1.3763819204711711), tuples.valueAt(70));
		assertEquals(interval(1.2087923504096076, 1.2891922317850648), tuples.valueAt(71));
		assertEquals(interval(1.1342773492554044, 1.208792350409608), tuples.valueAt(72));
		assertEquals(interval(1.0648918403247911, 1.1342773492554048), tuples.valueAt(73));
		assertEquals(interval(0.9999999999999998, 1.0648918403247916), tuples.valueAt(74));
		assertEquals(interval(0.9390625058174925, 1.0), tuples.valueAt(75));
		assertEquals(interval(0.8816185923631896, 0.9390625058174927), tuples.valueAt(76));
		assertEquals(interval(0.8272719459724763, 0.8816185923631898), tuples.valueAt(77));
		assertEquals(interval(0.7756795110496141, 0.8272719459724766), tuples.valueAt(78));
		assertEquals(interval(0.7265425280053621, 0.7756795110496143), tuples.valueAt(79));
		assertEquals(interval(0.6795992982245279, 0.7265425280053623), tuples.valueAt(80));
		assertEquals(interval(0.6346192975441497, 0.6795992982245281), tuples.valueAt(81));
		assertEquals(interval(0.5913983513994728, 0.6346192975441499), tuples.valueAt(82));
		assertEquals(interval(0.549754652192772, 0.591398351399473), tuples.valueAt(83));
		assertEquals(interval(0.5095254494944309, 0.5497546521927722), tuples.valueAt(84));
		assertEquals(interval(0.4705642812122538, 0.5095254494944311), tuples.valueAt(85));
		assertEquals(interval(0.43273864224742836, 0.4705642812122539), tuples.valueAt(86));
		assertEquals(interval(0.3959280087977239, 0.4327386422474285), tuples.valueAt(87));
		assertEquals(interval(0.36002215309575936, 0.395928008797724), tuples.valueAt(88));
		assertEquals(interval(0.3249196962329092, 0.36002215309575947), tuples.valueAt(89));
		assertEquals(interval(0.29052685673191947, 0.32491969623290934), tuples.valueAt(90));
		assertEquals(interval(0.2567563603677299, 0.2905268567319196), tuples.valueAt(91));
		assertEquals(interval(0.22352648289715243, 0.25675636036773003), tuples.valueAt(92));
		assertEquals(interval(0.1907602022185702, 0.2235264828971525), tuples.valueAt(93));
		assertEquals(interval(0.1583844403245399, 0.19076020221857026), tuples.valueAt(94));
		assertEquals(interval(0.12632937844611195, 0.15838444032453997), tuples.valueAt(95));
		assertEquals(interval(0.09452783117928597, 0.126329378446112), tuples.valueAt(96));
		assertEquals(interval(0.06291466725365384, 0.094527831179286), tuples.valueAt(97));
		assertEquals(interval(0.031426266043355404, 0.06291466725365387), tuples.valueAt(98));
		assertEquals(interval(4.440892098500625E-15, 0.03142626604335542), tuples.valueAt(99));
	}

	@Test
	public void minusTanXHiRes() {
		for (IntervalTuple tuple: hiResFunction("-tan(x)")) {
			assertEquals(IntervalConstants.whole().invert(), tuple.y());
		}
	}

	@Test
	public void tanXInverse() {
		IntervalTupleList tuples = functionValuesWithSampleCount("1/tan(x)",
				0, PI, -8, 8, 100);
		assertEquals(invertedInterval(Double.NEGATIVE_INFINITY, 31.820515953773953),
				tuples.valueAt(0));
		assertEquals(interval(15.894544843865297, 31.820515953773967), tuples.valueAt(1));
		assertEquals(interval(10.578894993405632, 15.894544843865308), tuples.valueAt(2));
		assertEquals(interval(7.915815088305824, 10.578894993405639), tuples.valueAt(3));
		assertEquals(interval(6.313751514675042, 7.91581508830583), tuples.valueAt(4));
		assertEquals(interval(5.242183581113175, 6.313751514675046), tuples.valueAt(5));
		assertEquals(interval(4.473742829211553, 5.242183581113179), tuples.valueAt(6));
		assertEquals(interval(3.8947428549298584, 4.473742829211557), tuples.valueAt(7));
		assertEquals(interval(3.442022576669218, 3.894742854929861), tuples.valueAt(8));
		assertEquals(interval(3.0776835371752536, 3.44202257666922), tuples.valueAt(9));
		assertEquals(interval(2.7776068539149747, 3.0776835371752553), tuples.valueAt(10));
		assertEquals(interval(2.525711689447305, 2.7776068539149765), tuples.valueAt(11));
		assertEquals(interval(2.3108636538824108, 2.5257116894473066), tuples.valueAt(12));
		assertEquals(interval(2.125108173157203, 2.310863653882412), tuples.valueAt(13));
		assertEquals(interval(1.9626105055051504, 2.125108173157204), tuples.valueAt(14));
		assertEquals(interval(1.818993247281066, 1.9626105055051517), tuples.valueAt(15));
		assertEquals(interval(1.690907655785011, 1.8189932472810673), tuples.valueAt(16));
		assertEquals(interval(1.5757478599686505, 1.690907655785012), tuples.valueAt(17));
		assertEquals(interval(1.4714553158199684, 1.5757478599686514), tuples.valueAt(18));
		assertEquals(interval(1.376381920471173, 1.4714553158199695), tuples.valueAt(19));
		assertEquals(interval(1.289192231785066, 1.3763819204711738), tuples.valueAt(20));
		assertEquals(interval(1.2087923504096083, 1.2891922317850668), tuples.valueAt(21));
		assertEquals(interval(1.1342773492554046, 1.2087923504096092), tuples.valueAt(22));
		assertEquals(interval(1.064891840324791, 1.1342773492554052), tuples.valueAt(23));
		assertEquals(interval(0.999999999999999, 1.0648918403247916), tuples.valueAt(24));
		assertEquals(interval(0.9390625058174913, 0.9999999999999997), tuples.valueAt(25));
		assertEquals(interval(0.8816185923631882, 0.939062505817492), tuples.valueAt(26));
		assertEquals(interval(0.8272719459724747, 0.8816185923631887), tuples.valueAt(27));
		assertEquals(interval(0.7756795110496121, 0.8272719459724752), tuples.valueAt(28));
		assertEquals(interval(0.7265425280053599, 0.7756795110496126), tuples.valueAt(29));
		assertEquals(interval(0.6795992982245255, 0.7265425280053603), tuples.valueAt(30));
		assertEquals(interval(0.6346192975441473, 0.6795992982245259), tuples.valueAt(31));
		assertEquals(interval(0.5913983513994702, 0.6346192975441476), tuples.valueAt(32));
		assertEquals(interval(0.5497546521927691, 0.5913983513994705), tuples.valueAt(33));
		assertEquals(interval(0.5095254494944278, 0.5497546521927695), tuples.valueAt(34));
		assertEquals(interval(0.4705642812122505, 0.5095254494944281), tuples.valueAt(35));
		assertEquals(interval(0.4327386422474249, 0.47056428121225086), tuples.valueAt(36));
		assertEquals(interval(0.3959280087977203, 0.4327386422474252), tuples.valueAt(37));
		assertEquals(interval(0.36002215309575564, 0.3959280087977205), tuples.valueAt(38));
		assertEquals(interval(0.32491969623290534, 0.36002215309575586), tuples.valueAt(39));
		assertEquals(interval(0.2905268567319154, 0.3249196962329055), tuples.valueAt(40));
		assertEquals(interval(0.2567563603677257, 0.2905268567319156), tuples.valueAt(41));
		assertEquals(interval(0.22352648289714802, 0.25675636036772587), tuples.valueAt(42));
		assertEquals(interval(0.19076020221856566, 0.2235264828971482), tuples.valueAt(43));
		assertEquals(interval(0.15838444032453516, 0.19076020221856577), tuples.valueAt(44));
		assertEquals(interval(0.126329378446107, 0.15838444032453525), tuples.valueAt(45));
		assertEquals(interval(0.09452783117928086, 0.1263293784461071), tuples.valueAt(46));
		assertEquals(interval(0.06291466725364854, 0.09452783117928093), tuples.valueAt(47));
		assertEquals(interval(0.03142626604334991, 0.06291466725364858), tuples.valueAt(48));
		assertEquals(uninvertedInterval(-1.339206523454095E-15, 0.0314573336268236),
				tuples.valueAt(49));
		assertEquals(interval(-0.031426266043352594, -1.393499969507555E-15), tuples.valueAt(50));
		assertEquals(interval(-0.06291466725365126, -0.03142626604335257), tuples.valueAt(51));
		assertEquals(interval(-0.09452783117928362, -0.06291466725365122), tuples.valueAt(52));
		assertEquals(interval(-0.1263293784461098, -0.09452783117928357), tuples.valueAt(53));
		assertEquals(interval(-0.158384440324538, -0.12632937844610972), tuples.valueAt(54));
		assertEquals(interval(-0.19076020221856851, -0.15838444032453788), tuples.valueAt(55));
		assertEquals(interval(-0.22352648289715096, -0.1907602022185684), tuples.valueAt(56));
		assertEquals(interval(-0.2567563603677287, -0.22352648289715082), tuples.valueAt(57));
		assertEquals(interval(-0.29052685673191847, -0.25675636036772853), tuples.valueAt(58));
		assertEquals(interval(-0.32491969623290845, -0.2905268567319183), tuples.valueAt(59));
		assertEquals(interval(-0.36002215309575886, -0.32491969623290823), tuples.valueAt(60));
		assertEquals(interval(-0.39592800879772355, -0.3600221530957586), tuples.valueAt(61));
		assertEquals(interval(-0.43273864224742836, -0.39592800879772333), tuples.valueAt(62));
		assertEquals(interval(-0.4705642812122541, -0.4327386422474281), tuples.valueAt(63));
		assertEquals(interval(-0.5095254494944312, -0.4705642812122538), tuples.valueAt(64));
		assertEquals(interval(-0.5497546521927724, -0.5095254494944309), tuples.valueAt(65));
		assertEquals(interval(-0.5913983513994733, -0.549754652192772), tuples.valueAt(66));
		assertEquals(interval(-0.6346192975441501, -0.5913983513994729), tuples.valueAt(67));
		assertEquals(interval(-0.6795992982245284, -0.6346192975441497), tuples.valueAt(68));
		assertEquals(interval(-0.7265425280053625, -0.6795992982245279), tuples.valueAt(69));
		assertEquals(interval(-0.7756795110496146, -0.726542528005362), tuples.valueAt(70));
		assertEquals(interval(-0.8272719459724768, -0.7756795110496141), tuples.valueAt(71));
		assertEquals(interval(-0.8816185923631901, -0.8272719459724762), tuples.valueAt(72));
		assertEquals(interval(-0.9390625058174932, -0.8816185923631895), tuples.valueAt(73));
		assertEquals(interval(-1.0000000000000004, -0.9390625058174925), tuples.valueAt(74));
		assertEquals(interval(-1.064891840324792, -0.9999999999999999), tuples.valueAt(75));
		assertEquals(interval(-1.134277349255405, -1.0648918403247911), tuples.valueAt(76));
		assertEquals(interval(-1.2087923504096083, -1.1342773492554044), tuples.valueAt(77));
		assertEquals(interval(-1.2891922317850653, -1.2087923504096076), tuples.valueAt(78));
		assertEquals(interval(-1.3763819204711714, -1.2891922317850644), tuples.valueAt(79));
		assertEquals(interval(-1.4714553158199664, -1.3763819204711705), tuples.valueAt(80));
		assertEquals(interval(-1.5757478599686474, -1.4714553158199652), tuples.valueAt(81));
		assertEquals(interval(-1.6909076557850065, -1.5757478599686463), tuples.valueAt(82));
		assertEquals(interval(-1.8189932472810602, -1.6909076557850053), tuples.valueAt(83));
		assertEquals(interval(-1.9626105055051428, -1.818993247281059), tuples.valueAt(84));
		assertEquals(interval(-2.1251081731571926, -1.9626105055051415), tuples.valueAt(85));
		assertEquals(interval(-2.310863653882398, -2.1251081731571912), tuples.valueAt(86));
		assertEquals(interval(-2.525711689447289, -2.3108636538823966), tuples.valueAt(87));
		assertEquals(interval(-2.7776068539149543, -2.525711689447287), tuples.valueAt(88));
		assertEquals(interval(-3.0776835371752265, -2.7776068539149525), tuples.valueAt(89));
		assertEquals(interval(-3.442022576669183, -3.0776835371752242), tuples.valueAt(90));
		assertEquals(interval(-3.894742854929812, -3.442022576669181), tuples.valueAt(91));
		assertEquals(interval(-4.4737428292114885, -3.8947428549298095), tuples.valueAt(92));
		assertEquals(interval(-5.242183581113083, -4.473742829211486), tuples.valueAt(93));
		assertEquals(interval(-6.3137515146749, -5.242183581113079), tuples.valueAt(94));
		assertEquals(interval(-7.9158150883055916, -6.3137515146748955), tuples.valueAt(95));
		assertEquals(interval(-10.578894993405198, -7.915815088305586), tuples.valueAt(96));
		assertEquals(interval(-15.894544843864274, -10.578894993405193), tuples.valueAt(97));
		assertEquals(interval(-31.820515953769654, -15.894544843864264), tuples.valueAt(98));
		assertEquals(interval(-2.2517998136852488E14, -31.820515953769632), tuples.valueAt(99));
	}

	@Test
	public void minusTanXInverse() {
		IntervalTupleList tuples = functionValuesWithSampleCount("-1/tan(x)",
				0, PI, -8, 8, 100);
		assertEquals(null, null);
	}

	}
