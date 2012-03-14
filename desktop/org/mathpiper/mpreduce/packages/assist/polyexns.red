module polyexns;

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


% Additional functions which manipulate polynomials.

switch distribute;

symbolic procedure fix_or_str u;
fixp u or stringp u;

symbolic procedure rgcdnl u;
% Searches the common gcd of all the integers inside the list u.
( if length x = 1 then abs car x else
 eval expand(x,'gcdn) ) where x=cdr revlis car u;

put('gcdnl,'psopfn,'rgcdnl);

symbolic procedure alg_to_symb u;
% transforms standard quotient expressions into prefix symbolic ones.
% dd => (LIST 1 (!*SQ ((((A . 2) . 1)) . 1) T)
% !*SQ ((((A . 1) . 1)) . 1) T) 3 (LIST 4))
% alg_to_symb dd ==> (1 (EXPT A 2) A 3 (4))
%
if null u  then nil else
if atom u then  u else
if car u neq 'list then reval u else
if car u eq 'list  then
  for each i in cdr u collect alg_to_symb i;

symbolic procedure symb_to_alg u;
% transforms prefix lisp list into an algebraic list.
% if null u then nil else
if null u then list('list) else
if fix_or_str u  then u else
if atom u then mk!*sq simp!* u  else
if listp u and  (getd car u or get(car u,'simpfn) )
                                 then  mk!*sq simp!* u else
if atomlis u then 'list . for each i in u collect
        if null i then list('list) else
        if fix_or_str i then i else
                            mk!*sq simp!* i
else
  'list . for each i in u collect symb_to_alg i ;

algebraic procedure mkdepth_one x;
% Flattens an algebraic list.
% Not clear if it is really useful.
lisp symb_to_alg flattens1 alg_to_symb algebraic x;

% Elementary functions to manipulate polynomials in
% a DISTRIBUTIVE way.

symbolic procedure addfd (u,v);
% It contains a modification to ADDF to avoid
% a recursive representation.
% U and V are standard forms. Value is a standard form.
if null u then v
else if null v then u
else if  domainp u then addd(u,v)
else if  domainp v then addd(v,u)
else if ordp(lpow u,lpow v)
then lt u .+ addfd(red u,v)
else lt v .+ addfd (u,red v);

symbolic procedure distribute u;
% Works ONLY when RATIONAL is ON.
 begin scalar s, !*rational;
!*rational:=t;
 s:=simp!* u;
 return mk!*sq (distri!_pol(numr s) ./ denr s)
end;

flag('(distribute),'opfn);

symbolic procedure distri!_pol u;
% This function assumes that u is a polynomial given
% as a standard form. It transforms its recursive representation into
% a distributive representation.
if null u then nil else
if atom u then u else
if red u  then
   addfd(distri!_pol !*t2f lt u,distri!_pol red u)
     else
 begin scalar x,y;
 x:=1 ;
 y:=u;
 while  not atom y and null red y do
                        <<x:=multf(x,!*p2f lpow y); y:=lc y>>;
 if atom y then return multf(x,y) else
 return
 addfd(distri!_pol multf(x,distri!_pol !*t2f lt y),
       distri!_pol multf(x,distri!_pol red y))
end;

symbolic procedure leadterm u;
<<u:=simp!* u; if !*distribute  then u:=distri!_pol numr u ./ denr u
  else u; if domainp u then mk!*sq u
  else  mk!*sq(!*t2f lt numr u ./ denr u)>>;

flag('(leadterm redexpr ),'opfn);

symbolic procedure redexpr u;
<<u:=simp!* u; if !*distribute  then u:=distri!_pol numr u ./ denr u
  else u; if domainp u then mk!*sq(nil ./ 1) else
  mk!*sq( red numr u ./ denr u)>>;

% Various decompositions.

symbolic procedure list!_of!_monom u;
% It takes a polynomial in distributive form.
% returns a list of monoms.
% u is numr simp!* (algebraic expression)
% if domainp u then u else  ELIMINATED
begin scalar exp,lmon,mon;
     exp:=u;
% l:  if null exp then return lmon ; OLD statement
l:  if null exp then return lmon else
    if domainp exp then return exp . lmon ;
    mon:=if atom exp then exp else lt exp;
    lmon:=(!*t2f mon ) . lmon;
    exp:=red exp;
     go to l;
end;

symbolic procedure monom y;
if !*rational then rederr "MONOM does only work on rings of integers"
else
begin scalar x;
x:=numr simp!* y;
x:=distri!_pol x;
x:=reversip list!_of!_monom x;
x:=for each m in x collect mk!*sq(m ./ 1);
return 'list . x end;

flag('(monom),'opfn);

symbolic procedure coeff_mon u;
% argument is lt numr simp!* "algebraic value".
if atom u then u else
coeff_mon((if atom x then x else lt x)where x=red u);

algebraic procedure list_coeff_pol u;
% Gives the list of coefficients of multivariate polynomial u.
% Terms are distributed.
for each i in monom u collect (lisp coeff_mon (if atom i then i else
                                                    lt numr simp!* i));

algebraic procedure norm_mon u;
% Sets the coefficient of the monom u to 1.
if u=0 then 0 else u/(lisp coeff_mon lt numr simp!* algebraic u);

algebraic procedure norm_pol u;
% Tries to put the leading coefficient to 1 i.e. u to normal form.
% If not, it puts the coefficient of the leading term positive.
if u=0 then 0 else
        begin scalar uu,sign;
        uu:=list_coeff_pol u;
        sign:=first uu /(abs first uu);
       if gcdnl uu = abs first uu then return u:= u/first uu
        else return sign * u
        end ;

symbolic procedure pol_ordp(u,v);
% u and v are multivariate polynomials.
% General ordering function.
(x<y or (x=y and ordp(u,v))) where x = length u, y = length v;

flag('(pol_ordp),'opfn);

symbolic procedure !&dpol u$
% Returns a list which contains the quotient and
% the remainder.
if length u neq 2 then rederr "divpol must have two arguments"
else
begin scalar poln,pold,aa,ratsav$
if lisp (!*factor) then off factor; % This restriction is
                                  % necessary for some implementatins .
    poln:= simp!* car u$
    pold:= simp!* cadr u$
    if denr poln neq 1 or denr pold neq 1 then
    rederr(" arguments must be polynomials")$
    poln:=numr poln$ pold:=numr pold$
    if lc poln neq 1 or lc poln neq lc pold then
                       <<ratsav:=lisp (!*rational); on rational>>;
    aa:=qremf(poln,pold)$
 aa:=mksq(list('list ,prepsq!*( car aa . 1), prepsq!*(cdr aa . 1)),1)$
    if not ratsav then off rational;
    return  aa end$

put('divpol,'simpfn,'!&dpol)$

symbolic procedure lowestdeg(u,v)$
% It extracts the lowest degree in v of the polynomial u.
 begin scalar x,y,uu,vv;
    uu:=simp!* u$
   if domainp uu then return 0;
    uu:=!*q2f uu;
    vv:=erase_pol_cst uu;
    if vv neq uu then return 0;
    vv:=!*a2k v;
    x:=setkorder list v;
    y:=reorder uu; setkorder x;
    y:=reverse y;
    uu:=mvar y;
    if not atom uu then if car uu eq 'expt then
         rederr("exponents must be integers")$
    if uu neq vv then return 0 else
        return  ldeg y
 end;

flag('(lowestdeg),'opfn)$

symbolic procedure erase_pol_cst u;
% u is a standard form.
if null u or numberp u then nil
 else lt u . erase_pol_cst red u;


% Splitting functions.

% For instance 'splitterms' returns a list of plus-terms and minus-terms.

symbolic operator splitterms;

symbolic procedure splitterms u;
   begin scalar a,b;
     if fixp u and evallessp(u, 0) then return
           'list . ('list . 0 . nil) . ('list .  list('minus, u)
                                                       . nil) . nil
     else if
        atom u  or not(car u member(list('plus,'minus))) then  return
           'list . ('list . u . nil) . ('list . 0 . nil) . nil
     else if
        car u eq 'minus then return
           'list . ('list . 0 . nil) . ('list . cdr u) . nil;
     while(u:=cdr u) do
        if atom car u or not (caar u eq 'minus) then a:= car u . a
         else b:=cadar u . b;
     if null a then a:=0 . nil;
     if null b then b:=0 . nil;
     return 'list . ('list . reversip a) . ('list . reversip b) . nil;
   end;

algebraic procedure splitplusminus(u);
% Applies to rational functions.
% u ==> {u+,u-}
 begin scalar uu;
  uu:=splitterms num u;
  return list((for each j in first uu sum j) /den u,
            - (for each j in second uu sum j)/den u)
 end;

endmodule;

end;
