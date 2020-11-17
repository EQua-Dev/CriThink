package com.example.onlinequizexpo

class Board {


    companion object {
        const val PLAYER = "O"
        const val COMPUTER = "X"
    }
    //    we will then create the skeleton frame model of the board ...that in the mainActivity is for the UI representation
    val board = Array(3){ arrayOfNulls<String>(3)}

    //    we create a val to store the 3 criteria /condition that the game is over
    val isGameOver : Boolean
        get() = hasComputerWon() || hasPlayerWon() || availableCells.isEmpty()

    //    we create a function to check the possibilties and conditions for the player to have won
    fun hasComputerWon() : Boolean{
//            we check for alike moves of the computer along the rows
        if (board[0][0] == board[1][1] &&
                board[0][0] == board[2][2] &&
                board[0][0] == COMPUTER ||
                board[0][2] == board[1][1] &&
                board[0][2] == board[2][0] &&
                board[0][2] == COMPUTER){
            return true
        }

        for (i in board.indices){
//            we check for alike moves of the computer along the rows and columns
            if (
                    board[i][0] == board[i][1] &&
                    board[i][0] == board[i][2] &&
                    board[i][0] == COMPUTER ||
                    board[0][i] == board[1][i] &&
                    board[0][i] == board[2][i] &&
                    board[0][i] == COMPUTER){
                return true
            }

        }
//        else...
        return false
    }

    //    we create a function to check the possibilties and conditions for the player to have won
    fun hasPlayerWon() : Boolean{
//            we check for alike moves of the player along the rows
        if (board[0][0] == board[1][1] &&
                board[0][0] == board[2][2] &&
                board[0][0] == PLAYER ||
                board[0][2] == board[1][1] &&
                board[0][2] == board[2][0] &&
                board[0][2] == PLAYER){
            return true
        }

        for (i in board.indices){
//            we check for alike moves of the player along the rows and columns
            if (
                    board[i][0] == board[i][1] &&
                    board[i][0] == board[i][2] &&
                    board[i][0] == PLAYER ||
                    board[0][i] == board[1][i] &&
                    board[0][i] == board[2][i] &&
                    board[0][i] == PLAYER){
                return true
            }

        }
//        else...
        return false
    }

    var computersMove : Cell? = null
    fun minimax(depth: Int, player: String): Int{
        if (hasComputerWon())return  +1
        if (hasPlayerWon())return -1

        if (availableCells.isEmpty())return 0

        var min = Integer.MAX_VALUE
        var max = Integer.MIN_VALUE

        for (i in availableCells.indices){

            val cell = availableCells[i]

            if (player == COMPUTER){

                placeMove(cell, COMPUTER)
                val currentScore = minimax(depth +1, PLAYER)

                max = Math.max(currentScore, max)

//                if the next available cell adds 1,2 or 3 to its current own position (ie. X or O) it moves to that cell and then wins or increases its chances
                if (currentScore >=0){
                    if (depth == 0) computersMove = cell
                }
                if (currentScore == 1){
                    board[cell.i][cell.j] = ""
                    break
                }

//                if the cell is the last cell, it moves to that cell and the game becomes tied
                if (i== availableCells.size -1 && max <0){
                    if (depth == 0) computersMove = cell
                }

            }else if (player == PLAYER){
                placeMove(cell, PLAYER)
                val currentScore = minimax(depth +1, COMPUTER)
                min = Math.min(currentScore, min)

                if (min == -1){
                    board[cell.i][cell.j] = ""
                    break
                }
            }
            board[cell.i][cell.j] = ""
        }
        return if (player == COMPUTER) max else min
    }

    //    we get the available empty playable cells on the board
    val availableCells : List<Cell>
        get(){
//        we create a var List of the cells
            val cells = mutableListOf<Cell>()
//        we then run a double loop through all the cells to find the empty ones...
            for (i in board.indices){
                for (j in board.indices){
//                ...and populate the empty ones to the 'cells' var
                    if (board [i][j].isNullOrEmpty()){
                        cells.add(Cell(i,j))
                    }
                }
            }
            return cells
        }



    //    when this function is called, we will pass the cell in which the move is to be made as well as the player string to make the move
    fun placeMove(cell: Cell, player: String){
        board[cell.i][cell.j] = player
//    above, we have stated that the player's move is to be placed on cell i or j

    }

}