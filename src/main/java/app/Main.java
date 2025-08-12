package app;

import app.io.*;
import app.model.*;
import app.sched.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            app.view.TrafficLightGUI.launchGUI();
            return;
        }
        if (!"--f".equals(args[0]) || args.length < 3) {
            System.out.println("Usage: java -jar app.jar --f <input.json> <output.json>");
            System.exit(1);
        }
        File in = new File(args[1]);
        File out = new File(args[2]);

        ObjectMapper om = new ObjectMapper();
        CommandFile cf = CommandFile.read(in, om);

        Intersection intersection = new Intersection();
        WeightedScheduler scheduler = new WeightedScheduler(intersection, /*agingQuantumMs*/ 1500, /*holdSteps*/ 1);
        OutputFile of = new OutputFile();

        for (CommandFile.Command c : cf.commands) {
            if (c instanceof CommandFile.AddVehicle av) {
                var v = new Vehicle(av.vehicleId, Direction.fromString(av.startRoad), Direction.fromString(av.endRoad));
                intersection.by(v.getStart()).add(v);
            } else if (c instanceof CommandFile.Step) {
                Phase p = scheduler.decideNextPhase();
                List<String> left = intersection.passThroughAxis(p);
                of.stepStatuses.add(new StepStatus(left));
            }
        }
        om.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT).writeValue(out, of);
    }
}
