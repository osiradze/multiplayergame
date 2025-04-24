precision highp float;

uniform sampler2D u_texture;
varying vec4 v_texture_coordinates;
varying vec3 v_color;
varying float v_isDestroyed;
uniform bool u_drawLine;



void main() {
    if(u_drawLine) {
        float lineAlpha = 1.0;
        if(v_isDestroyed > 0.8) {
            discard;
        }
        gl_FragColor = vec4(lineAlpha, lineAlpha, lineAlpha, 1.0);
    }
    else {
        vec2 texCoord = v_texture_coordinates.xy + gl_PointCoord * v_texture_coordinates.zw;
        vec4 pixel = texture2D(u_texture, texCoord);

        if(pixel.a < 0.1) {
            //gl_FragColor = vec4(1.0,0.0,0.0, 1.0);
            discard;
        } else {

            pixel.r *= v_color.x;
            pixel.g *= v_color.y;
            pixel.b *= v_color.z;

            if(v_isDestroyed != 1.0) {
                gl_FragColor = pixel;
            } else {
                discard;
            }
        }
    }
}