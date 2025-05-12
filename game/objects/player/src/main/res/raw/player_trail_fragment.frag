precision highp float;

varying float v_alpha;


void main() {
    gl_FragColor = vec4(v_alpha,v_alpha, v_alpha, 1.0);
}
