#version 310 es
layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (std430, binding = 0) buffer InputOutputBuffer {
    float data[];
} inputOutput;

uniform uint floats_per_vertex;
uniform vec2 u_camera;
float border = 2.0;


void runForIndex(uint index) {
    inputOutput.data[index] += inputOutput.data[index + 2u];
    if(inputOutput.data[index] > border) {
        inputOutput.data[index] = -border;
    }
    if(inputOutput.data[index] < -border) {
        inputOutput.data[index] = border;
    }
}

void main() {
    uint index = (gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x) * floats_per_vertex;
    // X
    runForIndex(index);
    // Y
    runForIndex(index + 1u);
}

