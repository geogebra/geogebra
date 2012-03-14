load mathmlom;

%in "$reduce/packages/mathml/examples.mml";

%  Description: This file contains a long list of examples demonstrating the abilities of
%               the translator. Most of these examples come straight from the MathML spec. They
%               were used during the development of the interface and should all be correctly
%               translated into OpenMath.
%
%  Version 17 April 2000
%
%  Author: Luis Alvarez Sobreviela
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%       


mml2om();
<math>
  <apply><sin/>
     <apply><plus/>
       <apply><cos/>
         <ci> x </ci>
       </apply>
       <apply><power/>
         <ci> x </ci>
         <cn> 3 </cn>
       </apply>
     </apply>
   </apply>
</math>            


mml2om();
<math>
  <apply><sin/>
     <apply><plus/>
       <apply><cos/>
         <ci> x </ci>
       </apply>
       <apply><power/>
         <ci type="real"> x </ci>
         <cn> 3 </cn>
       </apply>
     </apply>
   </apply>
</math>      


mml2om();
<math>
  <set type=normal>
     <ci> b </ci>
     <cn> 2 </cn>
     <ci> c </ci>
   </set>
</math>


mml2om();
<math>
  <set type="multiset">
     <ci> b </ci>
     <cn> 2 </cn>
     <ci> c </ci>
   </set>
</math>


mml2om();
<math>
  <vector>
     <ci> b </ci>
     <cn> 2 </cn>
     <ci> c </ci>
   </vector>
</math>

mml2om();
<math>
  <interval closure=closed>
     <ci> b </ci>
     <cn> 2 </cn>
   </interval>
</math>

mml2om();
<math>
  <interval closure=open>
     <ci> b </ci>
     <cn> 2 </cn>
   </interval>
</math>

mml2om();
<math>
  <interval closure=open-closed>
     <ci> b </ci>
     <cn> 2 </cn>
   </interval>
</math>


mml2om();
<math>
  <interval closure=closed-open>
     <ci> b </ci>
     <cn> 2 </cn>
   </interval>
</math>


mml2om();
<math>
   <cn type="complex-cartesian"> 6 <sep/> 3 </cn>
</math>

mml2om();
<math>
   <cn type="complex-polar"> 6 <sep/> 3 </cn>
</math>

mml2om();
<math>
   <cn type="integer" base="10"> 6 </cn>
</math>

mml2om();
<math>
  <apply><sum/>
     <bvar>
       <ci> x </ci>
     </bvar>
     <lowlimit>
       <ci> a </ci>
     </lowlimit>
     <uplimit>
       <ci> b </ci>
     </uplimit>
      <apply><plus/>
         <ci> x </ci>
         <apply><sin/>
            <ci> y </ci>
         </apply>
      </apply>
   </apply>
</math>


              
mml2om();
<math>
  <apply><int/>
     <bvar>
       <ci> x </ci>
     </bvar>
     <lowlimit>
       <ci> a </ci>
     </lowlimit>
     <uplimit>
       <ci> b </ci>
     </uplimit>
     <apply><fn><ci> f </ci></fn>
       <ci> x </ci>
     </apply>
   </apply>
</math>  



mml2om();
<math>
   <lambda>
      <bvar>
         <ci> x </ci>
      </bvar>
      <apply><sin/>
         <ci> x </ci>
      </apply>
   </lambda>
</math>          
    

mml2om();
<math>
 <apply><limit/>
     <bvar>
       <ci> x </ci>
     </bvar>
     <lowlimit>
       <cn> 0 </cn>
     </lowlimit>
     <apply><sin/>
       <ci> x </ci>
     </apply>
   </apply>
</math>
 
mml2om();            
<math>
  <apply><limit/>
    <bvar>
      <ci> x </ci>
    </bvar>
    <condition>
      <apply>
        <tendsto type="above"/>
        <ci> x </ci>
        <ci> a </ci>
      </apply>
    </condition>
    <apply><sin/>
       <ci> x </ci>
    </apply>
  </apply>
</math>


mml2om();
<math>
   <apply><not/>
      <apply><exists/>
         <bvar>
            <ci> x </ci>
         </bvar>
         <bvar>
            <ci> y </ci>
         </bvar>
         <bvar>
            <ci> z </ci>
         </bvar>
         <bvar>
            <ci> n </ci>
         </bvar>
         <apply><and/>
            <apply><gt/>
               <ci> n </ci>
               <cn type="integer"> 2 </cn>
            </apply>
            <apply><eq/>
               <apply><plus/>
                  <apply><power/>
                     <ci> x </ci>
                     <ci> n </ci>
                  </apply>
                  <apply><power/>
                     <ci> y </ci>
                     <ci> n </ci>
                  </apply>
               </apply>
               <apply><power/>
                  <ci> z </ci>
                  <ci> n </ci>
               </apply>
            </apply>
         </apply>
      </apply>
   </apply>
</math>      



mml2om();
<math>
  <matrix>
     <matrixrow>
       <cn> 0 </cn> <cn> 1 </cn> <cn> 0 </cn>
     </matrixrow>
     <matrixrow>
       <cn> 0 </cn> <cn> 0 </cn> <cn> 1 </cn>
     </matrixrow>
     <matrixrow>
       <cn> 1 </cn> <cn> 0 </cn> <cn> 0 </cn>
     </matrixrow>
   </matrix>
</math>

mml2om();
<math>
   <apply><int/>
      <bvar>
         <ci>x</ci>
      </bvar>
      <apply><power/>
         <ci>x</ci>
         <cn type="integer">2</cn>
      </apply>
   </apply>
</math>
     
mml2om();
<math>
   <apply><int/>
      <bvar>
         <ci> x </ci>
      </bvar>
      <apply><sin/>
         <ci> x </ci>
      </apply>
   </apply>
</math>                    


mml2om();
<math>
<apply><sum/>
     <bvar>
       <ci> x </ci>
     </bvar>
     <lowlimit>
       <ci> a </ci>
     </lowlimit>
     <uplimit>
       <ci> b </ci>
     </uplimit>
     <apply><fn><ci> f </ci></fn>
       <ci> x </ci>
     </apply>
   </apply>
</math>
                 


mml2om();
<math>
  <apply><diff/>
    <bvar>
      <ci> x </ci>
   </bvar>
    <apply><fn><ci>f</ci></fn>
       <ci> x </ci>
    </apply>
  </apply>
</math>
 

mml2om();
<math>
  <apply><diff/>
    <bvar>
      <ci> x </ci>
      <degree> 
        <cn> 2 </cn>
      </degree> 
   </bvar>
    <apply><fn><ci>f</ci></fn>
       <ci> x </ci>
    </apply>
  </apply>
</math>
 

mml2om();
<math>
  <apply><diff/>
    <bvar>
      <ci> x </ci>
      <degree> 
        <cn> 3 </cn>
      </degree> 
   </bvar>
    <apply><fn><ci>f</ci></fn>
       <ci> x </ci>
    </apply>
  </apply>
</math>
 



mml2om();
<math>
  <set type=normal>
     <ci> b </ci>
     <ci> a </ci>
     <ci> c </ci>
  </set>
</math>

mml2om();
<math>
   <list>
     <ci> b </ci>
     <ci> a </ci>
     <ci> c </ci>
   </list>
</math>

mml2om();
<math>
<list order="lexicographic">
     <ci> b </ci>
     <ci> a </ci>
     <ci> c </ci>
   </list>
</math>

mml2om();
<math>
<apply><union definitionurl="www.nag.co.uk"/>
     <ci type="set"> A </ci>
     <ci type="set"> B </ci>
   </apply>
</math>

mml2om();
<math>
<apply><union/>
  <set type="normal">
     <ci> b </ci>
     <cn> 2 </cn>
     <ci> c </ci>
   </set>
   <set>
     <ci> b </ci>
     <ci> r </ci>
     <cn> 2 </cn>
     <cn> 4 </cn>
     <ci> c </ci>
   </set>
   </apply>
</math>

mml2om();
<math>
<apply><intersect definitionurl="www.mit.edu"/>
     <ci type="set"> A </ci>
     <ci type="set"> B </ci>
   </apply>
</math>

mml2om();
<math>
<apply><intersect/>
  <set>
     <ci> b </ci>
     <cn> 2 </cn>
     <ci> c </ci>
   </set>
   <set>
     <ci> b </ci>
     <ci> r </ci>
     <cn> 2 </cn>
     <cn> 4 </cn>
     <ci> c </ci>
   </set>
 </apply>
</math>


mml2om();
<math>
<reln><in definitionurl="www.www.www"/>
     <ci> a </ci>
     <ci type="set"> A </ci>
   </reln>
</math>

mml2om();
<math>
 <reln><notin definitionurl="www.www.www"/>
   <ci> a </ci>
   <ci> A </ci>
 </reln>
</math>

mml2om();
<math>
<reln><prsubset definitionurl="www.www.www"/>
     <ci> A </ci>
     <ci> B </ci>
   </reln>
</math>

mml2om();
<math>
<reln><notsubset definitionurl="www.www.www"/>
     <ci> A </ci>
     <ci> B </ci>
   </reln>
</math>

mml2om();
<math>
<reln><notprsubset definitionurl="www.www.www"/>
     <ci> A </ci>
     <ci> B </ci>
   </reln>
</math>

mml2om();
<math>
<apply><setdiff definitionurl="www.www.www"/>
     <ci> A </ci>
     <ci> B </ci>
   </apply>
</math>

