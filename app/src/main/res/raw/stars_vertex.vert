precision mediump float;
attribute vec4 a_position;


void main() {
    gl_PointSize = 0.5;
    gl_Position = vec4(a_position.x, a_position.y, 0, 1.0);
}