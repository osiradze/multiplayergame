#version 310 es
layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (std430, binding = 0) buffer InputOutputBuffer {
    float data[];
} inputOutput;

uniform uint floats_per_vertex;
uniform vec2 u_camera;
float border = 2.0;

void main() {
    uint index = gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x;
    bool conditionX = (index % floats_per_vertex == 0u);
    bool conditionY = (index % floats_per_vertex == 1u);
    if (conditionX || conditionY) {
        inputOutput.data[index] += inputOutput.data[index + 2u];
        if(inputOutput.data[index] > border) {
            inputOutput.data[index] = -border;
        }
        if(inputOutput.data[index] < -border) {
            inputOutput.data[index] = border;
        }
    }
}