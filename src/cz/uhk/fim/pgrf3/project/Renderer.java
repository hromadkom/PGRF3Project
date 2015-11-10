package cz.uhk.fim.pgrf3.project;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.math.Matrix4;
import com.sun.javafx.geom.Vec3f;
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

    int shaderProgram, locMat, modelView, eyeVec, baseColor, normalMat, spotCutOff, spotDirection, locTime;

    Col color = new Col(0.2f, 0.3f, 0.4f, 1.0f);

    G3OGLTexture texture, normalTexture;

    Camera camera = new Camera();

    Mat4 proj;

    float spotLightCutOff;

    Vec3D spotLightDirection = new Vec3D(-0.3,-0.3,-0.3);

    float time = 0;

    public void init(GLAutoDrawable glDrawable){
        GL3 gl = glDrawable.getGL().getGL3();

        System.out.println("Init GL is " + gl.getClass().getName());

        shaderProgram = G3ShaderUtils.loadProgram(gl,"./shader/grid/sphereblinphongfull");
        texture = new G3OGLTexture(gl,"./textures/bricks.jpg");
        normalTexture = new G3OGLTexture(gl, "./textures/bricksn.png");
        createBuffers(gl);

        spotLightCutOff = (float) Math.cos(Math.PI/8.0);

        locMat = gl.glGetUniformLocation(shaderProgram, "mat");
        modelView = gl.glGetUniformLocation(shaderProgram, "modelView");
        eyeVec = gl.glGetUniformLocation(shaderProgram, "eyePosition");
        baseColor = gl.glGetUniformLocation(shaderProgram, "baseColor");
        normalMat = gl.glGetUniformLocation(shaderProgram, "normalMat");
        spotCutOff = gl.glGetUniformLocation(shaderProgram, "spotCutOff");
        spotDirection = gl.glGetUniformLocation(shaderProgram, "spotDirection");
        locTime = gl.glGetUniformLocation(shaderProgram, "time");

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

        time += 0.1;

        gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(shaderProgram);
        gl.glUniformMatrix4fv(locMat, 1, false,
                ToFloatArray.convert(camera.getViewMatrix().mul(proj)), 0);
        gl.glUniformMatrix4fv(modelView, 1, false, ToFloatArray.convert(camera.getViewMatrix()),0);
        gl.glUniform3fv(eyeVec, 1, ToFloatArray.convert(camera.getEye()), 0);
        gl.glUniform4fv(baseColor, 1, ToFloatArray.convert(color), 0);
        gl.glUniform1f(spotCutOff, spotLightCutOff);
        gl.glUniform3fv(spotDirection,1 ,ToFloatArray.convert(spotLightDirection), 0);
        gl.glUniform1f(locTime, time); // musi byt nastaven spravy shader

        Mat4 normal = camera.getViewMatrix();

       // System.out.println(camera.getEye());

        texture.bind(shaderProgram, "texture", 0);
        normalTexture.bind(shaderProgram, "normalTexture", 1);
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
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

}
