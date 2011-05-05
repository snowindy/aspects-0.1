package ru.neoflex.aspectj.logging;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

import ru.neoflex.aspectj.apache.commons.lang.ArrayUtils;

class LoggingLogic {
    private static boolean isThisMethod(Method m, String methodName, Class[] actualParamsTypes) {
        if (m.getName().equals(methodName)) {
            if (Arrays.asList(m.getParameterTypes()).equals(Arrays.asList(actualParamsTypes))) {
                return true;
            }
        }
        return false;
    }

    private static Method getActualMethod(CodeSignature codeSignature) {
        String methodName = codeSignature.getName();

        Method allClassMethods[] = codeSignature.getDeclaringType().getDeclaredMethods();
        Class[] actualParamsTypes = codeSignature.getParameterTypes();

        int size = allClassMethods.length;
        for (int i = 0; i < size; i++) {
            if (isThisMethod(allClassMethods[i], methodName, actualParamsTypes)) {
                return allClassMethods[i];
            }
        }
        throw new IllegalArgumentException("Method not found : " + codeSignature);

    }

    public static String printInputParams(JoinPoint joinPoint, CodeSignature codeSignature) throws Exception {
        Object[] args = joinPoint.getArgs();
        String[] argNames = codeSignature.getParameterNames();

        Method actualMethod = getActualMethod(codeSignature);

        Annotation[][] parameterAnnotations = actualMethod.getParameterAnnotations();

        int count = argNames.length;

        BitSet finestLogSet = new BitSet(count);

        int j = 0;
        for (Annotation[] annotations : parameterAnnotations) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Finest) {
                    finestLogSet.set(j);
                    break;
                }
            }
            j++;
        }

        StringBuilder sb = new StringBuilder();

        for (int jj = 0; jj < count; jj++) {
            sb.append(argNames[jj]);
            if (args[jj] == null) {
                sb.append(" = null; ");
            } else {
                sb.append(" = ");
                sb.append(forLog(jj, args[jj], finestLogSet));
                sb.append("; ");
            }
        }

        return sb.toString();
    }

    public static String forLog(Object obj) throws Exception {
        return forLog(-1, obj, null);
    }


    private static List boxPrimitiveArray(Object obj) throws Exception {
        String type = obj.getClass().getComponentType().getSimpleName();
        if ("int".equals(type)) {
            return Arrays.asList(ArrayUtils.toObject((int[]) obj));
        }
        if ("short".equals(type)) {
            return Arrays.asList(ArrayUtils.toObject((short[]) obj));
        }
        if ("long".equals(type)) {
            return Arrays.asList(ArrayUtils.toObject((long[]) obj));
        }
        if ("double".equals(type)) {
            return Arrays.asList(ArrayUtils.toObject((double[]) obj));
        }
        if ("float".equals(type)) {
            return Arrays.asList(ArrayUtils.toObject((float[]) obj));
        }
        if ("char".equals(type)) {
            return Arrays.asList(ArrayUtils.toObject((char[]) obj));
        }
        if ("byte".equals(type)) {
            return Arrays.asList(ArrayUtils.toObject((byte[]) obj));
        }
        if ("boolean".equals(type)) {
            return Arrays.asList(ArrayUtils.toObject((boolean[]) obj));
        }
        throw new Exception("Type is not recognized: " + obj.getClass().getComponentType());
    }

    private static String forLog(int idx, Object obj, BitSet finestLogSet) throws Exception {
        boolean finest = false;
        if (finestLogSet != null) {
            finest = finestLogSet.get(idx);
        }
        
        Collection collObj = null;
        String collType = null;

        try {
            if (obj.getClass().isArray()) {
                collObj = Arrays.asList((Object[]) obj);
                collType = obj.getClass().getComponentType().getSimpleName();
            }
        } catch (ClassCastException e) {
            collObj = boxPrimitiveArray(obj);
            collType = obj.getClass().getComponentType().getSimpleName();
        }

        if (collObj != null) {
            if (finest) {
                return String.valueOf(collObj);
            } else {
                return "["+collType+": size = " + collObj.size() + "]";
            }

        }

        return "'" + obj + "'";
    }
}
