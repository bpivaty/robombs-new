varying vec3 normal;
varying vec3 lightDir;
varying vec3 eyeVec;

void main()
{
    normal = normalize(gl_NormalMatrix * gl_Normal);
    vec4 vert = gl_ModelViewMatrix * gl_Vertex;
    lightDir = vec3(gl_LightSource[0].position.xyz - vert.xyz);
    eyeVec = -vert.xyz;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_FrontColor = gl_Color;
    gl_Position = ftransform();
}
