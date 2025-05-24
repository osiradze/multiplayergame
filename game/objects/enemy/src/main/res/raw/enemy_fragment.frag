precision highp float;

uniform sampler2D u_texture;

varying vec4 v_texture_coordinates;
varying float v_isAlive;
varying vec3 v_color;


void main() {
    if (v_isAlive == 0.0) {
        discard;
    }

    vec2 texCoord = v_texture_coordinates.xy + gl_PointCoord * v_texture_coordinates.zw;
    vec4 pixel = texture2D(u_texture, texCoord);

    if (pixel.a < 0.1) {
        discard;
    }

    pixel.rgb *= v_color;

    gl_FragColor = pixel;
}
