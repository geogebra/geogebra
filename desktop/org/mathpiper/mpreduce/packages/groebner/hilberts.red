module hilberts;% Hilbert series of a set of Monomials .

% Author : Joachim Hollman,Royal Institute for Technology,Stockholm,Sweden
%  email :  < joachim@nada.kth.se >
% Improvement : Herbert Melenk,ZIB Berlin,Takustr 9,email : < melenk@zib.de >

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


comment

A very brief " description " of the method used.

M=k[x,y,z]/(x^2*y,x*z^2,y^2)
                    x.
0 --> ker(x.) --> M --> M --> M/x --> 0

M/x = k[x,y,z]/(x^2*y,x*z^2,y^2,x) = k[x,y,z]/(x,y^2)

ker(x.) =((x) +(x^2*y,x*z^2,y^2))/(x^2*y,x*z^2,y^2) =

        =(x,y^2)/(x^2*y,x*z^2,y^2)

Hilb(ker(x.)) = Hilb        - Hilb
                 (x,y^2)    (x^2*y,x*z^2,y^2)

        = 1/(1-t)^3 - Hilb                -
                          k[x,y,z]/(x,y^2)

          -(1/(1-t)^3 - Hilb
                          k[x,y,z]/(x^2*y,x*z^2,y^2)

        = Hilb -Hilb
              M     k[x,y,z]/(x,y^2)

If you only keep the numerator in Hilb = N(t)/(1-t)^3
                                       M
then you get

(1-t)N(t) = N(t)  - t(N(t) - N(t)   )
      I       I+(x)       I       Ann(x) + I

i.e.

 N(t) = N(t)  + t N(t)           (*)
  I       I+(x)      Ann(x) + I

Where
      I          =(x^2*y,x*z^2,y^2)
      I +(x)    =(x,y^2)
      I + Ann(x) =(x*y,z^2,y^2)
      N(t) is the numerator polynomial in Hilb
      I                                       k[x,y,z]/I

Equation(*)is what we use to compute the numerator polynomial,i.e.
we " divide out " one variable at a time until we reach a base case.
( One is not limited to single variables but I don't know of any good
strategy for selecting a monomial.)

Usage : hilb({ monomial_1,...,monomial_n } [,variable ]);

fluid '(nvars!*);

% ************** MACROS ETC. **************

smacro procedure term(c,v,e);{ ' times,c,{ ' expt,v,e } };

% -------------- safety check --------------

