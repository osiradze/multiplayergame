precision highp float;

uniform sampler2D u_texture;
varying vec4 v_texture_coordinates;
varying float v_isAlive;
varying vec3 v_color;


vec4 toGrayscale(vec4 color) {
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    return vec4(vec3(gray), color.a);
}

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
        pixel.r *= v_color.x;
        pixel.g *= v_color.y;
        pixel.b *= v_color.z;
        gl_FragColor = pixel;
        //gl_FragColor = toGrayscale(pixel);
    }

}