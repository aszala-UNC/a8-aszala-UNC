package a8;

public abstract class UIEvent {
	
	public boolean isSliderEvent() {
		return false;
	}
	
	public boolean isGridEvent() {
		return false;
	}
	
	public boolean isCheckBoxEvent() {
		return false;
	}
	
}

class CheckBoxEvent extends UIEvent {

	private boolean value;
	
	public CheckBoxEvent(boolean value) {
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	@Override
	public boolean isCheckBoxEvent() {
		return true;
	}
	
}

class SliderEvent extends UIEvent {
	
	public static enum Intent { INTENT_SPEED }
	
	private int value;
	private Intent intent;
	
	public SliderEvent(int value, Intent intent) {
		this.value = value;
		this.intent = intent;
	}
	
	public Intent getTag() {
		return intent;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public boolean isSliderEvent() {
		return true;
	}
}

class GridEvent extends UIEvent {
	
	public static enum Intent { INTENT_REFRESH, INTENT_RANDOM, INTENT_CLEAR, INTENT_ADVANCE, INTENT_START_AUTO, INTENT_STOP_AUTO }
	
	private Intent intent;
	
	private int[] data;
	
	public GridEvent(Intent intent, int... data) {
		this.intent = intent;
		this.data = data;
	}
	
	public int[] getData() {
		return data;
	}
	
	public Intent getIntent() {
		return intent;
	}
	
	@Override
	public boolean isGridEvent() {
		return true;
	}
	
}
