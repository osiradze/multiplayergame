precision mediump float;
attribute vec2 a_position;
attribute float a_camera_speed;

attribute float a_brightness;
varying float v_brightness;

uniform vec2 u_camera;
uniform float u_ratio;

vec2 wrapToNDC(vec2 pos) {
    return mod(pos + 1.0, 2.0) - 1.0;
}

void main() {
    v_brightness = a_brightness;
    gl_PointSize = a_brightness * 2.0;

    vec2 position = vec2(
        (a_position.x - u_camera.x * a_camera_speed),
        (a_position.y - u_camera.y * a_camera_speed) * u_ratio
    );

    vec2 wrapPosition = wrapToNDC(position);

    gl_Position = vec4(wrapPosition.x, wrapPosition.y, 0, 1.0);
}