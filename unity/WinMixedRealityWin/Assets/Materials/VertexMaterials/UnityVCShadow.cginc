// Upgrade NOTE: replaced 'UNITY_INSTANCE_ID' with 'UNITY_VERTEX_INPUT_INSTANCE_ID'

#ifndef UNITY_VC_SHADOW_INCLUDED
#define UNITY_VC_SHADOW_INCLUDED

/*
Unity Standard Vertex Color Shader Shadow Lib v0.91
by defaxer
*/

#include "UnityCG.cginc"
#include "UnityShaderVariables.cginc"
#include "UnityInstancing.cginc"
#include "UnityStandardConfig.cginc"

#if defined(_VERTEXCOLOR) || defined(_VERTEXCOLOR_LERP)
float _IntensityVC;
#endif

struct VertexInput_VC
{
		float4 vertex	: POSITION;
	#if defined(_VERTEXCOLOR) || defined(_VERTEXCOLOR_LERP)
		fixed4 color : COLOR;
	#endif
		float3 normal	: NORMAL;
		float2 uv0		: TEXCOORD0;
		UNITY_VERTEX_INPUT_INSTANCE_ID
};

#ifdef UNITY_STANDARD_USE_SHADOW_OUTPUT_STRUCT
struct VertexOutputShadowCaster_VC
{
		V2F_SHADOW_CASTER_NOPOS
	#if defined(UNITY_STANDARD_USE_SHADOW_UVS)
		float2 tex : TEXCOORD1;
		#if defined(_VERTEXCOLOR) || defined(_VERTEXCOLOR_LERP)
			fixed4 color : COLOR;
		#endif
	#endif
};
#endif

// We have to do these dances of outputting SV_POSITION separately from the vertex shader,
// and inputting VPOS in the pixel shader, since they both map to "POSITION" semantic on
// some platforms, and then things don't go well.

void vertShadowCaster_VC(VertexInput_VC v,
#ifdef UNITY_STANDARD_USE_SHADOW_OUTPUT_STRUCT
	out VertexOutputShadowCaster_VC o,
#endif
	out float4 opos : SV_POSITION)
{
		UNITY_SETUP_INSTANCE_ID(v);
		TRANSFER_SHADOW_CASTER_NOPOS(o, opos)
	#if defined(UNITY_STANDARD_USE_SHADOW_UVS)
		o.tex = TRANSFORM_TEX(v.uv0, _MainTex);
		#if defined(_VERTEXCOLOR) || defined(_VERTEXCOLOR_LERP)
			o.color = v.color;
		#endif
	#endif
}

half4 fragShadowCaster_VC(
#ifdef UNITY_STANDARD_USE_SHADOW_OUTPUT_STRUCT
	VertexOutputShadowCaster_VC i
#endif
#ifdef UNITY_STANDARD_USE_DITHER_MASK
	, UNITY_VPOS_TYPE vpos : VPOS
#endif
) : SV_Target
{
	#if defined(UNITY_STANDARD_USE_SHADOW_UVS)
		half alpha = tex2D(_MainTex, i.tex).a * _Color.a;
		#if defined(_VERTEXCOLOR)
			alpha *= i.color.a;
		#endif
		#if defined(_VERTEXCOLOR_LERP)
			alpha *= lerp(1, i.color.a, _IntensityVC);
		#endif

		#if defined(_ALPHATEST_ON)
			clip(alpha - _Cutoff);
		#endif

		#if defined(_ALPHABLEND_ON) || defined(_ALPHAPREMULTIPLY_ON)
			#if defined(UNITY_STANDARD_USE_DITHER_MASK)
				// Use dither mask for alpha blended shadows, based on pixel position xy
				// and alpha level. Our dither texture is 4x4x16.
				half alphaRef = tex3D(_DitherMaskLOD, float3(vpos.xy*0.25,alpha*0.9375)).a;
				clip(alphaRef - 0.01);
			#else
				clip(alpha - _Cutoff);
			#endif
		#endif
	#endif // #if defined(UNITY_STANDARD_USE_SHADOW_UVS)

	SHADOW_CASTER_FRAGMENT(i)
}

#endif