precision mediump float;

attribute vec2 a_position;
uniform float u_ratio;

uniform vec2 u_position;
uniform vec2 u_middlePoint;

uniform float u_rotation;
uniform vec2 u_velosity;

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
    vec2 position = rotateAround(a_position, u_middlePoint, u_rotation);
    position += position + u_position;
    gl_Position = vec4(
        position.x,
        position.y * u_ratio,
        0.0, 1.0
    );
}

