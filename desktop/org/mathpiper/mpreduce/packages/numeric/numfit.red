module numfit; % approximation of functions with least spares.

% Author: H. Melenk, ZIB, Berlin

% Copyright (c): ZIB Berlin 1991, all rights resrved

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


fluid '(!*noequiv accuracy!* singularities!*);
global '(iterations !*trnumeric numfit_count!*);

numfit_count!* := 0;

symbolic procedure numfit_gensym();
   compress append(explode 'numfit_gensym!:,
                   explode (numfit_count!* := numfit_count!* + 1));

symbolic procedure fiteval u;
     % interface function;
  begin scalar a,b,e,r,l,q,x,v,var,pars,fcn,fl,basis,pts,
        grad,oldmode,!*noequiv;
        integer n,i;
    if not(length u=3) then goto synerr;
    u := for each x in u collect reval x;
    u := accuracycontrol(u,6,200);
    fcn :=car u; u :=cdr u;
    if eqcar(fcn,'list) then fl:=cdr fcn;
    basis:=car u; u :=cdr u;
    if not eqcar(basis,'list) then goto synerr;
    basis := for each x in cdr basis collect simp reval x;
    var:= car u; u := cdr u;
    if not eqcar(var,'equal) then goto synerr;
    if not eqcar(pts:=caddr var,'list) then goto synerr;
    var := cadr var; pts:=cdr pts; n:=length pts;
    if !*trnumeric then prin2t "build generic approximation function";
    a:=nil./1;
% The names of the generated symbols here will influence the ordering of them
% as variables and hence the exact structure used to represent the formula
% constructed here. That in turn will impact the exact numerical behaviour
% when it is evaluated. If I use just the Lisp "gensym" function then the
% ordering is not well defined (at least with CSL). It is also certainly
% inconsistent as between CSL and PSL, and with CSL it can be changed if you
% merely trace something!  To get fully repeatable results from the test
% file I need to do something: hence numfit_gensym().
    for each b in basis do
    <<v:=numfit_gensym(); pars:=v.pars; a:=addsq(multsq(simp v,b),a)>>;
      % generate the error expression
    oldmode:=switch!-mode!-rd nil;!*noequiv:=nil;
    b:=a:=simp prepsq a;
    fcn:=simp if null fl then fcn else 'dummy; e:=nil./1;
    for each p in pts do
    <<i:=i+1;l:=list(var.p); % value to be substituted.
      if fl then l:=('dummy . reval nth(fl,i)).l;
      q:=subtrsq(subsq(fcn,l),subsq(b,l));
      e:=addsq(e,multsq(q,q))>>;
    e:=prepsq e;
    if !*trnumeric then
      <<lprim "error function is:";writepri(mkquote e,'only)>>;
      % find minimum.
      % build gradient.
    grad:='list . for each v in pars collect reval {'df,e,v};
    r:=rdsolveeval
       list (grad,'list . for each p in pars collect list('equal,p,0),
                  {'equal,'accuracy,accuracy!*},
                  {'equal,'iterations,iterations!*});
      % resubstitute into target function.
    if !*trnumeric then lprim "resubstituting in approximating form";
    l:=nil; pars:= nil;
    for each p in cdr r collect
       <<x:=caddr p; pars:=x.pars; l:=(cadr p.x).l>>;
    a:=prepsq subsq(a,l);
    switch!-mode!-rd oldmode;
    return list('list, a ,'list . pars);
  synerr:
    rederr "illegal parameters in fit";
  end;

put('num_fit,'psopfn,'fiteval);

endmodule;

end;
