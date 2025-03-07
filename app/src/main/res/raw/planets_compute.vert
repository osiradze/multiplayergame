#version 310 es
layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (std430, binding = 0) buffer InputOutputBuffer {
    float data[];
} inputOutput;

layout(std430, binding = 1) buffer ResultBuffer {
    float[] result;
} resultBuffer;

uniform uint floats_per_vertex;
uniform vec2 player_position;

void main() {
    uint index = gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x;

    if(index % floats_per_vertex == 0u) {
        float x = inputOutput.data[index];
        float y = inputOutput.data[index + 1u];

        if(index == 0u) {
            resultBuffer.result[0] = 1.0f;
            resultBuffer.result[1] = 2.0f;
            resultBuffer.result[2] = 3.0f;
        }

    }

}