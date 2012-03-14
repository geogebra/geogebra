module hille; % Hillebrand decomposition of a zero - dimensional polynomial
% ideal following
% D. Hillebrand: Triangulierung nulldimensionaler Ideale - Implementierung und
% Vergleich zweier Algorithmen. Diplomarbeit im Studiengang Mathematik
% der Universit"at Dortmund. Betreuer: Prof. Dr. H. M. M"oller, 1999
% Dasi: hille.sav7

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


% triang4: groeposthillebrand: interface for the solver (for zero -
% dimensional polynomial ideals).

symbolic procedure groeposthillebrand(u,v);
% Solve - interface for the module 'hille' (for:Hillebrand);
% ' u ' is the (partial) basis in external form (the first word is 'list);
% ' v ' is the total list of variables (the first word is 'list);
% the routine returns a list of solutions, if 'u' is zero-dimensional;
% else it returns 'nil'.
begin scalar a,d,e;u:=cdr u;
 vdpinit groebnervars(u,nil);groedomainmode();
 a:=for each uu in u collect numr simp uu;
 vars!*:=dipvars!*:=vdpvars!*:=cdr v;
 if null hillebrandtriangular(cdr v,a,nil)then return nil;
  % From now on, the zero-dimensionality has been assured.
 !*groebopt:=nil;
 a:=hillebrand(a,nil);
 for each b in a do
 <<d:='list.for each c in b collect prepf c;
   e:=append(e,groepostfastsolve(d,v))>>;
 return groesolvearb(e,v) end;           %%%AENDERUNG 18.9.00

% triang3: saturation of a basis and a polynomial.

symbolic procedure hillebrandstdsat(g,p);
% Compute the basis of the saturation ideal ' g ' and polynomial ' p ' .
begin scalar a,b,c,e;
 if cdr g then go to a ;
  % ' g ' is a one - polynomial list .
 p:=p./ 1;g:=car g ./ 1;a:=t;
 while a do<<a:=hillebrandquot(g,p);if a then g:=a>>;
 e:=if not domainp numr g then{numr g}else nil;return e;
a: % The list 'g' has more than one polynomial.
 p:=prepf p;a:='list.for each gg in g collect prepf gg;
 b:=saturationeval{a,p};
 if b='(list 1)then return'(1);
 c:=for each bb in cdr b collect numr simp bb;
 return sort(c,function hillebrandcompare)end;

symbolic procedure hillebrandquot(g,p);
% Compute the quotient of 'g' and 'p', if 'p' divides 'g' as a
% polynomial (if 'p' is a polynomial divisor of 'g', ignoring the
% quotients of coefficients).
(if hillebrandvar(denr a , vars!*)then numr a ./ 1 else nil)
 where a=quotsq(g,p);

symbolic procedure hillebrandvar(p,m);
% Tests, if the variables of 'p' are contained in 'm' ; 'nil'
% if a variable of 'p' is part of 'm' ; else return 't'.
if domainp p then t else if mvar p member m then nil else
 hillebrandvar(lc p,m)and hillebrandvar(red p,m);

% triang2: the main routine 'hillebrand1'.

symbolic procedure hillebrand(g,fact);
% 'g' ist an untagged list of standard polynomials, a Groebner basis,
% 'fact' is a swich which involves faczorization (if set).
begin scalar a ; vars!*:=dipvars!*;!*trgroesolv and hillebrandmsg1 g;
 a:=hillebrand1(sort(g,function hillebrandcompare),fact);
 !*trgroesolv and hillebrandmsg2 a;return a end;

% The sorting is inverse to the normal sorting (polynomial with the
% highest leading term (normally) is the last one).

symbolic procedure hillebrandcompare(a,b);
% Comparison of 'a' and 'b' (standard polynomials) after inverse 'lex' principle.
hillebrandcompare1(a,b,vars!*);

symbolic procedure hillebrandcompare1(a,b,v);
% If the result is 't', 'a' and 'b' are sorted 'a'<'b'; if the result
% is 'nil', they are ordered 'b'<'a'.
begin scalar aa,bb,c;
 aa:=a;bb:=b;
 if domainp aa or not(mvar aa member v) then return t else
 if domainp bb or not(mvar bb member v) then
  (if mvar aa member v then return nil else return t);
aa: if domainp bb or not(mvar bb member v)then
  (if domainp aa or not(mvar aa member v)then
      return hillebrandcompare1(red a,red b,v)else return t) else
 if mvar aa member v and mvar aa=mvar bb then
  (if ldeg aa=ldeg bb then<<aa:=lc aa;bb:=lc bb;go to aa>>else
    if ldeg aa #< ldeg bb then return t else return nil)else
 if(c:=mvar bb member v)then
  (if domainp aa or not(mvar aa member c)or mvar aa member cdr c then return t else
     if mvar aa member v then return nil);
 return hillebrandcompare1(red a,red b,v)end;

% The routine HILLEBRAND1: the main(recursive) routine.

symbolic procedure hillebrand1(g,fact);
% Input: 'g' : a (reduced ) lexicographical groebner basis,
% fact: a switch, which involves factorization (if set);
% output: a list of bases (a decomposition of 'g' in triangular bases),
% internal form.
% 16. Jan 2005: test for '(1)' added. HM.
if g='(1)then nil else
if hillebrandtriangular(vars!*,g,t)then hillebrandfactorizelast(g,fact)else
begin scalar a,aa,b,c,r,f,ff,fh,g2,g3,h,l,o;
% first part of the split.
 g3:=g;while cdr g3 do g3:=cdr g3;
 a:=hillebranddecompose(g,mvar car g3);
 c:=for each aa in cdr a collect lc aa ;
 r:=hillebrandgroebner hillebrandjoin(car a,c);
 f:=hillebrand1(r,fact); % Recursive call with reduced basis.
 aa:=hillebrandlast g;
 for each tt in f do
 <<b:=hillebrandnormalform(aa,tt);
   ff:=hillebrandappend1(ff,tt,b)>> ; % append(tt,{b}).ff
 f:=reversip ff;
% second part of the split.
 h:=car a; % H := { g_1 1, ... , g_n-1 c_n-1 }
 o:=length c;
 for k := 1:o do
 <<l:=nth(c,k);
   g2:=hillebrandstdsat(h,l);
   if not(car g2=1)then
   <<fh:=hillebrand1(g2,fact);
    fh:=for each tt in fh collect
       hillebrandgroebner hillebrandappend(tt,{car cdr a});
    f:=hillebrandappend(f,fh)>>;
   h:=append(h,{nth(c,o)})>>;
 f:=for each ff in f collect sort(ff,function hillebrandcompare);
 return f end;

% Append a basis, (if that is not empty).

symbolic procedure hillebrandappend(a,b);
if null car b then a else append(a,b);

symbolic procedure hillebrandappend1(ff,tt,b);
% append(tt,{b}).ff.
<<if b then tt:=append(tt,{b});tt.ff>>;

% Detect, if 'g' is already triangular.

symbolic procedure hillebrandtriangular(a,g,m);
% 'a' is the list of variables, 'g' is the Groebner basis. If m='t',
% a basis with a mixed leading term is rejected. If m='nil', only the
% zero - dimensionality is tested (that each variable occurs once isolated).
begin scalar b,c;
 for each gg in g do
  if domainp lc gg or not(mvar lc gg member a)then b:=mvar gg.b else c:=t;
 if m and c then return nil;
 c:=t;for each gg in g do c and(c:=hillebrandtriangular1(a,gg,b));
 return c end;

symbolic procedure hillebrandtriangular1(a,g,b);
% Test, if all variables of 'g' occur in 'b'; return
% 't' then; return 'nil' if that is not the case. 'g'
% is a standard polynomial ; the 'variables' are the leading ones.
if domainp g or not(mvar g member a)then t else
 if not(mvar g member b)then nil else
  hillebrandtriangular1(a,lc g,b)and hillebrandtriangular1(a,red g,b);

symbolic procedure hillebrandfactorizelast(g,f);
% Factorize the last polynomial of 'g' if 'f' is non-nil.
if null f then {g} else
begin scalar a,b,c,d;
 aa: if cdr g then<<a:=car g.a;g:=cdr g>>;if cdr g then go to aa;
 b:=fctrf car g;if domainp car b then b:=cdr b;
 c:=for each bb in b collect
  <<d:={car bb};for each aa in a do d:=aa.d;d>>;
 return if null cdr c then c else
  for each cc in c collect sort(cc,function hillebrandcompare)end ;

% Decompose 'g' wrt'n'-th variable 'v'.

symbolic procedure hillebranddecompose(g,v);
begin scalar a,b,c,d;
 while g do
 <<c:=car g;d:=hillebranddecompose1(c,v,vars!*,0);
  if d=1 then a:=c.a else if d=2 then b:=c.b;g:=cdr g>>;
 return reversip a.reversip cdr b end;

symbolic procedure hillebranddecompose1(p,v,vv,m);
% 'p' is a polynomial; look, if it is a product of the
% variable 'v'; return '1' if the leading factor is not a product of
% variable 'v', '2' if it is.
if domainp p or not(mvar p member vv)then m else
  hillebranddecompose1(lc p,v,vv,n)
 where n=if mvar p=v then 2 else if m #< 1 and mvar p member vv then 1 else m;

% Join 2 lists.

symbolic procedure hillebrandjoin(a,b);
% Join 'a' and 'b' if 'b' is not 'nil'.
if null b then a else append(a,b);

% Last  polynomial of a list.

symbolic procedure hillebrandlast g;
<<while cdr g do g:=cdr g; car g>>;

% Compute a Groebner basis .

symbolic procedure hillebrandgroebner g;
% Compute the Groebner basis of 'g'; return the Groebner basis as a sorted
% list of standard polynomials sorted descending.
begin scalar a,b,c,d;
 for each gg in g do
 <<d:=prepf gg;if not(d=0)then a:=d.a>>;
 b:=groebnereval{'list.a,'list.vars!*}
  where dipvars!*=dipvars!*,vdpvars!*=vdpvars!*;
 c:=for each x in cdr b collect numr simp x;
 return sort(c,function hillebrandcompare)end;

% Compute the normal form of a polynomial.

symbolic procedure hillebrandnormalform(p,g);
% Compute 'p' modulo Groebner basis 'g'.
<<p:=hillebrandf2vdp p;
 g:=for each x in g collect hillebrandf2vdp x;
 vdp2f groebnormalform(p,g,'sort)>>;

symbolic procedure hillebrandf2vdp p;
gsetsugar(a,nil)where a=f2vdp p;

% General .

symbolic procedure hillebrandmsg1 g;
if !*trgroesolv then
<<writepri(" ",'only);writepri(" Hillebrand routine;solve{",'only);
 while g do<<writepri(mkquote prepf car g,'first);g:=cdr g;
 if g then writepri(" , ",'last)>>;
 writepri(" } with respect to ",nil);
 writepri(mkquote('list.vars!*),'last);
 writepri(" ",'only);>>;

symbolic procedure hillebrandmsg2 a;
if !*trgroesolv then
<<writepri(" Decomposition by Hillebrand : ",'only);
 for each aa in a do
 <<writepri(" { ",'only);
  while aa do<<writepri(mkquote prepf car aa,'first);
               aa:=cdr aa;
               if aa then writepri(" , ",'last)>>;
  writepri(" } ",'last)>>;
 writepri(" ",'only);>>;

endmodule;;end;
