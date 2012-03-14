% ----------------------------------------------------------------------
% $Id: rlslv.red 773 2010-10-05 21:33:20Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2010 Thomas Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
%

lisp <<
   fluid '(rl_slv_rcsid!* rl_slv_copyright!*);
   rl_sl_rcsid!* := "$Id: rlslv.red 773 2010-10-05 21:33:20Z thomas-sturm $";
   rl_sl_copyright!* := "Copyright (c) 2010 T. Sturm"
>>;

module rlslv;

global '(sl_slvc!*);
if not sl_slvc!* then sl_slvc!* := 0;

algebraic operator slv;
put('slv,'prifn,'slv_pri);
put('slv,'rl_simpfn,'slv_simp);

flag('(slv_simp),'full);

!#if rldynamic!#

procedure slv_mk(n);
   {nil,'slv,n};

procedure slv_n(slv);
   caddr slv;

procedure slv_simp(u);
   nil . u;

procedure slv_prep(slv);
   cdr slv;

!#else

procedure slv_mk(n);
   {'slv,n};

procedure slv_n(slv);
   cadr slv;

procedure slv_simp(u);
   u;

procedure slv_prep(slv);
   slv;

!#endif

procedure slv_new();
   slv_mk(sl_slvc!* := sl_slvc!* + 1);

procedure slv_sub(slv,al);
   (if w then cdr w else slv) where w=assoc(slv,al);

procedure slv_neg(slv);
   slv_mk(-slv_n slv);

procedure slv_pri(u);
   if not !*nat then
      'failed
   else <<
      prin2!* "#";
      prin2!* cadr u;
      prin2!* "#"
   >>;

rl_mkexternal('slv,'cl_simpl,function(lambda x; x));  % HACK
rl_mkexternal('slv,'cl_atnum,function(lambda x; 0));
rl_mkexternal('slv,'cl_depth,function(lambda x; 0));
rl_mkexternal('slv,'cl_pnf2,function(lambda x; {x}));
rl_mkexternal('slv,'rl_resimp,function(lambda x; x));
rl_mkexternal('slv,'rl_prepfof1,'slv_prep);
rl_mkexternal('slv,'cl_dcollect1,function(lambda f,n; nil));

endmodule;  % rlslv

end;  % of file
