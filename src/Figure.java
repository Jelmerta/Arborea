/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;
import java.awt.Point;
import java.awt.image.BufferedImage;

// Figure is an abstract class that can be instantiated by Sword, General, Goblin, Orc
abstract class Figure {
    
    // indexes of types
    final static byte TYPE_NONE = 0, TYPE_SWORD = 1, TYPE_GENERAL = 2, TYPE_GOBLIN = 3, TYPE_ORC = 4;

	// indexes of types of artificial intelligence
	static final int AI_RANDOM = 0;
	static final int AI_TRUE = 1;
	
    // Images to see if it has an attack left, move left and its health bar
	static final BufferedImage iconAttack = ArtManager.iconAttack;
	static final BufferedImage iconMove = ArtManager.iconMove;
	static final BufferedImage iconHealthbar = ArtManager.iconHealthbar;
	static final BufferedImage iconHealthbarGreen = ArtManager.iconHealthbarGreen;
	
	// whether the character is facing eastward
	boolean facingRight = true;
	
	// starting values of a character
    int startHitpoints, hit, weapon, type;
    
    // Euclidean coordinates of the tile on which the character stands
    private Point location;
    
    // whether the character is an Orc or a Man
    boolean teamIsOrcs;
    
    // whether the character has performed certain moves
    private boolean hasMoved = true;
    private boolean hasAttacked = true;

    // Separate threads for each figure exist to run the animation
    Thread animationThread;
    Animator animator;
    
    // lists that contain sprites
    ArrayList<BufferedImage> standSprites;
    ArrayList<BufferedImage> standSpritesL;

    // animation is started upon construction
    Figure(int startType, Point position) {
        this.type = startType;
        this.location = position;
        this.setUpSprites();
        this.startAnimation();
    }
    
    // copy constructor
    Figure(Figure copy){
    	this.facingRight = copy.facingRight;
    	this.startHitpoints = copy.hit;
    	this.weapon = copy.weapon;
    	this.type = copy.type;
    	this.location = new Point(copy.location.x,copy.location.y);
    	this.teamIsOrcs = copy.teamIsOrcs;
    	this.hasMoved = copy.hasMoved;
    	this.hasAttacked = copy.hasAttacked;
    	this.animationThread = copy.animationThread;
    	this.animator = copy.animator;
    	this.standSprites = copy.standSprites;
    	this.standSpritesL = copy.standSpritesL;
    }
    
    // prepares the sprites for animation
    // since there are only stand sprites, only they will be set up
    void setUpSprites(){
    	setUpStandSprites();
    }
    
    // seeting up of stand sprites is defined in subclasses
    abstract void setUpStandSprites();
    
