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

float rand(vec2 seed) {
    // This magic number is derived from common hash functions
    return fract(sin(dot(seed, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    uint index = gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x * u_floats_per_vertex;

    float x = inputOutput.data[index];
    float y = inputOutput.data[index + 1u];

    float r = inputOutput.data[index + 2u];
    float g = inputOutput.data[index + 3u];
    float b = inputOutput.data[index + 4u];

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
    float actDistance;
    if(u_push) {
        actDistance = 0.4;
    } else {
        actDistance = 0.5;
    }
    if(distance < actDistance) {
        // set velocity
        float powDistance = pow(distance, 2.0);
        vec2 vector;
        float power;
        float randValue = 0.4 + rand(vec2(g, r)) * 0.6;
        if(u_push) {
            power = 0.05;
            vector = vec2(x - u_player_position.x, y - u_player_position.y);
        } else {
            power = 0.3;
            randValue = 1.0;
            vector = vec2(u_player_position.x - x, u_player_position.y - y);
        }
        inputOutput.data[index + 5u] = vector.x / powDistance * u_delta_time * power * randValue;
        inputOutput.data[index + 6u] = vector.y / powDistance * u_delta_time * power * randValue;

        if(!u_push && distance < 0.06) {
            inputOutput.data[index + 7u] = 1.0;
        }
    }

}