precision mediump float;

uniform sampler2D u_texture;
varying vec4 texture_coordinates_pass;



void main() {
    vec2 texCoord = texture_coordinates_pass.xy + gl_PointCoord * texture_coordinates_pass.zw;
    vec4 pixel = texture2D(u_texture, 1.0 - texCoord);

    if(pixel.a == 0.0) {
        discard;
    } else {
        pixel.a = 0.8;
        gl_FragColor = pixel;
    }
}