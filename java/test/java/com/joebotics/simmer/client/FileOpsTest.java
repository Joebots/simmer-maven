package com.joebotics.simmer.client;

//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.asset.EmptyAsset;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.*;
import java.util.Scanner;

import static org.mockito.Mockito.mock;

@SuppressWarnings("NonJREEmulationClassesInClientCode")
@RunWith(JUnit4.class)
public class FileOpsTest {
    private String contents;

    @Before
    public void setup()
            throws IOException
    {
//        Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("setuplist.txt"));
//        contents = scanner.useDelimiter("\\A").next();
//        scanner.close();
        File f = new File("src/main/webapp/setuplist.txt");
        byte[] b = new byte[(int)f.length()];
        new FileInputStream(f).read(b);
        contents = new String(b);
    }

    @Test
    public void processLegacyCircuitCatalog() {

        Simmer mockSimmer = mock(Simmer.class);
        FileOps undertest = new FileOps(mockSimmer);
        undertest.processLegacyCircuitCatalog(contents.getBytes(), contents.length(), false);

    }
}
