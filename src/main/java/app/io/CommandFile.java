package app.io;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;

public class CommandFile {
    public List<Command> commands;

    public static CommandFile read(File f, ObjectMapper om) throws Exception {
        return om.readValue(f, CommandFile.class);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = AddVehicle.class, name = "addVehicle"),
        @JsonSubTypes.Type(value = Step.class,       name = "step")
    })
    public interface Command {}

    public static class AddVehicle implements Command {
        public String vehicleId;
        public String startRoad;
        public String endRoad;
        public AddVehicle() {}
    }

    public static class Step implements Command { public Step() {} }
}
