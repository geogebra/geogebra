module kredelw;% Kredel Weispfenning algorithm .

% Author: H . Melenk(ZIB Berlin).

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


symbolic procedure gdimension_eval u;
begin integer n,m;
 for each s in cdr gindependent_seteval u
  do if(m:=length cdr s) > n then n:=m;
 return n end;

put('gdimension,'psopfn,'gdimension_eval);

symbolic procedure gindependent_seteval pars;
% Independent set algorithm(Kredel/Weispfenning).
% Parameters:
%     1      Groebner basis
%     2      optional: list of variables.
begin scalar a,u,v,vars,w,oldorder,!*factor,!*exp,!*gsugar,!*groebopt;!*exp:=t;
 u:=reval car pars;
 v:=if cdr pars then reval cadr pars else nil;
 w:=for each j in groerevlist u collect if eqexpr j then !*eqn2a j else j;
 if null w then rerror(groebnr2,3,"empty list");
 a:=if global!-dipvars!* and cdr global!-dipvars!* then cdr global!-dipvars!*
  else gvarlis w;
 vars:=if null v then for each j in a collect !*a2k j else groerevlist v;
 if not vars then return'(list);
 oldorder:=vdpinit vars;
 w:=for each j in w collect vdpevlmon a2vdp j;
 vars:=for each y in vars collect y.vdpevlmon a2vdp y;
 w:=groebkwprec(vars,nil,w,nil);
 return 'list.for each s in w collect
         'list.reversip for each x in s collect car x end;

put('gindependent_sets,'psopfn,'gindependent_seteval);

symbolic procedure groebkwprec(vars,s,lt,m);
% Recursive Kredel Weispfennig algorithm.
% vars: unprocessed variables,
% s:    current subset of s,
% lt:   leading term basis,
% m:    collection of independent sets so far.
% Returns : updated m .
begin scalar x,s1,bool;
 s1:=for each y in s collect cdr y;
 while vars do
 <<x:=car vars;vars:= cdr vars;
  if groebkwprec1(cdr x.s1,lt) then m:=groebkwprec(vars,x.s,lt,m)>>;
 bool:=t;
 for each y in m do % bool and not subsetp(s,y);
 bool:=bool and not(length s=length intersection(s,y));
 return if bool then s.m else m end;

symbolic procedure groebkwprec1(s,lt);
% t if intersection of T(s) and lt is empty.
if null lt then t else groebkwprec2(s,car lt)and groebkwprec1(s,cdr lt);

symbolic procedure groebkwprec2(s,mon);
% t if monomial not in T(s).
<<for each m in s do mon:=vevcan0(m,mon);not vevzero!? mon>>;

symbolic procedure vevcan0(m,mon);
% Divide multiples of m1 out of mon.
if vevzero!? m then mon else
 if vevzero!? mon then nil else
 (if car m neq 0 then 0 else car mon).vevcan0(cdr m,cdr mon);

endmodule;;end;
