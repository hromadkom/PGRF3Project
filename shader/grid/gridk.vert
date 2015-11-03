#version 330
in vec2 inPosition; // vstup z vertex bufferu
out vec3 vertColor;
uniform mat4 mat;
void main() {
    float s = (inPosition.x*2.0)-1.0;
    float t = (inPosition.y*2.0)-1.0;
    float z = (cos(sqrt(20*pow(s,2.0)+20*pow(t,2.0))))/2.0;
	gl_Position = mat * vec4(inPosition,z, 1.0);
	vertColor = vec3(inPosition,0.0);
}