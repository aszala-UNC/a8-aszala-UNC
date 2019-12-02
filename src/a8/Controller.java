package a8;

import java.awt.Point;
import java.util.Map;
import java.util.Map.Entry;

public class Controller implements CellViewListener, CellObserver {

	private View view;
	private Model model;
	
	private Thread autoThread;
	
	private int autoDelay = 10;
	
	private boolean runAuto = false;
	
	public Controller(Model model, View view) {
		this.view = view;
		this.model = model;
		
		view.setListener(this);
		model.setObserver(this);
		
		autoThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (runAuto) {
						advance();
						
						try {
							Thread.sleep(autoDelay-2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					try {
						Thread.sleep(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		autoThread.start();
	}

	@Override
	public synchronized void handleEvent(Event e) {
		if (e.isUIEvent()) {
			UIEvent ue = (UIEvent)e;
			
			if (ue.isGridEvent()) {
				GridEvent ge = (GridEvent)ue;
				
				if (ge.getIntent() == null)
					return;
				
				if (ge.getIntent() == GridEvent.Intent.INTENT_REFRESH) {
					runAuto = false;
					view.clear();
					
					model.setSurviveAndBirthValues(ge.getData()[0], ge.getData()[1]);
					view.changeGridSize(ge.getData()[2], ge.getData()[3]);
				} else if (ge.getIntent() == GridEvent.Intent.INTENT_RANDOM) {
					view.clear();
					
					int randomCount = (int)(Math.random() * view.getGridWidth() * view.getGridHeight() / 10) + 5;
					
					for (int i=0;i<randomCount;i++) {
						int randomX = (int)Math.floor(Math.random() * view.getGridWidth());
						int randomY = (int)Math.floor(Math.random() * view.getGridHeight());
						
						view.toggleCell(randomX, randomY);
					}
				} else if (ge.getIntent() == GridEvent.Intent.INTENT_CLEAR) {
					view.clear();
				} else if (ge.getIntent() == GridEvent.Intent.INTENT_ADVANCE) {
					advance();
				} else if (ge.getIntent() == GridEvent.Intent.INTENT_START_AUTO) {
					runAuto = true;
				}  else if (ge.getIntent() == GridEvent.Intent.INTENT_STOP_AUTO) {
					runAuto = false;
				}
			} else if (ue.isCheckBoxEvent()) {
				model.toggleTorus(((CheckBoxEvent)(ue)).getValue());
			} else if (ue.isSliderEvent()) {
				int speed = ((SliderEvent)(ue)).getValue();
				autoDelay = speed;
				view.updateSpeedDisplay(speed);
			}
		}
		
	}

	private void advance() {
		final boolean[][] cells = view.getCells();
		final int total = view.getGridWidth() * view.getGridHeight();
		
		for (int i=0;i<view.getGridWidth();i++) {
			for (int j=0;j<view.getGridHeight();j++) {
				model.evaluateCell(cells, i, j, total);
			}
		}
	}
	
	@Override
	public void update(Model model) {
		Map<Point, Boolean> map = model.getMap();
		
		for (Entry<Point, Boolean> entry : map.entrySet()) {
			if (entry.getValue())
				view.toggleCell(entry.getKey().x, entry.getKey().y);
		}
	}
	
}
