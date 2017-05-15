package com.cyphercove.gdx.gdxtokryo.tests;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.collision.Segment;
import com.badlogic.gdx.math.collision.Sphere;
import com.cyphercove.gdx.gdxtokryo.GdxToKryo;
import com.esotericsoftware.kryo.Kryo;
import junit.framework.TestCase;

public class MathTest extends GdxToKryoTest {

    public static void main(String[] args) {
        MathTest test = new MathTest();
        try {
            test.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        test.testMathClasses();
    }

    public void testMathClasses (){
        Object[] objects = new Object[22];

        objects[0] = new Affine2().setToTranslation(3, -5000).rotate(30);
        objects[1] = new Affine2().setToRotation(30).translate(15, -3);
        objects[2] = new Bezier<Vector2>(new Vector2(3, 5), new Vector2(6, 7), new Vector2(-894343224, 1));
        objects[3] = new Bezier<Vector3>(new Vector3(5, 7, 800), new Vector3(2, 6, 8),
                new Vector3(15, 17, -4.5f), new Vector3(0, 0, 10000.02f));

        Vector2[] controlPoints = {new Vector2(0, 5f), new Vector2(15, -35.2f), new Vector2(80, -0.3f)};
        objects[4] = new BSpline<Vector2>(controlPoints, 2, true);

        Vector3[] controlPoints2 = {new Vector3(0, 5f, .5f), new Vector3(15, -35.2f, 5000f), new Vector3(80, -0.3f, 0)};
        objects[5] = new BSpline<Vector3>(controlPoints2, 3, false);

        objects[6] = new CatmullRomSpline<Vector2>(controlPoints, false);
        objects[7] = new CatmullRomSpline<Vector3>(controlPoints2, true);

        objects[8] = new Circle(0.6f, -5000f, 35f);
        objects[9] = new Ellipse(0.7f, -0.2f, 80f, 900f);
        objects[10] = new GridPoint2(50, -345987);
        objects[11] = new GridPoint3(-1, -2, 5);
        objects[12] = new Matrix3().setToRotation(50).translate(10f, -436f);
        objects[13] = new Matrix4().setToRotation(new Vector3(0.56f, 8, 90f).nor(), 86.3f).translate(0.3f, 0.8f, 52f);
        objects[14] = new Plane(new Vector3(0.5f, 0.6f, 5f).nor(), 60f);

        Polygon polygon = new Polygon(generateRandomFloatArray(3 * 7));
        polygon.setScale(13, 15.32f);
        polygon.translate(-503, - 15.5f);
        polygon.rotate(15f);
        polygon.setOrigin(50f, 0.1235f);
        objects[15] = polygon;

        Polyline polyline = new Polyline(generateRandomFloatArray(3 * 9));
        polyline.setOrigin(50f, 0.1235f);
        polyline.rotate(15f);
        polyline.translate(-503, - 15.5f);
        polyline.setScale(13, 15.32f);
        objects[16] = polyline;

        objects[17] = new Quaternion(new Vector3(0.5f, 0.6f, 5f).nor(), 60f);
        objects[18] = new RandomXS128();
        objects[19] = new Rectangle(50f, 0.2f, 904f, 3f);
        objects[20] = new Vector2(50, -63.24f);
        objects[21] = new Vector3(56f, -14f, 0);

        simpleRoundTrip(objects);
    }

    public void testCollisionClasses (){
        Object[] objects = new Object[4];

        objects[0] = new BoundingBox(new Vector3(randFloat(), randFloat(), randFloat()), new Vector3(randFloat(), randFloat(), randFloat()));
        objects[1] = new Ray(new Vector3(randFloat(), randFloat(), randFloat()), new Vector3(randFloat(), randFloat(), randFloat()).nor());
        objects[2] = new Segment(randFloat(), randFloat(), randFloat(), randFloat(), randFloat(), randFloat());
        objects[3] = new Sphere(new Vector3(randFloat(), randFloat(), randFloat()), randFloat());

        simpleRoundTrip(objects);
    }
}
