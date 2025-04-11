#version 310 es
layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (std430, binding = 0) buffer InputOutputBuffer {
    float data[];
} inputOutput;

uniform uint u_floats_per_vertex;
uniform vec2 u_player_position;
uniform float u_delta_time;
uniform bool u_push;

float getDistance(vec2 p1, vec2 p2) {
    return length(p2 - p1);
}

void main() {
    uint index = gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x * u_floats_per_vertex;

    float x = inputOutput.data[index];
    float y = inputOutput.data[index + 1u];

    float isDead = inputOutput.data[index + 7u];
    if(isDead == 1.0) {
        return;
    }

    // addVelocity
    inputOutput.data[index] += inputOutput.data[index + 5u];
    inputOutput.data[index + 1u] += inputOutput.data[index + 6u];

    if(inputOutput.data[index + 5u] > 0.001){
        inputOutput.data[index + 5u] *= 0.98;
    }
    if(inputOutput.data[index + 6u] > 0.001){
        inputOutput.data[index + 6u] *= 0.98;
    }

    float distance = getDistance(u_player_position, vec2(x, y));
    if(distance < 0.3) {
        // set velocity
        float powDistance = pow(distance, 2.0);
        vec2 vector;
        if(u_push) {
            vector = vec2(x - u_player_position.x, y - u_player_position.y);
        } else {
            vector = vec2(u_player_position.x - x, u_player_position.y - y);
        }
        inputOutput.data[index + 5u] = vector.x / powDistance * u_delta_time * 0.1;
        inputOutput.data[index + 6u] = vector.y / powDistance * u_delta_time * 0.1;

        if(!u_push && distance < 0.03) {
            inputOutput.data[index + 7u] = 1.0;
        }
    }

}