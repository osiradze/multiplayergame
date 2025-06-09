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

void addVelosity(uint index) {
    float speed = 0.8; // speed of the enemy

    // addVelocity
    inputOutput.data[index] += inputOutput.data[index + 2u] * u_delta_time * speed;
    inputOutput.data[index + 1u] += inputOutput.data[index + 3u] * u_delta_time * speed;

    vec2 vector = normalize(
        vec2(
            u_player_position.x - inputOutput.data[index],
            u_player_position.y - inputOutput.data[index + 1u]
        )
    );
    inputOutput.data[index + 2u] = vector.x;
    inputOutput.data[index + 3u] = vector.y;
}

void collisionBetweenEnemies(
    uint currentEnemyIndex,
    vec2 currentEnemyPosition,
    float currentEnemySize,
    uint isAliveIndex
) {
    uint enemyNumber = uint(inputOutput.data.length()) / u_floats_per_vertex;


    for(uint otherEnemy = 0u; otherEnemy < enemyNumber; otherEnemy++){
        if(otherEnemy != enemyNumber) {
            uint otherEnemyIndex = otherEnemy * u_floats_per_vertex;
            float isOtherEnemyAlive = inputOutput.data[otherEnemy * u_floats_per_vertex + isAliveIndex];
            if(isOtherEnemyAlive != 1.0) {
                continue; // Skip if the other enemy is not alive
            }

            vec2 otherEnemyPosition = vec2(
            inputOutput.data[otherEnemy * u_floats_per_vertex],
            inputOutput.data[otherEnemy * u_floats_per_vertex + 1u]
            );
            vec2 otherEnemyVelocity = vec2(
            inputOutput.data[otherEnemy * u_floats_per_vertex + 2u],
            inputOutput.data[otherEnemy * u_floats_per_vertex + 3u]
            );

            float otherAsteroidSize = inputOutput.data[otherEnemy * u_floats_per_vertex + 4u];
            float distance = getDistance(otherEnemyPosition, currentEnemyPosition);
            float minAvaliableDistance = (currentEnemySize + otherAsteroidSize) * 0.5;
            if(distance < minAvaliableDistance) {
                float speedX = (otherEnemyPosition.x - currentEnemyPosition.x);
                float speedY = (otherEnemyPosition.y - currentEnemyPosition.y);
                inputOutput.data[otherEnemyIndex + 2u] += speedX;
                inputOutput.data[otherEnemyIndex + 3u] += speedY;
            }
        }
    }
}


void main() {
    uint currentEnemy = (gl_NumWorkGroups.x * gl_WorkGroupID.y + gl_WorkGroupID.x);
    uint index = currentEnemy * u_floats_per_vertex;
    uint isAliveIndex = u_floats_per_vertex - 1u;
    vec2 currentEnemyPosition = vec2(
        inputOutput.data[index],
        inputOutput.data[index + 1u]
    );
    float currentEnemySize = inputOutput.data[index + 4u];

    // if enemy momory block is not alive return (it's last float)
    if(inputOutput.data[index + isAliveIndex] == 2.0) {
        return;
    }
    inputOutput.data[index + isAliveIndex] = 1.0;

    addVelosity(index);

    float distance = getDistance(
        vec2(inputOutput.data[index], inputOutput.data[index + 1u]),
        u_player_position
    );

    if(distance < 0.1) {
        inputOutput.data[index + isAliveIndex] = 2.0; // mark as destroyed
        return;
    }

    collisionBetweenEnemies(
        currentEnemy,
        currentEnemyPosition,
        currentEnemySize,
        isAliveIndex
    );
}