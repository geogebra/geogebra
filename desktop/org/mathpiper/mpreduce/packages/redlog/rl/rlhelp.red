% ----------------------------------------------------------------------
% $Id: rlhelp.red 81 2009-02-06 18:22:31Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2006-2009 Andreas Dolzmann and Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
% 

lisp <<
   fluid '(rl_help_rcsid!* rl_help_copyright!*);
   rl_help_rcsid!* :=
      "$Id: rlhelp.red 81 2009-02-06 18:22:31Z thomas-sturm $";
   rl_help_copyright!* := "Copyright (c) 2006-2009 A. Dolzmann and T. Sturm"
>>;

module rlhelp;

fluid '(!*lower);

fluid '(rl_helpdb!* rl_switchdb!* rl_helpindent!*);
rl_helpdb!* := nil;
rl_switchdb!* := nil;
rl_helpindent!* := 3;

put('?,'stat,'rlis);
flag('(?),'go);

procedure !?(l);
   rl_help!$ l;

procedure rl_help!$(l);
   if null l then
      rl_help(nil,nil)
   else if null cdr l then
      rl_help(car l,nil)
   else
      rl_help(car l,cadr l);

procedure rl_help(key,type);
   begin scalar entry;
      if null key then
	 return rl_prioverview();
      if type eq 'function then
	 return if (entry := lto_catsoc(key,rl_helpdb!*)) then
	    rl_prihelpentry(key,entry);
      if type eq 'switch then
 	 return if (entry := lto_catsoc(key,rl_switchdb!*)) then
	    rl_priswitchentry(key,entry);
      if type then
	 rederr {"unknown modifier", type};
      if (entry := lto_catsoc(key,rl_helpdb!*)) then
	 return rl_prihelpentry(key,entry);
      if (entry := lto_catsoc(key,rl_switchdb!*)) then
	 return rl_priswitchentry(key,entry)
   end;

procedure rl_prihelpentry(key,entry);
   begin scalar cfield;
      terpri();
      ioto_tprin2t "FUNCTION";
      for i:=1:rl_helpindent!* do ioto_prin2 " ";
      ioto_prin2t {key," -- ",car entry};
      terpri();
      entry := cdr entry;
      ioto_tprin2t "SYNOPSIS";
      rl_printhelpline(car entry,rl_helpindent!*);
      terpri();
      entry := cdr entry;
      ioto_tprin2t "DESCRIPTION";
      rl_printhelpline(car entry,rl_helpindent!*);
      entry := cdr entry;
      cfield := car entry;
      if cfield then <<
      	 terpri();
      	 ioto_tprin2t "EXAMPLES";
	 rl_printhelpline(cfield,rl_helpindent!*);
      >>;
      entry := cdr entry;
      cfield := car entry;
      if cfield then <<
      	 terpri();
	 ioto_tprin2t "SWITCHES";
	 rl_printhelpline(rl_idl2str cfield,rl_helpindent!*)
      >>;
      entry := cdr entry;
      cfield := car entry;
      if cfield then <<
      	 terpri();
	 ioto_tprin2t "FURTHER SWITCHES";
	 rl_printhelpline(rl_idl2str cfield,rl_helpindent!*)
      >>;
      entry := cdr entry;
      cfield := car entry;
      if cfield then <<
	 terpri();
	 ioto_tprin2t "SEE ALSO";
	 rl_printhelpline(rl_idl2str cfield,rl_helpindent!*);
      >>
   end;

procedure rl_priswitchentry(key,entry);
   begin scalar cfield,fix;
      terpri();
      ioto_tprin2t "SWITCH";
      for i:=1:rl_helpindent!* do ioto_prin2 " ";
      ioto_prin2t {key," -- ",car entry};
      terpri();
      entry := cdr entry;
      ioto_tprin2t "DEFAULT/CURRENT";
      for i:=1:rl_helpindent!* do ioto_prin2 " ";
      ioto_prin2t {car entry,"/",rl_swsetting key};
      terpri();
      entry := cdr entry;
      fix := car entry;
      entry := cdr entry;
      ioto_tprin2t "DESCRIPTION";
      rl_printhelpline(car entry,rl_helpindent!*);
      if fix eq 'fixed then <<
      	 terpri();
	 rl_printhelpline("Users do not want to modify this switch.",
	    rl_helpindent!*)
      >>;
      if fix eq 'advanced then <<
      	 terpri();
	 rl_printhelpline("This switch is provided for advanced users.",
	    rl_helpindent!*)
      >>;
      entry := cdr entry;
      cfield := car entry;
      if cfield then <<
      	 terpri();
      	 ioto_tprin2t "EXAMPLES";
      	 rl_printhelpline(cfield,rl_helpindent!*)
      >>;
      entry := cdr entry;
      cfield := car entry;
      if cfield then <<
      	 terpri();
	 ioto_tprin2t "AFFECTED FUNCTIONS";
	 rl_printhelpline(rl_idl2str cfield,rl_helpindent!*);
      >>;
      entry := cdr entry;
      cfield := car entry;
      if cfield then <<
	 terpri();
	 ioto_tprin2t "SEE ALSO";
	 rl_printhelpline(rl_idl2str cfield,rl_helpindent!*)
      >>
   end;

procedure rl_printhelpline(text,indent);
   if stringp text then
      rl_printhelpline1(text,indent)
   else
      for each line in text do
      	 rl_printhelpline1(line,indent);
	 
procedure rl_printhelpline1(line,indent);
   begin scalar buf,buf2,a,!*lower,nextword;
      integer linelen,leftspace,leftspace2;
      linelen := linelength nil - indent - 1;  % distance 1 to right margin
      buf := reversip cdr reversip cdr explode line;
      leftspace := linelen;
      while buf do <<
	 if leftspace = linelen then for i:=1:indent do prin2 " ";
	 nextword := nil;
	 buf2 := buf;
	 leftspace2 := leftspace;
	 while buf2 and (a := car buf2) neq '!  do <<
	    nextword := a . nextword;
	    buf2 := cdr buf2;
	    leftspace2 := leftspace2 - 1;
	 >>;
	 if leftspace2 geq 0 then <<
	    buf := buf2;
	    leftspace := leftspace2;
	    ioto_prin2 compress('!" . reversip('!" . nextword));
	    while eqcar(buf,'! ) do <<
	       if leftspace > 0 then <<
		  ioto_prin2 " ";
		  leftspace := leftspace - 1
	       >>;
	       buf := cdr buf
	    >>
	 >> else <<
	    if length nextword > linelen then
	       rederr {"increase linelength to at least",
		  length nextword + rl_helpindent!* + 1};
	    terpri();
	    leftspace := linelen;
	    while eqcar(buf,'! ) do buf := cdr buf
	 >>
      >>;
      terpri()
   end;

procedure rl_swsetting(sw);
   if eval intern compress append(explode '!*,explode sw) then
      "on"
   else
      "off";

procedure rl_prioverview();
   begin integer s1,s2;
      terpri();
      ioto_tprin2t "NAME";
      rl_printhelpline("? -- REDLOG online help system",rl_helpindent!*);
      terpri();
      ioto_tprin2t "SYNOPSIS";
      rl_printhelpline("?FUNCTION[,TYPE];  ?SWITCH[,TYPE];",rl_helpindent!*);
      terpri();
      rl_printhelpline("The optional argument TYPE is one of 'function', 'switch'. This is necessary only for rare cases where there is a name used for both a function and a switch.",rl_helpindent!*);
      terpri();
      ioto_tprin2t {"EXAMPLES"};
      rl_printhelpline("?rlqe;  ?rlsimpl,function;  ?rlsimpl,switch;",rl_helpindent!*);
      terpri();
      ioto_tprin2 {"FUNCTIONS"};
      if !*rlverbose then ioto_prin2 {" (",length rl_helpdb!*,")"};
      terpri();
      rl_printhelpline(rl_idl2str lto_mergesort(
	 for each pr in rl_helpdb!* collect car pr,'ordp),rl_helpindent!*);
      terpri();
      ioto_tprin2 {"SWITCHES"};
      if !*rlverbose then <<
	 for each sw in rl_switchdb!* do
	    if nth(cdr sw,3) then s2 := s2+1 else s1 := s1+1;
	 ioto_prin2 {" (",s1,")"}
      >>;
      terpri();
      rl_printhelpline(rl_idl2str lto_mergesort(
	 for each pr in rl_switchdb!* join
	    if not nth(cdr pr,3) then {car pr},'ordp),rl_helpindent!*);
      terpri();
      ioto_tprin2 {"FURTHER SWITCHES"};
      if !*rlverbose then ioto_prin2 {" (",s2,")"};
      terpri();
      rl_printhelpline(rl_idl2str lto_mergesort(
	 for each pr in rl_switchdb!* join
	    if nth(cdr pr,3) then {car pr},'ordp),rl_helpindent!*);
      terpri()
   end;

procedure rl_idl2str(idl);
   begin scalar charl;
      if null idl then return "";
      charl := '!" . charl;
      for each c in explode car idl do
	 charl := c . charl;
      for each x in cdr idl do <<
	 charl := '!, . charl;
	 charl := '!  . charl;
	 for each c in explode x do
	    charl := c . charl;
      >>;
      charl := '!" . charl;
      return compress reversip charl
   end;

procedure rl_prinstrl(l,n);
   if l then <<
      for i:=1:n do ioto_prin2 " ";
      ioto_prin2 car l;
      for each link in cdr l do
   	 ioto_prin2 {", ",link}
   >>;

procedure rl_addhelp(key,nam,syn,des,xmp,swi,fswi,seealso);
   rl_helpdb!* := (key . {nam,syn,des,xmp,swi,fswi,seealso}) . rl_helpdb!*;

procedure rl_addswitch(key,nam,default,fix,des,xmp,fun,seealso);
   rl_switchdb!* := (key . {nam,default,fix,des,xmp,fun,seealso})
      . rl_switchdb!*;

rl_addhelp('rlset,
   "Set Domain of Computation (aka REDLOG Context)",
   "OLDDOMAIN = rlset(DOMAIN,[EXTRA PARAMETRS,...]);",
   {"Choose the domain of computation. Valid domains are (short names in parenthesis) boolean (B), complex (C), padics, reals (R), queues, terms, integers (Z). For backward compatibility one can use instead context identifiers like ibalp, acfsf, dvfsf, ofsf, qqe, talp, pasf.",
      "",
      "The old domain specification is returned - possibly including extra parameters - in a format that allows it to be used as an argument to another rlset call."},
   {"rlset boolean;  rlset complex;  rlset reals;  rlset integers;",
      "rlset padics;  rlset(padics,103);  rlset(padics,-5);",
      "rlset(queues,reals);",
      "rlset(terms,{o,0},{s,1});"},
   {},
   nil,
   {});

rl_addhelp('rlqe,
   "Quantifier Elimination",
   "NEWFORMULA = rlqe(FORMULA,[THEORY])",
   {"Eliminate all quantifiers from FORMULA. That is NEWFORMULA <-> FORMULA, and NEWFORMULA is quantifier-free. Optional THEORY is a list of atomic formulas. If present, then the elimination result is correct for all interpretations of the parameters that fulfill THEORY. Formally /\THEORY -> (NEWFORMULA <-> FORMULA). Uses virtual substitution plus PCAD for high degrees."},
   {"rlqe all(x,ex(y,x^2+x*y+b>0 and x+a*y^2+b<=0));",
      "rlqe(ex(x,m*x+b=0),{m<>0});"},
   {'rlqepnf,'rlqedfs,'rlqegsd,'rlqesr,'rlqeheu,'rlqegenct,'rlqevarsel,
      'rlqeqsc,'rlqesqsc,'rlqefb},
   nil,
   {'rlqea,'rlgqe,'rllqe,'rlcad,'rlhqe,'rlgsn});

rl_addhelp('rlgsn,
   "Groebner Simplifier Normal form",
   "NEWFORMULA = rlgsn(FORMULA,[THEORY])",
   {"Simplifiy FORMULA using Groebner basis methods. That is NEWFORMULA <-> FORMULA, but NEWFORMULA is hopefully shorter than FORMULA. Optional THEORY is a list of atomic formulas. If present, the simplification is correct for all interpretations of variables that fulfill THEORY. Formally, /\THEORY -> (NEWFORMULA <-> FORMULA)."},
   {"rlgsn(x*z+1=0 and y*z+1=0 and x=z);"},
   {'rlgsred},
   nil,
   {'rlgsc,'rlgsd});

rl_addhelp('rlsimpl,
   "Simplify",
   "NEWFORMULA = rlsimpl(FORMULA,[THEORY])",
   {"Simplify FORMULA. That is NEWFORMULA <-> FORMULA, and NEWFORMULA hopefully contains less and less complex atomic formulas. Optional THEORY is a list of atomic formulas. If present, then the simplification result is correct for all interpretations of the parameters that fulfill THEORY. Formally /\THEORY -> (NEWFORMULA <-> FORMULA)."},
   {"rlsimpl(x>0 and 2*x-1>0 and 3*x+5<>0);",
      "rlsimpl(a=0 and (b=0 or (c=0 and d>=0)) and (d<>0 or a<>0));",
      "rlsimpl(a>=0 or (b+5=0 and c>0),{a<>0,b>0});"},
   {'rlsiatadv,'rlsipd,'rlsiexpl,'rlsiexpla,
      'rlsipw,'rlsipo,'rlsimpl,'rlsifac,'rlsitsqspl},
   {'rlsichk,'rlsiidem,'rlsism,'rlsiso},
   {'rlitab,'rlgsn,'rlcnf,'rldnf});

rl_addswitch('rlgsred,
   "Groebner Simplifier Reduction",
   "on",
   nil,
   {"Reduce some left hand side polynomials modulo Groebner bases of others during simplification.",
      "Sometimes better results. Sometimes considerably time consuming."},
   {"rlgsn(x+y**2<>0 or x*z**2+3*y**2*z**2=0);"},
   {'rlgsn,'rlgsc,'rlgsd},
   nil);

rl_addswitch('rlsiatadv,
   "Simplify Atomic Formulas Advanced",
   "on",
   nil,
   {"Triggers sophisticated atomic formula simplifications based on squarefree part computations, degree parity decomposition, and recognition of trivial square sums."},
   {"rlsimpl(a**2 + 2*a*b + b**2 <> 0);",
      "rlsimpl(a**2 + b**2 + 1 > 0);"},
   {'rlsimpl},
   {'rlsitsqspl,'rlsipd});

rl_addswitch('rlsipd,
   "Simplify Parity Decomposition",
   "on",
   nil,
   {"Triggers the degree parity decomposition of terms occurring with ordering relations. That is the factorization into the product of all factors of even degree on the one hand, and the product of all factor of odd degree on the other hand.",
      "",
      "rlsipd is evaluated only if the switch rlsiatadv is on."},
   {"rlsimpl(a**7 + a**6 - 3*a**5 - 3*a**4 + 3*a**3 + 3*a**2 - a - 1 >= 0);"},
   {'rlsimpl},
   {'rlsiatadv,'rlsiexpl,'rlsiexpla});

rl_addswitch('rlsifac,
   "Simplify Factorization",
   "on",
   nil,
   {"Triggers  splitting based on polynomial factorization of equations and negative equations into conjunctions or disjunction, respectively. Notice that factorization of terms occurring with ordering relations is generally limited to degree parity decompositions (rlsipd).",
      "",
      "The decision on splitting is also affected by the switches rlsiexpl and rlsiexpla. rlsipd is evaluated only if the switch rlsiatadv is on."},
   {"rlsimpl(x**2-1=0);"},
   {'rlsimpl},
   {'rlsiatadv,'rlsiexpl,'rlsiexpla,'rlsipd});

rl_addswitch('rlsism,
   "Simplify Smart",
   "on",
   'fixed,
   {"Combine information on various atomic formulas. This leads to much better simplifications."},
   {"rlsimpl(2*a+1>0 and a=1 and b**2+1>0);"},
   {'rlsimpl},
   nil);

rl_addswitch('rlsichk,
   "Simplify Check",
   "on",
   'fixed,
   {"Check for and eliminate equal sibling subformulas."},
   {"rlsimpl((x>0 and x-1<0) or (x>0 and x-1<0));"},
   {'rlsimpl},
   {'rlsism});

rl_addswitch('rlsiidem,
   "Simplify Idempotently",
   "on",
   'fixed,
   {"Take care that rlsimpl is idempotent in the very most cases. That is, rlsimpl(rlsimpl(FORMULA))=rlsimpl(FORMULA).",
      "",
      "rlsiidem is evaluated only if the switch rlsism is on."},
   nil,
   {'rlsimpl},
   {'rlsism});

rl_addswitch('rlsiso,
   "Simplify Sort",
   "on",
   'fixed,
   {"Sort neighbored atomic formulas wrt. some internal ordering. This leads to more canonical output, which in turn allows further simplifications. There is general convention that atomic formulas are placed before non-atomic ones. This is not affected by this switch."},
   {"rlsimpl((b=0 and a=0) or c=0);",
      "rlsimpl((b=0 and a=0) or (a=0 and b=0));"},
   {'rlsimpl},
   nil);

rl_addswitch('rlsipw,
   "Simplify Prefer Weak Orderings",
   "off",
   nil,
   {"Sometimes weak orderings '>=' and '<=' can be equivalently replaced by respective strict orderings '>' and '<'. It depends on the particular situation which choice should be considered simpler when such situations become apparent to rlsimpl."},
   {"rlsimpl(a<>0 and (a>=0 or b=0));"},
   {'rlsimpl},
   {'rlsipo});

rl_addswitch('rlsipo,
   "Simplify Prefer Orderings",
   "on",
   nil,
   {"Sometimes orderings like '>' and '<' can be equivalently replaced by '<>'. It depends on the particular situation which choice should be considered simpler when such situations become apparent to rlsimpl."},
   {"rlsimpl(a>=0 and (a<>0 or b=0));"},
   {'rlsimpl},
   {'rlsipw});

rl_addswitch('rlsiexpla,
   "Simplify Explode Always",
   "on",
   nil,
   {"There are simplifications where one atomic formula can be replaced by a conjunction or disjunction of several simpler atomic formulas.",
      "",
      "If rlsiexpla is on, then such replacements are generally performed. Otherwise such replacements are controlled by the switch rlsiexpl."},
   {"rlsimpl(x**2-1=0 and y=0);",
      "rlsimpl(x**2-1=0 or y=0);"},
   {'rlsimpl},
   {'rlsiexpl,'rlsifac,'rlsipd});

rl_addswitch('rlsiexpl,
   "Simplify Explode",
   "on",
   nil,
   {"There are simplifications where one atomic formula can be replaced by a conjunction or disjunction of several simpler atomic formulas.",
      "",
      "If rlsiexpl is on, then such replacements are performed if and only if the Boolean operator ('and' or 'or') coming into existence by that expansion equals the Boolean operator immediately dominating the affected atomic formula; i.e., the Boolean structure does not get more complicated.",
      "",
      "rlsiexpl is evaluated only if the switch rlsiexpla is off."},
   {"rlsimpl(x**2-1=0 and y=0);",
      "rlsimpl(x**2-1=0 or y=0);"},
   {'rlsimpl},
   {'rlsiexpla,'rlsifac,'rlsipd});

rl_addswitch('rlsimpl,
   "Simplify",
   "off",
   nil,
   {"If rlsimpl is on, then the standard simplifier rlsimpl is implicitly applied to every formula entered into REDLOG.",
      "",
      "The idea is to work with representations that support the recognition of equivalent formulas to the most feasible extent. This resembles the systematic expansion of polynomials. Notice, however, that rlsimpl does not produce canonical forms."},
   {"x**2+1>0"},
   {'rlsimpl},
   nil);

rl_addswitch('rlsitsqspl,
   "Simplify Trivial Square Sum Split",
   "on",
   nil,
   {"A trivial square sum is a polynomial containing exclusively even powers. This can easily be recognized to be positively (semi-)definite. If rlsitsqspl is on, then trivial square sums are split additively according to the rules specified by rlsiexpl and rlsiexpla.",
      "",
      "rlsipd is evaluated only if the switch rlsiatadv is on."},
   {"rlsimpl(a**2+b**2>0);"},
   {'rlsimpl},
   {'rlsiexpl,'rlsiexpla,'rlsiatadv});

rl_addswitch('rlqepnf,
   "Quantifier Elimination Prenex Normal Form",
   "on",
   'advanced,
   {"If rlqepnf is on, then prenex normal form computation is applied to the argument formula before quantifier elimination. It must be switched off only if the formula is already prenex. Then it can prevent expansions of 'equiv', which double the size."},
   {},
   {'rlqe,'rlgqe,'rlqea,'rlgqea,'rllqe,'rlqews},
   {'rlpnf,'rlnnf});

rl_addswitch('rlqedfs,
   "Quantifier Elimination Depth First Search",
   "off",
   'advanced,
   {"If rlqedfs is on, then the affected quantifier elimination procedures work in a depth first search manner in contrast to breadth first search. This saves space, and with decision problems where variable-free atomic formulas can be evaluated to truth values it might save time. In general, it leads to larger results.",
      "",
      "rlqedfs is evaluated only if the switch rlqeheu is off."},
   {},
   {'rlqe,'rlgqe,'rlqea,'rlgqea,'rllqe},
   {'rlqeheu});

rl_addswitch('rlqegsd,
   "Quantifier Elimination Groebner Simplifiy (DNF)",
   "off",
   nil,
   {"Assume wlog. that all quantifier blocks are existential ones. If rlqegsd is on, then rlgsd is applied to the matrix formula at the beginning of the elimination of each block."},
   {},
   {'rlqe,'rlgqe,'rlqea,'rlgqea,'rllqe},
   {'rlqegsd});

rl_addswitch('rlqesr,
   "Quantifier Elimination Separate Roots",
   "off",
   'fixed,
   {"rlqesr affects the substitution of the two solutions of quadratic constraints for the outermost quantifier block with extended quantifier elimination."},
   {},
   {'rlqea,'rlgqea},
   {});

rl_addswitch('rlqeheu,
   "Quantifier Elimination Search Heuristic",
   "on (reals), off (padics)",
   'advanced,
   {"If rlqeheu is on, then the switch rlqedfs is ignored.",
      "",
      "The affected quantifier elimination procedures then decide between BFS and DFS for each quantifier block, where DFS is chosen when the problem is a decision problem."},
   {},
   {'rlqe,'rlgqe,'rlqea,'rlgqea,'rllqe},
   {'rlqedfs});

rl_addswitch('rlqegenct,
   "Quantifier Elimination Generate Complex Theory",
   "on",
   nil,
   {"If rlgenct ist on, then the affected functions are allowed to assume inequalities over non-monomial terms."},
   {"rlgqe ex(x,(a+1)*x+1=0);"},
   {'rlgqe},
   {});

rl_addswitch('rlqevarsel,
   "",
   "",
   nil,
   {},
   {},
   {},
   {});

rl_addswitch('rlqeqsc,
   "",
   "",
   nil,
   {},
   {},
   {},
   {});

rl_addswitch('rlqesqsc,
   "",
   "",
   nil,
   {},
   {},
   {},
   {});

rl_addswitch('rlqefb,
   "",
   "",
   nil,
   {},
   {},
   {},
   {});

operator rlcheckhdb;

procedure rlcheckhdb();
   begin scalar swil,fswil,rswil,rfswil,funcl,rfuncl,alsol,dswitchl,dfuncl,w;
      for each pr in rl_helpdb!* do <<
	 if car pr memq funcl then
	    dfuncl := lto_insertq(car pr,dfuncl)
	 else
	    funcl := car pr . funcl;
      	 rswil := append(rswil,nth(cdr pr,5));
      	 rfswil := append(rfswil,nth(cdr pr,6));
	 alsol := append(alsol,nth(cdr pr,7))
      >>;
      for each pr in rl_switchdb!* do <<
	 if car pr memq fswil or car pr memq swil then
	    dswitchl := lto_insertq(car pr,dswitchl)
	 else if nth(cdr pr,3) then
 	    fswil := car pr . fswil
 	 else
 	    swil := car pr . swil;
	 rfuncl := append(rfuncl,nth(cdr pr,6));
	 alsol := append(alsol,nth(cdr pr,7))
      >>;
      if dfuncl then <<
      	 ioto_tprin2t "+++ Doubly documented functions:";
      	 ioto_tprin2t {dfuncl}
      >>;
      if dswitchl then <<
      	 ioto_tprin2t "+++ Doubly documented switches:";
      	 ioto_tprin2t {dswitchl}
      >>;
      w := for each f in rfuncl join if not (f memq funcl) then {f};
      if w then <<
      	 ioto_tprin2t "+++ Undocumented AFFECTED FUNCTIONS:";
	 ioto_tprin2t {w}
      >>;
      w := for each sw in rswil join if not (sw memq swil) then {sw};
      if w then <<
	 ioto_tprin2t "+++ Undocumented SWITCHES:";
	 ioto_tprin2t {w}
      >>;
      w := for each sw in rfswil join if not (sw memq fswil) then {sw};
      if w then <<
      	 ioto_tprin2t "+++ Undocumented FURTHER SWITCHES:";
	 ioto_tprin2t {w}
      >>;
      w := for each a in alsol join
	 if not (a memq funcl or a memq swil or a memq fswil) then {a};
      if w then <<
      	 ioto_tprin2t "+++ Undocumented SEE ALSO:";
      	 ioto_tprin2t {w}
      >>;
      w := for each a in swil join if not (a memq rswil) then {a};
      if w then <<
	 ioto_tprin2t "+++ Unreferenced SWITCHES:";
      	 ioto_tprin2t {w}
      >>;
      w := for each a in fswil join if not (a memq rfswil) then {a};
      if w then <<
	 ioto_tprin2t "+++ Unreferenced FURTHER SWITCHES:";
      	 ioto_tprin2t {w}
      >>
   end;

endmodule;  % [rlhelp]

end;  % of file
