/*
 * Deborah Lambregts
 * 11318643
 *
 * Problem set 4
 * 02-12-2016
 *
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;
import java.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
    
    public static void main(String[] args) {
        new Yahtzee().start(args);
    }
    
    public void run() {
        IODialog dialog = getDialog();
        nPlayers = dialog.readInt("Enter number of players");
        playerNames = new String[nPlayers];
        for (int i = 1; i <= nPlayers; i++) {
            playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
        }
        display = new YahtzeeDisplay(getGCanvas(), playerNames);
        playGame();
    }




    private void playGame() {
        scores = new int[N_CATEGORIES][nPlayers]; // Initialize the scoreboards

		// Turns played before calculating the endscore and winner, cannot be 
		// more than the amount of score categories.
		for(int turns = 0; turns < N_SCORING_CATEGORIES; turns++) {
			// Every player his own loops
			for(int playerNr = 1; playerNr <= nPlayers; playerNr++) { // bc start at 1
				firstRoll(playerNr);
				rerollOne(playerNr);
				rerollTwo(playerNr);
				selectCategory(playerNr);
			}
	
		}
	//upperScore();		not working
	//lowerScore();		not working
	//upperBonus();		not working
	endScore();			// not working
	theWinnerIs();		// not working
    }




	// To initiate the first roll of the player. Highlightes the name of the 
	// player. For every dice there must be a value. 
	private void firstRoll(int player) {
		display.printMessage(playerNames[player - 1] + 
				"'s turn! Click \"Roll Dice\" button to roll the dice.");
		display.waitForPlayerToClickRoll(player);
		for(int i = 0; i < N_DICE; i++){
			rolledDice[i] = rgen.nextInt(1, 6);	// Random 1-6, for each dice	
		}
		display.displayDice(rolledDice); 			// Display new rolled dice
	}



	// To initiate the second roll of the player. Message, select dice
	// click on roll again.
	private void rerollOne(int player) {
		display.printMessage("Select the dice you wish to re-roll " +
								"and click \"Roll Again\".");
		display.waitForPlayerToSelectDice();

		for( int i = 0; i < N_DICE; i++){ 			// For all the dice
			if(display.isDieSelected(i)) {
				rolledDice[i] = rgen.nextInt(1, 6);	// Create new value
				
			}
		}
		display.displayDice(rolledDice);
	}



	// The third roll. The same as the second roll.
	private void rerollTwo(int player) {
		display.printMessage("Select the dice you wish to re-roll " +
								"and click \"Roll Again\".");
		display.waitForPlayerToSelectDice();

		for(int i = 0; i <N_DICE; i++){
			if(display.isDieSelected(i)){
				rolledDice[i] = rgen.nextInt(1,6);
			}
		}
		display.displayDice(rolledDice);
	}
 
//------------------------------------------------------------------------


	// If the clicked category is legit and not already filled, the method to
	// calculate the score is called.
	private void selectCategory(int playerNr) {
		display.printMessage("Select a category for this roll.");

		while(true) {
			int chosenCategory = display.waitForPlayerToSelectCategory(); 
			if(myCheckCategory(playerNr, chosenCategory)){
				calculateScore(playerNr, chosenCategory);
				break;
			}
	
			display.printMessage("Your already used this category. " + 
									"Please select an other category."); 
		}
	}



	// Checks if the chosen category is empty and legit to assign the score to.
	// Value 0, which means there is not already another score in it = empty.
	// Buggy: like this a  category WITH 0 can be rechosen, and it shouldnt.
	// I didnt manage to fix this bug.
	private boolean myCheckCategory(int player, int category){
		if(category != UPPER_BONUS && category != TOTAL && 
			category != UPPER_SCORE && category != LOWER_SCORE &&
			scores[category-1][player-1] == 0) { 
				return true;	
		}
		return false;
	}



	// Calculate the scores for the different categories.
	private void calculateScore(int player, int category) {
		int score = 0;

		// For the top half categories: 1 to 6.
		// Check each dice (forloop) and if the dice has the same number as the
		// category, then add the value to score.
		if(category >= ONES && category <= SIXES) {	
			for(int i = 0; i < N_DICE; i++) {
				if(rolledDice[i] == category){ 
					score += category;
				}
			}

		// Calculate the scores for the bottom half categories.

		// Three of a kind.
		// Add up the values of all the dices.
		} else if(category == THREE_OF_A_KIND) {
			if(specialScore(category)){
				for(int i = 0; i < N_DICE; i++){	
					score += rolledDice[i];
				}
			} else{
				score = 0;
			}

		// Four of a kind
		// Add up the values of all the dices. (Same as three of a kind).
		} else if(category == FOUR_OF_A_KIND  ) {
			if(specialScore(category)){
				for(int i = 0; i < N_DICE; i++){	
					score += rolledDice[i];
				}
			} else{
				score = 0;
			}

		// Full house
		// If it meets the requirements, assign score 25.
		// If not, assign score 0.
		} else if(category == FULL_HOUSE) {
			if(specialScore(category)) {
				score = 25;
			} else {
				score = 0;
			}

		// Small straight
		// If it meets the requirements, assign score 30.
		// If not, assign score 0.
		} else if(category == SMALL_STRAIGHT) {
			if(specialScore(category)) {
				score = 30;
			} else {
				score = 0;
			}

		// Large straight
		// If it meets the requirements, assign score 40.
		// If not, assign score 0.
		} else if(category == LARGE_STRAIGHT) {
			if(specialScore(category)) {
				score = 40;
			} else {
				score = 0;
			}
		
		// Yahtzee
		// If it meets the requirements, assign score 50.
		// If not, assign score 0.
		} else if(category == YAHTZEE) {
			if(specialScore(category)) {
				score = 50;
			} else {
				score = 0;
			}

		// Chance
		// Does not have requirements, just add the values of all the dice.
		} else if(category == CHANCE) {
			for(int i = 0; i < N_DICE; i++){	
				score += rolledDice[i];	
			}
		}		
		
		// Update the category in the display with the just calculated score.
		// xx Update the 2d array
		display.updateScorecard(category, player, score);
		scores[category-1][player-1] = score;	

		// Update the total score in the display with the recent score.
		newTotal += score;
		display.updateScorecard(TOTAL, player, newTotal);
	}

//-----------------------------------------------------------------------

	// Calculate and display the score of the upper part of the scorecard.
	private int upperScore(int player) {
		int upperHalf = 0;
		for(int i = 0; i< SIXES; i++) {
			upperHalf += scores[i][player]; 
		}
		
		display.updateScorecard(UPPER_SCORE, player +1, upperHalf);
		return upperHalf;
	}


	// Calculate and display the score of de lower part of the scorecard.
	private int lowerScore(int player) {
		int lowerHalf = 0;
		for(int i = UPPER_BONUS -1; i < LOWER_SCORE; i++) {
			lowerHalf += scores[i][player]; 
		}
		display.updateScorecard(LOWER_SCORE, player +1, lowerHalf);
		return lowerHalf;
	}
	

	// Calculate and display 35 if the bonus is awarded, 0 if not.
	private void upperBonus(int player) {
		if(scores[UPPER_SCORE - 1][player] >= 63) {
			scores[UPPER_BONUS - 1][player] = 35;			
			display.updateScorecard(UPPER_BONUS, player+1, 35);
		} else{
			scores[UPPER_BONUS - 1][player] = 0;		
			display.updateScorecard(UPPER_BONUS, player +1, 0);
		}		
	}


	// Calculate and update total for every player
	private void endScore() {
		for(int playerNr = 0; playerNr < nPlayers; playerNr++) {
			int total = 0;
			total = upperScore(playerNr) + lowerScore(playerNr);
			scores[TOTAL - 1][playerNr] = scores[UPPER_SCORE -1][playerNr] + 
										scores[UPPER_BONUS -1][playerNr] +
										scores[LOWER_SCORE -1][playerNr];
			display.updateScorecard(TOTAL, playerNr +1, scores[TOTAL - 1][playerNr]); 
		}
	}


	// Calculate who is the winner (highest total score).
	// Compare the current score with the highestScore, if it's higher, this 
	// score becomes the new highestScore. The player with the highestScore is 
	// the highestPlayer and thus the winner. Print it on the display.
	private void theWinnerIs(){
		int highestScore = 0; 
		int highestPlayer = 0;	
		for(highestPlayer = 0; highestPlayer < nPlayers; highestPlayer++) {
			if(scores[TOTAL-1][highestPlayer] > highestScore) { 
				highestScore = scores[TOTAL -1][highestPlayer];					
			}
		}
		display.printMessage("Congratulations, " + playerNames[highestPlayer] 
							+ ", you're the winner with a total score of " 
							+ highestScore + "!");
	}




//---------------------------------------------------------------------


	// I didnt manage to get it work using an Array, but I did using an 
	// ArrayList, though I know that since this list is static (unless we want
	// to use dice with more than 6 sides) it might be better to use an Array.

	// To check if the set of rolled dice is in the lower half, so we
	// know how to calculate its special score.
	private boolean specialScore(int chosenCategory) { 
	// One for each side of the dice (so 6 total, 1-6).
		ArrayList<Integer> one = new ArrayList<Integer>();
		ArrayList<Integer> two = new ArrayList<Integer>();
		ArrayList<Integer> three = new ArrayList<Integer>();
		ArrayList<Integer> four = new ArrayList<Integer>();
		ArrayList<Integer> five = new ArrayList<Integer>();
		ArrayList<Integer> six = new ArrayList<Integer>();		

	// Add an element to the list when the number on the dice matches with
	// the requirement, so we can use the size of the list (to count the times
	// a specific amount of eyes is shown on the dice) later on.
		for(int i = 0; i < N_DICE; i++){
			if(rolledDice[i] == 1) {
				one.add(1);
			} else if(rolledDice[i] == 2) {
				two.add(2);
			} else if(rolledDice[i] == 3) {
				three.add(3);
			} else if(rolledDice[i] == 4) {
				four.add(4);
			} else if(rolledDice[i] == 5) {
				five.add(5);
			} else if(rolledDice[i] == 6) {
				six.add(6);
			}
		}

		// Use the size of the arraylists to state the requirements for each
		// category.
		// Categories 1-6 do not need any, because they can always be chosen.
		
		// Three of a kind
		if(chosenCategory == THREE_OF_A_KIND) { //check this
			if(one.size() >=3 || two.size() >=3 || three.size() >=3 || 
						four.size() >=3 || five.size() >=3 || six.size() >=3) {
				return true;
			}

		// Four of a kind
		} else if(chosenCategory == FOUR_OF_A_KIND) {
			if(one.size() >=4 || two.size() >=4 || three.size() >=4 || 
					four.size() >=4 || five.size() >=4 || six.size() >=4) {
				return true;
			}

		// Full house. Same as checking for three of a kind and a two of a kind.
		} else if(chosenCategory == FULL_HOUSE) {
			if(one.size() ==3 || two.size() ==3 || three.size() ==3 || 
					four.size() ==3 || five.size() ==3 || six.size() ==3) {
				if(one.size() ==2 || two.size() ==2 || three.size() ==2 || 
						four.size() ==2 || five.size() ==2|| six.size() ==2) {
					return true;
				}
			}
		//?	return false;
		
		// Small straight
		} else if(chosenCategory == SMALL_STRAIGHT) {
			if(one.size() >=1 && two.size() >=1 && three.size() >=1 
					&& four.size() >=1) { 						// 1-2-3-4
				return true;	
			
			}else if(two.size() >=1 && three.size() >=1 && four.size() >=1 
					&& five.size() >=1){ 						// 2-3-4-5
				return true;
			
			}else if(three.size() >=1 && four.size() >=1 && five.size() >=1 
					&& (six.size() >=1)){ 						// 3-4-5-6
				return true;
			}

		// Large straight
		} else if(chosenCategory == LARGE_STRAIGHT) {
			if(one.size() >=1 && two.size() >=1 && three.size() >=1 
					&& four.size() >=1 && five.size() >= 1) { 	// 1-2-3-4-5
				return true;	
			
			}else if(two.size() >=1 && three.size() >=1 && four.size() >=1 
					&& five.size() >=1 && six.size() >=1) { 	// 2-3-4-5-6
				return true; 
			}

		// Yahtzee - all the dices, so use N_DICE instead of '5'
		}else if(chosenCategory == YAHTZEE) {
			if(one.size() == N_DICE || two.size() == N_DICE 
					|| three.size() == N_DICE || four.size() == N_DICE 
					|| five.size() == N_DICE || six.size() == N_DICE) {
				return true;
			}
		
		// Chance
		} else if(chosenCategory == CHANCE) {
			return true;
			}

		return false;	// If none of the above, then it does not have a special
						// score, so it must be one of the categories 1 - 6.
	} 
	
	
	

	
//---------------------------------------------------------------------
	// Private instance variables given
    private int nPlayers;
    private String[] playerNames;
    private YahtzeeDisplay display;
    private RandomGenerator rgen = new RandomGenerator();
	// Private instance variables by me
	private int[] rolledDice = new int[N_DICE];	// The values of the (last) roll
	private int[][] scores; 					// The scores per player
	int newTotal =0;							// For updating the total
												// 		while playing


}
   
