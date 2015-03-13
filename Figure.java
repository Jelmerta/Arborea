/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.lang.Math;
import java.awt.Point;
import java.awt.image.BufferedImage;

abstract class Figure {
    
    // indexes of types
    final static byte TYPE_NONE = 0, TYPE_SWORD = 1, TYPE_GENERAL = 2, TYPE_GOBLIN = 3, TYPE_ORC = 4;

	// TODO deze images zijn dubbel met artmanager
	static final BufferedImage iconAttack = ArtManager.iconAttack;
	static final BufferedImage iconMove = ArtManager.iconMove;
	
    int hit, weapon, type;
    Point location;
    boolean teamIsOrcs;
    private boolean hasMoved = true;
    private boolean hasAttacked = true;
    
    Thread animationThread;
    Animator animator;
    
    ArrayList<BufferedImage> standSprites;
    
    void setUpSprites(){
    	setUpStandSprites();
    }
    abstract void setUpStandSprites();
    
    BufferedImage getStandSprite(){
    	return standSprites.get(animator.getAnimationIndex());
    }
	
	// creates animation thread and starts it
	void startAnimation(){
		if (animator != null) stopAnimation();
		
		animator = new Animator(standSprites.size(), 400);
		animationThread = new Thread(animator);
		animationThread.start();
	}
	
	// destroys the animation thread
	void stopAnimation(){
		animationThread.interrupt();
		animator = null;
	}
    
    Figure(){
        
    }
    
    Figure(int startType, Point position) {
        this.type = startType;
        this.location = position;
        this.setUpSprites();
        this.startAnimation();
    }
    
    // Uses grid in case of further AI development
    public void move(Grid grid, Tile destinationTile) {    	
    	grid.getTile(this.getLocation()).setFigure(null);
       	destinationTile.setFigure(this);
       	setLocation(destinationTile.getLocation());
    }
    
    //not used now
    boolean canMove(Grid grid, Tile destinationTile){
        if(destinationTile != null) {
            Tile currentTile = grid.getTile(location);
        	return !destinationTile.hasFigure() && Arrays.asList(currentTile.getNeighbours()).contains(destinationTile);
        } else
            return false;
    }

	public void attack(Grid grid, Figure attacked) {	
    	 double hitChance = calculateChance(this.weapon+this.calculateAdjacencyBonus(grid), attacked.weapon+attacked.calculateAdjacencyBonus(grid));
    	 boolean imHitCaptain = Math.random() < hitChance;
    	 if(imHitCaptain) {
    		 attacked.hit--;
    		 if(attacked.hit <= 0) {
    			 removeFromField(attacked);
    			 grid.removeFromTeam(attacked.getTeam(), attacked);
    		 }
    	 }
		 System.out.println(attacked.hit);
    }
    
    // For friendly units, there is a bonus for your weapon skill. This is contrary for enemy units and will decrease your weapon skill.
    public int calculateAdjacencyBonus(Grid grid) {
        int bonus = 0;
        Tile thisTile = grid.getTile(this.location);
        
        for(Tile neighbour : thisTile.getNeighbours()) {
        	if(neighbour != null) {
	            if(neighbour.getCharacterType() == TYPE_NONE)
	                continue;
	            if(neighbour.getCharacterType() == TYPE_SWORD) {
	                bonus++;
	                continue;
	            } else if(neighbour.getCharacterType() == TYPE_GENERAL) {
	                bonus+=2;
	                continue;
	            } else if(neighbour.getCharacterType() == TYPE_GOBLIN) {
	                bonus--;
	                continue;
	            } else if(neighbour.getCharacterType() == TYPE_ORC)
	                bonus-=2;
        	}
        }
        if(this.type == 3 || this.type == 4)
            bonus = -1*bonus;
        return bonus;
    }
    
    static private double calculateChance(int weaponSkills, int weaponSkillsAttacked) {
		return 1/(1+Math.exp(-0.4*(weaponSkills-weaponSkillsAttacked)));
	}

	public boolean inRange(Figure attacked) {
		Tile currentTile = Arborea.grid.getTile(this.location);
		Tile destinationTile = Arborea.grid.getTile(attacked.location);
		if(Arrays.asList(currentTile.getNeighbours()).contains(destinationTile))
			return true;
		else
			return false;
	}
	
	private void removeFromField(Figure attacked) {
	    Tile deadTile = Arborea.grid.getTile(attacked.location);
	    deadTile.setFigure(null); 
	    //Is dit genoeg of moet er ook nog iets met graphics veranderen?
	    // TODO yo zet er TODOs bij zodat we het later terug kunnen vinden
	}
	
