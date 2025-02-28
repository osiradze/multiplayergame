precision mediump float;
attribute vec2 a_position;
uniform float u_ratio;
uniform vec2 u_camera;


void main() {
    gl_PointSize = 10.0;
    gl_Position = vec4(
        a_position.x - u_camera.x,
        (a_position.y - u_camera.y) * u_ratio,
        0.0,
        1.0
    );
}