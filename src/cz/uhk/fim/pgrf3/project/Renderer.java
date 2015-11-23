package cz.uhk.fim.pgrf3.project;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.math.Matrix4;
import geom.GL3Generator;
import geom.GL3GeomGenerator;
import transforms.*;
import utils.G3OGLBuffers;
import utils.G3OGLTexture;
import utils.G3ShaderUtils;
import utils.ToFloatArray;

import javax.naming.directory.InvalidAttributesException;
import java.awt.event.*;

/**
 * Created by martinhromadko on 31.10.15.
 */
public class Renderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

    private static final int SHOW_COLOR_NOTHING = 0;
    private static final int SHOW_COLOR_POSITION = 1;
    private static final int SHOW_COLOR_NORMAL = 2;
    private static final int SHOW_COLOR_COLOR = 3;

    int width, height, ox, oy;

    G3OGLBuffers buffers;

    int shaderProgram;
    int shd_locMat, shd_modelView, shd_eyeVec, shd_baseColor, shd_normalMat, shd_spotCutOff;
    int shd_spotDirection, shd_locTime, shd_usedObject, shd_lightPosition, shd_shading, shd_spotlight, shd_extendedMapping;
    int shd_showColor, shd_lightDownturn;

    Col color = new Col(0.2f, 0.3f, 0.8f, 1.0f);

    G3OGLTexture texture, normalTexture, heightTexture;

    Camera camera = new Camera();

    Mat4 proj;
    // lights
    Vec3D lightPosition = new Vec3D(7.0, 7.0, 2.5);
    Vec3D spotLightDirection = new Vec3D(-0.3,-0.3,-0.25);
    float spotLightCutOff;

    int object = 1;
    int perVertexShading = 0;
    int spotlight = 0;
    int extendedMapping = 1;

    int showColorStatus = 0;
    float lightDownturn = 0.1f;

    public void init(GLAutoDrawable glDrawable){
        GL3 gl = glDrawable.getGL().getGL3();

        System.out.println("Init GL is " + gl.getClass().getName());

        shaderProgram = G3ShaderUtils.loadProgram(gl,"./shader/shdr");
        texture = new G3OGLTexture(gl,"./textures/bricks.jpg");
        texture.texture.setMustFlipVertically(true);
        normalTexture = new G3OGLTexture(gl, "./textures/bricksn.png");
        heightTexture = new G3OGLTexture(gl, "./textures/bricksh.png");
        createBuffers(gl);

        spotLightCutOff = (float) Math.cos(Math.PI/8.0);

        shd_locMat = gl.glGetUniformLocation(shaderProgram, "mat");
        shd_modelView = gl.glGetUniformLocation(shaderProgram, "modelView");
        shd_eyeVec = gl.glGetUniformLocation(shaderProgram, "eyePosition");
        shd_baseColor = gl.glGetUniformLocation(shaderProgram, "baseColor");
        shd_normalMat = gl.glGetUniformLocation(shaderProgram, "normalMat");
        shd_spotCutOff = gl.glGetUniformLocation(shaderProgram, "spotCutOff");
        shd_spotDirection = gl.glGetUniformLocation(shaderProgram, "spotDirection");
        shd_locTime = gl.glGetUniformLocation(shaderProgram, "time");
        shd_usedObject = gl.glGetUniformLocation(shaderProgram, "usedObject");
        shd_lightPosition = gl.glGetUniformLocation(shaderProgram, "lightPosition");
        shd_shading = gl.glGetUniformLocation(shaderProgram, "perVertexShading");
        shd_spotlight = gl.glGetUniformLocation(shaderProgram, "spotLightEnabled");
        shd_extendedMapping = gl.glGetUniformLocation(shaderProgram, "extendedMapping");
        shd_showColor = gl.glGetUniformLocation(shaderProgram, "showColor");
        shd_lightDownturn = gl.glGetUniformLocation(shaderProgram, "lightDownturn");

        //texture = new G3OGLTexture();

        camera.setPosition(new Vec3D(10, 10, 2.5));
        camera.setAzimuth(Math.PI * 1.25);
        camera.setZenith(Math.PI * -0.125);

        gl.glEnable(GL3.GL_DEPTH_TEST);
    }

    private void createBuffers(GL3 gl) {
        GL3Generator generator = null;
        try {
            generator = new GL3GeomGenerator(50,50);
        } catch (InvalidAttributesException e) {
            e.printStackTrace();
        }

        buffers = generator.generate(gl);
    }

    @Override
    public void display(GLAutoDrawable glDrawable) {
        GL3 gl = glDrawable.getGL().getGL3();
        //gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL3.GL_LINE);

        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(shaderProgram);
        gl.glUniformMatrix4fv(shd_locMat, 1, false,
                ToFloatArray.convert(camera.getViewMatrix().mul(proj)), 0);
        gl.glUniformMatrix4fv(shd_modelView, 1, false, ToFloatArray.convert(camera.getViewMatrix()),0);
        gl.glUniform3fv(shd_eyeVec, 1, ToFloatArray.convert(camera.getEye()), 0);
        gl.glUniform4fv(shd_baseColor, 1, ToFloatArray.convert(color), 0);
        gl.glUniform1f(shd_spotCutOff, spotLightCutOff);
        gl.glUniform3fv(shd_spotDirection,1 ,ToFloatArray.convert(spotLightDirection), 0);
        gl.glUniform3fv(shd_lightPosition, 1, ToFloatArray.convert(lightPosition),0);
        gl.glUniform1i(shd_usedObject, object);
        gl.glUniform1i(shd_shading, perVertexShading);
        gl.glUniform1i(shd_spotlight, spotlight);
        gl.glUniform1i(shd_extendedMapping, extendedMapping);
        gl.glUniform1i(shd_showColor, showColorStatus);
        gl.glUniform1f(shd_lightDownturn, lightDownturn);

        Mat4 normal = camera.getViewMatrix();
        texture.bind(shaderProgram, "texture", 0);
        normalTexture.bind(shaderProgram, "normalTexture", 1);
        heightTexture.bind(shaderProgram, "heightTexture", 2);
        buffers.draw(GL3.GL_TRIANGLES, shaderProgram);
    }

    private Matrix4 calculateNormalMatrix(Mat4 mat4){
        Matrix4 matrix4 = new Matrix4();
        matrix4.multMatrix(ToFloatArray.convert(mat4));
        matrix4.invert();
        matrix4.transpose();
        return matrix4;
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
                               boolean deviceChanged) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        ox = e.getX();
        oy = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        camera.addAzimuth((double) Math.PI * (ox - e.getX())
                / width);
        camera.addZenith((double) Math.PI * (e.getY() - oy)
                / width);
        ox = e.getX();
        oy = e.getY();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                camera.forward(1);
                break;
            case KeyEvent.VK_D:
                camera.right(1);
                break;
            case KeyEvent.VK_S:
                camera.backward(1);
                break;
            case KeyEvent.VK_A:
                camera.left(1);
                break;
            case KeyEvent.VK_SHIFT:
                camera.down(1);
                break;
            case KeyEvent.VK_CONTROL:
                camera.up(1);
                break;
            case KeyEvent.VK_SPACE:
                camera.setFirstPerson(!camera.getFirstPerson());
                break;
            case KeyEvent.VK_R:
                camera.mulRadius(0.9f);
                break;
            case KeyEvent.VK_F:
                camera.mulRadius(1.1f);
                break;
            case KeyEvent.VK_1:
            case KeyEvent.VK_NUMPAD1:
                object = 0;
                break;
            case KeyEvent.VK_2:
            case KeyEvent.VK_NUMPAD2:
                object = 1;
                break;
            case KeyEvent.VK_3:
            case KeyEvent.VK_NUMPAD3:
                object = 2;
                break;
            case KeyEvent.VK_4:
            case KeyEvent.VK_NUMPAD4:
                object = 3;
                break;
            case KeyEvent.VK_5:
            case KeyEvent.VK_NUMPAD5:
                object = 4;
                break;
            case KeyEvent.VK_6:
            case KeyEvent.VK_NUMPAD6:
                object = 5;
                break;
            case KeyEvent.VK_7:
            case KeyEvent.VK_NUMPAD7:
                object = 6;
                break;
            case KeyEvent.VK_8:
            case KeyEvent.VK_NUMPAD8:
                object = 7;
                break;
            case KeyEvent.VK_9:
            case KeyEvent.VK_NUMPAD9:
                object = 8;
                break;
            case KeyEvent.VK_E:
                extendedMapping = extendedMapping == 1 ? 0 : 1;
                break;
            case KeyEvent.VK_L:
                spotlight = spotlight == 1? 0: 1;
                break;
            case KeyEvent.VK_V:
                if(perVertexShading == 0){
                    extendedMapping = 0;
                    perVertexShading = 1;
                }else{
                    perVertexShading = 0;
                }
                break;
            case KeyEvent.VK_N:
                showColorStatus = showColorStatus != SHOW_COLOR_NORMAL ? SHOW_COLOR_NORMAL: SHOW_COLOR_NOTHING;
                break;
            case KeyEvent.VK_P:
                showColorStatus = showColorStatus != SHOW_COLOR_POSITION ? SHOW_COLOR_POSITION: SHOW_COLOR_NOTHING;
                break;
            case KeyEvent.VK_C:
                showColorStatus = showColorStatus != SHOW_COLOR_COLOR ? SHOW_COLOR_COLOR: SHOW_COLOR_NOTHING;
                break;
            case KeyEvent.VK_DOWN:
                spotLightDirection.y += 0.05;
                break;
            case KeyEvent.VK_UP:
                spotLightDirection.y -= 0.05;
                break;
            case KeyEvent.VK_LEFT:
                spotLightDirection.x += 0.05;
                break;
            case KeyEvent.VK_RIGHT:
                spotLightDirection.x -= 0.05;
                break;
            case KeyEvent.VK_U:
                if (lightDownturn < 1) lightDownturn += 0.05;
                break;
            case KeyEvent.VK_J:
                if (lightDownturn > 0) lightDownturn -= 0.05;
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public String[] getObjects(){
        String[] objects = new String[6];
        objects[0] = "Torus";
        return objects;
    }
}
