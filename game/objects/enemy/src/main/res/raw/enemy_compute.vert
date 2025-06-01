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

    // position of float where it says if the enemy is alive
    uint isAliveIndex = u_floats_per_vertex - 1u;

    // if enemy momory block is not alive return (it's last float)
    if(inputOutput.data[index + isAliveIndex] == 2.0) {
        return;
    }
    inputOutput.data[index + isAliveIndex] = 1.0;


    float speed = 0.6; // speed of the enemy

    // addVelocity
    inputOutput.data[index] += inputOutput.data[index + 2u] * u_delta_time * speed;
    inputOutput.data[index + 1u] += inputOutput.data[index + 3u] * u_delta_time * speed;


    float textureCoordX = inputOutput.data[index + 5u];
    float textureCoordY = inputOutput.data[index + 6u];

    float colorR = inputOutput.data[index + 9u];
    float colorG = inputOutput.data[index + 10u];
    float colorB = inputOutput.data[index + 11u];

    vec2 vector = normalize(vec2(
        u_player_position.x - inputOutput.data[index],
        u_player_position.y - inputOutput.data[index + 1u]
    ));

    inputOutput.data[index + 2u] = vector.x;
    inputOutput.data[index + 3u] = vector.y;

    float distance = getDistance(
        vec2(inputOutput.data[index], inputOutput.data[index + 1u]),
        u_player_position
    );

    if(distance < 0.1) {
        inputOutput.data[index + isAliveIndex] = 2.0; // mark as destroyed
        return;
    }

}