smacro procedure varp m;
 idp m and m or pairp m and get(car m,'simpfn)='simpiden;

smacro procedure checkexpt m;
 eqcar(m,'expt)and varp cadr m and numberp caddr m;

smacro procedure checksinglevar m;
 if varp m then t else checkexpt m;

smacro procedure checkmon m;
 if checksinglevar m then t
 else if eqcar(m,'times)then checktimes cdr m else nil;

smacro procedure checkargs(monl,var);
 listp monl and eqcar(monl,'list)and
  varp var and checkmonl monl;

symbolic procedure makevector(n,pat);
begin scalar v;v:=mkvect n;
 for i:=1:n do putv(v,i,pat);return v end;

% -------------- monomials --------------

smacro procedure allocmon n;makevector(n,0);

smacro procedure getnthexp(mon,n);getv(mon,n);

smacro procedure setnthexp(mon,n,d);putv(mon,n,d);

smacro procedure gettdeg mon;getv(mon,0);

smacro procedure settdeg(mon,d);putv(mon,0,d);

% -------------- ideals --------------

smacro procedure theemptyideal();{nil,nil};

smacro procedure getnextmon ideal;
<<x:=caadr ideal;
  if cdadr ideal then ideal:={car ideal,cdadr ideal}
    else ideal:=theemptyideal();x>>;

smacro procedure notemptyideal ideal;cadr ideal;

smacro procedure firstmon ideal;caadr ideal;

smacro procedure appendideals(ideal1,ideal2);
{car ideal2,append(cadr ideal1,cadr ideal2)};

symbolic procedure insertvar(var,ideal);
% Inserts variable var as last generator of ideal
begin scalar last;last:={makeonevarmon var};
 return({last,append(cadr ideal,last)})end;

symbolic procedure addtoideal(mon,ideal);
% Add mon as generator to the ideal
begin scalar last;last:={mon};
 if ideal = theemptyideal()then rplaca(cdr(ideal),last)
  else rplacd(car(ideal),last);
 rplaca(ideal,last)end;

% ************** END OF MACROS ETC. **************

% ************** INTERFACE TO ALGEBRAIC MODE **************

symbolic procedure hilbsereval u;
begin scalar l,monl,var;l:=length u;
 if l < 1 or l > 2 then rerror(groebnr2,17,
       "Usage: hilb({monomial_1,...,monomial_n} [,variable])")
  else if l = 1 then
  <<monl:=reval car u;var:=' x>>else
    <<monl:= reval car u;var:=reval cadr u>>;
  monl:='list.for each aa in cdr monl collect reval aa;
  if not checkargs(monl,var)then rerror(groebnr2,18,
        "Usage: hilb({monomial_1,...,monomial_n} [,variable])");
%  return(aeval
%            {'QUOTIENT,
%                 coefflist2prefix(NPol(gltb2arrideal(monl)), var),
%           {'EXPT,list('PLUS,1,list('TIMES,-1,var)},
%               nvars!*)});
 return(aeval coefflist2prefix(npol(gltb2arrideal(monl)),var)) end;

% Define "hilb" to be the algebraic mode function
put('hilb,'psopfn,'hilbsereval);

symbolic procedure checkmonl monl;
begin scalar flag,tmp;flag:=t;monl:=gltbfix(monl);
 while monl and flag do
 <<tmp:=car monl;
   flag:= checkmon(tmp);monl:=cdr monl>>;
 return flag end;

symbolic procedure checktimes m;
begin scalar flag,tmp;flag:=t;
 while m and flag do
 <<tmp:=car m;flag:=checksinglevar tmp;
   m:=cdr m>>;return flag end;

symbolic procedure coefflist2prefix(cl,var);
begin scalar poly;integer i;
 for each c in cl do
 <<poly:=term(c,var,i).poly;
   i:=i + 1>>;return'plus.poly end;

symbolic procedure indets l;
% "Indets"  returns a list containing all the
% indeterminates of l.
% L is supposed to have a form similar to the variable
% GLTB in the Groebner basis package.
%(LIST(EXPT Z 2)(EXPT X 2) Y)
begin scalar varlist;
 for each m in l do
  if m neq'list then
   if atom m then varlist:=union({m},varlist)
    else if eqcar(m,'expt)then varlist:=union({cadr m},varlist)
    else varlist:=union(indets cdr m,varlist);
 return varlist end;

symbolic procedure buildassoc l;
% Given a list of indeterminates(x1 x2 ...xn) we produce
% an a-list of the form(( x1 . 1)(x2 . 2)...(xn . n)).
begin integer i;
 return(for each var in l collect progn(i:=i #+1,var.i)) end;

symbolic procedure mons l;
% Rewrite the leading monomials(i . e . GLTB).
% the result is a list of monomials of the form :
%(variable . exponent)or(( variable1 . exponent1)...
% (variablen . exponentn))
%
% mons('(LIST(EXPT Z 2)(EXPT X 2)(TIMES Y(EXPT X 3))));
%(((Y . 1)(X . 3))(X . 2)(Z . 2)).
begin scalar monlist;
 for each m in l do
  if m neq'list then monlist:=
    if atom m then(m. 1).monlist
     else if eqcar(m,'expt)
      then(cadr m.caddr m).monlist
      else(for each x in cdr m collect monsaux x).monlist;
 return monlist end;

symbolic procedure monsaux m;
 if eqcar(m,'expt)then cadr m.caddr m else m . 1;

symbolic procedure lmon2arrmon m;
% List-monomial to array-monomial
% a list-monomial has the form:(variable_number . exponent)
% or is a list with entries of this form.
% "variable_number" is the number associated with the variable,
% see buildassoc().
begin scalar mon;integer tdeg;mon:=allocmon nvars!*;
 if listp m then
  for each varnodotexp in m do
  <<setnthexp(mon,car varnodotexp,cdr varnodotexp);
    tdeg:=tdeg+cdr varnodotexp >>
  else
  <<setnthexp(mon,car m,cdr m);tdeg:=tdeg+cdr m>>;
 settdeg(mon,tdeg);return mon end;

symbolic procedure gltbfix l;
% Sometimes GLTB has the form(list(list ...))
% instead of(list ...).
 if listp cadr l and caadr(l)='list then cadr l else l;

symbolic procedure gege(m1,m2);
 if gettdeg m1 >= gettdeg m2 then t else nil;

symbolic procedure getendptr l;
begin scalar ptr;while l do<<ptr:=l;l:=cdr l>>;
 return ptr end;

symbolic procedure gltb2arrideal xgltb;
% Convert the monomial ideal given by GLTB(in list form)
% to a list of vectors where each vector represents a monomial.
begin scalar l;l:=indets(gltbfix(xgltb));nvars!*:=length(l);
 l:=sublis(buildassoc l,mons gltbfix xgltb);
 l:=for each m in l collect lmon2arrmon(m);
 l:=sort(l,' gege);
 return{getendptr(l),l}end;

% ************** END OF INTERFACE TO ALGEBRAIC MODE **************

%************** PROCEDURES **************

symbolic procedure npol ideal;
% Recursively computes the numerator of the Hilbert series.
begin scalar v,si;v:=nextvar ideal;
 if not v then return basecasepol ideal;
 si:=splitideal(ideal,v);
 return shiftadd(npol car si,npol cadr si)end;

symbolic procedure dividesbyvar(var,mon);
begin scalar div;if getnthexp(mon,var)=0 then return nil;
 div:=allocmon nvars!*;
 for i:=1 : nvars!* do setnthexp(div,i,getnthexp(mon,i));
 setnthexp(div,var,getnthexp(mon,var)- 1);
 settdeg(div,gettdeg mon - 1);return div end;

symbolic procedure divides(m1,m2);
% Does m1 divide m2?
% m1 and m2 are monomials;
% result: either nil(when m1 does not divide m2)or m2 / m1.
begin scalar m,d,i;i:=1;m:=allocmon nvars!*;
 settdeg(m,d:=gettdeg m2 - gettdeg m1);
 while d >= 0 and i <= nvars!* do
 <<setnthexp(m,i,d:=getnthexp(m2,i)- getnthexp(m1,i));
   i:= i+1>>;
   return if d < 0 then nil else m end;

symbolic procedure shiftadd(p1,p2);
% p1 + z * p2;
% p1 and p2 are polynomials(nonempty coefficient lists).
begin scalar p,pptr;pptr:=p:=car p1.nil;
 p1:=cdr p1;
 while p1 and p2 do
 <<rplacd(pptr,(car p1 + car p2).nil);
   p1:=cdr p1;p2:=cdr p2;pptr:=cdr pptr>>;
 if p1 then rplacd(pptr,p1)
  else rplacd(pptr,p2);return p end;

symbolic procedure remmult(ipp1,ipp2);
% The union of two ideals with redundancy of generators eliminated.
begin scalar fmon,inew,isearch,primeflag,x;
 % fix;x is used in the macro...
 x:=nil;inew:=theemptyideal();
 while notemptyideal(ipp1)and notemptyideal(ipp2)do
  begin if gettdeg(firstmon(ipp2)) < gettdeg(firstmon(ipp1))
   then<<fmon:=getnextmon(ipp1);isearch:=ipp2>>
    else <<fmon:=getnextmon(ipp2);isearch:=ipp1>>;
 primeflag:=t;
 while primeflag and notemptyideal isearch do
  if divides(getnextmon isearch,fmon)then primeflag:=nil;
 if primeflag then addtoideal(fmon,inew)end;
 return if notemptyideal ipp1 then appendideals(inew,ipp1)
  else appendideals(inew,ipp2)end;

symbolic procedure nextvar ideal;
% Extracts a variable in the ideal suitable for division.
begin scalar m,var,x;x:=nil;
 repeat
 <<m:=getnextmon ideal;
   var:=getvarifnotsingle m;
   >>until var or ideal=theemptyideal();
 return var end;

symbolic procedure getvarifnotsingle mon;
% Returns nil if the monomial is in a single variable,
% otherwise the index of the second variable of the monomial.
begin scalar foundvarflag,exp;integer i;
 while not foundvarflag do
 <<i:=i + 1;exp:=getnthexp(mon,i);
    if exp>0 then foundvarflag:=t>>;
 foundvarflag:=nil;
 while i<nvars!* and not foundvarflag do
 <<i:=i+1;exp:=getnthexp(mon,i);
    if exp>0 then foundvarflag:=t>>;
 if foundvarflag then return i else return nil end;

symbolic procedure makeonevarmon vindex;
% Returns the monomial consisting of the single variable vindex.
begin scalar mon;mon:=allocmon nvars!*;
 for i:=1:nvars!* do setnthexp(mon,i,0);
 setnthexp(mon,vindex,1);
 settdeg(mon,1);return mon end;

symbolic procedure splitideal(ideal,var);
% Splits the ideal into two simpler ideals.
begin scalar div,ideal1,ideal2,m,x;x:=nil;
 ideal1:=theemptyideal();ideal2:=theemptyideal();
 while notemptyideal(ideal)do
 <<m:=getnextmon(ideal);
   if div:=dividesbyvar(var,m)then addtoideal(div,ideal2)
     else addtoideal(m,ideal1)>>;
   ideal2:=remmult(ideal1,ideal2);ideal1:=insertvar(var,ideal1);
 return{ideal1,ideal2}end;

symbolic procedure basecasepol ideal;
% In the base case every monomial is of the form Xi ^ ei;
% result : the numerator polynomial of the Hilbert series
%          i.e.(1 - z ^ e1)*(1 - z ^ e2)* ...
begin scalar p,degsofar,e;integer tdeg;
 for each mon in cadr ideal do tdeg:=tdeg+gettdeg mon;
 p:=makevector(tdeg,0);putv(p,0,1);degsofar:=0;
 for each mon in cadr ideal do
 <<e:=gettdeg mon;
   for j:= degsofar step -1 until 0 do
    putv(p,j+e,getv(p,j+e)-getv(p,j));
    degsofar:=degsofar+e>>;
 return vector2list p end;

symbolic procedure vector2list v;
% Convert a vector v to a list.  No type checking is done.
begin scalar u;
  for i:=upbv v step -1 until 0 do u:=getv(v,i).u;
  return u end;

endmodule;;end;
