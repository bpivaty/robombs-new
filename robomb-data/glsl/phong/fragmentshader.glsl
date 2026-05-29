varying vec3 normal, lightDir, eyeVec;
varying vec2 texCoord;

uniform sampler2D colorMap;
		
const float cos_outer_cone_angle = 0.8; // 36 degrees

void main (void)
{
	vec4 base = texture2D(colorMap, texCoord);
	vec4 final_color = (gl_FrontLightModelProduct.sceneColor * gl_FrontMaterial.ambient) + 
	(gl_LightSource[0].ambient * gl_FrontMaterial.ambient);

	vec3 L = normalize(lightDir);
	vec3 D = normalize(gl_LightSource[0].spotDirection);

	float cos_cur_angle = dot(-L, D);
	float cos_inner_cone_angle = gl_LightSource[0].spotCosCutoff;
	float cos_inner_minus_outer_angle = cos_inner_cone_angle - cos_outer_cone_angle;

	if (cos_cur_angle > cos_inner_cone_angle) 
	{
		vec3 N = normalize(normal);

		float lambertTerm = max( dot(N,L), 0.0);
		if(lambertTerm > 0.0)
		{
			final_color += gl_LightSource[0].diffuse * gl_FrontMaterial.diffuse * lambertTerm;	

			vec3 E = normalize(eyeVec);
			vec3 R = reflect(-L, N);
			float specular = clamp(pow( max(dot(R, E), 0.0), gl_FrontMaterial.shininess ),0.001,1.0);
			final_color += gl_LightSource[0].specular * gl_FrontMaterial.specular * specular;	
		}
	}
	else if( cos_cur_angle > cos_outer_cone_angle )
	{
		float falloff = (cos_cur_angle - cos_outer_cone_angle) / cos_inner_minus_outer_angle;

		vec3 N = normalize(normal);

		float lambertTerm = max( dot(N,L), 0.0);
		if(lambertTerm > 0.0)
		{
			final_color += gl_LightSource[0].diffuse * gl_FrontMaterial.diffuse * lambertTerm * falloff;	

			vec3 E = normalize(eyeVec);
			vec3 R = reflect(-L, N);
			float specular = clamp(pow( max(dot(R, E), 0.0), gl_FrontMaterial.shininess ),0.001,1.0);
			final_color += gl_LightSource[0].specular * gl_FrontMaterial.specular * specular * falloff;	
		}
	}

	
	gl_FragColor = final_color*2*base;
	gl_FragColor.a=0.45;
}