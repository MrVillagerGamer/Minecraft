uniform sampler2D texture;
varying vec4 color;
varying vec4 texcoord;
varying float fogval;
uniform int isunderwater;
void main(void) {
	vec4 fcolor = texture2D(texture, texcoord.st) * color;
	vec4 aircolor = vec4(0.4, 0.7, 1, 1);
	vec4 watercolor = vec4(aircolor.rgb / 2.0, 1.0);
	fcolor = fcolor * (1.0 - fogval) + mix(aircolor, watercolor, float(isunderwater)) * fogval;
	if(fcolor.a < 0.5) discard;
	gl_FragColor = fcolor;
}