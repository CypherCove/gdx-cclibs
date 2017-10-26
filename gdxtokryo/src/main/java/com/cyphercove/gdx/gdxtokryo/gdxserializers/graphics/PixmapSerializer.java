/*******************************************************************************
 * Copyright 2017 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.cyphercove.gdx.gdxtokryo.gdxserializers.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.cyphercove.gdx.gdxtokryo.GraphHeader;
import com.cyphercove.gdx.gdxtokryo.SkippableSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class PixmapSerializer extends SkippableSerializer<Pixmap> {

    private static final int FORMAT_UNKNOWN = 0;
    private static final int FORMAT_ALPHA = 1;
    private static final int FORMAT_INTENSITY = 2;
    private static final int FORMAT_LUMINANCE_ALPHA = 3;
    private static final int FORMAT_RGB565 = 4;
    private static final int FORMAT_RGBA4444 = 5;
    private static final int FORMAT_RGB888 = 6;
    private static final int FORMAT_RGBA8888 = 7;
    private static final int BLENDING_NONE = 0;
    private static final int BLENDING_SOURCE_OVER = 1;
    private static final int FILTER_NEAREST_NEIGHBOR = 0;
    private static final int FILTER_BILINEAR = 1;

    static private final int BUFFER_SIZE = 32000;
    static private final byte[] writeBuffer = new byte[BUFFER_SIZE];
    static private final byte[] readBuffer = new byte[BUFFER_SIZE];

    private ByteArrayOutputStream byteArrayOutputStream;
    private PixmapIO.PNG png;
    private boolean isIncludeDrawingParamsDefault = false;

    private static int toFormatInt (Pixmap.Format format){
        switch (format){
            case Alpha: return FORMAT_ALPHA;
            case Intensity: return FORMAT_INTENSITY;
            case LuminanceAlpha: return FORMAT_LUMINANCE_ALPHA;
            case RGB565: return FORMAT_RGB565;
            case RGBA4444: return FORMAT_RGBA4444;
            case RGB888: return FORMAT_RGB888;
            case RGBA8888: return FORMAT_RGBA8888;
            default: return FORMAT_UNKNOWN;
        }
    }

    private static Pixmap.Format toFormatEnum (int format){
        switch (format){
            case FORMAT_ALPHA: return Pixmap.Format.Alpha;
            case FORMAT_INTENSITY: return Pixmap.Format.Intensity;
            case FORMAT_LUMINANCE_ALPHA: return Pixmap.Format.LuminanceAlpha;
            case FORMAT_RGB565: return Pixmap.Format.RGB565;
            case FORMAT_RGBA4444: return Pixmap.Format.RGBA4444;
            case FORMAT_RGB888: return Pixmap.Format.RGB888;
            case FORMAT_RGBA8888: return Pixmap.Format.RGBA8888;
            default: return Pixmap.Format.RGBA8888;
        }
    }

    private static int toBlendingInt (Pixmap.Blending blending){
        switch (blending){
            case None: return BLENDING_NONE;
            case SourceOver: return BLENDING_SOURCE_OVER;
            default: return BLENDING_NONE;
        }
    }

    private static Pixmap.Blending toBlendingEnum (int blending){
        switch (blending){
            case BLENDING_NONE: return Pixmap.Blending.None;
            case BLENDING_SOURCE_OVER: return Pixmap.Blending.SourceOver;
            default: return Pixmap.Blending.None;
        }
    }

    private static int toFilterInt (Pixmap.Filter filter){
        switch (filter){
            case NearestNeighbour: return FILTER_NEAREST_NEIGHBOR;
            case BiLinear: return FILTER_BILINEAR;
            default: return FILTER_NEAREST_NEIGHBOR;
        }
    }

    private static Pixmap.Filter toFilterEnum (int filter){
        switch (filter){
            case FILTER_NEAREST_NEIGHBOR: return Pixmap.Filter.NearestNeighbour;
            case FILTER_BILINEAR: return Pixmap.Filter.BiLinear;
            default: return Pixmap.Filter.NearestNeighbour;
        }
    }

    public enum Compression { // do not change order
        None, PNG
    }

    public interface CompressionSelector {
        Compression chooseCompressionType (Pixmap pixmap);
    }

    private CompressionSelector compressionSelector = new CompressionSelector() {
        public Compression chooseCompressionType(Pixmap pixmap) {
            Pixmap.Format format = pixmap.getFormat();
            if ((format == Pixmap.Format.RGBA8888 || format == Pixmap.Format.RGBA4444 ||
                    format == Pixmap.Format.RGB888 || format == Pixmap.Format.RGB565) &&
                    pixmap.getWidth() * pixmap.getHeight() >= 16)
                return Compression.PNG;
            return Compression.None;
        }
    };

    /** Set a method for determining what type of {@link Compression} to use for writing a specific Pixmap. By default,
     * Pixmaps are compressed to PNG if their format is RGB or RGBA, and if they have at least 16 pixels. */
    public void setCompressionSelector (CompressionSelector compressionSelector) {
        this.compressionSelector = compressionSelector;
    }

    /** Sets whether drawing parameters (i.e. {@link Pixmap#setColor(Color)}, {@link Pixmap#getBlending()}) should be
     * written and read in the absence of a {@link GraphHeader}. False by default.*/
    public void setIncludeDrawingParamsDefault(boolean includeDrawingParamsDefault) {
        this.isIncludeDrawingParamsDefault = includeDrawingParamsDefault;
    }

    @Override
    public void writeNonNull (Kryo kryo, Output output, Pixmap pixmap) {
        Boolean includeDrawingParams = GraphHeader.isIncludePixmapDrawingParams(kryo);
        if (includeDrawingParams == null) includeDrawingParams = isIncludeDrawingParamsDefault;

        output.writeInt(toFormatInt(pixmap.getFormat()), true);
        if (includeDrawingParams) {
            output.writeInt(toBlendingInt(pixmap.getBlending()), true);
            output.writeInt(toFilterInt(pixmap.getFilter()), true);
            output.writeInt(getPixmapColor(pixmap));
        }

        Compression compression = compressionSelector.chooseCompressionType(pixmap);
        output.writeInt(compression.ordinal(), true);
        if (compression == Compression.None) {
            output.writeInt(pixmap.getWidth());
            output.writeInt(pixmap.getHeight());

            ByteBuffer pixelBuf = pixmap.getPixels();
            pixelBuf.position(0);
            pixelBuf.limit(pixelBuf.capacity());

            int totalBytes = pixelBuf.capacity();

            output.writeInt(totalBytes);

            int remainingBytes = totalBytes % BUFFER_SIZE;
            int iterations = totalBytes / BUFFER_SIZE;

            synchronized (writeBuffer) {
                for (int i = 0; i < iterations; i++) {
                    pixelBuf.get(writeBuffer);
                    output.writeBytes(writeBuffer);
                }

                pixelBuf.get(writeBuffer, 0, remainingBytes);
                output.write(writeBuffer, 0, remainingBytes);
            }

            pixelBuf.position(0);
            pixelBuf.limit(pixelBuf.capacity());
        } else { // PNG
            if (byteArrayOutputStream == null){
                byteArrayOutputStream = new ByteArrayOutputStream(pixmap.getWidth() * pixmap.getHeight());
            }
            if (png == null){
                png = new PixmapIO.PNG((int)(pixmap.getWidth() * pixmap.getHeight() * 2f));
                png.setFlipY(false);
            }
            try {
                png.write(byteArrayOutputStream, pixmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.reset();
            output.writeInt(bytes.length);
            output.writeBytes(bytes);
        }
    }

    @Override
    public Pixmap readOrSkip (Kryo kryo, Input input, Class<Pixmap> type, boolean obsolete) {
        Boolean includeDrawingParams = GraphHeader.isIncludePixmapDrawingParams(kryo);
        if (includeDrawingParams == null) includeDrawingParams = isIncludeDrawingParamsDefault;

        Pixmap.Format format = toFormatEnum(input.readInt(true));
        Pixmap.Blending blending = null;
        Pixmap.Filter filter = null;
        int color = 0;
        if (includeDrawingParams) {
            blending = toBlendingEnum(input.readInt(true));
            filter = toFilterEnum(input.readInt(true));
            color = input.readInt();
        }
        Compression compression = Compression.values()[input.readInt(true)];
        Pixmap pixmap = null;
        if (compression == Compression.None) {
            int width = input.readInt();
            int height = input.readInt();
            int totalBytes = input.readInt();
            if (obsolete){
                input.skip(totalBytes);
            } else {
                pixmap = new Pixmap(width, height, format);

                ByteBuffer pixelBuf = pixmap.getPixels();
                pixelBuf.position(0);
                pixelBuf.limit(pixelBuf.capacity());

                int remainingBytes = totalBytes % BUFFER_SIZE;
                int iterations = totalBytes / BUFFER_SIZE;

                synchronized (readBuffer) {
                    for (int i = 0; i < iterations; i++) {
                        input.read(readBuffer, 0, BUFFER_SIZE);
                        pixelBuf.put(readBuffer, 0, BUFFER_SIZE);
                    }

                    input.read(readBuffer, 0, remainingBytes);
                    pixelBuf.put(readBuffer, 0, remainingBytes);
                }

                pixelBuf.position(0);
                pixelBuf.limit(pixelBuf.capacity());
            }
        } else {
            int bytesLength = input.readInt();
            if (obsolete){
                input.skip(bytesLength);
            } else {
                byte[] bytes = input.readBytes(bytesLength);
                try {
                    Gdx2DPixmap gdx2DPixmap = new Gdx2DPixmap(bytes, 0, bytesLength, Pixmap.Format.toGdx2DPixmapFormat(format));
                    pixmap = new Pixmap(gdx2DPixmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (pixmap != null && includeDrawingParams) {
            pixmap.setBlending(blending);
            pixmap.setFilter(filter);
            pixmap.setColor(color);
        }
        return pixmap;
    }

    @Override
    public Pixmap copy (Kryo kryo, Pixmap original) {
        Boolean includeDrawingParams = GraphHeader.isIncludePixmapDrawingParams(kryo);
        if (includeDrawingParams == null) includeDrawingParams = isIncludeDrawingParamsDefault;

        Pixmap copy = new Pixmap(original.getWidth(), original.getHeight(), original.getFormat());
        copy.setBlending(Pixmap.Blending.None);
        copy.setFilter(Pixmap.Filter.NearestNeighbour);
        copy.drawPixmap(original, 0, 0);
        if (includeDrawingParams) {
            copy.setBlending(original.getBlending());
            copy.setFilter(original.getFilter());
            copy.setColor(getPixmapColor(original));
        } else { // restore defaults
            copy.setBlending(Pixmap.Blending.SourceOver);
            copy.setFilter(Pixmap.Filter.BiLinear);
        }
        return copy;
    }

    private static int getPixmapColor (Pixmap pixmap){
        int color = 0;
        try {
            Field colorField = Pixmap.class.getField("color");
            color = colorField.getInt(pixmap);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return color;
    }
}
