package geogebra.common.cas.mpreduce;

import geogebra.common.cas.Evaluate;
import geogebra.common.main.App;

public class ReduceLibrary {
	private Evaluate eval;

	public ReduceLibrary(Evaluate mpreduce1) {
		eval = mpreduce1;
	}

	private String eval(String command) throws Throwable {
		return eval.evaluate(command);
	}

	public void load() throws Throwable {
		eval("off nat;");
		eval("off pri;");

		eval("off numval;");
		eval("linelength 50000;");
		eval("scientific_notation {16,5};");
		eval("on fullroots;");	
		// set default switches Solve[{x+y=2,x=y}]
		// (note: off factor turns on exp, so off exp must be placed later)
		eval("numrules := {exp(~t*i)=>cos(~t)+i*sin(~t)};");
		eval("procedure resetsettings(keepin,taystd,curx,cury);begin;"
				+ "keepinput!!:=keepin;taylortostd:=taystd;"
				+ "currentx!!:=curx;currenty!!:=cury;"
				+ "numeric!!:=0$ precision 30; "
				+ "clearrules numrules;"
				+ "print_precision 16; "
				+ "off nat, allfac,revpri, complex, rounded, numval, factor, div; "
				+ "off combinelogs, expandlogs, combineexpt,rational,rationalize;"
				+ "on pri;" + "return 1;" + "end;");
		eval("procedure degasin(x); asin(x)*180/pi*'\u00b0;");
		eval("procedure degatan(x); atan(x)*180/pi*'\u00b0;");
		eval("procedure degacos(x); acos(x)*180/pi*'\u00b0;");
		eval("let { sin(~a*'\u00b0)=>sin(a*pi/180),"
				+ "cos(~a*'\u00b0)=>cos(a*pi/180),"
				+ "tan(~a*'\u00b0)=>tan(a*pi/180),"
				+ "cot(~a*'\u00b0)=>cot(a*pi/180),"
				+ "sec(~a*'\u00b0)=>sec(a*pi/180),"
				+ "csc(~a*'\u00b0)=>csc(a*pi/180),"
				+ "sin(\u00b0/~a)=>sin(1/a*pi/180),"
				+ "cos(\u00b0/~a)=>cos(1/a*pi/180),"
				+ "tan(\u00b0/~a)=>tan(1/a*pi/180),"
				+ "cot(\u00b0/~a)=>cot(1/a*pi/180),"
				+ "sec(\u00b0/~a)=>sec(1/a*pi/180),"
				+ "csc(\u00b0/~a)=>csc(1/a*pi/180),"
				+ "sin(~b*\u00b0/~a)=>sin(b/a*pi/180),"
				+ "cos(~b*\u00b0/~a)=>cos(b/a*pi/180),"
				+ "tan(~b*\u00b0/~a)=>tan(b/a*pi/180),"
				+ "cot(~b*\u00b0/~a)=>cot(b/a*pi/180),"
				+ "sec(~b*\u00b0/~a)=>sec(b/a*pi/180),"
				+ "csc(~b*\u00b0/~a)=>csc(b/a*pi/180)}");
		eval("intrules!!:={"
				+ "int(~w/~x,~x) => w*log(abs(x)) when freeof(w,x),"
				+ "int(~w/(~x+~a),~x) => w*log(abs(x+a)) when freeof(w,x) and freeof(a,x),"
				+ "int((~b*~x+~w)/(~x+~a),~x) => int((b*x)/(x+a),x)+w*log(abs(x+a)) when freeof(w,x) and freeof(a,x) and freeof(b,x),"
				+ "int((~a*~x+~w)/~x,~x) => int(a,x)+w*log(abs(x)) when freeof(w,x) and freeof(a,x),"
				+ "int((~x+~w)/~x,~x) => x+w*log(abs(x)) when freeof(w,x),"
				+ "int(tan(~x),~x) => log(abs(sec(x))),"
				+ "int(~w*tan(~x),~x) => w*log(abs(sec(x))) when freeof(w,x),"
				+ "int(~w+tan(~x),~x) => int(w,x)+log(abs(sec(x))),"
				+ "int(~a+~w*tan(~x),~x) => int(a,x)+w*log(abs(sec(x))) when freeof(w,x),"
				+ "int(cot(~x),~x) => log(abs(sin(x))),"
				+ "int(~w*cot(~x),~x) => w*log(abs(sin(x))) when freeof(w,x),"
				+ "int(~a+cot(~x),~x) => int(a,x)+log(abs(sin(x))),"
				+ "int(~a+~w*cot(~x),~x) => int(a,x)+w*log(abs(sin(x))) when freeof(w,x),"
				+ "int(sec(~x),~x) => -log(abs(tan(x / 2) - 1)) + log(abs(tan(x / 2) + 1)),"
				+ "int(~w*sec(~x),~x) => -log(abs(tan(x / 2) - 1))*w + log(abs(tan(x / 2) + 1) )*w when freeof(w,x),"
				+ "int(~w+sec(~x),~x) => -log(abs(tan(x / 2) - 1)) + log(abs(tan(x / 2) + 1) )+int(w,x),"
				+ "int(~a+w*sec(~x),~x) => -log(abs(tan(x / 2) - 1))*w + log(abs(tan(x / 2) + 1) )*w+int(a,x) when freeof(w,x),"
				+ "int(csc(~x),~x) => log(abs(tan(x / 2))),"
				+ "int(~w*csc(~x),~x) => w*log(abs(tan(x / 2))) when freeof(w,x),"
				+ "int(~w+csc(~x),~x) => int(w,x)+log(abs(tan(x / 2))),"
				+ "int(~a+~w*csc(~x),~x) => int(a,x)+w*log(abs(tan(x / 2))) when freeof(w,x)"
				+ "};");
		eval("procedure islist(n);"
				+ "if arglength(n)>-1 and part(n,0)='list then 1 else 0;");
		eval("operator iffun;");
		eval("operator ifelsefun;");
		eval("let { ifelsefun(~x,~a,~b) => ~a when x='true,  ifelsefun(~x,~a,~b) => ~b when x='false, "
				+ "iffun(~x,~a) => ~a when x='true, iffun(~x,~a) => '? when x='false," +
				"df(iffun(~cond,~f),~x)=>iffun(cond,df(f,x)),df(ifelsefun(~cond,~f,~g),~x)=>iffun(cond,df(f,x),df(g,x))}");
		eval("let {abs(pi)=>pi,abs(e)=>e,sign(pi)=1,sign(e)=1," + "sqrt(~a)*sqrt(~b)=>sqrt(a*b)};");
		String xBig=" mynumberp(~x)='true and mycompare(~x,1)>0 ";
		String xNear0="mynumberp(~x)='true and mycompare(abs(~x),1)<0";
		String xFarFrom0="mynumberp(~x)='true and mycompare(abs(~x),1)>0";
		String xJustAbove0="mynumberp(~x)='true and mycompare(~x,0)>0 and mycompare(~x,1)<0";
		App.debug(eval("let { limit(~x^~n,~n,infinity) => infinity when "+xBig+","
				+" limit(-~x^~n,~n,infinity) => -infinity when "+xBig+","
				+ "	limit(~a*~x^~n,~n,infinity) => a*infinity when "+xBig+" and numberp(~a) and not (a=0),"
				+ "	limit(~x^~n/~b,~n,infinity) => b*infinity when "+xBig+" and numberp(~b) and not (b=0),"
				+ "	limit(~a*~x^~n/~b,~n,infinity) => a*b*infinity when "+xBig+" and numberp(~a) and numberp(~b) and not (a*b=0),"

				+ "	limit(~x^~n,~n,infinity) => 0 when "+xNear0+","
				+ "	limit(-~x^~n,~n,infinity) => 0 when "+xNear0+","
				+ "	limit(~a*~x^~n,~n,infinity) => 0 when "+xNear0+" and numberp(~a) and not(~a=infinity or ~a=-infinity),"
				+ "	limit(~x^~n/~b,~n,infinity) => 0 when "+xNear0+" and numberp(~b) and not(~b=0),"
				+ "	limit(~a*~x^~n/~b,~n,infinity) => 0 when "+xNear0+" and numberp(~a) and numberp(~b) and not(~a=infinity or ~a=-infinity) and not(~b=0),"

				+ "	limit(~x^~n,~n,-infinity) => 0 when "+xFarFrom0+","
				+ "	limit(-~x^~n,~n,-infinity) => 0 when "+xFarFrom0+","
				+ "	limit(~a*~x^~n,~n,-infinity) => 0 when "+xFarFrom0+" and numberp(~a) and not(~a=infinity or ~a=-infinity),"
				+ "	limit(~x^~n/~b,~n,-infinity) => 0 when "+xFarFrom0+" and numberp(~b) and not(~b=0),"
				+ "	limit(~a*~x^~n/~b,~n,-infinity) => 0 when "+xFarFrom0+" and numberp(~a) and numberp(~b) and not(~a=infinity or ~a=-infinity) and not(~b=0),"

				+ "	limit(~x^~n,~n,-infinity) => infinity when "+xJustAbove0+","
				+ "	limit(-~x^~n,~n,-infinity) => -infinity when "+xJustAbove0+","
				+ "	limit(~a*~x^~n,~n,-infinity) => infinity when "+xJustAbove0+" and numberp(~a) and ~a>0,"
				+ "	limit(~a*~x^~n,~n,-infinity) => -infinity when "+xJustAbove0+" and numberp(~a) and ~a<0,"
				+ "	limit(~x^~n/~b,~n,-infinity) => infinity when "+xJustAbove0+" and numberp(~b) and ~b>0,"
				+ "	limit(~x^~n/~b,~n,-infinity) => -infinity when "+xJustAbove0+" and numberp(~b) and ~b<0,"
				+ "	limit(~a*~x^~n/~b,~n,-infinity) => infinity when "+xJustAbove0+" and numberp(~a) and numberp(~b) and ((~a>0 and ~b>0) or (~a<0 and ~b<0)),"
				+ "	limit(~a*~x^~n/~b,~n,-infinity) => -infinity when "+xJustAbove0+" and numberp(~a) and numberp(~b) and ((~a<0 and ~b>0) or (~a>0 and ~b<0))}"));
		
		eval("let {impart(arbint(~w)) => 0, arbint(~w)*i =>  0};");
		eval("let {atan(sin(~x)/cos(~x))=>x, " + "acos(1/sqrt(2)) => pi/4,"
				+ "factorial(~n) => gamma(n+1)};");

		eval("solverules:={" + "logb(~x,~b)=>log(x)/log(b),"
				+ "log10(~x)=>log(x)/log(10)" + "};");
		eval("procedure mkconditions(xx,yy,pts);for each el in mkdepthone(list(pts)) collect list(xx=xcoord(el),yy=ycoord(el));");
		eval("procedure myatan2(y,x);"
				+ " begin scalar xinput, yinput;"
				+ " xinput:=x; yinput:=y;"
				+ " on rounded, roundall, numval;"
				+ " x:=x+0; y:=y+0;"
				+ " return "
				+ " if numberp(y) and numberp(x) then"
				+ "   if x>0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)>>"
				+ "   else if x<0 and y>=0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)+pi>>"
				+ "   else if x<0 and y<0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)-pi>>"
				+ "   else if x=0 and y>0 then <<if numeric!!=0 then off rounded, roundall, numval; pi/2>>"
				+ "   else if x=0 and y<0 then <<if numeric!!=0 then off rounded, roundall, numval; -pi/2>>"
				+ "   else if x=0 and y=0 then <<if numeric!!=0 then off rounded, roundall, numval; 0>>"
				+ "   else '?" + " else" + "   atan(y/x) end;");

		eval("procedure mycoeff(p,x);" + " begin scalar coefflist, bool!!;"
				+ " on ratarg;" + " coefflist:=coeff(p,x);" + " off ratarg;"
				+ " if 1=for each elem!! in coefflist product"
				+ "   if freeof(elem!!,x) then 1 else 0 then"
				+ "   return reverse(coefflist)" + " else" + "   return '?"
				+ " end;");

		eval("operator sless;" + "  operator sgreater;"
				+ "  operator slessequal;" + "  operator sgreaterequal;"
				+ "  operator sequal;" + "  operator sunequal;"
				+ "  operator snot;" + "  operator sand;" + "  operator sor;"
				+ "  operator simplies; operator mydeg;");

		App.debug(eval("let({sless(~arg1,~arg2) => 'true when mynumberp(arg1-arg2) = 'true and mycompare(arg1,arg2) = -1,"
				+ " sless(~arg1,~arg2) => 'false when mynumberp(arg1-arg2) = 'true and mycompare(arg1,arg2) > -1,"
				+ " sgreater(~arg1,~arg2) => 'true when mynumberp(arg1-arg2) = 'true and mycompare(arg1,arg2) = 1,"
				+ " sgreater(~arg1,~arg2) => 'false when mynumberp(arg1-arg2) = 'true and mycompare(arg1,arg2) < 1,"
				+ " slessequal(~arg1,~arg2) => 'true when mynumberp(arg1-arg2) = 'true and mycompare(arg1,arg2) < 1,"
				+ " slessequal(~arg1,~arg2) => 'false when mynumberp(arg1-arg2) = 'true and mycompare(arg1,arg2) = 1,"
				+ " sgreaterequal(~arg1,~arg2) => 'true when mynumberp(arg1-arg2) = 'true and mycompare(arg1,arg2) > -1,"
				+ " sgreaterequal(~arg1,~arg2) => 'false when mynumberp(arg1-arg2) = 'true and mycompare(arg1,arg2) = -1,"				
				+ " snot(~arg) => 'true when arg = 'false,"
				+ " snot(~arg) => 'false when arg = 'true,"
				+ " snot(sand(~a,~b)) => sor(snot(a),snot(b)),"
				+ " snot(sor(~a,~b)) => sand(snot(a),snot(b)),"
				+ " snot(simplies(~a,~b)) => sand(a,snot(b)),"
				+ " snot(sgreater(~a,~b)) => slessequal(a,b),"
				+ " snot(sless(~a,~b)) => sgreaterequal(a,b),"
				+ " snot(sgreaterequal(~a,~b)) => sless(a,b),"
				+ " snot(slessequal(~a,~b)) => sgreater(a,b),"
				+ " snot(sunequal(~a,~b)) => sequal(a,b),"
				+ " snot(equal(~a,~b)) => sunequal(a,b),"
				+ " sand(~arg1, ~arg2) => arg2 when arg1 = 'true,"
				+ " sand(~arg1, ~arg2) => arg1 when arg2 = 'true,"
				+ " sand(~arg1, ~arg2) => 'false when arg1 = 'false or arg2 ='false,"
				+ " sor(~arg1, ~arg2) => 'true when arg1 = 'true or arg2 ='true,"
				+ " sor(~arg1, ~arg2) => arg2 when arg1 = 'false,"
				+ " sor(~arg1, ~arg2) => arg1 when arg2 = 'false,"
				+ " simplies(~arg1, ~arg2) => 'true when arg1 = 'false or arg2 ='true,"
				+ " simplies(~arg1, ~arg2) => arg2 when arg1 = 'true,"
				+ " simplies(~arg1, ~arg2) => snot(arg1) when arg2 = 'false,"

				+ " sunequal(~arg1,~arg2) => myunequalvec(arg1,arg2) when myvecp(arg1) and myvecp(arg2),"
				+ " sunequal(~arg1,~arg2) => myunequallist(arg1,arg2) when arglength(arg1)>-1 and arglength(arg2)>-1 and part(arg1,0)='list and part(arg2,0)='list,"
				+ " sunequal(~arg1,~arg2) => 'false when subtraction(arg1,arg2)=0 or trigsimp(subtraction(arg1,arg2),combine)=0,"
				+ " sunequal(~arg1,~arg2) => 'true when mynumberp(subtraction(arg1,arg2))='true and not(mycompare(subtraction(arg1,arg2),0)=0),"
				+ " sunequal(~arg1,~arg2) => 'true when mynumberp(trigsimp(subtraction(arg1,arg2),combine)) = 'true and not (mycompare(trigsimp(subtraction(arg1,arg2),combine),0)=0),"

				+ " sequal(~arg1,~arg2) => myequalvec(arg1,arg2) when myvecp(arg1) and myvecp(arg2),"
				+ " sequal(~arg1,~arg2) => myequallist(arg1,arg2) when arglength(arg1)>-1 and arglength(arg2)>-1 and part(arg1,0)='list and part(arg2,0)='list,"
				+ " sequal(~arg1,~arg2) => 'true when subtraction(arg1,arg2)=0 or trigsimp(subtraction(arg1,arg2),combine)=0,"
				+ " sequal(~arg1,~arg2) => 'false when mynumberp(subtraction(arg1,arg2))='true and not(mycompare(subtraction(arg1,arg2),0)=0),"
				+ " sequal(~arg1,~arg2) => 'false when mynumberp(trigsimp(subtraction(arg1,arg2),combine)) = 'true and not (mycompare(trigsimp(subtraction(arg1,arg2),combine),0)=0)," +
				"" +
				"mydeg(~a,~c)=>'? when arglength(c)>-1,"+
				"mydeg(~a+~b,~c)=>max(mydeg(a,c),mydeg(b,c))," +
				"mydeg(~a-~b,~c)=>max(mydeg(a,c),mydeg(b,c))," +
				"mydeg(~a*~b,~c)=>mydeg(a,c)+mydeg(b,c),"+
				"mydeg(~a/~b,~c)=>mydeg(a,c) when freeof(b,c),"+
				"mydeg(~c,~c)=>1," +
				"mydeg(~a,allvars)=>1 when arglength(a)=-1,"+
				"mydeg(~a,~c)=>0 when freeof(a,c) and not (c=allvars),"+
				"mydeg(~a,~c)=>0 when mynumberp(a)='true,"+
				"mydeg(~a^~b,~c)=>mydeg(a,c)*b});"));
		eval("procedure computedeg(p,var); begin scalar d; d:=mydeg(p,var); " +
				"return if freeof(d,mydeg) then d else ?;end;");

		eval("operator myintoperator;");
		eval("ifelseintsimplifications := {"
                		                
		                /* integration rules */

		                /* the condition is fulfilled in the whole interval */

		                + "myintoperator(iffun(sless(~var,~b),~expression),~var,~lower,~upper) => myintoperator(~expression, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper < ~b and ~lower < ~b, "
		                + "myintoperator(iffun(sless(~b,~var),~expression),~var,~lower,~upper) => myintoperator(~expression, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper > ~b and ~lower > ~b, "
		                + "myintoperator(iffun(slessequal(~var,~b),~expression),~var,~lower,~upper) => myintoperator(~expression, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper <= ~b and ~lower <= ~b, "
		                + "myintoperator(iffun(slessequal(~b,~var),~expression),~var,~lower,~upper) => myintoperator(~expression, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper > ~b and ~lower > ~b, "
		                
		                + "myintoperator(iffun(sgreater(~var,~b),~expression),~var,~lower,~upper) => myintoperator(~expression, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper > ~b and ~lower > ~b, "
		                + "myintoperator(iffun(sgreater(~b,~var),~expression),~var,~lower,~upper) => myintoperator(~expression, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper < ~b and ~lower < ~b, "
		                + "myintoperator(iffun(sgreaterequal(~var,~b),~expression),~var,~lower,~upper) => myintoperator(~expression, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower >= ~b, "
		                + "myintoperator(iffun(sgreaterequal(~b,~var),~expression),~var,~lower,~upper) => myintoperator(~expression, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper <= ~b and ~lower <= ~b, "
		                
		                
		                + "myintoperator(ifelsefun(sless(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper < ~b and ~lower < ~b, "
		                + "myintoperator(ifelsefun(sless(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper > ~b and ~lower > ~b, "
		                + "myintoperator(ifelsefun(slessequal(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper <= ~b and ~lower <= ~b, "
		                + "myintoperator(ifelsefun(slessequal(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower >= ~b, "
		                
		                + "myintoperator(ifelsefun(sgreater(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper > ~b and ~lower > ~b, "
		                + "myintoperator(ifelsefun(sgreater(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper < ~b and ~lower < ~b, "
		                + "myintoperator(ifelsefun(sgreaterequal(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower >= ~b, "
		                + "myintoperator(ifelsefun(sgreaterequal(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper <= ~b and ~lower <= ~b, "
		                

		                /* the condition is NOT fulfilled in the whole interval */
		                
		                + "myintoperator(ifelsefun(sless(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower >= ~b, "
		                + "myintoperator(ifelsefun(sless(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper <= ~b and ~lower <= ~b, "
		                + "myintoperator(ifelsefun(slessequal(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper > ~b and ~lower > ~b, "
		                + "myintoperator(ifelsefun(slessequal(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper < ~b and ~lower < ~b, "
		                
		                + "myintoperator(ifelsefun(sgreater(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper <= ~b and ~lower <= ~b, "
		                + "myintoperator(ifelsefun(sgreater(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower >= ~b, "
		                + "myintoperator(ifelsefun(sgreaterequal(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper < ~b and ~lower < ~b, "
		                + "myintoperator(ifelsefun(sgreaterequal(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper > ~b and ~lower > ~b, "

		                
		                /* the condition is fulfilled in a part of the interval */
		                
		                + "myintoperator(ifelsefun(sless(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~b) + myintoperator(~expfalse, ~var, ~b, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower <= ~b, "
		                + "myintoperator(ifelsefun(sless(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~b) + myintoperator(~exptrue, ~var, ~b, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower <= ~b, "
		                + "myintoperator(ifelsefun(slessequal(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~b) + myintoperator(~expfalse, ~var, ~b, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower <= ~b, "
		                + "myintoperator(ifelsefun(slessequal(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~b) + myintoperator(~exptrue, ~var, ~b, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower <= ~b, "
		                

		                + "myintoperator(ifelsefun(sgreater(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~b) + myintoperator(~exptrue, ~var, ~b, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower <= ~b, "
		                + "myintoperator(ifelsefun(sgreater(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~b) + myintoperator(~expfalse, ~var, ~b, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower <= ~b, "
		                + "myintoperator(ifelsefun(sgreaterequal(~var,~b),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~expfalse, ~var, ~lower, ~b) + myintoperator(~exptrue, ~var, ~b, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower <= ~b, "
		                + "myintoperator(ifelsefun(sgreaterequal(~b,~var),~exptrue,~expfalse),~var,~lower,~upper) => myintoperator(~exptrue, ~var, ~lower, ~b) + myintoperator(~expfalse, ~var, ~b, ~upper) when numberp(~lower) and numberp(~upper) and numberp(~b) and ~upper >= ~b and ~lower <= ~b};");

		// tests whether two vectors are equal,
		// assumes that both arguments are vectors
		// , please check before using this function
		eval("procedure myequalvec(arg1, arg2);"
				+ " begin scalar ret;"
				+ "   return if not(dim(arg1)=dim(arg2)) then"
				+ "     'false"
				+ "   else"
				+ "     <<ret:='true;"
				+ "     for i:=0:dim(arg1)-1 do ret:= sand(ret, sequal(get(arg1,i),get(arg2,i)));"
				+ "     ret>>" + " end;");
		eval("procedure myunequalvec(arg1, arg2);"
				+ " begin scalar ret;"
				+ "   return if not(dim(arg1)=dim(arg2)) then"
				+ "     'false"
				+ "   else"
				+ "     <<ret:='false;"
				+ "     for i:=0:dim(arg1)-1 do ret:= sor(ret, sunequal(get(arg1,i),get(arg2,i)));"
				+ "     ret>>" + " end;");

		// tests whether two list are equal,
		// assumes that both arguments are lists
		// , please check before using this function
		eval(" procedure myequallist(arg1, arg2);" + " begin scalar ret;"
				+ "   return if not(length(arg1)=length(arg2)) then"
				+ "     'false" + "   else" + "     <<ret:='true;"
				+ "     for i:=1:length(arg1) do"
				+ "       ret:= sand(ret, sequal(part(arg1,i),part(arg2,i)));"
				+ "     ret>>" + " end;");
		eval(" procedure myunequallist(arg1, arg2);" + " begin scalar ret;"
				+ "   return if not(length(arg1)=length(arg2)) then"
				+ "     'false" + "   else" + "     <<ret:='false;"
				+ "     for i:=1:length(arg1) do"
				+ "       ret:= sor(ret, sunequal(part(arg1,i),part(arg2,i)));"
				+ "     ret>>" + " end;");

		eval("procedure mynumberp(arg);"
				+ "  begin scalar roundedon;"
				+ "  roundedon := if lisp(!*rounded) then 'true else 'false;"
				+ "  on rounded,numval;"
				+ "  return if numberp(arg) or arg=infinity or arg=-infinity then"
				+ "    <<if roundedon='false then off rounded,numval; 'true>>"
				+ "  else"
				+ "    <<if roundedon='false then off rounded,numval; 'false>>"
				+ "  end;");

		// assumption: arg1 is number, please check before using
		eval("procedure compareinfinity(arg1);"
				+ "  if arg1 = infinity then 0 else 1;");
		// assumption: arg1 and arg2 are numbers, please check before using
		eval("procedure mycompare(arg1, arg2);" + "  begin scalar roundedon;"
				+ "  roundedon := if lisp(!*rounded) then 'true else 'false;"
				+ "  on rounded,numval;"
				+ "  return if arg1 = infinity then compareinfinity(arg2) "
				+ "  else if arg1 = -infinity then -compareinfinity(-arg2) "
				+ "  else if arg2 = infinity then -compareinfinity(arg2) "
				+ "  else if arg2 = -infinity then compareinfinity(-arg2) "
				+ "    else if arg1-arg2<0 then"
				+ "    <<if roundedon='false then off rounded,numval; -1>>"
				+ "  else if arg1=arg2 then"
				+ "    <<if roundedon='false then off rounded,numval; 0>>"
				+ "  else"
				+ "    <<if roundedon='false then off rounded,numval; 1>>"
				+ "  end;");

		eval(" Degree := pi/180;");

		eval(" procedure myfoldif(op,arg1);" + " begin scalar ret;"
				+ "     arg1:=mattolistoflists(arg1);"
				+ "     ret:=if depth(arg1)>1 then '? else part(arg1,1);"
				+ "     for i:=2:length(arg1) do"
				+ "       ret:= if op(ret, part(arg1,i))='false then ret else "
				+ "  if op(ret, part(arg1,i))='true then part(arg1,i) else '?;"
				+ "     return ret;" + " end;");
		eval("procedure myround(x);" + "begin; "
				+ " roundedon := if lisp(!*rounded) then 'true else 'false; "
				+ " on numval, rounded; r:=floor(x+0.5);"
				+ " if roundedon='false then off numval, rounded; "
				+ " return r;end");
		eval("procedure myfloor(x);" + "begin; "
				+ " roundedon := if lisp(!*rounded) then 'true else 'false; "
				+ " if mynumberp(x)='true then on numval, rounded;"
				+ " r:=floor(x);"
				+ " if roundedon='false then off numval, rounded; "
				+ " return r;end");
		eval("procedure myceil(x);" + "begin; "
				+ " roundedon := if lisp(!*rounded) then 'true else 'false; "
				+ " if mynumberp(x)='true then on numval, rounded; "
				+ " r:=ceiling(x);"
				+ " if roundedon='false then off numval, rounded; "
				+ " return r;end");

		eval("procedure harmonic(n,m); for i:=1:n sum 1/(i**m);");
		eval("procedure uigamma(n,m); gamma(n)-igamma(n,m);");
		eval("procedure myarg(x);"
				+ " if arglength(x)>-1 and part(x,0)='list then myatan2(part(x,2), part(x,1)) "
				+ " else if arglength(x)>-1 and part(x,0)='mat then <<"
				+ "   clear x!!;"
				+ "   x!!:=x;"
				+ "   if row_dim(x!!)=1 then myatan2(x!!(1,2),x!!(1,1))"
				+ "   else if column_dim(x!!)=1 then myatan2(x!!(2,1),x!!(2,1))"
				+ "   else arg(x!!) >>" + " else myatan2(impart(x),repart(x));");
		eval("procedure polartocomplex(r,phi); r*(cos(phi)+i*sin(phi));");
		eval("procedure polartopoint!\u00a7(r,phi); myvect(r*cos(phi),r*sin(phi));");
		eval("procedure complexexponential(r,phi); r*(cos(phi)+i*sin(phi));");
		eval("procedure conjugate(x); conj(x);");
		eval("procedure myrandom(); " + " begin;"
				+ " roundedon := if lisp(!*rounded) then 'true else 'false;"
				+ " on rounded; "
				+ " ret!!:=random(100000001)/(random(100000000)+1);"
				+ " if(roundedon='false) then off rounded;" + " return ret!!;"
				+ " end;");
		eval("procedure gammaRegularized(a,x); igamma(a,x);");
		eval("procedure gamma2(a,x); gamma(a)*igamma(a,x);");
		eval("procedure beta3(a,b,x); beta(a,b)*ibeta(a,b,x);");
		eval("symbolic procedure isbound!! x; if get(x, 'avalue) then 1 else 0;");
		eval("procedure myappend(x,y);"
				+ "if arglength(x)>-1 and part(x,0)='list then append(x,{y}) else append({x},y)");
		eval("procedure mylength(x);"
				+ " if arglength(x)>-1 and part(x,0)='list then length(x) else sqrt(mydot(x,x));");
		eval("procedure mytangent(pt,f);"
				+ "currenty!!=sub(currentx!!=pt,f)+sub(currentx!!=pt,df(f,mymainvar(f)))*(currentx!!-(pt))");
		eval("procedure myabs(x);"
				+ " if arglength(x!!)>-1 and part(x,0)='list then abs(x)"
				+ " else if arglength(x)>-1 and part(x,0)='mat then <<"
				+ "   clear tmp;" + "   tmp:=x;"
				+ "   for i:=1:column_dim(x) do"
				+ "     for j:=1:row_dim(x) do" + "		  tmp(i,j):=myabs(tmp);"
				+ "   tmp>>" + " else if myvecp(x) then" + "   vmod x"
				+ " else if freeof(x,i) then abs(x)"
				+ " else sqrt(repart(x)^2+impart(x)^2);");

		eval("procedure flattenlist a;"
				+ "if part(a,0)='list and 1=for each elem!! in a product length(elem!!) then for each elem!! in a join elem!! else a;");

		eval("procedure depth a; if arglength(a)>0 and part(a,0)='list then 1+depth(part(a,1)) else 0;");

		eval("operator ggbinterval;");
		eval("procedure mkinterval(var,ineqtype,a,b);" + "begin;"
				+ "return ggbinterval(var,a,b,ineqtype);" + "end;");

		eval("procedure xcoord(a); if myvecp(a) then xvcoord(a) else xscoord(a)");
		eval("operator xscoord");
		eval("procedure ycoord(a); if myvecp(a) then yvcoord(a) else yscoord(a)");
		eval("operator yscoord");
		eval("procedure zcoord(a); if myvecp(a) then zvcoord(a) else zscoord(a)");
		eval("operator zscoord");

		eval("procedure booltonum a; if a = 'true then 1 else if a = 'false then 0 else a;");
		eval("procedure isnonzero(a);if a=0 or not freeof(a,i) then 0 else 1;");
		eval("procedure mynumsolvesingle(a,b); "
				+ " begin;scalar eqn, denumer,var;"
				+ " var:=b;"
				+ " if arglength(var)>-1 and part(var,0)='equal then var:=part(var,1);"
				+ " if arglength(a)>-1 and part(a,0)='equal then <<eqn:=num(lhs(a)-rhs(a));denumer:=den(lhs(a)-rhs(a))>>"
				+ "   else if length(a)=1 then <<eqn:=num(a); denumer:=den(a);>>;"
				+ " return if not(mycoeff(eqn,var)='?)"
				+ " then mkdepthone(for each r in roots(eqn) collect if freeof(r,i) and isnonzero(sub(r,denumer))=1 then list(r) else list())"
				+ " else if not(arglength(b)>-1 and part(b,0)='equal and sub(var=rhs(b),df(eqn,var))=0) then"
				+ " num_solve(a,b,iterations=10000)"
				+
				// tangent is horizontal, try going big enough step to the right
				// to make it steep
				" else if not(arglength(b)>-1 and part(b,0)='equal and sub(var=rhs(b)+0.5,df(eqn,var))=0) then"
				+ " num_solve(a,var=rhs(b)+0.5,iterations=10000)" +
				// tangent still horizontal, let Reduce do its random magic
				" else num_solve(a,var,iterations=10000);end;");
		eval("procedure mynumsolve(a,b); "
				+ " begin;"
				+ " a:=mkdepthone(list(a));"
				+ " b:=mkdepthone(list(b));"
				+ " return if length(a)=1 then mynumsolvesingle(part(a,1),part(b,1))"
				+ " else num_solve(a,b,iterations=10000);" + " end;");
		App.debug(eval("procedure expandfractions(a);begin scalar d,n,lst,ret;" +
				"if arglength(a)>-1 and part(a,0)='quotient then <<" +
				"d:=den(a);n:=num(a);if arglength(n)>-1 and part(n,0)='plus then <<" +
				"lst:=for k:=1:arglength(n) collect part(n,k)/d;" +
				" return part(lst,0):='ggbplus;>> >>;" +
				"return a; end; "));
		eval("procedure listtodisjunction(v,lst);" + "begin scalar ret;"
				+ "ret:=part(lst,1);"
				+ "for i:=2:length(lst) do ret:=sor(ret,part(lst,i));"
				+ "return ret; end;");
		eval("procedure logofstd(a);begin scalar r;r:=mycoeff(logof(a),logminusone);return if length(r)=1 then part(r,1) else"
				+ " part(r,2)+(if(fixp(part(r,1)/2)) then 0 else i * pi); end;");

		eval("procedure logof(a);"
				+ " if numberp(a) and a<0 then logminusone + log(-a) else "
				+ " if (arglength(a)>-1) and (part(a,0)='minus )then logminusone+logof(part(a,1)) else "
				+ " if (arglength(a)>-1) and (part(a,0)='expt )then logof(part(a,1))*part(a,2) else "
				+ " if (arglength(a)>-1) and (part(a,0)='times)then for k:=1:arglength(a) sum logof(part(a,k)) else "
				+ " if (arglength(a)>-1) and (part(a,0)='quotient )then logof(part(a,1))-logof(part(a,2)) else log(a);");
		// exptolin({7^(2*x-5)* 5^x = 9^(x+1)})
		eval("procedure exptolin(eqn);"
				+ " if arglength(eqn)>-1 and part(eqn,0)='quotient and numberp(part(eqn,2)) then  exptolin(part(eqn,1)) else "
				+ " if arglength(eqn)>-1 and part(eqn,0)='plus then (logofstd(for k:=2:arglength(eqn) sum part(eqn,k))-logofstd(-part(eqn,1))) "
				+ " else eqn;");
		eval("procedure bigexponents(eqn);"
				+ " if arglength(eqn) = -1 or numberp(eqn) then 0 else if part(eqn,0)='expt "
				+ " and numberp(part(eqn,2)) and part(eqn,2)> 16 then  1 else "
				+ " for k:=1:arglength(eqn) sum bigexponents(part(eqn,k));");
		// procedure posneg(equation): used to decide whether an equation can be
		// solved, based on positivity and negativity
		// e.g. sqrt(x)+1=0 -> sqrt(x) is positive, 1 is non-zero positive ->
		// their sum is not zero thus the solution is {}
		eval("procedure ptqst(op,el1,el2);"
				+ // returns a state of two elements with an operator of
					// plus/times/quotient
				"begin scalar i,j;"
				+ "if el1=idk or el2=idk then return idk;"
				+ "i:=if el1=nzpoz then 1 else if el1=poz then 2 else if el1=nzneg then 3 else if el1=neg then 4; "
				+ "j:=if el2=nzpoz then 1 else if el2=poz then 2 else if el2=nzneg then 3 else if el2=neg then 4; "
				+ "M!!:=if op='plus then mat((nzpoz,nzpoz,idk, idk),(nzpoz,poz,idk,idk),(idk,idk,nzneg,nzneg),(idk,idk,nzneg,neg))"
				+ "else mat((nzpoz, poz, nzneg, neg), (poz, poz, neg, neg), (nzneg, neg, nzpoz, poz), (neg, neg, poz, poz));"
				+ "return M!!(i,j);"
				+ "end;"
				+

				"procedure absst(st);"
				+ // returns the state of an element in absolute value
				"begin;"
				+ "return if st=nzneg or st=nzpoz then nzpoz else poz;"
				+ "end;"
				+

				"procedure expst(base,pow);"
				+ // return the state of an exponential element: base^pow
				"begin scalar i,j;"
				+ "i:=if base=nzpoz then 1 else if base=poz then 2 else if base=nzneg then 3 else if base=neg then 4 else 5;"
				+ "j:=if evenp(pow) then 1 else if fixp(pow) then 2 else if numberp(pow) then 3 else 4;"
				+ "if j=4 then return idk else if j=3 then return poz;"
				+ "M!!:=mat((nzpoz, nzpoz),(poz,poz),(nzpoz,nzneg),(poz,neg), (poz,idk));"
				+ "return M!!(i,j);"
				+ "end;"
				+

				"procedure inv(state);"
				+ // returns the inverse of an element
				"begin scalar ret;"
				+ "ret:=if state=neg then poz else if state=poz then neg else if state=nzpoz then nzneg "
				+ "    else if state=nzneg then nzpoz else if state=idk then idk;"
				+ "return ret;"
				+ "end;"
				+

				"procedure state(type, lista);"
				+ "begin scalar ret,el;"
				+ "ret:="
				+ "if type='minus then inv(part(lista,1))"
				+ "else if type='plus or type='times or type='quotient then <<"
				+ "  el:=part(lista,1);"
				+ "  for i:=2:arglength(lista) do el:=ptqst(type,el,part(lista,i));"
				+ "  el"
				+ ">> else if type='abs then absst(part(lista,1))"
				+ "  else if type='sqrt or type='expt then expst(part(lista,1),part(lista,2))"
				+ "  else idk;"
				+ "return ret;"
				+ "end;"
				+

				"procedure posneg(eqn);"
				+ "begin scalar ret;"
				+ "ret:={};"
				+ "if arglength(eqn)>-1 then <<"
				+ "  if part(eqn,0)='sqrt then ret:={posneg(part(eqn,1)),-2}"
				+ "  else if part(eqn,0)='expt then ret:={posneg(part(eqn,1)),part(eqn,2)}"
				+ "  else for i:=1:arglength(eqn) do ret:=posneg(part(eqn,i)).ret;"
				+ "  ret:=state(part(eqn,0),ret);"
				+ "  >>"
				+ "else if numberp(eqn) then if eqn>0 then ret:=nzpoz else if eqn<0 then ret:=nzneg else ret:=idk else ret:=idk;"
				+ "return ret;" + "end;");

		// used in issolvableineq
		eval("procedure isniceop(op,exp,base);"
				+ "begin scalar bool;"
				+ "bool:=(if (op='plus or op='minus or op='times or op='abs or op='quotient or op='log or (op='expt and (fixp(exp) or base=e or numberp(base)))) then 1 else 0);"
				+ "return bool;" + "end;");

		// determines if an expression looks like polynomial/polynomial or not
		eval("procedure issolvableineq(inequ);"
				+ "begin scalar jj, b;"
				+ "jj:=1; b:=1;"
				+ "if arglength(inequ)>-1 then"
				+ " while (b=1 and jj<=arglength(inequ)) do <<"
				+ "   b:=issolvableineq(part(inequ,jj));"
				+ "   jj:=jj+1;"
				+ "  >>;"
				+ "if not (arglength(inequ)=-1) and b then <<"
				+ "if arglength(inequ)=0 then b:=0"
				+ " else if arglength(inequ)=1 then b:=isniceop(part(inequ,0),0,0)"
				+ " else b:=isniceop(part(inequ,0),part(inequ,2),part(inequ,1));>>;"
				+ "return b;" + "end;");

		eval("procedure existingsolutions(eqn,sol);"
				+ "begin scalar ret!!, bool!!; ret!!:={};"
				+ "for each solution in sol do <<" + "  bool!!:=1;"
				+ "  for each eq in eqn do <<"
				+ "    if sub(solution,den(eq))=0 then bool!!:=0;"
				+ "    on expandlogs;"
				+ "    if sub(solution,num(eq)) neq 0 then bool!!:=0; "
				+ "    off expandlogs;>>;"
				+ "  if bool!! then ret!!:=(solution).ret!!;>>;"
				+ "return reverse ret!!;" + "end;");

		eval("procedure mysolve(eqn, var);"
				+ " begin scalar solutions!!, bool!!, isineq,temp1!!,temp2!!, max, other!!, isfraction;"
				+ "isineq:=0; multi:={};temp1!!:={}; temp2!!:={}; isfraction:=0;"
				+ " if part(eqn,0)='sgreater then <<ineqop:=part(eqn,0); ineq:=part(eqn,0):='greaterp; isineq:=1 >>;"
				+ " if part(eqn,0)='sgreaterequal then <<ineqop:=part(eqn,0); ineq:=part(eqn,0):='geq; isineq:=1>>;"
				+ " if part(eqn,0)='sless then <<ineqop:=part(eqn,0); ineq:=part(eqn,0):='lessp; isineq:=1>>;"
				+ " if part(eqn,0)='slessequal then <<ineqop:=part(eqn,0); ineq:=part(eqn,0):='leq; isineq:=1>>;"
				+ " if isineq then eqn:=lhs(ineq)=rhs(ineq);"
				+ " eqn:=mkdepthone({eqn});"
				+ " let solverules;"
				+ " if arglength(eqn)>-1 and part(eqn,0)='list then"
				+ "    eqn:=for each x in eqn collect"
				+ "      if freeof(x,=) then x else subtraction(lhs(x),rhs(x))"
				+ " else if freeof(eqn,=) then eqn else eqn:=subtraction(lhs(eqn),rhs(eqn));"
				// if the inequality is not like (polynomial/polynomial)
				// or its a system of inequalities then we return with {x=?}
				// (yet)
				+ " if arglength(eqn)=1 and isineq and not(issolvableineq(part(eqn,1))) then "
				+ "   return {{(if arglength(var)=1 and part(var,0)='list then part(var,1) else var)='?}};"
				+ " sign!!:={};if not(isineq) then <<for i:=1:arglength(eqn) do sign!!:=posneg(part(eqn,1)).sign!!;>>;"
				+ " if not(isineq) and (not(freeof(sign!!,nzpoz)) or not(freeof(sign!!,nzneg))) then return {};"
				+ " solutions!!:=if bigexponents(eqn)>0 then list() else solve(eqn,var);"
				// to prevent Solve command to yield non-existing solutions
				// such as solve({c*a^(-2)=15/4,c*a^(-4)=15/64},{a,c}) does
				// {a=0,c=0}
				+ " if not(isineq) then solutions!!:=existingsolutions(eqn,solutions!!);"
				// if it cannot solve the equation, numeric is off, and isineq
				// then we return {x=?}
				+ " if not (freeof(solutions!!,root_of)=t and freeof(solutions!!,one_of)=t) and numeric!!=0 and isineq then"
				+ "   return {{(if arglength(var)=1 and part(var,0)='list then part(var,1) else var)='?}};"
				+ " multi:=for j:=1:length(solutions!!) join {m=part(root_multiplicities,j)};"

				// single inequality solution begins"
				+ "if isineq then <<"
				+ " if not freeof(eqn,/) then isfraction:=1;"
				+ " densol:={};denmulti:={};" // solutions of the denominator(if
												// needed)
				+ " if isfraction then <<"
				+ "   densol:=solve(den(part(eqn,1)),var);"
				+ "   denmulti:=for j:=1:length(densol) join {m=part(root_multiplicities,j)}; >>;"
				+ " if arglength(var)>-1 and part(var,0)='list then var := part(var,1);"
				// Clear non-real solutions"
				+ " temp1!!:=for j:=1:length(solutions!!) join if freeof(part(solutions!!,j),'i) then {part(solutions!!,j)} else {};"
				+ " temp2!!:=for j:=1:length(solutions!!) join if freeof(part(solutions!!,j),'i) then {part(multi,j)} else {};"
				+ " temp3!!:=for j:=1:length(densol) join if freeof(part(densol,j),'i) then {part(densol,j)} else {};"
				+ " temp4!!:=for j:=1:length(densol) join if freeof(part(densol,j),'i) then {part(denmulti,j)} else {};"
				+ " solutions!!:=temp1!!; multi:=temp2!!; densol:=temp3!!; denmulti:=temp4!!;"
				+ "  nroots:=length(solutions!!)+length(densol);"
				+ "  if not (nroots=0) then << sol:=part(part(solutions!!,1),2);"
				+ "     if not freeof(sol,'i) or (arglength(sol)>-1 and part(sol,0)='arbreal) then <<solutions!!:={}; nroots:=0>>; >>;"

				// Case 1: the corresponding equation has no solution
				+ "if nroots = 0 then  <<"
				+ "if (ineqop='sless or ineqop='slessequal) and sub({var=0},part(eqn,1)) < 0 then solutions!!:={var=!*interval!*(-infinity,infinity,0)}"
				+ "else if (ineqop='sless or ineqop='slessequal) and sub({var=0},part(eqn,1)) > 0 then solutions!!:={}"
				+ "else if (ineqop='sgreater or ineqop='sgreaterequal) and sub({var=0},part(eqn,1)) > 0 then solutions!!:={var=!*interval!*(-infinity,infinity,0)}"
				+ "else if (ineqop='sgreater or ineqop='sgreaterequal) and sub({var=0},part(eqn,1)) < 0 then solutions!!:={};"
				+ ">> else "
				// Case 2: the corresponding equation has some solution
				+ "<<"
				+ "solutionset:={};"
				+ "for j:=1:length(solutions!!) do <<"
				+ "  solutionset:=append(solutionset,{part(part(solutions!!,j),2)});"
				+ "  if evenp(part(part(multi,j),2)) then "
				+ "    solutionset:=append(solutionset,{part(part(solutions!!,j),2)});"
				+ " >>;"
				+ "temp1!!:={};"
				+ "for j:=1:length(densol) do <<"
				+ "  solutionset:=append(solutionset,{part(part(densol,j),2)});"
				+ "  temp1!!:=append(temp1!!,{part(part(densol,j),2)});"
				+ "  if evenp(part(part(denmulti,j),2)) then "
				+ "    solutionset:=append(solutionset,{part(part(densol,j),2)});"
				+ " >>;"
				+ "densol:=temp1!!;"
				+ "solutionset:=mysortdec(solutionset); "
				+ "solutionset:=append({infinity},solutionset); solutionset:=append(solutionset,{-infinity});"
				+ "ineqsol:={};"
				+ "nmroots:=length(solutionset);"
				+ "max:=second(solutionset);"
				// we turn numeric temporarily on so that sub returns number for
				// var=sqrt(2)
				+ " on rounded, roundall, numval;"
				+ " if (ineqop='sless or ineqop='slessequal) and sub({var=max+1},part(eqn,1)) < 0 then start:=1"
				+ "  else if (ineqop='sless or ineqop='slessequal) and sub({var=max+1},part(eqn,1)) > 0 then start:=2"
				+ "  else if (ineqop='sgreater or ineqop='sgreaterequal) and sub({var=max+1},part(eqn,1)) > 0 then start:=1"
				+ "  else if (ineqop='sgreater or ineqop='sgreaterequal) and sub({var=max+1},part(eqn,1)) < 0 then start:=2;"
				+ " if numeric!!=0 then off rounded, roundall, numval; "
				+ "j:=start;"
				+ "while j+1<=nmroots do <<"
				+ "  type:=0;"
				+ "  if ineqop='sless or ineqop='sgreater then type:=0"
				+ "   else if mymember(part(solutionset,j+1), densol) and mymember(part(solutionset,j), densol) then type:=0"
				+ "   else if mymember(part(solutionset,j+1), densol) then type:=1"
				+ "   else if mymember(part(solutionset,j), densol) then type:=2"
				+ "   else type:=3;"
				+ "  if part(solutionset,j+1) neq part(solutionset,j) then "
				+ "   ineqsol:=append({var=!*interval!*(part(solutionset,j+1), part(solutionset,j), type)},ineqsol) "
				+ "    else if sand(sequal(part(solutionset,j+1),part(solutionset,j)), sequal(type,3))=true then"
				+ "    ineqsol:=append({var=(part(solutionset,j))},ineqsol);"
				+ "  j:=j+2;>>;"
				+ "solutions!!:=ineqsol; >>;"
				+ "if solutions!!={} then return {};"
				+ " >>; "

				// inequality solution ends
				+ "solutions!! := solvepostprocess(solutions!!,var);"
				+ " other!!:=list(0);"
				+ " if not (part(solutions!!,1)=1) then "
				+ " other!!:=solvepostprocess(solve(map(exptolin(~r),eqn),var),var);"
				+
				// may happen that other!! is "we don't know" and solutions!! is
				// "no answer"
				" return if part(other!!,1)=1 then part(other!!,2) else part(solutions!!,2);"
				+ " end;");
		eval("procedure simplifyexp(x);"
				+ " begin scalar y;"
				+
				// the second and third rule are for Solve[cubic]
				" exprules:={e^(log(~aa)/~bb)=>aa^(1/bb),e^(~u*acosh(~t)/3)=>(sqrt(t^2 - 1)+t)^(u/3),e^(acosh(~t)/3)=>(sqrt(t^2 - 1)+t)^(1/3)};"
				+ " let exprules;" + " y:=x;" + " clearrules exprules;"
				+ " return y;" + " end; ");

		eval("procedure solvepostprocess(solutions!!,var);"
				+ " begin scalar bool!!, isineq,temp1!!,temp2!!, max, noofstdsolutions;"

				+ "  if not(arglength(solutions!!)>-1 and part(solutions!!,0)='list) then solutions!!:={solutions!!};"
				+ "	 if depth(solutions!!)<2 then"
				+ "		solutions!!:=for each x in solutions!! collect {x};"

				+ "	 solutions!!:=for each sol in solutions!! join <<"
				+ "    bool!!:=1;"
				+ "    for each solution!! in sol do"
				+ "     if freeof(solution!!,'root_of) and freeof(solution!!,'one_of) and arglength(lhs(solution!!))=-1 then <<"
				+ "		   on rounded, roundall, numval, complex;"
				+ "		   if freeof(solution!!,'i) or aeval(impart(rhs(solution!!)))=0 then 1 else bool!!:=0;"
				+ "		   off complex;"
				+ "		   if numeric!!=0 then off rounded, roundall, numval"
				+ "      >>"

				+ "      else"
				+ "	       bool!!:=2*bool!!;"
				+ " 	firstsol!!:=part(sol,1);"
				+ "     if arglength(part(firstsol!!,2))>-1 and part(part(firstsol!!,2),0)=!*interval!* then  {{mkinterval(var,part(part(firstsol!!,2),3),part(part(firstsol!!,2),1),part(part(firstsol!!,2),2))}}"
				+ "    else if bool!!=1 then"
				+ "  	 {simplifyexp(sol)}"
				+ "	   else if bool!!>1 then"
				+ "  	 {{var='?}}"
				+ "    else "
				+ "		 {} >>;"
				+ "  clearrules solverules;"
				+ "  if solutions!!=list() then bool!!:=0;"
				+ "  return if isineq then list(1,listtodisjunction(var,flattenlist(mkset(solutions!!)))) else list(bool!!,mkset(solutions!!));"
				+ " end;");

		eval("procedure mysolve1(eqn);"
				+ " mysolve(eqn,mymainvars(eqn,length(mkdepthone({eqn}))));");
		eval("procedure mycsolve(eqn, var);"
				+ " begin scalar solutions!!, bool!!;"
				+ "  eqn:=mkdepthone({eqn});"
				+ "  let solverules;"
				+ "  if arglength(eqn)>-1 and part(eqn,0)='list then"
				+ "    eqn:=for each x in eqn collect"
				+ "      if freeof(x,=) then x else subtraction(lhs(x),rhs(x))"
				+ "  else if freeof(eqn,=) then 1 else eqn:=subtraction(lhs(eqn),rhs(eqn));"
				+ "    solutions!!:=solve(eqn,var);"
				+ "    if depth(solutions!!)<2 then"
				+ "      solutions!!:=for each x in solutions!! collect {x};"
				+ "    solutions!!:= for each sol in solutions!! join <<"
				+ "      bool!!:=1;"
				+ "      for each solution!! in sol do"
				+ "        if freeof(solution!!,'root_of) and freeof(solution!!,'one_of) then 1 else"
				+ "      		bool!!:=0;" + "      if bool!!=1 then"
				+ "        {sol}" + "      else if bool!!=0 then"
				+ "        {{var='?}}" + "      >>;"
				+ "  clearrules solverules;" + "  return mkset(solutions!!);"
				+ " end;");

		eval("procedure mycsolve1(eqn);"
				+ " mycsolve(eqn,mymainvars(eqn,length(mkdepthone({eqn}))));");

		eval("procedure mydot(vec1,vec2); "
				+ "	begin scalar tmplength; "
				+ "  if myvecp(vec1) and myvecp(vec2) then"
				+ "    return dot(vec1,vec2);"
				+ "  if arglength(vec1)>-1 and part(vec1,0)='mat and column_dim(vec1)=1 then "
				+ "    vec1:=tp(vec1);"
				+ "  if arglength(vec2)>-1 and part(vec2,0)='mat and column_dim(vec2)=1 then "
				+ "    vec2:=tp(vec2); "
				+ "  return  "
				+ "  if arglength(vec1)>-1 and part(vec1,0)='list then << "
				+ "    if arglength(vec2)>-1 and part(vec2,0)='list then  "
				+ "      <<tmplength:=length(vec1);  "
				+ "      for i:=1:tmplength  "
				+ "			sum part(vec1,i)*part(vec2,i) >> "
				+ "    else if arglength(vec2)>-1 and part(vec2,0)='mat and row_dim(vec2)=1 then"
				+ "      <<tmplength:=length(vec1);  "
				+ "      for i:=1:tmplength  "
				+ "	sum part(vec1,i)*vec2(1,i)>> "
				+ "      else "
				+ "	'? "
				+ "  >> "
				+ "  else <<if arglength(vec1)>-1 and part(vec1,0)='mat and row_dim(vec1)=1 then << "
				+ "    if arglength(vec2)>-1 and part(vec2,0)='list then  "
				+ "      <<tmplength:=length(vec2); "
				+ "      for i:=1:tmplength  "
				+ "			sum vec1(1,i)*part(vec2,i)>> "
				+ "    else if arglength(vec2)>-1 and part(vec2,0)='mat and row_dim(vec2)=1 then"
				+ "      <<tmplength:=column_dim(vec1);  "
				+ "      for i:=1:tmplength  " + "			sum vec1(1,i)*vec2(1,i) "
				+ "      >> " + "      else " + "		'? " + "    >> " + "  else "
				+ "    '? " + "  >> " + "end;");
		eval("procedure vandermonde2(variables);"
				+ "begin "
				+ "integer i,j,sq_size; "
				+ "sq_size := length variables; "
				+ "return <<"
				+ "matrix vand(sq_size,sq_size); "
				+ "for i:=1:sq_size do "
				+ "  for j:=1:sq_size do "
				+ "     vand(i,j):= "
				+ "           if part(variables,i)=0 and j=1 then 1 else part(variables,i)^(j-1); "
				+ "vand >>" + "end;");

		eval("procedure mycross(atmp,btmp); "
				+ "begin;"
				+ "  if myvecp(atmp) then"
				+ "    if myvecp(btmp) then"
				+ "      return cross(atmp,btmp)"
				+ "    else"
				+ "      return cross(atmp, listtomyvect btmp)"
				+ "  else if myvecp(btmp) then"
				+ "  return cross(listtomyvect atmp,btmp);"
				+ "  a:=atmp; b:= btmp;"
				+ "  if arglength(a)=-1 or (length(a) neq 3 and length(a) neq 2 and length(a) neq {1,3} and length(a) neq {3,1} and length(a) neq {1,2} and length(a) neq {2,1}) then return '?;"
				+ "  if arglength(b)=-1 or (length(b) neq 3 and length(b) neq 2 and length(b) neq {1,3} and length(b) neq {3,1} and length(b) neq {1,2} and length(b) neq {2,1}) then return '?;"
				+ "  if length(a)={1,3} or length(b)={1,2} then a:=tp(a);"
				+ "  if length(b)={1,3} or length(b)={1,2} then b:=tp(b);"
				+ "  return"
				+ "  if arglength(a)>-1 and part(a,0)='mat then <<"
				+ "    if arglength(b)>-1 and part(b,0)='mat then <<"
				+ "      if length(a)={3,1} and length(b)={3,1} then"
				+ "        mat((a(2,1)*b(3,1)-a(3,1)*b(2,1)),"
				+ "        (a(3,1)*b(1,1)-a(1,1)*b(3,1)),"
				+ "        (a(1,1)*b(2,1)-a(2,1)*b(1,1)))"
				+ "      else if length(a)={2,1} and length(b)={2,1} then"
				+ "        mat((0)," + "        (0),"
				+ "        (a(1,1)*b(2,1)-a(2,1)*b(1,1)))" + "      else '?"
				+ "    >> else if arglength(b)>-1 and part(b,0)='list then <<"
				+ "      if length(a)={3,1} and length(b)=3 then"
				+ "        list(a(2,1)*part(b,3)-a(3,1)*part(b,2),"
				+ "        a(3,1)*part(b,1)-a(1,1)*part(b,3),"
				+ "        a(1,1)*part(b,2)-a(2,1)*part(b,1))"
				+ "      else if length(a)={2,1} and length(b)=2 then"
				+ "        list(0," + "        0,"
				+ "        a(1,1)*part(b,2)-a(2,1)*part(b,1))"
				+ "      else '?" + "    >> else << '? >>"
				+ "  >> else if arglength(a)>-1 and part(a,0)='list then <<"
				+ "    if arglength(b)>-1 and part(b,0)='mat then <<"
				+ "      if length(a)=3 and length(b)={3,1} then"
				+ "        list(part(a,2)*b(3,1)-part(a,3)*b(2,1),"
				+ "        part(a,3)*b(1,1)-part(a,1)*b(3,1),"
				+ "        part(a,1)*b(2,1)-part(a,2)*b(1,1))"
				+ "      else if length(a)=2 and length(b)={2,1} then"
				+ "        list(0," + "        0,"
				+ "        part(a,1)*b(2,1)-part(a,2)*b(1,1))"
				+ "      else '?"
				+ "    >> else if arglength(b)>-1 and part(b,0)='list then <<"
				+ "      if length(a)=3 and length(b)=3 then"
				+ "        list(part(a,2)*part(b,3)-part(a,3)*part(b,2),"
				+ "        part(a,3)*part(b,1)-part(a,1)*part(b,3),"
				+ "        part(a,1)*part(b,2)-part(a,2)*part(b,1))"
				+ "      else if length(a)=2 and length(b)=2 then"
				+ "        list(0," + "        0,"
				+ "        part(a,1)*part(b,2)-part(a,2)*part(b,1))"
				+ "      else '?" + "    >> else << '? >>"
				+ "  >> else << '? >> " + "end;");

		eval("procedure mattoscalar(m);"
				+ " if length(m)={1,1} then trace(m) else m;");

		eval("procedure multiplication(a,b);"
				+ "begin"
				+ "  a:=booltonum(a);"
				+ "  b:=booltonum(b);"
				+ "  return if arglength(a)>-1 and part(a,0)='mat then"
				+ "    if arglength(b)>-1 and part(b,0)='mat then"
				+ "      mattoscalar(a*b)"
				+ "    else if arglength(b)>-1 and part(b,0)='list then"
				+ "      mattoscalar(a*<<listtocolumnvector(b)>>)"
				+ "    else"
				+ "      a*b"
				+ "  else if arglength(a)>-1 and part(a,0)='list then"
				+ "    if arglength(b)>-1 and part(b,0)='mat then"
				+ "      mattoscalar(<<listtorowvector(a)>>*b)"
				+ "    else if arglength(b)>-1 and part(b,0)='list then"
				+ "      for i:=1:length(a) collect part(a,i)*part(b,i)"
				+ "    else if myvecp(b) then"
				+ "		 listtomyvect(a)*b"
				+ "	   else"
				+ "      map(~w!!*b,a)"
				+ "  else if myvecp(a) and arglength(b)>-1 and part(b,0)='list then"
				+ "    a*listtomyvect(b)"
				+ "  else"
				+ "    if arglength(b)>-1 and part(b,0)='list then"
				+ "      map(a*~w!!,b)"
				+ "    else"
				+ "		 if a=infinity then"
				+ "		   if (numberp(b) and b>0) or b=infinity then infinity"
				+ "		   else if (numberp(b) and b<0) or b=-infinity then -infinity"
				+ "		   else '?"
				+ "		 else if a=-infinity then"
				+ "		   if (numberp(b) and b>0) or b=infinity then -infinity"
				+ "		   else if (numberp(b) and b<0) or b=-infinity then infinity"
				+ "		   else '?"
				+ "		 else if b=infinity then"
				+ "		   if (numberp(a) and a>0) or a=infinity then infinity"
				+ "		   else if (numberp(a) and a<0) or a=-infinity then -infinity"
				+ "		   else '?"
				+ "		 else if b=-infinity then"
				+ "		   if (numberp(a) and a>0) or a=infinity then -infinity"
				+ "		   else if (numberp(a) and a<0) or a=infinity then infinity"
				+ "		   else '?" + "		 else" + "        a*b;end");

		eval("procedure applyfunction(a,b);"
				+ "if(arglength(b)<0) then a(b) else "
				+ "if(part(b,0)='mat) then applyfunction(a,mattolistoflists(b))"
				+ "else if (part(b,0)='list) then for i:=1:length(b) "
				+ "collect applyfunction(a,part(b,i))" + "else a(b)");
		eval("procedure applyfunction2(a,b,p);"
				+ "if(arglength(b)<0) then a(b,p) else "
				+ "if(part(b,0)='mat) then applyfunction2(a,mattolistoflists(b),p)"
				+ "else if (part(b,0)='list) then for i:=1:length(b) "
				+ "collect applyfunction2(a,part(b,i),p)" + "else a(b,p)");

		eval("operator multiplication;");
		eval("procedure mydivision(a,b); "
				+ "multiplication(a,if b=0 then infinity"
				+ " else if (b=infinity or b=-infinity)"
				+ " then 0 else 1/booltonum(b))");
		eval("operator mydivision;");
		eval("procedure mypower(a,b); "
				+ "if b=0 and a=0 then 1 "
				+ " else if myvecp(a) then (if b=2 then multiplication(a,a) else '?)"
				+ " else if arglength(a)>-1 and part(a,0)='mat and numberp(b) and b<0 and det(a)=0 then mat(('?))"
				+ " else a^b;");
		eval("operator mypower;");

		eval("operator listtomyvect;");

		eval("procedure pointlist(lista);"
				+ "for each a in lista collect "
				+ "  if arglength(a)>-1 and freeof(a,i) and part(a,0)='equal then listtomyvect({rhs(a),0})"
				+ "  else if arglength(a)>-1 and part(a,0)='equal then listtomyvect({repart(rhs(a)),impart(rhs(a))})"
				+ "  else if (arglength(a)=-1 or (not (part(a,0)='list))) and freeof(a,i) then listtomyvect({a,0})"
				+ "  else if arglength(a)=-1 or (not (part(a,0)='list)) then listtomyvect({repart(a),impart(a)})"
				+ "  else if (arglength(part(a,1))>-1) and (part(part(a,1),0)='equal) then "
				+ "    <<begin tmp!!:=map(rhs,a); return listtomyvect(tmp!!); end>>"
				+ " else listtomyvect(a); ");
		eval("procedure rootlist(lista);"
				+ "for each a in lista collect if (arglength(a)>-1 and part(a,0)='equal) then "
				+ " listtomyvect({rhs(a),0}) else listtomyvect({a,0});");
		eval("operator objecttomyvect;");

		eval("procedure addition(a,b);"
				+ "begin"
				+ "  a:=booltonum(a);"
				+ "  b:=booltonum(b);"
				+ "  return if arglength(a)>-1 and part(a,0)='list and arglength(b)>-1 and part(b,0)='list then"
				+ "    for i:=1:length(a) collect addition(part(a,i),part(b,i))"
				+ "  else if arglength(a)>-1 and part(a,0)='list then"
				+ "    if myvecp(b) and length(a)>1 and not myvecp(part(a,1)) then"
				+ "      listtomyvect(a)+b"
				+ "    else"
				+ "      map(addition(~w!!,b),a)"
				+ "  else if arglength(b)>-1 and part(b,0)='list then"
				+ "    if myvecp(a)  and length(b)>1 and not myvecp(part(b,1)) then"
				+ "      listtomyvect(b)+a"
				+ "    else"
				+ "      map(addition(a,~w!!),b)"
				+ "else if (arglength(a)>-1 and part(a,0)='mat) or (arglength(b)>-1 and part(b,0)='mat) then"
				+ "   listofliststomat(addition(mattolistoflists(a),mattolistoflists(b)))"
				+ "  else if (a=infinity and b neq -infinity) or (b=infinity and a neq -infinity) then"
				+ "    infinity"
				+ "  else if (a=-infinity and b neq infinity) or (b=-infinity and a neq infinity) then"
				+ "    -infinity" + "  else" + "    a+b; end");

		eval("operator addition;");

		eval("procedure subtraction(a,b);"
				+ "begin"
				+ "  a:=booltonum(a);"
				+ "  b:=booltonum(b);"
				+ "  return if arglength(a)>-1 and part(a,0)='list and arglength(b)>-1 and part(b,0)='list then"
				+ "    for i:=1:length(a) collect part(a,i)-part(b,i)"
				+ "  else if arglength(a)>-1 and part(a,0)='list then"
				+ "    if myvecp b  and length(a)>1 and not myvecp(part(a,1)) then"
				+ "      listtomyvect(a)-b"
				+ "    else"
				+ "      map(~w!!-b,a)"
				+ "  else if arglength(b)>-1 and part(b,0)='list then"
				+ "    if myvecp(a)  and length(b)>1 and not myvecp(part(b,1)) then"
				+ "      a-listtomyvect(b)"
				+ "    else"
				+ "      map(a-~w!!,b)"
				+ "  else if (a=infinity and b neq infinity) or (b=-infinity and a neq -infinity) then "
				+ "    infinity"
				+ "  else if (a=-infinity and b neq -infinity) or (b=infinity and a neq infinity) then "
				+ "    -infinity" + "  else" + "    a-b;end");

		eval("operator subtraction;");

		eval("procedure myneq(a,b);snot myequal(a,b);");

		eval("procedure fractionalpart(a);ifelsefun(sgreater(a,0),a-myfloor(a),a-myceil(a))");
		eval("procedure myreal(a);if myvecp(a) then xvcoord(a) else repart(a)");
		eval("procedure imaginary(a);if myvecp(a) then yvcoord(a) else impart(a)");
		// erf in Reduce is currently broken:
		// http://sourceforge.net/projects/reduce-algebra/forums/forum/899364/topic/4546339
		// this is a numeric approximation according to Abramowitz & Stegun
		// 7.1.26.
		eval("procedure myerf(x); "
				+ "begin scalar a1!!, a2!!, a3!!, a4!!, a5!!, p!!, x!!, t!!, y!!, sign!!, result!!;"
				+ "     on rounded;"
				+ "  x:=booltonum(x);"
				+ "		if numberp(x) then 1 else return !*hold(erf(x));"
				+ "     if x=0 then return 0;"
				+ "     a1!! :=  0.254829592; "
				+ "     a2!! := -0.284496736; "
				+ "     a3!! :=  1.421413741; "
				+ "     a4!! := -1.453152027; "
				+ "     a5!! :=  1.061405429; "
				+ "     p!!  :=  0.3275911; "
				+ "     sign!! := 1; "
				+ "     if x < 0 then sign!! := -1; "
				+ "     x!! := Abs(x); "
				+ "     t!! := 1.0/(1.0 + p!!*x!!); "
				+ "     y!! := 1.0 - (((((a5!!*t!! + a4!!)*t!!) + a3!!)*t!! + a2!!)*t!! + a1!!)*t!!*Exp(-x!!*x!!); "
				+ "     result!! := sign!!*y!!;"
				+ "     if numeric!!=1 then off rounded;"
				+ "     return result!! " + "end;");

		eval("procedure mkdepthone(liste);"
				+ "	for each x in mattolistoflists(liste) join "
				+ "	if arglength(x)>-1 and (part(x,0)='list or part(x,0)='mat) then"
				+ "	mkdepthone(x) else {x};");

		eval("procedure listtocolumnvector(list); "
				+ "begin scalar lengthoflist; "
				+ "lengthoflist:=length(list); "
				+ "matrix m!!(lengthoflist,1); " + "for i:=1:lengthoflist do "
				+ "m!!(i,1):=part(list,i); " + "return m!! " + "end;");

		eval("procedure listtorowvector(list); "
				+ "begin scalar lengthoflist; "
				+ "	lengthoflist:=length(list); "
				+ "	matrix m!!(1,lengthoflist); "
				+ "	for i:=1:lengthoflist do " + "		m!!(1,i):=part(list,i); "
				+ "	return m!!; " + "end;");

		eval("procedure mod!!(a,b);" + " a-b*div(a,b);");

		eval("procedure div(a,b);" + " begin scalar a!!, b!!, result!!;"
				+ "  a!!:=a; b!!:=b;" + "  on rounded, roundall, numval;"
				+ "  return " + "  if numberp(a!!) and numberp(b!!) then <<"
				+ "    if numeric!!=0 then"
				+ "      off rounded, roundall, numval;" + "    if b!!>0 then "
				+ "	   floor(a/b)" + "    else" + "      ceiling(a/b)"
				+ "  >> else << " + "    if numeric!!=0 then"
				+ "      off rounded, roundall, numval;" + "    on rational;"
				+ "    result!!:=part(divide(a,b),1);" + "    off rational;"
				+ "    if numeric!!=1 then on rounded, roundall, numval;"
				+ "    result!!>>" + " end;");

		// to avoid using the package assist
		eval("procedure mkset a;" + " begin scalar result, bool;"
				+ "  result:=list();" + "  for each elem in a do <<"
				+ "  bool:=1;" + "  for each x in result do"
				+ "    if elem=x then bool:=0;" + "  if bool=1 then"
				+ "    result:=elem . result;" + "  >>;"
				+ "  return reverse(result)" + " end;");

		eval("procedure shuffle a;" + "begin scalar lengtha,s,tmp;"
				+ " lengtha:=length(a);" + " if lengtha>1 then"
				+ "  for i:=lengtha step -1 until 1 do <<"
				+ "   s:=random(i)+1;" + "   tmp:= part(a,i);"
				+ "   a:=(part(a,i):=part(a,s));" + "   a:=(part(a,s):=tmp);"
				+ "  >>;" + " return a " + "end;");

		eval("procedure listofliststomat(a); "
				+ " begin scalar length!!, bool!!, i!!, elem!!;"
				+ "  return"
				+ "  if arglength(a)>-1 and part(a,0)='list then <<"
				+ "    length!!:=-1;"
				+ "    bool!!:=1;"
				+ "    i!!:=0;"
				+ "    while i!!<length(a) and bool!!=1 do <<"
				+ "      i!!:=i!!+1;"
				+ "      elem!!:=part(a,i!!);"
				+ "      if arglength(elem!!)<0 or part(elem!!,0) neq 'list or (length(elem!!) neq length!! and length!! neq -1) then"
				+ "        bool!!:=0"
				+ "      else <<"
				+ "        length!!:=length(elem!!);"
				+ "        if 0=(for i:=1:length(elem!!) product if freeof(elem!!,=) then 1 else 0) then"
				+ "          bool!!:=0;" + "      >>" + "    >>;"
				+ "    if bool!!=0 or length(a)=0 then a" + "    else <<"
				+ "      matrix matrix!!(length(a),length(part(a,1)));"
				+ "      for i:=1:length(a) do"
				+ "        for j!!:=1:length(part(a,1)) do"
				+ "          matrix!!(i,j!!):=part(part(a,i),j!!);"
				+ "      matrix!!>>" + "    >>" + " else" + "    a;" + " end;");

		eval("procedure mattolistoflists(a);" + " begin scalar list!!, j!!;"
				+ "  tmpmatrix!!:=a;" + "  return"
				+ "  if arglength(a)<0 or part(a,0) neq 'mat then"
				+ "    tmpmatrix!!" + "  else"
				+ "    for i:=1:part(length(a),1) collect"
				+ "      for j!!:=1:part(length(a),2) collect"
				+ "        tmpmatrix!!(i,j!!)" + " end;");

		eval("procedure mysort a;"
				+ "begin scalar leftlist, rightlist, eqlist;"
				+ " leftlist:=list();"
				+ " rightlist:=list();"
				+ " eqlist:=list();"
				+ " return"
				+ " if length(a)<2 then a"
				+ " else <<"
				+ "  for each elem in a do"
				+ "    if elem<part(a,1) then"
				+ "     leftlist:=elem . leftlist"
				+ "    else if elem=part(a,1) then"
				+ "     eqlist:=elem . eqlist"
				+ "    else"
				+ "     rightlist:=elem . rightlist;"
				+ "  if length(leftlist)=0 and length(rightlist)=0 then"
				+ "    eqlist"
				+ "  else if length(leftlist)=0 then"
				+ "    append(eqlist, mysort(rightlist))"
				+ "  else if length(rightlist)=0 then"
				+ "    append(mysort(leftlist), eqlist)"
				+ "  else"
				+ "    append(append(mysort(leftlist),eqlist),mysort(rightlist))"
				+ " >> " + "end;");

		// mygreatersort and myequalsort are needed when trying to compare
		// rational numbers
		// eg. sqrt(2) and 2
		eval("procedure mygreatersort(a,b);" + "begin;"
				+ "on rounded, roundall, numval;"
				+ "ret:=if sgreater(a,b)=true then 1 else 0;"
				+ "if numeric!!=0 then off rounded, roundall, numval;"
				+ "return ret;" + "end;");
		eval("procedure myequalsort(a,b);" + "begin;"
				+ "on rounded, roundall, numval;"
				+ "ret:=if sequal(a,b)=true then 1 else 0;"
				+ "if numeric!!=0 then off rounded, roundall, numval;"
				+ "return ret;" + "end;");
		eval("procedure mysortdec a;"
				+ "begin scalar leftlist, rightlist, eqlist;"
				+ " leftlist:=list();"
				+ " rightlist:=list();"
				+ " eqlist:=list();"
				+ " return"
				+ " if length(a)<2 then a"
				+ " else <<"
				+ "  for each elem in a do"
				+ "    if mygreatersort(elem,part(a,1)) then"
				+ "     leftlist:=elem . leftlist"
				+ "    else if myequalsort(elem,part(a,1)) then"
				+ "     eqlist:=elem . eqlist"
				+ "    else"
				+ "     rightlist:=elem . rightlist;"
				+ "  if length(leftlist)=0 and length(rightlist)=0 then"
				+ "    eqlist"
				+ "  else if length(leftlist)=0 then"
				+ "    append(eqlist, mysortdec(rightlist))"
				+ "  else if length(rightlist)=0 then"
				+ "    append(mysortdec(leftlist), eqlist)"
				+ "  else"
				+ "    append(append(mysortdec(leftlist),eqlist),mysortdec(rightlist))"
				+ " >> " + "end;");

		eval("procedure mymember(a, list);"
				+ "begin;boole:=0;jj:=1;"
				+ "while jj<=length(list) and not boole do <<if part(list,jj)=a then boole:=1;jj:=jj+1;>>;"
				+ "return boole;" + "end;");

		eval("procedure myint(exp, var, from, upto);"
				+ "begin scalar upper, lower;"
				+ "antiderivative:=int(exp, var);"
				+ "if upto=Infinity or upto=-Infinity then upper:=limit(antiderivative,var,upto) else upper:=sub(var=upto,antiderivative);"
				+ "if from=Infinity or from=-Infinity then lower:=limit(antiderivative,var,from) else lower:=sub(var=from,antiderivative);"
				+ "return if freeof(upper,'limit) and freeof(lower,'limit) then upper-lower else '?;"
				+ "end;");

		eval("procedure myfirst(l, n);" + "for i:=1:n collect part(l,i);");

		eval("procedure getkernels(a);" + "for each element in a join"
				+ "  if arglength(element) = -1 or numberp(element) then"
				+ "    if numberp(element) then" + "      list()" + "    else"
				+ "      list(element)" + "  else"
				+ "    getkernels(part(element,0):=list);");

		eval("procedure mymainvars(a,n);"
				+ "begin scalar variables!!, result!!;"
				+ " variables!!:=gvars(getkernels(list(a)));"
				+ " result!!:="
				+ " if length(variables!!)<n then <<"
				+ "   write \"*** the expression \",a,\" has less than \",n,\" variables.\";"
				+ "   list(mymainvaraux(variables!!))" + " >> else <<"
				+ "   myfirst(variables!!,n)" + " >>;"
				+ " write \"***chosen variables: \",result!!;"
				+ " return result!! end;");

		eval("procedure mymainvaraux a;"
				+ "if a=list() then currentx!! else first(a);");

		eval("procedure mymainvar a;" + "first(mymainvars(a,1));");

	}

}
