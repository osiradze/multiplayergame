#version 310 es
layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (std430, binding = 0) buffer InputOutputBuffer {
    float data[];
} inputOutput;

layout(std430, binding = 1) buffer ResultBuffer {
    float[] result;
} resultBuffer;

uniform uint u_floats_per_vertex;
uniform vec2 u_player_position;

float getDistance(vec2 p1, vec2 p2) {
    return length(p2 - p1);
}

void main() {
    uint index = gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x;

    if(index % u_floats_per_vertex == 0u) {
        float x = inputOutput.data[index];
        float y = inputOutput.data[index + 1u];
        float size = inputOutput.data[index + 2u];


        float distance = getDistance(u_player_position, vec2(x, y));
        if(distance < size / 2.0) {
            resultBuffer.result[0] = u_player_position.x - x;
            resultBuffer.result[1] = u_player_position.y - y;
            resultBuffer.result[2] = 1.0; // indicates that the player is colliding with the planet
            inputOutput.data[index + 10u] = 1.0; // indicates that the player is collided with the planet
        }


    }

}