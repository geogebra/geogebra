/* Colors.java
 * =========================================================================
 * This file is originally part of the JMathTeX Library - http://jmathtex.sourceforge.net
 *
 * Copyright (C) 2004-2007 Universiteit Gent
 * Copyright (C) 2009 DENIZET Calixte
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce
 * an executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under terms
 * of your choice, provided that you also meet, for each linked independent
 * module, the terms and conditions of the license of that module.
 * An independent module is a module which is not derived from or based
 * on this library. If you modify this library, you may extend this exception
 * to your version of the library, but you are not obliged to do so.
 * If you do not wish to do so, delete this exception statement from your
 * version.
 *
 */

package com.himamis.retex.renderer.share;

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;

/**
 * An atom representing the foreground and background color of an other atom.
 */
public class Colors {

	private static Map<String, Color> all;

	private static final void init() {
		all = new HashMap<String, Color>() {
			{

				GraphicsFactory g = FactoryProvider.getInstance()
						.getGraphicsFactory();

				// jlm2 vs colors below
				// put("AliceBlue", g.createColor(15792383));
				// put("AntiqueWhite", g.createColor(16444375));
				// put("Apricot", g.createColor(16756090));
				// put("Aqua", g.createColor(65535));
				// put("Aquamarine", g.createColor(3080115));
				// put("Azure", g.createColor(15794175));
				// put("AzureAzureBlue", g.createColor(26367));
				// put("AzureAzureCyan", g.createColor(39423));
				// put("AzureBlueDark", g.createColor(13209));
				// put("AzureBlueLight", g.createColor(6724095));
				// put("AzureBlueMedium", g.createColor(3368652));
				// put("AzureCyanDark", g.createColor(26265));
				// put("AzureCyanLight", g.createColor(6737151));
				// put("AzureCyanMedium", g.createColor(3381708));
				// put("AzureDarkDull", g.createColor(3368601));
				// put("AzureDarkHard", g.createColor(26316));
				// put("AzureLightDull", g.createColor(6724044));
				// put("AzureLightHard", g.createColor(3381759));
				// put("AzureObscureDull", g.createColor(13158));
				// put("AzurePaleDull", g.createColor(10079487));
				// put("Beige", g.createColor(16119260));
				// put("Bisque", g.createColor(16770244));
				// put("Bittersweet", g.createColor(12726272));
				// put("Black", g.createColor(0));
				// put("BlanchedAlmond", g.createColor(16772045));
				// put("Blue", g.createColor(255));
				// put("BlueAzureDark", g.createColor(13260));
				// put("BlueAzureLight", g.createColor(3368703));
				// put("BlueBlueAzure", g.createColor(13311));
				// put("BlueBlueViolet", g.createColor(3342591));
				// put("BlueDarkDull", g.createColor(3355545));
				// put("BlueDarkFaded", g.createColor(153));
				// put("BlueDarkHard", g.createColor(204));
				// put("BlueDarkWeak", g.createColor(3355494));
				// put("BlueGreen", g.createColor(2555819));
				// put("BlueLightDull", g.createColor(6710988));
				// put("BlueLightFaded", g.createColor(6711039));
				// put("BlueLightHard", g.createColor(3355647));
				// put("BlueLightWeak", g.createColor(10066380));
				// put("BlueMediumFaded", g.createColor(3355596));
				// put("BlueMediumWeak", g.createColor(6710937));
				// put("BlueObscureDull", g.createColor(102));
				// put("BlueObscureWeak", g.createColor(51));
				// put("BluePaleDull", g.createColor(10066431));
				// put("BluePaleWeak", g.createColor(13421823));
				// put("BlueViolet", g.createColor(2234101));
				// put("BlueVioletDark", g.createColor(3342540));
				// put("BlueVioletLight", g.createColor(6697983));
				// put("BrickRed", g.createColor(12063755));
				// put("Brown", g.createColor(6689536));
				// put("BurlyWood", g.createColor(14596231));
				// put("BurntOrange", g.createColor(16743680));
				// put("CadetBlue", g.createColor(6385348));
				// put("CarnationPink", g.createColor(16735999));
				// put("Cerulean", g.createColor(1041407));
				// put("Chartreuse", g.createColor(8388352));
				// put("Chocolate", g.createColor(13789470));
				// put("Coral", g.createColor(16744272));
				// put("CornflowerBlue", g.createColor(5889791));
				// put("Cornsilk", g.createColor(16775388));
				// put("Crimson", g.createColor(14423100));
				// put("Cyan", g.createColor(65535));
				// put("CyanAzureDark", g.createColor(39372));
				// put("CyanAzureLight", g.createColor(3394815));
				// put("CyanCyanAzure", g.createColor(52479));
				// put("CyanCyanTeal", g.createColor(65484));
				// put("CyanDarkDull", g.createColor(3381657));
				// put("CyanDarkFaded", g.createColor(39321));
				// put("CyanDarkHard", g.createColor(52428));
				// put("CyanDarkWeak", g.createColor(3368550));
				// put("CyanLightDull", g.createColor(6737100));
				// put("CyanLightFaded", g.createColor(6750207));
				// put("CyanLightHard", g.createColor(3407871));
				// put("CyanLightWeak", g.createColor(10079436));
				// put("CyanMediumFaded", g.createColor(3394764));
				// put("CyanMediumWeak", g.createColor(6723993));
				// put("CyanObscureDull", g.createColor(26214));
				// put("CyanObscureWeak", g.createColor(13107));
				// put("CyanPaleDull", g.createColor(10092543));
				// put("CyanPaleWeak", g.createColor(13434879));
				// put("CyanTealDark", g.createColor(52377));
				// put("CyanTealLight", g.createColor(3407820));
				// put("Dandelion", g.createColor(16758057));
				// put("DarkBlue", g.createColor(139));
				// put("DarkCyan", g.createColor(35723));
				// put("DarkGoldenrod", g.createColor(12092939));
				// put("DarkGray", g.createColor(11119017));
				// put("DarkGreen", g.createColor(25600));
				// put("DarkKhaki", g.createColor(12433259));
				// put("DarkMagenta", g.createColor(9109643));
				// put("DarkOliveGreen", g.createColor(5597999));
				// put("DarkOrange", g.createColor(16747520));
				// put("DarkOrchid", g.createColor(10040268));
				// put("DarkRed", g.createColor(9109504));
				// put("DarkSalmon", g.createColor(15308410));
				// put("DarkSeaGreen", g.createColor(9419919));
				// put("DarkSlateBlue", g.createColor(4734347));
				// put("DarkSlateGray", g.createColor(3100495));
				// put("DarkTurquoise", g.createColor(52945));
				// put("DarkViolet", g.createColor(9699539));
				// put("DeepPink", g.createColor(16716947));
				// put("DeepSkyBlue", g.createColor(49151));
				// put("DimGray", g.createColor(6908265));
				// put("DodgerBlue", g.createColor(2003199));
				// put("Emerald", g.createColor(65408));
				// put("FireBrick", g.createColor(11674146));
				// put("FloralWhite", g.createColor(16775920));
				// put("ForestGreen", g.createColor(1368091));
				// put("Fuchsia", g.createColor(8132075));
				// put("Gainsboro", g.createColor(14474460));
				// put("GhostWhite", g.createColor(16316671));
				// put("Gold", g.createColor(16766720));
				// put("Goldenrod", g.createColor(16770601));
				// put("Gray", g.createColor(8421504));
				// put("GrayDark", g.createColor(6710886));
				// put("GrayLight", g.createColor(10066329));
				// put("GrayObscure", g.createColor(3355443));
				// put("GrayPale", g.createColor(13421772));
				// put("Green", g.createColor(65280));
				// put("GreenDarkDull", g.createColor(3381555));
				// put("GreenDarkFaded", g.createColor(39168));
				// put("GreenDarkHard", g.createColor(52224));
				// put("GreenDarkWeak", g.createColor(3368499));
				// put("GreenGreenSpring", g.createColor(3407616));
				// put("GreenGreenTeal", g.createColor(65331));
				// put("GreenLightDull", g.createColor(6736998));
				// put("GreenLightFaded", g.createColor(6750054));
				// put("GreenLightHard", g.createColor(3407667));
				// put("GreenLightWeak", g.createColor(10079385));
				// put("GreenMediumFaded", g.createColor(3394611));
				// put("GreenMediumWeak", g.createColor(6723942));
				// put("GreenObscureDull", g.createColor(26112));
				// put("GreenObscureWeak", g.createColor(13056));
				// put("GreenPaleDull", g.createColor(10092441));
				// put("GreenPaleWeak", g.createColor(13434828));
				// put("GreenSpringDark", g.createColor(3394560));
				// put("GreenSpringLight", g.createColor(6750003));
				// put("GreenTealDark", g.createColor(52275));
				// put("GreenTealLight", g.createColor(3407718));
				// put("GreenYellow", g.createColor(14286671));
				// put("Honeydew", g.createColor(15794160));
				// put("HotPink", g.createColor(16738740));
				// put("IndianRed", g.createColor(13458524));
				// put("Indigo", g.createColor(4915330));
				// put("Ivory", g.createColor(16777200));
				// put("JungleGreen", g.createColor(262010));
				// put("Khaki", g.createColor(15787660));
				// put("Lavender", g.createColor(16745983));
				// put("LavenderBlush", g.createColor(16773365));
				// put("LawnGreen", g.createColor(8190976));
				// put("LemonChiffon", g.createColor(16775885));
				// put("LightBlue", g.createColor(11393254));
				// put("LightCoral", g.createColor(15761536));
				// put("LightCyan", g.createColor(14745599));
				// put("LightGoldenrod", g.createColor(16448210));
				// put("LightGray", g.createColor(13882323));
				// put("LightGreen", g.createColor(9498256));
				// put("LightPink", g.createColor(16758465));
				// put("LightSalmon", g.createColor(16752762));
				// put("LightSeaGreen", g.createColor(2142890));
				// put("LightSkyBlue", g.createColor(8900346));
				// put("LightSlateGray", g.createColor(7833753));
				// put("LightSteelBlue", g.createColor(11584734));
				// put("LightYellow", g.createColor(16777184));
				// put("Lime", g.createColor(65280));
				// put("LimeGreen", g.createColor(8453888));
				// put("Linen", g.createColor(16445670));
				// put("Magenta", g.createColor(16711935));
				// put("MagentaDarkDull", g.createColor(10040217));
				// put("MagentaDarkFaded", g.createColor(10027161));
				// put("MagentaDarkHard", g.createColor(13369548));
				// put("MagentaDarkWeak", g.createColor(6697830));
				// put("MagentaLightDull", g.createColor(13395660));
				// put("MagentaLightFaded", g.createColor(16738047));
				// put("MagentaLightHard", g.createColor(16724991));
				// put("MagentaLightWeak", g.createColor(13408716));
				// put("MagentaMagentaPink", g.createColor(16711884));
				// put("MagentaMagentaViolet", g.createColor(13369599));
				// put("MagentaMediumFaded", g.createColor(13382604));
				// put("MagentaMediumWeak", g.createColor(10053273));
				// put("MagentaObscureDull", g.createColor(6684774));
				// put("MagentaObscureWeak", g.createColor(3342387));
				// put("MagentaPaleDull", g.createColor(16751103));
				// put("MagentaPaleWeak", g.createColor(16764159));
				// put("MagentaPinkDark", g.createColor(13369497));
				// put("MagentaPinkLight", g.createColor(16724940));
				// put("MagentaVioletDark", g.createColor(10027212));
				// put("MagentaVioletLight", g.createColor(13382655));
				// put("Mahogany", g.createColor(10885398));
				// put("Maroon", g.createColor(11343671));
				// put("MediumAquamarine", g.createColor(6737322));
				// put("MediumBlue", g.createColor(205));
				// put("MediumOrchid", g.createColor(12211667));
				// put("MediumPurple", g.createColor(9662683));
				// put("MediumSeaGreen", g.createColor(3978097));
				// put("MediumSlateBlue", g.createColor(8087790));
				// put("MediumSpringGreen", g.createColor(64154));
				// put("MediumTurquoise", g.createColor(4772300));
				// put("MediumVioletRed", g.createColor(13047173));
				// put("Melon", g.createColor(16747136));
				// put("MidnightBlue", g.createColor(229009));
				// put("MintCream", g.createColor(16121850));
				// put("MistyRose", g.createColor(16770273));
				// put("Moccasin", g.createColor(16770229));
				// put("Mulberry", g.createColor(10820090));
				// put("NavajoWhite", g.createColor(16768685));
				// put("Navy", g.createColor(128));
				// put("NavyBlue", g.createColor(1013247));
				// put("OldLace", g.createColor(16643558));
				// put("Olive", g.createColor(8421376));
				// put("OliveDrab", g.createColor(7048739));
				// put("OliveGreen", g.createColor(3643656));
				// put("Orange", g.createColor(16737057));
				// put("OrangeDarkDull", g.createColor(10053171));
				// put("OrangeDarkHard", g.createColor(13395456));
				// put("OrangeLightDull", g.createColor(13408614));
				// put("OrangeLightHard", g.createColor(16750899));
				// put("OrangeObscureDull", g.createColor(6697728));
				// put("OrangeOrangeRed", g.createColor(16737792));
				// put("OrangeOrangeYellow", g.createColor(16750848));
				// put("OrangePaleDull", g.createColor(16764057));
				// put("OrangeRed", g.createColor(16711808));
				// put("OrangeRedDark", g.createColor(10040064));
				// put("OrangeRedLight", g.createColor(16750950));
				// put("OrangeRedMedium", g.createColor(13395507));
				// put("OrangeYellowDark", g.createColor(10053120));
				// put("OrangeYellowLight", g.createColor(16764006));
				// put("OrangeYellowMedium", g.createColor(13408563));
				// put("Orchid", g.createColor(11361535));
				// put("PaleGoldenrod", g.createColor(15657130));
				// put("PaleGreen", g.createColor(10025880));
				// put("PaleTurquoise", g.createColor(11529966));
				// put("PaleVioletRed", g.createColor(14381203));
				// put("PapayaWhip", g.createColor(16773077));
				// put("Peach", g.createColor(16744525));
				// put("PeachPuff", g.createColor(16767673));
				// put("Periwinkle", g.createColor(7238655));
				// put("Peru", g.createColor(13468991));
				// put("PineGreen", g.createColor(1032014));
				// put("Pink", g.createColor(16761035));
				// put("PinkDarkDull", g.createColor(10040166));
				// put("PinkDarkHard", g.createColor(13369446));
				// put("PinkLightDull", g.createColor(13395609));
				// put("PinkLightHard", g.createColor(16724889));
				// put("PinkMagentaDark", g.createColor(10027110));
				// put("PinkMagentaLight", g.createColor(16737996));
				// put("PinkMagentaMedium", g.createColor(13382553));
				// put("PinkObscureDull", g.createColor(6684723));
				// put("PinkPaleDull", g.createColor(16751052));
				// put("PinkPinkMagenta", g.createColor(16711833));
				// put("PinkPinkRed", g.createColor(16711782));
				// put("PinkRedDark", g.createColor(10027059));
				// put("PinkRedLight", g.createColor(16737945));
				// put("PinkRedMedium", g.createColor(13382502));
				// put("Plum", g.createColor(8388863));
				// put("PowderBlue", g.createColor(11591910));
				// put("ProcessBlue", g.createColor(720895));
				// put("Purple", g.createColor(9184511));
				// put("RawSienna", g.createColor(9185024));
				// put("RebeccaPurple", g.createColor(6697881));
				// put("Red", g.createColor(16711680));
				// put("RedDarkDull", g.createColor(10040115));
				// put("RedDarkFaded", g.createColor(10027008));
				// put("RedDarkHard", g.createColor(13369344));
				// put("RedDarkWeak", g.createColor(6697779));
				// put("RedLightDull", g.createColor(13395558));
				// put("RedLightFaded", g.createColor(16737894));
				// put("RedLightHard", g.createColor(16724787));
				// put("RedLightWeak", g.createColor(13408665));
				// put("RedMediumFaded", g.createColor(13382451));
				// put("RedMediumWeak", g.createColor(10053222));
				// put("RedObscureDull", g.createColor(6684672));
				// put("RedObscureWeak", g.createColor(3342336));
				// put("RedOrange", g.createColor(16726817));
				// put("RedOrangeDark", g.createColor(13382400));
				// put("RedOrangeLight", g.createColor(16737843));
				// put("RedPaleDull", g.createColor(16751001));
				// put("RedPaleWeak", g.createColor(16764108));
				// put("RedPinkDark", g.createColor(13369395));
				// put("RedPinkLight", g.createColor(16724838));
				// put("RedRedOrange", g.createColor(16724736));
				// put("RedRedPink", g.createColor(16711731));
				// put("RedViolet", g.createColor(10293672));
				// put("Rhodamine", g.createColor(16723711));
				// put("RosyBrown", g.createColor(12357519));
				// put("RoyalBlue", g.createColor(33023));
				// put("RoyalPurple", g.createColor(4200959));
				// put("RubineRed", g.createColor(16711902));
				// put("SaddleBrown", g.createColor(9127187));
				// put("Salmon", g.createColor(16742558));
				// put("SandyBrown", g.createColor(16032864));
				// put("SeaGreen", g.createColor(5242752));
				// put("Seashell", g.createColor(16774638));
				// put("Sepia", g.createColor(5049600));
				// put("Sienna", g.createColor(10506797));
				// put("Silver", g.createColor(12632256));
				// put("SkyBlue", g.createColor(6422496));
				// put("SlateBlue", g.createColor(6970061));
				// put("SlateGray", g.createColor(7372944));
				// put("Snow", g.createColor(16775930));
				// put("SpringDarkDull", g.createColor(6723891));
				// put("SpringDarkHard", g.createColor(6736896));
				// put("SpringGreen", g.createColor(12451645));
				// put("SpringGreenDark", g.createColor(3381504));
				// put("SpringGreenLight", g.createColor(10092390));
				// put("SpringGreenMedium", g.createColor(6736947));
				// put("SpringLightDull", g.createColor(10079334));
				// put("SpringLightHard", g.createColor(10092339));
				// put("SpringObscureDull", g.createColor(3368448));
				// put("SpringPaleDull", g.createColor(13434777));
				// put("SpringSpringGreen", g.createColor(6749952));
				// put("SpringSpringYellow", g.createColor(10092288));
				// put("SpringYellowDark", g.createColor(6723840));
				// put("SpringYellowLight", g.createColor(13434726));
				// put("SpringYellowMedium", g.createColor(10079283));
				// put("SteelBlue", g.createColor(4620980));
				// put("Tan", g.createColor(14390384));
				// put("Teal", g.createColor(32896));
				// put("TealBlue", g.createColor(2357925));
				// put("TealCyanDark", g.createColor(39270));
				// put("TealCyanLight", g.createColor(6750156));
				// put("TealCyanMedium", g.createColor(3394713));
				// put("TealDarkDull", g.createColor(3381606));
				// put("TealDarkHard", g.createColor(52326));
				// put("TealGreenDark", g.createColor(39219));
				// put("TealGreenLight", g.createColor(6750105));
				// put("TealGreenMedium", g.createColor(3394662));
				// put("TealLightDull", g.createColor(6737049));
				// put("TealLightHard", g.createColor(3407769));
				// put("TealObscureDull", g.createColor(26163));
				// put("TealPaleDull", g.createColor(10092492));
				// put("TealTealCyan", g.createColor(65433));
				// put("TealTealGreen", g.createColor(65382));
				// put("Thistle", g.createColor(14707199));
				// put("Tomato", g.createColor(16737095));
				// put("Turquoise", g.createColor(2555852));
				// put("Violet", g.createColor(3547135));
				// put("VioletBlueDark", g.createColor(3342489));
				// put("VioletBlueLight", g.createColor(10053375));
				// put("VioletBlueMedium", g.createColor(6697932));
				// put("VioletDarkDull", g.createColor(6697881));
				// put("VioletDarkHard", g.createColor(6684876));
				// put("VioletLightDull", g.createColor(10053324));
				// put("VioletLightHard", g.createColor(10040319));
				// put("VioletMagentaDark", g.createColor(6684825));
				// put("VioletMagentaLight", g.createColor(13395711));
				// put("VioletMagentaMedium", g.createColor(10040268));
				// put("VioletObscureDull", g.createColor(3342438));
				// put("VioletPaleDull", g.createColor(13408767));
				// put("VioletRed", g.createColor(16724223));
				// put("VioletVioletBlue", g.createColor(6684927));
				// put("VioletVioletMagenta", g.createColor(10027263));
				// put("Wheat", g.createColor(16113331));
				// put("White", g.createColor(16777215));
				// put("WhiteSmoke", g.createColor(16119285));
				// put("WildStrawberry", g.createColor(16714396));
				// put("Yellow", g.createColor(16776960));
				// put("YellowDarkDull", g.createColor(10066227));
				// put("YellowDarkFaded", g.createColor(10066176));
				// put("YellowDarkHard", g.createColor(13421568));
				// put("YellowDarkWeak", g.createColor(6710835));
				// put("YellowGreen", g.createColor(9436994));
				// put("YellowLightDull", g.createColor(13421670));
				// put("YellowLightFaded", g.createColor(16777062));
				// put("YellowLightHard", g.createColor(16777011));
				// put("YellowLightWeak", g.createColor(13421721));
				// put("YellowMediumFaded", g.createColor(13421619));
				// put("YellowMediumWeak", g.createColor(10066278));
				// put("YellowObscureDull", g.createColor(6710784));
				// put("YellowObscureWeak", g.createColor(3355392));
				// put("YellowOrange", g.createColor(16749568));
				// put("YellowOrangeDark", g.createColor(13408512));
				// put("YellowOrangeLight", g.createColor(16763955));
				// put("YellowPaleDull", g.createColor(16777113));
				// put("YellowPaleWeak", g.createColor(16777164));
				// put("YellowSpringDark", g.createColor(10079232));
				// put("YellowSpringLight", g.createColor(13434675));
				// put("YellowYellowOrange", g.createColor(16763904));
				// put("YellowYellowSpring", g.createColor(13434624));
				// put("aliceblue", g.createColor(15792383));
				// put("antiquewhite", g.createColor(16444375));
				// put("aqua", g.createColor(65535));
				// put("aquamarine", g.createColor(8388564));
				// put("azure", g.createColor(15794175));
				// put("beige", g.createColor(16119260));
				// put("bisque", g.createColor(16770244));
				// put("black", g.createColor(0));
				// put("blanchedalmond", g.createColor(16772045));
				// put("blue", g.createColor(255));
				// put("blueviolet", g.createColor(9055202));
				// put("brown", g.createColor(10824234));
				// put("burlywood", g.createColor(14596231));
				// put("cadetblue", g.createColor(6266528));
				// put("chartreuse", g.createColor(8388352));
				// put("chocolate", g.createColor(13789470));
				// put("coral", g.createColor(16744272));
				// put("cornflowerblue", g.createColor(6591981));
				// put("cornsilk", g.createColor(16775388));
				// put("crimson", g.createColor(14423100));
				// put("cyan", g.createColor(65535));
				// put("darkblue", g.createColor(139));
				// put("darkcyan", g.createColor(35723));
				// put("darkgoldenrod", g.createColor(12092939));
				// put("darkgray", g.createColor(11119017));
				// put("darkgreen", g.createColor(25600));
				// put("darkgrey", g.createColor(11119017));
				// put("darkkhaki", g.createColor(12433259));
				// put("darkmagenta", g.createColor(9109643));
				// put("darkolivegreen", g.createColor(5597999));
				// put("darkorange", g.createColor(16747520));
				// put("darkorchid", g.createColor(10040012));
				// put("darkred", g.createColor(9109504));
				// put("darksalmon", g.createColor(15308410));
				// put("darkseagreen", g.createColor(9419919));
				// put("darkslateblue", g.createColor(4734347));
				// put("darkslategray", g.createColor(3100495));
				// put("darkslategrey", g.createColor(3100495));
				// put("darkturquoise", g.createColor(52945));
				// put("darkviolet", g.createColor(9699539));
				// put("deeppink", g.createColor(16716947));
				// put("deepskyblue", g.createColor(49151));
				// put("dimgray", g.createColor(6908265));
				// put("dimgrey", g.createColor(6908265));
				// put("dodgerblue", g.createColor(2003199));
				// put("firebrick", g.createColor(11674146));
				// put("floralwhite", g.createColor(16775920));
				// put("forestgreen", g.createColor(2263842));
				// put("fuchsia", g.createColor(16711935));
				// put("gainsboro", g.createColor(14474460));
				// put("ghostwhite", g.createColor(16316671));
				// put("gold", g.createColor(16766720));
				// put("goldenrod", g.createColor(14329120));
				// put("gray", g.createColor(8421504));
				// put("green", g.createColor(65280));
				// put("greenyellow", g.createColor(11403055));
				// put("grey", g.createColor(8421504));
				// put("honeydew", g.createColor(15794160));
				// put("hotpink", g.createColor(16738740));
				// put("indianred", g.createColor(13458524));
				// put("indigo", g.createColor(4915330));
				// put("ivory", g.createColor(16777200));
				// put("khaki", g.createColor(15787660));
				// put("lavender", g.createColor(15132410));
				// put("lavenderblush", g.createColor(16773365));
				// put("lawngreen", g.createColor(8190976));
				// put("lemonchiffon", g.createColor(16775885));
				// put("lightblue", g.createColor(11393254));
				// put("lightcoral", g.createColor(15761536));
				// put("lightcyan", g.createColor(14745599));
				// put("lightgoldenrodyellow", g.createColor(16448210));
				// put("lightgray", g.createColor(13882323));
				// put("lightgreen", g.createColor(9498256));
				// put("lightgrey", g.createColor(13882323));
				// put("lightpink", g.createColor(16758465));
				// put("lightsalmon", g.createColor(16752762));
				// put("lightseagreen", g.createColor(2142890));
				// put("lightskyblue", g.createColor(8900346));
				// put("lightslategray", g.createColor(7833753));
				// put("lightslategrey", g.createColor(7833753));
				// put("lightsteelblue", g.createColor(11584734));
				// put("lightyellow", g.createColor(16777184));
				// put("lime", g.createColor(65280));
				// put("limegreen", g.createColor(3329330));
				// put("linen", g.createColor(16445670));
				// put("magenta", g.createColor(16711935));
				// put("maroon", g.createColor(8388608));
				// put("mediumaquamarine", g.createColor(6737322));
				// put("mediumblue", g.createColor(205));
				// put("mediumorchid", g.createColor(12211667));
				// put("mediumpurple", g.createColor(9662683));
				// put("mediumseagreen", g.createColor(3978097));
				// put("mediumslateblue", g.createColor(8087790));
				// put("mediumspringgreen", g.createColor(64154));
				// put("mediumturquoise", g.createColor(4772300));
				// put("mediumvioletred", g.createColor(13047173));
				// put("midnightblue", g.createColor(1644912));
				// put("mintcream", g.createColor(16121850));
				// put("mistyrose", g.createColor(16770273));
				// put("moccasin", g.createColor(16770229));
				// put("navajowhite", g.createColor(16768685));
				// put("navy", g.createColor(128));
				// put("oldlace", g.createColor(16643558));
				// put("olive", g.createColor(8421376));
				// put("olivedrab", g.createColor(7048739));
				// put("orange", g.createColor(16753920));
				// put("orangered", g.createColor(16729344));
				// put("orchid", g.createColor(14315734));
				// put("palegoldenrod", g.createColor(15657130));
				// put("palegreen", g.createColor(10025880));
				// put("paleturquoise", g.createColor(11529966));
				// put("palevioletred", g.createColor(14381203));
				// put("papayawhip", g.createColor(16773077));
				// put("peachpuff", g.createColor(16767673));
				// put("peru", g.createColor(13468991));
				// put("pink", g.createColor(16761035));
				// put("plum", g.createColor(14524637));
				// put("powderblue", g.createColor(11591910));
				// put("purple", g.createColor(8388736));
				// put("red", g.createColor(16711680));
				// put("rosybrown", g.createColor(12357519));
				// put("royalblue", g.createColor(4286945));
				// put("saddlebrown", g.createColor(9127187));
				// put("salmon", g.createColor(16416787));
				// put("sandybrown", g.createColor(16032864));
				// put("seagreen", g.createColor(3050327));
				// put("seashell", g.createColor(16774638));
				// put("sienna", g.createColor(10506797));
				// put("silver", g.createColor(12632256));
				// put("skyblue", g.createColor(8900331));
				// put("slateblue", g.createColor(6970061));
				// put("slategray", g.createColor(7372944));
				// put("slategrey", g.createColor(7372944));
				// put("snow", g.createColor(16775930));
				// put("springgreen", g.createColor(65407));
				// put("steelblue", g.createColor(4620980));
				// put("tan", g.createColor(13808780));
				// put("teal", g.createColor(32896));
				// put("thistle", g.createColor(14204888));
				// put("tomato", g.createColor(16737095));
				// put("turquoise", g.createColor(4251856));
				// put("violet", g.createColor(15631086));
				// put("wheat", g.createColor(16113331));
				// put("white", g.createColor(16777215));
				// put("whitesmoke", g.createColor(16119285));
				// put("yellow", g.createColor(16776960));
				// put("yellowgreen", g.createColor(10145074));

				// GeoGebra colors below

				// not in jlm2
				// put("lightorange", g.createColor(16773077));
				// put("lightviolet", g.createColor(14725375));

				// different to jlm2
				// put("turquoise", g.createColor(11529966));
				// put("aqua", g.createColor(12375270));
				// put("lightblue", g.createColor(8224255));
				// put("darkgray", g.createColor(2105376));
				// put("darkblue", g.createColor(1849787));
				// put("lightgray", g.createColor(10526880));
				// put("lightyellow", g.createColor(16775885));
				// put("lime", g.createColor(12582656));
				// put("lightpurple", g.createColor(13421823));
				// put("violet", g.createColor(8323327));
				// put("lightgreen", g.createColor(13693120));
				// put("brown", g.createColor(10040064));
				// put("orange", g.createColor(16744192));
				// put("purple", g.createColor(8388736));
				// put("silver", g.createColor(4210752));
				// put("maroon", g.createColor(8388608));

				// same as jlm2

				// put("pink", g.createColor(16761035));
				// put("darkgreen", g.createColor(25600));
				// put("gold", g.createColor(16766720));
				// put("crimson", g.createColor(14423100));
				// put("gray", g.createColor(8421504));
				// put("indigo", g.createColor(4915330));

				// same as jlm2, also in jlm v1
				// put("black", g.createColor(0));
				// put("white", g.createColor(16777215));
				// put("red", g.createColor(16711680));
				// put("green", g.createColor(65280));
				// put("blue", g.createColor(255));
				// put("cyan", g.createColor(65535));
				// put("magenta", g.createColor(16711935));
				// put("yellow", g.createColor(16776960));

				// jlm v1 colors below
				put("black", ColorUtil.BLACK);
				put("white", ColorUtil.WHITE);
				put("red", ColorUtil.RED);
				put("green", ColorUtil.GREEN);
				put("blue", ColorUtil.BLUE);
				put("cyan", ColorUtil.CYAN);
				put("magenta", ColorUtil.MAGENTA);
				put("yellow", ColorUtil.YELLOW);

				put("greenyellow", g.createColor(217, 255, 79));
				put("goldenrod", g.createColor(255, 229, 41));
				put("dandelion", g.createColor(255, 181, 41));
				put("apricot", g.createColor(255, 173, 122));
				put("peach", g.createColor(255, 128, 77));
				put("melon", g.createColor(255, 138, 128));
				put("yelloworange", g.createColor(255, 148, 0));
				put("orange", g.createColor(255, 99, 33));
				put("burntorange", g.createColor(255, 125, 0));
				put("bittersweet", g.createColor(194, 48, 0));
				put("redorange", g.createColor(255, 59, 33));
				put("mahogany", g.createColor(166, 25, 22));
				put("maroon", g.createColor(173, 23, 55));
				put("brickred", g.createColor(184, 20, 11));
				put("orangered", g.createColor(255, 0, 128));
				put("rubinered", g.createColor(255, 0, 222));
				put("wildstrawberry", g.createColor(255, 10, 156));
				put("salmon", g.createColor(255, 120, 158));
				put("carnationpink", g.createColor(255, 94, 255));
				put("magenta", g.createColor(255, 0, 255));
				put("violetred", g.createColor(255, 48, 255));
				put("rhodamine", g.createColor(255, 46, 255));
				put("mulberry", g.createColor(165, 25, 250));
				put("redviolet", g.createColor(124, 21, 235));
				put("fuchsia", g.createColor(157, 17, 168));
				put("lavender", g.createColor(255, 133, 255));
				put("thistle", g.createColor(224, 105, 255));
				put("orchid", g.createColor(173, 92, 255));
				put("darkorchid", g.createColor(153, 51, 204));
				put("purple", g.createColor(140, 36, 255));
				put("plum", g.createColor(128, 0, 255));
				put("violet", g.createColor(54, 31, 255));
				put("royalpurple", g.createColor(64, 26, 255));
				put("blueviolet", g.createColor(34, 22, 245));
				put("periwinkle", g.createColor(110, 115, 255));
				put("cadetblue", g.createColor(97, 110, 196));
				put("cornflowerblue", g.createColor(89, 222, 255));
				put("midnightblue", g.createColor(3, 126, 145));
				put("navyblue", g.createColor(15, 117, 255));
				put("royalblue", g.createColor(0, 128, 255));
				put("cerulean", g.createColor(15, 227, 255));
				put("processblue", g.createColor(10, 255, 255));
				put("skyblue", g.createColor(97, 255, 224));
				put("turquoise", g.createColor(38, 255, 204));
				put("tealblue", g.createColor(35, 250, 165));
				put("aquamarine", g.createColor(46, 255, 178));
				put("bluegreen", g.createColor(38, 255, 171));
				put("emerald", g.createColor(0, 255, 128));
				put("junglegreen", g.createColor(3, 255, 122));
				put("seagreen", g.createColor(79, 255, 128));
				put("forestgreen", g.createColor(20, 224, 27));
				put("pinegreen", g.createColor(15, 191, 78));
				put("limegreen", g.createColor(128, 255, 0));
				put("yellowgreen", g.createColor(143, 255, 66));
				put("springgreen", g.createColor(189, 255, 61));
				put("olivegreen", g.createColor(55, 153, 8));
				put("rawsienna", g.createColor(140, 39, 0));
				put("sepia", g.createColor(77, 13, 0));
				put("brown", g.createColor(102, 19, 0));
				put("tan", g.createColor(219, 148, 112));
				put("gray", g.createColor(128, 128, 128));

			}
		};
	}

