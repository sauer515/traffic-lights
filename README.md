# Intelligent Traffic Light Simulation

## Overview
This project simulates an **intelligent traffic light control system** for a four-way intersection (North, South, East, West).  
The simulation dynamically adjusts green light cycles based on real-time traffic intensity, aiming to optimize vehicle throughput and reduce waiting time.

The system supports:
- **Batch mode** – execute a list of commands from a JSON file (`--f input.json output.json`)
- **GUI mode** – JavaFX interface for interactive simulation

---

## Tech Stack
- **Java 21** – core simulation logic
- **JavaFX** – graphical user interface
- **Maven** – build & dependency management
- **JUnit 5** – unit testing
- **GitHub Actions** – CI/CD integration

---

## Simulation Algorithm

The simulation is based on a **Weighted Round Robin (WRR)** algorithm:

1. **Traffic measurement**
    - Each approach lane counts the number of vehicles waiting.
    - These counts are used as *weights*.

2. **Cycle allocation**
    - More vehicles = longer green light duration for that road.
    - Less congested roads get shorter green phases.

3. **Single green light rule**
    - Only one road has a green light at any given moment to avoid collisions.

4. **Vehicle movement**
    - When a road turns green, vehicles move forward and leave the intersection.
    - In batch mode, vehicles are processed in discrete simulation steps.
    - In GUI mode, their movement is visually animated along predefined paths.

---

## Simplifications & Limitations
To keep the focus on traffic distribution logic:
- **No yellow lights** – transitions are immediate from green to red.
- **No turn handling logic** – vehicles are assumed to wait for the turn.
- **No pedestrian crossing phases** – only vehicle flow is considered.
- **No traffic sensor delays** – vehicle counts are updated instantly.

---

## Project Structure
```
src/main/java/app/
  ├── model/         # Core simulation data models
  ├── sched/    # Simulation control logic
  ├── view/          # JavaFX GUI implementation
  └── io/          # Helper classes (parsing, I/O, etc.)
```

---

## Running the Simulation

### 1. GUI Mode (default)
```bash
mvn clean install
mvn exec:java -Dexec.mainClass="app.view.TrafficLightGUI"
```

### 2. Batch Mode
```bash
mvn clean install
java -jar target/traffic-lights.jar --f input.json output.json
```

Example `input.json`:
```json
{
  "commands": [
    { "type": "addVehicle", "vehicleId": "V1", "startRoad": "NORTH" },
    { "type": "step" }
  ]
}
```

---

## Testing
Run unit tests with:
```bash
mvn test
```

---

## CI/CD
The project includes:
- **GitHub Actions** workflow for build & test

---
