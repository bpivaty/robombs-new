varying vec3 normal, lightDir, eyeVec;
varying vec2 texCoord;	

void main()
{	
	normal = gl_NormalMatrix * gl_Normal;
		
	vec3 vVertex = vec3(gl_ModelViewMatrix * gl_Vertex);
	lightDir = vec3(gl_LightSource[0].position.xyz - vVertex);
	eyeVec = -vVertex;
	
	gl_Position = ftransform();
	texCoord = gl_MultiTexCoord0.xy;
}