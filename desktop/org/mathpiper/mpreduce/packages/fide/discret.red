module discret; % Data for discretization.

% Author: Richard Liska.

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


algebraic;

difmatch all,1,
  0,1$
difmatch all,u,
  u=one,0,
    u(i),
  u=half,0,
    (u(i-1/2)+u(i+1/2))/2$
difmatch all,diff(u,x),
  u=one,2,
    (u(i+1)-u(i-1))/(dip1+dim1),
  u=half,0,
    (u(i+1/2)-u(i-1/2))/di$
difmatch all,diff(u,x,2),
  u=one,0,
    ((u(i+1)-u(i))/dip1-(u(i)-u(i-1))/dim1)/di,
  u=half,2,
    ((u(i+3/2)-u(i+1/2))/dip2-(u(i-1/2)-u(i-3/2))/dim2)/(dip1+dim1)$
difmatch all,u*v,
  u=one,v=one,0,
    u(i)*v(i),
  u=one,v=half,0,
    u(i)*(v(i-1/2)+v(i+1/2))/2,
  u=half,v=one,0,
    (u(i-1/2)+u(i+1/2))/2*v(i),
  u=half,v=half,0,
    (u(i-1/2)*v(i-1/2)+u(i+1/2)*v(i+1/2))/2$
difmatch all,u**n,
  u=one,0,
    u(i)**n,
  u=half,0,
    (u(i-1/2)**n+u(i+1/2)**n)/2$
difmatch all,u*v**n,
  u=one,v=one,0,
    u(i)*v(i)**n,
  u=one,v=half,0,
    u(i)*(v(i-1/2)**n+v(i+1/2)**n)/2,
  u=half,v=one,0,
    (u(i-1/2)+u(i+1/2))/2*v(i)**n,
  u=half,v=half,0,
    (u(i-1/2)*v(i-1/2)**n+u(i+1/2)*v(i+1/2)**n)/2$
difmatch all,u*v*w,
  u=one,v=one,w=one,0,
    u(i)*v(i)*w(i),
  u=one,v=one,w=half,0,
    u(i)*v(i)*(w(i+1/2)+w(i-1/2))/2,
  u=one,v=half,w=one,0,
    u(i)*(v(i-1/2)+v(i+1/2))/2*w(i),
  u=one,v=half,w=half,0,
    u(i)*(v(i-1/2)*w(i-1/2)+v(i+1/2)*w(i+1/2))/2,
  u=half,v=one,w=one,0,
    (u(i-1/2)+u(i+1/2))/2*v(i)*w(i),
  u=half,v=one,w=half,0,
    (u(i-1/2)*w(i-1/2)+u(i+1/2)*w(i+1/2))/2*v(i),
  u=half,v=half,w=one,0,
    (u(i-1/2)*v(i-1/2)+u(i+1/2)*v(i+1/2))/2*w(i),
  u=half,v=half,w=half,0,
    (u(i-1/2)*v(i-1/2)*w(i-1/2)+u(i+1/2)*v(i+1/2)*w(i+1/2))/2$
difmatch all,v*diff(u,x),
  u=one,v=one,2,
    v(i)*(u(i+1)-u(i-1))/(dip1+dim1),
  u=one,v=half,2,
    (v(i+1/2)+v(i-1/2))/2*(u(i+1)-u(i-1))/(dip1+dim1),
  u=half,v=one,0,
    v(i)*(u(i+1/2)-u(i-1/2))/di,
  u=half,v=half,0,
    (v(i+1/2)+v(i-1/2))/2*(u(i+1/2)-u(i-1/2))/di$
difmatch all,v*w*diff(u,x),
  u=one,v=one,w=one,2,
    v(i)*w(i)*(u(i+1)-u(i-1))/(dip1+dim1),
  u=one,v=one,w=half,2,
    v(i)*(w(i-1/2)+w(i+1/2))/2*(u(i+1)-u(i-1))/(dip1+dim1),
  u=one,v=half,w=one,2,
    (v(i+1/2)+v(i-1/2))/2*w(i)*(u(i+1)-u(i-1))/(dip1+dim1),
  u=one,v=half,w=half,2,
    (v(i+1/2)*w(i+1/2)+v(i-1/2)*w(i-1/2))/2*(u(i+1)-u(i-1))/(dip1+dim1),
  u=half,v=one,w=one,0,
    v(i)*w(i)*(u(i+1/2)-u(i-1/2))/di,
  u=half,v=one,w=half,0,
    v(i)*(w(i-1/2)+w(i+1/2))/2*(u(i+1/2)-u(i-1/2))/di,
  u=half,v=half,w=one,0,
    (v(i+1/2)+v(i-1/2))/2*w(i)*(u(i+1/2)-u(i-1/2))/di,
  u=half,v=half,w=half,0,
    (v(i+1/2)*w(i+1/2)+v(i-1/2)*w(i-1/2))/2*(u(i+1/2)-u(i-1/2))/di$
