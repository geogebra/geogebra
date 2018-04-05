// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

Shader "ColorPicker/ColorSaturationBrightness" {
	Properties {
	    _Color ("Main Color", Color) = (0.75,0.15,0.56,1)
	}
	SubShader {
	    Pass {	
			CGPROGRAM
			#pragma vertex vert
			#pragma fragment frag
			#include "UnityCG.cginc"
			
			float4 _Color;
			
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
				half4 c = o.uv.y + (_Color - 1)*o.uv.x;
			    return c;			
			}
			ENDCG
	    }
	}
}
