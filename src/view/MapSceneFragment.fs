#version 460

//
// The code in this file is based on code from:
//   https://github.com/lwjglgamedev/lwjglbook
// The original code has been modified to suit this project.
//

in vec3 outColor;

out vec4 fragColor;

void main()
{
    fragColor = vec4(outColor, 1);
}
