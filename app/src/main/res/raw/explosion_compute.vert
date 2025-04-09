#version 310 es
layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (std430, binding = 0) buffer InputOutputBuffer {
    float data[];
} inputOutput;

uniform uint u_floats_per_vertex;
uniform vec2 u_player_position;

float getDistance(vec2 p1, vec2 p2) {
    return length(p2 - p1);
}

void main() {
    uint index = gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x * u_floats_per_vertex;

    float x = inputOutput.data[index];
    float y = inputOutput.data[index + 1u];

    // addVelocity
    inputOutput.data[index] += inputOutput.data[index + 5u];
    inputOutput.data[index + 1u] += inputOutput.data[index + 6u];

    if(inputOutput.data[index + 5u] > 0.001){
        inputOutput.data[index + 5u] *= 0.99;
    }
    if(inputOutput.data[index + 6u] > 0.001){
        inputOutput.data[index + 6u] *= 0.99;
    }

    float distance = getDistance(u_player_position, vec2(x, y));
    if(distance < 0.4) {
        float powDistance = pow(distance, 2.0);
        inputOutput.data[index + 5u] = (x - u_player_position.x) / powDistance / 1000.0;
        inputOutput.data[index + 6u] = (y - u_player_position.y) / powDistance / 1000.0;
    }

}