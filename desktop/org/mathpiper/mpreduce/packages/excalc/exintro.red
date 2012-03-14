module exintro;

% Author: Eberhard Schruefer.

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


fluid '(depl!*);

global '(dimex!* lftshft!* detm!* basisforml!* sgn!* wedgemtch!*
         bndeq!* basisvectorl!* indxl!* nosuml!* !*nosum coord!*
         keepl!* metricd!* metricu!* !*product!-rule);

% Some initialiations.

dimex!* := !*q2f simp 'dim;
sgn!* := !*k2q 'sgn;
!*product!-rule := t;

rlistat('(pform fdomain remfdomain tvector spacedim forder remforder
          frame dualframe keep closedform xpnd noxpnd
          isolate remisolate));

symbolic procedure spacedim u;
   begin
     dimex!* := !*q2f simp car u
   end;

symbolic procedure fdomain u;
   %Sets up implicit dependencies;
   while u do
     <<if not eqexpr car u then errpri2(car u,'hold)
        else begin scalar y;
               rmsubs();
               y := get(cadar u,'rtype);
               remprop(cadar u,'rtype);
               for each x  in cdr caddar u do
                 <<if indvarp x then
                     for each j in mkaindxc(flatindxl cdr x,nil) do
                        depend1(cadar u,prepsq simpindexvar
                                sublis(pair(flatindxl cdr x,j),x),t)
                    else depend1(cadar u,x,t)>>;
               flag(list cadar u,'impfun);
               if y then put(cadar u,'rtype,y)
             end;
       u := cdr u>>;


symbolic procedure remfdomain u;
%Removes implicit dependencies;
   begin scalar x;
     for each j in u do
         if x := assoc(j,depl!*) then <<depl!* := delete(x,depl!*);
                                        remflag(list j,'impfun)>>
          else rerror(excalc,1,list(j," had no dependencies"));
   end;

symbolic procedure putform(u,v);
   if atom u then <<if flagp(u,'reserved)
                       then <<remflag({u},'reserved);
                              lpri {"***Warning: reserved variable",
                                    u,"declared exterior form"}>>;
                    put(u := !*a2k u,'fdegree,list !*q2f simp v);
                            put(u,'clearfn,'clearfdegree)>>
    else begin scalar x,y; integer n;
           n := length cdr u;
           if (x := get(car u,'ifdegree)) and (y := assoc(n,x))
              then x := delete(y,x);
           put(car u,'ifdegree,if x then (n . !*q2f simp v) . x
                                else list(n . !*q2f simp v));
           x := car u;
           flag(list x,'indexvar);
           put(x,'rtype,'indexed!-form);
           put(x,'simpfn,'simpindexvar);
           put(x,'partitfn,'partitindexvar);
           put(x,'evalargfn,'revalindl);
           flag(list x,'full);
           put(x,'prifn,'indvarprt);
           put(x,'fancy!-pprifn,'xindvarprt);
           % The next line is needed in 3.6 to avoid the wrong
           % simplification of an index -0 to 0.
           remflag('(minus),'intfn);
           if null numr simp v then flag(list x,'covariant)
         end;

symbolic procedure pform u;
   begin rmsubs();
     for each j in u do
       if not eqexpr j then errpri2(j,'hold)
        else if eqcar(cadr j,'list)
                then for each k in cdadr j do putform(k,caddr j)
        else putform(cadr j,caddr j)
   end;

symbolic procedure tvector u;
   for each j in u do putform(j,-1);

symbolic procedure getlower u;
   cdr atsoc(u,metricd!*);

symbolic procedure getupper u;
   cdr atsoc(u,metricu!*);

symbolic procedure xpnd u;
   <<rmsubs(); remflag(u,'noxpnd)>>;

symbolic procedure noxpnd u;
   <<rmsubs(); flag(u,'noxpnd)>>;

symbolic procedure closedform u;
   <<rmsubs(); flag(u,'closed)>>;


symbolic procedure memqcar(u,v);
   null atom u and car u memq v;

endmodule;

end;
