package com.mosaic.djar;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class DJar {

    // http://repo1.maven.org/maven2/junit/junit/4.11/junit-4.11.jar
    // junit:junit:jar:4.11


    // dependency file:
    // junit;junit;4.11;http://repo1.maven.org/maven2/junit/junit/4.11/junit-4.11.jar


    public static void main( String[] args ) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        URL owningJar = getOwningJarURL(DJar.class);

        List<URL> localJarURLs = new ArrayList<URL>();
        localJarURLs.add( owningJar );


        InputStream in1 = locateLocalResource(owningJar, "/META-INF/DEPENDENCIES");
        BufferedReader in = new BufferedReader( new InputStreamReader(in1) );

        String line = in.readLine();
        while ( line != null ) {
            String[] parts = line.split(";");
            String group = parts[0];
            String artifact = parts[1];
            String version = parts[2];
            String url = parts[3];

            File   localFile = locateArtifactLocally( group, artifact, version );

            if ( !localFile.exists() ) {
                System.out.println( "downloading " + url + " to " + localFile );
                downloadArtifact( url, localFile);
            } else {
                System.out.println( "located " + url + " at " + localFile);
            }

            URL localJar = localFile.toURI().toURL();
            localJarURLs.add( localJar );
            System.out.println("localJar = " + localJar);

            line = in.readLine();
        }


        ClassLoader boot = DJar.class.getClassLoader();
        while ( boot.getParent() != null ) {
            boot = boot.getParent();
        }


        ClassLoader cl = new URLClassLoader( localJarURLs.toArray(new URL[localJarURLs.size()]),boot );


        Class  c = cl.loadClass("com.world.HelloMain");
        Method m = c.getMethod("main", args.getClass() );


        m.invoke(null, new Object[] {args} );
    }

    private static URL getOwningJarURL( Class<?> clazz ) throws MalformedURLException {
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();

        String url;
        if ( classPath.startsWith("jar") ) {
            url = classPath.substring(0, classPath.lastIndexOf("!") + 1);
        } else {
            url = classPath.substring(0,classPath.lastIndexOf("/com"));
        }

        return new URL(url);
    }

    private static InputStream locateLocalResource( URL rootLocationURL, String relativePath ) throws IOException {
        if ( !relativePath.startsWith("/") ) {
            relativePath = "/" + relativePath;
        }

        String manifestPath = rootLocationURL + relativePath;

        return new URL(manifestPath).openStream();
    }

    private static File locateArtifactLocally( String group, String artifactName, String version ) {
        String expectedPath = System.getProperty("user.home")+"/.m2/repository/" + group.replaceAll("\\.","/") + "/" + artifactName + "/" + version + "/" + artifactName + "-" + version + ".jar";

        return new File(expectedPath);
    }

    private static void downloadArtifact( String remoteURL, File targetLocation ) throws IOException {
        targetLocation.getAbsoluteFile().getParentFile().mkdirs();


        InputStream in = new URL( remoteURL ).openStream();

        try {
            FileOutputStream out = new FileOutputStream(targetLocation);

            try {
                byte[] buf = new byte[4096];

                int numBytesRead = in.read(buf);
                while ( numBytesRead >= 0 ) {
                    out.write(buf,0,numBytesRead);

                    numBytesRead = in.read(buf);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

}
