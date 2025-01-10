# Route Optimization for Delivery Services

This Java application, `RouteOptimizationApp`, is a GUI-based tool for optimizing delivery routes in the city of Sargodha. It allows users to calculate the shortest or nearest neighbor routes between selected locations using algorithms like **Dijkstra's Algorithm** and the **Nearest Neighbor Algorithm**. The app also provides a visual map for graphical representation of the routes.

---

## Features

1. **Location Selection**:
   - Users can select a starting location from a predefined list.
   - Users can choose one or more delivery destinations.

2. **Algorithms for Route Optimization**:
   - **Dijkstra's Algorithm** for finding the shortest path.
   - **Nearest Neighbor Algorithm** for finding an optimized delivery route.

3. **Graphical Route Representation**:
   - Displays a map with fixed locations.
   - Shows the selected route graphically with a connecting line.

4. **Interactive GUI**:
   - Built using `Swing` for a responsive and user-friendly interface.
   - Input fields for locations, algorithm selection, and output display.

---

## Technologies Used

- **Language**: Java
- **Framework**: Swing for GUI
- **Algorithms**: Dijkstra's Algorithm, Nearest Neighbor Algorithm

---

## Locations

The app operates on a set of predefined locations in Sargodha:
1. Satellite Town
2. University Road
3. Noor Pur Thal
4. Khushab Road
5. Shaheen Chowk

---

## How to Run

1. Clone the repository to your local system.
2. Compile the Java file using a Java compiler:
   ```bash
   javac RouteOptimizationApp.java
