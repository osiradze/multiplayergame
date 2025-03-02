precision mediump float;

attribute vec2 a_position;
uniform float u_ratio;
uniform vec2 u_camera;

attribute vec4 a_texture_coordinates;
varying vec4 texture_coordinates_pass;


void main() {
    texture_coordinates_pass = a_texture_coordinates;
    gl_PointSize = 100.0;
    gl_Position = vec4(
        a_position.x - u_camera.x,
        (a_position.y - u_camera.y) * u_ratio,
        0.0, 1.0
    );
}