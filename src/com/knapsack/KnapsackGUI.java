package com.knapsack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KnapsackGUI extends JFrame {
    private JTextField capacityField, weightField, valueField;
    private JTextArea resultArea, flowArea;
    private JPanel visualPanel;
    private JButton nextStepButton, solveButton;
    private int[][] K;
    private int[] wt, val;
    private int W, n;
    private int currentI = 0, currentW = 0;
    private boolean problemInitialized = false;

    public KnapsackGUI() {
        setTitle("Knapsack Problem Solver");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Capacidad de la mochila:"));
        capacityField = new JTextField();
        inputPanel.add(capacityField);
        inputPanel.add(new JLabel("Pesos (separados por coma):"));
        weightField = new JTextField();
        inputPanel.add(weightField);
        inputPanel.add(new JLabel("Valores (separados por coma):"));
        valueField = new JTextField();
        inputPanel.add(valueField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        solveButton = new JButton("Resolver");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeKnapsack();
            }
        });
        buttonPanel.add(solveButton);

        nextStepButton = new JButton("Siguiente Paso");
        nextStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextStep();
            }
        });
        nextStepButton.setEnabled(false);
        buttonPanel.add(nextStepButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        visualPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (problemInitialized) {
                    drawKnapsackTable(g);
                }
            }
        };
        visualPanel.setPreferredSize(new Dimension(800, 400));

        flowArea = new JTextArea();
        flowArea.setEditable(false);
        flowArea.setLineWrap(true);
        flowArea.setWrapStyleWord(true);
        JScrollPane flowScrollPane = new JScrollPane(flowArea);
        flowScrollPane.setPreferredSize(new Dimension(800, 200));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(visualPanel, BorderLayout.CENTER);
        centerPanel.add(flowScrollPane, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void initializeKnapsack() {
        try {
            W = Integer.parseInt(capacityField.getText());
            wt = parseIntArray(weightField.getText());
            val = parseIntArray(valueField.getText());
            n = wt.length;

            if (wt.length != val.length) {
                throw new IllegalArgumentException("El número de pesos y valores debe ser igual");
            }

            K = new int[n + 1][W + 1];
            currentI = 0;
            currentW = 0;
            problemInitialized = true;
            nextStepButton.setEnabled(true);
            resultArea.setText("Inicialización completada. Presiona 'Siguiente Paso' para continuar.");
            flowArea.setText("Inicialización del problema:\n" +
                             "Capacidad de la mochila: " + W + "\n" +
                             "Pesos: " + arrayToString(wt) + "\n" +
                             "Valores: " + arrayToString(val) + "\n\n");
            repaint();
        } catch (NumberFormatException e) {
            resultArea.setText("Error: Asegúrate de ingresar números válidos");
        } catch (IllegalArgumentException e) {
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    private void nextStep() {
        if (currentI <= n) {
            if (currentW <= W) {
                String stepDescription = "Fila " + currentI + ", Columna " + currentW + ":\n";
                if (currentI == 0 || currentW == 0) {
                    K[currentI][currentW] = 0;
                    stepDescription += "Caso base: K[" + currentI + "][" + currentW + "] = 0\n";
                } else if (wt[currentI - 1] <= currentW) {
                    int include = val[currentI - 1] + K[currentI - 1][currentW - wt[currentI - 1]];
                    int exclude = K[currentI - 1][currentW];
                    K[currentI][currentW] = Math.max(include, exclude);
                    stepDescription += "Decidiendo sobre el item " + currentI + " (peso: " + wt[currentI - 1] + ", valor: " + val[currentI - 1] + "):\n" +
                                       "  Incluir item " + currentI + ": " + include + " (valor del item + mejor valor para el peso restante)\n" +
                                       "  No incluir item " + currentI + ": " + exclude + " (mejor valor sin este item)\n" +
                                       "K[" + currentI + "][" + currentW + "] = max(" + include + ", " + exclude + ") = " + K[currentI][currentW] + "\n";
                } else {
                    K[currentI][currentW] = K[currentI - 1][currentW];
                    stepDescription += "Item " + currentI + " (peso: " + wt[currentI - 1] + ") no cabe en la capacidad actual " + currentW + ".\n" +
                                       "K[" + currentI + "][" + currentW + "] = K[" + (currentI - 1) + "][" + currentW + "] = " + K[currentI][currentW] + "\n";
                }
                flowArea.append(stepDescription + "\n");
                currentW++;
            } else {
                currentI++;
                currentW = 0;
            }
            resultArea.setText(String.format("Procesando fila %d, columna %d", currentI, currentW));
        } else {
            nextStepButton.setEnabled(false);
            resultArea.setText("Solución completa. El valor máximo es: " + K[n][W]);
            flowArea.append("Solución encontrada: El valor máximo es " + K[n][W] + "\n");
            reconstructSolution();
        }
        repaint();
    }

    private void reconstructSolution() {
        StringBuilder solution = new StringBuilder("Items seleccionados:\n");
        int i = n, w = W;
        while (i > 0 && w > 0) {
            if (K[i][w] != K[i-1][w]) {
                solution.append("Item ").append(i).append(" (peso: ").append(wt[i-1]).append(", valor: ").append(val[i-1]).append(")\n");
                w -= wt[i-1];
            }
            i--;
        }
        flowArea.append(solution.toString());
    }

    private void drawKnapsackTable(Graphics g) {
        int cellWidth = 50;
        int cellHeight = 30;
        int startX = 50;
        int startY = 50;

        // Dibujar etiquetas de columnas (pesos)
        for (int w = 0; w <= W; w++) {
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(w), startX + w * cellWidth + 20, startY - 10);
        }

        // Dibujar etiquetas de filas (items)
        for (int i = 0; i <= n; i++) {
            g.setColor(Color.BLACK);
            if (i == 0) {
                g.drawString("0", startX - 30, startY + i * cellHeight + 20);
            } else {
                g.drawString("Item " + i, startX - 45, startY + i * cellHeight + 20);
            }
        }

        // Dibujar la tabla
        for (int i = 0; i <= n; i++) {
            for (int w = 0; w <= W; w++) {
                int x = startX + w * cellWidth;
                int y = startY + i * cellHeight;
                g.setColor(getCellColor(i, w));
                g.fillRect(x, y, cellWidth, cellHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellWidth, cellHeight);
                g.drawString(String.valueOf(K[i][w]), x + 20, y + 20);
            }
        }
    }

    private Color getCellColor(int i, int w) {
        if (i == currentI && w == currentW) {
            return Color.YELLOW;
        } else if (i < currentI || (i == currentI && w < currentW)) {
            return Color.LIGHT_GRAY;
        } else {
            return Color.WHITE;
        }
    }

    private int[] parseIntArray(String input) {
        String[] parts = input.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }

    private String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KnapsackGUI().setVisible(true);
            }
        });
    }
}