    // returns a sprite corresponding to the animator and the direction
    BufferedImage getStandSprite(){
    	if (facingRight)
    		return standSprites.get(animator.getAnimationIndex());
    	else
    		return standSpritesL.get(animator.getAnimationIndex());
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
    
    // moves a character to another tile
    void move(Grid grid, Tile destinationTile) {    	
    	if (destinationTile.getLocation().x < this.getLocation().x)
    		this.facingRight = false;
    	if (destinationTile.getLocation().x > this.getLocation().x)
    		this.facingRight = true;
    	grid.getTile(this.getLocation()).setFigure(null);
       	destinationTile.setFigure(this);
       	setLocation(destinationTile.getLocation());
    }
    
    // finds a defensive spot based on position of allies
    double lengthToMiddleOfTeam(Tile thisTile, Team thisTeam) {
    	double[] middleOfTeam = thisTeam.getAverageMiddlePointOfTeam();
    	Point location = thisTile.getLocation();
    	return (Math.abs((location.getX() - middleOfTeam[0])) + Math.abs(location.getY() - middleOfTeam[1]))
    			- (Math.abs(location.getX() - middleOfTeam[0]) - Math.abs(location.getY() - middleOfTeam[1])/2);    }
    
    // character attacks another character, based on a hit % and adjacencies
	void attack(Grid grid, Figure attacked) {
		
		// if attacked = null, figure is already dead
		if(attacked == null) return;
		
    	 double hitChance = calculateChance(this.weapon+this.calculateAdjacencyBonus(grid), attacked.weapon+attacked.calculateAdjacencyBonus(grid));
    	 boolean imHitCaptain = Math.random() < hitChance;
    	 if(imHitCaptain) {
    		 attacked.hit--;
    		 if(attacked.hit <= 0) {
    			 removeFromField(grid, attacked);
    		 }
    	 }
    	 hasAttacked = true;
    }
	
	// decides whether to be agressive or not, based on ally strength
	boolean isNextMoveOffensive(Grid grid, double threshold) {
		double lengthToOwnTeam;
		if(this.getTeam()) {
			lengthToOwnTeam = lengthToMiddleOfTeam(grid.getTile(this.getLocation()), grid.orcs);
		} else {
			lengthToOwnTeam = lengthToMiddleOfTeam(grid.getTile(this.getLocation()), grid.humans);
		}
		if(lengthToOwnTeam > threshold) {
			return false;
		} else {
			return true;
		}
	}
	
	// calculates best move, based on surroundings
	Act calculateBestMove(ArrayList<Act> allActs, boolean offensive) {
		
		// a new temporary grid is made
		Grid usedGrid = new Grid(Arborea.grid);
		
		int bestAdjacency = 1000;
		int currentAdjacency;
		double ownTeamDistance, enemyTeamDistance;
		Tile bestAdjacencyTile;
		Act bestAct = null;
		ArrayList<Act> oneHPNeighbourActs = new ArrayList<Act>();
		ArrayList<Act> offensiveActs = new ArrayList<Act>();
		ArrayList<Act> defensiveActs = new ArrayList<Act>();

		Tile thisTile = usedGrid.getTile(this.getLocation());
		if(this.getTeam()) {
			ownTeamDistance = lengthToMiddleOfTeam(thisTile, Arborea.grid.orcs);
			enemyTeamDistance = lengthToMiddleOfTeam(thisTile, Arborea.grid.humans);
		} else {
			ownTeamDistance = lengthToMiddleOfTeam(thisTile, Arborea.grid.orcs);
			enemyTeamDistance = lengthToMiddleOfTeam(thisTile, Arborea.grid.humans);
		}
		
		// check all acts for relevance
		for (Act currentAct : allActs) {
			Tile attackTileBefore = currentAct.getAttackTileBefore();
			if(attackTileBefore != null) {
				this.attack(usedGrid, attackTileBefore.getFigure());
			}
			Tile moveTile = currentAct.getMovingTile();
			if(moveTile != null) {
				this.move(usedGrid, moveTile);
				Tile[] neighbours = moveTile.neighbours;
				if(neighbours != null) {
					for(Tile neighbour : neighbours) {
						if(neighbour != null) {
							Figure neighbourFigure = neighbour.getFigure();
							if(neighbourFigure != null && neighbourFigure.getHitpoints() == 1) {
								Act oneHPAct = new Act();
								oneHPAct.setSelectedTile(thisTile);
								oneHPAct.setMovingTile(moveTile);
								oneHPAct.setAttackTileAfter(neighbour);
								oneHPNeighbourActs.add(oneHPAct);
							}
						}
					}
				}
			}
			Tile attackTileAfter = currentAct.getAttackTileAfter();
			if(attackTileAfter != null) {
				this.attack(usedGrid, attackTileAfter.getFigure());
			}
			
			// calculate distances
			Tile thisTileNew = usedGrid.getTile(this.getLocation());
			double ownTeamDistanceNew, enemyTeamDistanceNew;
			if(this.getTeam()) {
				ownTeamDistanceNew= lengthToMiddleOfTeam(thisTileNew, usedGrid.orcs);
				enemyTeamDistanceNew = lengthToMiddleOfTeam(thisTileNew, usedGrid.humans);
			} else {
				ownTeamDistanceNew = lengthToMiddleOfTeam(thisTileNew, usedGrid.orcs);
				enemyTeamDistanceNew = lengthToMiddleOfTeam(thisTileNew, usedGrid.humans);
			}			
			if(ownTeamDistanceNew < ownTeamDistance) {
				Act defensiveAct = new Act();
				defensiveAct.setSelectedTile(thisTileNew);
				defensiveAct.setMovingTile(moveTile); // no attack selected yet (select random one for acts that fit this move)
				defensiveActs.add(defensiveAct);
			}
			if(enemyTeamDistanceNew > enemyTeamDistance) {
				Act offensiveAct = new Act();
				offensiveAct.setSelectedTile(thisTileNew);
				offensiveAct.setMovingTile(moveTile); // no attack selected yet (select random one for acts that fit this move)
				offensiveActs.add(offensiveAct);
			}
			
			currentAdjacency = this.calculateAdjacencyBonus(usedGrid);
			if(currentAdjacency < bestAdjacency) { // or just below a threshold
				bestAdjacency = currentAdjacency;
				bestAdjacencyTile = moveTile;
				if(bestAdjacencyTile == null) {
					bestAdjacencyTile = usedGrid.getTile(this.getLocation());
				}
			}
			
			usedGrid = new Grid(Arborea.grid);
		}
		
		// select best move, random if no clear favorite
		Random randomGenerator = new Random();
		int index;
		if(offensive) {
			if(!oneHPNeighbourActs.isEmpty()) {
				index = randomGenerator.nextInt(oneHPNeighbourActs.size());
				bestAct = oneHPNeighbourActs.get(index);
			} else if(!offensiveActs.isEmpty()) {
					index = randomGenerator.nextInt(offensiveActs.size());
					bestAct = offensiveActs.get(index);
			} else {
				Act idle = new Act();
				idle.setSelectedTile(thisTile);
				bestAct = idle;
			}
		} else if(!defensiveActs.isEmpty()){
			index = randomGenerator.nextInt(defensiveActs.size());
			bestAct = defensiveActs.get(index);
		} else {
			Act idle = new Act();
			idle.setSelectedTile(thisTile);
			bestAct = idle;
		}
		return bestAct;
	}
	
	// calculate every single move possible by a figure
	ArrayList<Act> getAllPossibleActs() {
		Grid gridBeforeMove = new Grid(Arborea.grid);
		
		ArrayList<Act> allPossibleActs = new ArrayList<Act>();
		ArrayList<Tile> moveableTiles = this.getAllMoveableTiles(gridBeforeMove);
		ArrayList<Tile> attackableTiles = this.getAllAttackableTiles(gridBeforeMove);
		Act currentAct = new Act();
		currentAct.setSelectedTile(gridBeforeMove.getTile(this.getLocation()));
		allPossibleActs.add(currentAct);
		Grid usedGrid = new Grid(gridBeforeMove);
		
		for(Tile attackableTile : attackableTiles) {
			currentAct = new Act();
			currentAct.setSelectedTile(gridBeforeMove.getTile(this.getLocation()));
			currentAct.setAttackTileBefore(attackableTile);
			allPossibleActs.add(currentAct);
			usedGrid = new Grid(gridBeforeMove);
		}
		for(Tile attackableTile : attackableTiles) {
			currentAct = new Act();
			currentAct.setSelectedTile(gridBeforeMove.getTile(this.getLocation()));
			currentAct.setAttackTileBefore(attackableTile);
			for(Tile moveableTile : moveableTiles) {
				currentAct.setMovingTile(moveableTile);
				allPossibleActs.add(currentAct);
				usedGrid = new Grid(gridBeforeMove);
			}
		}
		for(Tile moveableTile : moveableTiles) {
			this.move(usedGrid, moveableTile);
			currentAct = new Act();
			currentAct.setSelectedTile(gridBeforeMove.getTile(this.getLocation()));
			currentAct.setMovingTile(moveableTile);
			attackableTiles = this.getAllAttackableTiles(usedGrid);
			usedGrid = new Grid(gridBeforeMove);
			for(Tile attackableTile : attackableTiles) {
				currentAct.setAttackTileAfter(attackableTile);
				allPossibleActs.add(currentAct);
			}
			usedGrid = new Grid(gridBeforeMove);
		}
		return allPossibleActs;
	}
	
	// get all tiles a figure can move to
	ArrayList<Tile> getAllMoveableTiles(Grid grid) {
		ArrayList<Tile> neighboursNotNull = new ArrayList<Tile>();
		ArrayList<Tile> neighboursMoveable = new ArrayList<Tile>();
		Tile thisTile = grid.getTile(this.getLocation());
		Tile[] neighbours = thisTile.neighbours;
		for (Tile currentNeighbourTile : neighbours) {
			if(currentNeighbourTile != null) {
				neighboursNotNull.add(currentNeighbourTile);
			}
		}		
		
		for (Tile currentNeighbourTileNotNull : neighboursNotNull) {
			if(!currentNeighbourTileNotNull.hasFigure())
				neighboursMoveable.add(currentNeighbourTileNotNull);
		}
		return neighboursMoveable;
	}
	
	// get all enemy figures surrounding this figure
	ArrayList<Tile> getAllAttackableTiles(Grid grid) {
		ArrayList<Tile> neighboursNotNull = new ArrayList<Tile>();
		ArrayList<Tile> neighboursAttackable = new ArrayList<Tile>();
		Tile thisTile = grid.getTile(this.getLocation());
		Tile[] neighbours = thisTile.neighbours;
		
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
		return neighboursAttackable;
	}
	
    // allies give adjaceny bonus, enemies do the opposite
    int calculateAdjacencyBonus(Grid grid) {
        int bonus = 0;
        Tile thisTile = grid.getTile(this.location);
        
        for(Tile neighbour : thisTile.neighbours) {
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
       
    // calculate chance to hit a character
    static private double calculateChance(int weaponSkills, int weaponSkillsAttacked) {
		return 1/(1+Math.exp(-0.4*(weaponSkills-weaponSkillsAttacked)));
	}
	
    // removes a figures from the team and grid
	private void removeFromField(Grid grid, Figure attacked) {
	    Tile deadTile = grid.getTile(attacked.location);
	    deadTile.setFigure(null);
		grid.removeFromTeam(attacked.getTeam(), attacked);
	}

	// sets Euclidean coordinate of the tile on which a character stands
	void setLocation(Point location) {
		this.location = location;
	}
	
	// gets that tile
	Point getLocation() {
		return location;
	}
	
	// returns the index of the type of this character
	int returnType() {
        return type;
    }
	
	// returns to what team this character belongs
	boolean getTeam() {
		return teamIsOrcs;
	}
	
	// sets whether a character has a move left
	void setMoved(boolean moved) {
		hasMoved = moved;
	}
	
	// sets whether a character has attacked
	void setAttacked(boolean attacked) {
		hasAttacked = attacked;
	}
	
	// returns whether or not a character can still move
	boolean hasMovesLeft() {
		return (!hasMoved);
	}
	
	// returns whether or not a character can still attack
	boolean hasAttacksLeft() {
		return (!hasAttacked);
	}
	
	// returns the hit points of the character
	int getHitpoints() {
		return this.hit;
	}
	
	// returns the initial hit points of the character
	int getStartHitpoints() {
		return this.startHitpoints;
	}
}