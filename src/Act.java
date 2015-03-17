/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

// this is an object that represents the action of a character
class Act {
	Tile selectedTile = null;
	Tile movingTile = null;
	Tile attackTileBefore = null;
	Tile attackTileAfter = null;
	
	// an Act can be made with or without predefined target tiles
	public Act() {
	}
	public Act(Tile selectedTile) {
		this.selectedTile = selectedTile;
	}	
	public Act(Tile currentFigureTile, Tile movingTile, Tile attackTileBefore, Tile attackTileAfter) {
		this.selectedTile = currentFigureTile;
		this.movingTile = movingTile;
		this.attackTileBefore = attackTileBefore;
		this.attackTileAfter = attackTileAfter;
	}
	
	// get/set-methods for storing the selected tile of the action
	public void setSelectedTile(Tile selectedTile) {
		this.selectedTile = selectedTile;
	}	
	public Tile getSelectedTile() {
		return this.selectedTile;
	}

	// get/set-methods for storing the target tile of an action
	public void setMovingTile(Tile movingTile) {
		this.movingTile = movingTile;
	}	
	public Tile getMovingTile() {
		return this.movingTile;
	}

	// get/set-methods for storing a tile before an action
	public void setAttackTileBefore(Tile attackTileBefore) {
		this.attackTileBefore = attackTileBefore;
	}	
	public Tile getAttackTileBefore() {
		return this.attackTileBefore;
	}
	
	// get/set-methods for storing a tile after an action
	public void setAttackTileAfter(Tile attackTileAfter) {
		this.attackTileAfter = attackTileAfter;
	}	
	public Tile getAttackTileAfter() {
		return this.attackTileAfter;
	}
	
	// returns the String representation of the action
	@Override
	public String toString() {
		String s = "currentAct:\n"
		 + selectedTile + "\n" + attackTileBefore + "\n"
		 + movingTile + "\n" + attackTileAfter;
		return s;
	}
}
