package app;

import app.io.*;
import app.model.*;
import app.sched.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {

    @Test
    void provided_sample_matches_expected() throws Exception {
        String input = """
        {"commands": [
          {"type":"addVehicle","vehicleId":"vehicle1","startRoad":"south","endRoad":"north"},
          {"type":"addVehicle","vehicleId":"vehicle2","startRoad":"north","endRoad":"south"},
          {"type":"step"},
          {"type":"step"},
          {"type":"addVehicle","vehicleId":"vehicle3","startRoad":"west","endRoad":"south"},
          {"type":"addVehicle","vehicleId":"vehicle4","startRoad":"west","endRoad":"south"},
          {"type":"step"},
          {"type":"step"}
        ]}
        """;
        var tmpIn = File.createTempFile("input",".json");
        var tmpOut = File.createTempFile("out",".json");
        Files.writeString(tmpIn.toPath(), input);

        Main.main(new String[]{"--f", tmpIn.getAbsolutePath(), tmpOut.getAbsolutePath()});

        ObjectMapper om = new ObjectMapper();
        OutputFile out = om.readValue(tmpOut, OutputFile.class);
        assertEquals(4, out.stepStatuses.size());
        assertEquals(List.of("vehicle1", "vehicle2"), out.stepStatuses.get(0).leftVehicles);
        assertEquals(List.of(), out.stepStatuses.get(1).leftVehicles);
        assertEquals(List.of("vehicle3"), out.stepStatuses.get(2).leftVehicles);
        assertEquals(List.of("vehicle4"), out.stepStatuses.get(3).leftVehicles);
    }

    @Test
    void fairness_when_one_axis_starves() {
        var x = new Intersection();
        var sched = new WeightedScheduler(x, 1, 0);
        for (int i=0;i<5;i++) x.north.add(new Vehicle("n"+i, Direction.NORTH, Direction.SOUTH));
        x.east.add(new Vehicle("e0", Direction.EAST, Direction.WEST));

        assertEquals(Phase.NS, sched.decideNextPhase());
        x.passThroughAxis(Phase.NS);
        try { Thread.sleep(3); } catch (InterruptedException ignored) {}
        assertEquals(Phase.NS, sched.decideNextPhase());
    }
}
