import controlP5.ControlP5;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * GeneticVisual
 *
 * @author: onlylemi
 */

GeneticAlgorithm gm;
Point[] points;
int[] best;
ControlP5 cp5;

boolean running = false;

List<Point> lists;

void settings() {
	size(1000, 600);
}

void setup() {
	lists = new ArrayList<Point>();
	addPoint(50);

	gm = new GeneticAlgorithm();

	cp5 = new ControlP5(this);
	cp5.addButton("onAdd").setPosition(5, 100);
	cp5.addButton("onStart").setPosition(5, 130);
	cp5.addButton("onStop").setPosition(5, 160);
	cp5.addButton("reStart").setPosition(5, 190);
	cp5.addButton("onClear").setPosition(5, 220);
}

void draw() {
	background(220);

	if (best != null) {
		if (running) {
			best = gm.nextGeneration();
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
	text("current generation: " + gm.getCurrentGeneration(), 5, 35);
	text("mutation times: " + gm.getMutationTimes(), 5, 50);
	text("best distance: " + gm.getBestDist(), 5, 65);
}

void onStart() {
	if (lists.size() <= 3) {
		points = null;
		addPoint(3 - lists.size());
	}

	if (points == null || points.length != lists.size()) {
		points = new Point[lists.size()];
		for (int i = 0; i < lists.size(); i++) {
			points[i] = lists.get(i);
		}
		best = gm.tsp(getDist(points));
	}

	running = true;
}

void onAdd() {
	if (!running) {
		addPoint(10);
		best = null;

		points = null;
	}
}

void onStop() {
	running = false;
}

void reStart() {
	if (points != null) {
		best = gm.tsp(getDist(points));
		running = true;
	}
}

void onClear() {
	lists.clear();

	best = null;
	running = false;
	points = null;
}


void mousePressed() {
	if (!running && mouseX > 150) {
		addMousePoint();
		best = null;
		points = null;
	}
}

void addPoint(int num) {
	Point point = null;
	for (int i = 0; i < num; i++) {
		point = new Point();
		point.x = random(150, width - 8);
		point.y = random(8, height - 8);

		lists.add(point);
	}
}

void addMousePoint() {
	Point point = new Point();
	point.x = mouseX;
	point.y = mouseY;

	lists.add(point);
}

float[][] getDist(Point[] points) {
	float[][] dist = new float[points.length][points.length];
	for (int i = 0; i < points.length; i++) {
		for (int j = 0; j < points.length; j++) {
			dist[i][j] = distance(points[i], points[j]);
		}
	}
	return dist;
}

float distance(Point p1, Point p2) {
	return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
}