module ranks;  % Rank operations.

% Author:  Anthony C. Hearn.

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


fluid '(ZERO);

ZERO := mkobject(0,'zero);

symbolic procedure addrank(name,arity,coarity);
   begin scalar m,disambop,x;
      m := length arity;   % Number of arguments.
      disambop := mkrankedname(name,arity,if atom coarity then nil
                                           else car coarity);
      x := get(name,'ranks);
      if null x
        then put(name,'ranks,
                      {m . {mkranklist(arity,disambop,coarity)}})
       else addrank1(disambop,arity,x,m,coarity)
   end;

symbolic procedure addrank0(name,arity,coarity);
   begin scalar m,disambop,x;
      disambop := caadr cadadr coarity;
      m := length arity;   % Number of arguments.
      x := get(name,'ranks);
      if null x
        then put(name,'ranks,
                      {m . {mkranklist(arity,disambop,coarity)}})
       else addrank1(disambop,arity,x,m,coarity)
   end;


symbolic procedure addrank1(disambop,arity,tree,noargs,coarity);
   if noargs = caar tree
     then mergerank(disambop,arity,cdar tree,coarity)
    else if noargs > caar tree
     then rplaca(rplacd(tree,car tree . cdr tree),
                        {noargs,mkranklist(arity,disambop,coarity)})
    else if null cdr tree
     then rplacd(tree,{{noargs,
                        mkranklist(arity,disambop,coarity)}})
    else addrank1(disambop,arity,cdr tree,noargs,coarity);

symbolic procedure mergerank(disambop,arity,tree,coarity);
   if car arity = caar tree
     then if null cdr arity
            then upd_coarity(cdar tree,coarity)

%if cadar tree eq disambop
%                    then <<lprim {"rank entry for",disambop,"redefined"};
%                           rplacd(car tree,{disambop,coarity})>>
%          else <<lprim {"Type name",disambop,"changed"};
%                 rplaca(cdar tree,disambop)>>
           else mergerank(disambop,cdr arity,cdar tree,coarity)
    else if type_greaterp(caar tree,car arity)
     then rplaca(rplacd(tree,car tree . cdr tree),
                 mkranklist(arity,disambop,coarity))
    else if null cdr tree
     then rplacd(tree,{mkranklist(arity,disambop,coarity)})
    else mergerank(disambop,arity,cdr tree,coarity);

symbolic procedure mkrankedname(name,arity,coarity);
   begin scalar x,y;
      if name then x := explode name
       else <<x := explode car arity; arity := cdr arity>>;
      y := explode2 "_";
      for each j in arity do x := nconc(x,append(y,explode j));
      if coarity then x := nconc(x,append(explode2 "!>",explode coarity));
      return intern compress x
   end;

symbolic procedure mkranklist(arity,name,coarity);
   if null cdr arity then {car arity,'lambda,car coarity,'cond . cdr coarity}
%name,coarity}
    else {car arity,mkranklist(cdr arity,name,coarity)};

symbolic procedure upd_coarity(u,v);
   begin         %u: (lambda () (cond ((bool (disambop coarity)) ((bool
     u := cdaddr u;
     if cadr v = car u and null cdr u then return nil else
     if caadr v eq t then aconc(u,cadr v)    %%% THIS MAY BE A PROBLEM
      else rplaca(rplacd(u,car u . cdr u),cadr v) %%% IN FASL VERSION
   end;


symbolic procedure type_greaterp(u,v); u eq 'generic;

symbolic procedure addnullary(arity,props);
%   if null xtype1(car coarity,arity)
%      then rederr {"Types in constraint",arity,"->",car coarity,
%                   "are unrelated"} else
    begin scalar x,y;
           x := get(arity,'!*nullary!*);
           if null x then put(arity,'!*nullary!*,
                              {'lambda,car props,'cond . cdr props})
%            else if y := atsoc(car coarity,x)
%                    then <<lprim {"Entry for",arity,"->",
%                                  car coarity,"redefined"};
%                           rplacd(y,cdr coarity)>>
            else put(arity,'!*nullary!*,
                     {'lambda,cadr x,'cond . cadr props . cdr caddr x})
         end;

endmodule;

end;
