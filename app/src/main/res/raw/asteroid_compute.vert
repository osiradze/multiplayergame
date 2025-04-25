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

uint findNextDeadAsteroidMemory() {
    uint isAliveIndex = u_floats_per_vertex - 1u;
    uint numberOfPlanets = uint(inputOutput.data.length()) / u_floats_per_vertex;
    for(uint i = 0u; i < numberOfPlanets; i++) {
        uint startIndex = i * u_floats_per_vertex;
        if(inputOutput.data[startIndex + isAliveIndex] != 1.0) {
            return startIndex;
        }
    }
}

void handleAsteroidCreation(
      uint aseteroidNumber,
      uint index,
      uint requestedIndex
) {
    uint placeToCreateAsteroid = requestedIndex + 1u;
    uint isAliveIndex = u_floats_per_vertex - 1u;

    // check if creating asteroid is requested
    if(createAsteroidBuffer.request[u_floats_per_vertex] == 1.0){
        // check if the index is the one where we want to create the asteroid
        if(aseteroidNumber == uint(createAsteroidBuffer.request[placeToCreateAsteroid])){
            //is is alive don't touch it
            uint startIndexOfEmptyAsteroid = index;
            if(inputOutput.data[index + isAliveIndex] == 1.0) {
                startIndexOfEmptyAsteroid = findNextDeadAsteroidMemory();
                return;
            }
            // write data
            for(uint i = 0u; i < u_floats_per_vertex; i++){
                inputOutput.data[startIndexOfEmptyAsteroid + i] = createAsteroidBuffer.request[uint(i)];
            }
        }
    }
}


void main() {

    uint aseteroidNumber = (gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x);
    uint index = aseteroidNumber * u_floats_per_vertex;

    // position of float where it says if the asteroid is alive
    uint isAliveIndex = u_floats_per_vertex - 1u;

    // position of float where it says creating asteroid requested is after the last float of the vertex
    uint requestIndex = u_floats_per_vertex;


    handleAsteroidCreation(
        aseteroidNumber,
        index,
        requestIndex
    );


    // if asteroid momory block is not alive return (it's last float)
    if(inputOutput.data[index + isAliveIndex] != 1.0) {
        return;
    }

    // addVelocity
    vec2 normalized = normalize(vec2(inputOutput.data[index + 2u], inputOutput.data[index + 3u]));
    inputOutput.data[index] +=  normalized.x / 1000.0;
    inputOutput.data[index + 1u] += normalized.y / 1000.0;


    uint planetNumber = uint(inputOutput.data.length()) / u_floats_per_vertex;
    vec2 thisAsteroidPosition = vec2(
        inputOutput.data[index],
        inputOutput.data[index + 1u]
    );
    vec2 thisAsteroidVelocity = vec2(
        inputOutput.data[index + 2u],
        inputOutput.data[index + 3u]
    );


    float thisAsteroidSize = inputOutput.data[index + 4u];
    float textureCoordX = inputOutput.data[index + 5u];
    float textureCoordY = inputOutput.data[index + 6u];

    float distance = getDistance(u_player_position, vec2(thisAsteroidPosition.x, thisAsteroidPosition.y));

    // if asteroid is too far away, destroy it
    if(distance > 7.0) {
        inputOutput.data[index + isAliveIndex] = 0.0; // mark the other asteroid as not alive
        return;
    }

    if(distance < thisAsteroidSize / 1.8) {
        inputOutput.data[index + isAliveIndex] = 0.0; // mark the other asteroid as not alive

        resultBuffer.result[0] = 1.0; // indicates that the colliding happened
        resultBuffer.result[1] = thisAsteroidPosition.x;
        resultBuffer.result[2] = thisAsteroidPosition.y;
        resultBuffer.result[3] = thisAsteroidSize;

        resultBuffer.result[4] = textureCoordX;
        resultBuffer.result[5] = textureCoordY;

    }
    for(uint otherAsteroid = 0u; otherAsteroid < planetNumber; otherAsteroid++){
        if(otherAsteroid != aseteroidNumber) {

            float isAlive = inputOutput.data[otherAsteroid * u_floats_per_vertex + isAliveIndex];
            if(isAlive != 1.0) {
                continue; // Skip if the other asteroid is not alive
            }

            vec2 otherAsteroidPosition = vec2(
                inputOutput.data[otherAsteroid * u_floats_per_vertex],
                inputOutput.data[otherAsteroid * u_floats_per_vertex + 1u]
            );
            vec2 otherAsteroidVelocity = vec2(
                inputOutput.data[otherAsteroid * u_floats_per_vertex + 2u],
                inputOutput.data[otherAsteroid * u_floats_per_vertex + 3u]
            );

            float otherAsteroidSize = inputOutput.data[otherAsteroid * u_floats_per_vertex + 4u];

            float distance = getDistance(otherAsteroidPosition, thisAsteroidPosition);
            float minAvaliableDistance = (thisAsteroidSize + otherAsteroidSize) / 2.2;
            if(distance < minAvaliableDistance) {
                uint otherAsteroidIndex = otherAsteroid * u_floats_per_vertex;
                //inputOutput.data[otherAsteroidIndex + u_floats_per_vertex - 1u] = 0.0; // mark the other asteroid as not alive

                inputOutput.data[otherAsteroidIndex + 2u] = (otherAsteroidPosition.x - thisAsteroidPosition.x);
                inputOutput.data[otherAsteroidIndex + 3u] = (otherAsteroidPosition.y - thisAsteroidPosition.y);
            }
        }
    }
}