mml2om();
<math>
<apply><sum/>
     <bvar>
       <ci> x </ci>
     </bvar>
     <lowlimit>
       <ci> a </ci>
     </lowlimit>
     <uplimit>
       <ci> b </ci>
     </uplimit>
     <apply><fn><ci> f </ci></fn>
       <ci> x </ci>
     </apply>
   </apply>
</math>



mml2om();
<math>
<apply><product/>
     <bvar>
       <ci> x </ci>
     </bvar>
     <lowlimit>
       <ci> a </ci>
     </lowlimit>
     <uplimit>
       <ci> b </ci>
     </uplimit>
     <apply><fn><ci> f </ci></fn>
       <ci> x </ci>
     </apply>
   </apply>
</math>

mml2om();
<math>
 <apply><limit/>
   <bvar>
     <ci> V </ci>
   </bvar>
   <condition>
     <apply>
       <tendsto type=above/>
       <ci> V </ci>
       <cn> 0 </cn>
     </apply>
   </condition>
   <apply><divide/>
     <apply><int/>
       <bvar>
         <ci> S</ci>
       </bvar>
       <ci> a </ci>
     </apply>
     <ci> V </ci>
   </apply>
 </apply>
</math>               

mml2om();
<math>
<apply><limit/>
     <bvar>
       <ci> x </ci>
     </bvar>
     <lowlimit>
       <cn> 0 </cn>
     </lowlimit>
     <apply><sin/>
       <ci> x </ci>
     </apply>
   </apply>
</math>

mml2om();
<math>
<apply><limit/>
     <bvar>
       <ci> x </ci>
     </bvar>
     <condition>
       <reln>
         <tendsto type="above"/>
         <ci> x </ci>
         <ci> a </ci>
       </reln>
     </condition>
     <apply><sin/>
        <ci> x </ci>
     </apply>
   </apply>
</math>


mml2om();
<math>
<apply><sin/>
     <apply><plus/>
       <apply><cos/>
         <ci> x </ci>
       </apply>
       <apply><power/>
         <ci> x </ci>
         <cn> 3 </cn>
       </apply>
     </apply>
   </apply>
</math>

mml2om();
<math>
<apply><mean/>
     <ci> b </ci>
     <ci> r </ci>
     <cn> 2 </cn>
     <cn> 4 </cn>
     <ci> c </ci>
   </apply>
</math>

mml2om();
<math>
<apply><sdev/>
     <ci> b </ci>
     <ci> r </ci>
     <cn> 2 </cn>
     <cn> 4 </cn>
     <ci> c </ci>
   </apply>
</math>

mml2om();
<math>
 <apply><var/>
     <ci> b </ci>
     <ci> r </ci>
     <cn> 2 </cn>
     <cn> 4 </cn>
     <ci> c </ci>
 </apply>
</math>


mml2om();
<math>
<vector>
     <cn> 1 </cn>
     <cn> 2 </cn>
     <cn> 3 </cn>
     <ci> x </ci>
   </vector>
</math>

mml2om();
<math>
<matrix>
     <matrixrow>
       <cn> 0 </cn> <cn> 1 </cn> <cn> 0 </cn>
     </matrixrow>
     <matrixrow>
       <cn> 0 </cn> <cn> 0 </cn> <cn> 1 </cn>
     </matrixrow>
     <matrixrow>
       <cn> 1 </cn> <cn> 0 </cn> <cn> 0 </cn>
     </matrixrow>
   </matrix>
</math>

mml2om();
<math>
<apply><determinant/>
<matrix>
     <matrixrow>
       <cn> 3 </cn> <cn> 1 </cn> <cn> 5 </cn>
     </matrixrow>
     <matrixrow>
       <cn> 7 </cn> <cn> 0 </cn> <cn> 2 </cn>
     </matrixrow>
     <matrixrow>
       <cn> 1 </cn> <cn> 7 </cn> <cn> 8 </cn>
     </matrixrow>
   </matrix>
</apply>
</math>

mml2om();
<math>
<apply><transpose/>
<matrix>
     <matrixrow>
       <cn> 3 </cn> <cn> 1 </cn> <cn> 5 </cn>
     </matrixrow>
     <matrixrow>
       <cn> 7 </cn> <cn> 0 </cn> <cn> 2 </cn>
     </matrixrow>
     <matrixrow>
       <cn> 1 </cn> <cn> 7 </cn> <cn> 8 </cn>
     </matrixrow>
   </matrix>
</apply>
</math>

mml2om();
<math>
<apply><selector/>
     <matrix>
       <matrixrow>
         <cn> 1 </cn> <cn> 2 </cn>
       </matrixrow>
       <matrixrow>
         <cn> 3 </cn> <cn> 4 </cn>
       </matrixrow>
     </matrix>
     <cn> 1 </cn>
   </apply>
</math>

mml2om();
<math>
<apply><select/>
     <matrix>
       <matrixrow>
         <cn> 1 </cn> <cn> 2 </cn>
       </matrixrow>
       <matrixrow>
         <cn> 3 </cn> <cn> 4 </cn>
       </matrixrow>
     </matrix>
     <cn> 2 </cn>
     <cn> 2 </cn>
   </apply>
</math>

mml2om();
<math>
<apply><determinant/>
 <matrix>
      <matrixrow>
         <ci>a</ci>
         <cn type="integer">1</cn>
      </matrixrow>
      <matrixrow>
         <cn type="integer">2</cn>
         <ci>s</ci>
      </matrixrow>
   </matrix>
</apply>
</math>


mml2om();
<math>
 <apply><determinant/>
  <apply><transpose/>
   <matrix>
      <matrixrow>
         <cn type="integer">1</cn>
         <cn type="integer">2</cn>
         <cn type="integer">3</cn>
         <cn type="integer">4</cn>
      </matrixrow>
      <matrixrow>
         <cn type="integer">1</cn>
         <cn type="integer">2</cn>
         <cn type="integer">1</cn>
         <cn type="integer">2</cn>
      </matrixrow>
      <matrixrow>
         <cn type="integer">2</cn>
         <cn type="integer">3</cn>
         <cn type="integer">2</cn>
         <cn type="integer">1</cn>
      </matrixrow>
      <matrixrow>
         <cn type="integer">2</cn>
         <cn type="integer">1</cn>
         <cn type="integer">1</cn>
         <cn type="integer">1</cn>
      </matrixrow>
   </matrix>
  </apply>
 </apply>
</math>


mml2om();
<math>
   <apply><plus/>
      <apply><times/>
         <cn type="integer">2</cn>
         <apply><cos/>
            <ci>x</ci>
         </apply>
         <ci>x</ci>
      </apply>
      <apply><minus/>
         <apply><times/>
            <apply><sin/>
               <ci>x</ci>
            </apply>
            <apply><power/>
               <ci>x</ci>
               <cn type="integer">2</cn>
            </apply>
         </apply>
      </apply>
   </apply>
</math>


mml2om();
<math>
   <list>
      <reln><eq/>
         <ci>x</ci>
         <apply><plus/>
            <cn type="constant">&ImaginaryI;</cn>
            <apply><minus/>
               <cn type="integer">1</cn>
            </apply>
         </apply>
      </reln>
      <reln><eq/>
         <ci>x</ci>
         <apply><plus/>
            <apply><minus/>
               <cn type="constant">&ImaginaryI;</cn>
            </apply>
            <apply><minus/>
               <cn type="integer">1</cn>
            </apply>
         </apply>
      </reln>
   </list>
</math>



mml2om();
<math>
   <apply><plus/>
      <apply><minus/>
         <apply><times/>
            <apply><cos/>
               <apply><times/>
                  <ci>x</ci>
                  <ci>y</ci>
               </apply>
            </apply>
            <ci>x</ci>
            <ci>y</ci>
         </apply>
      </apply>
      <apply><times/>
         <apply><power/>
            <cn type="integer">2</cn>
            <apply><times/>
               <ci>x</ci>
               <ci>y</ci>
            </apply>
         </apply>
         <apply><power/>
            <apply><log/>
               <cn type="integer">2</cn>
            </apply>
            <cn type="integer">2</cn>
         </apply>
         <ci>x</ci>
         <ci>y</ci>
      </apply>
      <apply><times/>
         <apply><power/>
            <cn type="integer">2</cn>
            <apply><times/>
               <ci>x</ci>
               <ci>y</ci>
            </apply>
         </apply>
         <apply><log/>
            <cn type="integer">2</cn>
         </apply>
      </apply>
      <apply><minus/>
         <apply><sin/>
            <apply><times/>
               <ci>x</ci>
               <ci>y</ci>
            </apply>
         </apply>
      </apply>
      <cn type="integer">1</cn>
   </apply>
</math>


mml2om();
<math>
 <reln><eq/>
  <cn>2</cn>
  <cn>2</cn>
  <cn>2</cn>
 </reln>
</math>

mml2om();
<math>
 <reln><eq/>
  <cn>2</cn>
  <ci>A</ci>
  <ci>u</ci>
 </reln>
</math>

mml2om();
<math>
 <reln><neq/>
  <cn>2</cn>
  <cn>2</cn>
 </reln>
</math>

mml2om();
<math>
 <reln><neq/>
  <cn>2</cn>
  <ci>A</ci>
 </reln>
</math>

mml2om();
<math>
 <reln><lt/>
  <cn>2</cn>
  <cn>2</cn>
  <cn>2</cn>
 </reln>
</math>

mml2om();
<math>
 <reln><lt/>
  <cn>2</cn>
  <ci>A</ci>
  <ci>u</ci>
 </reln>
</math>

