precision highp float;

attribute vec2 a_position;
uniform float u_ratio;
uniform vec2 u_camera;


uniform vec2 u_position;
uniform vec2 u_middlePoint;

uniform vec2 u_direction;

float angleBetween(vec2 v1, vec2 v2) {
    float dotProd = dot(normalize(v1), normalize(v2));
    return acos(clamp(dotProd, -1.0, 1.0)); // Clamp to prevent precision errors
}

vec2 rotateAround(vec2 point, vec2 center, float angle) {
    float c = cos(angle);
    float s = sin(angle);

    // Translate point to origin
    vec2 translated = point - center;

    // Rotate using matrix multiplication
    vec2 rotated = vec2(
        translated.x * c - translated.y * s,
        translated.x * s + translated.y * c
    );

    // Translate back to original position
    return rotated + center;
}


void main() {
    float angle = angleBetween(u_direction, vec2(0.0, 1.0)); // 0.0, 1.0 because the player is facing up by default
    if (u_direction.x > 0.0) {
        angle = -angle;
    }
    vec2 position = rotateAround(a_position, u_middlePoint, angle);
    position += position + u_position;
    gl_Position = vec4(
        position.x - u_camera.x,
        (position.y - u_camera.y) * u_ratio,
        0.0, 1.0
    );
}

