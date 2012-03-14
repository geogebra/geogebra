module tpsdom; % Domain definitions for truncated power series.

% Authors: Julian Padget & Alan Barnes.

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


fluid '(ps!:exp!-lim ps!:max!-order);
global '(domainlist!*);

symbolic (domainlist!*:=union('(!:ps!:),domainlist!*));
% symbolic here seems to be essential in Cambridge Lisp systems

put('tps,'tag,'!:ps!:);
put('!:ps!:,'dname,'tps);
flag('(!:ps!:),'field);
put('!:ps!:,'i2d,'i2ps);
put('!:ps!:,'minusp,'ps!:minusp!:);
put('!:ps!:,'plus,'ps!:plus!:);
put('!:ps!:,'times,'ps!:times!:);
put('!:ps!:,'difference,'ps!:difference!:);
put('!:ps!:,'quotient,'ps!:quotient!:);
put('!:ps!:,'zerop,'ps!:zerop!:);
put('!:ps!:,'onep,'ps!:onep!:);
put('!:ps!:,'prepfn,'ps!:prepfn!:);
%put('!:ps!:,'specprn,'ps!:prin!:);
put('!:ps!:,'prifn,'ps!:print0);
put('!:ps!:,'pprifn,'ps!:print);
put('!:ps!:,'intequivfn,'psintequiv!:);
put('!:ps!:,'expt,'ps!:expt!:);

% conversion functions

put('!:ps!:,'!:mod!:,mkdmoderr('!:ps!:,'!:mod!:));
% put('!:ps!:,'!:gi!:,mkdmoderr('!:ps!:,'!:gi!:));
% put('!:ps!:,'!:bf!:,mkdmoderr('!:ps!:,'!:bf!:));
% put('!:ps!:,'!:rn!:,mkdmoderr('!:ps!:,'!:rn!:));
put('!:rn!:,'!:ps!:,'!*d2ps);
put('!:ft!:,'!:ps!:,'!*d2ps);
put('!:bf!:,'!:ps!:,'!*d2ps);
put('!:gi!:,'!:ps!:,'!*d2ps);
put('!:gf!:,'!:ps!:,'!*d2ps);
put('!:rd!:,'!:ps!:,'!*d2ps);
put('!:cr!:,'!:ps!:,'!*d2ps);
put('!:crn!:,'!:ps!:,'!*d2ps);

symbolic procedure psintequiv!: u;
  if idp cdr u or ps!:depvar u or denr(u:=ps!:get!-term(u,0)) neq 1
  then nil
  else if domainp (u:=numr u) then
        if atom u then if null u then 0 else u
        else (if x  and (x:= apply1(x,u)) then x else nil)
               where x = get(car u,'intequivfn)
  else nil;

symbolic procedure i2ps u;
   u;

symbolic procedure !*d2ps u;
  make!-constantps ((u ./ 1), prepsqxx(u ./ 1), nil);

%  begin scalar ps;
%      ps:=get('tps,'tag) . mkvect 7;
%      ps!:set!-order(ps,0);
%      ps!:set!-expression(ps,list ('psconstant, u ./ 1));
%      ps!:set!-value(ps,u:=prepsqxx( u ./ 1));
%      ps!:set!-last!-term(ps,ps!:max!-order);
%      ps!:set!-terms(ps,list ( 0 . simp!* u)));
%     return ps
%   end;

symbolic procedure ps!:minusp!: u;
   nil;   % what else makes sense?

symbolic procedure ps!:plus!:(u,v);
   ps!:operator!:('plus,u,v);

symbolic procedure ps!:difference!:(u,v);
   ps!:operator!:('difference,u,v);

symbolic procedure ps!:times!:(u,v);
   ps!:operator!:('times,u,v);

symbolic procedure ps!:quotient!:(u,v);
   ps!:operator!:('quotient,u,v);


symbolic procedure ps!:diff!:(u,v);
  (( if idp deriv then
        make!-ps!-id(deriv,ps!:depvar u,ps!:expansion!-point u)
      else if numberp deriv then
              if zerop deriv then nil
              else deriv
      else <<
         u:=make!-ps(list('df,u,v), deriv,
                     ps!:depvar u,ps!:expansion!-point u);
         ps!:find!-order u;
         u
      >>)
  ./ 1)
   where (deriv = prepsqxx simp!* list('df, ps!:value u,v));

put('!:ps!:,'domain!-diff!-fn,'ps!:diff!:);

symbolic procedure ps!:depends!-fn(u,v);
   depends(ps!:value u, v);

put('!:ps!:, 'domain!-depends!-fn, 'ps!:depends!-fn);

symbolic procedure ps!:operator!:(op,u,v);
% u and v are domain elements at least one of which is a power series
   begin scalar value,x,x0,y,y0;
      if not ps!:p v  then
        << x:=ps!:depvar u; x0:= ps!:expansion!-point u >>
      else if not ps!:p u then
        << x:=ps!:depvar v; x0:= ps!:expansion!-point v>>
      else % both are power series
        <<x:= ps!:depvar u;
          y:= ps!:depvar v;
          x0:= ps!:expansion!-point u;
          y0:= ps!:expansion!-point v;
          if x0 and y0 then
             if x0 eq y0 and x eq y then nil
             else if x0 neq y0 then
               rerror(tps,29,
                      list("power series expansion points differ in ",
                           op))
             else
               rerror(tps,30,
                  list("power series dependent variables differ in ",
                       op));
          if null x0 then x0 := y0
          else if null y0 then y0:=x0;
        >>;
        if null x0 then  % both are constant power series
           << if x and y then
                if x eq y then nil else
                  rerror(tps,31,
                  list("power series dependent variables differ in ",
                        op))
              else if y then x:=y;
              if ps!:p u then u:= ps!:value u;
              if ps!:p v then v:= ps!:value v;
              value := simp!* list(op, u, v);
              if denr value=1 and domainp numr value then
                 return numr value
              else
                 return make!-constantps(value, prepsqxx value, x) >>;
        if x and y then
          if x eq y then nil
          else rerror(tps,32,
                  list("power series dependent variables differ in ",
                        op))
        else if y then x:=y;
        value:= simp!* list(op,ps!:value u,ps!:value v);
        if denr value=1 and domainp numr value then return numr value;
        u:= make!-ps(list(op,u,v), prepsqxx value,x,x0);
        ps!:find!-order u;
        return u;
  end;

symbolic procedure ps!:zerop!: u;
  (numberp v and zerop v) where v=ps!:value u;

symbolic procedure ps!:onep!: u;
  onep ps!:value u;

symbolic procedure ps!:prepfn!: u;
   u;

initdmode 'tps;

endmodule;

end;
