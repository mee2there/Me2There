package com.innovation.me2there.util;

import android.util.Log;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by ashley on 9/6/15.
 */

public class PojoMapper {

    private static ObjectMapper m = new ObjectMapper();
    private static JsonFactory jf = new JsonFactory();

    public static <T> Object fromJson(String jsonAsString, Class<T> pojoClass)
            throws JsonMappingException, JsonParseException, IOException {
        return m.readValue(jsonAsString, pojoClass);
    }

    public static <T> Object fromJson(FileReader fr, Class<T> pojoClass)
            throws JsonParseException, IOException
    {
        return m.readValue(fr, pojoClass);
    }

    public static String toJson(Object pojo, boolean prettyPrint)
            throws JsonMappingException, JsonGenerationException, IOException {
        Log.i("PojoMapper", "toJson");

        StringWriter sw = new StringWriter();
        JsonGenerator jg = jf.createJsonGenerator(sw);
        if (prettyPrint) {
            jg.useDefaultPrettyPrinter();
        }
        m.writeValue(jg, pojo);
        Log.i("PojoMapper", "toJson value");

        return sw.toString();
    }

    public static void toJson(Object pojo, FileWriter fw, boolean prettyPrint)
            throws JsonMappingException, JsonGenerationException, IOException {
        JsonGenerator jg = jf.createJsonGenerator(fw);
        if (prettyPrint) {
            jg.useDefaultPrettyPrinter();
        }
        m.writeValue(jg, pojo);
    }
}
