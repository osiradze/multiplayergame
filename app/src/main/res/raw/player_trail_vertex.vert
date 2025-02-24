layout(std430, binding = 0) buffer TrailBuffer {
    vec2 a_trail[30];
};

void main() {
    gl_Position = vec4(positions[gl_VertexID], 0.0, 1.0);
}

