public class Act {
	Tile selectedTile;
	Tile movingTile;
	Tile attackTileBefore;
	Tile attackTileAfter;
	
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
	
	public void setMovingTile(Tile movingTile) {
		this.movingTile = movingTile;
	}
	
	public void setAttackTileBefore(Tile attackTileBefore) {
		this.attackTileBefore = attackTileBefore;
	}
	
	public void setAttackTileAfter(Tile attackTileAfter) {
		this.attackTileAfter = attackTileAfter;
	}
}
