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
uniform float u_delta_time;
uniform uint u_reader_offset;

float getDistance(vec2 p1, vec2 p2) {
    return length(p2 - p1);
}

void main() {

    uint aseteroidNumber = (gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x);
    uint index = aseteroidNumber * u_floats_per_vertex;

    // position of float where it says if the asteroid is alive
    uint isAliveIndex = u_floats_per_vertex - 1u;

    // if asteroid momory block is not alive return (it's last float)
    if(inputOutput.data[index + isAliveIndex] != 1.0) {
        return;
    }

    // addVelocity
    inputOutput.data[index] += inputOutput.data[index + 2u] * u_delta_time;
    inputOutput.data[index + 1u] += inputOutput.data[index + 3u] * u_delta_time;


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

    float colorR = inputOutput.data[index + 9u];
    float colorG = inputOutput.data[index + 10u];
    float colorB = inputOutput.data[index + 11u];


    float distance = getDistance(u_player_position, vec2(thisAsteroidPosition.x, thisAsteroidPosition.y));

   /** // if asteroid is too far away, destroy it
    if(abs(thisAsteroidPosition.x - u_player_position.x) > 4.0) {
        inputOutput.data[index] -= (thisAsteroidPosition.x - u_player_position.x) * 2.0; // mark the other asteroid as not alive
        return;
    }

    if(abs(thisAsteroidPosition.y - u_player_position.y) > 4.0) {
        inputOutput.data[index + 1u] -= (thisAsteroidPosition.y - u_player_position.y) * 2.0; // mark the other asteroid as not alive
        return;
    }

    if(distance < thisAsteroidSize / 1.8) {
        inputOutput.data[index + isAliveIndex] = 0.0; // mark the other asteroid as not alive

        resultBuffer.result[u_reader_offset] = 1.0; // indicates that the colliding happened
        resultBuffer.result[u_reader_offset + 1u] = thisAsteroidPosition.x;
        resultBuffer.result[u_reader_offset + 2u] = thisAsteroidPosition.y;
        resultBuffer.result[u_reader_offset + 3u] = thisAsteroidSize;

        resultBuffer.result[u_reader_offset + 4u] = textureCoordX;
        resultBuffer.result[u_reader_offset + 5u] = textureCoordY;

        resultBuffer.result[u_reader_offset + 6u] = colorR;
        resultBuffer.result[u_reader_offset + 7u] = colorG;
        resultBuffer.result[u_reader_offset + 8u] = colorB;

    }
    for(uint otherAsteroid = 0u; otherAsteroid < planetNumber; otherAsteroid++){
        if(otherAsteroid != aseteroidNumber) {
            uint otherAsteroidIndex = otherAsteroid * u_floats_per_vertex;
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
                float speedX = (otherAsteroidPosition.x - thisAsteroidPosition.x) * 0.5;
                float speedY = (otherAsteroidPosition.y - thisAsteroidPosition.y) * 0.5;
                inputOutput.data[otherAsteroidIndex + 2u] = speedX;
                inputOutput.data[otherAsteroidIndex + 3u] = speedY;
            }
        }
    }*/
}