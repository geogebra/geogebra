module map;  % Mapping univariate functions to composite objects.

% Author: Herbert Melenk.

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


% Syntax: map(unary-function,linear-structure-or-matrix)
%
%           map(sqrt ,{1,2,3,4});
%           map(df(~u,x),mat((x^2,sin x)));
%
%         select(unary-predicate,linear-structure)
%
%           select(evenp,{1,2,3,4,5,6,7});
%           select(evenp deg(~u,x),(x+y)^5);
%
%  The function/predicate may contain one free variable.

put('!~map,'oldnam,'map);

put('map,'newnam,'!~map);

put('!~map,'psopfn,'map!-eval);

put('!~map,'rtypefn,'getrtypecadr);

symbolic procedure getrtypecadr u; getrtype cadr u;

symbolic procedure map!-eval u;
  <<if length u neq 2 then rederr "illegal number of arguments for map";
    map!-eval1(reval cadr u,car u,
         function(lambda y;y),'aeval)>>;

symbolic procedure !~map(b,a);
 % Called only inside matrix expressions.
   cdr map!-eval1('mat . matsm a,b,
     function (lambda w; list('!*sq,w,t)),'simp);

symbolic procedure map!-eval1(o,q,fcn1,fcn2);
 % o       structure to be mapped.
 % q       map expression (univariate function).
 % fcn1    function for evaluating members of o.
 % fcn2    function computing results (e.g. aeval).
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
    if atom o then rederr "cannot map for atom";
    return if car o ='mat then
     'mat . for each row in cdr o collect
             for each w in row collect
              map!-eval2(w,v,q,fcn1,fcn2)
     else car o . for each w in cdr o collect
       map!-eval2(w,v,q,fcn1,fcn2);
  end;

symbolic procedure map!-eval2(w,v,q,fcn1,fcn2);
  begin scalar r;
   r :=evalletsub2({{{'replaceby ,v,apply1(fcn1,w)}},
        {fcn2,mkquote q}},nil);
   if errorp r then rederr "error during map";
   return car r;
  end;

symbolic procedure map!-frvarsof(q,l);
  if atom q then l
  else if car q eq '!~ then
     if q member l then l else q.l
  else map!-frvarsof(cdr q,map!-frvarsof(car q,l));

symbolic procedure select!-eval u;
 % select from a list l members according to a boolean test.
 begin scalar l,w,v,r;
  l := reval cadr u; w := car u;
  if atom l or (car l neq'list and not flagp(car l,'nary)) then
           typerr(l,"select operand");
  if idp w and get(w,'number!-of!-args)=1 then w:={w,{'~,'!&!&}};
  if eqcar(w,'replaceby) then <<v:=cadr w;w:=caddr w>>;
  w:=freequote formbool(w,nil,'algebraic);
  if v then w:={'replaceby,v,w};
  r:=for each q in
        pair(cdr map!-eval1(l,w,function(lambda y;y),'lispeval),cdr l)
      join if car q and car q neq 0 then {cdr q};
  if r then return car l . r;
  if (r:=atsoc(car l,'((plus . 0)(times . 1)(and . 1)(or . 0))))
    then return cdr r
   else rederr {"empty selection for operator ",car l}
 end;

symbolic procedure freequote u;
  % Preserve structure where possible.
  if atom u then u
  else if car u eq 'list and cdr u and cadr u = '(quote !~)
   then mkquote{'!~,cadr caddr u}
  else (if v=u then u else v)
        where v = freequote car u . freequote cdr u;

put('select,'psopfn,'select!-eval);

put('select,'number!-of!-args,2);

endmodule;

end;
