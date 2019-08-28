package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;


/*
class ServerIOTest {

    @Test
    @DisplayName("testIOProcess")
    void testIOProcess() {
        List<String> args;
        args = new ArrayList<>();
        args.add("-c");
        args.add("\" \\\"python src/main.py\\\" \"");
        args.add("-l");
        args.add(" \"MAExample123.lvl\" ");

        Stream<String> response = ServerIO.JarExecutor.executeJar("server.jar", args);

        assertTrue(response.count()>1);



    }
}
*/