module ncdip; % Non-commutative distributive polynomials.

% Author: H. Melenk, ZIB Berlin, J. Apel, University of Leipzig.

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


symbolic procedure ncdsetup!* u;
 % U is a list of algebraic arguments:
 % 1. list of variables,
 % 2. list of commutator relations in explicit form x*y=y*x + r
 %    where ord(r) < ord(x*y) .
 % All variable pairs whitch do not occur here are considered
 % communtative.
  begin scalar x,y,w,vars,lh,z,lv,r,q;
    vars := for each x in cdr listeval(car u,nil)
       collect reval x;
    ncdipcircular!*:=nil;
    if null vdpsortmode!* then vdpsortmode!*:= 'lex;
    vdpinit2 (ncdipvars!*:=vars);
    lv:=length vars;
    ncdipbase!*:=mkvect lv;
    ncdiptable!*:=mkvect lv;
    for i:=1:lv do putv(ncdiptable!*,i,mkvect lv);
    q:=cdr listeval(cadr u,nil);
    while q do
    <<r:=car q;q:=cdr q;
     if (eqcar(r,'equal) or eqcar(r,'replaceby)) and
          eqcar(lh:=cadr r,'times) and
          null cdddr r and
          (w:=member(y:=caddr lh,vars)) and
          member(x:=cadr lh,w)
       then
       << % does any variable other than x or y appear in the rhs?
          if smember(x,q) or smember(y,q) then ncdipcircular!*:=t;
          for each v in vars do
           if v neq x and v neq y and smember(v,caddr r) then
            ncdipcircular!*:=t;
           % establish the commutator in DIP form.
          w:=ncdipndx(x,vars,1).ncdipndx(y,vars,1);
          r:=a2dip(z:=reval caddr r);
          if evcomp(dipevlmon r,dipevlmon a2dip {'times,x,y})<0 then
             typerr({'equal,{'times,x,y},z},
               "commutator under current term order");
          getv(ncdipbase!*,car w):= sort(cdr w.getv(ncdipbase!*,car w),'lessp);
          getv(getv(ncdiptable!*,car w),cdr w):= {'(1 . 1).r}>>
       else typerr(r,"commutator ")>>end;

symbolic procedure ncdipndx(x,vars,n);
if null vars then 0 else if x=car vars then n else ncdipndx(x,cdr vars,n #+ 1);

%------------ noncom multiply ----------------------------

symbolic procedure vdp!-nc!-m!*p(bc,ev,p);
  % multiply polynomial p left by monomial (bc,ev).
  begin scalar r,s;
   r:=dip2vdp dip!-nc!-m!*p(bc,ev,vdppoly p);
   if !*gsugar then
   <<s:=gsugar p; for each e in ev do s:=s+e;r:=gsetsugar(r,s)>>;
   return r end;

symbolic procedure vdp!-nc!-prod(u,v);
  % non-commutative product of two distributive polynomials.
  begin scalar r;
   r:=dip2vdp dip!-nc!-prod(vdppoly u,vdppoly v);
   if !*gsugar then r:=gsetsugar(r,gsugar u + gsugar v);
   return r end;

symbolic procedure dip!-nc!-prod(u,v);
  % We distribute first over the shorter of the two factors.
  if length u < length v then dip!-nc!-prod!-distleft(u,v)
        else dip!-nc!-prod!-distright(u,v);

symbolic procedure dip!-nc!-prod!-distleft(u,v);
  if dipzero!? u then u else
   dipsum(dip!-nc!-m!*p!-distleft(diplbc u,dipevlmon u,v),
          dip!-nc!-prod!-distleft(dipmred u,v));

symbolic procedure dip!-nc!-m!*p!-distleft(bc,ev,p);
   if dipzero!? p then nil else
   begin scalar lev,lbc,q;
     lev:=dipevlmon p;lbc:=diplbc p;
     p:=dip!-nc!-m!*p!-distleft(bc,ev,dipmred p);
     q:=dip!-nc!-ev!-prod(bc,ev,lbc,lev);
     return dipsum(p,q)end;

symbolic procedure dip!-nc!-prod!-distright(u,v);
  if dipzero!? v then v else
   dipsum(dip!-nc!-m!*p!-distright(u,diplbc v,dipevlmon v),
          dip!-nc!-prod!-distright(u,dipmred v));

symbolic procedure dip!-nc!-m!*p!-distright(p,bc,ev);
   if dipzero!? p then nil else
   begin scalar lev,lbc,q;
     lev:=dipevlmon p;lbc:=diplbc p;
     p:=dip!-nc!-m!*p!-distright(dipmred p,bc,ev);
     q:=dip!-nc!-ev!-prod(lbc,lev,bc,ev);
     return dipsum(p,q)end;

symbolic procedure dip!-nc!-ev!-prod(bc1,ev1,bc2,ev2);
  % compute (bc1*ev1) * (bc2*ev2). Result is a dip.
   dip!-nc!-ev!-prod1(ev1,1,dipfmon(bcprod(bc1,bc2),ev2));

symbolic procedure dip!-nc!-ev!-prod1(ev,n,r);
  % loop over ev and n (counter). NOTE: ev must be processed from right to left!
   if null ev then r else
      dip!-nc!-ev!-prod2(car ev,n,dip!-nc!-ev!-prod1(cdr ev,n#+1,r));


symbolic procedure dip!-nc!-ev!-prod2(j,n,r);
  % muliply x_n^j * r
  if j=0 or dipzero!? r then r else
 begin scalar ev,bc,r0,w,s,evl,evr;integer i;
    ev:=dipevlmon r;bc:=diplbc r;
    r:=dip!-nc!-ev!-prod2(j,n,dipmred r);
     % collect the variables in ev which do not commute with x_n;
    w:=getv(ncdipbase!*,n);
    while w and nth(ev,car w)=0 do w:=cdr w;
     % no commutator?
    if null w then
      <<r0:=dipfmon(bc,dipevadd1var(j,n,ev));return dipsum(r0,r)>>;
     % We handle now the leftmost commutator and
     % push the rest of the problem down to the recursion:
     % split the monmial into parts left and
     % right of the noncom variable and multiply these.
    w:=car w;s:=nth(ev,w);
     % Split the ev into left and right part.
    i:=0;for each e in ev do <<i:=i#+1;if i<w then<<evl:=e.evl;evr:=0 .evr>>
          else if i=w then <<evl:=0 .evl;evr:=0 .evr>>
          else             <<evl:=0 .evl;evr:=e .evr>> >>;
    evl:=reversip evl;evr:=reversip evr;
    r0:=dip!-nc!-get!-commutator(n,j,w,s);
         % multiply by left exponent
    r0:=if ncdipcircular!* then
       <<r0:=dip!-nc!-prod(dipfmon(a2bc 1,evl),r0);
         r0:=dip!-nc!-prod(r0,dipfmon(bc,evr))>> else
       <<r0:=dipprod(dipfmon(a2bc 1,evl),r0);
         r0:=dipprod(r0,dipfmon(bc,evr))>>;
  done:return dipsum(r0,r)end;

symbolic procedure dip!-nc!-get!-commutator(n1,e1,n2,e2);
  % Compute the commutator for y^e1*x^e2 where y is
  % the n1-th variable and x is the n2-th variable.
  % The commutators for power products are computed
  % recursively when needed. They are stored in a data base.
  % I assume here that the commutator for (1,1) has been
  % put into the data base before. We update the table
  % in place by nconc-ing new pairs to its end.
  begin scalar w,r,p;
    w:=getv(getv(ncdiptable!*,n1),n2);p:=e1.e2;
    if (r:=assoc(p,w)) then return cdr r;
      % compute new commutator recursively:
      % first e1 downwards, then e2
    r:=if e1>1 then
      % compute y^e1*x^e2 as  y*(y^(e1-1)*x^e2)
       dip!-nc!-ev!-prod2(1,n1,dip!-nc!-get!-commutator(n1,e1#-1,n2,e2))
     else
      % compute y*x^e2 as (y*x^(e2-1))*x
       dip!-nc!-prod(dip!-nc!-get!-commutator(n1,1,n2,e2#-1),dipfvarindex n2);
    nconc(w,{(p.r)});
    return r end;

symbolic procedure dipfvarindex n;
  % Make a dip from a single variable index.
a2dip nth(dipvars!*,n);


symbolic procedure dipevadd1var(e,n,ev);
  % add e into the nth position of ev.
if null ev or n<1 then ev else
 if n=1 then (car ev #+ e).cdr ev else car ev.dipevadd1var(e,n#-1,cdr ev);

% ------------ conversion algebraic => nc dip --------------

symbolic procedure a2ncdip a;
  if atom a then a2dip a else
  if car a = 'difference then
   a2ncdip{'plus,cadr a,{'times,-1,caddr a}} else
  if car a = 'minus then
   a2ncdip{'times,-1,cadr a} else
  if car a='expt and fixp caddr a then
   a2ncdip('times.for i:=1:caddr a collect cadr a) else
  if car a='plus then
   begin scalar r;
     r:=a2ncdip cadr a;
     for each w in cddr a do r:=dipsum(r,a2ncdip w);
     return r end  else
  if car a='times then
   begin scalar r;
     r:=a2ncdip cadr a;
     for each w in cddr a do r:=dip!-nc!-prod(r,a2ncdip w);
     return r end  else
  if car a='!*sq then a2ncdip prepsq cadr a else a2dip a;

symbolic procedure a2ncvdp a;dip2vdp a2ncdip a;

endmodule;;end;
