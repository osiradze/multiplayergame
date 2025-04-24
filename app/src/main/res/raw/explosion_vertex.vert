precision highp float;

attribute vec2 a_position;
attribute vec3 a_color;
varying vec3 v_color;

attribute float a_isDead;
varying float v_isDead;

uniform float u_ratio;
uniform vec2 u_camera;

void main() {
    if(a_isDead == 1.0) {
        gl_Position = vec4(0.0, 0.0, 0.0, 1.0);
    }
    v_isDead = a_isDead;
    v_color = a_color;
    gl_PointSize = 2.0;

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