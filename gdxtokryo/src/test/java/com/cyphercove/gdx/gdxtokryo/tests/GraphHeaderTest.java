package com.cyphercove.gdx.gdxtokryo.tests;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.GridPoint2;
import com.cyphercove.gdx.gdxtokryo.GdxToKryo;
import com.cyphercove.gdx.gdxtokryo.GraphHeader;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class GraphHeaderTest extends GdxToKryoTest {

    public static void main(String[] args) {
        GraphHeaderTest test = new GraphHeaderTest();
        try {
            test.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        test.testRemovedPixmapField();
    }

    public void testRemovedPixmapField(){
        GraphHeader.currentReadWriteVersion = 0;

        TestClass object = new TestClass();
        object.someInt = randInt();
        object.someFloat = randFloat();
        object.point = new GridPoint2(randInt(), randInt());
        object.color = new Color(Color.CYAN);
        object.smallTestClass = new SmallTestClass();
        object.smallTestClass.someValue = randInt();

        Pixmap pixmap = new Pixmap(20, 24, Pixmap.Format.RGBA8888);
        for (int i = 0; i < pixmap.getWidth(); i++) {
            for (int j = 0; j < pixmap.getHeight(); j++) {
                pixmap.drawPixel(i, j, randInt());
            }
        }
        object.smallTestClass.somePixmap = pixmap;

        kryo.register(SmallTestClass.class);
        kryo.register(TestClass.class);

        GraphHeader<TestClass> graphHeader = new GraphHeader<TestClass>();
        graphHeader.data = object;

        GraphHeader.currentReadWriteVersion = 0;
        byte[] written = write(graphHeader);

        GraphHeader.currentReadWriteVersion = 1;
        GraphHeader<TestClass> returned = read(written, GraphHeader.class);
        assertTrue(equals(graphHeader, returned));
        assertTrue(returned.data.smallTestClass.somePixmap == null);
    }

    static class SmallTestClass implements KryoSerializable{
        int someValue;
        Pixmap somePixmap; // treat as removed in later version

        @Override
        public void write(Kryo kryo, Output output) {
            // Write the data as a version 0 object.
            // Normally, an object would always be written as the latest version.
            output.writeInt(someValue);
            kryo.writeObject(output, somePixmap); // later version would not write this
        }

        @Override
        public void read(Kryo kryo, Input input) {
            int version = GraphHeader.getWrittenVersion(kryo);

            someValue = input.readInt();
            if (version == 0){ // old data but pixmap is now obsolete
                GdxToKryo.skipObsoleteObject(kryo, input, Pixmap.class);
            }
        }

        // version 1 of class doesn't use deprecated pixmap
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SmallTestClass that = (SmallTestClass) o;

            return someValue == that.someValue;
        }
    }

    static class TestClass {
        public int someInt;
        public float someFloat;
        public GridPoint2 point;
        public Color color; // treat as added in later version
        public SmallTestClass smallTestClass;

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestClass testClass = (TestClass) o;

            if (someInt != testClass.someInt) return false;
            if (Float.compare(testClass.someFloat, someFloat) != 0) return false;
            if (point != null ? !point.equals(testClass.point) : testClass.point != null) return false;
            if (color != null ? !color.equals(testClass.color) : testClass.color != null) return false;
            return smallTestClass != null ? smallTestClass.equals(testClass.smallTestClass) : testClass.smallTestClass == null;
        }
    }



}