	public static Color getFromName(final String name) {
		if (all == null) {
			init();
		}
		final Color c = all.get(name);
		if (c == null) {
			return all.get(name.toLowerCase());
		}
		return c;
	}

	public static void add(final String name, final Color color) {
		if (all == null) {
			init();
		}
		all.put(name, color);
	}

	public static int clamp(final int n) {
		return Math.min(255, Math.max(n, 0));
	}

	public static double clamp(final double n) {
		return Math.min(1., Math.max(n, 0.));
	}

	public static Color conv(final double c, final double m, final double y,
			final double k) {
		final double kk = 255. * (1. - k);
		final int R = (int) (kk * (1. - c) + 0.5);
		final int G = (int) (kk * (1. - m) + 0.5);
		final int B = (int) (kk * (1. - y) + 0.5);
		return FactoryProvider.getInstance().getGraphicsFactory()
				.createColor((R << 16) | (G << 8) | B);
	}

	public static Color convHSB(final double h, final double s,
			final double l) {
		final double h1 = normH(h);
		return FactoryProvider.getInstance().getGraphicsFactory()
				.createColor(ColorUtil.HSBtoRGB(h1, s, l));
	}

	public static Color convHSL(final double h, final double s, final double l,
			final double a) {
		// https://www.w3.org/TR/css3-color/#hsl-color for algorithm
		final double ls = l * s;
		final double m2 = l + (l <= 0.5 ? ls : (s - ls));
		final double m1 = l * 2. - m2;
		final double h1 = normH(h);
		final float R = (float) HUEtoRGB(m1, m2, h1 + 1. / 3.);
		final float G = (float) HUEtoRGB(m1, m2, h1);
		final float B = (float) HUEtoRGB(m1, m2, h1 - 1. / 3.);

		return FactoryProvider.getInstance().getGraphicsFactory().createColor(R,
				G, B, (float) a);
	}

