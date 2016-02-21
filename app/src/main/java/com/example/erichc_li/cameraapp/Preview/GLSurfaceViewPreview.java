package com.example.erichc_li.cameraapp.Preview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.erichc_li.cameraapp.CameraBase.CameraManager;
import com.example.erichc_li.cameraapp.ViewProcessing.SurfaceTextureProcessing;
import com.example.erichc_li.cameraapp.ViewProcessing.ViewProcessing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewPreview extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = GLSurfaceViewPreview.class.getName();

    private CameraManager mCameraManager;
    private SurfaceTexture mSurfaceTexture;
    private final ViewProcessing mViewProcessing;

    private int mTextureID;
    private DirectDrawer mDirectDrawer;

    public GLSurfaceViewPreview(Context context, CameraManager camera) {
        super(context);
        mCameraManager = camera;
        setEGLContextClientVersion(2);
        setRenderer(this);
        mViewProcessing = new SurfaceTextureProcessing(mCameraManager);
        mTextureID = createTextureID();
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCameraManager.ShowWhatView("GLSurfaceViewPreview");
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "surfaceCreated...");
        mDirectDrawer = new DirectDrawer(mTextureID);
        mViewProcessing.viewCreated(mSurfaceTexture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "surfaceChanged...");
        mViewProcessing.viewChanged(mSurfaceTexture);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();
        float[] mtx = new float[16];
        mSurfaceTexture.getTransformMatrix(mtx);
        mDirectDrawer.draw(mtx);
    }

    private int createTextureID() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

    public class DirectDrawer {
        private final String vertexShaderCode = "attribute vec4 vPosition;" + "attribute vec4 inputTextureCoordinate;" + "uniform mat4 u_xform;\n"
                + "varying vec2 textureCoordinate;" + "void main()" + "{" + "gl_Position = vPosition;"
                + "textureCoordinate = (u_xform * inputTextureCoordinate).xy;" + "}";

        private final String fragmentShaderCode = "#extension GL_OES_EGL_image_external : require\n" + "precision mediump float;"
                + "varying vec2 textureCoordinate;\n" + "uniform samplerExternalOES s_texture;\n" + "void main() {"
                + "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n" + "}";

        private FloatBuffer vertexBuffer, textureVerticesBuffer;
        private ShortBuffer drawListBuffer;
        private final int mProgram;
        private int mPositionHandle;
        private int mTextureCoordHandle;

        private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw
        // vertices

        // number of coordinates per vertex in this array
        private static final int COORDS_PER_VERTEX = 2;

        private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
        // vertex

        final float squareCoords[] = { -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, };

        final float textureVertices[] = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, };

        private int texture;
        private int mTransformLocation;

        public DirectDrawer(int texture) {
            this.texture = texture;
            // initialize vertex byte buffer for shape coordinates
            ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(squareCoords);
            vertexBuffer.position(0);

            // initialize byte buffer for the draw list
            ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(drawOrder);
            drawListBuffer.position(0);

            ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
            bb2.order(ByteOrder.nativeOrder());
            textureVerticesBuffer = bb2.asFloatBuffer();
            textureVerticesBuffer.put(textureVertices);
            textureVerticesBuffer.position(0);

            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

            mProgram = GLES20.glCreateProgram(); // create empty OpenGL ES
            // Program
            GLES20.glAttachShader(mProgram, vertexShader); // add the vertex
            // shader to program
            GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
            // shader to
            // program
            GLES20.glLinkProgram(mProgram); // creates OpenGL ES program
            // executables

            mTransformLocation = GLES20.glGetUniformLocation(mProgram, "u_xform");
        }

        public void draw(float[] mtx) {
            GLES20.glUseProgram(mProgram);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);

            GLES20.glUniformMatrix4fv(mTransformLocation, 1, false, mtx, 0);

            // get handle to vertex shader's vPosition member
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            // Prepare the <insert shape here> coordinate data
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

            mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
            GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

            // textureVerticesBuffer.clear();
            // textureVerticesBuffer.put( transformTextureCoordinates(
            // textureVertices, mtx ));
            // textureVerticesBuffer.position(0);
            GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureVerticesBuffer);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(mPositionHandle);
            GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        }

        private int loadShader(int type, String shaderCode) {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            int shader = GLES20.glCreateShader(type);

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            return shader;
        }

    }

}
