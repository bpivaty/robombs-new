uniform sampler2D colorMap;

varying vec3 normal;
varying vec3 lightDir;
varying vec3 eyeVec;

void main()
{
    vec4 color = texture2D(colorMap, gl_TexCoord[0].st);
    vec3 n = normalize(normal);
    vec3 l = normalize(lightDir);
    float NdotL = clamp(dot(n, l), 0.0, 1.0);
    vec4 diffuse = gl_LightSource[0].diffuse * NdotL;
    vec3 r = reflect(-l, n);
    float spec = pow(clamp(dot(r, normalize(eyeVec)), 0.0, 1.0), 32.0);
    vec4 specular = gl_LightSource[0].specular * spec;
    vec4 ambient = gl_FrontLightProduct[0].ambient + gl_LightModel.ambient;
    gl_FragColor = color * (ambient + diffuse) + specular;
    gl_FragColor.a = color.a * gl_Color.a;
}
