// Upgrade NOTE: replaced 'mul(UNITY_MATRIX_MVP,*)' with 'UnityObjectToClipPos(*)'

Shader "AShader" {
	Properties{
		_Color("Main Color", Color) = (1,1,1,1)
		_MainTex("Base (RGB) Alpha (A)", 2D) = "white" {}
	_Cutoff("Alpha cutoff", Range(0,1)) = 0.5
	}

		SubShader{
		Tags{ "Queue" = "Transparent" "IgnoreProjector" = "True" "RenderType" = "Transparent" "LightMode" = "ForwardBase" }
		LOD 100
		Cull Off
		ZWrite Off
		Blend SrcAlpha OneMinusSrcAlpha


		CGINCLUDE
#include "UnityCG.cginc"
		sampler2D _MainTex;
	half4 _MainTex_ST;
	half4 _Color;

	float4 _GrassWind; //is not defined in terrainengine.cginc

	struct v2f {
		float4 pos : SV_POSITION;
		float2 uv : TEXCOORD0;
	};

	inline float4 AnimateGrass(float4 pos, float3 normal, float animParams)
	{
		pos.xyz += animParams * _GrassWind.xyz * _GrassWind.w; // controlled by vertex color blue
		return pos;
	}

	v2f vert(appdata_full v)
	{
		v2f o;
		float4    windParams = float(v.color.b);

		float4 mdlPos = AnimateGrass(v.vertex, v.normal, windParams);
		o.pos = UnityObjectToClipPos(mdlPos);
		o.uv = TRANSFORM_TEX(v.texcoord, _MainTex);
		return o;
	}
	ENDCG

		Pass{
		CGPROGRAM
#pragma debug
#pragma vertex vert
#pragma fragment frag
#pragma fragmentoption ARB_precision_hint_fastest     
		fixed4 frag(v2f i) : COLOR
	{
		fixed4 tex = tex2D(_MainTex, i.uv);

	fixed4 c;
	c.rgb = tex.rgb;
	c.a = tex.a;

	return c * _Color;
	}
		ENDCG
	}
	}
}