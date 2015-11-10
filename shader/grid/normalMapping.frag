#version 330
uniform vec3 eyeVec;
uniform vec4 baseColor;
uniform sampler2D texture;
uniform sampler2D normalTexture;
in vec3 vertColor;
in vec4 position;
in vec3 normal;
in vec3 lightDirection;
in vec3 viewDirection;
in float dist;
in vec2 textCoor;
out vec4 outColor;


void main() {
    vec3 ld = normalize( lightDirection );
    vec3 nd = normalize( normal );
    vec3 vd = normalize( viewDirection );

    vec3 bump = texture(normalTexture,textCoor).rgb * 2.0 - 1.0;
    bump = normalize(bump);
    //bump = normalize(normal);

    vec4 textColor = texture(texture, textCoor);

    vec3 halfVector = normalize( lightDirection + viewDirection);

    float NdotL = max(dot(bump, ld),0.0);
    vec4 totalAmbient = 0.2 * textColor;
    vec4 totalDiffuse = 0.6 * NdotL * textColor;
    float NdotHV = max( 0.0, dot(normalize( ld + vd), bump) );
    vec4 totalSpecular = vec4(0.8) *  pow( NdotHV, 20.0 );

    float att=1.0;// / (0.3 + 0.1 * dist);

  	vec4 color= totalAmbient + att * (totalDiffuse + totalSpecular);
  	//outColor = vec4(bump,1.0);
  	//outColor = totalDiffuse;
  	//outColor = vec4(NdotL, 1.0, 1.0, 1.0);
  	outColor = color;
}