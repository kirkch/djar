package com.mosaic.djar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 *
 */
public class M {

    public static void m( String[] args ) throws IOException {
        Class clazz = M.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            System.out.println( "Class not from JAR" );
            return;
        }
        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                "/META-INF/MANIFEST.MF";


        System.out.println("manifestPath = " + manifestPath);
        Manifest m = new Manifest(new URL(manifestPath).openStream());

        Attributes attr = m.getMainAttributes();

        System.out.println("entries.size() = " + attr.size());
        for ( Object k : attr.keySet() ) {
            System.out.println("k = " + k + " -> " + attr.getValue(k.toString()) );
        }


        String value = attr.getValue("Manifest-Version");

        System.out.println("value = " + value);
    }

}
