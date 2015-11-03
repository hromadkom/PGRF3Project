package utils;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;

public class G3OGLTexture {
	protected GL3 gl;
	public Texture texture;

	public G3OGLTexture(GL3 gl, String fileName) {
		this.gl = gl;
		try {
			System.out.print("Loading texture " + fileName + " ... ");
			texture = TextureIO.newTexture(new File(fileName), true);
		} catch (IOException e) {
			System.out.println("failed");
			System.out.println(e.getMessage());
		}
		if (texture != null)
			System.out.println("OK");

	}

	public void bind(int shaderProgram, String name, int slot) {
		if (texture == null) return;
		gl.glGenerateMipmap(GL.GL_TEXTURE_2D);
		gl.glActiveTexture(GL3.GL_TEXTURE0 + slot);
		texture.bind(gl);
		gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, name), slot);
	}
}
