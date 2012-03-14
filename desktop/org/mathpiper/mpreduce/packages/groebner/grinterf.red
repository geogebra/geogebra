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


module grinterf;% Interface of Groebner package to reduce.
% Entry points to the main module and general interface support.

flag('(groebrestriction gvarslast groebprotfile gltb glterms gmodule),'share);

switch groebopt,trgroeb,gltbasis,gsugar;

vdpsortmode!*:='lex;% Initial mode .

gltb:='(list);    % Initially empty .

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%    Interface functions .

symbolic procedure groebnereval u;
% Non factorizing Groebner calculation .
begin scalar n,!*groebfac,!*groebrm,!*factor,!*exp;!*exp:=t;
 n:=length u;
 if n=1 then return cadr groebner1(reval car u,nil,nil)
  else if n neq 2
   then rerror(groebner,1,"groebner called with wrong number of arguments");
 u:=groebner1(reval car u,reval cadr u,nil);
 if !*gltbasis then gltb:=cadr gltb;
 return cadr u end;

put('groebner,'psopfn,'groebnereval);

symbolic procedure groebnerfeval u;
% Non factorizing Groebner calculation .
begin scalar n,!*groebfac,!*groebrm,!*factor,!*exp,!*ezgcd,
 s,r,q;!*exp:=t;
 !*groebrm:=!*groebfac:=t;
 groebrestriction!*:=reval groebrestriction;
 if null dmode!* then !*ezgcd:=t;
 n:=length u;
 r:=if n=1 then groebner1(reval car u,nil,nil)else
      if n=2 then groebner1(reval car u,reval cadr u,nil)else
      if n neq 3
        then rerror(groebner,2,
            "groebner called with wrong number of arguments")
       else  groebner1(reval car u,reval cadr u,reval caddr u);
 q:=r;
       % Remove duplicates.
 while q do<<s:=car q;q:=cdr q;if member(s,q)then r:=delete(s,r)>>;
 return r end;

put('groebnerf,'psopfn,'groebnerfeval);

symbolic procedure idquotienteval u;
begin scalar n,!*factor,!*exp;!*exp:=t;
 n:=length u;
 if n=2 then return groebidq(reval car u,reval cadr u,nil)
 else if n neq 3
  then rerror(groebner,3,"idquotient called with wrong number of arguments")
   else return groebidq(reval car u,reval cadr u,reval caddr u)end;

put('idealquotient,'psopfn,'idquotienteval);

symbolic procedure saturationeval u;
begin scalar a,b,c,!*factor,!*exp;!*exp:=t;
 if length u=2 then go to aa;
 rerror(groebner,19,"saturation called with wrong number of arguments");
aa:a:=reval car u;
 if car a='list then go to bb;
 rerror(groebner,20,"saturation, first parameter must be a list");
bb:a:='list.for each aa in cdr a collect
  if eqexpr aa then reval !*eqn2a aa else aa;
 c:=reval cadr u;
 if car c='list then
  rerror(groebner,25,"saturation, second parameter must not be a list");
 if eqexpr c then c:=reval !*eqn2a c;
 while not(b=a)do<<if b then a:=b;b:=groebidq(a,c,nil)>>;return b end;

put('saturation,'psopfn,'saturationeval);

symbolic procedure groebner1(u,v,r);
% Buchberger algorithm system driver.'u'is a list of expressions
% and'v'a list of variables or nil in which case the variables in'u'
% are used.
begin scalar vars,w,np,oldorder,!*grmod!*;integer pcount!*;
 w:=for each j in groerevlist u
  collect if eqexpr j then !*eqn2a j else j;
 if null w then rerror(groebner,4,"empty list in groebner");
 vars:=groebnervars(w,v);
 if r then r:=groerevlist r;
 groedomainmode();
 if vars then go to notempty;
 u:=0;for each p in w do if p neq 0 then u:=1;
 return{'list,{'list,u}};
notempty:if dmode!* eq'!:mod!: and null setdiff(gvarlis w,vars)
  and current!-modulus < largest!-small!-modulus
   then !*grmod!*:=t;
 oldorder:=vdpinit vars;
                  % Cancel common denominators.
 w:=for each j in w collect reorder numr simp j;
                  % Optimize variable sequence if desired.
 if !*groebopt and vdpsortmode!* memq'(lex gradlex revgradlex)
  then<<w:=vdpvordopt(w,vars);vars:=cdr w;w:=car w;vdpinit vars>>;
 w:=for each j in w collect f2vdp j;
 if not !*vdpinteger then
 <<np:=t;
  for each p in w do np:=if np then vdpcoeffcientsfromdomain!? p else nil;
  if not np then<<!*vdpmodular:= nil;!*vdpinteger:=t>>>>;
 if !*groebprot then groebprotfile:={'list};
 if r then r:=for each p in r collect vdpsimpcont f2vdp numr simp p;
 w:=groebner2(w,r);
 if cdr w then  % Remove redundant partial bases.
 begin scalar !*gsugar;
  for each b in w do
   for each c in w do
    if b and b neq c then
    <<v:=t;for each p in c do v:=v and vdpzero!? groebnormalform(p,b,'list);
     if v then<<w:=delete(b,w);b:=nil>>>>end;
 if !*gltbasis then
  gltb:='list.for each base in w collect
         'list.for each j in base collect vdp2a vdpfmon(a2vbc 1,vdpevlmon j);
 w:='list.for each base in w collect 'list.for each j in base collect vdp2a j;
 vdpcleanup();gvarslast:='list.vars;return w end;

symbolic procedure groebnervars(w,v);
begin scalar z,dv,gdv,vars;
 if v='(list)then v:=nil;
 v:=v or(gdv:=cdr global!-dipvars!*)and global!-dipvars!*;
 vars:=
  if null v then
   for each j in gvarlis w collect !*a2k j
  else                      % test, if vars are really used
  <<z:=gvarlis w;
   groebnerzerobc setdiff(z,v:=groerevlist v);
   for each j in v do if member(j,z) then dv:=!*a2k j.dv;
   dv:=reversip dv;
   if not(length v=length dv)and !*trgroeb then
   <<prin2 " Groebner: ";
    prin2(length v-length dv);
    prin2t " of the variables not used";terpri()>>;dv>>;
 return gdv or vars end;

symbolic procedure groebnerzerobc u;
%'u'is the list of parameters in a Groebner job. Extract the
% corresponding rules from !*match and powlis!*.
if u then
 begin scalar w,m,p;
  bczerodivl!*:=nil;m:=!*match;!*match:=nil;p:=powlis!*;powlis!*:=nil;
  for each r in m do if cadr r='(nil.t)then
  <<w:=(numr simp{'difference,'times.for each q in car r collect
{'expt,car q,cdr q}
                 ,caddr r});
  for each x in kernels w do if not member(x,u)then w:=nil;
  if w then bczerodivl!*:=w.bczerodivl!*>>;
 for each r in p do if member(car r,u)and caddr r='(nil.t)then
 <<w:=(numr simp{'difference,{'expt,car r,cadr r},cadddr r});
  bczerodivl!*:=w.bczerodivl!*>>;
 for each r in asymplis!* do if member(car r,u)then
  bczerodivl!*:=(r .* 1 .+ nil).bczerodivl!*;
 !*match:=m;powlis!*:=p end;

% Hier

symbolic procedure gvarlis u;
% Finds variables(kernels)in the list of expressions u.
sort(gvarlis1(u,nil),function ordop);

symbolic procedure gvarlis1(u,v);
if null u then v else union(gvar1(car u,v),gvarlis1(cdr u,v));

symbolic procedure gvar1(u,v);
if null u or numberp u or(u eq'i and !*complex)then v
 else if atom u then if u member v then v else u.v
 else if get(car u,'dname)then v
 else if car u memq'(plus times expt difference minus)
  then gvarlis1(cdr u,v)
 else if car u eq'quotient then gvar1(cadr u,v)
 else if u member v then v else u.v;

symbolic procedure groebidq(u,f,v);
% Ideal quotient.'u'is a list of expressions(gbasis),'f'a polynomial
%  and'v'a list of variables or nil.
begin scalar vars,w,np,oldorder,!*factor,!*exp;integer pcount!*;
 !*exp:=t;
 w:=for each j in groerevlist u
  collect if eqexpr j then !*eqn2a j else j;
 if null w then rerror(groebner,5,"empty list in idealquotient");
 if eqexpr f then f:=!*eqn2a f;
 vars:=groebnervars(w,v);
 groedomainmode();
 if null vars then vdperr'idealquotient;
 oldorder:=vdpinit vars;
                  % Cancel common denominators.
 w:=for each j in w collect numr simp j;
 f:=numr simp f;
 w:=for each j in w collect f2vdp j;
 f:=f2vdp f;% Now do the conversions.
 if not !*vdpinteger then
 <<np:=t;
  for each p in f.w do
   np:=if np then vdpcoeffcientsfromdomain!? p else nil;
  if not np then <<!*vdpmodular:= nil;!*vdpinteger:=t>> >>;
 w:=groebidq2(w,f);
 w:='list.for each j in w collect vdp2a j;
 setkorder oldorder;
 return w end;

fluid'(!*backtrace);

symbolic procedure vdperr name;
% Case that no variables were found.
<<prin2 "**** Groebner illegal parmeter in ";prin2 name;
 if !*backtrace then backtrace();
 rerror(groebner,6,",e.g. no relevant variables found")>>;

symbolic procedure groeparams(u,nmin,nmax);
%'u'is a list of psopfn-parameters;they are given to reval and
% the number of parameters is controlled to be between nmin,nmax;
% result is the list of evaluated parameters padded with nils.
begin scalar n,w;n:=length u;
 if n < nmin or n > nmax then rerror(groebner,7,
         "illegal number of parameters in call to groebner package");
 u:=for each v in u collect
 <<w:=reval v;
  if eqcar(w,'list)then'list.groerevlist w else w>>;
 while length u < nmax do u:=append(u,'(nil));return u end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Initialization of the distributive polynomial arithmetic.
%

symbolic procedure vdpinit vars;
begin scalar r,gm;
% Eventually set up module basis.
 if eqcar(gmodule,'list)and cdr gmodule then
  gm:=for each y in cdr gmodule collect
  <<y:=reval y;if not member(y,vars)then vars:=append(vars,{y});y>>;
  r:=vdpinit2 vars;
          % Convert an eventual module basis.
  gmodule!*:=if gm then vdpevlmon a2vdp('times.gm);return r end;

symbolic procedure groedomainmode();
<<!*vdpinteger:=!*vdpmodular:=nil;
 if not flagp(dmode!*,'field)then !*vdpinteger:=t
  else if !*modular then !*vdpmodular:=t>>;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% Some lisp functions which are not member of standard lisp.
%

symbolic procedure groedeletip(a,b);
begin scalar q;
 while b and a=car b do b:=cdr b;if null b then return nil;
 q:=b;
 while cdr b do if a=cadr b then cdr b:=cddr b else b:=cdr b;
 return q end;

symbolic procedure groerevlist u;
<<if idp u then u:=reval u;for each p in getrlist u collect reval p>>;

endmodule;;end;
