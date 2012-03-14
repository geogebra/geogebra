module edsexptl;

% Experimental (algebraic mode) operators

% Author: David Hartley

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


% These procedures need the other packages loaded during compilation

load_package 'xideal;

%%%% Characteristic variety, symbol relations and symbol matrix

Comment. At present, algebraic routines.

endcomment;

fluid '(!*varopt !*arbvars xvars!* !*allbranch);


symbolic operator indexnames;
symbolic procedure indexnames u;
   begin
   u := makelist uniqids foreach k in getrlist u collect !*a2k k;
   apply1('indexrange,{{'equal,gensym(),u}});
   return u;
   end;


algebraic procedure symbol_relations(S,name);
   % S:eds, name:id -> symbol_relations:list of 1-form
   begin scalar tbl,ix,sys,pis,!*varopt,!*arbvars;
   pform name(i,j) = 1;
   tbl := tableau S;
   ix := indexnames independence S;
   for i:=1:first length tbl do
      indexrange !!symbol!!index=i;
   pis := for i:=1:first length tbl collect
      foreach j in ix collect name(i,-j);
   sys := for i:=1:first length tbl join
      for j:=1:length ix collect (tbl(i,j) - part(pis,i,j));
   pis := foreach l in pis join l;
   sys := first solve(sys,append(cobasis s,pis));
   sys := foreach x in sys join
      if lhs x member pis then {lhs x - rhs x} else {};
   return sys;
   end;


algebraic procedure symbol_matrix(S,name);
   % S:eds, name:id -> symbol_matrix:matrix of 0-form
   begin scalar sys,wlist,n;
   pform name(i) = 0,{!!symbol!!pi(i,j),!!symbol!!w(i)}=1;
   n := first length tableau S;
   wlist := for i:=1:n collect !!symbol!!w(i);
   sys := symbol_relations(S,!!symbol!!pi);
   rl := for i:=1:n join
      foreach j in indexnames independence S collect
          make_rule(!!symbol!!pi(i,-j),!!symbol!!w(i)*name(-j));
   let rl;
%   sys := (sys where rl);
   sys := sys;
%   write showrules !!symbol!!pi;
   clearrules rl;
   matrix !!symbol!!mat(length sys,length wlist);
   for i:=1:length sys do
      for j:=1:length wlist do
         !!symbol!!mat(i,j) := coeffn(part(sys,i),part(wlist,j),1);
   return !!symbol!!mat;
   end;


algebraic procedure characteristic_variety(S,name);
   % S:eds, name:id -> characteristic_variety:{list of 0-form,list of
   % variable}
   begin scalar ix,m,sys;
         scalar xvars!*;  % make all 0-forms coefficients
   ix := indexnames independence S;
   m := symbol_matrix(S,name);
   if first length m > second length m then m := tp m;
   for i:=1:second length m do
      indexrange symbol!!index!!=i;
   wlist := for i:=1:second length m collect !!symbol!!w(i);
   www := 1;
   for i:=1:first length m do
      www := (for j:=1:length wlist sum m(i,j)*!!symbol!!w(j))^www;
   return {excoeffs www,foreach i in ix collect name(-i)};
   end;


algebraic procedure make_rule(lh,rh);
   lh => rh;


%%% Invariants, or first integrals.

fluid '(!*edsdebug print_ fname_ time_ xvars!* !*allbranch !*arbvars);


