module tableaux;

% Definition and manipulation of tableaux using tab structure

% Author: David Hartley

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


Comment. Tableaux are stored internally in a tagged matrix of 1-forms:

         tab     ::= 'tab . matrix of pf

The external format is a matrix:

         prefix tab       ::= prefix matrix of prefix


endcomment;


symbolic procedure !*a2tab u;
   % u:prefix tab -> tab
   % primitive - not much checking
   if not eqcar(u,'mat) then typerr(u,'matrix)
   else mktab foreach r in cdr u collect
           foreach f in r collect xpartitop f;


symbolic procedure !*tab2a u;
   % u:tab -> prefix tab
   'mat . foreach r in cdr u collect
         foreach f in r collect !*pf2a f;


symbolic procedure mktab u;
   % u:matrix of pf -> mktab:tab
   'tab . u;


symbolic procedure tableaup u;
   % u:any -> tableaup:bool
   eqcar(u,'tab);


put('tableau,'psopfn,'tableaueval);

symbolic procedure tableaueval s;
   % s:{eds} -> prefix tab
   if not edsp(s := reval car s) then typerr(s,'eds)
   else !*tab2a edscall tableau s;


symbolic procedure tableau s;
   % s:eds -> tab
   % very much like characterseds
   % Only non-empty rows are returned, unless whole thing is empty,
   % in which case a 1xN matrix of zeros is returned.
   begin scalar prl,ind;
   if not pfaffian s or not quasilinearp s then
      rerror(eds,000,
             "Tableau only works for quasilinear Pfaffian systems");
   s := car tmpind closure s;
   prl := prlkrns s; ind := indkrns s;
   s := foreach f in nonpfaffpart eds_sys s join
           if f := linearpart(f,prl) then {f};
   if null s then return mktab {nlist(nil,length ind)};
   return mktab foreach f in s collect
                   foreach i in ind collect negpf xcoeff(f,wedgefax i);
   end;

endmodule;

end;
