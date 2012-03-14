% Author: Alan Barnes <barnesa@aston.ac.uk>

psexplim 8;
% expand as far as 8th power (default is 6)

cos!-series:=ps(cos x,x,0);
sin!-series:=ps(sin x,x,0);
atan!-series:=ps(atan x,x,0);
tan!-series:=ps(tan x,x,0);

cos!-series*tan!-series;        % should series for sin(x)
df(cos!-series,x);              % series for sin(x) again

cos!-series/atan!-series;       % should be expanded


tmp:=ps(1/(1+x^2),x,infinity);
df(tmp,x);
ps(df(1/(1+x^2),x),x,infinity);

tmp*x;  % not expanded as a single power series
ps(tmp*x,x,infinity);   % now expanded

ps(1/(a*x-b*x^2),x,a/b);   % pole at expansion point

ps(cos!-series*x,x,2);

tmp:=ps(x/atan!-series,x,0);
tmp1:=ps(atan!-series/x,x,0);
tmp*tmp1;               % should be 1, of course


cos!-sin!-series:=ps(cos sin!-series,x,0);
% cos(sin(x))
tmp:=cos!-sin!-series^2;
tmp1:=ps((sin(sin!-series))^2,x,0);
tmp+tmp1;               % sin^2 + cos^2
psfunction tmp1;
% function represented by power series tmp1

tmp:=tan!-series^2;
psdepvar tmp;
% in case we have forgotten the dependent variable
psexpansionpt tmp;      % .... or the expansion point
psterm(tmp,6);  % select 6th term
psterm(tmp,10); % select 10th term (series extended automtically)

tmp1:=ps(1/(cos x)^2,x,0);
tmp1-tmp;       % sec^2-tan^2

ps(int(e^(x^2),x),x,0); % integrator not called
tmp:=ps(1/(y+x),x,0);
ps(int(tmp,y),x,0);     % integrator called on each coefficient

pscompose(cos!-series,sin!-series);
% power series composition cos(sin(x)) again
cos!-sin!-series;
% should be same as previous result
psfunction cos!-sin!-series;

tmp:=ps(log x,x,1);
tmp1:=pscompose(tmp, cos!-series);
% power series composition of log(cos(x))
df(tmp1,x);     % series for -tan x

psreverse tan!-series;
% should be series for atan x
atan!-series;
tmp:=ps(e^x,x,0);
psreverse tmp;
% NB expansion of log x  in powers of (x-1)

pschangevar(tan!-series,y);

end;
