/* ----------------------------------------------------------
 Jelmer Alphenaar 10655751 & Joseph Weel 10321624 - Assignment3
---------------------------------------------------------- */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
//import java.util.Random;
import java.lang.Math;
import java.awt.Point;
import java.awt.image.BufferedImage;

abstract class Figure {
    
    // indexes of types
    final static byte TYPE_NONE = 0, TYPE_SWORD = 1, TYPE_GENERAL = 2, TYPE_GOBLIN = 3, TYPE_ORC = 4;

	static final BufferedImage iconAttack = ArtManager.iconAttack;
	static final BufferedImage iconMove = ArtManager.iconMove;
	static final BufferedImage iconHealthbar = ArtManager.iconHealthbar;
	static final BufferedImage iconHealthbarGreen = ArtManager.iconHealthbarGreen;

	static final int AI_RANDOM = 0;
	static final int AI_TRUE = 1;
	
	boolean facingRight = true;
	
    int startHitpoints, hit, weapon, type;
    int index;
    Point location;
    boolean teamIsOrcs;
    private boolean hasMoved = true;
    private boolean hasAttacked = true;
    
    Thread animationThread;
    Animator animator;
    
    ArrayList<BufferedImage> standSprites;
    ArrayList<BufferedImage> standSpritesL;
    
    void setUpSprites(){
    	setUpStandSprites();
    }
    abstract void setUpStandSprites();
    
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

    	if (destinationTile.getLocation().x < this.getLocation().x)
    		this.facingRight = false;
    	if (destinationTile.getLocation().x > this.getLocation().x)
    		this.facingRight = true;
    	
    	grid.getTile(this.getLocation()).setFigure(null);
       	destinationTile.setFigure(this);
       	setLocation(destinationTile.getLocation());
    }
    
    //not used now
    //TODO
    boolean canMove(Grid grid, Tile destinationTile){
        if(destinationTile != null) {
            Tile currentTile = grid.getTile(location);
        	return !destinationTile.hasFigure() && Arrays.asList(currentTile.getNeighbours()).contains(destinationTile);
        } else
            return false;
    }
    
    //not optimal method, but good enough
    public double lengthToMiddleOfTeam(Tile thisTile, Team thisTeam) {
    	double[] middleOfTeam = thisTeam.getAverageMiddlePointOfTeam(); //thisTeam = Arborea.grid.orcs or humans
    	Point location = thisTile.getLocation();
    	return (Math.abs((location.getX() - middleOfTeam[0])) + Math.abs(location.getY() - middleOfTeam[1]))
    			- (Math.abs(location.getX() - middleOfTeam[0]) - Math.abs(location.getY() - middleOfTeam[1])/2); 
    	//return Math.sqrt(Math.pow((location.getX() - middleOfTeam[0]), 2) + Math.pow((location.getY() - middleOfTeam[1]), 2));
    }
    
	public void attack(Grid grid, Figure attacked, boolean print) {
		// if attacked = null, figure is already dead
		if(attacked == null) return;
    	 double hitChance = calculateChance(this.weapon+this.calculateAdjacencyBonus(grid), attacked.weapon+attacked.calculateAdjacencyBonus(grid));
    	 boolean imHitCaptain = Math.random() < hitChance;
    	 if(imHitCaptain) {
    		 attacked.hit--;
    		 if(attacked.hit <= 0) {
    			 removeFromField(grid, attacked);
    		 }
    	 } if(print) {
    		 System.out.println("This unit has " + attacked.hit + " HP left.");
    	 }
    }
	
	public boolean isNextMoveOffensive(Grid grid, double threshold) {
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
	
	public Act calculateBestMove(ArrayList<Act> allActs, Grid gridBefore, boolean offensive) {
		Grid usedGrid = new Grid(gridBefore);
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
			ownTeamDistance = lengthToMiddleOfTeam(thisTile, gridBefore.orcs);
			enemyTeamDistance = lengthToMiddleOfTeam(thisTile, gridBefore.humans);
		} else {
			ownTeamDistance = lengthToMiddleOfTeam(thisTile, gridBefore.orcs);
			enemyTeamDistance = lengthToMiddleOfTeam(thisTile, gridBefore.humans);
		}
		
		for (Act currentAct : allActs) {
			currentAct.printAct();
			Tile attackTileBefore = currentAct.getAttackTileBefore();
			if(attackTileBefore != null) {
				this.attack(usedGrid, attackTileBefore.getFigure(), false);
			}
			Tile moveTile = currentAct.getMovingTile();
			if(moveTile != null) {
				this.move(usedGrid, moveTile);
				Tile[] neighbours = moveTile.getNeighbours();
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
				this.attack(usedGrid, attackTileAfter.getFigure(), false);
			}
			
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
					bestAdjacencyTile = gridBefore.getTile(this.getLocation());
				}
			}
			
			usedGrid = new Grid(gridBefore);//new Grid(gridBefore); //check if works without copy constructor
		}
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
	
	public ArrayList<Act> getAllPossibleActs(Grid gridBeforeMove, Grid usedGrid) {
		ArrayList<Act> allPossibleActs = new ArrayList<Act>();
		ArrayList<Tile> moveableTiles = this.getAllMoveableTiles(gridBeforeMove);
		ArrayList<Tile> attackableTiles = this.getAllAttackableTiles(gridBeforeMove);
		Act currentAct = new Act();
		currentAct.setSelectedTile(gridBeforeMove.getTile(this.getLocation()));
		allPossibleActs.add(currentAct);
		
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
			this.move(usedGrid, gridBeforeMove.getTile(this.getLocation())); // is this necessary?
			usedGrid = new Grid(gridBeforeMove);
		}
		return allPossibleActs;
	}
	
	public ArrayList<Tile> getAllMoveableTiles(Grid grid) {
		ArrayList<Tile> neighboursNotNull = new ArrayList<Tile>();
		ArrayList<Tile> neighboursMoveable = new ArrayList<Tile>();
		Tile thisTile = grid.getTile(this.getLocation());
		Tile[] neighbours = thisTile.getNeighbours();
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
	
	public ArrayList<Tile> getAllAttackableTiles(Grid grid) {
		ArrayList<Tile> neighboursNotNull = new ArrayList<Tile>();
		ArrayList<Tile> neighboursAttackable = new ArrayList<Tile>();
		Tile thisTile = grid.getTile(this.getLocation());
		Tile[] neighbours = thisTile.getNeighbours();
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
		grid.removeFromTeam(attacked.getTeam(), attacked);
		System.out.println(grid.getTeam(false));
	    /*if(attacked.getTeam()) {
	    	grid.orcs.update(attacked.getIndex(), null);
	    } else {
	    	grid.humans.update(attacked.getIndex(), null);
	    }*/ //niet nodig want figure in team is zelfde als figure op board
	}
	/*
	public void setIndex(int index) {
		this.index = index;
	}
	*/
	/*public int getIndex() {
		return this.index;
	}*/
	
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
	

}
