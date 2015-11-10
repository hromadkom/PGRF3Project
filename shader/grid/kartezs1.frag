#version 330
in vec4 position;
in vec3 normal;
in vec3 lightDirection;
in vec3 vertColor;
out vec4 outColor;
void main() {
	vec3 nd = normalize(normal);
	float NdotL = max(dot(normalize(lightDirection),nd),0.0);
	vec4 color=NdotL*vec4(1.0);
	outColor = color;
}