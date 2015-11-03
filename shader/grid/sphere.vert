#version 330
in vec2 inPosition; // vstup z vertex bufferu
out vec3 vertColor;
uniform mat4 mat;
float azimuth,zenith,x,y,z;
void main() {
	azimuth = inPosition.x * 2 * 3.14;
	zenith = - (inPosition.y * 3.14) - (3.14/2.0);

	x = cos(zenith) * cos(azimuth);
	y = cos(zenith) * sin(azimuth);
	z = sin(zenith);
	gl_Position = mat * vec4(x,y,z, 1.0);
	vertColor = vec3(inPosition.xy,0.0);
} 