mml2om();
<math>
 <reln><gt/>
  <cn>2</cn>
  <cn>2</cn>
  <cn>2</cn>
 </reln>
</math>

mml2om();
<math>
 <reln><gt/>
  <cn>2</cn>
  <ci>A</ci>
  <ci>u</ci>
 </reln>
</math>

mml2om();
<math>
 <reln><geq/>
  <cn>2</cn>
  <cn>2</cn>
  <cn>2</cn>
 </reln>
</math>

mml2om();
<math>
 <reln><geq/>
  <cn>2</cn>
  <ci>A</ci>
  <ci>u</ci>
 </reln>
</math>

mml2om();
<math>
 <reln><leq/>
  <cn>2</cn>
  <cn>2</cn>
  <cn>2</cn>
 </reln>
</math>

mml2om();
<math>
 <reln><leq/>
  <cn>2</cn>
  <ci>A</ci>
  <ci>u</ci>
 </reln>
</math>

%The following examples work perfectly when read
%in by mml2om() and prove that the tags employed
%work correctly. The ir output can then be used
%to see if the mathml produced works:

mml2om();
<math>
   <apply><int/>
      <bvar>
         <ci>x</ci>
      </bvar>
      <lowlimit>
         <cn type="integer">0</cn>
      </lowlimit>
      <uplimit>
         <cn type="integer">1</cn>
      </uplimit>
      <apply><power/>
         <ci>x</ci>
         <cn type="integer">2</cn>
      </apply>
   </apply>
</math>           


mml2om();
<math>
   <apply><int/>
      <bvar>
         <ci>x</ci>
      </bvar>
      <lowlimit>
         <cn type="integer">1</cn>
      </lowlimit>
      <uplimit>
         <cn type="constant">&infin;</cn>
      </uplimit>
      <ci>x</ci>
   </apply>
</math>       

mml2om();
<math>       
 <apply><int/>
   <bvar>
     <ci> x </ci>
   </bvar>
   <interval>
      <ci> a </ci>
      <ci> b </ci>
    </interval>
   <apply><cos/>
     <ci> x </ci>
   </apply>
 </apply>
</math>       

%this example is MathML1.0 and when passed
%through function mml2om() it translates it to
%MathML2.0

mml2om();
<math>
  <apply><diff/>
    <bvar>
      <ci> x </ci>
      <degree>
        <cn> 2 </cn>
      </degree>
    </bvar>
    <apply><fn><ci>f</ci></fn>
       <ci> x </ci>
    </apply>
  </apply>
</math>             



mml2om();
<math>
  <list>
    <apply><plus/>
       <ci> x </ci>
       <ci> y </ci>
    </apply>
    <cn> 3 </cn>
    <cn> 7 </cn>
  </list>
</math>       

mml2om();
<math>       
  <interval closure="open-closed">
    <ci> a </ci>
    <ci> b </ci>
  </interval>
</math>       

mml2om();
<math>
   <interval>
     <ci> a </ci>
     <ci> b </ci>
   </interval>
</math>

mml2om();
<math>
   <list>
      <list>
         <reln><eq/>
            <ci>x</ci>
            <apply>
               <csymbol definitionURL="..." encoding="...">
                  <ci>root_of</ci>
               </csymbol>
               <apply><plus/>
                  <apply><minus/>
                     <apply><power/>
                        <ci>y</ci>
                        <ci>x_</ci>
                     </apply>
                  </apply>
                  <apply><minus/>
                     <apply><times/>
                        <apply><int/>
                           <bvar>
                              <ci>x_</ci>
                           </bvar>
                           <apply><power/>
                              <ci>x_</ci>
                              <ci>x_</ci>
                           </apply>
                        </apply>
                        <ci>y</ci>
                     </apply>
                  </apply>
                  <ci>x_</ci>
                  <ci>y</ci>
               </apply>
               <ci>x_</ci>
               <ci>tag_1</ci>
            </apply>
         </reln>
         <reln><eq/>
            <ci>a</ci>
            <apply><plus/>
               <ci>x</ci>
               <ci>y</ci>
            </apply>
         </reln>
      </list>
   </list>
</math>          
mml2om();
<math>
   <list>
      <list>
         <reln><eq/>
            <ci>x</ci>
            <apply>
               <csymbol definitionURL="..." encoding="...">
                  <ci>root_of</ci>
               </csymbol>
               <apply><plus/>
                  <apply><times/>
                     <apply><exp/>
                        <apply><plus/>
                           <cn type="constant">&ImaginaryI;</cn>
                           <ci>x_</ci>
                        </apply>
                     </apply>
                     <ci>y</ci>
                  </apply>
                  <apply><exp/>
                     <apply><plus/>
                        <cn type="constant">&ImaginaryI;</cn>
                        <ci>x_</ci>
                     </apply>
                  </apply>
                  <apply><power/>
                     <ci>x_</ci>
                     <apply><plus/>
                        <ci>y</ci>
                        <cn type="integer">1</cn>
                     </apply>
                  </apply>
                  <apply><times/>
                     <apply><int/>
                        <bvar>
                           <ci>x_</ci>
                        </bvar>
                        <apply><power/>
                           <ci>x_</ci>
                           <ci>x_</ci>
                        </apply>
                     </apply>
                     <apply><power/>
                        <ci>y</ci>
                        <cn type="integer">2</cn>
                     </apply>
                  </apply>
                  <apply><times/>
                     <apply><int/>
                        <bvar>
                           <ci>x_</ci>                  
                        </bvar>
                        <apply><power/>
                           <ci>x_</ci>
                           <ci>x_</ci>
                        </apply>
                     </apply>
                     <ci>y</ci>
                  </apply>
               </apply>
               <ci>x_</ci>
               <ci>tag_2</ci>
            </apply>
         </reln>
         <reln><eq/>
            <ci>z</ci>
            <ci>y</ci>
         </reln>
      </list>
   </list>
</math>                   


mml2om();
<math>
  <apply><curl/>
  <vector>
     <ci> b </ci>
     <cn> 2 </cn>
     <ci> c </ci>
   </vector>
  </apply>
</math>       

mml2om();
<math>
  <apply><divergence/>
  <vector>
     <ci> b </ci>
     <cn> 2 </cn>
     <ci> c </ci>
   </vector>
  </apply>
</math>       

mml2om();
<math>
  <apply><laplacian/>
  <vector>
     <ci> b </ci>
     <cn> 2 </cn>
     <ci> c </ci>
   </vector>
  </apply>
</math>       

mml2om();
<math>
   <apply><forall/>
      <bvar>
         <ci> a </ci>
      </bvar>
      <apply><eq/>
         <apply><inverse/>
            <apply><inverse/>
               <ci> a </ci>
            </apply>
         </apply>
         <ci> a </ci>
      </apply>
   </apply>
</math>            


%in "$reduce/packages/mathml/examples.om";

%  Description: This file contains a long list of examples demonstrating the abilities of
%               the translator. Most of these examples come straight from the CDs. They
%		were used during the development of the interface and should all be correctly
%		translated into MathML.
%
%  Version 17 April 2000
%
%  Author: Luis Alvarez Sobreviela
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="arith1" name="plus"/>
      <OMV name=f/>
      <OMV name=d/>
      <OMA>
         <OMS cd="arith1" name="plus"/>
          <OMI>1</OMI>
          <OMF dec=1e10/>
      </OMA>
  </OMA>
</OMOBJ>       

om2mml();
<OMOBJ>
  <OMBIND>
  <OMS cd=fns1 name=lambda/>
  <OMBVAR>
    <OMV name=x/>
  </OMBVAR>
  <OMA>
    <OMS cd="transc1" name=sin/>
    <OMV name=x/>
  </OMA>
  </OMBIND>
</OMOBJ>       

om2mml();
<OMOBJ>
  <OMBIND>
  <OMS cd=fns1 name=lambda/>
  <OMBVAR>
    <OMV name=x/>
    <OMV name=y/>
  </OMBVAR>
  <OMA>
    <OMS cd="arith1" name=plus/>
    <OMV name=x/>
    <OMA>
      <OMS cd="transc1" name=sin/>
      <OMV name=y/>
    </OMA>
  </OMA>
  </OMBIND>
</OMOBJ>       

om2mml();
<OMOBJ>       
  <OMA>
    <OMS cd="arith1" name=plus/>
    <OMV name=x/>
    <OMA>
      <OMS cd="transc1" name=sin/>
      <OMV name=x/>
    </OMA>
  </OMA>
</OMOBJ>       

om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="x"/>
    </OMBVAR>
    <OMA>
      <OMS cd="relation1" name="leq"/>
      <OMA>
        <OMS cd="arith1" name="abs"/>
        <OMA>
          <OMS cd="transc1" name="sin"/>
          <OMV name="x"/>
        </OMA>
      </OMA>
      <OMF dec="1.0"/>
    </OMA>
  </OMBIND>
</OMOBJ>      

om2mml();
<OMOBJ>
 <OMA>
   <OMS cd="logic1" name="not"/>
   <OMBIND>
     <OMS cd="quant1" name="exists"/>
     <OMBVAR>
       <OMV name="x"/>
       <OMV name="y"/>
       <OMV name="z"/>
       <OMV name="n"/>
     </OMBVAR>
     <OMA>
       <OMS cd="logic1" name="and"/>
       <OMA>
         <OMS cd="relation1" name="gt"/>
         <OMV name="n"/>
         <OMI> 2 </OMI>
       </OMA>
       <OMA>
         <OMS cd="relation1" name="eq"/>
         <OMA>
           <OMS cd="arith1" name="plus"/>
           <OMA>
              <OMS cd="arith1" name="power"/>
              <OMV name="x"/>
              <OMV name="n"/>
           </OMA>
           <OMA>
              <OMS cd="arith1" name="power"/>
              <OMV name="y"/>
              <OMV name="n"/>
           </OMA>
         </OMA>
         <OMA>
            <OMS cd="arith1" name="power"/>
            <OMV name="z"/>
            <OMV name="n"/>
         </OMA>
       </OMA>
     </OMA>
   </OMBIND>
 </OMA>
