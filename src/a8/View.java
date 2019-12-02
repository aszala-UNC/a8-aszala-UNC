package a8;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class View extends JPanel implements ActionListener, ItemListener, ChangeListener {
	
	private int width = 10, height = width;
	
	private static final int MAX_WIDTH = 1000, MAX_HEIGHT = 1000;
	
	private boolean[][] cells;
	
	private CellViewListener listener;
	
	private JPanel cellPanel, controls;
	private JFrame rename;
	private JSpinner surviveThreshold, birthThreshold, widthSpinner, heightSpinner;
	private JCheckBox torus;
	private JLabel speedText;
	private JButton start, stop;
	
	public View() {
		try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); 
        } catch (Exception e) { 
            System.out.println("Look and Feel not set"); 
        } 
		
		rename = new JFrame("Loading");
		rename.setLocation(150, 250);
		
		JPanel p = new JPanel();
		p.setBackground(Color.black);
		p.setPreferredSize(new Dimension(100, 100));
		p.setLayout(new BorderLayout());
		
		JLabel loadingText = new JLabel("Loading");
		loadingText.setForeground(Color.white);
		
		p.add(loadingText, BorderLayout.CENTER);
		
		rename.setContentPane(p);
		rename.setAlwaysOnTop(true);
		rename.setUndecorated(true);
		rename.pack();

		rename.revalidate();
		rename.repaint();
		
		JLabel widthText = new JLabel("Grid Width (10-500):");
		JLabel heightText = new JLabel("Grid Height (10-500):");
		
		cellPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D)g;

				Dimension size = new Dimension(MAX_WIDTH / width, MAX_HEIGHT / height);
				
				int x = 0, y = 0;
				
		        for (int i=0;i<width;i++) {
					for (int j=0;j<height;j++) {
						if (cells[i][j])
							g2d.setColor(Color.black);
						else
							g2d.setColor(Color.white);
						
						g2d.fillRect(x, y, (int)size.getWidth(), (int)size.getHeight());
						g2d.setColor(Color.gray);
						g2d.drawRect(x, y, (int)size.getWidth(), (int)size.getHeight());
						
						x += (int)size.getWidth();
					}
					
					y += (int)size.getHeight();
					x = 0;
		        }
			}
		};
		cellPanel.setBackground(Color.black);
		cellPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int mx = e.getX(), my = e.getY();
				int i = mx / (MAX_WIDTH / width), j = my / (MAX_HEIGHT / height);
				
				cells[j][i] = !cells[j][i];
				
				cellPanel.repaint();
			}
		});
		
		controls = new JPanel();
		controls.setPreferredSize(new Dimension(200, MAX_HEIGHT));
		
		start = new JButton("Start");
		start.setName("start");
		setButtonValues(start);
		
		stop = new JButton("Stop");
		stop.setName("stop");
		setButtonValues(stop);
		
		changeGridSize(width, height);
		
		this.add(cellPanel);
		
		SpinnerModel widthModel = new SpinnerNumberModel(width, 10, 500, 1);
		SpinnerModel heightModel = new SpinnerNumberModel(height, 10, 500, 1);
		
		widthSpinner = new JSpinner(widthModel);
		heightSpinner = new JSpinner(heightModel);
		
		Dimension textBoxDimen = new Dimension(50, 30);
		
		SpinnerModel survive = new SpinnerNumberModel(3, 1, 8, 1);
		SpinnerModel birth = new SpinnerNumberModel(2, 1, 8, 1);
		
		surviveThreshold = new JSpinner(survive);
		surviveThreshold.setPreferredSize(textBoxDimen);
		birthThreshold = new JSpinner(birth);
		birthThreshold.setPreferredSize(textBoxDimen);
		
		torus = new JCheckBox("Torus Mode");
		torus.addItemListener(this);
		
		JButton refresh = new JButton("Update and Apply Changes");
		refresh.setName("gridRefresh");
		setButtonValues(refresh);
		
		JButton random = new JButton("Random Fill");
		random.setName("randFill");
		setButtonValues(random);
		
		JButton clear = new JButton("Clear Board");
		clear.setName("clear");
		setButtonValues(clear);
		
		JButton advance = new JButton("Advance To Generation");
		advance.setName("advance");
		setButtonValues(advance);
		
		JSlider speed = new JSlider(10, 1000, 10);
		speed.addChangeListener(this);
		
		speedText = new JLabel(speed.getValue() + " Millis");
		
		controls.add(widthText);
		controls.add(widthSpinner);
		controls.add(heightText);
		controls.add(heightSpinner);
		controls.add(new JLabel("Survive Threshold (1-8):"));
		controls.add(surviveThreshold);
		controls.add(new JLabel("Birth Threshold (1-8):"));
		controls.add(birthThreshold);
		controls.add(refresh);
		controls.add(random);
		controls.add(clear);
		controls.add(torus);
		controls.add(advance);
		controls.add(start);
		controls.add(stop);
		controls.add(new JLabel("Speed in millis (10-1000):"));
		controls.add(speed);
		controls.add(speedText);
		
		this.add(controls);
	}
	
	private void setButtonValues(JButton button) {
		button.addActionListener(this);
		button.setFocusPainted(false);
		//button.setBorderPainted(false);
	}
	
	public synchronized void changeGridSize(int width, int height) {
		if (width < 10 || height < 10 || width > 500 || height > 500) {
			throw new IllegalArgumentException("Illegal board geometry");
		}
		
		this.width = width;
		this.height = height;
		
		cells = new boolean[width][height];
		
		populateGrid();
	}
	
	private synchronized void populateGrid() {
		/*cellPanel.removeAll();
		
		cellPanel.setLayout(new GridLayout(height, width));
		
		cellPanel.setBackground(Color.black);
		*/
		cellPanel.setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		cellPanel.repaint();
		repaint();
		Main.main_frame.revalidate();
		
		//final ActionListener a = this;
		
		rename.setVisible(true);
		
		for (Component c : controls.getComponents())
			c.setEnabled(false);
		
		Thread loadingThread = new Thread(new Runnable() {
			@Override
			public void run() {
		        for (int i=0;i<width;i++) {
					for (int j=0;j<height;j++) {
						/*Cell c = new Cell();
						c.addActionListener(a);
						
						c.setPreferredSize(size);
						
						c.setFocusable(false);
					    c.setFocusPainted(false);
					    c.setBorderPainted(false);
					    
					    cellPanel.add(c);
					    */
					    cells[i][j] = false;
					}
				}
		        
				rename.setVisible(false);
		        
				cellPanel.revalidate();
				
				for (Component c : controls.getComponents())
					c.setEnabled(true);
				
				start.setEnabled(true);
				stop.setEnabled(false);
			}
		});
		loadingThread.start();
	}

	public void toggleCell(int x, int y) {
		cells[x][y] = !cells[x][y];
		cellPanel.repaint();
	}
	
