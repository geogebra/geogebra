load_package sum;

on rounded;

sum(x**0.5,x,1,2);

operator f;

symbolic;

% The following forms shpuld both evaluate to nil

freeof!-df(aeval '(df (f u v) u v),'v);
freeof!-df(reval '(df (f u v) u v),'v);

end;
