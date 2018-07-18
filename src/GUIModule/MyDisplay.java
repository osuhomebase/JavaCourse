package GUIModule;
import processing.core.PApplet;

public class MyDisplay extends PApplet{
	public void setup() {
		size(600,600);
		background(66, 134, 244);
	}
	
	public void draw() {
		fill(244, 241, 66);
		ellipse(300,300,400,400);
		fill(0,0,0);
		ellipse(250,250,25,25);
		ellipse(350,250,25,25);
		noFill();
		strokeWeight(4);
		arc(300, 350, 120, 80, 0, PI);
		
	}
}
