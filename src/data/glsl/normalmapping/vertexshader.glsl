uniform float invRadius;

attribute vec4 tangent;

varying vec3 lightDir;
varying vec3 eyeVec;
varying float attenuation;

void main()
{
    vec4 vert = gl_ModelViewMatrix * gl_Vertex;
    vec3 n = normalize(gl_NormalMatrix * gl_Normal);
    vec3 t = normalize(gl_NormalMatrix * tangent.xyz);
    vec3 b = cross(n, t) * tangent.w;

    // Transposed TBN: transforms vectors from eye space into tangent space
    mat3 tbn = mat3(t.x, b.x, n.x,
                    t.y, b.y, n.y,
                    t.z, b.z, n.z);

    vec3 toLight = vec3(gl_LightSource[0].position.xyz - vert.xyz);
    float dist = length(toLight);
    attenuation = clamp(1.0 - invRadius * dist, 0.0, 1.0);

    lightDir = tbn * toLight;
    eyeVec = tbn * (-vert.xyz);

    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_FrontColor = gl_Color;
    gl_Position = ftransform();
}
