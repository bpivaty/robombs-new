uniform sampler2D colorMap;
uniform sampler2D normalMap;

varying vec3 lightDir;
varying vec3 eyeVec;
varying float attenuation;

void main()
{
    vec4 color = texture2D(colorMap, gl_TexCoord[0].st);
    vec3 bump = normalize(texture2D(normalMap, gl_TexCoord[0].st).rgb * 2.0 - 1.0);

    vec3 l = normalize(lightDir);
    float NdotL = clamp(dot(bump, l), 0.0, 1.0);
    vec4 diffuse = gl_LightSource[0].diffuse * NdotL * attenuation;

    vec3 r = reflect(-l, bump);
    float spec = pow(clamp(dot(r, normalize(eyeVec)), 0.0, 1.0), 32.0);
    vec4 specular = gl_LightSource[0].specular * spec * attenuation;

    vec4 ambient = gl_LightModel.ambient * gl_FrontMaterial.ambient;

    gl_FragColor = color * (ambient + diffuse) + specular;
    gl_FragColor.a = color.a * gl_Color.a;
}
