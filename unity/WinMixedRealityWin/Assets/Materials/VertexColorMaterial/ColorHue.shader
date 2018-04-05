// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

Shader "ColorPicker/ColorHue" {
	SubShader {
	    Pass {	
			CGPROGRAM
			#pragma vertex vert
			#pragma fragment frag
			#include "UnityCG.cginc"		
			
			// vertex input: position, UV
			struct appdata {
			    float4 vertex : POSITION;
			    float4 texcoord : TEXCOORD0;
			};
	
			struct pos_output {
			    float4 pos : SV_POSITION;
			    float4 uv : TEXCOORD0;
			};
			
			pos_output vert(appdata v) {
			    pos_output o;
			    o.pos = UnityObjectToClipPos(v.vertex);
			    o.uv = float4(v.texcoord.xy, 0, 0);
			    return o;
			}
			
			half4 frag(pos_output o) : COLOR {
				half p = floor(o.uv.x*6);
				half i = o.uv.x*6-p;
				half4 c = p == 0 ? half4(1, i, 0, 1) :
						  p == 1 ? half4(1-i, 1, 0, 1) :
						  p == 2 ? half4(0, 1, i, 1) :
						  p == 3 ? half4(0, 1-i, 1, 1) :
						  p == 4 ? half4(i, 0, 1, 1) :
						  p == 5 ? half4(1, 0, 1-i, 1) :
						           half4(1, 0, 0, 1);
			    return c;
			}
			ENDCG
	    }
	}
}

