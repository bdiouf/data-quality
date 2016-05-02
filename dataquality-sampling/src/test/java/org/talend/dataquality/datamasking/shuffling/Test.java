package org.talend.dataquality.datamasking.shuffling;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Test {

    private static void createAndShowGui() {
        List<Long> scores = new ArrayList<Long>();
        Random random = new Random();
        int maxDataPoints = 40;
        int maxScore = 10;
        for (int i = 0; i < maxDataPoints; i++) {
            scores.add(random.nextLong() * maxScore);
            // scores.add((double) i);
        }
        GraphPanel mainPanel = new GraphPanel(scores);
        mainPanel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame("DrawGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            PrintWriter writer = new PrintWriter("/home/qzhao/talend_data_anony/TDQ-11904-shuffling/execution_time_notes-1.txt",
                    "UTF-8");
            writer.println("Rows' number | column's group | execution time");

            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGui();
            }
        });
    }

}
