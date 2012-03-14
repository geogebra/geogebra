% ----------------------------------------------------------------------
% $Id: guardianschemes.red 475 2009-11-28 14:03:08Z arthurcnorman $
% ----------------------------------------------------------------------
% (c) 1999 Andreas Dolzmann, 1999, 2009 Thomas Sturm
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

module guardianschemes;

put('gdmkguarded,'psopfn,'gd_mkguarded);

procedure gd_mkguarded(argl);
   % Make guarded expression.
   <<
      put(car argl,'rtypefn,'cquotegex);
      put(car argl,'gd_scheme,cadr argl)
   >>;

algebraic gdmkguarded(abs,ge(ger(true,abs(a1)),gec(a1>=0,a1),gec(a1<0,-a1)));
algebraic gdmkguarded(quotient,ge(geg(a2 neq 0,a1/a2)));
algebraic gdmkguarded(sqrt,ge(geg(a1>=0,sqrt(a1))));
algebraic gdmkguarded(sign,ge(
   ger(true,sign(a1)),gec(a1>0,1),gec(a1=0,0),gec(a1<0,-1)));

put('min,'rtypefn,'cquotegex);
put('min,'gd_schemefn,'gd_scheme!-min);
put('max,'rtypefn,'cquotegex);
put('max,'gd_schemefn,'gd_scheme!-max);

procedure gd_scheme!-min(n);
   'ge . {'ger,'true,'min . for i:=1:n collect mkid('a,i)} .
      for i:=1:n collect
	 {'gec,'and . for j:=1:n join
	    if j neq i then {{'leq,mkid('a,i),mkid('a,j)}},mkid('a,i)};

procedure gd_scheme!-max(n);
   'ge . {'ger,'true,'max . for i:=1:n collect mkid('a,i)} .
      for i:=1:n collect
	 {'gec,'and . for j:=1:n join
	    if j neq i then {{'geq,mkid('a,i),mkid('a,j)}},mkid('a,i)};

procedure gd_getscheme(op,n);
   begin scalar w;
      if (w:=get(op,'gd_scheme)) then
	 return w;
      if (w:=get(op,'gd_schemefn)) then
	 return apply(w,{n});
      return {'ge,{'geg,'true,op . for i:=1:n collect mkid('a,i)}}
   end;

endmodule;  % [guardianschemes]

end;  % of file