</OMOBJ>


% The following two examples show how the translator
% can deal with matrices represented either in columns
% or rows. The translator then converts matrices
% represented in columns into ones represented in
% rows. Mapping to MathML is then possible.

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="linalg2" name="matrix"/>
      <OMA>
        <OMS cd="linalg2" name="matrixcolumn"/>
        <OMI> 1 </OMI>
        <OMI> 2 </OMI>
      </OMA>
      <OMA>
        <OMS cd="linalg2" name="matrixcolumn"/>
        <OMI> 3 </OMI>
        <OMI> 4 </OMI>
      </OMA>
      <OMA>
        <OMS cd="linalg2" name="matrixcolumn"/>
        <OMI> 5 </OMI>
        <OMI> 6 </OMI>
      </OMA>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="linalg2" name="matrix"/>
      <OMA>
        <OMS cd="linalg2" name="matrixrow"/>
        <OMI> 1 </OMI>
        <OMI> 0 </OMI>
      </OMA>
      <OMA>
        <OMS cd="linalg2" name="matrixrow"/>
        <OMI> 0 </OMI>
        <OMI> 1 </OMI>
      </OMA>
  </OMA>
</OMOBJ>     


om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="M"/>
    </OMBVAR>
    <OMA>
      <OMS cd="logic1" name="and"/>
      <OMA>
        <OMS cd="relation1" name="eq"/>
        <OMA>
          <OMS cd="arith1" name="times"/>
          <OMA>
            <OMS cd="linalg3" name="identity"/>
            <OMA>
              <OMS cd="linalg3" name="rowcount"/>
              <OMV name="M"/>
            </OMA>
          </OMA>
          <OMV name="M"/>
        </OMA>
        <OMV name="M"/>
      </OMA>
      <OMA>
        <OMS cd="relation1" name="eq"/>
        <OMA>
          <OMS cd="arith1" name="times"/>
          <OMV name="M"/>
          <OMA>
            <OMS cd="linalg3" name="identity"/>
            <OMA>
              <OMS cd="linalg3" name="columncount"/>
              <OMV name="M"/>
            </OMA>
          </OMA>
        </OMA>
        <OMV name="M"/>
      </OMA>
    </OMA>
  </OMBIND>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="limit1" name="limit"/>
    <OMF dec="0.0"/>
    <OMS cd="limit1" name="above"/>
    <OMBIND>
      <OMS cd="fns1" name="lambda"/>
      <OMBVAR>
        <OMV name="x"/>
      </OMBVAR>
      <OMA>
        <OMS cd="transc1" name="sin"/>
        <OMV name="x"/>
      </OMA>
    </OMBIND>
  </OMA>
</OMOBJ>
                 
% This following example will show that the translator only
% identifies the limit symbol of the limit1 CD

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="fakeCD" name="limit"/>
    <OMF dec="0.0"/>
    <OMS cd="limit1" name="above"/>
    <OMBIND>
      <OMS cd="fns1" name="lambda"/>
      <OMBVAR>
        <OMV name="x"/>
      </OMBVAR>
      <OMA>
        <OMS cd="transc1" name="sin"/>
        <OMV name="x"/>
      </OMA>
    </OMBIND>
  </OMA>
</OMOBJ>
                 
% The following two examples show how the translator
% recognizes whether a symbol has a mathml equivalent
% depending on the CD it comes from. 

% They both use symbol 'notsubset' but from different
% CDs. Only one of them can be mapped to MathML
% and the program distinguishes it by checking if 
% the CD given is the correct one on its table
% om_mml!*.

om2mml();
<OMOBJ>
 <OMA>
   <OMS cd="multiset1" name="notsubset"/>
   <OMA>
     <OMS cd="multiset1" name="set"/>
     <OMI> 2 </OMI>
     <OMI> 3 </OMI>
     <OMI> 3 </OMI>
   </OMA>
   <OMA>
     <OMS cd="multiset1" name="set"/>
       <OMI> 1 </OMI>
       <OMI> 2 </OMI>
       <OMI> 3 </OMI>
   </OMA>
  </OMA>
</OMOBJ>    


om2mml();
<OMOBJ>
 <OMA>
   <OMS cd="set1" name="notsubset"/>
   <OMA>
     <OMS cd="multiset1" name="set"/>
     <OMI> 2 </OMI>
     <OMI> 3 </OMI>
     <OMI> 3 </OMI>
   </OMA>
   <OMA>
     <OMS cd="multiset1" name="set"/>
       <OMI> 1 </OMI>
       <OMI> 2 </OMI>
       <OMI> 3 </OMI>
   </OMA>
  </OMA>
</OMOBJ>    



om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="a"/>
      <OMV name="b"/>
    </OMBVAR>
    <OMA>
      <OMS cd="relation1" name="eq"/>
      <OMA>
        <OMS cd="arith1" name="plus"/>
        <OMV name="a"/>
        <OMV name="b"/>
      </OMA>
      <OMA>
        <OMS cd="arith1" name="plus"/>
        <OMV name="b"/>
        <OMV name="a"/>
      </OMA>
    </OMA>
  </OMBIND>
</OMOBJ>

% Example of a symbol which has a MathML equivalent
% but under another name.

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="arith1" name="unary_minus"/>
        <OMI> 1 </OMI>
  </OMA>
</OMOBJ>  


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMA>
      <OMS cd="logic1" name="not"/>
      <OMS cd="logic1" name="false"/>
    </OMA>
    <OMS cd="logic1" name="true"/>
  </OMA>
</OMOBJ>    



om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMA>
      <OMS cd="arith1" name="times"/>
      <OMA>
        <OMS cd="fns1" name="identity"/>
        <OMA>
          <OMS cd="linalg3" name="rowcount"/>
          <OMV name="M"/>
        </OMA>
      </OMA>
      <OMV name="M"/>
    </OMA>
    <OMV name="M"/>
  </OMA>
</OMOBJ>
                 


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="linalg1" name="scalarproduct"/>
    <OMA>
      <OMS cd="linalg1" name="vector"/>
        <OMI> 3 </OMI>
        <OMI> 6 </OMI>
        <OMI> 9 </OMI>
    </OMA>
    <OMA>
      <OMS cd="linalg1" name="vector"/>
        <OMI> 3 </OMI>
        <OMI> 6 </OMI>
        <OMI> 9 </OMI>
    </OMA>
  </OMA>
</OMOBJ>     

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="linalg1" name="outerproduct"/>
    <OMA>
      <OMS cd="linalg1" name="vector"/>
        <OMI> 3 </OMI>
        <OMI> 6 </OMI>
        <OMI> 9 </OMI>
    </OMA>
    <OMA>
      <OMS cd="linalg1" name="vector"/>
        <OMI> 3 </OMI>
        <OMI> 6 </OMI>
        <OMI> 9 </OMI>
    </OMA>
  </OMA>
</OMOBJ>     



om2mml();
<OMOBJ>
   <OMBIND>
       <OMS cd="quant1" name="forall"/>
       <OMBVAR>
         <OMV name="a"/>
       </OMBVAR>
       <OMA>
         <OMS cd="relation1" name="eq"/>
         <OMA>
           <OMS cd="arith1" name="plus"/>
           <OMV name="a"/>
           <OMS cd="alg1" name="zero"/>
         </OMA>
         <OMV name="a"/>
      </OMA>
   </OMBIND>
</OMOBJ>  



om2mml();
<OMOBJ>
  <OMBIND>
     <OMS cd="quant1" name="forall"/>
     <OMBVAR>
       <OMV name="a"/>
     </OMBVAR>
     <OMA>
       <OMS cd="relation1" name="eq"/>
       <OMA>
         <OMS cd="arith1" name="times"/>
         <OMS cd="alg1" name="one"/>
         <OMV name="a"/>
       </OMA>
       <OMV name="a"/>
     </OMA>
   </OMBIND>
</OMOBJ>   


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMA>
      <OMS cd="bigfloat1" name="bigfloat"/>
      <OMV name="m"/>
      <OMV name="r"/>
      <OMV name="e"/>
    </OMA>
    <OMA>
      <OMS cd="arith1" name="times"/>
      <OMV name="m"/>
      <OMA>
        <OMS cd="arith1" name="power"/>
        <OMV name="r"/>
        <OMV name="e"/>
      </OMA>
    </OMA>
  </OMA>
</OMOBJ>   


% The integral symbols defint and int are ambigious as defined
% in the CDs. They do not specify their variable of integration
% explicitly. The following shows that when the function
% to integrate is defined as a lambda expression, then the
% bound variable is easily determined. However, in other
% cases, it is not possible to determine the bound variable.

om2mml();
<OMOBJ>
 <OMA>
    <OMS cd="calculus1" name="int"/>
    <OMBIND>
      <OMS cd="fns1" name="lambda"/>
      <OMBVAR>
        <OMV name="x"/>
      </OMBVAR>
      <OMA>
        <OMS cd="transc1" name="sin"/>
        <OMV name="x"/>
      </OMA>
    </OMBIND>
  </OMA>
