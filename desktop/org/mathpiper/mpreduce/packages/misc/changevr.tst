%*********************************************************************;
%         This is a test file    for  the  CHANGEVAR package.         ;
%         Make sure that before you attempt to run it the             ;
%         MATRIX package and CHANGEVAR is loaded.                     ;
%*********************************************************************;
algebraic;
%*********************************************************************;
% ON DISPJACOBIAN;   % To get the Jacobians printed, remove the...    ;
%                      ... percentage sign before the word ON         ;
%*********************************************************************;
%                                                                     ;
%               *** First test problem ***                            ;
%                                                                     ;
% Here are two  Euler type of differential equations,                 ;
%                                                                     ;
%         3             2                                             ;
%      2 x  y'''  +  3 x  y''  -  y  =  0                             ;
%                                                                     ;
%                                                                     ;
%          2                                                          ;
%      5 x  y''  -   x y' + 7 y  =  0                                 ;
%                                                                     ;
%                                                                     ;
% An Euler equation can be converted into a (linear) equation with    ;
% constant coefficients by making change of independent variable:     ;
%                                                                     ;
%                         u                                           ;
%                    x = e                                            ;
%                                                                     ;
% The  resulting equations will be                                    ;
%                                                                     ;
%                                                                     ;
%      2 y'''  -  3 y''  +  y' -  y  =  0                             ;
%                                                                     ;
% and                                                                 ;
%                                                                     ;
%      5  y''  -  6 y' + 7 y  =  0                                    ;
%                                                                     ;
%                                                                     ;
% Where, now (prime) denotes differentiation with respect to the new  ;
% independent variable: u                                             ;
% How this change of variable is done using CHANGEVAR follows.        ;
%                                                                     ;
%*********************************************************************;

operator y;

changevar(y, u, x=e**u, { 2*x**3*df(y(x),x,3)+3*x**2*df(y(x),x,2)-y(x),
                          5*x**2*df(y(x),x,2)-x*df(y(x),x)+7*y(x) } ) ;

%*********************************************************************;
%               *** Second test problem ***                           ;
%                                                                     ;
% Now, the problem is to obtain the polar coordinate form of Laplace's;
% equation:                                                           ;
%                                                                     ;
%          2              2                                           ;
%         d  u           d  u                                         ;
%        ------    +    ------    =    0                              ;
%            2              2                                         ;
%         d x            d y                                          ;
%                                                                     ;
% (The differentiations are partial)                                  ;
%                                                                     ;
% For polar coordinates the change of variables are :                 ;
%                                                                     ;
%        x = r cos(theta) ,    y = r sin(theta)                       ;
%                                                                     ;
% As known, the result is :                                           ;
%                                                                     ;
%                                                                     ;
%          2                                     2                    ;
%         d  u         1    d  u         1      d  u                  ;
%        ------   +   ---  ------   +   ---  ----------   =   0       ;
%            2         r    d r           2          2                ;
%         d r                            r    d theta                 ;
%                                                                     ;
% How this change of variable is done using CHANGEVAR follows.        ;
%                                                                     ;
%                              2       2                              ;
% (To get rid of the boring sin  + cos   terms we introduce a LET     ;
%  statement)                                                         ;
%                                                                     ;
%*********************************************************************;

operator u;

let sin theta**2 = 1 - cos theta**2 ;

changevar(u, { r , theta }, { x=r*cos theta, y=r*sin theta },
          df(u(x,y),x,2)+df(u(x,y),y,2) ) ;

end;  % End of test programs for CHANGEVAR ;
