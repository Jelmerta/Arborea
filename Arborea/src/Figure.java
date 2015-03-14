import java.util.Arrays;
import java.lang.Math;
import java.awt.Point;
import java.awt.image.BufferedImage;

abstract class Figure {
    
    // indexes of types
    final static byte TYPE_NONE = 0, TYPE_SWORD = 1, TYPE_GENERAL = 2, TYPE_GOBLIN = 3, TYPE_ORC = 4;
    static final BufferedImage swordImage = ArtManager.swordImage;
    static final BufferedImage generalImage = ArtManager.generalImage;
	static final BufferedImage goblinImage = ArtManager.goblinImage;
	static final BufferedImage orcImage = ArtManager.orcImage;
	
    int hit, weapon, type;
    Point location;
    boolean teamIsOrcs;
    boolean hasMoved = false;
    boolean hasAttacked = false;
    
    Figure(){
        
    }
    
    Figure(int startType, Point position) {
        this.type = startType;
        this.location  = position;
    }
    
    public void move(Tile destinationTile) {
       	destinationTile.setFigure(this);
       	this.location = destinationTile.getLocation();
    }
    
    //not used now
    boolean canMove(Tile destinationTile){
        if(destinationTile != null) {
            Tile currentTile = Grid.getTile(location);
        	return !destinationTile.hasFigure() && Arrays.asList(currentTile.getNeighbours()).contains(destinationTile);
        } else
            return false;
    }

	public void attack(Figure attacked) {
		 Tile attackedTile = Grid.getTile(attacked.location);
    	 double hitChance = calculateChance(this.weapon+this.calculateAdjacencyBonus(), attacked.weapon+attacked.calculateAdjacencyBonus());
    	 boolean imHitCaptain = Math.random() < hitChance;
    	 if(imHitCaptain) {
    		 attacked.hit--;
    		 if(attacked.hit <= 0) {
    			 removeFromField(attacked);
    			 Grid.removeFromTeam(attacked.getTeam(), attacked);
    		 }
    	 }
		 System.out.println(attacked.hit);
    }
    
    // For friendly units, there is a bonus for your weapon skill. This is contrary for enemy units and will decrease your weapon skill.
    public int calculateAdjacencyBonus() {
        int bonus = 0;
        Tile thisTile = Grid.getTile(this.location);
        
        for(Tile neighbour : thisTile.getNeighbours()) {
        	if(neighbour != null) {
	            if(neighbour.getCharacterType() == TYPE_NONE)
	                continue;
	            if(neighbour.getCharacterType() == TYPE_SWORD)
	                bonus++;
	            if(neighbour.getCharacterType() == TYPE_GENERAL)
	                bonus+=2;
	            if(neighbour.getCharacterType() == TYPE_GOBLIN)
	                bonus--;
	            if(neighbour.getCharacterType() == TYPE_ORC)
	                bonus-=2;
        	}
        }
        if(type == 3 || type == 4)
            bonus = -1*bonus;
        return bonus;
    }
    
    static private double calculateChance(int weaponSkills, int weaponSkillsAttacked) {
		return 1/(1+Math.exp(-0.4*(weaponSkills-weaponSkillsAttacked)));
	}

	public boolean inRange(Figure attacked) {
		Tile currentTile = Grid.getTile(this.location);
		Tile destinationTile = Grid.getTile(attacked.location);
		if(Arrays.asList(currentTile.getNeighbours()).contains(destinationTile))
			return true;
		else
			return false;
	}
	
	private void removeFromField(Figure attacked) {
	    Tile deadTile = Grid.getTile(attacked.location);
	    deadTile.setFigure(null); //Is dit genoeg of moet er ook nog iets met graphics veranderen?
	}
	
	public int returnType() {
        return type;
    }
	
	public boolean getTeam() {
		return teamIsOrcs;
	}
	
	public void setMoved(boolean moved) {
		hasMoved = moved;
	}
	
	public void setAttacked(boolean attacked) {
		hasAttacked = attacked;
	}
	
	public boolean hasMovesLeft() {
		return (!hasMoved);
	}
	
	public boolean hasAttacksLeft() {
		return (!hasAttacked);
	}
}