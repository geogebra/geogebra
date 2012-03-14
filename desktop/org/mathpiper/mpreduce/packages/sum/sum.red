module sum;  % Driver for various sum capabilities.

% Author: Anthony C. Hearn, derived from code by F. Kako and W. Koepf.

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


create!-package('(sum sum2 complx prod),'(contrib sum));
% create!-package('(sum sum2 complx prod zeilberg),'(contrib sum));

fluid '(!*zeilberg);

switch zeilberg;

put('sum,'simpfn,'simp!-sum);

symbolic procedure freeof!-df(u, v);
   % check u contains differential operator with respect to v;
   if atom u then t
    else if car(u) eq 'df
     then freeof!-df(cadr u,v) and not smember(v,cddr u)
    else freeof!-dfl(cdr u,v);

symbolic procedure freeof!-dfl(u, v);
   if null u then t else freeof!-df(car u,v) and freeof!-dfl(cdr u,v);

symbolic procedure simp!-sum u;
   %ARGUMENT  CAR U: expression of prefix form.
   %         CADR U: kernel.
   %        CADDR U: lower bound.
   %       CADDDR U: upper bound.
   %value          : expression of sq form.
   begin scalar y;
      y := cdr u;
      u := car u;
      if not atom y and not freeof!-df(u, car y) then
        if atom y
               then return !*p2f(car fkern(list('sum,u)) .* 1) ./ 1
         else return sum!-df(u, y);
      u := simp!* u;
      return if null numr u then u
              else if atom y
               then !*p2f(car fkern(list('sum,prepsq u)) .* 1) ./ 1
              else if !*zeilberg then gosper!*(mk!*sq u,y)
              else simp!-sum0(u,y)
   end;

endmodule;

end;
