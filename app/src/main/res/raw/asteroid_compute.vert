#version 310 es

layout (local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

layout (std430, binding = 0) buffer InputOutputBuffer {
    float data[];
} inputOutput;

layout(std430, binding = 1) buffer InputBuffer  {
    float[] request;
} createAsteroidBuffer;

layout(std430, binding = 2) buffer ResultBuffer {
    float[] result;
} resultBuffer;

uniform uint u_floats_per_vertex;
uniform vec2 u_player_position;
uniform bool u_destructible;

float getDistance(vec2 p1, vec2 p2) {
    return length(p2 - p1);
}

void main() {
    uint index = (gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x) * u_floats_per_vertex;

    // position of float where it says creating asteroid requested is after the last float of the vertex
    uint requestIndex = u_floats_per_vertex;
    uint placeToAddAsteroid = requestIndex + 1u;

    // check if creating asteroid is requested
    if(createAsteroidBuffer.request[u_floats_per_vertex] == 1.0){
        // check if the index is the one where we want to create the asteroid
        if(index == uint(createAsteroidBuffer.request[placeToAddAsteroid])){
            for(uint i = 0u; i < u_floats_per_vertex; i++){
                inputOutput.data[index + i] = createAsteroidBuffer.request[uint(i)];
            }
        }
    }

    // if asteroid momory block is not alive return (it's last float)
    if(inputOutput.data[u_floats_per_vertex - 1u] == 1.0) {
        return;
    }

    // addVelocity
    inputOutput.data[index] += inputOutput.data[index + 2u];
    inputOutput.data[index + 1u] += inputOutput.data[index + 3u];


/** if(inputOutput.data[index + 9u] == 1.0) {
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

        resultBuffer.result[6] = inputOutput.data[index + 7u]; // colorR
        resultBuffer.result[7] = inputOutput.data[index + 8u]; // colorG
        resultBuffer.result[8] = inputOutput.data[index + 9u]; // colorB

        inputOutput.data[index + 10u] = 1.0; // indicates that the player is collided with the planet
        if(u_destructible){
            inputOutput.data[index + 11u] = 1.0; // indicates that the planet is destroyed
        }
    }*/




}