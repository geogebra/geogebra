module lmatrix;

%**********************************************************************%
%                                                                      %
% This module forms the ability for matrices to be passed locally.     %
%                                                                      %
% Authors: W. Neun (customised by Matt Rebbeck).                       %
%                                                                      %
%**********************************************************************%

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


switch mod_was_on;  % Used internally to keep track of the modular
                    % switch.


symbolic procedure mkmatrix(n,m);
  %
  % Create an nXm matrix.
  %
   'mat . (for i:=1:n collect
           for j:=1:m collect 0);



symbolic procedure setmat(matri,i,j,val);
  %
  % Set matrix element (i,j) to val.
  %
  <<   if !*modular then << off modular; on mod_was_on; >>;
       i := my_reval i;
       j := my_reval j;
       my_letmtr(list(matri,i,j),val,matri);
       if !*mod_was_on then << on modular; off mod_was_on; >>;
       matri>>;



symbolic procedure getmat(matri,i,j);
  %
  % Get matrix element (i,j).
  %
  <<   if !*modular then << off modular; on mod_was_on; >>;
       i := my_reval i;
       j := my_reval j;
       if !*mod_was_on then << on modular; off mod_was_on; >>;
       unchecked_getmatelem list(matri,i,j)>>;



symbolic procedure unchecked_getmatelem u;
  begin scalar x;
         if not eqcar(x :=  car u,'mat)
          then  rerror(matrix,1,list("Matrix",car u,"not set"))
         else return nth(nth(cdr x,cadr u),caddr u);
   end;



symbolic procedure my_letmtr(u,v,y);
  %
  % Substitution for matrix elements with reval only when necessary.
  %
  begin
    scalar z;
    if not eqcar(y,'mat) then
     rerror(matrix,10,list("Matrix",car u,"not set"))
      else if not numlis (z := my_revlis cdr u) or length z neq 2
       then return errpri2(u,'hold);
    rplaca(pnth(nth(cdr y,car z),cadr z),v);
  end;


endmodule;  % lmatrix.

end;
