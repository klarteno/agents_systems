package env;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WorldEnvTest {

    @Test
    @DisplayName("WorldEnv testing")
    void worldEnvInitTest() {
        WorldEnv  worldEnv = null;
        assertNull(worldEnv);
        worldEnv = new WorldEnv();
        assertNotNull(worldEnv);

/*
        int length = worldEnv.getSolutionLength();

        assertTrue(length>0);
*/
/*

        worldEnv.executePlanner();
*/
    }
}
