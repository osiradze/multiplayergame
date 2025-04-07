precision highp float;

attribute vec2 a_position;
attribute vec3 a_color;
varying vec3 v_color;

uniform float u_ratio;
uniform vec2 u_camera;
uniform vec2 u_position;


void main() {
    v_color = a_color;
    gl_PointSize = 10.0;

    vec2 position = a_position + u_position;
    position = vec2(
    position.x - u_camera.x,
        (position.y - u_camera.y) * u_ratio
    );


    gl_Position = vec4(
        position.x,
        position.y,
        0.0, 1.0
    );
}