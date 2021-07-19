Shader "Voxel/Fluid Vertex"
{
	Properties
	{
		_Color("Color", Color) = (1, 1, 1, 1)
		_MainTex("Albedo (RGB)", 2D) = "white" {}
	_Glossiness("Smoothness", Range(0, 1)) = 0.5
		_Metallic("Metallic", Range(0,1)) = 0.0
		_Speed("Speed", float) = 2.0
		_Alpha("Alpha", float) = 0.8
	}
		SubShader
	{
		Tags{ "RenderType" = "Transparent" "Queue" = "Transparent" }
		Blend SrcAlpha OneMinusSrcAlpha
		LOD 200
		Cull off

		CGPROGRAM

#pragma surface surf Standard vertex:vert fullforwardshadows
#pragma target 3.0

		float _Alpha;
	float _Speed;

	struct Input
	{
		float2 uv_MainTex;
		float3 vertexColor;
	};

	struct v2f
	{
		float4 pos : SV_POSITION;
		fixed4 color : COLOR;
	};

	void vert(inout appdata_full v, out Input o)
	{
		UNITY_INITIALIZE_OUTPUT(Input,o);
		v.texcoord.x += _Time * _Speed;
		o.vertexColor = v.color;
	}

	sampler2D _MainTex;

	half _Glossiness;
	half _Metallic;
	fixed4 _Color;

	void surf(Input IN, inout SurfaceOutputStandard o)
	{
		fixed4 c = tex2D(_MainTex, IN.uv_MainTex) * _Color;
		o.Albedo = c.rgb * IN.vertexColor;

		o.Metallic = _Metallic;
		o.Smoothness = _Glossiness;

		o.Alpha = c.a * _Alpha;
	}

	ENDCG
	}
		FallBack "Standard"
}