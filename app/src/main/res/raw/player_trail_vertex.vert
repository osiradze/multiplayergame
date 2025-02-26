precision mediump float;
attribute vec2 a_position;
uniform float u_ratio;



void main() {
    gl_PointSize = 10.0;
    gl_Position = vec4(
        a_position.x,
        a_position.y * u_ratio,
        0.0,
        1.0
    );
}