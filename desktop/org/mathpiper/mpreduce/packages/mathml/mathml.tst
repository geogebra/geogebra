on mathml; % output

sin (x);

(x + y)^5;

off mathml;


parseml();<math> 
<apply><plus/> 
<cn>3</cn> 
<cn>5</cn> 
</apply> 
</math> 

operator gt;

 % MathML 1.x form
parseml();<math>
<reln><gt/>
<ci>x</ci>
<ci>y</ci>
</reln>
</math>

 % MathML 2.x form
parseml();<math>
<apply><gt/>
<ci>x</ci>
<ci>y</ci>
</apply>
</math>

end;
