#version 330
in vec2 inPosition;
uniform vec3 eyePosition;
uniform mat4 mat;
uniform mat4 modelView;
const float PI=3.1415926;
    vec3 function (vec2 uv){
        float azimuth = uv.x*2.0*PI;
        float zenith = (uv.y * PI) - (PI/2.0);
        vec3 position;

        position.x=cos(azimuth)*cos(zenith);
        position.y=sin(azimuth)*cos(zenith);
        position.z=sin(zenith);
        return position;
}
vec3 normalDiff (vec2 uv){
    float delta = 0.001;
    vec3 dzdu = (function(uv+vec2(delta,0))-function(uv-vec2(delta,0)));
    vec3 dzdv = (function(uv+vec2(0,delta))-function(uv-vec2(0,delta)));
    return cross(dzdu,dzdv);
}

mat3 tbn(vec2 uv){
    float delta = 0.01;
    vec3 pos = function(uv);
    vec3 p1 = vec3(pos.x+delta,pos.y,pos.z);
    vec3 p2= vec3(pos.x-delta,pos.y,pos.z);
    vec3 tangent = (p2 - p1);

    p1 = vec3(pos.x,pos.y+delta,pos.z);
    p2= vec3(pos.x,pos.y-delta,pos.z);
    vec3 bitangent = (p2 - p1);

    vec3 normal = cross(tangent,bitangent);
   // bitangent = cross(normal, tangent);
    return mat3(tangent, bitangent, normal);
}

out vec4 position;
out vec3 normal;
out vec3 lightDirection;
out vec3 viewDirection;
out float dist;
out vec2 textCoor;
out vec3 vertColor;

void main()
{
    mat3 TBN = tbn(inPosition);
    vec3 position = function(inPosition);
    normal = normalDiff(inPosition);
    //normal = gl_NormalMatrix*normal;
    vec3 lightPosition = vec3(5.0,5.0, 2.5);
    viewDirection = (eyePosition) - position;
    viewDirection = viewDirection * TBN;
    lightDirection = lightPosition - position;
    lightDirection = lightDirection * TBN;

    textCoor = vec2(inPosition.x,inPosition.y);
    //textCoor.y += 0.2*sin(2*PI*textCoor.x);
    dist = length(lightDirection);

    vertColor = normal;
    gl_Position= mat* vec4(position,1.0);
}