package a8;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class Cell extends JButton /*implements ActionListener*/ {

	private boolean alive;
	
	// UNCOMMENT EVERYTHING FOR ANIMATIONS
	// But it is laggy, especially at higher grid sizes
	
	/*
	private int rgb = 255;
	private boolean rgbIncrease = true;
	private Timer timer;
	*/
	public Cell() {
		/*
		timer = new Timer(1, this);
		timer.setCoalesce(false);
		timer.start();
		*/
		setAlive(false);
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
		
		//timer.restart();
		
		if (alive)
			setBackground(Color.black);
			//rgbIncrease = false;
		else
			setBackground(Color.white);
			//rgbIncrease = true;
		
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
		
		g.setColor(Color.gray);
		g.drawRect(0, 0, getPreferredSize().width, getPreferredSize().height);
	}
	
	public boolean isAlive() {
		return alive;
	}
	/*
	public void disposeAnimation() {
		timer.stop();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (rgbIncrease)
			rgb += 5;
		else
			rgb -= 5;
		
		if (rgb > 255) {
			rgb = 255;
			timer.stop();
		} else if (rgb < 0) {
			rgb = 0;
			timer.stop();
		}
		
		setBackground(new Color(rgb, rgb, rgb));
		
		repaint();
	}
	*/
}