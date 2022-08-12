package com.example.dataFunctions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class LocationInit {

    static String dataSaveLocation;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initialise(){
        String baseLocation = System.getenv("APPDATA");
        dataSaveLocation = baseLocation + "\\ReportGenerator";
        new File(dataSaveLocation).mkdir();
    }

    public static String getPath(){
        return dataSaveLocation;
    }

    public static void deletePath() throws IOException {
        FileUtils.deleteDirectory(new File(dataSaveLocation));
    }

}
