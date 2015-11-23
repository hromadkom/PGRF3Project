#version 330
in vec2 inPosition;
uniform vec3 eyePosition;
uniform mat4 mat;
uniform mat4 modelView;
uniform float time;

uniform sampler2D texture;

uniform int usedObject;
uniform int perVertexShading;
uniform int extendedMapping;

uniform vec3 lightPosition;
const float PI=3.1415926;

uniform vec3 spotDirection;
uniform float spotCutOff;
uniform int spotLightEnabled;
uniform float lightDownturn;

out vec3 position;
out vec3 normal;
out vec3 lightDirection;
out vec3 viewDirection;
out vec3 lightDirectionTBN;
out vec3 viewDirectionTBN;
out vec3 perVertexShades;

out float dist;
out vec2 textCoor;
out vec3 vertColor;

// objects
vec3 object_plain(vec2 uv){
    return vec3(uv,1.0);
}

vec3 object_hat (vec2 uv){
    float s = (uv.x*2.0)-1.0;
    float t = (uv.y*2.0)-1.0;
    float z = (cos(sqrt(20*pow(s,2.0)+20*pow(t,2.0))))/2.0;
    return vec3(uv,z);
}

vec3 object_torus(vec2 uv){
    float s = uv.x*2.0*PI;
    float t = uv.y*2.0*PI;

    vec3 position;

    position.x = 2*cos(s)+cos(t)*cos(s);
    position.y = 2*sin(s)+cos(t)*sin(s);
    position.z = sin(t);
    return position;
}

vec3 object_sphere(vec2 uv){
    float s = uv.x*2.0*PI;
    float t = (uv.y * PI) - (PI/2.0);
    vec3 position;

    position.x=cos(s)*cos(t);
    position.y=sin(s)*cos(t);
    position.z=sin(t);
    return position;
}

vec3 object_space_station(vec2 uv){
    float s = uv.y*1.5*PI;
    float t = uv.x*PI;
    float r = 1+0.5*sin(4*t);
    vec3 position;

    position.x=r * cos(s)*sin(t);
    position.y=r * sin(s)*sin(t);
    position.z=r * cos(t);
    return position;
}

vec3 object_my_spherical(vec2 uv){
    float s = uv.y*1.5*PI;
    float t = uv.x*PI;
    float r = 1+0.5*sin(4*t)+sin(s*4);
    vec3 position;

    position.x=r * cos(s)*sin(t);
    position.y=r * sin(s)*sin(t);
    position.z=r * cos(t);
    return position;
}

vec3 object_cylinder(vec2 uv){
    float s = uv.x*2.0*PI;
    float t = uv.y*3.0;
    vec3 position;

    position.x = cos(s);
    position.y = sin(s);
    position.z = t;

    return position;
}

vec3 object_crooked_pipe(vec2 uv){
    float s = uv.x * 2.0 * PI;
    float t = (uv.y * PI*2.0) - (PI);
    float r = 2+cos(2*t)*sin(s);
    vec3 position;

    position.x = r * cos(s);
    position.y = r * sin(s);
    position.z = t;

    return position;
}

vec3 object_my_cylindrical(vec2 uv){
    float s = uv.y * 2.0 * PI;
    float t = (uv.x * PI*3.0);
    float r = (1+max(sin(t*2)+sin(t/2)+sin(s/2),0))*1;
    float theta = s+4;
    vec3 position;

    position.x = r * cos(theta);
    position.y = r * sin(theta);
    position.z = 4-t;
    return position;
}

vec3 function (vec2 uv){
    vec3 pos;
    switch(usedObject){
        case 0:
            pos = object_plain(uv);
            break;
        case 1:
            pos = object_torus(uv);
            break;
        case 2:
            pos = object_sphere(uv);
            break;
        case 3:
            pos = object_space_station(uv);
            break;
        case 4:
            pos = object_my_spherical(uv);
            break;
        case 5:
            pos = object_cylinder(uv);
            break;
        case 6:
            pos = object_crooked_pipe(uv);
            break;
        case 7:
            pos = object_my_cylindrical(uv);
            break;
        case 8:
            pos = object_hat(uv);
            break;
        default:
            pos = object_sphere(uv);
            break;
    }
   return pos;
}
//normal
vec3 normalDiff (vec2 uv){
     float delta = 0.1;
     vec3 dzdu = (function(uv+vec2(delta,0))-function(uv-vec2(delta,0)));
     vec3 dzdv = (function(uv+vec2(0,delta))-function(uv-vec2(0,delta)));
     return cross(dzdu,dzdv);
}

// tbn matrix
mat3 tbn(vec2 uv){
    float delta = 0.001;
    vec3 x = (function(uv+vec2(delta,0))-function(uv-vec2(delta,0)));
    vec3 y = (function(uv+vec2(0,delta))-function(uv-vec2(0,delta)));
    vec3 z = cross(x,y);

    vec3 yy = cross(z,x);

    return mat3(normalize(x), normalize(yy), normalize(z));
}

//shadings
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


void main()
{
    position = function(inPosition);
    normal = normalDiff(inPosition);
    //normal = gl_NormalMatrix*normal;
    vec3 lightPos = lightPosition;
    viewDirection = eyePosition - position;
    lightDirection = lightPos - position;
    dist = length(lightDirection);

    viewDirectionTBN = viewDirection;
    lightDirectionTBN = lightDirection;
    if(extendedMapping == 1 && perVertexShading == 0){
        mat3 TBN = tbn(inPosition);
        viewDirectionTBN = viewDirection * TBN;
        lightDirectionTBN = lightDirection * TBN;
    }
    textCoor = vec2(inPosition.x,inPosition.y);
        //textCoor.y += 0.2*sin(2*PI*textCoor.x);
    gl_Position= mat* vec4(position,1.0);

    if(perVertexShading == 1){
   	    perVertexShades = shading(viewDirectionTBN, lightDirectionTBN, normal);
   	}
}