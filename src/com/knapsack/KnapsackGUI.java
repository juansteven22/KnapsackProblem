package com.knapsack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KnapsackGUI extends JFrame {
    private JTextField capacityField, weightField, valueField;
    private JTextArea resultArea;

    public KnapsackGUI() {
        setTitle("Knapsack Problem Solver");
        setSize(400, 300);
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

        JButton solveButton = new JButton("Resolver");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveKnapsack();
            }
        });

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        add(inputPanel, BorderLayout.NORTH);
        add(solveButton, BorderLayout.CENTER);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    private void solveKnapsack() {
        try {
            int capacity = Integer.parseInt(capacityField.getText());
            int[] weights = parseIntArray(weightField.getText());
            int[] values = parseIntArray(valueField.getText());

            if (weights.length != values.length) {
                throw new IllegalArgumentException("El número de pesos y valores debe ser igual");
            }

            int result = KnapsackSolver.knapsack(capacity, weights, values, weights.length);
            resultArea.setText("El valor máximo que se puede obtener es: " + result);
        } catch (NumberFormatException e) {
            resultArea.setText("Error: Asegúrate de ingresar números válidos");
        } catch (IllegalArgumentException e) {
            resultArea.setText("Error: " + e.getMessage());
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KnapsackGUI().setVisible(true);
            }
        });
    }
}