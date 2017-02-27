package javagiac;
// This is based on runme.java. Just a playground to test various hacks with Giac conveniently.

public class test {
  static {
    try {
	System.out.println("Loading giac java interface");
        //System.load("c:\\cygwin\\usr\\local\\lib\\javagiac.dll");
        //System.load("/home/kovzol/workspace/desktop/lib/libjavagiac64.so");
		System.loadLibrary("javagiac64");
    } catch (UnsatisfiedLinkError e) {
    	e.printStackTrace();
      System.err.println("Native code library failed to load. See the chapter on Dynamic Linking Problems in the SWIG Java documentation for help.\n" + e);
      System.exit(1);
    }
  }

  public static void main(String argv[]) 
  {
    context C=new context();
    String s;
    /*
    s=new String("[[1],[ii:=gbasis(subst([v9*v4+-1*v10*v3+-1*v9*v2+v3*v2+v10*v1+-1*v4*v1,v11*v6+-1*v12*v5+-1*v11*v2+v5*v2+v12*v1+-1*v6*v1," + 
    		"v13*v8+-1*v14*v7+-1*v13*v2+v7*v2+v14*v1+-1*v8*v1,v15*v8+-1*v16*v7+-1*v15*v6+v7*v6+v16*v5+-1*v8*v5," + 
    		"v15*v14+-1*v16*v13+-1*v15*v12+v13*v12+v16*v11+-1*v14*v11,-1*v17*v8+v18*v7+v17*v4+-1*v7*v4+-1*v18*v3+v8*v3," + 
    		"-1*v17*v14+v18*v13+v17*v10+-1*v13*v10+-1*v18*v9+v14*v9,v19*v6+-1*v20*v5+-1*v19*v4+v5*v4+v20*v3+-1*v6*v3," + 
    		"v19*v12+-1*v20*v11+-1*v19*v10+v11*v10+v20*v9+-1*v12*v9,-1+-1*v21*v19*v18+v21*v20*v17+v21*v19*v16+-1*v21*v17*v16+-1*v21*v20*v15+v21*v18*v15]," +
    		"[v1=0,v2=0,v3=0,v4=1]),[v1,v2,v3,v4,v5,v6,v7,v8,v9,v11,v13,v10,v12,v14,v15,v17,v16,v19,v18,v21,v20],revlex)],(ii[0]!=1)&&(ii[0]!=-1)][2]");
    		*/
    //s=new String("normal(regroup((when(type(723394816*x^4 + 970407680*x^3*y + 2893579264*x^3 - 49757600*x^2*y^2 + 2911223040*x^2*y + 4340368896*x^2 - 251658000*x*y^3 - 99515200*x*y^2 + 2911223040*x*y + 2893579264*x - 1988189375*y^4 + 6429177200*y^3 - 5528042464*y^2 + 970407680*y = -723394816)==DOM_LIST,map(723394816*x^4 + 970407680*x^3*y + 2893579264*x^3 - 49757600*x^2*y^2 + 2911223040*x^2*y + 4340368896*x^2 - 251658000*x*y^3 - 99515200*x*y^2 + 2911223040*x*y + 2893579264*x - 1988189375*y^4 + 6429177200*y^3 - 5528042464*y^2 + 970407680*y = -723394816,left),left(723394816*x^4 + 970407680*x^3*y + 2893579264*x^3 - 49757600*x^2*y^2 + 2911223040*x^2*y + 4340368896*x^2 - 251658000*x*y^3 - 99515200*x*y^2 + 2911223040*x*y + 2893579264*x - 1988189375*y^4 + 6429177200*y^3 - 5528042464*y^2 + 970407680*y = -723394816)))-(when(type(723394816*x^4 + 970407680*x^3*y + 2893579264*x^3 - 49757600*x^2*y^2 + 2911223040*x^2*y + 4340368896*x^2 - 251658000*x*y^3 - 99515200*x*y^2 + 2911223040*x*y + 2893579264*x - 1988189375*y^4 + 6429177200*y^3 - 5528042464*y^2 + 970407680*y = -723394816)==DOM_LIST,map(723394816*x^4 + 970407680*x^3*y + 2893579264*x^3 - 49757600*x^2*y^2 + 2911223040*x^2*y + 4340368896*x^2 - 251658000*x*y^3 - 99515200*x*y^2 + 2911223040*x*y + 2893579264*x - 1988189375*y^4 + 6429177200*y^3 - 5528042464*y^2 + 970407680*y = -723394816,right),right(723394816*x^4 + 970407680*x^3*y + 2893579264*x^3 - 49757600*x^2*y^2 + 2911223040*x^2*y + 4340368896*x^2 - 251658000*x*y^3 - 99515200*x*y^2 + 2911223040*x*y + 2893579264*x - 1988189375*y^4 + 6429177200*y^3 - 5528042464*y^2 + 970407680*y = -723394816)))))");
    //s=new String("normal(regroup((when(type(100000000*x^4 - 474000000*x^3*y + 400000000*x^3 + 709690000*x^2*y^2 - 1422000000*x^2*y + 600000000*x^2 - 350760000*x*y^3 + 1419380000*x*y^2 - 1422000000*x*y + 400000000*x - 247302961*y^4 + 253365922*y^3 + 407627039*y^2 - 474000000*y = -100000000)==DOM_LIST,map(100000000*x^4 - 474000000*x^3*y + 400000000*x^3 + 709690000*x^2*y^2 - 1422000000*x^2*y + 600000000*x^2 - 350760000*x*y^3 + 1419380000*x*y^2 - 1422000000*x*y + 400000000*x - 247302961*y^4 + 253365922*y^3 + 407627039*y^2 - 474000000*y = -100000000,left),left(100000000*x^4 - 474000000*x^3*y + 400000000*x^3 + 709690000*x^2*y^2 - 1422000000*x^2*y + 600000000*x^2 - 350760000*x*y^3 + 1419380000*x*y^2 - 1422000000*x*y + 400000000*x - 247302961*y^4 + 253365922*y^3 + 407627039*y^2 - 474000000*y = -100000000)))-(when(type(100000000*x^4 - 474000000*x^3*y + 400000000*x^3 + 709690000*x^2*y^2 - 1422000000*x^2*y + 600000000*x^2 - 350760000*x*y^3 + 1419380000*x*y^2 - 1422000000*x*y + 400000000*x - 247302961*y^4 + 253365922*y^3 + 407627039*y^2 - 474000000*y = -100000000)==DOM_LIST,map(100000000*x^4 - 474000000*x^3*y + 400000000*x^3 + 709690000*x^2*y^2 - 1422000000*x^2*y + 600000000*x^2 - 350760000*x*y^3 + 1419380000*x*y^2 - 1422000000*x*y + 400000000*x - 247302961*y^4 + 253365922*y^3 + 407627039*y^2 - 474000000*y = -100000000,right),right(100000000*x^4 - 474000000*x^3*y + 400000000*x^3 + 709690000*x^2*y^2 - 1422000000*x^2*y + 600000000*x^2 - 350760000*x*y^3 + 1419380000*x*y^2 - 1422000000*x*y + 400000000*x - 247302961*y^4 + 253365922*y^3 + 407627039*y^2 - 474000000*y = -100000000)))))");
    //s=new String("1+2");
    String v1 = "v28,v36,v35,v34,v33,v31,v32,v30,v29,v27,v40";
		// String v2 = "v40,v36,v35,v34,v33,v32,v31,v30,v29,v27,v28";
		//
		// String w1 = "v16,v15,v21,v14,v13,v12,v11,v10,v9,v7,v8";
		// String w2 = "v9,v10,v7,v8,v16,v21,v15,v14,v12,v13,v11";
    
    /*
    s=new String("[[1],[ff:=\"\"],[aa:=eliminate([-1*v28+-1*v25+v24,-1*v27+v26+v23,-1*v30+v26+v23,-1*v29+v25+-1*v24,v31*v28+-1*v32*v27+-1*v31*v24+v27*v24+v32*v23+-1*v28*v23,v31*v30+-1*v32*v29+-1*v31*v26+v29*v26+v32*v25+-1*v30*v25,-1*v34+v25+-1*v23,-1*v33+-1*v26+v24,v35*v34+-1*v36*v33,v35*v30+-1*v36*v29+-1*v35*v26+v29*v26+v36*v25+-1*v30*v25,-1+-1*v40*v35+v40*v31],[" +
    		v2 +
    		"])],[bb:=size(aa)],[for ii from 0 to bb-1 do ff+=(\"[\"+(ii+1)+\"]: [1]:  _[1]=1\");cc:=factors(aa[ii]);dd:=size(cc);for jj from 0 to dd-1 by 2 do ff+=(\"  _[\"+(jj/2+2)+\"]=\"+cc[jj]); od; ff+=(\" [2]: \"+cc[1]);for kk from 1 to dd-1 by 2 do ff+=(\",\"+cc[kk]);od;od],ff][5]");
    */
    /*
    s="[[1],[ff:=\"\"],[aa:=eliminate([-1*v8+-1*v5+v4,-1*v7+v6+v3,-1*v10+v6+v3,-1*v9+v5+-1*v4,v11*v8+-1*v12*v7+-1*v11*v4+v7*v4+v12*v3+-1*v8*v3,v11*v10+-1*v12*v9+-1*v11*v6+v9*v6+v12*v5+-1*v10*v5,-1*v14+v5+-1*v3,-1*v13+-1*v6+v4,v15*v14+-1*v16*v13,v15*v10+-1*v16*v9+-1*v15*v6+v9*v6+v16*v5+-1*v10*v5,-1+-1*v21*v16+v21*v12],[" +
    		w2 +
    		"])],[bb:=size(aa)],[for ii from 0 to bb-1 do ff+=(\"[\"+(ii+1)+\"]: [1]:  _[1]=1\");cc:=factors(aa[ii]);dd:=size(cc);for jj from 0 to dd-1 by 2 do ff+=(\"  _[\"+(jj/2+2)+\"]=\"+cc[jj]); od; ff+=(\" [2]: \"+cc[1]);for kk from 1 to dd-1 by 2 do ff+=(\",\"+cc[kk]);od;od],ff][5]";
    */
    s="eliminate([-1*v28+-1*v25+v24,-1*v27+v26+v23,-1*v30+v26+v23,-1*v29+v25+-1*v24,v31*v28+-1*v32*v27+-1*v31*v24+v27*v24+v32*v23+-1*v28*v23,v31*v30+-1*v32*v29+-1*v31*v26+v29*v26+v32*v25+-1*v30*v25,-1*v34+v25+-1*v23,-1*v33+-1*v26+v24,v35*v34+-1*v36*v33,v35*v30+-1*v36*v29+-1*v35*v26+v29*v26+v36*v25+-1*v30*v25,-1+-1*v40*v35+v40*v31],["
    		+ v1 +
    		"]))";
    s="eliminate([v107+-1*v105+-1*v103,v108+-1*v106+-1*v104,v109+v105+-1*v103,v110+v106+-1*v104,v111*v108+-1*v112*v107+-1*v111*v106+v107*v106+v112*v105+-1*v108*v105,v111*v110+-1*v112*v109,-1*v113*v106+v114*v105,-1*v113*v112+v114*v111+v113*v104+-1*v111*v104+-1*v114*v103+v112*v103,2*v115+-1*v105,2*v116+-1*v106,-1+-1*v121*v116+v121*v114],[v121,v116,v115,v114,v113,v112,v111,v110,v109,v108,v107])]";
    
    String z1 = "v16,v12,v11,v10,v9,v8,v7,v6,v5";
    String z2 = "v16,v11,v10,v9,v8,v7,v6,v5,v12";
    
    s="eliminate([2*v5+-1*v3,2*v6+-1*v4,2*v7+-1*v3,2*v8+-1*v4,v10+-1*v8+-1*v7,v9+v8+-1*v7,-1*v11*v10+v12*v9+v11*v8+-1*v9*v8+-1*v12*v7+v10*v7,-1*v11*v4+v12*v3,-1+-1*v16*v12+v16*v6],[" + 
    		z1
    		+ "])]";
    
    String i = "2*v5+-1*v3,2*v6+-1*v4,2*v7+-1*v3,2*v8+-1*v4,v10+-1*v8+-1*v7,v9+v8+-1*v7,-1*v11*v10+v12*v9+v11*v8+-1*v9*v8+-1*v12*v7+v10*v7,-1*v11*v4+v12*v3,-1+-1*v16*v12+v16*v6";
    s="gbasis([" + i + "],[v16,v12,v11,v10,v9,v8,v7,v6,v5],revlex)";
    s="degree([[a:=gbasis([" + i + "],[v16,v11,v10,v9,v8,v7,v6,v5,v12],revlex)],a][1][1],v6)";

    s="contains_vars(poly,var_list):={local ii; for (ii:=0; ii<size(var_list); ii++) { if (degree(poly,var_list[ii])>0) { return true } } return false}; contains_vars(c^2+a,[x,y,z,c]);" +
    		"my_eliminate(poly_list,var_list):={local ii,jj,kk; kk:=[]; jj:=gbasis(poly_list,var_list,revlex); for (ii:=0; ii<size(jj); ii++) { if (!contains_vars(jj[ii],var_list)) { kk:=append(kk,jj[ii]) } } return kk }; " +
    		"my_eliminate([" + i + "],[" + z2 + "])";
    
    gen g=new gen("caseval(\"close geogebra\")",C);
    // gen g=new gen("caseval(\"init geogebra\")",C);
    g=g.eval(1,C);
    System.out.println(g.print(C));
    g=new gen("caseval(\"timeout 4\")",C);
    g=g.eval(1,C);
    System.out.println(g.print(C));
    g=new gen("proba_epsilon:=0",C);
    g=g.eval(1,C);
    System.out.println(g.print(C));
    g=new gen(s,C);
    System.out.println(g.eval(1,C).print(C));
    System.out.println( "Goodbye" );
  }
}
