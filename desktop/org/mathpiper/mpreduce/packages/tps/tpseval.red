module tpseval; % Evaluator for truncated power series.

% Authors: Julian Padget & Alan Barnes

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


% The evaluator interprets the results of the compilation phase and
% is also rule driven until I get round to getting the compilation
% phase to produce directly executable code

% The evaluation functions live on the erule property of the name.

fluid '(ps ps!:order!-limit ps!:max!-order);

% Printing functions now in module tpsconv

% symbolic procedure ps!:prin!: p;
% if constantpsp p then
%   maprint(prepsqxx ps!:get!-term(p,0), 0)
% else
%  (lambda (first,u,delta,symbolic!-exp!-pt,about,atinf);
%   <<  if !*nat and posn!*<20 then orig!*:=posn!*;
%       atinf:=(about='ps!:inf);
%       ps!:find!-order p;
%       delta:=prepf((ps!:depvar p) .** 1 .*1 .+
%               (negf  if atinf then nil
%                       % expansion about infinity
%                       else if idp about then !*k2f about
%                       else if ps!:numberp about then !*n2f about
%                       else if (u:=!*pre2dp about) then !*n2f u
%                       else !*k2f(symbolic!-exp!-pt:= compress
%                          append(explode ps!:depvar p, explode '0))));
%       if symbolic!-exp!-pt then prin2!* "[";
%       prin2!* "{";
%       for i:=(ps!:order p): ps!:exp!-lim do
%         << u:=ps!:get!-term(p,i);
%            if null u then u := ps!:evaluate!-next(p,i);
%            if not null numr u then
%               <<if minusf numr u then <<u:=negsq u; prin2!* " - ">>
%                   else if not first then prin2!* " + ";
%                 first := nil;
%                 if posn!*>55 then <<terpri!* nil;prin2!* "  ">>;
%                 if denr u neq 1 then prin2!* "(";
%                 if u neq '(1 . 1) then
%                         maprint(prepsqxx u,get('times,'infix))
%                   else if i=0 then prin2!* 1;
%                 if denr u neq 1 then prin2!* ")";
%                 if i neq 0 and u neq '(1 . 1) then prin2!* "*";
%                 if i neq 0 then
%                 xprinf(!*p2f mksp(delta,
%                        if atinf then -i else i),nil,nil)
%               >>
%        >>;
%       if first then prin2!* "0";
%       if posn!*>55 then terpri!* nil;
%       u:=ps!:exp!-lim +1;
%       if (u=1) and not atinf and (about neq 0) then
%             prin2!* " + O"
%       else prin2!* " + O(";
%       xprinf(!*p2f mksp(delta,if atinf then -u else u),nil,nil);
%       if (u=1) and not atinf and (about neq 0) then
%               prin2!* "}"
%          else prin2!* ")}";
%       if symbolic!-exp!-pt then
%         << if posn!*>45 then terpri!* nil;
%            prin2!* "  where ";
%            prin2!* symbolic!-exp!-pt;
%            prin2!* " = ";
%            maprin about;
%            prin2!* "]"
%         >>;
%       terpri!* nil;
%  >>)
%  (t,nil,nil,nil,ps!:expansion!-point p,nil);
%


symbolic procedure ps!:unknown!-order ps;
  (lambda (u, v);
          if v >= u then u
          else rerror(tps,20,
                      list("Can't find the order of ",ps!:value ps)))
   (ps!:order ps, ps!:last!-term ps);

symbolic procedure ps!:find!-order ps;
  if null ps then 0
  else if idp ps then ps  % second arg of DF etc are identifiers
  else if ps!:numberp ps then 0
  else if eqcar(ps,'!:ps!:) then <<
     if idp cdr ps then ps!:unknown!-order ps
     else if atom ps!:expression ps then ps!:order ps
     else ps!:find!-order1(ps)>>
  else rerror(tps,21,"Unexpected form in ps!:find!-order");

symbolic procedure ps!:find!-order1(ps);
begin scalar psoperator,psord,pslast;
      psord:=ps!:order ps;
      pslast:=ps!:last!-term ps;
      if psord leq pslast then
         return psord;

      psoperator:=ps!:operator ps;
      psord:=apply(get(psoperator,'ps!:order!-fn), list ps);
      ps!:set!-order(ps,psord);
      ps!:set!-last!-term(ps,psord-1);

      if ps!:value ps =0 then
         % prevents infinite loop if we have exact cancellation
         <<psord:=0; ps!:set!-last!-term(ps, ps!:max!-order)>>
      else while ps!:evaluate!-next(ps,psord)=(nil ./ 1 ) do
      % in case we have finite # of cancellations in a sum or difference
          <<psord:=psord+1;
            if psord > ps!:order!-limit then
                rerror(tps,22,list("Expression ", ps!:value ps,
                                   " has zero expansion to order ",
                                   psord))
            % We may not always be able to recognise zero,
            % so give up after specified number of iterations.
          >>;
      return psord
end;

symbolic (ps!:order!-limit:=100);
% symbolic here seems to be essential in Cambridge Lisp systems

put('psordlim, 'simpfn, 'simppsordlim);

symbolic procedure simppsordlim u;
begin integer n;
   n:=ps!:order!-limit;
   if u then ps!:order!-limit := ieval carx(u,'psordlim);
   return (if n=0 then nil ./ 1 else n ./ 1);
end;

put('plus,'ps!:order!-fn, 'ps!:plus!-orderfn);
put('int,'ps!:order!-fn,'ps!:int!-orderfn);
put('df,'ps!:order!-fn,'ps!:df!-orderfn);
put('quotient,'ps!:order!-fn, 'ps!:quotient!-orderfn);
put('times,'ps!:order!-fn, 'ps!:times!-orderfn);
put('minus,'ps!:order!-fn, 'ps!:minus!-orderfn);
put('difference,'ps!:order!-fn, 'ps!:difference!-orderfn);

symbolic procedure ps!:int!-orderfn ps;
begin scalar u,v;
     v := ps!:depvar ps;
     u := ps!:find!-order(rand1 ps!:expression ps);
     return
        if v=rand2 ps!:expression ps then
          if ps!:expansion!-point ps neq 'ps!:inf then
            if u=-1 then rerror(tps,23,"Logarithmic Singularity")
            else u+1
          else   % expansion about infinity
            if u=1 then rerror(tps,24,"Logarithmic Singularity")
            else u-1
          else u;
end;

symbolic procedure ps!:df!-orderfn ps;
begin scalar u, v, pt, dfvar;
     v:= ps!:expression ps;
     u := ps!:find!-order(rand1 v);
     dfvar := rand2 v;
     pt := ps!:expansion!-point ps;
     return
        if ps!:depvar ps = dfvar then
           if pt neq 'ps!:inf then
              if u=0 then 0 else u-1
           else if u=0 then 2 else u+1    % expansion about infinity
        else if depends(pt, dfvar) then if u=0 then 0 else u-1
             else u;
end;

symbolic procedure ps!:quotient!-orderfn ps;
begin scalar u,v;
     v := ps!:expression ps;
     u := ps!:find!-order(rand1 v);
     v := ps!:find!-order(rand2 v);
     return difference(u,v);
end;

symbolic procedure ps!:times!-orderfn ps;
begin scalar u,v;
     v := ps!:expression ps;
     u := ps!:find!-order(rand1 v);
     v := ps!:find!-order(rand2 v);
     return plus2(u,v);
end;

%symbolic procedure ps!:plus!-orderfn ps;
%  eval cons('min ,  mapcar(rands ps!:expression ps, 'ps!:find!-order));

symbolic procedure ps!:plus!-orderfn ps;
  % Re-worked by ACN to avoid excessive numbers of args.
   begin scalar w, m;
      if null ps then return 0;
      w := mapcar(rands ps!:expression ps, 'ps!:find!-order);
      m := car w;
      for each z in cdr w do if z < m then m := z;
      return m
   end;

symbolic procedure ps!:minus!-orderfn ps;
  ps!:find!-order(rand1 ps!:expression ps);

symbolic procedure ps!:difference!-orderfn ps;
begin scalar u,v;
     v := ps!:expression ps;
     u := ps!:find!-order(rand1 v);
     v := ps!:find!-order(rand2 v);
     return min2(u,v);
end;

put('sqrt,'ps!:order!-fn,'ps!:sqrt!-orderfn);
put('sqrt,'ps!:erule,'ps!:sqrt!-erule);

symbolic procedure ps!:sqrt!-orderfn ps;
begin scalar u;
  u:=ps!:find!-order rand1 ps!:expression ps;
  return
     (if v*2=u then v else rerror(tps,25,"Branch Point in Sqrt"))
      where v=u/2
end;

symbolic procedure ps!:sqrt!-erule(a,n);
begin scalar aa,x,y,z;
  aa:=rand1 a;  z:= nil ./ 1;
  y:=ps!:order aa;
  x:=ps!:order(ps);   %order of sqrt ps
  if n=x then return simpexpt(list(prepsqxx ps!:evaluate(aa,y),
                                   '(quotient 1 2)));
  for k:=1:n-x do
              z:=addsq(z,
                    multsq(((lambda y; if y=0 then nil else y)
                             (k*3-2*n+y)) ./ 1,
                          multsq(ps!:evaluate(aa,k+y),
                               ps!:evaluate(ps,n-k))));
            return quotsq(z,multsq(2*(n-x) ./ 1,ps!:evaluate(aa,y)))
end;

%  alternative algorithm (for order 0 only)
%  for i:=1:n-1 do
%    z:=addsq(z,multsq(multsq( i ./ 1,ps!:evaluate(ps,i)),
%                      ps!:evaluate(ps,n-i)));
%    z:=multsq(z, 1 ./ (n+1));
%  return quotsq(addsq(ps!:evaluate(aa,n),negsq z),
%                multsq(2 ./ 1,ps!:evaluate(b,x)))


put('cbrt,'ps!:order!-fn,'ps!:cbrt!-orderfn);
put('cbrt,'ps!:erule,'ps!:cbrt!-erule);

symbolic procedure ps!:cbrt!-orderfn ps;
begin scalar u;
   u:=ps!:find!-order rand1 ps!:expression ps;
   return
      (if v*3=u then v else rerror(tps,26,"Branch Point in Cbrt"))
       where v=u/3
end;

symbolic procedure ps!:cbrt!-erule(a,n);
begin scalar aa,x,y,z;
  aa:=rand1 a;  z:= nil ./ 1;
  y:=ps!:order aa;
  x:=ps!:order(ps);   %order of cbrt ps
  if n=x then return simpexpt(list(prepsqxx ps!:evaluate(aa,y),
                                   '(quotient 1 3)));
  for k:=1:n-x do
              z:=addsq(z,
                    multsq(((lambda y; if y=0 then nil else y)
                             (k*4-3*n+y)) ./ 1,
                          multsq(ps!:evaluate(aa,k+y),
                               ps!:evaluate(ps,n-k))));
            return quotsq(z,multsq(3*(n-x) ./ 1,ps!:evaluate(aa,y)))
end;

symbolic procedure ps!:evaluate(ps,i);
begin scalar term;
   term:=ps!:get!-term (ps,i);
   if term then return term;
   for j:=ps!:last!-term(ps)+1:i do
      term:= ps!:evaluate!-next(ps,j);
   return term;
end;

symbolic procedure ps!:evaluate!-next(ps,n);
% The appropriate evaluation rule for the operator
% in the ps is selected and invoked
begin scalar next;
    next := apply(get(ps!:operator ps,'ps!:erule),
                  list(ps!:expression ps,n));
    ps!:set!-term(ps,n,next:=simp!* prepsqxx next);
    return next;
end;

symbolic procedure ps!:plus!-erule(a,n);
begin scalar z;
      z := nil ./ 1;
      foreach term in rands a do
           z:=addsq(z, ps!:evaluate(term, n));
      return z
end;

put('plus,'ps!:erule,'ps!:plus!-erule);

symbolic procedure ps!:minus!-erule(a,n);
   negsq ps!:evaluate(rand1 a,n);

put('minus,'ps!:erule,'ps!:minus!-erule);

symbolic procedure ps!:difference!-erule(a,n);
   addsq(ps!:evaluate(rand1 a,n),
         negsq ps!:evaluate(rand2 a,n));

put('difference,'ps!:erule,'ps!:difference!-erule);

symbolic procedure ps!:times!-erule(a,n);
begin scalar aa,b,x,y,y1,z;
   aa:=rand1 a; b:= rand2 a; z:= nil ./ 1;
   x:=ps!:order(aa);
   y:=ps!:order(ps);    % order of product ps
   y1 := ps!:order b;
   for i := 0:n-y do if n-x-i>=y1 then
     z:= addsq(z,multsq(ps!:evaluate(aa,i+x),
                        ps!:evaluate(b,n-x-i)));
   return z
end;

put('times,'ps!:erule,'ps!:times!-erule);

symbolic procedure ps!:quotient!-erule(a,n);
begin scalar aa,b,x,y,z;
  aa:=rand1 a; b:=rand2 a; z:= nil ./ 1;
  y:=ps!:order(b);
  x:=ps!:order(ps);   %order of quotient ps
  for i:=1:n-x do
    z:=addsq(z,multsq(ps!:evaluate(b,i+y),
                      ps!:evaluate(ps,n-i)));
  return quotsq(addsq(ps!:evaluate(aa,n+y),negsq z),
                      ps!:evaluate(b,y))
end;

put('quotient,'ps!:erule,'ps!:quotient!-erule);

% the next two functions deal more efficiently with common special
% cases of multiplication or division by a constant
% the constmult operator is produced by
% ps!:times!-crule and ps!:quotient!-crule
%

put('psmult,'ps!:order!-fn, 'ps!:constmult!-orderfn);
put('psmult,'ps!:erule,'ps!:constmult!-erule);

symbolic procedure ps!:constmult!-orderfn ps;
  ps!:find!-order rand2 ps!:expression ps;

symbolic procedure ps!:constmult!-erule(a,n);
    multsq(rand1 a, ps!:evaluate(rand2 a,n));

symbolic procedure ps!:df!-erule(a,n);
begin scalar dfvar, series, about;
  dfvar := rand2 a;
  series := rand1 a;
  about := ps!:expansion!-point series;
  return
    if dfvar = ps!:depvar series then
       if about neq 'ps!:inf then
          multsq((n+1) ./ 1,ps!:evaluate(series, n+1))
      else multsq((1-n) ./ 1,ps!:evaluate(series, n-1))
    else if depends(about, dfvar) then
         addsq(diffsq(ps!:evaluate(series,n),dfvar),
               multsq((-n-1) ./ 1, multsq(ps!:evaluate(series,n+1),
                                          diffsq(simp!* about,dfvar))))
    else diffsq(ps!:evaluate(series,n),dfvar);
end;

put('df,'ps!:erule,'ps!:df!-erule);

symbolic procedure ps!:int!-erule(a,n);
  if rand2 a=ps!:depvar rand1 a then
     if ps!:expansion!-point rand1 a neq 'ps!:inf then
         quotsq(ps!:evaluate(rand1 a,n-1), n ./ 1)
     else quotsq(ps!:evaluate(rand1 a,n+1),-n ./ 1)
  else simpint list(prepsqxx ps!:evaluate(rand1 a,n),rand2 a);

put('int,'ps!:erule,'ps!:int!-erule);

symbolic procedure ps!:expt!-orderfn ps;
begin scalar u, v, w, expres;
     expres := ps!:expression ps;
     u:= ps!:find!-order rand1 expres;
     v:= rand2 expres;
     w := cadddr expres;
     if cdr(v:=divide(u * v,w))=0 then return car v
     else rerror(tps,27,"Branch Point in EXPT")
end;

symbolic procedure ps!:expt!-erule(a,n);
begin scalar base,x,y,z,p,q;
   base:= rand1 a;
   p:=rand2 a; q:=cadddr a;
   y:=ps!:order(base);
   z:= ps!:order ps;         % order of exponential
   if n=z then <<
         if q =1 then x := p else x := list('quotient, p, q);
         return simpexpt(list(prepsqxx ps!:evaluate(base,y),x))>>
   else << x:= nil ./ 1;
           for k:=1:n-z do
              x:=addsq(x,
                       multsq(((lambda num; if num=0 then nil else num)
                               (k*p+q*(k-n+z))) ./ q,
                              multsq(ps!:evaluate(base,k+y),
                                     ps!:evaluate(ps,n-k))));
            return quotsq(x,multsq((n-z) ./ 1, ps!:evaluate(base,y)))
        >>;
  end;

put('expt,'ps!:erule, 'ps!:expt!-erule);
put('expt,'ps!:order!-fn,'ps!:expt!-orderfn);

symbolic procedure ps!:exp!-orderfn ps;
  if ps!:find!-order rand1 ps!:expression ps<0 then
      rerror(tps, 28, "Essential Singularity in EXP")
  else 0;

symbolic procedure ps!:exp!-erule(a,n);
begin scalar exp1, x;
   exp1:= rand1 a;
   if n=0 then
      return simpexpt(list('e, prepsqxx ps!:evaluate(exp1,0)));
   x:= nil ./ 1;
   for k:=0:n-1 do
       x:=addsq(x, multsq((n-k) ./ 1,
                          multsq(ps!:evaluate(exp1,n-k),
                                 ps!:evaluate(ps,k))));
   return quotsq(x, n ./ 1);
end;

put('exp,'ps!:erule, 'ps!:exp!-erule);
put('exp,'ps!:order!-fn,'ps!:exp!-orderfn);

endmodule;

end;
