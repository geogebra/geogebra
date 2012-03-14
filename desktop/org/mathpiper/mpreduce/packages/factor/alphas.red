module alphas;

% Authors: A. C. Norman and P. M. A. Moore, 1981.

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


fluid '(alphalist current!-modulus hensel!-growth!-size
        number!-of!-factors);


%********************************************************************;
%
% This section contains access and update functions for the alphas.


symbolic procedure get!-alpha poly;
% Gets the poly and its associated alpha from the current alphalist
% if poly is not on the alphalist then we force an error.
  begin scalar w;
    w:=assoc!-alpha(poly,alphalist);
    if null w then errorf list("Alpha not found for ",poly," in ",
        alphalist);
    return w
  end;

symbolic procedure divide!-all!-alphas n;
% Multiply the factors by n mod p and alter the alphas accordingly.
  begin scalar om,m,nn;
    om:=set!-modulus hensel!-growth!-size;
    nn:=modular!-number n;
    m:=modular!-expt(
          modular!-reciprocal nn,
          number!-of!-factors #- 1);
    alphalist:=for each a in alphalist collect
      (times!-mod!-p(nn,car a) . times!-mod!-p(m,cdr a));
    set!-modulus om
  end;

symbolic procedure multiply!-alphas(n,oldpoly,newpoly);
% Multiply all the alphas except the one associated with oldpoly
% by n mod p. also replace oldpoly by newpoly in the alphalist.
  begin scalar om,faca;
    om:=set!-modulus hensel!-growth!-size;
    n:=modular!-number n;
    oldpoly:=reduce!-mod!-p oldpoly;
    faca:=get!-alpha oldpoly;
    alphalist:=delete(faca,alphalist);
    alphalist:=for each a in alphalist collect
      car a . times!-mod!-p(cdr a,n);
    alphalist:=(reduce!-mod!-p newpoly . cdr faca) . alphalist;
    set!-modulus om
  end;

symbolic procedure multiply!-alphas!-recip(n,oldpoly,newpoly);
% Multiply all the alphas except the one associated with oldpoly
% by the reciprocal mod p of n. also replace oldpoly by newpoly.
  begin scalar om,w;
    om:=set!-modulus hensel!-growth!-size;
    n:=modular!-reciprocal modular!-number n;
    w:=multiply!-alphas(n,oldpoly,newpoly);
    set!-modulus om;
    return w
  end;

endmodule;

end;
