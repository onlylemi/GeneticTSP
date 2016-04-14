package com.onlylemi.genetictsp;

import controlP5.ControlP5;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * GeneticVisual
 *
 * @author: onlylemi
 */
public class GeneticP5Visual extends PApplet {

    private GeneticAlgorithm ga;
    private Point[] points;
    private int[] best;
    private ControlP5 cp5;

    private boolean running = false;

    private List<Point> lists;

    @Override
    public void settings() {
        size(1000, 600);
    }

    @Override
    public void setup() {
        lists = new ArrayList<>();
        addPoint(50);

        ga = GeneticAlgorithm.getInstance();

        cp5 = new ControlP5(this);
        cp5.addButton("onAdd").setPosition(5, 100);
        cp5.addButton("onStart").setPosition(5, 130);
        cp5.addButton("onStop").setPosition(5, 160);
        cp5.addButton("reStart").setPosition(5, 190);
        cp5.addButton("onClear").setPosition(5, 220);
    }

    @Override
    public void draw() {
        background(220);

        if (best != null) {
            if (running) {
                best = ga.nextGeneration();
            }

            stroke(255, 0, 0);
            strokeWeight(2);
            for (int i = 1; i < best.length; i++) {
                line(points[best[i]].x, points[best[i]].y, points[best[i - 1]].x, points[best[i - 1]].y);
            }
            line(points[best[0]].x, points[best[0]].y, points[best[best.length - 1]].x, points[best[best.length - 1]]
                    .y);
        }

        fill(0);
        noStroke();
        for (int i = 0; i < lists.size(); i++) {
            ellipse(lists.get(i).x, lists.get(i).y, 8, 8);
        }

        stroke(0);
        fill(0);
        textSize(12);
        text("point length: " + lists.size(), 5, 20);
        text("current generation: " + ga.getCurrentGeneration(), 5, 35);
        text("mutation times: " + ga.getMutationTimes(), 5, 50);
        text("best distance: " + ga.getBestDist(), 5, 65);
    }

    public void onStart() {
        if (lists.size() <= 3) {
            points = null;
            addPoint(3 - lists.size());
        }

        if (points == null || points.length != lists.size()) {
            points = new Point[lists.size()];
            for (int i = 0; i < lists.size(); i++) {
                points[i] = lists.get(i);
            }
            best = ga.tsp(points);
        }

        running = true;
    }

    public void onAdd() {
        if (!running) {
            addPoint(10);
            best = null;

            points = null;
        }
    }

    public void onStop() {
        running = false;
    }

    public void reStart() {
        if (points != null) {
            best = ga.tsp(points);
            running = true;
        }
    }

    public void onClear() {
        lists.clear();

        best = null;
        running = false;
        points = null;
    }

    @Override
    public void mousePressed() {
        if (!running && mouseX > 150) {
            addMousePoint();
            best = null;
            points = null;
        }
    }

    private void addPoint(int num) {
        Point point = null;
        for (int i = 0; i < num; i++) {
            point = new Point();
            point.x = random(150, width - 8);
            point.y = random(8, height - 8);

            lists.add(point);
        }
    }

    private void addMousePoint() {
        Point point = new Point();
        point.x = mouseX;
        point.y = mouseY;

        lists.add(point);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{new GeneticP5Visual().getClass().getName()});
    }
}