/*	
	public void update() {
		for (int i=0;i<width;i++) {
			for (int j=0;j<height;j++) {
				cells[i][j].repaint();
			}
		}
		
		repaint();
	}
	*/
	
	public void updateSpeedDisplay(int speed) {
		if (speed == 1000)
			speedText.setText("1 Second");
		else
			speedText.setText(speed + " Millis");
	}
	
	public int getGridWidth() {
		return width;
	}
	
	public int getGridHeight() {
		return height;
	}
	
	public boolean[][] getCells() {
		return cells;
	}
	
	public boolean getCell(int x, int y) {
		return cells[x][y];
	}

	public void setListener(CellViewListener listener) {
		this.listener = listener;
	}
	
	public void clear() {
		for (int i=0;i<width;i++) {
			for (int j=0;j<height;j++) {
				cells[i][j] = false;
				// Uncomment if you use animations cells[i][j].disposeAnimation();
			}
		}

		cellPanel.repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Thread handler = new Thread(new Runnable() {
			@Override
			public void run() {
				Object src = e.getSource();
				
				if (src instanceof JComponent) {
					JComponent jc = (JComponent)src;
					
					if (jc.getName().equals("gridRefresh")) {
						listener.handleEvent(new GridEvent(GridEvent.Intent.INTENT_REFRESH, (Integer)birthThreshold.getValue(), (Integer)surviveThreshold.getValue(),
								(Integer)widthSpinner.getValue(), (Integer)heightSpinner.getValue()));
					} else {
						GridEvent.Intent intent = null;
						
						if (jc.getName().equals("randFill"))
							intent = GridEvent.Intent.INTENT_RANDOM;
						else if (jc.getName().equals("clear"))
							intent = GridEvent.Intent.INTENT_CLEAR;
						else if (jc.getName().equals("advance"))
							intent = GridEvent.Intent.INTENT_ADVANCE;
						else if (jc.getName().equals("start")) {
							intent = GridEvent.Intent.INTENT_START_AUTO;
							start.setEnabled(false);
							stop.setEnabled(true);
						} else if (jc.getName().equals("stop")) {
							intent = GridEvent.Intent.INTENT_STOP_AUTO;
							start.setEnabled(true);
							stop.setEnabled(false);
						}
						
						listener.handleEvent(new GridEvent(intent));
					}
				}
			}
		});
		handler.start();
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		listener.handleEvent(new CheckBoxEvent(e.getStateChange() == 1 ? true : false));
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		listener.handleEvent(new SliderEvent(((JSlider)(e.getSource())).getValue(), SliderEvent.Intent.INTENT_SPEED));
	}
	
}
