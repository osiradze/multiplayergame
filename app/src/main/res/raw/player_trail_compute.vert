#version 310 es
layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (std430, binding = 0) buffer InputOutputBuffer {
    float data[];
} inputOutput;

uniform uint u_index;
uniform vec2 u_position;



void main() {
    uint floatIndex = gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x;

    uint dataSize = uint(inputOutput.data.length);
    if(floatIndex == 0u)  {
        for (uint i = 0u; i < dataSize; i += 2u) {
            if(i + 2u < dataSize) {
                inputOutput.data[i] = inputOutput.data[i + 2u];
                inputOutput.data[i + 1u] = inputOutput.data[i + 3u];
            } else {
                inputOutput.data[i] = u_position.x;
                inputOutput.data[i + 1u] = u_position.y;
            }
        }
    }


}