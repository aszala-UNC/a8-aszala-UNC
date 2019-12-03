package a8;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class View extends JPanel implements ActionListener, ItemListener, ChangeListener {
	
	private int width = 10, height = width, cellWidth = 0, cellHeight = 0;
	
	private static final int MAX_WIDTH = 1000, MAX_HEIGHT = 1000;
	
	private boolean[][] cells;
	
	private CellViewListener listener;
	
	private JPanel cellPanel, controls;
	private JSpinner surviveThreshold, birthThreshold, widthSpinner, heightSpinner;
	private JCheckBox torus;
	private JLabel speedText;
	private JButton start, stop;
	
	private Dimension size = new Dimension(0, 0);
	
	private Set<Point> pointsAlive = new HashSet<>();
	private Point highlight = new Point(-1000, -1000);
	
	public View() {
		/* Uncomment for Better UI
		try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); 
        } catch (Exception e) { 
            System.out.println("Look and Feel not set");
        }
        */
		
		JLabel widthText = new JLabel("Grid Width (10-500):");
		JLabel heightText = new JLabel("Grid Height (10-500):");
		
		cellPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D)g;
				
				g2d.setColor(Color.gray);
				g2d.fillRect(0, 0, MAX_WIDTH, MAX_HEIGHT);
				
				g2d.setColor(Color.white);
				g2d.fillRect(0, 0, cellWidth, cellHeight);

				/*
				g2d.setColor(Color.gray);
				
				int x = 0, y = 0;
				
		        for (int i=0;i<cellWidth;i++) {
		        	g2d.drawLine(x, 0, x, cellHeight);
		        	
		        	x += size.width;
		        }
				
				for (int j=0;j<cellHeight;j++) {
		        	g2d.drawLine(0, y, cellWidth, y);
		        	
		        	y += size.height;
				}
		        */
				
				g2d.setColor(Color.black);
				g2d.drawRect(highlight.x, highlight.y, size.width, size.height);
				
				Iterator<Point> it = pointsAlive.iterator();
				
				while (it.hasNext()) {
					Point p;
					
					try {
						p = it.next();
					} catch (ConcurrentModificationException e) {
						break;
					}
					
					g2d.fillRect(p.x, p.y, size.width, size.height);
				}
			}
		};
		cellPanel.setBackground(Color.black);
		cellPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int mx = e.getX(), my = e.getY();
				int i = mx / size.width, j = my / size.height;
				
				cells[j][i] = !cells[j][i];
				
				if (cells[j][i])
					pointsAlive.add(new Point(i * size.width, j * size.height));
				else
					pointsAlive.remove(new Point(i * size.width, j * size.height));
					
				cellPanel.repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				highlight = new Point(-1000, -100);
				
				cellPanel.repaint();
			}
		});
		cellPanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int mx = e.getX(), my = e.getY();
				int i = mx / size.width, j = my / size.height;
				
				highlight = new Point(i * size.width, j * size.height);
				
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
		cellPanel.setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		cellPanel.repaint();
		
		size = new Dimension(MAX_WIDTH / width, MAX_HEIGHT / height);
		
		cellWidth = cells.length * size.width;
		cellHeight = cells[0].length * size.height;
		
		clear();
        
		start.setEnabled(true);
		stop.setEnabled(false);
	}

	public void toggleCell(int x, int y) {
		cells[x][y] = !cells[x][y];
		
		if (cells[x][y])
			pointsAlive.add(new Point(y * size.width, x * size.height));
		else
			pointsAlive.remove(new Point(y * size.width, x * size.height));
	}
	
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
	
	public void setListener(CellViewListener listener) {
		this.listener = listener;
	}
	
	public void update() {
		cellPanel.repaint();
	}
	
	public void clear() {
		for (int j=0;j<height;j++) {
			for (int i=0;i<width;i++) {
				cells[i][j] = false;
			}
		}

		pointsAlive.clear();
		
		cellPanel.repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
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
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		listener.handleEvent(new CheckBoxEvent(e.getStateChange() == 1 ? true : false));
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		listener.handleEvent(new SliderEvent(((JSlider)(e.getSource())).getValue(), SliderEvent.Intent.INTENT_SPEED));
	}
	
}
