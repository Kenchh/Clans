package me.rey.clans.enums;

public enum MathAction {

	SET(),
	ADD(),
	REMOVE();
	
	public long calc(long initial, long toCalc) {
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