</OMOBJ>        

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="calculus1" name="int"/>
    <OMA>
     <OMS cd="arith1" name="plus"/>
      <OMV name="x"/>
      <OMV name="y"/>
    </OMA>
  </OMA>
</OMOBJ>

% Some calculus

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMA>
      <OMS cd="calculus1" name="diff"/>
      <OMBIND>
        <OMS cd="fns1" name="lambda"/>
        <OMBVAR>
          <OMV name="x"/>
        </OMBVAR>
        <OMA>
          <OMS cd="arith1" name="plus"/>
          <OMV name="x"/>
          <OMF dec="1.0"/>
        </OMA>
      </OMBIND>
    </OMA>
    <OMF dec="1.0"/>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
       <OMA>
         <OMS cd="relation1" name="eq"/>
           <OMA>
             <OMS cd="calculus1" name="partialdiff"/>
             <OMA>
               <OMS cd="list1" name="list"/>
               <OMI> 1 </OMI>
               <OMI> 3 </OMI>
             </OMA>
             <OMBIND>
               <OMS cd="fns1" name="lambda"/>
               <OMBVAR>
                 <OMV name="x"/>
                 <OMV name="y"/>
                 <OMV name="z"/>
               </OMBVAR>
               <OMA>
                 <OMS cd="arith2" name="times"/>
                 <OMV name="x"/>
                 <OMV name="y"/>
                 <OMV name="z"/>
               </OMA>
             </OMBIND>
           </OMA>
         <OMV name="y"/>
       </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMA>
     <OMS cd="relation1" name="eq"/>
     <OMA>
       <OMS cd="integer1" name="factorial"/>
       <OMV name="n"/>
     </OMA>
     <OMA>
       <OMS cd="arith1" name="product"/>
       <OMA>
         <OMS cd="interval1" name="integer_interval"/>
         <OMI> 1 </OMI>
         <OMV name="n"/>
       </OMA>
       <OMBIND>
         <OMS cd="fns1" name="lambda"/>
         <OMBVAR>
           <OMV name="i"/>
         </OMBVAR>
         <OMV name="i"/>
      </OMBIND>
    </OMA>
  </OMA>
</OMOBJ>

                        
om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="logic1" name="not"/>
    <OMBIND>
      <OMS cd="quant1" name="exists"/>
      <OMBVAR>
        <OMV name="c"/>
      </OMBVAR>
      <OMA>
        <OMS cd="logic1" name="and"/>
        <OMA>
          <OMS cd="set1" name="in"/>
          <OMA>
            <OMS cd="arith1" name="divide"/>
            <OMV name="a"/>
            <OMV name="c"/>
          </OMA>
          <OMS cd="setname1" name="Z"/>
        </OMA>
        <OMA>
          <OMS cd="set1" name="in"/>
          <OMA>
            <OMS cd="arith1" name="divide"/>
            <OMV name="b"/>
            <OMV name="c"/>
          </OMA>
          <OMS cd="setname1" name="Z"/>
        </OMA>
        <OMA>
          <OMS cd="relation1" name="gt"/>
          <OMV name="c"/>
          <OMA>
            <OMS cd="integer1" name="gcd"/>
            <OMV name="a"/>
            <OMV name="b"/>
          </OMA>
        </OMA>
      </OMA>
    </OMBIND>
  </OMA>
</OMOBJ>





om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="x"/>
    </OMBVAR>
    <OMA>
      <OMS cd="logic1" name="implies"/>
      <OMS cd="logic1" name="false"/>
      <OMV name="x"/>
    </OMA>
  </OMBIND>
</OMOBJ>
                  


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMA>
      <OMS cd="minmax1" name="max"/>
      <OMI> 1 </OMI>
      <OMI> 9 </OMI>
      <OMI> 5 </OMI>
    </OMA>
    <OMI> 9 </OMI>
  </OMA>
</OMOBJ>

% The following examples belong to the multiset CD

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="logic1" name="implies"/>
    <OMA>
      <OMS cd="logic1" name="and"/>
      <OMA>
        <OMS cd="multiset1" name="in"/>
        <OMV name="a"/>
        <OMV name="A"/>
      </OMA>
      <OMA>
        <OMS cd="multiset1" name="in"/>
        <OMV name="a"/>
        <OMV name="B"/>
      </OMA>
    </OMA>
    <OMA>
      <OMS cd="multiset1" name="in"/>
      <OMV name="a"/>
      <OMA>
        <OMS cd="multiset1" name="intersect"/>
        <OMV name="A"/>
        <OMV name="B"/>
      </OMA>
    </OMA>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="multiset1" name="multiset"/>
    <OMI> 4 </OMI>
    <OMI> 1 </OMI>
    <OMI> 0 </OMI>
    <OMI> 1 </OMI>
    <OMI> 4 </OMI>
  </OMA>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="logic1" name="and"/>
    <OMA>
      <OMS cd="multiset1" name="subset"/>
      <OMA>
        <OMS cd="multiset1" name="intersect"/>
        <OMV name="A"/>
        <OMV name="B"/>
      </OMA>
      <OMV name="A"/>
    </OMA>
    <OMA>
      <OMS cd="multiset1" name="subset"/>
      <OMA>
        <OMS cd="multiset1" name="intersect"/>
        <OMV name="A"/>
        <OMV name="B"/>
      </OMA>
      <OMV name="B"/>
    </OMA>
  </OMA>
</OMOBJ>

     
om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="logic1" name="and"/>
    <OMA>
      <OMS cd="multiset1" name="subset"/>
      <OMV name="A"/>
      <OMA>
        <OMS cd="multiset1" name="union"/>
        <OMV name="A"/>
        <OMV name="B"/>
      </OMA>
    </OMA>
    <OMA>
      <OMS cd="multiset1" name="subset"/>
      <OMV name="B"/>
      <OMA>
        <OMS cd="multiset1" name="union"/>
        <OMV name="A"/>
        <OMV name="B"/>
      </OMA>
    </OMA>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="A"/>
      <OMV name="B"/>
      <OMV name="C"/>
    </OMBVAR>
    <OMA>
      <OMS cd="relation1" name="eq"/>
      <OMA>
        <OMS cd="multiset1" name="union"/>
        <OMV name="A"/>
        <OMA>
          <OMS cd="multiset1" name="intersect"/>
          <OMV name="B"/>
          <OMV name="C"/>
        </OMA>
      </OMA>
      <OMA>
        <OMS cd="multiset1" name="intersect"/>
        <OMA>
          <OMS cd="multiset1" name="union"/>
          <OMV name="A"/>
          <OMV name="B"/>
        </OMA>
        <OMA>
          <OMS cd="multiset1" name="union"/>
          <OMV name="A"/>
          <OMV name="C"/>
        </OMA>
      </OMA>
    </OMA>    
  </OMBIND>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="multiset1" name="subset"/>
    <OMA>
      <OMS cd="multiset1" name="setdiff"/>
      <OMV name="A"/>
      <OMV name="B"/>
    </OMA>
    <OMV name="A"/>
  </OMA>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="logic1" name="implies"/>
    <OMA>
      <OMS cd="logic1" name="and"/>
      <OMA>
        <OMS cd="multiset1" name="subset"/>
        <OMV name="B"/>
        <OMV name="A"/>
      </OMA>
      <OMA>
        <OMS cd="multiset1" name="subset"/>
        <OMV name="C"/>
        <OMV name="B"/>
      </OMA>
    </OMA>
    <OMA>
      <OMS cd="multiset1" name="subset"/>
      <OMV name="C"/>
      <OMV name="A"/>
    </OMA>
  </OMA>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="multiset1" name="notin"/>
    <OMI> 4 </OMI>
    <OMA>
      <OMS cd="multiset1" name="multiset"/>
      <OMI> 1 </OMI>
      <OMI> 1 </OMI>
      <OMI> 2 </OMI>
      <OMI> 3 </OMI>
    </OMA>
  </OMA>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="multiset1" name="prsubset"/>
    <OMA>
      <OMS cd="multiset1" name="multiset"/>
      <OMI> 2 </OMI>
      <OMI> 3 </OMI>
    </OMA>
    <OMA>
      <OMS cd="multiset1" name="multiset"/>
      <OMI> 2 </OMI>
      <OMI> 2 </OMI>
      <OMI> 3 </OMI>
    </OMA>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="multiset1" name="notsubset"/>
    <OMA>
      <OMS cd="multiset1" name="multiset"/>
      <OMI> 2 </OMI>
      <OMI> 3 </OMI>
      <OMI> 3 </OMI>
    </OMA>
    <OMA>
      <OMS cd="multiset1" name="multiset"/>
      <OMI> 1 </OMI>
      <OMI> 2 </OMI>
      <OMI> 3 </OMI>
    </OMA>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="multiset1" name="notprsubset"/>
    <OMA>
      <OMS cd="multiset1" name="multiset"/>
      <OMI> 1 </OMI>
      <OMI> 2 </OMI>
      <OMI> 1 </OMI>
    </OMA>
    <OMA>
      <OMS cd="multiset1" name="multiset"/>
      <OMI> 1 </OMI>
      <OMI> 2 </OMI>
      <OMI> 1 </OMI>
    </OMA>
  </OMA>
</OMOBJ>

