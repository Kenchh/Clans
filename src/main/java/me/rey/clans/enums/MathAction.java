package me.rey.clans.enums;

public enum MathAction {

	SET(),
	ADD(),
	REMOVE();
	
	public int calc(int initial, int toCalc) {
		switch(this) {
		
		case SET:
			return toCalc;
			
		case ADD:
			return initial + toCalc;
			
		case REMOVE:
			return initial - toCalc;
			
		default:
			return 0;
		}
	}
}
