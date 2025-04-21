precision highp float;

attribute vec2 a_position;
attribute float a_size;

uniform float u_ratio;
uniform vec2 u_camera;
uniform float u_screen_width;

attribute vec4 a_texture_coordinates;
varying vec4 v_texture_coordinates;

attribute float a_isAlive;
varying float v_isAlive;

void main() {
    v_isAlive = a_isAlive;
    v_texture_coordinates = a_texture_coordinates;
    gl_PointSize = a_size * u_screen_width;

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