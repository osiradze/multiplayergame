precision highp float;

uniform sampler2D u_texture;
uniform bool u_drawLine;

varying vec4 v_texture_coordinates;
varying vec3 v_color;
varying float v_isAlive;

void main() {

    if (u_drawLine) {
        if (v_isAlive == 0.0) {
            discard;
        }
        // Draw simple white line fragment
        gl_FragColor = vec4(0.1);
        return;
    }

    vec2 texCoord = v_texture_coordinates.xy + gl_PointCoord * v_texture_coordinates.zw;
    vec4 pixel = texture2D(u_texture, texCoord);

    if (pixel.a < 0.1 || v_isAlive == 0.0) {
        discard;
    }
    pixel.rgb *= v_color;

    gl_FragColor = pixel;
}
