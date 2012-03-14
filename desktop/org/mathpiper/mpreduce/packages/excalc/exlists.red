module excalc!-lists;

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


symbolic procedure exdflist(u,v);
   'list . exdfl1 listeval(car u,v);

symbolic procedure exdfl1 u;
   if null u then nil
    else (if x then mk!*sq !*pf2sq x . exdfl1 cdr u
           else exdfl1 cdr u)
           where x = partitexdf list car u;

put('d,'listfn,'exdflist);

symbolic procedure innerprodlist(u,v);
  ('list . if eqcar(x,'list) then
              if eqcar(y,'list) then
               rederr "currently only one list arg is implemented"
               else innerprodl1(cdr x,y)
            else if eqcar(y,'list)
                    then innerprod1l(x,cdr y))
   where x = if getrtype car u eq 'list then listeval(car u,nil)
              else car u,
         y = if getrtype cadr u eq 'list then listeval(cadr u,nil)
              else cadr u;

symbolic procedure innerprodl1(u,v);
   if null u then nil
    else (if x then mk!*sq !*pf2sq x . innerprodl1(cdr u,v)
           else innerprodl1(cdr u,v))
           where x = partitinnerprod list(car u,v);

symbolic procedure innerprod1l(u,v);
   if null v then nil
    else (if x then mk!*sq !*pf2sq x . innerprod1l(u,cdr v)
           else innerprod1l(u,cdr v))
           where x = partitinnerprod list(u,car v);

put('innerprod,'listfn,'innerprodlist);

symbolic procedure liedflist(u,v);
    ('list . if eqcar(x,'list) then
              if eqcar(y,'list) then
               rederr "currently only one list arg is implemented"
               else liedfl1(cdr x,y)
            else if eqcar(y,'list)
                    then liedf1l(x,cdr y))
   where x = if getrtype car u eq 'list then listeval(car u,nil)
              else car u,
         y = if getrtype cadr u eq 'list then listeval(cadr u,nil)
              else cadr u;

symbolic procedure liedfl1(u,v);
   if null u then nil
    else (if x then mk!*sq !*pf2sq x . liedfl1(cdr u,v)
           else liedfl1(cdr u,v))
           where x = partitliedf list(car u,v);

symbolic procedure liedf1l(u,v);
   if null v then nil
    else (if x then mk!*sq !*pf2sq x . liedf1l(u,cdr v)
           else liedf1l(u,cdr v))
           where x = partitliedf list(u,car v);


put('liedf,'listfn,'liedflist);

symbolic procedure modulolist(u,v);
   'list . modulol1(cdr listeval(car u,nil),cadr u);

symbolic procedure modulol1(u,v);
   if null u then nil
    else (if x then mk!*sq !*pf2sq x . modulol1(cdr u,v)
           else modulol1(cdr u,v))
           where x = partitmodulo list(car u,v);

put('modulo,'listfn,'modulolist);

symbolic procedure wedgelist(u,v);
   ('list . if eqcar(x,'list) then
              if eqcar(y,'list) then
               rederr "currently only one list arg is implemented"
               else wedgel1(cdr x,y)
            else if eqcar(y,'list)
                    then wedge1l(x,cdr y))
   where x = if getrtype car u eq 'list then listeval(car u,nil)
              else car u,
         y = if getrtype cadr u eq 'list then listeval(cadr u,nil)
              else cadr u;

symbolic procedure wedgel1(u,v);
   if null u then nil
    else (if x then mk!*sq !*pf2sq x . wedgel1(cdr u,v)
           else wedgel1(cdr u,v))
           where x = partitwedge list(car u,v);

symbolic procedure wedge1l(u,v);
   if null v then nil
    else (if x then mk!*sq !*pf2sq x . wedge1l(u,cdr v)
           else wedge1l(u,cdr v))
           where x = partitwedge list(u,car v);

put('wedge,'listfn,'wedgelist);

symbolic procedure exc!-maplist(u,v);
  ('list .  if eqcar(y,'list)
                    then exc!-map1l(car u,cadr u,cdr y))
   where y = if getrtype caddr u eq 'list then listeval(caddr u,v)
              else caddr u;


symbolic procedure exc!-map1l(u,p,v);
   if null v then nil
    else (if x then mk!*sq !*pf2sq x . exc!-map1l(u,p,cdr v)
           else exc!-map1l(u,p,cdr v))
           where x = excalc!-mapfn list(u,p,car v);



endmodule;

end;
