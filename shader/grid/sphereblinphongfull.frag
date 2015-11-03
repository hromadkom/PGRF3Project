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

void main() {
    vec3 ld = normalize( lightDirection );
    vec3 nd = normalize( normal );
    vec3 vd = normalize( viewDirection );
    float NDotL = max(dot( nd, ld),0.0 );

    vec3 reflection = normalize( ( ( 2.0 * nd ) * NDotL ) - ld );
    float RDotV = max( 0.0, dot( reflection, vd ) );

    vec3 halfVector = normalize( ld + vd);
    float NDotH = max( 0.0, dot( nd, halfVector ) );

    vec4 textColor = texture(texture, textCoor);

    vec4 totalAmbient = 0.3 * textColor;
    vec4 totalDiffuse = 1.0 * NDotL * textColor;
    vec4 totalSpecular = vec4(1.0) * ( pow( NDotH, 4.0 ) );

    float att=1.0 * (0.1*dist);

	vec4 color= totalAmbient + att * (totalDiffuse + totalSpecular);
	outColor = vec4(vertColor,1.0);
}