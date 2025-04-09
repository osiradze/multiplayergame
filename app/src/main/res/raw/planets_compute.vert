/*
 Running as many work as there is planet, and each work will be working with each planet.
*/


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
    uint index = (gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x) * u_floats_per_vertex;

    if(inputOutput.data[index + 10u] == 1.0) {
        // If the planet is already collided with, skip the collision check
        return;
    }
    float x = inputOutput.data[index];
    float y = inputOutput.data[index + 1u];
    float size = inputOutput.data[index + 2u];
    float textureCoordX = inputOutput.data[index + 3u];
    float textureCoordY = inputOutput.data[index + 4u];


    float distance = getDistance(u_player_position, vec2(x, y));
    if(distance < size / 2.2) {
        resultBuffer.result[0] = 1.0; // indicates that the player is colliding with the planet
        resultBuffer.result[1] = x;
        resultBuffer.result[2] = y;
        resultBuffer.result[3] = size;
        resultBuffer.result[4] = textureCoordX;
        resultBuffer.result[5] = textureCoordY;
        resultBuffer.result[6] = inputOutput.data[index + 6u]; // colorR
        resultBuffer.result[7] = inputOutput.data[index + 7u]; // colorG
        resultBuffer.result[8] = inputOutput.data[index + 8u]; // colorB

        inputOutput.data[index + 10u] = 1.0; // indicates that the player is collided with the planet
    }




}