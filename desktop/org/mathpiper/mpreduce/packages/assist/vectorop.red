module vectorop;

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


% This small module makes basic operation between EXPLICIT
% vectors available. They are assumed to be represented by
% BAGS or LISTS.
% Mixed product is restricted to 3-space vectors.
                ;
symbolic procedure depthl1!: u;
 if null u then t else (caar u neq 'list) and depthl1!: cdr u;

symbolic procedure depthl1 u;
not null getrtype u and  depthl1!: cdr u;

symbolic procedure !:vect(u,v,bool);
   %returns a list whose elements are the sum of each  list elements.
   % null v check not necessary;
   if null u then nil
else  addsq(car u,if null bool then car v else negsq car v)
                                          . !:vect(cdr u,cdr v,bool);
symbolic procedure rsumvect(u);
begin scalar x,y,prf;
x:=reval car u;y:=reval cadr u; prf:=car x;
 if (rdepth list x = 0) or (rdepth list y = 0) then
    rederr " both arguments must be of depth 1 " else
x:=cdr x; y:=cdr y;
if length x neq length y then rederr "vector mismatch";
x:=for each j in x collect simp!* j;
y:=for each j in y collect simp!* j;
return prf . (for each j in !:vect(x,y,nil) collect mk!*sq j) end;

put('sumvect,'psopfn,'rsumvect);

symbolic procedure rminvect(u);
begin scalar x,y,prf;
x:=reval car u;y:=reval cadr u; prf:=car x;
 if (rdepth list x = 0) or (rdepth list y = 0) then
    rederr " both arguments must be of depth 1 " else
x:=cdr x; y:=cdr y;
if length x neq length y then rederr "vector mismatch";
x:=for each j in x collect simp!* j;
y:=for each j in y collect simp!* j;
return prf . (for each j in !:vect(x,y,'minus) collect mk!*sq j) end;

put('minvect,'psopfn,'rminvect);

symbolic procedure !:scalprd(u,v);
   %returns scalar product of two lists;
   if null u and null v then nil ./ 1
    else addsq(multsq(car u,car v),!:scalprd(cdr u,cdr v));

symbolic procedure sscalvect(u);
begin scalar x,y;
  x:=reval car u;y:=reval cadr u;
 if (rdepth list x = 0) or (rdepth list y = 0) then
     rederr " both arguments must be of depth 1 " else
 if length x neq length y then rederr "vector mismatch";
  x:=cdr x; y:=cdr y;
  x:=for each j in x collect simp!* j;
  y:=for each j in y collect simp!* j;
 return mk!*sq !:scalprd(x,y)
end;

put('scalvect,'psopfn,'sscalvect);

symbolic procedure !:pvect3 u;
begin scalar x,y; integer xl;
 if (rdepth list car u = 0) or (rdepth cdr u = 0) then
    rederr " both arguments must be of depth 1 " else
 x:=reval car u;y:=reval cadr u;
 if (xl:=length x) neq 4 then rederr "not 3-space vectors" else
 if xl neq length y then rederr "vector mismatch" ;
 x:=cdr x; y:=cdr y;
 x:=for each j in x collect simp!* j;
 y:=for each j in y collect simp!* j;
 return
 list( addsq(multsq(cadr x,caddr y),negsq multsq(caddr x,cadr y)),
        addsq(multsq(caddr x,car y),negsq multsq(car x,caddr y)),
         addsq(multsq(car x,cadr y),negsq multsq(cadr x,car y)))
end;

symbolic procedure rcrossvect u;
% implemented only with LIST prefix;
'list . (for each j in !:pvect3 u collect mk!*sq j);

put ('crossvect,'psopfn,'rcrossvect);

symbolic procedure smpvect u;
begin scalar x;
if  (rdepth list car u =0) then
    rederr " arguments must be of depth 1 "  else
x:=reval car u; u:=cdr u;
x:=cdr x;
if length x neq 3 then rederr " not 3-space vector";
x:=for each j in x collect simp!* j;
return mk!*sq !:scalprd(x,!:pvect3 u) end;

put('mpvect,'psopfn,'smpvect);


endmodule;

end;
