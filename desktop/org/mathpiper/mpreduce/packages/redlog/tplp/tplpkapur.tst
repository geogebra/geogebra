% ----------------------------------------------------------------------
% $Id: tplpkapur.tst 469 2009-11-28 13:58:18Z arthurcnorman $
% ----------------------------------------------------------------------
% Copyright (c) 2007-2009 Thomas Sturm
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

load redlog$

% sokrates (true)
rlset(tplp,{{sokrates,0}},{{human,1},{mortal,1}})$
sok := (all(x,human(x) impl mortal(x)) and human(sokrates)) impl mortal(sokrates)$
rlkapur sok;

% 3.1 (true)
rlset(tplp,{{fi,1}},{{m,3},{p,1}})$
ex31 := (ex(y,all(x,(m(y,x,x) and m(x,y,x) and m(x,fi(x),y) and m(fi(x),x,y))))
   and (all({x,y,z}, ((p(x) and p(y) and m(x,fi(y),z)) impl p(z))))) impl
   all(z,p(z) impl p(fi(z)))$
rlkapur ex31;

% 4.2 (true)
rlset(tplp,{},{{a,1},{b,1}})$
ex42 := all(x,a(x) equiv b(x)) impl (all(x,a(x)) equiv all(x,b(x)))$
rlkapur ex42;

% 4.3 (satisfiable)
rlset(tplp,{},{{p,0},{q,1}})$
ex43 := ex(x,p() impl q(x)) impl (p() impl all(x,q(x)))$
rlkapur ex43;

% 4.4 (true)
rlset(tplp,{},{{p,0},{q,1}})$
ex44 := ex(x,p() impl q(x)) impl (p() impl ex(x,q(x)))$
rlkapur ex44;

% 4.5 -> infinit example
rlset(tplp,{},{{p,2}})$
ex45 := not(all(x,not(p(x,x))) and all({x,y,z},(p(x,y) and p(y,z)) impl p(x,z))
   and all(x,ex(y,p(x,y))))$
%rlkapur ex45;

% 4.6 -> satisfiable (finit GB not containing 1)
rlset(tplp,{{f,1}},{{p,1}})$
ex46 := (p(f(x)) or p(x)) and (not(p(f(y))) or not(p(y)))$
rlkapur not(ex46);

% 4.7 (true) -> takes very long without optimization
rlset(tplp,{},{{p,1},{q,1}})$
ex47 :=
(ex(x,all(y,p(x) equiv p(y))) equiv ex(x,q(x)) equiv all(y,p(y)))
 equiv
(ex(x,all(y,q(x) equiv q(y))) equiv ex(x,p(x)) equiv all(y,q(y)))$
rlkapur ex47;

end;
