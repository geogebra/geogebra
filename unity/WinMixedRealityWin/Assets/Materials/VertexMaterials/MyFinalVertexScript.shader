// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'


// Unlit alpha-blended shader.
// - no lighting
// - no lightmap support
// - no per-material color

Shader "Unlit/Transparent 2" {
	Properties{
		_Color("Main Color", Color) = (1,1,1,1)
		_MainTex("Base (RGB) Trans (A)", 2D) = "white" {}
		_Glossiness("Smoothness", Range(0,1)) = 0.5
		_Metallic("Metallic", Range(0,1)) = 0.0
	}

		SubShader{
		Tags{ "Queue" = "Fade" "IgnoreProjector" = "True" "RenderType" = "Fade" }
		LOD 100

		ZWrite Off
		Blend SrcAlpha OneMinusSrcAlpha

		Pass{
		CGPROGRAM
#pragma vertex vert
#pragma fragment frag

#include "UnityCG.cginc"

		struct appdata_t {
		float4 vertex : POSITION;
		float2 texcoord : TEXCOORD0;
		float4 color : COLOR;
	};

	struct v2f {
		float4 vertex : SV_POSITION;
		half2 texcoord : TEXCOORD0;
		float4 color : COLOR;
	};

	sampler2D _MainTex;
	float4 _MainTex_ST;
	half _Glossiness;
	half _Metallic;
	fixed4 _Color;

	v2f vert(appdata_t v)
	{
		v2f o;
		o.vertex = UnityObjectToClipPos(v.vertex);
		o.texcoord = TRANSFORM_TEX(v.texcoord, _MainTex);
		o.color = v.color;
		o.Metallic = _Metallic;
		o.Smoothness = _Glossiness;
		return o;
	}

	fixed4 frag(v2f i) : COLOR
	{
		fixed4 col = tex2D(_MainTex, i.texcoord) * _Color * i.color;
	return col;
	}
		ENDCG
	}
	}

}