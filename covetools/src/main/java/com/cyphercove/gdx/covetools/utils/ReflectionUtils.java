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
package com.cyphercove.gdx.covetools.utils;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;

/**
 * Reflection methods that cannot be done with the LibGDX reflection API. This class cannot be used in GWT.
 */
public class ReflectionUtils {

    private static Field getField (com.badlogic.gdx.utils.reflect.Field field){
        try {
            Field fieldField = com.badlogic.gdx.utils.reflect.Field.class.getDeclaredField("field");
            fieldField.setAccessible(true);
            return (Field) fieldField.get(field);
        }
        catch (NoSuchFieldException e) { }
        catch (IllegalAccessException e){ }
        throw new GdxRuntimeException("Cannot get Java field value from LibGDX Field.");
    }

    public static void makeFieldNonFinal (com.badlogic.gdx.utils.reflect.Field field) throws ReflectionException {
        Field fieldJ = getField(field);

        Exception exception = null;

        if (field.isFinal()){
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(fieldJ, fieldJ.getModifiers() & ~Modifier.FINAL);
            } catch (AccessControlException e) {
                exception = e;
            }catch (NoSuchFieldException e){
                exception = e;
            } catch (IllegalAccessException e){
                exception = e;
            }
        }
        if (exception != null){
            throw new ReflectionException(String.format("Cannot make field %s of class %s non-final", field.getName(),
                    field.getClass().getName()), exception);
        }
    }
}
