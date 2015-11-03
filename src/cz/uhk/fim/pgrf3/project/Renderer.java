package cz.uhk.fim.pgrf3.project;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
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

    int width, height, ox, oy;

    G3OGLBuffers buffers;

    int shaderProgram, locMat, modelView, eyeVec, baseColor;

    Col color = new Col(0.2f, 0.3f, 0.4f, 1.0f);

    G3OGLTexture texture;

    Camera camera = new Camera();

    Mat4 proj;

    public void init(GLAutoDrawable glDrawable){
        GL3 gl = glDrawable.getGL().getGL3();

        System.out.println("Init GL is " + gl.getClass().getName());

        shaderProgram = G3ShaderUtils.loadProgram(gl,"./shader/grid/sphereleph");
        texture = new G3OGLTexture(gl,"./textures/bricks.jpg");
        createBuffers(gl);

        locMat = gl.glGetUniformLocation(shaderProgram, "mat");
        modelView = gl.glGetUniformLocation(shaderProgram, "modelView");
        eyeVec = gl.glGetUniformLocation(shaderProgram, "eyeVec");
        baseColor = gl.glGetUniformLocation(shaderProgram, "baseColor");

        //texture = new G3OGLTexture();

        camera.setPosition(new Vec3D(5, 5, 2.5));
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
        gl.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(camera.getViewMatrix().mul(proj)), 0);
        gl.glUniformMatrix4fv(modelView, 1, false, ToFloatArray.convert(camera.getViewMatrix()),0);
        gl.glUniform3fv(eyeVec, 1, ToFloatArray.convert(camera.getEyeVector()), 0);
        gl.glUniform4fv(baseColor, 1, ToFloatArray.convert(color), 0);
        texture.bind(shaderProgram, "texture", 0);
        buffers.draw(GL3.GL_TRIANGLES, shaderProgram);
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
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

}
