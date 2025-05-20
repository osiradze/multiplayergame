precision highp float;

varying float v_brightness;


void main() {
    gl_FragColor = vec4(v_brightness, v_brightness, v_brightness, 1.0);
}