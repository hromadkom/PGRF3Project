#version 330
uniform vec3 eyeVec;
uniform vec4 baseColor;
uniform sampler2D texture;
in vec3 vertColor;
in vec4 position;
in vec3 normal;
in vec3 lightDirection;
in vec3 viewDirection;
in float dist;
in vec2 textCoor;
out vec4 outColor;

vec3 shading(vec3 viewPosition, vec3 lightPosition, vec3 normal){
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
    vec3 ld = normalize( lightDirection );
    vec3 nd = normalize( normal );
    vec3 vd = normalize( viewDirection );

    vec4 textColor = texture(texture, textCoor);

    vec3 halfVector = normalize( lightDirection + viewDirection);

    vec4 totalAmbient = 0.2 * textColor;
    vec4 totalDiffuse = 0.6 * max(dot(ld, nd),0.0 ) * textColor;
    vec4 totalSpecular = vec4(0.8) * ( pow( max(dot(nd, halfVector), 0.0 ), 20.0 ) );

    float dotProd = dot(halfVector,nd);

    float att=1.0 / (0.3 + 0.1 * dist);

  	vec4 color= totalAmbient + att * (totalDiffuse + totalSpecular);
  	outColor = color;
}