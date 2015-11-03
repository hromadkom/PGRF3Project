package geom;

import com.jogamp.opengl.GL3;
import utils.G3OGLBuffers;

/**
 * Created by martinhromadko on 13.10.15.
 */
public interface GL3Generator {

    public G3OGLBuffers generate(GL3 gl);
}
