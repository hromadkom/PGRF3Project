#version 330
in vec3 vertColor;
in vec3 lightvec;
in vec3 lightdir;
in vec3 eyevec;
in vec3 vertPosition;
in vec3 normal;
in vec2 texCoord;
in float dist;
in vec3 blinnPhongPerVertex;
out vec4 outColor;
float cutoff;

uniform sampler2D texture;
uniform int colorType;
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

void main() {
	vec3 eye = normalize(eyevec);
	vec3 light = normalize(lightvec);
	vec3 norm = normalize(normal);
	
	vec3 blinPhong;
	
	if(lightningPerVertex)
		blinPhong = blinnPhongPerVertex;
	else 
		blinPhong = lightning(eye, light, norm);
		
	vec4 color;
	
	if(colorType == 1)
		color = texture2D(texture, texCoord);
	else if(colorType == 2)
		color = vec4(norm, 1.0);
	else if(colorType == 3)
		color = vec4(vertPosition, 1.0);	
	else if(colorType == 4)
		color = vec4(vertColor, 1.0);	
				
	cutoff = 0.7;
	
	float spotEffect= dot(normalize(lightdir), normalize(-light));
	float att = 1.0/(0.2 + 0.2*dist + 0.2*dist*dist);
	
	if(spotEffect > cutoff)	
		outColor = att*(blinPhong.x + blinPhong.y)*color + blinPhong.z*vec4(1.0, 1.0, 1.0, 1.0);
	else
		outColor = att*blinPhong.x*color;
} 
