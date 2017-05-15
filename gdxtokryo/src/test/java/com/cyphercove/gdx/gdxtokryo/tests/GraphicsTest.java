package com.cyphercove.gdx.gdxtokryo.tests;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.*;
import com.cyphercove.gdx.gdxtokryo.GdxToKryo;
import com.cyphercove.gdx.gdxtokryo.gdxserializers.graphics.PixmapSerializer;

public class GraphicsTest extends GdxToKryoTest {

    public static void main(String[] args) {
        GraphicsTest test = new GraphicsTest();
        try {
            test.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        test.testGraphics();
    }

    public void testGraphics(){
        Object[] objects = new Object[5];

        objects[0] = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat());

        OrthographicCamera orthographicCamera = new OrthographicCamera();
        orthographicCamera.position.set(randFloat(), randFloat(), randFloat());
        orthographicCamera.viewportWidth = random.nextInt(1000);
        orthographicCamera.viewportHeight = random.nextInt(1000);
        orthographicCamera.zoom = random.nextFloat() * random.nextInt(3);
        orthographicCamera.near = 0.014f;
        orthographicCamera.far = 5000.35f;
        orthographicCamera.up.rotateRad(new Vector3(randFloat(), randFloat(), randFloat()).nor(), randFloat());
        orthographicCamera.update();
        objects[1] = orthographicCamera;

        PerspectiveCamera perspectiveCamera = new PerspectiveCamera();
        perspectiveCamera.position.set(randFloat(), randFloat(), randFloat());
        perspectiveCamera.viewportWidth = random.nextInt(1000);
        perspectiveCamera.viewportHeight = random.nextInt(1000);
        perspectiveCamera.fieldOfView = (random.nextFloat() * 0.5f + 0.5f) * 60;
        perspectiveCamera.near = random.nextFloat() * 5;
        perspectiveCamera.far = (random.nextFloat() * 0.5f + 0.5f) * 5000;
        perspectiveCamera.up.rotateRad(new Vector3(randFloat(), randFloat(), randFloat()).nor(), randFloat());
        perspectiveCamera.update();
        objects[2] = perspectiveCamera;

        Pixmap pixmap = new Pixmap(2, 3, Pixmap.Format.RGBA8888);
        for (int i = 0; i < pixmap.getWidth(); i++) {
            for (int j = 0; j < pixmap.getHeight(); j++) {
                pixmap.drawPixel(i, j, random.nextInt());
            }
        }
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setFilter(Pixmap.Filter.NearestNeighbour);
        objects[3] = pixmap;

        Pixmap pixmap2 = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        for (int i = 0; i < pixmap2.getWidth(); i++) {
            for (int j = 0; j < pixmap2.getHeight(); j++) {
                pixmap2.drawPixel(i, j, random.nextInt());
            }
        }
        pixmap2.setBlending(Pixmap.Blending.SourceOver);
        pixmap2.setFilter(Pixmap.Filter.BiLinear);
        objects[4] = pixmap2;

        Object[] returned = simpleRoundTrip(objects);

        pixmap.dispose();
        pixmap2.dispose();
        ((Pixmap)returned[3]).dispose();
        ((Pixmap)returned[4]).dispose();
    }

}
