package app.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandFileTest {
    @Test void parses_polymorphic_commands() throws Exception {
        String json = "{ \"commands\": [" +
                "{\"type\":\"addVehicle\",\"vehicleId\":\"v1\",\"startRoad\":\"north\",\"endRoad\":\"south\"}," +
                "{\"type\":\"step\"}" +
                "]}";
        ObjectMapper om = new ObjectMapper();
        CommandFile cf = om.readValue(json, CommandFile.class);
        assertEquals(2, cf.commands.size());
        assertTrue(cf.commands.get(0) instanceof CommandFile.AddVehicle);
        assertTrue(cf.commands.get(1) instanceof CommandFile.Step);
    }
}
