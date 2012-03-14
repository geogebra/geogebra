module groebman;  % Operators for manipulation of bases and
                  % polynomials in Groebner style.

flag ('(groebrestriction groebresmax gvarslast groebprotfile gltb),'share);

% control of the polynomial arithmetic actually loaded

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


symbolic procedure gsorteval pars;
% reformat a polynomial or a list of polynomials by a distributive
% ordering; a list will be sorted  and zeros are elimiated
   begin scalar vars,u,v,w,oldorder,nolist,!*factor,!*exp,!*gsugar;
         integer n,pcount!*;!*exp:=t;
      n:=length pars;
      u:=reval car pars;
      v:=if n>1 then reval cadr pars else nil;
      if not eqcar(u,'list) then
      <<nolist:=t;u:=list('list,u)>>;
      w:= for each j in groerevlist u
              collect if eqexpr j then !*eqn2a j else j;
      vars:=groebnervars(w,v);
      if not vars then vdperr 'gsort;
      oldorder:= vdpinit vars;
      !*vdpinteger:=nil;
      w:=for each j in w collect a2vdp j;
      w:=vdplsort w;
      w:=for each x in w collect vdp2a x;
      while member(0,w) do w:=delete(0,w);
      setkorder oldorder;
      return if nolist and w then car w else 'list.w end;

put('gsort,'psopfn,'gsorteval);

symbolic procedure gspliteval pars;
% split a polynomial into leading monomial and reductum;
   begin scalar vars,x,u,v,w,oldorder,!*factor,!*exp,!*gsugar;
         integer n,pcount!*;!*exp:=t;
      n:=length pars;
      u:=reval car pars;
      v:=if n>1 then reval cadr pars else nil;
      u:=list('list,u);
      w:=for each j in groerevlist u
              collect if eqexpr j then !*eqn2a j else j;
      vars:=groebnervars(w,v);
      if not vars then  vdperr 'gsplit;
      oldorder:=vdpinit vars;
      !*vdpinteger:=nil;
      w:=a2vdp car w;
      if vdpzero!? w then x:=w else
      <<x:=vdpfmon(vdplbc w,vdpevlmon w);w:=vdpred w>>;
      w:={'list,vdp2a x,vdp2a w};
      setkorder oldorder;return w end;

put('gsplit,'psopfn,'gspliteval);

symbolic procedure gspolyeval pars;
% calculate the S Polynomial from two given polynomials
   begin scalar vars,u,u1,u2,v,w,oldorder,!*factor,!*exp,!*gsugar;
         integer n,pcount!*;!*exp:=t;
      n:=length pars;
      if n<2 or n#>3 then
           rerror(groebnr2,1,"gspoly, illegal number or parameters");
      u1:= car pars;u2:= cadr pars;
      u:={'list,u1,u2};
      v:=if n>2 then groerevlist caddr pars else nil;
      w:=for each j in groerevlist u
              collect if eqexpr j then !*eqn2a j else j;
      vars:=groebnervars(w,v);
      if not vars then vdperr 'gspoly;
      groedomainmode();
      oldorder:=vdpinit vars;
      w:=for each j in w collect f2vdp numr simp j;
      w:=vdp2a groebspolynom3 (car w,cadr w);
      setkorder oldorder;return w end;

put('gspoly,'psopfn,'gspolyeval);

symbolic procedure gvarseval u;
% u is a list of polynomials; gvars extracts the variables from u
   begin integer n;scalar v,!*factor,!*exp,!*gsugar;!*exp:=t;
      n:=length u;
      v:=for each j in groerevlist reval car u collect
                      if eqexpr j then !*eqn2a j else j;
      v:=groebnervars(v,nil);
      v:=if n=2 then
         intersection (v,groerevlist reval cadr u) else v;
      return 'list.v end;

put('gvars,'psopfn,'gvarseval);

symbolic procedure greduceeval pars;
%  Polynomial reduction modulo a Groebner basis driver. u is an
% expression and v a list of expressions.  Greduce calculates the
% polynomial u reduced wrt the list of expressions v reduced to a
% groebner basis modulo using the optional caddr argument as the
% order of variables.
%     1      expression to be reduced
%     2      polynomials or equations; base for reduction
%     3      optional: list of variables
  begin scalar vars,x,u,v,w,np,oldorder,!*factor,!*groebfac,!*exp;
       scalar !*gsugar;
       integer n,pcount!*;!*exp:=t;
      if !*groebprot then groebprotfile:={'list};
      n:=length pars;
      x:=reval car pars;
      u:=reval cadr pars;
      v:=if n>2 then reval caddr pars else nil;
      w:=for each j in groerevlist u
              collect if eqexpr j then !*eqn2a j else j;
      if null w then rerror(groebnr2,2,"Empty list in greduce");
      vars:=groebnervars(w,v);
      if not vars then vdperr 'greduce;
      oldorder:=vdpinit vars;
      groedomainmode();
                  % cancel common denominators
      w:=for each j in w collect reorder numr simp j;
                  % optimize varable sequence if desired
      if !*groebopt then<<w:=vdpvordopt (w,vars);vars:=cdr w;
                          w:=car w;vdpinit vars>>;
      w:=for each j in w collect f2vdp j;
      if !*groebprot then w:=for each j in w collect vdpenumerate j;
      if not !*vdpinteger then
      <<np:=t;
        for each p in w do
          np:=if np then vdpcoeffcientsfromdomain!? p
                else nil;
        if not np then <<!*vdpmodular:= nil;!*vdpinteger:=t>> >>;
      w:=groebner2(w,nil);x:=a2vdp x;
      if !*groebprot then
          <<w:=for each j in w collect vdpenumerate j;
            groebprotsetq('candidate,vdp2a x);
            for each j in w do groebprotsetq(mkid('poly,vdpnumber j),
                                             vdp2a j)>>;
      w:=car w;
      !*vdpinteger:=nil;
      w:=groebnormalform(x,w,'sort);
      w:=vdp2a w;
      setkorder oldorder;
      gvarslast:='list.vars;
      return if w then w else 0 end;

put('greduce,'psopfn,'greduceeval);

put('preduce,'psopfn,'preduceeval);

endmodule;;end;
