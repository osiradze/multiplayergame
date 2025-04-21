precision highp float;

uniform sampler2D u_texture;
varying vec4 v_texture_coordinates;
varying float v_isAlive;

void main() {

    if(v_isAlive == 0.0) {
        discard;
    }
    gl_FragColor = vec4(1.0,0.0,0.0, 1.0);
    vec2 texCoord = v_texture_coordinates.xy + gl_PointCoord * v_texture_coordinates.zw;
    vec4 pixel = texture2D(u_texture, texCoord);

    if(pixel.a < 0.1) {
        //gl_FragColor = vec4(1.0,0.0,0.0, 1.0);
        discard;
    } else {
        gl_FragColor = pixel;
    }

}