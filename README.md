# Intelligent Traffic Lights – Java Simulation (CLI + JavaFX GUI)

Command-line + GUI simulation of a 4-way intersection that adapts green phases to traffic intensity.

## Build (Java 17 + Maven 3.9+)
```bash
mvn -q -DskipTests package
```

### CLI usage
```bash
java -jar target/traffic-lights-1.0.0.jar --f input.json output.json
```
If you run without `--f`, the **JavaFX GUI** starts.

### Run GUI directly
```bash
mvn -q -DskipTests package
mvn javafx:run
```

### Docker (CLI mode)
```bash
docker build -t traffic-lights:1.0 .
docker run --rm -v "$PWD"/samples:/data traffic-lights:1.0 --f /data/input.json /data/output.json
```

### Example I/O
Input and output JSON match the format from the task statement (see `samples/` directory).

## Design summary
- Safety: Only one axis is green at a time: **NS** (north+south) or **EW** (east+west).
- Throughput per step: up to 2 vehicles (one per direction on the active axis).
- Scheduler: Weighted round-robin with aging and optional hold to avoid flip-flopping.
- Extensible: lanes, pedestrians, emergency preemption, green arrows, metrics.

## GUI
- Shows queues for N/S/E/W, current green axis, simple lights and controls.
- Buttons to add vehicles (random ids), perform single `Step`, or `Auto Run` (toggle).

### GUI movement selection
Each road panel now has a **Turn** selector (straight/left/right) that sets the vehicle's destination before adding it.
