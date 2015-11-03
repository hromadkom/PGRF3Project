#version 330
uniform vec3 eyeVec;
uniform vec4 baseColor;
in vec4 position;
in vec3 normal;
in vec3 lightDirection;
in vec3 viewDirection;
in float dist;
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

    vec4 totalAmbient = 0.3 * baseColor;
    vec4 totalDiffuse = 1.0 * NDotL * baseColor;
    vec4 totalSpecular = vec4(1.0) * ( pow( NDotH, 4.0 ) );

    float att=1.0 * (0.1*dist);

	vec4 color= totalAmbient + att * (totalDiffuse + totalSpecular);
	outColor = color;
}