mkform!*('eds!:t,0);

algebraic procedure edsorderp(x,y);
   % Just a hook for sort
   if ordp(x,y) then 1 else 0;

put('invariants,'psopfn,'invariants);

symbolic procedure invariants u;
   if length u = 2 then
      (algebraic invariants0(x,y)) where x=car u, y=cadr u
   else if length u = 1 then
      (algebraic invariants0(x,y)) where x=car u, y=makelist nil
   else
      rederr "Wrong number of arguments to invariants";


algebraic procedure invariants0(S,C);
   begin scalar ans,inv,cfrm,Z,xvars!*;
   load_package odesolve,crack;
   % Update for CRACK version 1-Dec-2002
   setcrackflags();
   cfrm := coframing();
   if part(S,0) = eds then
   << set_coframing S;
      if C = {} then C := coordinates S;
      S := systemeds S >> % Use systemeds rather than system for
                          % compiler.
   else
      S := xauto S;
   if C = {} then C := reverse sort(coordinates S,edsorderp);
   Z := for a:=1:length S collect lisp mkform!*(mkid('eds!:u,a),0);
   ans := foliation(S,C,Z);
   inv := solve(ans,Z);
   if length Z = 1 then
      inv := foreach x in inv collect {x};
   if lisp !*edsdebug then write "Constants";
   if lisp !*edsdebug then write inv;

   if length inv neq 1 then
      rederr "Not a unique solution";

   set_coframing cfrm;
   return foreach x in first inv collect rhs x;
   end;


algebraic procedure foliation(S,C,Z);
   begin scalar r,n,x,S0,Z0,g,Q,f,f0;
               scalar print_,fname_,time_,!*allbranch,!*arbvars,xvars!*;
   % Constants
   r := length S;
   n := length C;
   fname_ := 'eds!:c;
   % Deal with errors and end case
   if r > n then
      rerror(eds,000,"Not enough coordinates in foliation");
   if r neq length Z then
      rerror(eds,000,"Wrong number of invariant labels in foliation");
   if r = n then
   << g := for a:=1:r collect part(C,a) = part(Z,a);
      lisp edsdebug("Intermediate result",g,'prefix);
      return g >>;
   % Choose truncation
   S0 := {}; Z0 := {};
   while length S0 < r do
   << x := first C;
      C := rest C;
      Z0 := x . Z0;
      S0 := xauto(S xmod {d x}) >>;
   C := append(C,rest Z0);
   lisp edsdebug("Truncating coordinate : ",x,'prefix);
   % Compute foliation for truncation
   g := foliation(S0,C,Z);
   % Calculate ODE
   foreach y in Z do
   << lisp(y := !*a2k y); fdomain y=y(eds!:t) >>;
   S := pullback(S,g);
   S := pullback(S,{x = eds!:t});
   Q := foreach f in S collect @eds!:t _| f;
   Q := solve(Q,foreach y in Z collect @(y,eds!:t));
   if r neq 1 then Q :=  first Q;
   Q := foreach f in Q collect (lhs f - rhs f);
   Q := sub(partdf=df,Q);
   lisp edsdebug("CRACK ODE",Q,'prefix);
   % Solve ODE
   Q := crack(Q,{},Z,{});
   % Restore 0-form properties of Z (cleared by CRACK)
   foreach y in Z do
      << lisp(y := !*a2k y); lisp mkform!*(y, 0) >>;
   lisp edsdebug("CRACK solution",Q,'prefix);
   % Analyse result for the general solution
   f := {};
   while Q neq {} do
   << f := first Q; Q := rest Q;
      Z0 := third f;
      if first f = {} and length Z0 = r then Q := {}
      else if length Z0 > r then
               if length(f0 := solve(first f,Z)) = 0 then f := {}
               else
               << if r = 1 then f0 := {{first f0}};
            Z0 := foreach v in Z0 join
               if v member Z then {} else {v};
            f := {{},append(second f,first f0),Z0};
             Q := {} >>
      else f := {} >>;
   foreach y in Z do
      << lisp(y := !*a2k y); remfdomain y >>;
   if f = {} then
      rerror(eds,000,"Intermediate ODE general solution not found");
   % Compose general solution with truncated foliation
   g := sub(second f,g);
   f := (eds!:t = x) . for a := 1:r collect part(Z0,a) = part(Z,a);
   g := sub(f,g);
   lisp edsdebug("Intermediate result",g,'prefix);
   return g;
   end;


%%% Homotopy operator

algebraic procedure poincare df;
   % with df a closed p-form POINCARE returns a (p-1)-form f
   % satisfying df=d f.
   begin scalar f;
   pform !!lambda!! = 0;
   f := sub(for each c in coordinates df collect c = c * !!lambda!!,df);
%   f := sub(for each c in allcoords df collect c = c * !!lambda!!,df);
   f := @(!!lambda!!) _| f;
   f := int(f,!!lambda!!);
   f := sub(!!lambda!! = 1,f) - sub(!!lambda!! = 0,f);
%   if d f - df neq 0 then write "error in POINCARE";
   return reval f;
   end;


%%% Integrability conditions

put('integrability,'rtypefn,'quotelist);
put('integrability,'listfn,'evalintegrability);

symbolic procedure evalintegrability(s,v);
   % s:eds|rlist, v:bool -> evalintegrability:rlist
   if edsp(s := reval car s) then
      !*sys2a1(nonpfaffpart eds_sys edscall closure s,v)
   else
      algebraic append(s xmod one_forms s,d s xmod one_forms s);

endmodule;

end;
