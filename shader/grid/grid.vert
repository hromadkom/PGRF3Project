#version 330
in vec2 inPosition; // vstup z vertex bufferu
out vec3 vertColor;
uniform mat4 mat;
void main() {
	gl_Position = mat * vec4(inPosition,0.0, 1.0);
	vertColor = vec3(inPosition,1.0);
} 
