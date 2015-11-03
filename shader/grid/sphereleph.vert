#version 330
in vec2 inPosition;
uniform mat4 mat;
uniform mat4 modelView;
out vec3 vertColor;
const float PI=3.1415926;
vec3 function (vec2 uv){
    float azimuth = uv.x*2.0*PI;
    float zenith = uv.y*PI;
    float r = 3 + cos(4*azimuth);
    vec3 position;
    position.x=r * cos(azimuth)*sin(zenith);
    position.y=r * sin(azimuth)*sin(zenith);
    position.z=r * cos(zenith);
    return position;
}
vec3 normalDiff (vec2 uv){
    float delta = 0.01;
    vec3 dzdu = (function(uv+vec2(delta,0))-function(uv-vec2(delta,0)))/2.0/delta;
    vec3 dzdv = (function(uv+vec2(0,delta))-function(uv-vec2(0,delta)))/2.0/delta;
    return cross(dzdu,dzdv);
}

out vec4 position;
out vec3 normal;
out vec3 lightDirection;
void main()
{
    position = vec4(function(inPosition),1.0);
    normal = normalDiff(inPosition);
    //normal = gl_NormalMatrix*normal;
    vec4 lightPosition = vec4(-10.0,5.0,2.0,1.0);
    vec4 objectPosition = modelView * position;
    lightDirection = lightPosition.xyz - objectPosition.xyz;
    gl_Position= mat*position;
    vertColor = vec3(1.0);
}