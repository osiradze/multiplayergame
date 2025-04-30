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
uniform bool u_destructible;
uniform uint u_reader_offset;

float getDistance(vec2 p1, vec2 p2) {
    return length(p2 - p1);
}

void setFirstPlanetPostisionToPlayerForLine(
        uint index
) {
    inputOutput.data[index] = u_player_position.x;
    inputOutput.data[index + 1u] = u_player_position.y;
    // kill planet
    inputOutput.data[index + 11u] = 1.0;
}

void main() {

    uint planetNumber = gl_WorkGroupID.x + gl_WorkGroupID.y * gl_NumWorkGroups.x;
    uint index = planetNumber * u_floats_per_vertex;

   /** if(inputOutput.data[index + 11u] == 1.0) {
        // If the planet is already collided with, skip the collision check
        return;
    }*/
    float x = inputOutput.data[index];
    float y = inputOutput.data[index + 1u];
    float size = inputOutput.data[index + 2u];
    float textureCoordX = inputOutput.data[index + 3u];
    float textureCoordY = inputOutput.data[index + 4u];


    float distance = getDistance(u_player_position, vec2(x, y));
    if(distance < size / 2.0 * 0.95) {
        resultBuffer.result[u_reader_offset] = 1.0; // indicates that the player is colliding with the planet
        resultBuffer.result[u_reader_offset + 1u] = x;
        resultBuffer.result[u_reader_offset + 2u] = y;
        resultBuffer.result[u_reader_offset + 3u] = size;

        resultBuffer.result[u_reader_offset + 4u] = textureCoordX;
        resultBuffer.result[u_reader_offset + 5u] = textureCoordY;

        resultBuffer.result[u_reader_offset + 6u] = inputOutput.data[index + 7u]; // colorR
        resultBuffer.result[u_reader_offset + 7u] = inputOutput.data[index + 8u]; // colorG
        resultBuffer.result[u_reader_offset + 8u] = inputOutput.data[index + 9u]; // colorB

        inputOutput.data[index + 10u] = 1.0; // indicates that the player is collided with the planet
        /**if(u_destructible){
            inputOutput.data[index + 11u] = 1.0; // indicates that the planet is destroyed
        }*/
    }

    if(planetNumber == 0u) {
        // Set the first planet's position to the player's position
        //setFirstPlanetPostisionToPlayerForLine(index);
    }






}