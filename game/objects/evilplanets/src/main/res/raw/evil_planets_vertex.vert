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

attribute float a_isAlive;
varying float v_isAlive;

uniform int u_counter;

uniform bool u_drawLine;

void pulseEffect() {
    float size = a_size;
    if(a_isAlive == 0.0) {
        //size /= 2.0; // if the planet is dead, make it smaller
    }
    float pulseSpeed = 0.04; // Speed of the pulse effect
    float pulseSize = 0.01; // Size of the pulse effect
    float pulse = 1.0 + pulseSize * sin(float(u_counter) * pulseSpeed); // oscillates between 0.8 and 1.2
    gl_PointSize = size * u_screen_width * pulse;
}

void main() {
    if(u_drawLine != true) {
        v_texture_coordinates = a_texture_coordinates;
        v_color = a_color;
        v_isAlive = a_isAlive;
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