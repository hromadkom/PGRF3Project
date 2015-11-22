#version 330
uniform vec3 eyeVec;
uniform vec4 baseColor;

uniform sampler2D texture;
uniform sampler2D normalTexture;
uniform sampler2D heightTexture;

uniform vec3 spotDirection;
uniform float spotCutOff;
uniform int spotLightEnabled;

uniform int perVertexShading;
uniform int bumpEnabled;
uniform int extendedMapping;
uniform int showColor;
uniform float lightDownturn;

in vec3 vertColor;
in vec3 position;
in vec3 normal;
in vec3 lightDirection;
in vec3 viewDirection;
in vec3 lightDirectionTBN;
in vec3 viewDirectionTBN;
in vec3 perVertexShades;
in float dist;
in vec2 textCoor;
out vec4 outColor;

vec2 cBumpSize = vec2 (0.02, -0.01);

vec3 shading(vec3 viewDirection, vec3 lightDirection, vec3 normal){
	float am = 0.2;
	float spec = 0.0;
	float diff = 0.0;

	vec3 ld = normalize( lightDirection );
    vec3 nd = normalize( normal );
    vec3 vd = normalize( viewDirection );

	vec3 halfVector = normalize( lightDirection + viewDirection);

	diff = max(dot(ld, nd), 0.0 );
	if(diff > 0.0){
		spec = ( pow( max(dot(nd, halfVector), 0.0 ), 40.0 ) );
	}

	return vec3(am, diff, spec);
}


vec4 calculateColor(vec3 shades, vec4 color, float distance, bool ambientOnly){
	vec4 totalAmbient = shades.x * color;
	if(ambientOnly){
		return totalAmbient;
	}else{
		vec4 totalDiffuse = shades.y * color;
		vec4 totalSpecular = vec4(1.0) * shades.z;
		float att= lightDownturn / 1.0 + distance*0.1;
		return totalAmbient + att * (totalDiffuse + totalSpecular);
	}
}

void main() {
	if(showColor != 0 && showColor != 3){
		switch(showColor){
			case 1:
				outColor = vec4(position.x, position.y, 0.0,1.0);
				break;
			case 2:
				outColor = vec4(normal.xyz, 1.0);
				break;
		}
		return;
	}

	vec2 texUV = textCoor.xy;
	vec3 n = normal;
	if(extendedMapping == 1 && perVertexShading == 0){
		float height = texture(heightTexture, textCoor).r;
		height = height * cBumpSize.x + cBumpSize.y;
		texUV = textCoor.xy + normalize( viewDirectionTBN ).xy * height;
		vec3 bump = texture(normalTexture,texUV.xy).rgb * 2.0 - 1.0;
		n = normalize(bump);
   	}

	vec4 bsColor = texture(texture, texUV.xy);
	if(showColor == 3){
		bsColor = baseColor;
	}
	vec3 shades = shading(viewDirectionTBN, lightDirectionTBN, n);
	if(perVertexShading == 1){
		shades = perVertexShades;
	}

	if(spotLightEnabled==1){
		float spotEffect = dot(normalize(spotDirection), normalize(-lightDirection));
        	if (spotEffect>spotCutOff) {
				outColor = calculateColor(shades, bsColor, dist, false);
        	}else{
				outColor = calculateColor(shades, bsColor, dist, true);
        	}
	}else{
		outColor = calculateColor(shades, bsColor, dist, false);
	}
}