	public void setLocation(Point location) {
		this.location = location;
	}
	public Point getLocation() {
		return location;
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
	
	private int getHitpoints() {
		return this.hit;
	}
	
	// Figure AI
	public Point[] getAI() {
		Point characterMove;
		Point characterAttackBefore;
		Point characterAttackAfter;
		
		Tile currentTile = Arborea.grid.getTile(location);
		Figure currentFigure = currentTile.getFigure();
		Tile[] neighbours = currentTile.getNeighbours();
		ArrayList<Tile> neighboursNotNull = new ArrayList<Tile>();
		ArrayList<Tile> neighboursMoveable = new ArrayList<Tile>();
		ArrayList<Tile> neighboursAttackable = new ArrayList<Tile>();
		
		Random randomizer = new Random();
		int randomIndex;
		
		//boolean checkAttackAfter;
			
		// Setup before move
		for (Tile currentNeighbourTile : neighbours) {
			if(currentNeighbourTile != null) {
				neighboursNotNull.add(currentNeighbourTile);
			}
		}		

		for (Tile currentNeighbourTileNotNull : neighboursNotNull) {
			if(!currentNeighbourTileNotNull.hasFigure())
				neighboursMoveable.add(currentNeighbourTileNotNull);
		}
		
		// Before move attack
		for (Tile currentNeighbourTileNotNull : neighboursNotNull) {
			if(currentNeighbourTileNotNull.hasFigure() && currentNeighbourTileNotNull.getFigure().getTeam() != this.getTeam()) {
				neighboursAttackable.add(currentNeighbourTileNotNull);
			}
		}
		
		//eigenlijk beter met een canAttack
		if(!neighboursAttackable.isEmpty()) {
			characterAttackBefore = getbestAttack(Arborea.grid, neighboursAttackable);
			//randomIndex = randomizer.nextInt(neighboursAttackable.size());
			//get best attack
			//characterAttack = neighboursAttackable.get(randomIndex).getLocation();
		} else {
			characterAttackBefore = null;
		}
		
		// Movement
		// eigenlijk beter met canMove maar die is al een tijd niet getest
		if(!neighboursMoveable.isEmpty()) {
			randomIndex = randomizer.nextInt(neighboursMoveable.size()); // TODO dont do this randomly
			characterMove = new Point(neighboursMoveable.get(randomIndex).getLocation());
		} else {
			characterMove = null;
		}
		
		if(characterMove != null) {
			currentFigure.move(Arborea.aiGrid, currentTile);
			currentTile = Arborea.aiGrid.getTile(characterMove);
			//checkAttackAfter = true;
		} else {
			//what do when can't move TODO break /returnwith just attack
		}

		// Attack after move
		neighboursNotNull.clear();
		neighboursAttackable.clear();
		neighbours = currentTile.getNeighbours();
		
		for (Tile currentNeighbourTile : neighbours) {
			if(currentNeighbourTile != null) {
				neighboursNotNull.add(currentNeighbourTile);
			}
		}	
		
		for (Tile currentNeighbourTileNotNull : neighboursNotNull) {
			if(currentNeighbourTileNotNull.hasFigure() && currentNeighbourTileNotNull.getFigure().getTeam() != this.getTeam()) {
				neighboursAttackable.add(currentNeighbourTileNotNull);
			}
		}
		
		//eigenlijk beter met een canAttack
		if(!neighboursAttackable.isEmpty()) {
			//randomIndex = randomizer.nextInt(neighboursAttackable.size());
			//characterAttackAfter = new Point(neighboursAttackable.get(randomIndex).getLocation());
			characterAttackAfter = getbestAttack(Arborea.aiGrid, neighboursAttackable);
		} else {
			characterAttackAfter = null;
		}		
		
		//if(characterAttack != null) {
			//Tile attackTile = aiGrid.getTile(characterAttack);
			//Figure attackedFigure = attackTile.getFigure();
			//currentFigure.attack(aiGrid, attackedFigure); TODO why no work
		//}
		
		Point[] characterAttackBeforeMoveAndAttackAfter = new Point[3];
		int bestAttack = compareAttackBeforeAndAfter(characterAttackBefore, characterAttackAfter);
		if(bestAttack == 0) {
			characterAttackBeforeMoveAndAttackAfter[0] = null;
			characterAttackBeforeMoveAndAttackAfter[2] = null;
		} else if(bestAttack == 1) {
			characterAttackBeforeMoveAndAttackAfter[0] = characterAttackBefore;
			characterAttackBeforeMoveAndAttackAfter[2] = null;
		} else {
			characterAttackBeforeMoveAndAttackAfter[0] = null;
			characterAttackBeforeMoveAndAttackAfter[2] = characterAttackAfter;
		}	
		
		characterAttackBeforeMoveAndAttackAfter[1] = characterMove;
		return characterAttackBeforeMoveAndAttackAfter;
	}
	
	//TODO needs to be specific for the grid
	private Point getbestAttack(Grid testGrid, ArrayList<Tile> arrayAttackTiles) {
		if(arrayAttackTiles.size() == 1) 
			return arrayAttackTiles.get(0).getLocation();
		
		Figure currentAttackFigure;
		Figure worstAdjacencyFigure = null;
		Figure lowestAttackHPFigure = null;
		int currentAdjacency;
		int worstAdjacency = 1000;
		int currentAttackHP;
		int lowestAttackHP = 1000;
		for(Tile attackTile : arrayAttackTiles) {
			currentAttackFigure = attackTile.getFigure();
			currentAttackHP = currentAttackFigure.getHitpoints();
			if(currentAttackHP < lowestAttackHP) {
				lowestAttackHP = currentAttackHP;
				lowestAttackHPFigure = currentAttackFigure;
			}
			if((currentAdjacency = currentAttackFigure.calculateAdjacencyBonus(testGrid)) < worstAdjacency) { 
				worstAdjacency = currentAdjacency;
				worstAdjacencyFigure = currentAttackFigure;
			}
		}
		if(lowestAttackHP == 1) {
			return lowestAttackHPFigure.getLocation();
		} else {
			return worstAdjacencyFigure.getLocation();
		}
	}
	
	//maybe just make separate functions returning lowest adjacency and lowest hitpoints and compare those TODO 
	// 0 is no attack, 1 is attack before, 2 attack after
	private int compareAttackBeforeAndAfter(Point attackBefore, Point attackAfter) {
		if(attackBefore == null && attackAfter == null) 
			return 0;
		else if(attackBefore != null && attackAfter == null)
			return 1;
		else if(attackBefore == null && attackAfter != null)
			return 2;
		else {
			Figure currentFigureBefore;
			Figure worstAdjacencyFigureBefore = null;
			Figure lowestAttackHPFigureBefore = null;
			int currentAdjacency;
			int worstAdjacencyBefore = 1000;
			int currentAttackHP;
			int lowestAttackHPBefore = 1000;
			
			Tile tileBefore = Arborea.grid.getTile(attackBefore);
			//Figure figureBefore = tileBefore.getFigure();
			Tile tileAfter = Arborea.aiGrid.getTile(attackAfter);
			//Figure figureAfter = tileAfter.getFigure();
			Tile[] neighboursBefore = tileBefore.getNeighbours();
			Tile[] neighboursAfter = tileAfter.getNeighbours();
			
			for(Tile currentTile : neighboursBefore) {
				currentFigureBefore = currentTile.getFigure();
				if((currentAdjacency = currentFigureBefore.calculateAdjacencyBonus(Arborea.grid)) < worstAdjacencyBefore) {
					worstAdjacencyBefore = currentAdjacency;
					worstAdjacencyFigureBefore = currentFigureBefore;
					if((currentAttackHP = currentFigureBefore.getHitpoints()) < lowestAttackHPBefore) {
						lowestAttackHPBefore = currentAttackHP;
						lowestAttackHPFigureBefore = currentFigureBefore;
					}
				}
			}
			
			Figure currentFigureAfter;
			Figure worstAdjacencyFigureAfter = null;
			Figure lowestAttackHPFigureAfter = null;
			int worstAdjacencyAfter = 1000;
			int lowestAttackHPAfter = 1000;
			
			for(Tile currentTile : neighboursAfter) {
				currentFigureAfter = currentTile.getFigure();
				if((currentAdjacency = currentFigureAfter.calculateAdjacencyBonus(Arborea.aiGrid)) < worstAdjacencyAfter) {
					worstAdjacencyAfter = currentAdjacency;
					worstAdjacencyFigureAfter = currentFigureAfter;
					if((currentAttackHP = currentFigureAfter.getHitpoints()) < lowestAttackHPAfter) {
						lowestAttackHPAfter = currentAttackHP;
						lowestAttackHPFigureAfter = currentFigureAfter;
					}
				}
			}
			
			//TODO get best one
			return 0;
			
		}
	}
}