module defintb;

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


algebraic ;

 defint_choose_data :=

{ %defint_choose(1/e**(~x),~var) => f1(1,x),
  defint_choose(1/~a**(~~b*~x),~var) => f1(1,x*log(a)*b)
        when a freeof var and b freeof var,
  defint_choose(sin(~x),~var)   => f1(2,x),
  defint_choose(-sin(~x),~var)   => f1(25,x),
  defint_choose(cos(~x),~var)   => f1(3,x),

  defint_choose(acos(~x)*Heaviside (1-(~x)),~var) => f1(7,x),
  defint_choose(acos(1/~x)*Heaviside ((~x)-1),~var) => f1(8,x),
  defint_choose(atan(~x),~var) => f1(9,x),
  defint_choose(mysinh(~x),~var) => f1(10,x),
  defint_choose((e^(2*~x)-1)/(2*e^~x),~var) => f1(10,x),   %sinh(x)
  defint_choose((e^(~y)-1)/(2*e^~x),~var) => f1(10,x)   %sinh(nx)
     when y = 2*x,
  defint_choose(mycosh(~x),~var)=> f1(11,x),

  defint_choose((e^(2*~x)+1)/(2*e^~x),~var) => f1(11,x),   %cosh(x)
  defint_choose((e^(~y)+1)/(2*e^~x),~var) => f1(11,x)   %cosh(nx)
     when y = 2*x,
  defint_choose(Heaviside (1-(~x)),~var) => f1(30,x),
  defint_choose(Heaviside ((~p-~x)/~p),~var) => f1(30,x/p),
  defint_choose(Heaviside ((~x)-1),~var) => f1(31,x),
  defint_choose(log(~x)*Heaviside (1-(~x)),~var) => f1(32,x),
  defint_choose(log(~x)*Heaviside ((~x)-1),~var) => f1(33,x),
  defint_choose((log(~x))^(~n)*Heaviside (1-(~x)),~var) => f31(n,x),
  defint_choose((log(~x))^(~n)*Heaviside ((~x)-1),~var) => f32(n,x),
  defint_choose(log(1+~x),~var) => f1(34,x),
  defint_choose(log((~x+1)/~x),~var) => f1(35,x),
  defint_choose(ei(-~x),~var) => f1(36,x),
  defint_choose(si(~x),~var) => f1(37,x),
  defint_choose(ci(~x),~var) => f1(38,x),
  defint_choose(shi(~x),~var) => f1(39,x),

  defint_choose(erf(~x),~var) => f1(41,x),
  defint_choose(-erf(~x)+1,~var) => f1(42,x),    %erfc(x)
  defint_choose(fresnel_s(~x),~var) => f1(43,x),
  defint_choose(fresnel_c(~x),~var) => f1(44,x),
  defint_choose(gamma(~n,~x),~var) => f1(45,x,n),

  defint_choose(besselj(~n,~x),~var) => f1(50,x,n),
  defint_choose(bessely(~n,~x),~var) => f1(51,x,n),
  defint_choose(besseli(~n,~x),~var) => f1(52,x,n),
  defint_choose(besselk(~n,~x),~var) => f1(53,x,n),
  defint_choose(struveh(~n,~x),~var) => f1(54,x,n),
  defint_choose(struvel(~n,~x),~var) => f1(55,x,n),
  defint_choose(m_legendrep(~n,~x)*Heaviside(1-(~x)),~var) =>
                                          f1(56,x,n),
  defint_choose(m_legendrep(~n,1/~x)*Heaviside((~x)-1),~var) =>
                                          f1(57,x,n),
  defint_choose((1-(~x))^(-1/2)*m_chebyshevt(~n,~x),~var) =>
                                          f1(58,x,n),
  defint_choose(((~x)-1)^(-1/2)*m_chebyshevt(~n,1/~x),~var) =>
                                          f1(59,x,n),
  defint_choose((1-(~x))^(1/2)*m_chebyshevu(~n,~x),~var) =>
                                          f1(60,x,n),
  defint_choose(((~x)-1)^(1/2)*m_chebyshevu(~n,1/~x),~var) =>
                                          f1(61,x,n),
  defint_choose(m_hermitep(~n,~x),~var) => f1(62,x,n),

  defint_choose(m_laguerrep(~n,~l,~x),~var) => f1(63,x,n,l),

  defint_choose(sqrt(1-~x)*m_gegenbauerp(~n,~l,~x),~var) =>
                                          f1(64,x,n,l),

  defint_choose(sqrt(1-~x)*(1-~x)*m_gegenbauerp(~n,~l,~x),~var) =>
                                          f1(64,x,n,l),

  defint_choose((~x-1)^~k*sqrt(~x-1)*m_gegenbauerp(~n,~l,~x),~var) =>
                                          f1(64,x,n,l),
  defint_choose((~x-1)^~k*sqrt(1-~x)*m_gegenbauerp(~n,~l,~x),~var) =>
                                          f1(64,x,n,l),

  defint_choose(-(~x-1)^~k*sqrt(1-~x)*m_gegenbauerp(~n,~l,~x),~var) =>
                                          f1(64,x,n,l),

  defint_choose(sqrt(~x-1)*m_gegenbauerp(~n,~l,1/~x),~var) =>
                                          f1(65,x,n,l),

  defint_choose(sqrt(~x-1)*(~x-1)*m_gegenbauerp(~n,~l,1/~x),~var) =>
                                          f1(65,x,n,l),

  defint_choose(sqrt(~x-1)*(~x-1)^(~k)*m_gegenbauerp(~n,~k,1/~x),~var)=>
                                          f1(65,x,n,l),

  defint_choose(-sqrt(~x-1)*(~x-1)^(~k)*m_gegenbauerp(~n,~k,1/~x),~var)
                                          => f1(65,x,n,l),

  defint_choose((1-~x)^~r*m_jacobip(~n,~r,~s,~x),~var) =>
                                                                       f1(66,x,n,r,s),
  defint_choose((~x-1)^~r*m_jacobip(~n,~r,~s,1/~x),~var) =>
                                          f1(67,x,n,r,s),
  defint_choose(0,~var) => f1(0,0),

  defint_choose(~n,~var) => f1(0,n)
     when numberp n,

  defint_choose(~f,~var)        => unknown };  % fallthrough case

let defint_choose_data;

endmodule;
end;




