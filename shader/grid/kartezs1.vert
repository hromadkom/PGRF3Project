#version 330
in vec2 inPosition;
uniform mat4 mat;
uniform mat4 modelView;
const float PI=3.1415926;
// pro hezke osvetleni - scalarni soucin vektor k pozorovateli a normaly
vec3 function (vec2 uv){
        float s = (uv.x*2.0)-1.0;
        float t = (uv.y*2.0)-1.0;
        float z = (cos(sqrt(20*pow(s,2.0)+20*pow(t,2.0))))/2.0;
        return vec3(uv,z);
}
vec3 normalDiff (vec2 uv){
    float delta = 0.01;
    vec3 dzdu = (function(uv+vec2(delta,0))-function(uv-vec2(delta,0)));
    vec3 dzdv = (function(uv+vec2(0,delta))-function(uv-vec2(0,delta)));
    return cross(dzdu,dzdv);
}

out vec4 position;
out vec3 normal;
out vec3 lightDirection;
out vec3 vertColor;
void main()
{
    position = vec4(function(inPosition),1.0);
    normal = normalDiff(inPosition);
    //normal = gl_NormalMatrix*normal;
    vec4 lightPosition = vec4(-10.0,5.0,2.0,1.0);
    vec4 objectPosition = modelView * position;
    lightDirection = lightPosition.xyz - objectPosition.xyz;
    vertColor = vec3(1.0);
    gl_Position= mat*position;
}