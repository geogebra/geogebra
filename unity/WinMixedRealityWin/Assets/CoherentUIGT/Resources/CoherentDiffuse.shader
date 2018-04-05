Shader "Coherent/Diffuse" {
Properties {
	_Color ("Main Color", Color) = (1,1,1,1)
	_MainTex ("Base (RGB)", 2D) = "white" {}
}
SubShader {
	Tags { "RenderType"="Opaque" }
	LOD 200

CGPROGRAM
#pragma surface surf Lambert
#pragma multi_compile COHERENT_NO_FLIP_Y COHERENT_FLIP_Y
#pragma multi_compile COHERENT_NO_CORRECT_GAMMA COHERENT_CORRECT_GAMMA

sampler2D _MainTex;
fixed4 _Color;

struct Input {
	float2 uv_MainTex;
};

void surf (Input IN, inout SurfaceOutput o) {
	#if COHERENT_FLIP_Y ^ UNITY_UV_STARTS_AT_TOP
		float4 color = tex2D(_MainTex, IN.uv_MainTex) * _Color;
	#else
		float2 flipuv = float2(IN.uv_MainTex.x, 1 -  IN.uv_MainTex.y);
		float4 color = tex2D(_MainTex, flipuv) * _Color;
	#endif

	#if COHERENT_CORRECT_GAMMA
		float4 compensated_c = float4(pow(color.rgb, 0.454545), color.a);
		o.Albedo = compensated_c.rgb;
		o.Alpha = compensated_c.a;
	#else
		o.Albedo = color.rgb;
		o.Alpha = color.a;
	#endif
}
ENDCG
}

Fallback "Transparent/VertexLit"
}
