package a8;

import javax.swing.JFrame;

public class Main {

	public static JFrame main_frame;
	
	public static void main(String[] args) {
		main_frame = new JFrame();
		
		View view = new View();
		Model model = new Model();
		Controller controller = new Controller(model, view);
		
		main_frame.setTitle("Game of Life");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		main_frame.setContentPane(view);
		
		main_frame.pack();
		main_frame.setVisible(true);
	}
	
}