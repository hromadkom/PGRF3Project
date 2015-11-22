package cz.uhk.fim.pgrf3.project;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by martinhromadko on 31.10.15.
 */
public class App {

    private JFrame frame;

    public App() {
        frame = new JFrame("PGRF 3 - Hromadko");
        frame.setSize(600,385);
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setDepthBits(32);

        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.setSize(512, 384);
        Renderer ren = new Renderer();
        canvas.addGLEventListener(ren);
        canvas.addMouseListener(ren);
        canvas.addMouseMotionListener(ren);
        canvas.addKeyListener(ren);

        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);

//        JPanel leftPanel = new JPanel();
//        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
//        JComboBox object = new JComboBox(ren.getObjects());
//        leftPanel.add(object);
//        frame.add(leftPanel, BorderLayout.WEST);

        final FPSAnimator animator = new FPSAnimator(canvas, 60, true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread() {
                    public void run() {
                        if (animator.isStarted()) {
                            animator.stop();
                        }
                        System.exit(0);
                    }
                }.start();
            }
        });

        frame.pack();
        frame.setVisible(true);
        animator.start();
    }

    public static void main(String[] args) {
        App app = new App();
    }

}
