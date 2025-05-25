precision highp float;

attribute vec2 a_position;
attribute float a_size;

uniform float u_ratio;
uniform vec2 u_camera;
uniform float u_screen_width;

attribute vec4 a_texture_coordinates;
varying vec4 v_texture_coordinates;

attribute vec3 a_color;
varying vec3 v_color;

attribute float a_isDestroyed;
varying float v_isDestroyed;

uniform int u_counter;

uniform bool u_drawLine;

void pulseEffect() {
    float pulseSpeed = 0.03; // Speed of the pulse effect
    float pulseSize = 0.02; // Size of the pulse effect
    float pulse = 1.0 + pulseSize * sin(float(u_counter) * pulseSpeed); // oscillates between 0.8 and 1.2
    gl_PointSize = a_size * u_screen_width * pulse;
}

void main() {
    if(u_drawLine == false) {
        v_texture_coordinates = a_texture_coordinates;
        v_color = a_color;
        v_isDestroyed = a_isDestroyed;

        pulseEffect();
    }
    vec2 position = vec2(
    a_position.x - u_camera.x,
        (a_position.y - u_camera.y) * u_ratio
    );

    gl_Position = vec4(
        position.x,
        position.y,
        0.0, 1.0
    );
}