package a8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {

	private CellObserver observer;
	private int count = 0;
	
	private int birth = 2, survive = 3;
	
	private boolean torus = false;
	
	private Map<Cell, Boolean> cellMap;
	
	public Model() {
		cellMap = new HashMap<Cell, Boolean>();
	}
	
	public synchronized void evaluateCell(Cell[][] cells, int x, int y, int total) {
		List<Cell> neighbors = new ArrayList<>();
		
		int px = x+1, sx = x-1;
		int py = y+1, sy = y-1;
		
		if (torus) {
			if (px >= cells.length)
				px = 0;
			if (sx < 0)
				sx = cells.length-1;

			if (py >= cells[x].length)
				py = 0;
			if (sy < 0)
				sy = cells[x].length-1;
		}
		
		if (px < cells.length)
			neighbors.add(cells[px][y]);
		if (sx >= 0)
			neighbors.add(cells[sx][y]);
		if (py < cells[x].length)
			neighbors.add(cells[x][py]);
		if (sy >= 0)
			neighbors.add(cells[x][sy]);
		if (px < cells.length && py < cells[x].length)
			neighbors.add(cells[px][py]);
		if (sx >= 0 && sy >= 0)
			neighbors.add(cells[sx][sy]);
		if (px < cells.length && sy >= 0)
			neighbors.add(cells[px][sy]);
		if (sx >= 0 && py < cells[x].length)
			neighbors.add(cells[sx][py]);
		
		int alive = 0;
		
		for (Cell c : neighbors) {
			if (c.isAlive())
				alive++;
		}
		
		boolean shouldLive = false;
		
		if (alive < birth)
			shouldLive = false;
		else if (alive == birth && !cells[x][y].isAlive())
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
		
		boolean toggle = !(shouldLive == cells[x][y].isAlive());
		
		count++;
		
		cellMap.put(cells[x][y], toggle);
		
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
	
	public void notifyObserver() {
		observer.update(this);
		count = 0;
		cellMap.clear();
	}
	
	public void setObserver(CellObserver obeserver) {
		this.observer = obeserver;
	}
	
	public Map<Cell, Boolean> getMap() {
		return cellMap;
	}
	
}