% Examples from CD nums1

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMI> 8 </OMI>
    <OMA>
      <OMS cd="nums1" name="based_integer"/>
      <OMI> 8 </OMI>
      <OMSTR> 10 </OMSTR>
    </OMA>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="nums1" name="rational"/>
    <OMI> 1 </OMI>
    <OMI> 2 </OMI>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
 <OMBIND>
   <OMS cd="quant1" name="forall"/>
   <OMBVAR>
     <OMV name="x"/>        
     <OMV name="y"/>
   </OMBVAR>
   <OMA>
     <OMS cd="relation1" name="eq"/>
     <OMA>
       <OMS cd="nums1" name="complex_cartesian"/>
       <OMV name="x"/>
       <OMV name="y"/>
     </OMA>
     <OMA>
       <OMS cd="arith1" name="plus"/>
       <OMV name="x"/>
       <OMA>
         <OMS cd="arith1" name="times"/>
         <OMS cd="nums1" name="i"/>
         <OMV name="y"/>
       </OMA>
     </OMA>
   </OMA>
 </OMBIND>
</OMOBJ>


om2mml();
<OMOBJ>
<OMBIND>
  <OMS cd="quant1" name="forall"/>
  <OMBVAR>
    <OMV name="x"/>
    <OMV name="y"/>
    <OMV name="r"/>
    <OMV name="a"/>
  </OMBVAR>
  <OMA>
    <OMS cd="logic1" name="implies"/>
    <OMA>
      <OMS cd="logic1" name="and"/>
      <OMA>
        <OMS cd="relation1" name="eq"/>
        <OMA>
          <OMS cd="arith1" name="times"/>
          <OMV name="r"/>
          <OMA>
            <OMS cd="transc1" name="sin"/>
            <OMV name="a"/>
          </OMA>
        </OMA>
        <OMV name="y"/>
      </OMA>
      <OMA>
        <OMS cd="relation1" name="eq"/>
        <OMA>
          <OMS cd="arith1" name="times"/>
          <OMV name="r"/>
          <OMA>
            <OMS cd="transc1" name="cos"/>
            <OMV name="a"/>
          </OMA>
        </OMA>
        <OMV name="x"/>
      </OMA>
    </OMA>
    <OMA>
      <OMS cd="relation1" name="eq"/>
      <OMA>
        <OMS cd="nums1" name="complex_polar"/>
        <OMV name="r"/>
        <OMV name="a"/>
      </OMA>
      <OMA>
        <OMS cd="nums1" name="complex_cartesian"/>
        <OMV name="x"/>
        <OMV name="y"/>
      </OMA>
    </OMA>
  </OMA>
</OMBIND>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="x"/>
    </OMBVAR>
    <OMA>
      <OMS cd="logic1" name="implies"/>
      <OMA>
        <OMS cd="logic1" name="and"/>
        <OMA>
          <OMS cd="set1" name="in"/>
          <OMV name="a"/>
          <OMS cd="setname1" name="R"/>
        </OMA>
        <OMA>
          <OMS cd="set1" name="in"/>
          <OMV name="k"/>
          <OMS cd="setname1" name="Z"/>
        </OMA>
      </OMA>
      <OMA>
        <OMS cd="relation1" name="eq"/>
        <OMA>
          <OMS cd="nums1" name="complex_polar"/>
          <OMV name="x"/>
          <OMV name="a"/>
        </OMA>
        <OMA>
          <OMS cd="nums1" name="complex_polar"/>
          <OMV name="x"/>
          <OMA>
            <OMS cd="arith1" name="plus"/>
            <OMV name="a"/>
            <OMA>
              <OMS cd="arith1" name="times"/>
              <OMI> 2 </OMI>
              <OMS cd="nums1" name="pi"/>
              <OMV name="k"/>
            </OMA>
          </OMA>
        </OMA>
      </OMA>
    </OMA>
  </OMBIND>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMS cd="nums1" name="e"/>
    <OMA>
      <OMS cd="arith1" name="sum"/>
      <OMA>
        <OMS cd="interval1" name="integer_interval"/>
        <OMS cd="alg1" name="zero"/>
        <OMS cd="nums1" name="infinity"/>
      </OMA>
      <OMBIND>
        <OMS cd="fns1" name="lambda"/>
        <OMBVAR>
          <OMV name="j"/>
        </OMBVAR>
        <OMA>
          <OMS cd="arith1" name="divide"/>
          <OMS cd="alg1" name="one"/>
          <OMA>
            <OMS cd="integer1" name="factorial"/>
            <OMV name="j"/>
          </OMA>
        </OMA>
      </OMBIND>
    </OMA>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMA>
      <OMS cd="arith1" name="power"/>
      <OMS cd="nums1" name="i"/>
      <OMI> 2 </OMI>
    </OMA>
    <OMA>
      <OMS cd="arith1" name="minus"/>
      <OMS cd="alg1" name="one"/>
    </OMA>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="x"/>
      <OMV name="y"/>
    </OMBVAR>
    <OMA>
      <OMS cd="relation1" name="eq"/>
      <OMV name="y"/>
      <OMA>
        <OMS name="imaginary" cd="nums1"/>
        <OMA>
          <OMS name="complex_cartesian" cd="nums1"/>
          <OMV name="x"/>
          <OMV name="y"/>
        </OMA>
      </OMA>
    </OMA>
  </OMBIND>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="x"/>
      <OMV name="y"/>
    </OMBVAR>
    <OMA>
      <OMS cd="relation1" name="eq"/>
      <OMV name="x"/>
      <OMA>
        <OMS name="real" cd="nums1"/>
        <OMA>
          <OMS name="complex_cartesian" cd="nums1"/>
          <OMV name="x"/>
          <OMV name="y"/>
        </OMA>
      </OMA>
    </OMA>
  </OMBIND>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="logic1" name="implies"/>
    <OMA>
      <OMS cd="set1" name="in"/>
      <OMV name="a"/>
      <OMS cd="setname1" name="R"/>
    </OMA>
    <OMA>
      <OMS cd="relation1" name="lt"/>
      <OMV name="x"/>
      <OMS cd="nums1" name="infinity"/>
    </OMA>
  </OMA>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="neq"/>
    <OMS cd="nums1" name="NaN"/>
    <OMS cd="nums1" name="NaN"/>
  </OMA>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMS cd="nums1" name="pi"/>
    <OMA>
      <OMS cd="arith1" name="sum"/>
      <OMA>
        <OMS cd="interval1" name="integer_interval"/>
        <OMS cd="alg1" name="zero"/>
        <OMS cd="nums1" name="infinity"/>
      </OMA>
      <OMBIND>
        <OMS cd="fns1" name="lambda"/>
        <OMBVAR>
          <OMV name="j"/>
        </OMBVAR>
        <OMA>
          <OMS cd="arith1" name="minus"/>
          <OMA>
            <OMS cd="arith1" name="divide"/>
            <OMS cd="alg1" name="one"/>
            <OMA>
              <OMS cd="arith1" name="plus"/>
              <OMA>
                <OMS cd="arith1" name="times"/>
                <OMI> 4 </OMI>
                <OMV name="j"/>
              </OMA>
              <OMS cd="alg1" name="one"/>
            </OMA>
          </OMA>
          <OMA>
            <OMS cd="arith1" name="divide"/>
            <OMS cd="alg1" name="one"/>
            <OMA>
              <OMS cd="arith1" name="plus"/>
              <OMA>
                <OMS cd="arith1" name="times"/>
                <OMI> 4 </OMI>
                <OMV name="j"/>
              </OMA>
              <OMS cd="alg1" name="one"/>
            </OMA>
          </OMA>
        </OMA>
      </OMBIND>
    </OMA>
  </OMA>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="x"/>
    </OMBVAR>
    <OMA>
      <OMS cd="logic1" name="and"/>
      <OMA>
        <OMS cd="relation1" name="lt"/>
        <OMA>
          <OMS cd="arith1" name="minus"/>
          <OMA>
            <OMS cd="rounding1" name="ceiling"/>
            <OMV name="x"/>
          </OMA>
          <OMS cd="alg1" name="one"/>
        </OMA>
        <OMV name="x"/>
       </OMA>
       <OMA>
         <OMS cd="relation1" name="leq"/>
         <OMV name="x"/>
         <OMA>
           <OMS cd="rounding1" name="ceiling"/>
           <OMV name="x"/>
         </OMA>
       </OMA>
     </OMA>
  </OMBIND>
</OMOBJ>



om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMA>
      <OMS cd="stats1" name="mean"/>
      <OMI> 1 </OMI> <OMI> 2 </OMI> <OMI> 3 </OMI>
    </OMA>
    <OMI> 3 </OMI>
  </OMA>
</OMOBJ>



om2mml();
<OMOBJ>
  <OMA>
     <OMS cd="stats1" name="sdev"/>
     <OMF dec="3.1"/> 
     <OMF dec="2.2"/> 
     <OMF dec="1.8"/> 
     <OMF dec="1.1"/>
     <OMF dec="3.3"/> 
     <OMF dec="2.4"/> 
     <OMF dec="5.5"/>
     <OMF dec="2.3"/>
     <OMF dec="1.7"/> 
     <OMF dec="1.8"/> 
     <OMF dec="3.4"/>
     <OMF dec="4.0"/>
     <OMF dec="3.3"/>
   </OMA>
</OMOBJ>