difmatch all,x*u,
  u=one,0,
    x(i)*u(i),
  u=half,1,
    (x(i-1/2)*u(i-1/2)+x(i+1/2)*u(i+1/2))/2$
difmatch all,u/x**n,
  u=one,0,
    u(i)/x(i)**n,
  u=half,0,
    (u(i-1/2)/x(i-1/2)**n+u(i+1/2)/x(i+1/2)**n)/2$
difmatch all,u*v/x**n,
  u=one,v=one,0,
    u(i)*v(i)/x(i)**n,
  u=one,v=half,0,
    u(i)*(v(i-1/2)+v(i+1/2))/2/x(i)**n,
  u=half,v=one,0,
    (u(i-1/2)+u(i+1/2))/2*v(i)/x(i)**n,
  u=half,v=half,0,
    (u(i-1/2)*v(i-1/2)/x(i-1/2)**n+u(i+1/2)*v(i+1/2)/x(i+1/2)**n)/2$
difmatch all,diff(x**n*u,x)/x**n,
  u=one,2,
    (x(i+1)**n*u(i+1)-x(i-1)**n*u(i-1))/x(i)**n/(dim1+dip1),
  u=half,0,
    (x(i+1/2)**n*u(i+1/2)-x(i-1/2)**n*u(i-1/2))/di/x(i)**n$
difmatch all,diff(u*v,x),
  u=one,v=one,4,
    (u(i+1)*v(i+1)-u(i-1)*v(i-1))/(dim1+dip1),
  u=one,v=half,2,
    ((u(i+1)+u(i))/2*v(i+1/2)-(u(i-1)+u(i))/2*v(i-1/2))/di,
  u=half,v=one,2,
    ((v(i+1)+v(i))/2*u(i+1/2)-(v(i-1)+v(i))/2*u(i-1/2))/di,
  u=half,v=half,0,
    (u(i+1/2)*v(i+1/2)-u(i-1/2)*v(i-1/2))/di$
difmatch all,diff(u*v,x)/x**n,
  u=one,v=one,4,
    (u(i+1)*v(i+1)-u(i-1)*v(i-1))/x(i)**n/(dim1+dip1),
  u=one,v=half,2,
    ((u(i+1)+u(i))/2*v(i+1/2)-(u(i-1)+u(i))/2*v(i-1/2))/x(i)**n/di,
  u=half,v=one,2,
    ((v(i+1)+v(i))/2*u(i+1/2)-(v(i-1)+v(i))/2*u(i-1/2))/x(i)**n/di,
  u=half,v=half,0,
    (u(i+1/2)*v(i+1/2)-u(i-1/2)*v(i-1/2))/x(i)**n/di$
difmatch all,diff(u*diff(v,x),x)/x**n,
  u=half,v=one,0,
   (u(i+1/2)*(v(i+1)-v(i))/dip1-u(i-1/2)*(v(i)-v(i-1))/dim1)/di/x(i)**n,
  u=half,v=half,2,
    (u(i+1/2)*(v(i+3/2)-v(i-1/2))/(di+dip2)-u(i-1/2)*(v(i+1/2)-
       v(i-3/2))/(di+dim2))/di/x(i)**n,
  u=one,v=one,2,
    ((u(i+1)+u(i))/2*(v(i+1)-v(i))/dip1-(u(i)+u(i-1))/2*(v(i)-v(i-1))
       /dim1)/di/x(i)**n,
  u=one,v=half,4,
    ((u(i+1)+u(i))/2*(v(i+3/2)-v(i-1/2))/(di+dip2)-
     (u(i)+u(i-1))/2*(v(i+1/2)-v(i-3/2))/(di+dim2))/di/x(i)**n$

endmodule;

end;
