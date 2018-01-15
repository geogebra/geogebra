// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See LICENSE in the project root for license information.

Shader "MRDL/ColorMaskShader" 
{
	Properties
	{
		_Color ("Color", Color) = (1,1,1,1)
		_MainTex ("Albedo", 2D) = "white" {}
		_ColorMask ("Alpha", 2D) = "white" {}
		_MetallicMap ("Metallic", 2D) = "white" {}
		_Smoothness ("Smoothness", Range(0, 1)) = 0
		_Metallic ("Metallic",Range(0, 1)) = 0
		_Normal ("Normal Map", 2D) = "bump" {}
	}
		
	SubShader
	{
		Tags { "RenderType" = "Opaque" }
		LOD 200

		CGPROGRAM
		#pragma surface surf Standard fullforwardshadows

		sampler2D _MainTex;
		sampler2D _Normal;
		sampler2D _MetallicMap;
		sampler2D _ColorMask;

		float _Metallic;
		float _Smoothness;

		struct Input {
			float2 uv_MainTex;
		};

		fixed4 _Color;

		void surf(Input IN, inout SurfaceOutputStandard o) 
		{
			fixed4 mask = tex2D(_ColorMask, IN.uv_MainTex);
			fixed4 color = tex2D(_MainTex, IN.uv_MainTex);
			color.rgb *= lerp(1, _Color, mask.a);
			o.Albedo = color.rgb;
			o.Normal = UnpackNormal(tex2D(_Normal, IN.uv_MainTex));
			fixed4 metallic = tex2D(_MetallicMap, IN.uv_MainTex);
			o.Metallic = metallic.r * _Metallic;
			o.Smoothness = metallic.a * _Smoothness;
		}
		ENDCG
	}
	FallBack "Diffuse"
}