om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="logic1" name="implies"/>
    <OMA>
      <OMS cd="relation1" name="eq"/>
      <OMA>
        <OMS cd="arith1" name="power"/>
        <OMV name="a"/>
        <OMV name="b"/>
      </OMA>
      <OMV name="c"/>
    </OMA>
    <OMA>
      <OMS cd="relation1" name="eq"/>
      <OMA>
        <OMS cd="transc1" name="log"/>
        <OMV name="a"/>
        <OMV name="c"/>
      </OMA>
      <OMV name="b"/>
    </OMA>
  </OMA>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS name="and" cd="logic1"/>
    <OMA>
      <OMS name="lt" cd="relation1"/>
      <OMA>
        <OMS name="unary_minus" cd="arith1"/>
        <OMS name="pi" cd="nums1"/>
      </OMA>
      <OMA>
        <OMS name="imaginary" cd="nums1"/>
        <OMA>
          <OMS name="ln" cd="transc1"/>
          <OMV name="x"/>
        </OMA>
      </OMA>
    </OMA>
    <OMA>
      <OMS name="leq" cd="relation1"/>
      <OMA>
        <OMS name="imaginary" cd="nums1"/>
        <OMA>
          <OMS name="ln" cd="transc1"/>
          <OMV name="x"/>
        </OMA>
      </OMA>
      <OMS name="pi" cd="nums1"/>
    </OMA>
  </OMA>
</OMOBJ>




om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMA>
      <OMS cd="veccalc1" name="curl"/>
      <OMV name="F"/>
    </OMA>
    <OMA>
      <OMS cd="arith1" name="plus"/>
      <OMA>
        <OMS cd="linalg1" name="vectorproduct"/>
        <OMA>
          <OMS cd="linalg1" name="vector"/>
          <OMI> 1 </OMI>
          <OMI> 0 </OMI>
          <OMI> 0 </OMI>
        </OMA>
        <OMA>
          <OMS cd="calculus1" name="partialdiff"/>
          <OMA>
            <OMS cd="list1" name="list"/>
            <OMI> 1 </OMI>
          </OMA>
          <OMV name="F"/>
        </OMA>
      </OMA>
      <OMA>
        <OMS cd="linalg1" name="vectorproduct"/>
        <OMA>
          <OMS cd="linalg1" name="vector"/>
          <OMI> 0 </OMI>
          <OMI> 1 </OMI>
          <OMI> 0 </OMI>
        </OMA>
        <OMA>
          <OMS cd="calculus1" name="partialdiff"/>
          <OMA>
            <OMS cd="list1" name="list"/>
            <OMI> 2 </OMI>
          </OMA>
          <OMV name="F"/>
        </OMA>
      </OMA>
      <OMA>
        <OMS cd="linalg1" name="vectorproduct"/>
        <OMA>
          <OMS cd="linalg1" name="vector"/>
          <OMI> 0 </OMI>
          <OMI> 0 </OMI>
          <OMI> 1 </OMI>
        </OMA>
        <OMA>
          <OMS cd="calculus1" name="partialdiff"/>
          <OMA>
            <OMS cd="list1" name="list"/>
            <OMI> 3 </OMI>
          </OMA>
          <OMV name="F"/>
        </OMA>
      </OMA>
    </OMA>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="x"/>
    </OMBVAR>
    <OMA>
      <OMS cd="logic1" name="and"/>
      <OMA>
        <OMS cd="relation1" name="lt"/>
        <OMA>
          <OMS name="unary_minus" cd="arith1"/>
          <OMS cd="nums1" name="pi"/>
        </OMA>
        <OMA>
          <OMS name="arg" cd="arith2"/>
          <OMV name="x"/>
        </OMA>
      </OMA>
      <OMA>
        <OMS cd="relation1" name="leq"/>
        <OMA>
          <OMS name="arg" cd="arith2"/>
          <OMV name="x"/>
        </OMA>
        <OMS cd="nums1" name="pi"/>
      </OMA>
    </OMA>
  </OMBIND>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="a"/>
    </OMBVAR>
    <OMA>
      <OMS cd="relation1" name="eq"/>
      <OMA>
        <OMS cd="arith2" name="inverse"/>
        <OMA>
          <OMS cd="arith2" name="inverse"/>
          <OMV name="a"/>
        </OMA>
      </OMA>
      <OMV name="a"/>
    </OMA>
 </OMBIND>
</OMOBJ>

% An example of elements which do not have a MathML 
% equivalent. This example comes from the fns1 CD

om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="n"/>
    </OMBVAR>
    <OMA>
      <OMS cd="relation1" name="eq"/>
      <OMA>
        <OMS cd="fns2" name="apply_to_list"/>
        <OMA>
          <OMS cd="arith1" name="plus"/>
          <OMA>
            <OMS cd="list1" name="make_list"/>
            <OMI> 1 </OMI>
            <OMV name="n"/>
            <OMS cd="fns1" name="identity"/>
          </OMA>
        </OMA>
      </OMA>
      <OMA>
        <OMS cd="arith1" name="divide"/>
        <OMA>
        <OMS cd="arith1" name="times"/>
          <OMV name="n"/>
          <OMA>
            <OMS cd="arith1" name="plus"/>
            <OMV name="n"/>
            <OMI> 1 </OMI>
          </OMA>
        </OMA>
        <OMI> 2 </OMI>
      </OMA>
    </OMA>
  </OMBIND>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMA>
      <OMS cd="linalg3" name="determinant"/>
      <OMA>
        <OMS cd="linalg3" name="identity"/>
        <OMV name="n"/>
      </OMA>
    </OMA>
    <OMS cd="alg1" name="one"/>
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="relation1" name="eq"/>
    <OMA>
      <OMS cd="linalg3" name="transpose"/>
      <OMA>
        <OMS cd="linalg1" name="matrix"/>
        <OMA>
          <OMS cd="linalg1" name="matrixrow"/>
          <OMI> 0 </OMI>
          <OMI> 1 </OMI>
        </OMA>
        <OMA>
          <OMS cd="linalg1" name="matrixrow"/>
          <OMI> 2 </OMI>
          <OMI> 3 </OMI>
        </OMA>
      </OMA>
    </OMA>
    <OMA>
      <OMS cd="linalg1" name="matrix"/>
      <OMA>
        <OMS cd="linalg1" name="matrixrow"/>
        <OMI> 0 </OMI>
        <OMI> 2 </OMI>
      </OMA>
      <OMA>
        <OMS cd="linalg1" name="matrixrow"/>
        <OMI> 1 </OMI>
        <OMI> 3 </OMI>
      </OMA>
    </OMA>
  </OMA>
</OMOBJ>



om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="logic2" name="equivalent"/>
    <OMA>
      <OMS cd="logic2" name="equivalent"/>
      <OMV name="A"/>
      <OMV name="B"/>
    </OMA>
    <OMA>
      <OMS cd="logic1" name="and"/>
      <OMA>
        <OMS cd="logic1" name="implies"/>
        <OMV name="A"/>
        <OMV name="B"/>
      </OMA>
      <OMA>
        <OMS cd="logic1" name="implies"/>
        <OMV name="B"/>
        <OMV name="A"/>
      </OMA>
    </OMA>
  </OMA>
</OMOBJ>
om2mml();
<OMOBJ>
  <OMATTR>
    <OMATP>
      <OMS cd="typmml" name="type"/>
      <OMS cd="typmml" name="complex_polar_type"/>
    </OMATP>
    <OMV name="z"/>
  </OMATTR>
</OMOBJ>

% Examples of assigning types to variables.

om2mml();
      <OMOBJ>
         <OMATTR>
           <OMATP>
              <OMS cd="typmml" name="type"/>
              <OMS cd="typmml" name="integer_type"/>
           </OMATP>
           <OMV name="z"/>
         </OMATTR>
       </OMOBJ>

om2mml();
       <OMOBJ>
         <OMATTR>
           <OMATP>
              <OMS cd="typmml" name="type"/>
              <OMS cd="typmml" name="real_type"/>
           </OMATP>
           <OMV name="z"/>
         </OMATTR>
       </OMOBJ>

om2mml();
       <OMOBJ>
         <OMATTR>
           <OMATP>
              <OMS cd="typmml" name="type"/>
              <OMS cd="typmml" name="rational_type"/>
           </OMATP>
           <OMV name="z"/>
         </OMATTR>
       </OMOBJ>


% These examples show the use of attributions within OpenMath
% expressions.


om2mml();
<OMOBJ>
   <OMA>
     <OMATTR>
       <OMATP>
          <OMS cd="typmml" name="type"/>
          <OMS cd="typmml" name="fn_type"/>
       </OMATP>
       <OMV name="f"/>
     </OMATTR>
     <OMI>1</OMI>
     <OMI>2</OMI>
     <OMI>3</OMI>
   </OMA>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="arith1" name=times/>
    <OMATTR>
      <OMATP>
        <OMS cd="typmml" name="type"/>
        <OMS cd="typmml" name="matrix_type"/>
      </OMATP>
    <OMV name=A/>
    </OMATTR>
    <OMA>
      <OMS cd="transc1" name=sin/>
      <OMV name=x/>
    </OMA>
  </OMA>
</OMOBJ>   


om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="linalg3" name="vector_selector"/>
    <OMI>2</OMI>
    <OMA>
      <OMS cd="linalg1" name="vector"/>
      <OMI> 3 </OMI>
      <OMI> 6 </OMI>
      <OMI> 9 </OMI>
    </OMA>     
  </OMA>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="linalg3" name="vector_selector"/>
    <OMI>2</OMI>
    <OMA>
      <OMS cd="linalg1" name="matrixrow"/>
      <OMI> 0 </OMI>
      <OMI> 1 </OMI>
      <OMI> 0 </OMI>
    </OMA>     
  </OMA>
</OMOBJ>


