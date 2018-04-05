// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

Shader "Coherent/ViewShader" {
	Properties { _MainTex ("Texture", any) = "" {} }

	SubShader {

		Tags { "ForceSupported" = "True" "RenderType"="Overlay" }

		Lighting Off
		Blend One OneMinusSrcAlpha
		Cull Off
		ZWrite Off
		Fog { Mode Off }
		ZTest Always

		Pass {
			CGPROGRAM
			#pragma vertex vert
			#pragma fragment frag
			#pragma multi_compile COHERENT_NO_FLIP_Y COHERENT_FLIP_Y
			#pragma multi_compile COHERENT_NO_CORRECT_GAMMA COHERENT_CORRECT_GAMMA

			#include "UnityCG.cginc"

			struct appdata_t {
				float4 vertex : POSITION;
				fixed4 color : COLOR;
				float2 texcoord : TEXCOORD0;
			};

			struct v2f {
				float4 vertex : SV_POSITION;
				fixed4 color : COLOR;
				float2 texcoord : TEXCOORD0;
			};

			sampler2D _MainTex;

			uniform float4 _MainTex_ST;

			v2f vert (appdata_t v)
			{
				v2f o;
				o.vertex = UnityObjectToClipPos(v.vertex);
				o.color = v.color;
				o.texcoord = TRANSFORM_TEX(v.texcoord,_MainTex);
				return o;
			}

			fixed4 frag (v2f i) : SV_Target
			{
			#if COHERENT_FLIP_Y ^ UNITY_UV_STARTS_AT_TOP
				float2 flipuv = float2(i.texcoord.x, 1 - i.texcoord.y);
				fixed4 color = tex2D(_MainTex, flipuv) * i.color;
			#else
				fixed4 color = tex2D(_MainTex, i.texcoord) * i.color;
			#endif

			#if COHERENT_CORRECT_GAMMA
				return fixed4(pow(color.rgb, 0.454545), color.a);
			#else
				return color;
			#endif
			}
			ENDCG
		}
	}

	Fallback off
}
