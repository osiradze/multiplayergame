precision highp float;

uniform sampler2D u_texture;
uniform bool u_drawLine;

varying vec4 v_texture_coordinates; // xy = offset, zw = size
varying vec3 v_color;               // color tint
varying float v_isDestroyed;        // 1.0 = destroyed

void main() {
    gl_FragColor = vec4(0.3);
    if (u_drawLine) {
        if (v_isDestroyed > 0.8) {
            discard;
        }
        // Draw simple white line fragment

        return;
    }

    // Calculate subtexture coordinates
    vec2 texCoord = v_texture_coordinates.xy + gl_PointCoord * v_texture_coordinates.zw;

    // Sample texture
    vec4 pixel = texture2D(u_texture, texCoord);

    // Discard if transparent or destroyed
    if (pixel.a < 0.1 || v_isDestroyed == 1.0) {
        discard;
    }

    // Apply planet color tint
    pixel.rgb *= v_color;

    gl_FragColor = pixel;
}
