#version 310 es
layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (std430, binding = 0) buffer InputOutputBuffer {
    float data[];
} inputOutput;

uniform uint u_index;
uniform vec2 u_position;



void main() {
    uint floatIndex = gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x;

    if(floatIndex == u_index) {
        inputOutput.data[floatIndex] = u_position.x;
        inputOutput.data[floatIndex + 1u] = u_position.y;
    }
}