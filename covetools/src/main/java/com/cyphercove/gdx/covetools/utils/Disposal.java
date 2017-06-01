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

import java.lang.annotation.*;
import java.security.AccessControlException;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.*;

/**
 * Convenience methods for disposing and nulling fields for Disposable objects.
 *
 * @author cypherdare
 */
public class Disposal {

    /** Disposes all {@link Disposable} objects referenced by fields in the specified object and sets those field
     * values to null.
     * @param object The object containing references to Disposable objects.
     */
    public static void disposeAll (Object object){
        disposeAllExcept(object);
    }

    /** Disposes all {@link Disposable} objects referenced by fields in the specified object and sets those field
     * values to null, except those specified.
     * @param object The object containing references to Disposable objects.
     * @param skippedGroup Fields annotated with {@link Group} with matching numbers will not be disposed.
     */
    public static void disposeAllExcept (Object object, int... skippedGroup){
        Field[] fields = ClassReflection.getFields(object.getClass());
        boolean checkGroups = skippedGroup != null && skippedGroup.length > 0;
        outer:
        for (Field field : fields){
            if (checkGroups) {
                com.badlogic.gdx.utils.reflect.Annotation groupAnnotation = field.getDeclaredAnnotation(Group.class);
                if (groupAnnotation != null) {
                    Group group = groupAnnotation.getAnnotation(Group.class);
                    for (int i : skippedGroup)
                        if (i == group.value())
                            continue outer;
                }
            }
            disposeAndNullField(object, field);
        }
    }

    /** Disposes all {@link Disposable} objects referenced by fields in the specified object and sets those field
     * values to null, if they are tagged with the corresponding group number.
     * @param object The object containing references to Disposable objects.
     * @param groupNumber The {@link Group} value of fields that will be disposed.
     */
    public static void disposeGroup (Object object, int groupNumber){
        Field[] fields = ClassReflection.getFields(object.getClass());
        for (Field field : fields){
            com.badlogic.gdx.utils.reflect.Annotation groupAnnotation = field.getDeclaredAnnotation(Group.class);
            if (groupAnnotation == null || groupAnnotation.getAnnotation(Group.class).value() != groupNumber)
                continue;
            disposeAndNullField(object, field);
        }
    }

    private static void disposeAndNullField (Object object, Field field){
        makeAccessible(field);
        Object referenced = null;
        try {
            referenced = field.get(object);
        } catch (ReflectionException e) {
            if (field.getDeclaringClass().isAssignableFrom(Disposable.class)){
                throw new GdxRuntimeException("Failed to read Disposable field " + field.getName(), e);
            }
        }
        if (referenced instanceof Disposable){
            ((Disposable) referenced).dispose();
            try {
                field.set(object, null);
            } catch (ReflectionException e) {
                throw new GdxRuntimeException("Failed to write null to Disposable field " + field.getName(), e);
            }
        }
    }

    private static void makeAccessible (Field field){
        if (!field.isAccessible()) {
            try {
                field.setAccessible(true);
            } catch (AccessControlException ex) {
                throw new GdxRuntimeException(String.format("Field %s cannot be made accessible", field.getName()));
            }
        }
    }

    /**
     * Annotation for a field to give it a named group for use with the {@link #disposeGroup(Object, int)} method.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Group {
        int value(); // the paths
    }
}
