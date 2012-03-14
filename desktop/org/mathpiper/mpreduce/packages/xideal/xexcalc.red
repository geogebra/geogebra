module xexcalc;

% Modifications to Eberhard Schruefer's excalc

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


Comment. The core routines in EXCALC have symbols:

        wedgepf2: pf,wedgepf -> wedgepf
        wedgek2:  lpow pf,lpow wedgepf -> wedgepf
        addpf: pf,pf -> pf
        addpf: wedgepf,wedgepf -> wedgepf

The overloading on addpf makes it hard to modify to use a different
order: the following routines cannot guarantee that ordering of terms
in a polynomial will be the same in pf or wedgpf representation.

endcomment;


global '(dimex!*);


symbolic procedure addpf(u,v);
% change to use termordp!! rather than ordop
   if null u then v
    else if null v then u
    else if ldpf u = 1 then addmpf(u,v)
    else if ldpf v = 1 then addmpf(v,u)
    else if ldpf u = ldpf v then
       (lambda x,y;
        if null numr x then y else ldpf u .* x .+ y)
       (addsq(lc u,lc v),addpf(red u,red v))
    else if termordp!!(ldpf u,ldpf v) then lt u .+ addpf(red u,v)
    else lt v .+ addpf(u,red v);


symbolic procedure termordp!!(u,v);
   % u,v:lpow pf|lpow wedgepf -> termordp!!:bool
   % as for termordp, but trying to accomodate wedgepf and pf terms
   u neq v and
   termordp(guesspftype u,guesspftype v);


symbolic procedure guesspftype u;
   % u:lpow pf|lpow wedgepf -> guesspftype:lpow pf
   % if we have pform x=1,y=1,x(i)=1, then we can't tell whether
   % (x y) means x^y or x(y). Here we choose the former.
   if atom u then u
   else if car u memq '(wedge d partdf hodge innerprod liedf) then u
   else if assoc(length cdr u,get(car u,'ifdegree)) and
           not xvarlistp cdr u then u
   else mknwedge u;


symbolic procedure xvarlistp x;
   % x:list of kernel -> xvarlistp:bool
   % heuristic to check if x is a list of pform variables
   null x or xvarp car x and xvarlistp cdr x;


symbolic procedure addmpf(u,v);
% add extra test for vanishing coefficient
   if null v then u
    else if ldpf v = 1 then
      (if numr x then 1 .* x .+ nil) where x = addsq(lc u,lc v)
    else lt v .+ addmpf(u,red v);


symbolic procedure deg!*form u;
%U is a prefix expression. Result is the degree of u;
% add !*sq prefix forms
   if atom u then get!*fdeg u
    else (if flagp(x,'indexvar) then get!*ifdeg u
           else if x eq 'wedge then deg!*farg cdr u
           else if x eq 'd then addd(1,deg!*form cadr u)
           else if x eq 'hodge then addf(dimex!*,negf deg!*form cadr u)
           else if x eq 'partdf then if cddr u then nil else -1
           else if x eq 'liedf then deg!*form caddr u
           else if x eq 'innerprod then addd(-1,deg!*form caddr u)
           else if x memq '(plus minus difference quotient) then
                     deg!*form cadr u
           else if x eq 'times then deg!*farg cdr u
           else if x eq '!*sq then deg!*form prepsq simp!* u
           else nil) where x = car u;


% The following two routines are copied from the development version of
% excalc to overcome an error message "+++ oddp nil" in the CSL version.


symbolic procedure oddp m;
   if not fixp m then typerr(m,"integer") else remainder(m,2) neq 0;


symbolic procedure wedgek2(u,v,w);
   if u eq car v and null eqcar(u,'wedge)
      then if (fixp n and oddp n) where n = deg!*form u then nil
            else multpfsq(wedgef(u . v),mksgnsq w)
    else if eqcar(car v,'wedge) then wedgek2(u,cdar v,w)
    else if eqcar(u,'wedge)
            then multpfsq(wedgewedge(cdr u,v),mksgnsq w)
    else if wedgeordp(u,car v)
            then multpfsq(wedgef(u . v),mksgnsq w)
    else if cdr v
            then wedgepf2(!*k2pf car v,
                          wedgek2(u,cdr v,addf(w,multf(deg!*form u,
                                                   deg!*form car v))))
    else multpfsq(wedgef list(car v,u),
                  mksgnsq addf(w,multf(deg!*form u,deg!*form car v)));


endmodule;

end;
