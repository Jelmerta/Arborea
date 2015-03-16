/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Random;
import java.lang.Math;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.image.BufferedImage;

abstract class Figure {
    
    // indexes of types
    final static byte TYPE_NONE = 0, TYPE_SWORD = 1, TYPE_GENERAL = 2, TYPE_GOBLIN = 3, TYPE_ORC = 4;

	// TODO deze images zijn dubbel met artmanager
	static final BufferedImage iconAttack = ArtManager.iconAttack;
	static final BufferedImage iconMove = ArtManager.iconMove;
	static final BufferedImage iconHealthbar = ArtManager.iconHealthbar;
	static final BufferedImage iconHealthbarGreen = ArtManager.iconHealthbarGreen;
	static final BufferedImage iconHealthbarRed = ArtManager.iconHealthbarRed;

    int startHitpoints, hit, weapon, type;
    int index;
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
       	System.out.println(this.getIndex());
    	if(this.getTeam()) {
    		grid.orcs.update(this.getIndex(), this);
    	} else {
    		grid.humans.update(this.getIndex(), this);
    	}
    }
    
    //not used now
    boolean canMove(Grid grid, Tile destinationTile){
        if(destinationTile != null) {
            Tile currentTile = grid.getTile(location);
        	return !destinationTile.hasFigure() && Arrays.asList(currentTile.getNeighbours()).contains(destinationTile);
        } else
            return false;
    }

    //TODO doing this with pythagoras, probably not a good way to do this because of hexagonal grid
    public double lengthToMiddleOfTeam(Tile bestTile, Team thisTeam) {
    	double[] middleOfTeam = thisTeam.getAverageMiddlePointOfTeam(); //thisTeam = Arborea.grid.orcs or humans
    	Point location = bestTile.getLocation();
    	return Math.sqrt(Math.pow((location.getX() - middleOfTeam[0]), 2) + Math.pow((location.getY() - middleOfTeam[1]), 2));
    }
    
	public void attack(Grid grid, Figure attacked) {	
    	 double hitChance = calculateChance(this.weapon+this.calculateAdjacencyBonus(grid), attacked.weapon+attacked.calculateAdjacencyBonus(grid));
    	 boolean imHitCaptain = Math.random() < hitChance;
    	 if(imHitCaptain) {
    		 attacked.hit--;
    		 if(attacked.hit <= 0) {
    			 removeFromField(grid, attacked);
    			 grid.removeFromTeam(attacked.getTeam(), attacked);
    			 
    		 }
    	 }
		 System.out.println("This unit has " + attacked.hit + " HP left.");
    }
	
	private boolean isNextMoveOffensive(Grid grid) {
		double lengthToOwnTeam;
		if(this.getTeam()) {
			lengthToOwnTeam = lengthToMiddleOfTeam(grid.getTile(this.getLocation()), grid.orcs);
		} else {
			lengthToOwnTeam = lengthToMiddleOfTeam(grid.getTile(this.getLocation()), grid.humans);
		}
		if(lengthToOwnTeam > 2) {
			return false;
		} else {
			return true;
		}
	}
    
	private Tile getMoveCloserToTeam(Grid grid, Boolean orcs, ArrayList<Tile> neighboursMoveable) {
		Tile bestTile = grid.getTile(this.getLocation());
		double bestLength;
		double currentLength;
		
		if(orcs) {
			bestLength = lengthToMiddleOfTeam(bestTile, grid.orcs);
			for(Tile currentTile : neighboursMoveable) {
				if(((currentLength = lengthToMiddleOfTeam(currentTile, grid.orcs)) < bestLength)){
					bestLength = currentLength;
					bestTile = currentTile;
				}
			}
		} else {
			bestLength = lengthToMiddleOfTeam(bestTile, grid.humans);
			for(Tile currentTile : neighboursMoveable) {
				//currentTile.getNeighbours();
				//System.out.println("this tile ");// + currentTile);
				currentLength = lengthToMiddleOfTeam(currentTile, grid.humans);
				//System.out.println("length = " + currentLength);
				if(currentLength < bestLength) {
					//System.out.println("hi");
					bestLength = currentLength;
					bestTile = currentTile;
				}
			}
		}
		//System.out.println(bestTile);
		return bestTile;
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
    
    private Figure calculateLowestAdjacencyEnemyFigure(Grid grid) {
    	Tile[] neighbours = grid.getTile(this.getLocation()).getNeighbours();
    	Figure currentFigure;
    	int currentAdjacency;
    	int worstAdjacency = 1000;
    	boolean gotValue = false;
    	Figure worstAdjacencyFigure = null;
		for(Tile currentTile : neighbours) {
			if(currentTile != null) {
				currentFigure = currentTile.getFigure();
				if(currentFigure != null) {
					if((currentAdjacency = currentFigure.calculateAdjacencyBonus(grid)) < worstAdjacency) {
						worstAdjacency = currentAdjacency;
						worstAdjacencyFigure = currentFigure;
						gotValue = true;
					}
				}
			}
		}
		if(gotValue) {
			return worstAdjacencyFigure;
		} else {
			return null;
		}
    }
    
    private Figure calculateLowestHitEnemyFigure(Grid grid) {
    	Tile[] neighbours = grid.getTile(this.getLocation()).getNeighbours();
    	Figure currentFigure;
    	int currentHP;
    	int worstHP = 1000;
    	boolean gotValue = false;
    	Figure worstHPFigure = null;
		for(Tile currentTile : neighbours) {
			if(currentTile != null) {
				currentFigure = currentTile.getFigure();
				if(currentFigure != null) {
					if((currentHP = currentFigure.getHitpoints()) < worstHP) {
						worstHP = currentHP;
						worstHPFigure = currentFigure;
						gotValue = true;
					}
				}
			}
		}
		if(gotValue) {
			return worstHPFigure;
		} else {
			return null;
		}
    }
    
    static private double calculateChance(int weaponSkills, int weaponSkillsAttacked) {
		return 1/(1+Math.exp(-0.4*(weaponSkills-weaponSkillsAttacked)));
	}

	public boolean inRange(Grid grid, Figure attacked) {
		Tile currentTile = grid.getTile(this.location);
		Tile destinationTile = grid.getTile(attacked.location);
		if(Arrays.asList(currentTile.getNeighbours()).contains(destinationTile))
			return true;
		else
			return false;
	}
	
	private void removeFromField(Grid grid, Figure attacked) {
	    Tile deadTile = grid.getTile(attacked.location);
	    deadTile.setFigure(null);
	    if(attacked.getTeam()) {
	    	grid.orcs.update(attacked.getIndex(), null);
	    } else {
	    	grid.humans.update(attacked.getIndex(), null);
	    }
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
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
	
	public int getHitpoints() {
		return this.hit;
	}
	
	public int getStartHitpoints() {
		return this.startHitpoints;
	}
	
	// Figure AI
	public Point[] getAI(Grid aiGrid, Grid gridAttackBefore, Grid gridAttackAfter) {
		Point characterMove;
		Figure characterAttackBefore;
		Figure characterAttackAfter;
		
		Tile currentTile = aiGrid.getTile(location);
		Figure currentFigure = currentTile.getFigure();
		Tile[] neighbours = currentTile.getNeighbours();
		ArrayList<Tile> neighboursNotNull = new ArrayList<Tile>();
		ArrayList<Tile> neighboursMoveable = new ArrayList<Tile>();
		ArrayList<Tile> neighboursAttackable = new ArrayList<Tile>();
		
		//Random randomizer = new Random();
		//int randomIndex;
	
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
			characterAttackBefore = getbestAttack(gridAttackBefore, neighboursAttackable);
		} else {
			characterAttackBefore = null;
		}
		
		if(characterAttackBefore != null) {
			currentFigure.attack(gridAttackBefore, characterAttackBefore);
		}
		
		// Movement
		// eigenlijk beter met canMove maar die is al een tijd niet getest
		if(!neighboursMoveable.isEmpty()) {
			//randomIndex = randomizer.nextInt(neighboursMoveable.size()); // TODO dont do this randomly
			//characterMove = neighboursMoveable.get(randomIndex).getLocation();
			boolean offensive = isNextMoveOffensive(aiGrid);
			//System.out.println("offensive: " + offensive);
			if(offensive) {
				characterMove = currentFigure.getMoveCloserToTeam(aiGrid, !currentFigure.getTeam(), neighboursMoveable).getLocation();
			} else {
				characterMove = currentFigure.getMoveCloserToTeam(aiGrid, currentFigure.getTeam(), neighboursMoveable).getLocation();
			}
			if(characterMove == currentTile.getLocation())  {
				characterMove = null;
			}
		} else {
			characterMove = null;
		}
		//System.out.println("currentFigure: " + this + " characterMove: " + characterMove);
		
		if(characterMove != null) {
			currentFigure.move(gridAttackBefore, currentTile);
			currentTile = gridAttackBefore.getTile(characterMove.getLocation());
			currentFigure.move(gridAttackAfter, currentTile);
			currentTile = gridAttackAfter.getTile(characterMove.getLocation());
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
			characterAttackAfter = getbestAttack(gridAttackAfter, neighboursAttackable);
		} else {
			characterAttackAfter = null;
		}		
		
		if(characterAttackAfter != null) {
			currentFigure.attack(gridAttackAfter, characterAttackAfter);
		}
		
		
		
		Point[] characterAttackBeforeMoveAndAttackAfter = new Point[3];
		
		int bestAttack;
		if(characterAttackBefore != null && characterAttackAfter != null) {
			bestAttack = compareAttackBeforeAndAfter(gridAttackBefore, gridAttackAfter, characterAttackBefore, characterAttackAfter);
		} else if(characterAttackBefore != null) {
			bestAttack = 1;
		} else if(characterAttackAfter != null) {
			bestAttack = 2;
		} else {
			bestAttack = 0;
		}
		if(bestAttack == 0) {
			characterAttackBeforeMoveAndAttackAfter[0] = null;
			characterAttackBeforeMoveAndAttackAfter[2] = null;
		} else if(bestAttack == 1) {
			characterAttackBeforeMoveAndAttackAfter[0] = characterAttackBefore.getLocation();
			characterAttackBeforeMoveAndAttackAfter[2] = null;
			aiGrid = gridAttackBefore;
		} else {
			characterAttackBeforeMoveAndAttackAfter[0] = null;
			characterAttackBeforeMoveAndAttackAfter[2] = characterAttackAfter.getLocation();
			aiGrid = gridAttackAfter;
		}	
		
		if(characterMove != null) {
			characterAttackBeforeMoveAndAttackAfter[1] = characterMove.getLocation();
		} else {
			characterAttackBeforeMoveAndAttackAfter[1] = null;
		}
		return characterAttackBeforeMoveAndAttackAfter;
	}
	
	//TODO needs to be specific for the grid. now making it so it can be checked in different states
	private Figure getbestAttack(Grid testGrid, ArrayList<Tile> arrayAttackTiles) {
		if(arrayAttackTiles.size() == 1) 
			return arrayAttackTiles.get(0).getFigure();
		
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
			return lowestAttackHPFigure;
		} else {
			return worstAdjacencyFigure;
		}
	}
	
	//maybe just make separate functions returning lowest adjacency and lowest hitpoints and compare those TODO 
	// 0 is no attack, 1 is attack before, 2 attack after
	private int compareAttackBeforeAndAfter(Grid gridBefore, Grid gridAfter, Figure attackBefore, Figure attackAfter) {
		if(attackBefore.getHitpoints() == 1) {
			return 1;
		} else if (attackAfter.getHitpoints() == 1) { 
			return 2;
		} else if(attackBefore.calculateAdjacencyBonus(gridBefore) < attackAfter.calculateAdjacencyBonus(gridAfter)) {
			return 1;
		} else {
			return 2;
		}
	}
}