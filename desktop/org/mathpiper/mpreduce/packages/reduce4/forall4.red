module forall4;  % Support for "let" etc. statements in REDUCE 4.

% Author: Anthony C. Hearn.

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


% For the time being, we are defaulting to the REDUCE 3 model until we
% decide how such rules should be handled.

symbolic procedure n_formforall(u,vars);
   mkobject(formforall(u,vars,'algebraic),'noval);

put('forall,'n_formfn,'n_formforall);

symbolic procedure n_formlet(u,vars);
   mkobject(formlet(u,vars,'algebraic),'noval);

put('let,'n_formfn,'n_formlet);

symbolic procedure n_formclear(u,vars);
   mkobject(formclear(u,vars,'algebraic),'noval);

put('clear,'n_formfn,'n_formclear);

symbolic procedure n_formmatch(u,vars);
   mkobject(formmatch(u,vars,'algebraic),'noval);

put('match,'n_formfn,'n_formmatch);

symbolic procedure form4where(u,vars);
   begin scalar expn,equivs;
      expn := n_form1(cadr u,vars);
      equivs := remcomma caddr u;
      equivs := formc('list . equivs,vars,'algebraic);
      equivs := cadr equivs;  % FIX THIS.
      return mkobject(
        {'prog, '(newrule!* oldrules!* v w),
            {'setq, 'w, {'set_rules,{'cdr, equivs}, nil}}, % FIX THIS.
            {'setq, 'u, {'errorset!*,
                            {'mkquote, {'simp4!*,value expn}}, nil}},
            '(restore_rules w),
            '(return (cond ((errorp u) (rederr nil)) (t (car u))))},
        type expn)
   end;

put('where,'n_formfn,'form4where);

endmodule;

end;