	public static Color convHSL(final double h, final double s,
			final double l) {
		return convHSL(h, s, l, 1f);
	}

	private static double HUEtoRGB(final double m1, final double m2, double h) {
		if (h < 0.) {
			h += 1.;
		} else if (h > 1.) {
			h -= 1.;
		}
		final double h6 = h * 6.;
		if (h6 < 1.) {
			return m1 + (m2 - m1) * h6;
		}
		if (h * 2. < 1.) {
			return m2;
		}
		if (h * 3. < 2.) {
			return m1 + (m2 - m1) * (4. - h6);
		}
		return m1;
	}

	private static double mod360(final double x) {
		return x - Math.floor(x / 360.) * 360.;
	}

	private static double normH(final double x) {
		return mod360(mod360(x) + 360.) / 360.;
	}

	private static double adjust(final double c, final double factor) {
		if (c == 0. || factor == 0.) {
			return 0.;
		}

		final double Gamma = 0.8;
		return Math.round(Math.pow(c * factor, Gamma));
	}

	public static Color convWave(final double waveLen) {
		double R, G, B;

		if (waveLen >= 380. && waveLen <= 439.) {
			R = -(waveLen - 440.) / 60.;
			G = 0.;
			B = 1.;
		} else if (waveLen >= 440. && waveLen <= 489.) {
			R = 0.;
			G = (waveLen - 440.) / 50.;
			B = 1.;
		} else if (waveLen >= 490. && waveLen <= 509.) {
			R = 0.;
			G = 1.;
			B = -(waveLen - 510.) / 20.;
		} else if (waveLen >= 510. && waveLen <= 579.) {
			R = (waveLen - 510.) / 70.;
			G = 1.;
			B = 0.;
		} else if (waveLen >= 580. && waveLen <= 644.) {
			R = 1.;
			G = -(waveLen - 645.) / 65.;
			B = 0.;
		} else if (waveLen >= 645. && waveLen <= 780.) {
			R = 1.;
			G = 0.;
			B = 0.;
		} else {
			R = 0.;
			G = 0.;
			B = 0.;
		}

		final double twave = Math.floor(waveLen);
		double factor;
		if (twave >= 380. && twave <= 419.) {
			factor = 0.3 + 0.7 * (waveLen - 380.) / 40.;
		} else if (twave >= 420. && twave <= 700.) {
			factor = 1.;
		} else if (twave >= 701. && twave <= 780.) {
			factor = 0.3 + 0.7 * (780. - waveLen) / 80.;
		} else {
			factor = 0.;
		}

		R = adjust(R, factor);
		G = adjust(G, factor);
		B = adjust(B, factor);

		return FactoryProvider.getInstance().getGraphicsFactory()
				.createColor((float) R, (float) G, (float) B);
	}
}
