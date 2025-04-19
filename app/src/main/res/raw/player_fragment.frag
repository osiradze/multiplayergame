precision highp float;

uniform sampler2D u_texture;
varying vec2 v_texture_coordinates;

void main() {
    vec4 color = texture2D(u_texture, v_texture_coordinates);
    if(color.a < 0.01) {
        discard;
    }
    gl_FragColor = color;
}
