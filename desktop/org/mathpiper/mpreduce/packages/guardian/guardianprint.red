% ----------------------------------------------------------------------
% $Id: guardianprint.red 475 2009-11-28 14:03:08Z arthurcnorman $
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

module guardianprint;

fluid '(gd_omode!*);

gd_omode!* := 'matrix;

flag('(gex),'sprifn);
put('gex,'tag,'ge);
put('ge,'setprifn,'gd_setgepri);
put('ge,'prifn,'gd_gepri);
put('ge,'fancy!-setprifn,'gd_fancy!-setgepri);
put('ge,'fancy!-prifn,'gd_fancy!-gepri);

put('gdomode,'psopfn,'gd_omode);

procedure gd_omode(argl);
   begin scalar w;
      w := gd_omode!*;
      gd_omode!* := car argl;
      return w
   end;

procedure gd_setgepri(v,u);
   apply(intern compress append(explode 'gd_setgepri,explode gd_omode!*),{v,u})
      where !*guardian=nil;

procedure gd_gepri(u);
   apply(intern compress append(explode 'gd_gepri,explode gd_omode!*),{u})
      where !*guardian=nil;

procedure gd_setgepridebug(v,u);
   if cdr u then setmatpri(v,u);

procedure gd_gepridebug(u);
   if cdr u then matpri u;

procedure gd_setgeprimatrix(v,u);
   gd_setgepridebug(v,'ge . for each x in cdr u collect cdr x);

procedure gd_geprimatrix(u);
   gd_gepridebug('ge . for each x in cdr u collect cdr x);

procedure gd_setgeprigcase(v,u);
   gd_setgepridebug(v,{'ge,cdar cdr u});

procedure gd_geprigcase(u);
   gd_gepridebug {'ge,cdar cdr u};

procedure gd_setgeprigterm(v,u);
   <<
      if cadr car cdr u = 'false then
	 lprim "contradictive situation";
      assgnpri(mk!*sq simp caddr car cdr u,{v},'only)
   >>;

procedure gd_geprigterm(u);
   <<
      if cadr car cdr u = 'false then
	 lprim "contradictive situation";
      assgnpri(mk!*sq simp caddr car cdr u,nil,'only)
   >>;

endmodule;  % [guardianprint]

end;  % of file
