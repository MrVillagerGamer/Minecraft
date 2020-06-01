varying vec4 color;
varying vec4 texcoord;
varying float fogval;
uniform float fogstart;
uniform float foglength;
uniform int usefog;
uniform int isunderwater;
const float waterfogstart = 10.0;
const float waterfoglength = 10.0;

void main(void) {
	vec4 pos = gl_ModelViewMatrix * gl_Vertex;
	fogval = clamp((length(pos.xyz)-fogstart)/foglength, 0, 1) * float(usefog) * float(1 - isunderwater);
	fogval += clamp((length(pos.xyz)-waterfogstart)/waterfoglength, 0, 1) * float(usefog) * float(isunderwater);
	
	color = gl_Color;
	texcoord = gl_MultiTexCoord0;
	gl_Position = gl_ProjectionMatrix * pos;
}