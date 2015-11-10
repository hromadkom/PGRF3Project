#version 330
uniform vec3 eyeVec;
uniform vec4 baseColor;
uniform sampler2D texture;
uniform vec3 spotDirection;
uniform float spotCutOff;
in vec3 vertColor;
in vec4 position;
in vec3 normal;
in vec3 lightDirection;
in vec3 viewDirection;
in float dist;
in vec2 textCoor;
out vec4 outColor;

vec3 shading(vec3 viewDirection, vec3 lightDirection, vec3 normal){
	float am = 0.2;
	float spec = 0.0;
	float diff = 0.0;

	vec3 ld = normalize( lightDirection );
    vec3 nd = normalize( normal );
    vec3 vd = normalize( viewDirection );

	vec3 halfVector = normalize( lightDirection + viewDirection);

	diff = max(dot(ld, nd),0.0 );

	if(diff > 0.0){
		spec = ( pow( max(dot(nd, halfVector), 0.0 ), 40.0 ) );
	}

	return vec3(am, diff, spec);
}

void main() {
    vec4 textColor = texture(texture, textCoor);
    vec4 totalAmbient = 0.2 * textColor;
	vec4 color= totalAmbient;
    float spotEffect = dot(normalize(spotDirection), normalize(-lightDirection));
    if (spotEffect>spotCutOff) {

		vec3 shade = shading(viewDirection, lightDirection, normal);

    	vec4 totalDiffuse = shade.y * textColor;
    	vec4 totalSpecular = vec4(1.0) * shade.z;
    	float att=1.0 / (1.3 + 0.1 * dist);

  		color= totalAmbient + (totalDiffuse + totalSpecular);
  	}

  	outColor = color;
}