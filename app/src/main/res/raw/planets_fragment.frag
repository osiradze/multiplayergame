precision highp float;

uniform sampler2D u_texture;
varying vec4 v_texture_coordinates;
varying vec3 v_color;
varying float v_collision;



void main() {

    vec2 texCoord = v_texture_coordinates.xy + gl_PointCoord * v_texture_coordinates.zw;
    vec4 pixel = texture2D(u_texture, texCoord);

    if(pixel.a == 0.0) {
        discard;
    } else {

        if(v_collision == 1.0) {
            gl_FragColor = pixel;
        } else {
            pixel.r *= v_color.x;
            pixel.g *= v_color.y;
            pixel.b *= v_color.z;

            vec3 luminance = vec3(0.299, 0.587, 0.114);
            float gray = dot(pixel.rgb, luminance);

            gl_FragColor = vec4(vec3(gray), pixel.a);
        }

    }
}