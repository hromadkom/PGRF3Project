#version 330
in vec3 inPosition; // vstup z vertex bufferu
in vec3 inColor;
in vec3 inNormal;
out vec3 vertColor;
out vec2 texCoord;
out vec3 lightvec;
out vec3 lightdir;
out vec3 eyevec;
out vec3 normal;
out float dist;
out vec3 vertPosition;
out vec3 blinnPhongPerVertex;

uniform vec3 eyeVec;
uniform vec3 lightVec; 
uniform mat4 mat;
uniform vec3 lightDirectionVec;
uniform int funcType;
uniform bool lightningPerVertex;

vec3 lightning(vec3 viewPosition, vec3 lightPosition, vec3 normal){
	float am = 0.5;
	float spec = 0.0;
	float diff = 0.0;
	
	vec3 hal = normalize(lightPosition + viewPosition);
	
	diff = max(dot(lightPosition, normal), 0.0);	
		
	if(diff > 0.0){
		spec = pow(max(dot(hal, normal), 0.0), 60);
	}
	
	return vec3(am, diff, spec);
}

vec3 cylindricFunction(in float azimut, in float t, in int funcType){
	float v = 1.0;
	
	if(funcType == 1)
		v = 4*cos(t/4);
	else if(funcType == 2)	
		v = 2*sin(t);
	
	vec3 func;
	func.x = t*cos(azimut)/4;
	func.y = t*sin(azimut)/4;
	func.z = v/2;
	
	return func;
}

vec3 transformPosition(in vec3 pos, in int funcType){
	float PI = 3.141592;
	float t = 2*pos.x*PI;
	float azimut = 2*pos.y*PI;
	
	return cylindricFunction(azimut, t, funcType);
}

vec3 transformNormal(in vec3 position, in int funcType){
	float deltaU = 0.01;
	float deltaV = 0.01;

	vec3 u1 = transformPosition(vec3(position.x + (deltaU/2), position.yz), funcType);
	vec3 u2 = transformPosition(vec3(position.x - (deltaU/2), position.yz), funcType);
	vec3 v1 = transformPosition(vec3(position.x, position.y + (deltaV/2), position.z), funcType);
	vec3 v2 = transformPosition(vec3(position.x, position.y - (deltaV/2), position.z), funcType);
	
	vec3 u = u1-u2;
	vec3 v = v1-v2;
	
	return normalize(cross(u, v));
}

void main() {

	vec3 position = transformPosition(inPosition, funcType);
	normal = transformNormal(inPosition, funcType);
	
	lightvec = lightVec - position; // lightVec = poziceSvetla
	eyevec = eyeVec - position; // eyeVec = camera.getEye
	lightdir = lightDirectionVec - position;
	
	vertPosition = inPosition;
	
	dist = length(lightvec);
	
	if(lightningPerVertex)
		blinnPhongPerVertex = lightning(eyevec, lightvec, normal);
	
	texCoord.x = inPosition.y;
	texCoord.y = inPosition.x; 
	
	gl_Position = mat * vec4(position, 1.0);
	vertColor = inColor;
} 
