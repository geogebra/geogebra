module indsymm;

% Author: Eberhard Schruefer

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


fluid '(indl);   % Needed by Common Lisp.

comment index_symmetries u(k,l,m,n):    symmetric     in {k,l},{m,n}
                                        antisymmetric in {{k,l},{m,n}},
                         g(k,l),h(k,l): symmetric;

symbolic procedure index!-symmetriestat;
   begin scalar res,x,y; scan();
     a: res :=
        (begin scalar indexedvars,syms,asyms;
           d: indexedvars := (xread1 'for) . indexedvars;
              if null(cursym!* eq '!*colon!*) then <<scan(); go to d>>;
           x := scan();
           if x eq 'symmetric then go to sym
            else if x eq 'antisymmetric then go asym
            else symerr('index!-symmetries,t);
           sym: if scan() eq 'in then
                   begin scan();
                     flag('(antisymmetric),'delim);
                     b: y := cdr xread1 'for;
                        if eqcar(car y,'list) then
                           y := for each j on y collect
                                  if eqcar(car j,'list) and
                                     (null cdr j or
                                      (length car j = length cadr j))
                                     then cdar j
                                   else symerr('index!-symmetries,t);
                        syms := y . syms;
                        if null((x := cursym!*) eq 'antisymmetric) and
                           null(x eq '!*semicol!*) and
                           (scan() eq '!*lcbkt!*)
                           then  go to b;
                     remflag('(antisymmetric),'delim);
                   end
                 else <<syms := 'symmetric; x := cursym!*;
                        if x eq '!*comma!* then scan()>>;
           if x eq 'antisymmetric then go to asym
            else return {indexedvars,syms,asyms};
           asym: if scan() eq 'in then
                    begin scan();
                      flag('(symmetric),'delim);
                      c: y := cdr xread1 'for;
                         if eqcar(car y,'list) then
                           y := for each j on y collect
                                  if eqcar(car j,'list) and
                                     (null cdr j or
                                      (length car j = length cadr j))
                                     then cdar j
                                   else symerr('index!-symmetries,t);
                         asyms := y . asyms;
                         if null((x := cursym!*) eq 'symmetric) and
                            null(x eq '!*semicol!*) and
                            (scan() eq '!*lcbkt!*)
                            then  go to c;
                     remflag('(symmetric),'delim)
                   end
                 else <<asyms := 'antisymmetric; x := cursym!*;
                        if x eq '!*comma!* then scan()>>;
          if x eq 'symmetric then go to sym
           else return {indexedvars,syms,asyms}
         end) . res;
     if null(x eq '!*semicol!*)
        then go to a;
      return {'indexsymmetries,mkquote res}
   end;

put('index_symmetries,'stat,'index!-symmetriestat);

symbolic procedure indexsymmetries u;
   for each j in u do
     begin scalar v,x,y,z; integer n;
       v := cdr j;
       for each m in car j do
       <<x := v;
         if car v eq 'symmetric then x := list cdr m . cdr v
            else if cadr v eq 'antisymmetric
                    then x := {car v,list cdr m};
         n := 0;
         z := x;
         for each k in cdr m
             do <<x := subst(list('nth,'indl,n := n+1),k,x);
                  z := subst(n,k,z)>>;
         y := for each l in car x
                  collect {'lambda,'(indl),
                             {'tot!-sym!-indp,
                                {'evlis,if atom caar l
                                           then mkquote l
                                         else mkquote for each r in l
                                                   collect {'evlis,
                                     mkquote r}}}};
         for each l in cadr x
             do y := {'lambda,'(indl),
                           {'tot!-asym!-indp,
                           {'evlis,if atom caar l
                                      then mkquote l
                                    else mkquote for each r in l
                                                   collect {'evlis,
                                     mkquote r}}}} . y;
         put(car m,'indxsymmetries,y);
         y := for each l in car z
                  collect {'lambda,'(indl),
                                {'symmetrize!-inds,
                                  mkquote l,'indl}};
         for each l in cadr z
             do y := {'lambda,'(indl),
                           {'asymmetrize!-inds,
                                mkquote l,'indl}} . y;
         put(car m,'indxsymmetrize,y)>>
     end;

symbolic procedure indxsymp(u,bool);
   null bool or apply1(car bool,u) and indxsymp(u,cdr bool);

symbolic procedure tot!-sym!-indp u;
   null u or null cdr u or (car u = cadr u) or
    (if atom car u then indordp(car u,cadr u)
      else (indxchk car u or indxchk cadr u
            or indordlp(car u,cadr u)))
     and tot!-sym!-indp cdr u;

symbolic procedure tot!-asym!-indp u;
   null u or null cdr u or (null(car u=cadr u) and
    (if atom car u then indordp(car u,cadr u)
       else (indxchk car u or indxchk cadr u
             or indordlp(car u,cadr u))))
    and tot!-asym!-indp cdr u;

symbolic procedure indexsymmetrize u;
   begin scalar x,y; integer sgn;
     x := get(car u,'indxsymmetrize);
     sgn := 1;
     y := 1 . cdr u;
     a: if null x then return sgn . (car u . cdr y);
        y := apply1(car x,cdr y);
        if null y then return;
        sgn := car y*sgn;
        x := cdr x;
        go to a;
   end;

symbolic procedure symmetrize!-inds(u,v);
   begin scalar x,y,z; integer n;
     x := for each j in u
            collect if atom j then nth(v,j)
                     else for each k in j
                            collect nth(v,k);
     z := if atom car x then indordn x else flatindl indordln x;
     if null atom car u
        then u := flatindl u;
     x := pair(u,z);
     return 1 . for each j in v
                  collect if x and (caar x = (n := n+1))
                             then <<y := cdar x;
                                    x  := cdr x;
                                    y>>
                           else j
   end;

symbolic procedure asymmetrize!-inds(u,v);
   % Permp must use = here.
   begin scalar x,y,z; integer n,sgn;
     x := for each j in u
            collect if atom j then nth(v,j)
                     else for each k in j
                            collect nth(v,k);
     if repeats x then return;
     sgn := if permp(z := if atom car x then indordn x
                             else indordln x,x) then 1 else -1;
     if null atom car u
        then <<u := flatindl u; z := flatindl z>>;
     z := pair(u,z);
     return sgn . for each j in v
                    collect if z and (caar z = (n := n+1))
                               then <<y := cdar z;
                                      z  := cdr z;
                                      y>>
                             else j
   end;

symbolic procedure indordln u;
   if null u then nil
    else if null cdr u then u
    else if null cddr u then indordl2(car u,cadr u)
    else indordlad(car u,indordln cdr u);

symbolic procedure indordl2(u,v);
   if indordlp(u,v) then list(u,v) else list(v,u);

symbolic procedure indordlad(a,u);
   if null u then list a
    else if indordlp(a,car u) then a . u
    else car u . indordlad(a,cdr u);

endmodule;

end;
