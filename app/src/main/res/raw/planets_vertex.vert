precision highp float;

attribute vec2 a_position;
attribute float a_size;

uniform float u_ratio;
uniform vec2 u_camera;
uniform float u_screen_width;

attribute vec4 a_texture_coordinates;
varying vec4 texture_coordinates_pass;

attribute vec3 a_color;
varying vec3 color_pass;


void main() {
    texture_coordinates_pass = a_texture_coordinates;
    color_pass = a_color;
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