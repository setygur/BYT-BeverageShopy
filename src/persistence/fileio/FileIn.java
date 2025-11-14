package persistence.fileio;

import persistence.JsonCtor;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileIn {

    public static void readJson(Path file, Collection<Class<?>> knownModels) throws IOException {
        String json;
        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            json = sb.toString();
        }

        Object rootObj = JsonMini.parse(json);
        if (!(rootObj instanceof Map)) return;
        Map<?, ?> root = (Map<?, ?>) rootObj;

        Object modelsObj = root.get("models");
        if (!(modelsObj instanceof Map)) return;
        Map<?, ?> models = (Map<?, ?>) modelsObj;

        for (Map.Entry<?, ?> e : models.entrySet()) {
            String fqcn = String.valueOf(e.getKey());
            Object block = e.getValue();
            if (!(block instanceof Map)) continue;
            Map<?, ?> classBlock = (Map<?, ?>) block;

            Class<?> clazz = resolveClass(fqcn, knownModels);
            if (clazz == null) {
                System.err.println("Unknown class: " + fqcn + " (skipping)");
                continue;
            }

            Object stat = classBlock.get("static");
            if (stat instanceof Map) {
                restoreStaticFields(clazz, (Map<?, ?>) stat);
            }

            Object arr = classBlock.get("objects");
            if (!(arr instanceof List)) continue;
            List<?> items = (List<?>) arr;

            for (Object item : items) {
                if (!(item instanceof Map)) continue;
                Map<?, ?> objMap = (Map<?, ?>) item;
                try {
                    Object instance = constructViaAnnotatedCtorUsingParamNames(clazz, objMap);
                } catch (InvocationTargetException ite) {
                    Throwable cause = ite.getTargetException();
                    if (isValidationException(cause)) {
                        System.err.println("ValidationException in " + clazz.getSimpleName()
                                + ": " + cause.getMessage() + " — skipping " + objMap);
                    } else {
                        System.err.println("Ctor threw " + cause.getClass().getSimpleName()
                                + ": " + cause.getMessage() + " — skipping " + objMap);
                    }
                } catch (ReflectiveOperationException | IllegalArgumentException ex) {
                    System.err.println("Failed to construct " + clazz.getSimpleName()
                            + ": " + ex.getMessage() + " — skipping " + objMap);
                }
            }
        }
    }

    private static Class<?> resolveClass(String fqcn, Collection<Class<?>> known) {
        for (Class<?> c : known) if (c.getName().equals(fqcn)) return c;
        try { return Class.forName(fqcn); } catch (ClassNotFoundException e) { return null; }
    }

    private static void restoreStaticFields(Class<?> clazz, Map<?, ?> stat) {
        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) continue;
            if (hasAnnotation(f, "ObjectList")) continue;
            if (!stat.containsKey(f.getName())) continue;

            Object raw = stat.get(f.getName());
            Object coerced = coerce(raw, f.getType());
            try {
                f.setAccessible(true);
                f.set(null, coerced);
            } catch (IllegalAccessException ex) {
                System.err.println("Cannot set " + clazz.getSimpleName() + "." + f.getName() + ": " + ex.getMessage());
            }
        }
    }

    /** Use @JsonCtor constructor; parameter names (via -parameters) must match JSON keys. */
    private static Object constructViaAnnotatedCtorUsingParamNames(Class<?> clazz, Map<?, ?> obj)
            throws ReflectiveOperationException {

        Constructor<?> target = null;
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            if (c.isAnnotationPresent(JsonCtor.class)) { target = c; break; }
        }
        if (target == null) throw new NoSuchMethodException("@JsonCtor not found in " + clazz.getName());

        target.setAccessible(true);
        Parameter[] params = target.getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            String key = params[i].getName();
            Object raw = obj.get(key);
            Object coerced = coerce(raw, params[i].getType());
            if (coerced == null && params[i].getType().isPrimitive()) {
                throw new IllegalArgumentException("Missing primitive arg '" + key + "' for " + clazz.getName());
            }
            args[i] = coerced;
        }
        return target.newInstance(args);
    }

    private static boolean isValidationException(Throwable t) {
        if (t == null) return false;
        return "ValidationException".equals(t.getClass().getSimpleName());
    }

    private static boolean hasAnnotation(Field f, String simpleName) {
        for (Annotation a : f.getAnnotations()) {
            if (a.annotationType().getSimpleName().equals(simpleName)) return true;
        }
        return false;
    }

    /** Minimal type coercion for String/number/boolean/char/null; pass-through for Map/List. */
