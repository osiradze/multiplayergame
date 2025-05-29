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
    if (v_isAlive != 1.0) {
        discard;
    }

    vec2 texCoord = v_texture_coordinates.xy + gl_PointCoord * v_texture_coordinates.zw;
    vec4 pixel = texture2D(u_texture, texCoord);

    if (pixel.a < 0.1) {
        discard;
    }

    pixel.rgb *= v_color;

    // Optional grayscale toggle:
    // gl_FragColor = toGrayscale(pixel);

    gl_FragColor = pixel;
}
