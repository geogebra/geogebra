module forder;

% Author: Eberhard Schruefer;

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


global '(keepl!* wedgemtch!* lftshft!* indxl!*);

fluid '(kord!* subfg!*);


symbolic procedure add2l(u,v);
   !*a2k u . if u memq v then delete(u,v) else v;

symbolic procedure forder u;
   forder1 u;

symbolic procedure forder1 u;
   (lambda x;
    while x do
    <<kord!* := add2l(car x,kord!*);
      if eqcar(car x,'wedge) then
         for each j in reverse cdar x do
             kord!* := add2l(j,kord!*);
      x:=cdr x>>)
    reverse u;

symbolic procedure remforder u;
   for each j in u do kord!* := delete(j,kord!*);

symbolic procedure isolate u;
   rerror(excalc,2,"Sorry, ISOLATE not supported in this version");
%  for each j in u do
%    <<lftshft!* := !*a2k car u . lftshft!*;
%      kord!* := !*a2k car u . kord!*>>;

symbolic procedure remisolate u;
   for each j in u do lftshft!* := delete(j,lftshft!*);

symbolic procedure worderp(x,y);
   if null atom x and flagp(car x,'indexvar) and
      null atom y and flagp(car y,'indexvar)
      then indexvarordp(x,y)
    else if atom x or (x memq kord!*) then
            if atom y or (y memq kord!*) then ordop(x,y)
             else (if x eq z then t
                    else worderp(x,z)) where z = peel y
    else if atom y or (y memq kord!*)
            then (if z eq y then nil
                   else worderp(z,y)) where z = peel x
    else worderp(peel x,peel y);

symbolic procedure indexvarordp(u,v);
   if not(car u eq car v) or (u memq kord!*) or (v memq kord!*) then
      ordop(u,v)
   else ((if boundindp(x,indxl!*) then
      if boundindp(y,indxl!*) then indordlp(cdr u,cdr v)
      else t
   else if boundindp(y,indxl!*) then nil
   else ordop(u,v))
      where x = flatindxl cdr u, y = flatindxl cdr v);

symbolic procedure indordlp(u,v);
   if null u then nil
    else if null v then t
    else if car u = car v then indordlp(cdr u, cdr v)
    else if atom car u then
      if atom car v then indordp(car u,car v)
       else t
    else if atom car v then nil
    else indordp(cadar u,cadar v);

symbolic procedure peel u;
   if car u memq '(liedf innerprod) then caddr u
    else if car u eq 'quotient then
            if worderp(cadr u,caddr u) then cadr u
             else caddr u
    else cadr u;

symbolic procedure indordp(u,v);
   begin scalar x;
     x := indxl!*;
     if null(u memq x) then return t;
     a: if null x then return orderp(u,v);
     if u eq car x then return t
     else if v eq car x then return nil;
     x := cdr x;
     go to a
  end;

symbolic procedure indordn u;
   if null u then nil
    else if null cdr u then u
    else if null cddr u then indord2(car u,cadr u)
    else indordad(car u,indordn cdr u);

symbolic procedure indord2(u,v);
   if indordp(u,v) then list(u,v) else list(v,u);

symbolic procedure indordad(a,u);
   if null u then list a
    else if indordp(a,car u) then a . u
    else car u . indordad(a,cdr u);

symbolic procedure keep u;
   while u do
     <<if not eqexpr car u then errpri2(car u,'hold)
        else begin scalar x,y,z;
       z := subfg!*;
       subfg!* := nil;
       x := !*a2k cadar u;
       y := !*a2k caddar u;
       forder1 list(x,y);
       keepl!* := (x . y) . keepl!*;
       flag(list x,'keep);
       put(x,'keepl,list y);
       subfg!* := z;
       putdep(x,y);
       if null exdfk y then flag(list x,'closed);
       if eqcar(y,'wedge) then
         <<wedgemtch!*:=(cdr y . x) . wedgemtch!*;
           for each j in cdr y do
             wedgemtch!* := (list(x,j) . nil) . wedgemtch!*>>
       else let2(y,x,nil,t)
     end;
     u := cdr u>>;

symbolic procedure putdep(u,v);
   for each j in cdr v do
     if atom j then depend1(u,j,t) else putdep(u,j);

endmodule;

end;
