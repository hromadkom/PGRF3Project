package geom;

import com.jogamp.opengl.GL3;
import transforms.Vec2D;
import utils.G3OGLBuffers;
import utils.ToFloatArray;
import utils.ToIntArray;

import javax.naming.directory.InvalidAttributesException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by martinhromadko on 13.10.15.
 */
public class GL3GeomGenerator implements GL3Generator {

    private int m;

    private int n;

    public GL3GeomGenerator(int m, int n) throws InvalidAttributesException {
        if(m<2 || n < 2){
            throw new InvalidAttributesException("Parameter m or parameter n je mensi nez 2");
        }
        this.m = m;
        this.n = n;
    }


    @Override
    public G3OGLBuffers generate(GL3 gl) {
        List<Vec2D> vertexBuffer = new ArrayList<>();

        float stepX = 1.0f / (float)(m - 1);
        float stepY = 1.0f / (float)(n - 1);
        float actualY = 0;
        for(int i = 0; i < n; i++){
            float actualX = 0;
            for (int j = 0; j < m; j++){
                Vec2D vec2D = new Vec2D(actualX, actualY);
                vertexBuffer.add(vec2D);
                actualX += stepX;
            }
            actualY += stepY;
        }

        List<Integer> indexBuffer = new ArrayList<>();
        for(int i = 0; i< n - 1; i++){
            for(int j = 0; j < m - 1; j++){
                indexBuffer.add(new Integer( (i*m) + (j) ));
                indexBuffer.add(new Integer(((i)*m)+((j+1))));
                indexBuffer.add(new Integer(((i+1)*m)+((j))));

                indexBuffer.add(new Integer(((i)*m)+((j+1))));
                indexBuffer.add(new Integer(((i+1)*m)+((j))));
                indexBuffer.add(new Integer(((i+1)*m)+((j+1))));
            }
        }

        G3OGLBuffers.Attrib[] attributesPos = {
                new G3OGLBuffers.Attrib("inPosition", 2),
        };

        float[] vertexB = ToFloatArray.convert(vertexBuffer);
        int[] indexB = ToIntArray.convert(indexBuffer);

        return new G3OGLBuffers(gl, vertexB, attributesPos, indexB);
    }
}
