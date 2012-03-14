module dfpart;  % support of generic differentiation.

% Author: H. Melenk
% May 1993
% Copyright (c) Konrad-Zuse-Zentrum Berlin, all rights reserved.

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


create!-package('(dfpart),'(contrib misc));

fluid '(ycoord!* ymin!*);

put('dfp,'simpfn,'simpdfp);

symbolic procedure simpdfp u;
   begin scalar f,fn,dd,p,w,l;
     if length u<2 then goto error;
     f := reval car u;
     if not pairp f then return
        if member(cadr u,frlis!*) then mksq('dfp.u,1)
        else simpdf(f . cdr cadr u);
     fn:=car f;
     p:=cdr f;
     dd :=reval cadr u;
     if not member(dd,frlis!*) and not eqcar(dd,'list) then
     << dd:= dd.for each y in cddr u collect reval y;
        dd:= 'list.dfp!-normalize(dd,nil);
        % apply pattern matching again
        return simp {'dfp,f,dd};
     >>;
     l:=get(fn,'generic_function);
     w:= t;
     if l and eqcar(dd,'list) then
        for each y in cdr dd do
           w:=w and member(y,l);
     if not w then return nil ./ 1;
     if dd='(list) then return mksq(f,1);
     if l and flagp(fn,'dfp_commute) then
      dd := 'list . sort(cdr dd,'ordp) where kord!*=l;
     u:={'dfp,f,dd};
     return mksq(u,1);
  error:
   typerr('dfp . u,"generic differential");
   end;

symbolic procedure dfp!-normalize(l,x);
   if null l then nil else
   if idp car l then car l . dfp!-normalize(cdr l,car l)
   else if numberp car l then
     append(for i:=2:car l collect x,dfp!-normalize(cdr l,nil))
   else typerr(car l,"dfp variable");

symbolic procedure generic_function u;
 for each fc in u do
 begin scalar rs,pars,fcn;
      integer l;
    if atom fc or not idp (fcn:=car fc)
       then typerr(fc, "generic function");
    l := length cdr fc;
    algebraic clear fcn;
    apply('depend,list fc);
    apply('operator,list list fcn) where !*mode='algebraic;
    pars := for i:=1:l collect {'!~,gensym()};
    rs := {'list ,
     {'replaceby,
       {'df, {fcn},{'!~,'!:x}},
       {'df, fc ,'!:x}},
     {'replaceby,
       {'df, fcn . pars,{'!~,'!:x}},
       'plus .
         for i:=1:l collect
          {'times,{'dfp, fcn.pars,{'list,nth(cdr fc,i)}},
                 {'df,nth(pars,i),'!:x}
     }}};
   put(fcn,'generic_function,cdr fc);
   put(fcn,'subfunc,'generic!-sub);
   algebraic let rs;
  end;


put('generic_function,'stat,'rlis);

symbolic procedure dfp_commute u;
  for each f in u do
  <<f:=reval f;
    if not idp f then f:=car f;
    if not get(f,'generic_function) then typerr(f,"generic function")
     else flag({f},'dfp_commute);
  >>;

put('dfp_commute,'stat,'rlis);

symbolic procedure generic_arguments f;
  % List of generic arguments of f.
  'list. get(car f,'generic_function);

symbolic procedure actual_arguments f;
  % List of actual arguments of f. If none are given,
  % return the generic arguments.
  'list . (cdr f or get(car f,'generic_function));

symbolic operator generic_arguments;
symbolic operator actual_arguments;

% differentiation rules

symbolic procedure dfp!-rule!-found(newform,oldf);
    not eqcar(newform,'dfp) or cadr newform neq oldf;

symbolic operator dfp!-rule!-found;

symbolic procedure soft!-append(a,b);
   <<a:=if eqcar(a,'list) then cdr a else {a};
     b:=if eqcar(b,'list) then cdr b else {b};
     'list.append(a,b)
    >>;

symbolic operator soft!-append;

algebraic;

dfp_rules:={

    df(dfp(~f,~q),~x) =>
    for i:=1:length generic_arguments(f) sum
         dfp(f,append(q,{part(generic_arguments f,i)}))
             *df(part(actual_arguments f,i),x),

    dfp(~f+~g,~q) => dfp(f,q) + dfp(f,q),

    dfp(-~f,~q) => -dfp(f,q),

       % recursive unrolling

    dfp(~f,~q) => dfp(dfp(f,{first q}),rest q) when
        arglength q neq -1 and part(q,0)=list and
        length q>1 and dfp!-rule!-found(dfp(f,{first q}),f),

       % Now we can concentrate on single derivatives,
       % the rest wil be done by unrolling.

    dfp(~f*~g,{~q}) =>
      dfp(f,{q})*g + dfp(g,{q})*f,

    dfp(~f/~g,{~q}) =>
      (dfp(f,{q})*g - dfp(g,{q})*f)/g**2,

    dfp(~f**~n,{~q}) =>
      n*f**(n-1)*dfp(f,{q}) when numberp n,

    dfp(dfp(~f,~q),~r) => dfp(f,soft!-append(q,r))
}$

let dfp_rules;

symbolic;

symbolic procedure generic!-sub(u,v);
    dfp!-sub(u,{'dfp,v,{'list}});

symbolic procedure dfp!-sub(u,v);
  % Subsitutions take place first in the arguments.
  % If the generic funtion is to be replaced:
  %  1. differentiate the target expression formally,
  %  2. transfer the arguments into the result expression.
  begin scalar p,f,fn,nf,l,w;
    f:=cadr v;
    p:=cdr f;
    l:=get(fn:=car f,'generic_function);
      % If f has no arguments, insert generic arguments if
      % one of these would be toched by the substitution.
    if null p then
    <<w := nil;
      for each y in l do w:=w or assoc(y,u);
      if w then p:=l;
    >>;
    p:= cdr listsub(u,'list.p);
    if null(nf:=assoc(fn,u)) and null(nf:=assoc(fn.l,u))
       then return {'dfp,fn.p,caddr v};
    nf := reval cdr nf;
    nf:=dfp!-sub1(nf,if p then pair(l,p),u);
    return {'dfp,nf,caddr v};
  end;

symbolic procedure dfp!-sub1(u,l,s);
 % U: expression to replace a generic function.
 % l: alist for inherited generic arguments.
 % s: alist for substituted expressions.
  if idp u then
    if get(u,'generic_function) then dfp!-sub1({u},l,s)
      else u
   else if atom u then u else
  begin scalar op,p,pp;
   op:=car u;
   if (p:=get(op,'generic_function)) then
     <<p:=cdr u or p;
       pp:=subla(l,p); pp:=subla(s,pp);
       return if p=pp then u else reval(op.pp);
     >>;
   return op. for each q in cdr u collect dfp!-sub1(q,l,s);
  end;

put('dfp,'subfunc,'dfp!-sub);

% ------------------ printing ----------------------------

symbolic procedure dfppri l;
  begin scalar dd,f;
    if not !*nat or !*fort then return 'failed;
    f:=cadr l; dd:=caddr l;
    if atom f or not get(car f,'generic_function)
        then return 'failed;
    prin2!* car f;
    ycoord!* := ycoord!*-1;
    if ycoord!* < ymin!* then ymin!*:=ycoord!*;
    for each y in cdr dd do prin2!* y;
    ycoord!* := ycoord!*+1;
    if cdr f then
    << prin2!* "(";
       inprint('!*comma!*,0,cdr f);
       prin2!* ")";
    >>;
    return l;
  end;

put('dfp,'prifn,'dfppri);

symbolic procedure fancy!-dfppri l;
       fancy!-dfpriindexed(l,nil);

put('dfp,'fancy!-prifn,'fancy!-dfppri);

endmodule;

end;
