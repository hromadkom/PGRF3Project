package utils;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;


public class OGLTextureCube {
	protected GL2 gl;
	public Texture texture;

	public static final String[] SUFFIXES_POS_NEG = { "posx", "negx", "posy", "negy", "posz", "negz" };
	public static final String[] SUFFIXES_POSITIVE_NEGATIVE = { "positive_x", "negative_x", "positive_y", "negative_y", "positive_z", "negative_z" };
	public static final String[] SUFFIXES_RIGHT_LEFT = { "right", "left", "bottom", "top", "front", "back" };
	private static final int[] targets = { GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
	                                         GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
	                                         GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
	                                         GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
	                                         GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
	                                         GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z };
	
	public OGLTextureCube(GL2 gl, String[] fileNames) {
		this.gl = gl;
		texture = TextureIO.newTexture(GL2.GL_TEXTURE_CUBE_MAP);
		readFiles(texture,fileNames);
		if (texture != null)
			System.out.println("OK");
	}

	public OGLTextureCube(GL2 gl, String fileName, String[] suffixes) {
		this.gl = gl;
		texture = TextureIO.newTexture(GL2.GL_TEXTURE_CUBE_MAP);
		
		String[] fileNames = new String[suffixes.length];    
		String baseName=fileName.substring(0,fileName.lastIndexOf('.'));
    	String suffix=fileName.substring(fileName.lastIndexOf('.')+1,fileName.length());
    	for (int i = 0; i < suffixes.length; i++) {
    		fileNames[i] = new String(baseName + suffixes[i] + "." + suffix);
	       }
    	readFiles(texture,fileNames);
		if (texture != null)
			System.out.println("OK");
	}

	private void readFiles(Texture texture, String[] fileNames){
		for (int i = 0; i < fileNames.length; i++) {
	    	TextureData data;
	        System.out.println("reading texture " + fileNames[i]);
			try {
				data = TextureIO.newTextureData(gl.getGLProfile(),
						new File(fileNames[i]),
						true,
						null);
				texture.updateImage(gl, data, targets[i]);
			} catch (IOException e) {
				System.out.println("failed");
				System.out.println(e.getMessage());
			}
	       }
	}
	
	public void bind(int shaderProgram, String name, int slot) {
		if (texture == null) return;
		gl.glActiveTexture(GL2.GL_TEXTURE0 + slot);
		texture.bind(gl);
		gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, name), slot);
	}
}
