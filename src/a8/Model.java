package a8;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class Model {

	private CellObserver observer;
	private int count = 0;
	
	private int birth = 2, survive = 3;
	
	private boolean torus = false;
	
	private Map<Point, Boolean> cellMap;
	
	public Model() {
		cellMap = new HashMap<Point, Boolean>();
	}
	
	public void evaluateCell(boolean[][] cells, int x, int y, int total) {
		int px = x+1, sx = x-1;
		int py = y+1, sy = y-1;
		
		if (torus) {
			if (px >= cells.length)
				px = 0;
			if (sx < 0)
				sx = cells.length-1;

			if (x < cells.length && py >= cells[x].length)
				py = 0;
			if (sy < 0)
				sy = cells[x].length-1;
		}
		
		int alive = 0;
		
		if (px < cells.length && cells[px][y])
			alive++;
		if (sx >= 0 && cells[sx][y])
			alive++;
		if (py < cells[x].length && cells[x][py])
			alive++;
		if (sy >= 0 && cells[x][sy])
			alive++;
		if (px < cells.length && py < cells[x].length && cells[px][py])
			alive++;
		if (sx >= 0 && sy >= 0 && cells[sx][sy])
			alive++;
		if (px < cells.length && sy >= 0 && cells[px][sy])
			alive++;
		if (sx >= 0 && py < cells[x].length && cells[sx][py])
			alive++;
		
		boolean shouldLive = false;
		
		if (alive < birth)
			shouldLive = false;
		else if (alive == birth && !cells[x][y])
			shouldLive = false;
		else if (alive == birth || alive == survive)
			shouldLive = true;
		else if (alive > survive)
			shouldLive = false;
		
		/* Uncomment to get fancy patterns
		
		if (alive < 2)
			shouldLive = false;
		else if (alive == 2 || alive == 3)
			shouldLive = true;
		else if (alive > 3)
			shouldLive = false;
		*/
		
		boolean toggle = !(shouldLive == cells[x][y]);
		
		count++;
		
		cellMap.put(new Point(x, y), toggle);
		
		if (count == total)
			notifyObserver();
	}
	
	public void toggleTorus(boolean value) {
		torus = value;
	}
	
	public void setSurviveAndBirthValues(int birth, int survive) {
		this.birth = birth;
		this.survive = survive;
	}
	
	public synchronized void notifyObserver() {
		observer.update(this);
		count = 0;
		cellMap.clear();
	}
	
	public void setObserver(CellObserver obeserver) {
		this.observer = obeserver;
	}
	
	public Map<Point, Boolean> getMap() {
		return cellMap;
	}
	
}
