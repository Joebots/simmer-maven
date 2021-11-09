package com.joebotics.simmer.client;

import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.*;

import static org.mockito.Mockito.mock;

@SuppressWarnings("NonJREEmulationClassesInClientCode")
@RunWith(JUnit4.class)
public class FileOpsTest extends GWTTestCase {

    public String read(String filePath)
            throws IOException
    {
        File f = new File(filePath);
        byte[] b = new byte[(int)f.length()];
        new FileInputStream(f).read(b);
        return new String(b);
    }

    @Test
    public void processLegacyCircuitCatalog() throws IOException {

        String contents = read("src/main/webapp/setuplist.txt");
        Simmer mockSimmer = mock(Simmer.class);
        FileOps undertest = new FileOps(mockSimmer);
        undertest.processLegacyCircuitCatalog(contents.getBytes(), contents.length(), false);

    }

//    @Test
//    public void processXmlCircuitCatalog() throws IOException {
//
//        String contents = read("src/main/webapp/circuits/catalog.xml");
//        Simmer mockSimmer = mock(Simmer.class);
//        FileOps undertest = new FileOps(mockSimmer);
//        TreeNode<CircuitLinkInfo> xmlTree = undertest.processXmlCircuitCatalog(contents);
//
//    }
//
//    @Test
//    public void compareXmlAndLegacyCircuitCatalog() throws IOException {
//
//        Simmer mockSimmer = mock(Simmer.class);
//        FileOps undertest = new FileOps(mockSimmer);
//
//        String legacy = read("src/main/webapp/setuplist.txt");
//        TreeNode<CircuitLinkInfo> legacyTree = undertest.processLegacyCircuitCatalog(legacy.getBytes(), legacy.length(), false);
//
//        String xml = read("src/main/webapp/circuits/catalog.xml");
//        TreeNode<CircuitLinkInfo> xmlTree = undertest.processXmlCircuitCatalog(xml);
//
//        System.out.println("ff");
//
//    }

    @Override
    public String getModuleName() {
        return "com.joebotics.simmer.simmer";
    }
}