om2mml();
<OMOBJ>
  <OMBIND>
    <OMS cd="quant1" name="forall"/>
    <OMBVAR>
      <OMV name="M"/>
    </OMBVAR>
    <OMA>
      <OMS cd="logic1" name="and"/>
      <OMA>
        <OMS cd="relation1" name="eq"/>
        <OMA>
          <OMS cd="arith1" name="times"/>
          <OMA>
            <OMS cd="linalg3" name="zero"/>
            <OMA>
              <OMS cd="linalg3" name="rowcount"/>
              <OMV name="M"/>
            </OMA>
            <OMA>
              <OMS cd="linalg3" name="rowcount"/>
              <OMV name="M"/>
            </OMA>
          </OMA>
          <OMV name="M"/>
        </OMA>
        <OMA>
          <OMS cd="linalg3" name="zero"/>
          <OMA>
            <OMS cd="linalg3" name="rowcount"/>
            <OMV name="M"/>
          </OMA>
          <OMA>
            <OMS cd="linalg3" name="columncount"/>
            <OMV name="M"/>
          </OMA>
        </OMA>
      </OMA>
      <OMA>
        <OMS cd="relation1" name="eq"/>
        <OMA>
          <OMS cd="arith1" name="times"/>
          <OMV name="M"/>
          <OMA>
            <OMS cd="linalg3" name="zero"/>
            <OMA>
              <OMS cd="linalg3" name="columncount"/>
              <OMV name="M"/>
            </OMA>
            <OMA>
              <OMS cd="linalg3" name="columncount"/>
              <OMV name="M"/>
            </OMA>
          </OMA>
        </OMA>
        <OMA>
          <OMS cd="linalg3" name="zero"/>
          <OMA>
            <OMS cd="linalg3" name="rowcount"/>
            <OMV name="M"/>
          </OMA>
          <OMA>
            <OMS cd="linalg3" name="columncount"/>
            <OMV name="M"/>
          </OMA>
        </OMA>
      </OMA>
    </OMA>
  </OMBIND>
</OMOBJ>


om2mml();
<OMOBJ>
   <OMA>
      <OMS cd="linalg3" name="vector_selector"/>
      <OMI> 1 </OMI>
      <OMATTR>
        <OMATP>
          <OMS cd="typmml" name="type"/>
          <OMS cd="typmml" name="vector_type"/>
        </OMATP>
        <OMV name=A/>
      </OMATTR>        
   </OMA>     
</OMOBJ>

om2mml();
<OMOBJ>
   <OMA>
      <OMS cd="linalg3" name="matrix_selector"/>
      <OMI> 1 </OMI>
      <OMI> 1 </OMI>
      <OMATTR>
        <OMATP>
          <OMS cd="typmml" name="type"/>
          <OMS cd="typmml" name="matrix_type"/>
        </OMATP>
        <OMV name=A/>
      </OMATTR>        
   </OMA>     
</OMOBJ>


% The following two examples were produced by REDUCE in MathML with the
% MathML interface, then translated to OpenMath. It is now possible to
% translate them back to MathML.

om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="list1" name="list"/>
    <OMA>
      <OMS cd="list1" name="list"/>
      <OMA>
        <OMS cd="relation1" name="eq">
        <OMV name="x"/>
        <OMA>
          <OMATTR>
            <OMATP>
              <OMS cd="typmml" name="type"/>
              <OMS cd="typmml" name="fn_type"/>
            </OMATP>
            <OMV name="root_of"/>
          </OMATTR>
          <OMA>
            <OMS cd="arith1" name="plus">
            <OMA>
              <OMS cd="arith1" name="minus">
              <OMA>
                <OMS cd="arith1" name="power">
                <OMV name="y"/>
                <OMV name="x_"/>
              </OMA>
            </OMA>
            <OMA>
              <OMS cd="arith1" name="minus">
              <OMA>
                <OMS cd="arith1" name="times">
                <OMA>
                  <OMS cd="calculus1" name="int"/>
                  <OMBIND>
                    <OMS cd="fns1" name="lambda"/>
                    <OMBVAR>
                      <OMV name="x_"/>
                    </OMBVAR>
                    <OMA>
                      <OMS cd="arith1" name="power">
                      <OMV name="x_"/>
                      <OMV name="x_"/>
                    </OMA>
                  </OMBIND>
                </OMA>
                <OMV name="y"/>
              </OMA>                              
            </OMA>
            <OMV name="x_"/>
            <OMV name="y"/>
          </OMA>
          <OMV name="x_"/>
          <OMV name="tag_1"/>
        </OMA>
      </OMA>
      <OMA>
        <OMS cd="relation1" name="eq">
        <OMV name="a"/>
        <OMA>
          <OMS cd="arith1" name="plus">
          <OMV name="x"/>
          <OMV name="y"/>
        </OMA>
      </OMA>
    </OMA>
  </OMA>
</OMOBJ>      




om2mml();
<OMOBJ>
  <OMA>
    <OMS cd="list1" name="list"/>
    <OMA>
      <OMS cd="list1" name="list"/>
      <OMA>
        <OMS cd="relation1" name="eq">
        <OMV name="x"/>
        <OMA>
          <OMATTR>
            <OMATP>
              <OMS cd="typmml" name="type"/>
              <OMS cd="typmml" name="fn_type"/>
            </OMATP>
            <OMV name="root_of"/>
          </OMATTR>
          <OMA>
            <OMS cd="arith1" name="plus">
            <OMA>
              <OMS cd="arith1" name="times">
              <OMA>
                <OMS cd="transc1" name="exp">
                <OMA>
                  <OMS cd="arith1" name="plus">
                  <OMS cd="nums1" name="i"/>
                  <OMV name="x_"/>
                </OMA>
              </OMA>
              <OMV name="y"/>
            </OMA>
            <OMA>
              <OMS cd="transc1" name="exp">
              <OMA>
                <OMS cd="arith1" name="plus">
                <OMS cd="nums1" name="i"/>
                <OMV name="x_"/>
              </OMA>
            </OMA>
            <OMA>
              <OMS cd="arith1" name="power">
              <OMV name="x_"/>
              <OMA>
                <OMS cd="arith1" name="plus">
                <OMV name="y"/>
                <OMI> 1 </OMI>
              </OMA>
            </OMA>
            <OMA>
              <OMS cd="arith1" name="times">
              <OMA>                                 
                <OMS cd="calculus1" name="int"/>
                <OMBIND>
                  <OMS cd="fns1" name="lambda"/>
                  <OMBVAR>
                    <OMV name="x_"/>
                  </OMBVAR>
                  <OMA>
                    <OMS cd="arith1" name="power">
                    <OMV name="x_"/>
                    <OMV name="x_"/>
                  </OMA>
                </OMBIND>
              </OMA>
              <OMA>
                <OMS cd="arith1" name="power">
                <OMV name="y"/>
                <OMI> 2 </OMI>
              </OMA>
            </OMA>
            <OMA>
              <OMS cd="arith1" name="times">
              <OMA>
                <OMS cd="calculus1" name="int"/>
                <OMBIND>
                  <OMS cd="fns1" name="lambda"/>
                  <OMBVAR>
                    <OMV name="x_"/>
                  </OMBVAR>
                  <OMA>
                    <OMS cd="arith1" name="power">
                    <OMV name="x_"/>
                    <OMV name="x_"/>
                  </OMA>
                </OMBIND>
              </OMA>
              <OMV name="y"/>
            </OMA>
          </OMA>
          <OMV name="x_"/>
          <OMV name="tag_2"/>
        </OMA>
      </OMA>
      <OMA>
        <OMS cd="relation1" name="eq">
        <OMV name="z"/>
        <OMV name="y"/>
      </OMA>
    </OMA>
  </OMA>
</OMOBJ>                     
om2mml();
<OMOBJ>
 <OMATTR>
    <OMATP>
       <OMS cd="cc" name="type"/>
       <OMS cd="omtypes" name="integer"/>
    </OMATP>
    <OMI> 0 </OMI>  
  </OMATTR>
</OMOBJ>

om2mml();
<OMOBJ>
  <OMATTR>
    <OMATP>
      <OMS cd="cc" name="type"/>
      <OMS cd="omtypes" name="float"/>
    </OMATP>
    <OMF dec=1.0/>  
  </OMATTR>
</OMOBJ>

om2mml();
<OMOBJ> 
  <OMA>
    <OMS name="complex_cartesian" cd="nums1"/>
    <OMV name="x"/>
    <OMV name="y"/>
  </OMA> 
</OMOBJ> 

om2mml();
<OMOBJ> 
  <OMA>
    <OMS name="complex_polar" cd="nums1"/>
    <OMV name="x"/>
    <OMV name="y"/>
  </OMA> 
</OMOBJ> 

om2mml();
<OMOBJ> 
  <OMA>
    <OMS name="rational" cd="nums1"/>
    <OMV name="x"/>
    <OMV name="y"/>
  </OMA> 
</OMOBJ> 

om2mml();
<OMOBJ> 
  <OMA>
    <OMS name="complex_cartesian" cd="nums1"/>
    <OMI>4</OMI>
    <OMI>2</OMI>
  </OMA> 
</OMOBJ> 

om2mml();
<OMOBJ> 
  <OMA>
    <OMS name="complex_polar" cd="nums1"/>
    <OMI>4</OMI>
    <OMI>2</OMI>
  </OMA> 
</OMOBJ> 

om2mml();
<OMOBJ> 
  <OMA>
    <OMS name="rational" cd="nums1"/>
    <OMI>4</OMI>
    <OMI>2</OMI>
  </OMA> 
</OMOBJ> 



end;