//    @SuppressWarnings("unchecked")
    private static Object coerce(Object raw, Class<?> target) {
        if (raw == null) return null;
        if (target == String.class) return String.valueOf(raw);
        if (target == int.class || target == Integer.class) return toNumber(raw).intValue();
        if (target == long.class || target == Long.class) return toNumber(raw).longValue();
        if (target == double.class || target == Double.class) return toNumber(raw).doubleValue();
        if (target == float.class || target == Float.class) return toNumber(raw).floatValue();
        if (target == boolean.class || target == Boolean.class) return toBoolean(raw);
        if (target == char.class || target == Character.class) {
            String s = String.valueOf(raw);
            return s.isEmpty() ? '\0' : s.charAt(0);
        }
        if (raw instanceof Map && Map.class.isAssignableFrom(target)) return raw;
        if (raw instanceof List && List.class.isAssignableFrom(target)) return raw;
        return raw;
    }

    private static Number toNumber(Object o) {
        if (o instanceof Number n) return n;
        String s = String.valueOf(o);
        if (s.contains(".") || s.contains("e") || s.contains("E")) return Double.parseDouble(s);
        long L = Long.parseLong(s);
        if (L >= Integer.MIN_VALUE && L <= Integer.MAX_VALUE) return (int) L;
        return L;
    }

    private static boolean toBoolean(Object o) {
        if (o instanceof Boolean b) return b;
        String s = String.valueOf(o).toLowerCase(Locale.ROOT);
        return s.equals("true") || s.equals("1") || s.equals("yes");
    }

    /** Tiny JSON parser supporting objects, arrays, strings, numbers, booleans, null. */
    static final class JsonMini {
        private final String s;
        private int i;
        private JsonMini(String s){
            this.s=s;
        }

        static Object parse(String json){ return new JsonMini(json).val(); }

        private void ws(){ while(i<s.length() && " \n\r\t".indexOf(s.charAt(i))>=0) i++; }

        private Object val(){
            ws(); if(i>=s.length()) throw err("Unexpected end");
            char c=s.charAt(i);
            if(c=='{') return obj();
            if(c=='[') return arr();
            if(c=='"') return str();
            if(c=='t'||c=='f') return bool();
            if(c=='n') return nul();
            if(c=='-'||Character.isDigit(c)) return num();
            throw err("Unexpected char '"+c+"'");
        }

        private Map<String,Object> obj(){
            Map<String,Object> m=new LinkedHashMap<>();
            i++; ws(); if(s.charAt(i)=='}'){ i++; return m; }
            while(true){
                ws(); String k=str();
                ws(); if(s.charAt(i++)!=':') throw err("Expected ':'");
                Object v=val(); m.put(k,v);
                ws(); char c=s.charAt(i++);
                if(c=='}') return m;
                if(c!=',') throw err("Expected ',' or '}'");
            }
        }

        private List<Object> arr(){
            List<Object> a=new ArrayList<>(); i++; ws();
            if(s.charAt(i)==']'){ i++; return a; }
            while(true){
                Object v=val(); a.add(v);
                ws(); char c=s.charAt(i++);
                if(c==']') return a;
                if(c!=',') throw err("Expected ',' or ']'");
            }
        }

        private String str(){
            if(s.charAt(i)!='"') throw err("Expected '\"'");
            i++; StringBuilder b=new StringBuilder();
            while(i<s.length()){
                char c=s.charAt(i++);
                if(c=='"') return b.toString();
                if(c=='\\'){
                    if(i>=s.length()) throw err("Bad escape");
                    char e=s.charAt(i++);
                    switch(e){
                        case '"': b.append('"'); break;
                        case '\\': b.append('\\'); break;
                        case '/': b.append('/'); break;
                        case 'b': b.append('\b'); break;
                        case 'f': b.append('\f'); break;
                        case 'n': b.append('\n'); break;
                        case 'r': b.append('\r'); break;
                        case 't': b.append('\t'); break;
                        case 'u':
                            if(i+4> s.length()) throw err("Bad \\u");
                            int cp=Integer.parseInt(s.substring(i,i+4),16);
                            b.append((char)cp); i+=4; break;
                        default: throw err("Bad escape: \\"+e);
                    }
                } else {
                    b.append(c);
                }
            }
            throw err("Unterminated string");
        }

        private Boolean bool(){
            if(s.startsWith("true",i)){ i+=4; return true; }
            if(s.startsWith("false",i)){ i+=5; return false; }
            throw err("Bad boolean");
        }

        private Object nul(){
            if(s.startsWith("null",i)){ i+=4; return null; }
            throw err("Bad null");
        }

        private Number num(){
            int start=i; if(s.charAt(i)=='-') i++;
            while(i<s.length() && Character.isDigit(s.charAt(i))) i++;
            boolean frac=false, exp=false;
            if(i<s.length() && s.charAt(i)=='.'){ frac=true; i++; while(i<s.length()&&Character.isDigit(s.charAt(i))) i++; }
            if(i<s.length() && (s.charAt(i)=='e'||s.charAt(i)=='E')){
                exp=true; i++; if(i<s.length()&&(s.charAt(i)=='+'||s.charAt(i)=='-')) i++;
                while(i<s.length()&&Character.isDigit(s.charAt(i))) i++;
            }
            String token=s.substring(start,i);
            try{
                if(frac||exp) return Double.parseDouble(token);
                long L=Long.parseLong(token);
                if(L>=Integer.MIN_VALUE && L<=Integer.MAX_VALUE) return (int)L;
                return L;
            }catch(NumberFormatException e){ throw err("Bad number"); }
        }

        private RuntimeException err(String msg){ return new RuntimeException(msg+" at pos "+i); }
    }
}
