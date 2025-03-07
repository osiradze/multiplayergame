precision mediump float;
attribute vec2 a_position;

attribute float a_brightness;
varying float v_brightness;


void main() {
    v_brightness = a_brightness;
    gl_PointSize = 0.5;
    gl_Position = vec4(a_position.x, a_position.y, 0, 1.0);
}