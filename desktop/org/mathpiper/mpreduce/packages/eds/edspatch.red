module edspatch;

% Various patches for other parts of Reduce.

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


fluid '(!*edsverbose !*edsdebug !*arbvars !*varopt !*groebopt
        !*solveinconsistent depl!*);

%%% General changes

% Extend MAP/SELECT to other structures than list/matrix

symbolic procedure map!-eval1(o,q,fcn1,fcn2);
 % o       structure to be mapped.
 % q       map expression (univariate function).
 % fcn1    function for evaluating members of o.
 % fcn2    function computing results (e.g. aeval).
 map!-apply(map!-function(q,fcn1,fcn2),o);

symbolic procedure map!-function(q,fcn1,fcn2);
   begin scalar v,w;
   v := '!&!&x;
   if idp q
      and (get(q,'simpfn) or get(q,'number!-of!-args)=1)
   then <<w:=v; q:={q,v}>>
   else if eqcar(q,'replaceby) then
      <<w:=cadr q; q:=caddr q>>
   else
   <<w:=map!-frvarsof(q,nil);
      if null w then rederr "map/select: no free variable found" else
         if cdr w then rederr "map/select: free variable ambiguous";
      w := car w;
   >>;
   if eqcar(w,'!~) then w:=cadr w;
   q := sublis({w.v,{'!~,w}.v},q);
   return {'lambda,{'w},
      {'map!-eval2,'w,mkquote v,mkquote q,mkquote fcn1,mkquote fcn2}};
   end;

symbolic procedure map!-apply(f,o);
   if atom o then apply1(f,o)
   else (if m then apply2(m,f,o)
               else car o . for each w in cdr o collect apply1(f,w))
                   where m = get(car o,'mapfn);

symbolic procedure mapmat(f,o);
   'mat . for each row in cdr o collect
      for each w in row collect
         apply1(f,w);

put('mat,'mapfn,'mapmat);


%%% EXCALC modifications

global '(indxl!*);
fluid '(kord!*);

% Remove covariant flag from indexed 0-forms.
% Add a subfunc to indexed forms.

if not getd 'excalcputform then copyd('excalcputform,'putform);

symbolic procedure putform(u,v);
   begin
   excalcputform(u,v);
   if not atom u then
   << put(car u,'subfunc,'(lambda (a b) b));
      remflag({car u},'covariant) >>;
   end;


% Have to update ndepends to REDUCE3.6 depends.

symbolic procedure ndepends(u,v);
   if null u or numberp u or numberp v then nil
    else if u=v then u
    else if atom u and u memq frlis!* then t
      %to allow the most general pattern matching to occur;
    else if (lambda x; x and lndepends(cdr x,v)) assoc(u,depl!*)
     then t
    else if not atom u and idp car u and get(car u,'dname) then
        (if depends!-fn then apply2(depends!-fn,u,v) else nil)
           where (depends!-fn = get(car u,'domain!-depends!-fn))
    else if not atomf u
      and (lndepends(cdr u,v) or ndepends(car u,v)) then t
    else if atomf v or idp car v and get(car v,'dname) then nil
    % else ndependsl(u,cdr v);
    else nil;

%%% Make depends from ALG into ndepends from EXCALC (identical except
%%% for atomf test which treats indexed variables as atoms).

copyd('depends,'ndepends);

symbolic procedure lndepends(u,v);
   % Need to allow the possibility that U is an atom because the int
   % package calls depends with sq instead of prefix.
   if null u then nil
    else if atom u then ndepends(u,v)
    else ndepends(car u,v) or lndepends(cdr u,v);

% changed first u -> v (error otherwise)

symbolic procedure ndependsl(u,v);
   v and (ndepends(u,car v) or ndependsl(u,cdr v));


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%        OLD PATCHES: REMOVE ONCE IN PATCHES.RED!!!             %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


fluid '(!*edsverbose !*edsdebug !*arbvars !*varopt !*groebopt
        !*solveinconsistent depl!*);


%%% SOLVE changes

% Changed depl!* line to work for non-atom kernels as well.

fluid '(!*expandexpt);   % from simp.red

fluid '( system!*        % system to be solved
         osystem!*       % original system on input
         uv!*            % user supplied variables
         iv!*            % internal variables
         fv!*            % restricted variables
         kl!*            % kernels to be investigated
         sub!*           % global substitutions
         inv!*           % global inverse substitutions
         depl!*          % REDUCE dependency list
         !*solvealgp     % true if using this module
         solvealgdb!*    % collecting some data
         last!-vars!*    % collection of innermost aux variables
         const!-vars!*   % variables representing constants
         root!-vars!*    % variables representing root expressions
         !*expli         % local switch: explicit solution
         groebroots!*    % predefined roots from input surds
         !*test_solvealg % debugging support
         !*arbvars
       );

fluid '(!*trnonlnr);
  % If set on, the modified system and the Groebner result
  % or the reason for the failure are printed.

global '(loaded!-packages!* !!arbint);

symbolic procedure solvenonlnrsys2();
  % Main driver. We need non-local exits here
  % because of possibly hidden non algebraic variable
  % dependencies.
  if null !*solvealgp then system!*:='(FAILED) else % against recursion.
  (begin scalar iv!*,kl!*,inv!*,fv!*,r,w,!*solvealgp,solvealgdb!*,sub!*;
         scalar last!-vars!*,groebroots!*,const!-vars!*,root!-vars!*;
         % preserving the variable sequence if *varopt is off
%      if not !*varopt then depl!* :=
%        append(pair(uv!*,append(cdr uv!*,{gensym()})),depl!*);
      if not !*varopt then depl!* :=
        append(foreach l on uv!* collect l,depl!*);
         % hiding dmode because exponentials need integers.
      for each f in system!* do solvealgk0
         (if dmode!* then numr subf(f,nil) where dmode!*=nil else f);
      if !*trnonlnr then print list("original system:",system!*);
      if !*trnonlnr then print list("original kernels:",kl!*);
      if null cdr system!* then
          if (smemq('sin,system!*)or smemq('cos,system!*)) and
             (r:=solvenonlnrtansub(prepf(w:=car system!*),car uv!*))
             and car r
            then return solvenonlnrtansolve(r,car uv!*,w)
           else if (smemq('sinh,system!*)or smemq('cosh,system!*)) and
             (r:=solvenonlnrtanhsub(prepf(w:=car system!*),car uv!*))
             and car r
            then return solvenonlnrtanhsolve(r,car uv!*,w);
      if atom (errorset('(solvealgk1),!*trnonlnr,nil))
               where dmode!*=nil
         then return (system!*:='(FAILED));
      system!*:='LIST.for each p in system!* collect prepf p;
      if not('groebner memq loaded!-packages!*)
        then load!-package 'groebner;
      for each x in iv!* do if not member(x,last!-vars!*) then
        for each y in last!-vars!* do depend1(x,y,t);
      iv!* := sort(iv!*,function (lambda(a,b);depends(a,b)));
      if !*trnonlnr then
      <<  prin2t "Entering Groebner for system";
          writepri(mkquote system!*,'only);
          writepri(mkquote('LIST.iv!*), 'only);
      >>;
      r := list(system!*,'LIST.iv!*);
      r := groesolveeval r;
      if !*trnonlnr then
      <<  prin2t "leaving Groebner with intermediate result";
          writepri(mkquote r,'only);
          terpri(); terpri();
      >>;
      if 'sin memq solvealgdb!* then r:=solvealgtrig2 r;
      if 'sinh memq solvealgdb!* then r:=solvealghyp2 r;
      r:= if r='(LIST) then '(INCONSISTENT) else solvealginv r;
      system!* := r;  % set value aside
      return r;
  end) where depl!*=depl!* ;


% Make variable dependency override reordering.


fluid '(!*trsparse);

symbolic procedure solvesparsecheck(sys,vl);
   % sys: list of sf, vl: list of kernel
   % -> solvesparsecheck: nil or {list of sf,list of kernel}
   % This program checks for a sparse linear system. If the
   % system is sparse enough, it returns (exlis.varlis) reordered
   % such that a maximum triangular upper diagonal form is
   % established. Otherwise the result is NIL.
   begin scalar vl1,xl,sys1,q,x,y;
         integer sp;

   % First collect a list vl1 where each variable is followed
   % by the number of equations where it occurs, and then
   % by the number of other variables which occur in these
   % equations (connectivity). At the same time, collect a measure
   % of the sparsity.
   sp:=0;
   vl1:= for each x in vl collect x . 0 . nil;
   foreach q in sys do
      foreach x in (xl := intersection(topkerns q,vl)) do
       <<y := assoc(x,vl1);
         cdr y := (cadr y + 1) . union(xl,cddr y);
         sp := sp + 1>>;
   foreach p in vl1 do
      cddr p := length cddr p - 1; % could drop the -1

   % Drop out if density > 80%
   if sp > length sys * length vl * 0.8 then
    <<if !*trsparse then prin2t "System is not very sparse";
      return nil>>;

   % If varopt is on, sort variables first by least occurrences and then
   % least connectivity, but allow dependency to override.
   % Reset kernel order and reorder equations.
   if !*trsparse then
     solvesparseprint("Original sparse system",sys,vl);

   if !*varopt then
   << vl1 := sort(vl1,function solvevarordp);
      vl1 := foreach x in vl1 collect car x;
%      if !*trsparse then lpriw("Optimal variable order:",vl1);
%      foreach k in reverse vl1 do updkorder k;
%      vl1 := sort(vl1,function solvevarordp1);
      vl1 := solvevaradjust vl1;
%      if !*trsparse then lpriw("Adjusted variable order:",vl1);
      foreach k in reverse vl1 do updkorder k;
      sys := for each q in sys collect reorder q >>
   else vl1 := foreach x in vl1 collect car x;

   % Next sort equations in ascending order of their first variable
   % and then descending order of the next variable.
   sys1:= (nil . nil) . foreach x in vl1 collect x . nil;
   foreach q in sys do
      <<if domainp q or not member(mvar q,vl1) then y := assoc(nil,sys1)
        else y := assoc(mvar q,sys1);
        cdr y := q . cdr y>>;
   foreach p in cdr sys1 do
      if cdr p then cdr p := sort(cdr p, function solvesparsesort);

   % Finally split off a leading diagonal system and push the remaining
   % equations down.
   sys := nconc(foreach p in sys1 join if cdr p then {cadr p},
                reversip foreach p in sys1 join if cdr p then cddr p);
   if !*trsparse then
      solvesparseprint("Variables and/or equations rearranged",sys,vl1);
   return sys.vl1;
   end;


symbolic procedure solvevarordp(x,y);
   cadr x < cadr y or
   cadr x = cadr y and cddr x < cddr y;

symbolic procedure solvevarordp1(x,y);
   % This is incomplete, since it is not transitive
   depends(x,y) or
   not depends(y,x) and ordop(x,y);


symbolic procedure solvevaradjust u;
   begin scalar v,y;
   if null u then return nil;
   v := foreach x in u join
              << y := assoc(x,depl!*);
            if null y or null xnp(cdr y,u) then {x} >>;
   return nconc(solvevaradjust setdiff(u,v),v);
   end;

% Usually solve goes to the Cramer method since the expressions
% contain exponentials. The Bareiss code should work, so disable this.

symbolic procedure exptexpflistp u;
   nil;

endmodule;

end;
