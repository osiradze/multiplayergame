precision highp float;

uniform sampler2D u_texture;
varying vec4 texture_coordinates_pass;
varying vec3 color_pass;



void main() {
    vec2 texCoord = texture_coordinates_pass.xy + gl_PointCoord * texture_coordinates_pass.zw;
    vec4 pixel = texture2D(u_texture, 1.0 - texCoord);

    if(pixel.a == 0.0) {
        discard;
    } else {

        pixel.r *= color_pass.x;
        pixel.g *= color_pass.y;
        pixel.b *= color_pass.z;

        vec3 luminance = vec3(0.299, 0.587, 0.114);
        float gray = dot(pixel.rgb, luminance);

        gl_FragColor = vec4(vec3(gray), pixel.a);
    }
}