package com.cyphercove.gdx.gdxtokryo.tests;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Sphere;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.cyphercove.gdx.gdxtokryo.GdxToKryo;
import com.cyphercove.gdx.gdxtokryo.GraphHeader;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public abstract class GdxToKryoTest extends TestCase {

    public static boolean logInequalities = true;

    protected Kryo kryo;
    protected final Random random = new Random();

    protected void setUp() throws Exception {
        GdxNativesLoader.load();
        kryo = new Kryo();
        kryo.setRegistrationRequired(true);
        kryo.register(Object.class);
        kryo.register(Object[].class);
        GdxToKryo.registerAll(kryo, 50);
    }

    /** Check functional equality between two classes whose equals methods don't necessarily support field comparison. Many
     * LibGDX classes do not since this usually isn't practically useful.
     *
     * @return Whether the two objects are functionally equal.
     */
    static boolean equals (Object a, Object b){
        if (a == b)
            return true;
        if (a == null || b == null)
            return reportInequality(a, b);
        if (a.getClass() != b.getClass())
            return reportInequality(a, b);
        final Class type = a.getClass();
        if (type == float[].class){
            boolean equal = Arrays.equals((float[])a, (float[])b);
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == int[].class){
            boolean equal = Arrays.equals((int[])a, (int[])b);
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (a instanceof Object[]){
            Object[] _a = (Object[])a;
            Object[] _b = (Object[])b;
            if (_a.length != _b.length) {
                reportInequality(a, b);
                return false;
            }
            for (int i = 0; i < _a.length; i++) {
                if (!equals(_a[i], _b[i])) {
                    reportInequality(a, b);
                    return false;
                }
            }
            return true;
        } else if (type == FloatCounter.class){
            FloatCounter fcA = (FloatCounter)a;
            FloatCounter fcB = (FloatCounter)b;
            boolean wasLogInequalities = logInequalities;
            logInequalities = false; // wait for logging by objec
            boolean equal = equals(fcA.mean, fcB.mean);
            logInequalities = wasLogInequalities;
            if (!equal) {
                reportInequality(a, b);
                return false;
            }
            if (fcA.count != fcB.count || fcA.value != fcB.value || fcA.average != fcB.average || fcA.latest != fcB.latest ||
                fcA.max != fcB.max || fcA.min != fcB.min){
                reportInequality(a, b);
                return false;
            }
            return true;
        } else if (type == WindowedMean.class){
            WindowedMean wmA = (WindowedMean)a;
            WindowedMean wmB = (WindowedMean)b;
            float[] wmAValues = wmA.getWindowValues();
            float[] wmBValues = wmB.getWindowValues();
            boolean wasLogInequalities = logInequalities;
            logInequalities = false; // wait for logging by objec
            boolean equal = equals(wmAValues, wmBValues);
            logInequalities = wasLogInequalities;
            if (!equal)
                reportInequality(a, b);
            return equal;
        } else if (type == OrthographicCamera.class){
            OrthographicCamera camA = (OrthographicCamera)a;
            OrthographicCamera camB = (OrthographicCamera)b;
            boolean equal = equals(camA.position, camB.position) && equals(camA.direction, camB.direction) && equals(camA.up, camB.up) &&
                    equals(camA.projection, camB.projection) && camA.near == camB.near && camA.far == camB.far && camA.viewportWidth == camB.viewportWidth &&
                    camA.viewportHeight == camB.viewportHeight && camA.zoom == camB.zoom;
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == PerspectiveCamera.class){
            PerspectiveCamera camA = (PerspectiveCamera)a;
            PerspectiveCamera camB = (PerspectiveCamera)b;
            boolean equal = equals(camA.position, camB.position) && equals(camA.direction, camB.direction) && equals(camA.up, camB.up) &&
                    equals(camA.projection, camB.projection) && camA.near == camB.near && camA.far == camB.far && camA.viewportWidth == camB.viewportWidth &&
                    camA.viewportHeight == camB.viewportHeight && camA.fieldOfView == camB.fieldOfView;
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Pixmap.class){
            ByteBuffer aPixels = ((Pixmap)a).getPixels();
            ByteBuffer bPixels = ((Pixmap)b).getPixels();
            boolean equal = aPixels.equals(bPixels);
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Affine2.class){
            boolean equal = a.toString().equals(b.toString());
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Bezier.class){
            boolean equal = equals(((Bezier)a).points, ((Bezier)b).points);
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == BSpline.class){
            BSpline _a = (BSpline)a;
            BSpline _b = (BSpline)b;
            boolean equal = _a.spanCount == _b.spanCount && _a.continuous == _b.continuous && _a.degree == _b.degree &&
                    equals(_a.knots, _b.knots) && equals(_a.controlPoints, _b.controlPoints);
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == CatmullRomSpline.class){
            CatmullRomSpline _a = (CatmullRomSpline)a;
            CatmullRomSpline _b = (CatmullRomSpline)b;
            boolean equal = _a.spanCount == _b.spanCount && _a.continuous == _b.continuous &&
                    equals(_a.controlPoints, _b.controlPoints);
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Matrix3.class){
            boolean equal = equals(((Matrix3)a).val, ((Matrix3)b).val);
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Matrix4.class){
            boolean equal = equals(((Matrix4)a).val, ((Matrix4)b).val);
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Plane.class){
            Plane _a = (Plane)a;
            Plane _b = (Plane)b;
            boolean equal =  _a.d == _b.d && equals(_a.normal, _b.normal);
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Polygon.class){
            Polygon _a = (Polygon)a;
            Polygon _b = (Polygon)b;
            boolean equal = _a.getX() == _b.getX() && _a.getY() == _b.getY() && _a.getOriginX() == _b.getOriginX() &&
                    _a.getOriginY() == _b.getOriginY() && _a.getRotation() == _b.getRotation() &&
                    _a.getScaleX() == _b.getScaleX() && _a.getScaleY() == _b.getScaleY() &&
                    equals(_a.getVertices(), _b.getVertices());
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Polyline.class){
            Polyline _a = (Polyline)a;
            Polyline _b = (Polyline)b;
            boolean equal = _a.getX() == _b.getX() && _a.getY() == _b.getY() && _a.getOriginX() == _b.getOriginX() &&
                    _a.getOriginY() == _b.getOriginY() && _a.getRotation() == _b.getRotation() &&
                    _a.getScaleX() == _b.getScaleX() && _a.getScaleY() == _b.getScaleY() &&
                    equals(_a.getVertices(), _b.getVertices());
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Quaternion.class){
            Quaternion _a = (Quaternion)a;
            Quaternion _b = (Quaternion)b;
            boolean equal = _a.x == _b.x && _a.y == _b.y && _a.z == _b.z && _a.w == _b.w;
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == RandomXS128.class){
            RandomXS128 _a = (RandomXS128)a;
            RandomXS128 _b = (RandomXS128)b;
            boolean equal = _a.getState(0) == _b.getState(0) &&
                    _a.getState(1) == _b.getState(1) && _a.nextFloat() == _b.nextFloat();
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Rectangle.class) {
            Rectangle _a = (Rectangle) a;
            Rectangle _b = (Rectangle) b;
            boolean equal = _a.x == _b.x && _a.y == _b.y && _a.width == _b.width && _a.height == _b.height;
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Vector2.class){
            Vector2 _a = (Vector2)a;
            Vector2 _b = (Vector2)b;
            boolean equal = _a.x == _b.x && _a.y == _b.y;
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == Vector3.class){
            Vector3 _a = (Vector3)a;
            Vector3 _b = (Vector3)b;
            boolean equal = _a.x == _b.x && _a.y == _b.y && _a.z == _b.z;
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == BoundingBox.class){
            BoundingBox _a = (BoundingBox)a;
            BoundingBox _b = (BoundingBox)b;
            boolean equal = equals(_a.min, _b.min) && equals(_a.max, _b.max);
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == ArrayMap.class){
            ArrayMap<?,?> _a = (ArrayMap<?,?>)a;
            ArrayMap<?,?> _b = (ArrayMap<?,?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            if (_a.ordered != _b.ordered)
                return reportInequality(a, b);
            for (int i = 0; i < _a.size; i++) {
                if (!equals(_a.getKeyAt(i), _b.getKeyAt(i)) || !equals(_a.getValueAt(i), _b.getValueAt(i)))
                    return reportInequality(a, b);
            }
            return true;
        } else if (type == Array.class || type == DelayedRemovalArray.class || type == SnapshotArray.class){
            Array<?> _a = (Array<?>)a;
            Array<?> _b = (Array<?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            for (int i = 0; i < _a.size; i++) {
                if (!equals(_a.get(i), _b.get(i)))
                    return reportInequality(a, b);
            }
            return true;
        } else if (type == IdentityMap.class){
            IdentityMap<?, ?> _a = (IdentityMap<?, ?>)a;
            IdentityMap<?, ?> _b = (IdentityMap<?, ?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            outer:
            for (IdentityMap.Entry entryA : _a.entries()){
                boolean wasLogInequalities = logInequalities;
                logInequalities = false; // Inequalities in the loop may be for non-matching pairs.
                for (IdentityMap.Entry entryB : _b.entries()){
                    if (equals(entryA.key, entryB.key) && equals(entryA.value, entryB.value))
                        continue outer;
                }
                logInequalities = wasLogInequalities;
                return reportInequality(a, b);
            }
            return true;
        } else if (type == LongMap.class){
            LongMap<?> _a = (LongMap<?>)a;
            LongMap<?> _b = (LongMap<?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            outer:
            for (LongMap.Entry entryA : _a.entries()){
                if (!_b.containsKey(entryA.key))
                    return reportInequality(a, b);
                boolean wasLogInequalities = logInequalities;
                logInequalities = false; // Inequalities in the loop may be for non-matching pairs.
                for (LongMap.Entry entryB : _b.entries()){
                    if (equals(entryA.key, entryB.key) && equals(entryA.value, entryB.value))
                        continue outer;
                }
                logInequalities = wasLogInequalities;
                return reportInequality(a, b);
            }
            return true;
        } else if (type == ObjectFloatMap.class){
            ObjectFloatMap<?> _a = (ObjectFloatMap<?>)a;
            ObjectFloatMap<?> _b = (ObjectFloatMap<?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            outer:
            for (ObjectFloatMap.Entry entryA : _a.entries()){
                if (!_b.containsValue(entryA.value))
                    return reportInequality(a, b);
                boolean wasLogInequalities = logInequalities;
                logInequalities = false; // Inequalities in the loop may be for non-matching pairs.
                for (ObjectFloatMap.Entry entryB : _b.entries()){
                    if (equals(entryA.key, entryB.key) && equals(entryA.value, entryB.value))
                        continue outer;
                }
                logInequalities = wasLogInequalities;
                return reportInequality(a, b);
            }
            return true;
        } else if (type == ObjectIntMap.class){
            ObjectIntMap<?> _a = (ObjectIntMap<?>)a;
            ObjectIntMap<?> _b = (ObjectIntMap<?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            outer:
            for (ObjectIntMap.Entry entryA : _a.entries()){
                if (!_b.containsValue(entryA.value))
                    return reportInequality("b doesn't contain value " + entryA.value, a, b);
                boolean wasLogInequalities = logInequalities;
                logInequalities = false; // Inequalities in the loop may be for non-matching pairs.
                for (ObjectIntMap.Entry entryB : _b.entries()){
                    if (equals(entryA.key, entryB.key) && equals(entryA.value, entryB.value))
                        continue outer;
                }
                logInequalities = wasLogInequalities;
                return reportInequality(a, b);
            }
            return true;
        } else if (type == ObjectMap.class){
            ObjectMap<?, ?> _a = (ObjectMap<?, ?>)a;
            ObjectMap<?, ?> _b = (ObjectMap<?, ?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            outer:
            for (ObjectMap.Entry entryA : _a.entries()){
                boolean wasLogInequalities = logInequalities;
                logInequalities = false; // Inequalities in the loop may be for non-matching pairs.
                for (ObjectMap.Entry entryB : _b.entries()){
                    if (equals(entryA.key, entryB.key) && equals(entryA.value, entryB.value))
                        continue outer;
                }
                logInequalities = wasLogInequalities;
                return reportInequality(a, b);
            }
            return true;
        } else if (type == ObjectSet.class){
            ObjectSet<?> _a = (ObjectSet<?>)a;
            ObjectSet<?> _b = (ObjectSet<?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            outer:
            for (Object objectA : _a){
                for (Object objectB : _b){
                    if (equals(objectA, objectB))
                        continue outer;
                }
                return reportInequality(a, b);
            }
            return true;
        } else if (type == OrderedMap.class){
            OrderedMap<?, ?> _a = (OrderedMap<?, ?>)a;
            OrderedMap<?, ?> _b = (OrderedMap<?, ?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            OrderedMap.Entries iteratorA = _a.iterator();
            OrderedMap.Entries iteratorB = _b.iterator();
            while (iteratorA.hasNext()){
                if (!iteratorB.hasNext())
                    return reportInequality(a, b);
                ObjectMap.Entry entryA = iteratorA.next();
                ObjectMap.Entry entryB = iteratorB.next();
                if (!equals(entryA.key, entryB.key) || !equals(entryA.value, entryB.value))
                    return reportInequality(a, b);
            }
            return true;
        } else if (type == OrderedSet.class){
            OrderedSet<?> _a = (OrderedSet<?>)a;
            OrderedSet<?> _b = (OrderedSet<?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            OrderedSet.OrderedSetIterator iteratorA = _a.iterator();
            OrderedSet.OrderedSetIterator iteratorB = _b.iterator();
            while (iteratorA.hasNext()){
                if (!iteratorB.hasNext())
                    return reportInequality(a, b);
                if (!equals(iteratorA.next(), iteratorB.next()))
                    return reportInequality(a, b);
            }
            return true;
        } else if (type == Queue.class){
            Queue<?> _a = (Queue<?>)a;
            Queue<?> _b = (Queue<?>)b;
            if (_a.size != _b.size)
                return reportInequality("Size mismatch.\n", a, b);
            for (int i = 0; i < _a.size; i++) {
                if (!equals(_a.get(i), _b.get(i)))
                    return reportInequality(a, b);
            }
            return true;
        } else if (type == SortedIntList.class){
            SortedIntList<?> _a = (SortedIntList<?>)a;
            SortedIntList<?> _b = (SortedIntList<?>)b;
            if (_a.size() != _b.size())
                return reportInequality("Size mismatch.\n", a, b);
            for (int i = 0; i < _a.size(); i++) {
                if (!equals(_a.get(i), _b.get(i)))
                    return reportInequality(a, b);
            }
            return true;
        } else if (type == StringBuilder.class) {
            boolean equal = a.toString().equals(b.toString());
            if (!equal) reportInequality(a, b);
            return equal;
        } else if (type == GraphHeader.class){
            GraphHeader _a = (GraphHeader)a;
            GraphHeader _b = (GraphHeader)b;
            boolean equal = _a.useCompactColor ==_b.useCompactColor && _a.includePixmapDrawingParams == _b.includePixmapDrawingParams &&
                    equals(_a.data, _b.data);
            if (!equal) reportInequality(a, b);
            return equal;
        }
        boolean equal = a.equals(b);
        if (!equal) reportInequality(a, b);
        return equal;
    }

    private static boolean reportInequality (Object a, Object b){
        if (logInequalities) {
            if (a == null)
                System.err.println(String.format("Second object (%s: %s) paired with null", b.getClass().getSimpleName(), b));
            else if (b == null)
                System.err.println(String.format("First object (%s: %s) paired with null", a.getClass().getSimpleName(), a));
            else
                System.err.println(String.format("Inequal objects:\n%s: %s\n%s: %s\n", a.getClass().getSimpleName(), a, b.getClass().getSimpleName(), b));
        }
        return false;
    }

    private static boolean reportInequality (String msg, Object a, Object b){
        if (logInequalities) {
            if (a == null)
                System.err.println(String.format("Second object (%s: %s) paired with null", b.getClass().getSimpleName(), b));
            else if (b == null)
                System.err.println(String.format("First object (%s: %s) paired with null", a.getClass().getSimpleName(), a));
            else
                System.err.println(String.format("Inequal objects (%s):\n%s: %s\n%s: %s\n", msg, a.getClass().getSimpleName(), a, b.getClass().getSimpleName(), b));
        }
        return false;
    }

    float[] generateRandomFloatArray (int length){
        float[] array = new float[length];
        for (int i = 0; i < length; i++) {
            array[i] = random.nextFloat();
        }
        return array;
    }

    int randSize (){
        return random.nextInt(20) + 2;
    }

    int randInt (){
        return random.nextInt(100) - 50;
    }

    float randFloat (){
        return random.nextFloat() * 100f - 50f;
    }

    boolean randBool (){
        return random.nextBoolean();
    }

    <T> T simpleRoundTrip (T object){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Output output = new Output(outStream);
        kryo.writeClassAndObject(output, object);
        output.close();
        byte[] outBytes = outStream.toByteArray();

        ByteArrayInputStream inStream = new ByteArrayInputStream(outBytes);
        Input input = new Input(inStream);
        T object1 = (T)kryo.readClassAndObject(input);
        input.close();

        assertTrue(equals(object, object1));
        return object1;
    }

    byte[] write (Object object){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Output output = new Output(outStream);
        kryo.writeClassAndObject(output, object);
        output.close();
        return outStream.toByteArray();
    }

    <T> T read (byte[] bytes, Class<T> type){
        ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
        Input input = new Input(inStream);
        T object = (T)kryo.readClassAndObject(input);
        input.close();
        return object;
    }
}
