import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Map;

public class RouteOptimizationApp {

    // Map location names to indices
    private static final Map<String, Integer> locationIndex = new HashMap<>();
    private static final int[][] distances = {
            // Satellite University NoorPur Khushab Shaheen
            /* Satellite */ { 0, 5, 12, 15, 8 },
            /* University */ { 5, 0, 8, 10, 6 },
            /* NoorPur */ { 12, 8, 0, 7, 10 },
            /* Khushab */ { 15, 10, 7, 0, 5 },
            /* Shaheen */ { 8, 6, 10, 5, 0 }
    };

    static {
        locationIndex.put("Satellite Town", 0);
        locationIndex.put("University Road", 1);
        locationIndex.put("Noor Pur Thal", 2);
        locationIndex.put("Khushab Road", 3);
        locationIndex.put("Shaheen Chowk", 4);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Route Optimization");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600); // Fixed size
            frame.setLayout(new BorderLayout());

            // Top Panel: Location Selection
            JPanel topPanel = new JPanel(new GridLayout(6, 1, 10, 10));
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel startLabel = new JLabel("Starting Location:");
            JComboBox<String> startField = new JComboBox<>(locationIndex.keySet().toArray(new String[0]));

            JLabel destinationsLabel = new JLabel("Select Delivery Destinations:");
            JComboBox<String> destinationsField = new JComboBox<>(locationIndex.keySet().toArray(new String[0]));
            destinationsField.setEditable(true); // Allow user to type in custom destinations

            topPanel.add(startLabel);
            topPanel.add(startField);
            topPanel.add(destinationsLabel);
            topPanel.add(destinationsField);

            frame.add(topPanel, BorderLayout.NORTH);

            // Center Panel: Algorithm Selection
            JPanel centerPanel = new JPanel();
            centerPanel.setBorder(BorderFactory.createTitledBorder("Select Algorithm"));

            JComboBox<String> algorithmDropdown = new JComboBox<>(new String[] {
                    "Dijkstra's Algorithm",
                    "Nearest Neighbor Algorithm"
            });
            centerPanel.add(algorithmDropdown);
            frame.add(centerPanel, BorderLayout.CENTER);

            // Bottom Panel: Calculate Button and Output
            JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JButton calculateButton = new JButton("Calculate Route");
            JTextArea outputArea = new JTextArea();
            outputArea.setEditable(false);
            outputArea.setLineWrap(true);
            outputArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(outputArea);

            bottomPanel.add(calculateButton, BorderLayout.NORTH);
            bottomPanel.add(scrollPane, BorderLayout.CENTER);

            frame.add(bottomPanel, BorderLayout.SOUTH);

            // Add map panel for graphical route representation
            MapPanel mapPanel = new MapPanel();
            frame.add(mapPanel, BorderLayout.CENTER);

            // Button Action
            calculateButton.addActionListener(e -> {
                String startLocation = (String) startField.getSelectedItem();
                String destination = (String) destinationsField.getSelectedItem(); // Get selected destination
                String algorithm = (String) algorithmDropdown.getSelectedItem();

                if (destination.isEmpty()) {
                    outputArea.setText("Please select a valid delivery destination.");
                    return;
                }

                String result;
                if ("Dijkstra's Algorithm".equals(algorithm)) {
                    result = dijkstraAlgorithm(startLocation, Arrays.asList(destination));
                } else {
                    result = nearestNeighborAlgorithm(startLocation, Arrays.asList(destination));
                }
                outputArea.setText(result);
                // Update the map with the route
                mapPanel.updateRoute(startLocation, destination);
            });

            frame.setVisible(true);
        });
    }

    // Dijkstra's Algorithm
    private static String dijkstraAlgorithm(String start, List<String> destinations) {
        int n = distances.length;
        int[] minDist = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(minDist, Integer.MAX_VALUE);

        int startIndex = locationIndex.get(start);
        minDist[startIndex] = 0;

        for (int i = 0; i < n - 1; i++) {
            int u = -1;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && (u == -1 || minDist[j] < minDist[u])) {
                    u = j;
                }
            }

            visited[u] = true;
            for (int v = 0; v < n; v++) {
                if (!visited[v] && distances[u][v] != 0 && minDist[u] + distances[u][v] < minDist[v]) {
                    minDist[v] = minDist[u] + distances[u][v];
                }
            }
        }

        StringBuilder result = new StringBuilder("Dijkstra's Algorithm Result:\n");
        for (String destination : destinations) {
            int destIndex = locationIndex.get(destination);
            result.append(start).append(" -> ").append(destination)
                    .append(": ").append(minDist[destIndex]).append(" km\n");
        }

        return result.toString();
    }

    // Nearest Neighbor Algorithm
    private static String nearestNeighborAlgorithm(String start, List<String> destinations) {
        int startIndex = locationIndex.get(start);
        boolean[] visited = new boolean[distances.length];
        visited[startIndex] = true;

        StringBuilder route = new StringBuilder("Nearest Neighbor Route:\n" + start);
        int current = startIndex;
        int totalDistance = 0;

        while (!destinations.isEmpty()) {
            int nearest = -1;
            int nearestDist = Integer.MAX_VALUE;

            for (String destination : destinations) {
                int destIndex = locationIndex.get(destination);
                if (!visited[destIndex] && distances[current][destIndex] < nearestDist) {
                    nearest = destIndex;
                    nearestDist = distances[current][destIndex];
                }
            }

            if (nearest == -1)
                break;

            visited[nearest] = true;
            totalDistance += nearestDist;
            route.append(" -> ").append(getLocationName(nearest));
            current = nearest;

            destinations.remove(getLocationName(nearest));
        }

        route.append("\nTotal Distance: ").append(totalDistance).append(" km");
        return route.toString();
    }

    private static String getLocationName(int index) {
        for (Map.Entry<String, Integer> entry : locationIndex.entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return null;
    }

    // MapPanel for graphical representation of routes
    static class MapPanel extends JPanel {
        private String startLocation = "";
        private String destination = "";

        // Fixed positions for locations (x, y coordinates)
        private final Map<String, Point> locationPoints = new HashMap<>() {
            {
                put("Satellite Town", new Point(50, 100));
                put("University Road", new Point(150, 200));
                put("Noor Pur Thal", new Point(300, 100));
                put("Khushab Road", new Point(400, 250));
                put("Shaheen Chowk", new Point(550, 150));
            }
        };

        public MapPanel() {
            this.setPreferredSize(new Dimension(800, 600)); // Fixed panel size
        }

        // Method to update the route when calculating the route
        public void updateRoute(String start, String destination) {
            this.startLocation = start;
            this.destination = destination;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw fixed locations on the map
            for (Map.Entry<String, Point> entry : locationPoints.entrySet()) {
                String location = entry.getKey();
                Point point = entry.getValue();
                g.fillOval(point.x, point.y, 10, 10);
                g.drawString(location, point.x + 15, point.y);
            }

            // Draw the route between the selected locations
            if (!startLocation.isEmpty() && !destination.isEmpty()) {
                Point startPoint = locationPoints.get(startLocation);
                Point endPoint = locationPoints.get(destination);

                g.setColor(Color.BLUE);
                g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
                g.drawString("Route: " + startLocation + " -> " + destination,
                        (startPoint.x + endPoint.x) / 2, (startPoint.y + endPoint.y) / 2);
            }
        }
    }
}
