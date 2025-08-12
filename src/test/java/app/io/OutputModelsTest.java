package app.io;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OutputModelsTest {
    @Test void step_status_holds_ids() {
        var s = new StepStatus(List.of("a","b"));
        assertEquals(List.of("a","b"), s.leftVehicles);
    }
    @Test void output_file_collects_statuses() {
        var o = new OutputFile();
        o.stepStatuses.add(new StepStatus(List.of("x")));
        assertEquals(1, o.stepStatuses.size());
    }
}
