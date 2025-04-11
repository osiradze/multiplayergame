precision highp float;

varying vec3 v_color;
varying float v_isDead;


void main() {
    if(v_isDead == 1.0) {
        discard;
    }
    gl_FragColor = vec4(v_color, 1.0);
}