public class Act {
	Tile selectedTile = null;
	Tile movingTile = null;
	Tile attackTileBefore = null;
	Tile attackTileAfter = null;
	
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
	
	public void setSelectedTile(Tile selectedTile) {
		this.selectedTile = selectedTile;
	}
	
	public Tile getSelectedTile() {
		return this.selectedTile;
	}
	
	public void setMovingTile(Tile movingTile) {
		this.movingTile = movingTile;
	}
	
	public Tile getMovingTile() {
		return this.movingTile;
	}
	
	public void setAttackTileBefore(Tile attackTileBefore) {
		this.attackTileBefore = attackTileBefore;
	}
	
	public Tile getAttackTileBefore() {
		return this.attackTileBefore;
	}
	
	public void setAttackTileAfter(Tile attackTileAfter) {
		this.attackTileAfter = attackTileAfter;
	}
	
	public Tile getAttackTileAfter() {
		return this.attackTileAfter;
	}
	
	public void printAct() {
		System.out.println("currentAct: ");
		System.out.println(selectedTile);
		System.out.println(attackTileBefore);
		System.out.println(movingTile);
		System.out.println(attackTileAfter);
	}
}
