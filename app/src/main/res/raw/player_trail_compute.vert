#version 310 es
layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (std430, binding = 0) buffer InputOutputBuffer {
    float data[];
} inputOutput;

uniform uint u_index;
uniform vec2 u_position;
uniform uint u_floatsPerVertex;



void main() {
    uint floatIndex = gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x;

    uint dataSize = uint(inputOutput.data.length);
    if(floatIndex == 0u && u_floatsPerVertex != 0u)  {
        for (uint i = 0u; i < dataSize; i += u_floatsPerVertex) {
            if(i + u_floatsPerVertex < dataSize) {
                inputOutput.data[i] = inputOutput.data[i + u_floatsPerVertex];
                inputOutput.data[i + 1u] = inputOutput.data[i + u_floatsPerVertex + 1u];
            } else {
                inputOutput.data[i] = u_position.x;
                inputOutput.data[i + 1u] = u_position.y;
            }
        }
    }


}