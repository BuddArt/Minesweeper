package minesweeper

import kotlin.random.Random

class Minesweeper (private val row: Int, private val point: Int) {
    private var myboard = Array(row) { Array(point) { '.' } } // Невидимое поле
    private var board = Array(row) { Array(point) { '.' } } //Видимое поле
    private var numberOfMines = 0
    private var x = 0
    private var y = 0
    var wrongMark = 0

    private var listOfHintNumber = listOf('1', '2', '3', '4', '5', '6', '7', '8')

    // Функция ввода пользовательского ввода количества мин
    private fun inputOfMines() {
        print("How many mines do you want on the field? ")
        numberOfMines = readln().toInt()
    }

    // Функция создания рандомного расположения мин в невидимом поле
    private fun createMines() {
        var countMines = 0
        val random = Random(2)
        while (countMines != numberOfMines) {
            val random1 = random.nextInt(0, row)
            val random2 = random.nextInt(0, point)
            if (myboard[random1][random2] != 'X' && random1 != y || random2 != x) {
                myboard[random1][random2] = 'X'
                countMines++
            }
        }
    }

    // Функция создания числовых подсказок количества мин вокруг ячейки
    private fun createHints() {
        for (y in 0 until row) {
            for (x in 0 until point) {
                if (myboard[y][x] != 'X') {
                    var nMines = 0                                                // number of mines next to cell
                    if (y > 0 && x > 0) {
                        when {
                            myboard[y - 1][x - 1] == 'X' -> nMines++      // cell up left
                        }
                    }
                    if (y > 0) {
                        when {
                            myboard[y - 1][x] == 'X' -> nMines++          // cell up
                        }
                    }
                    if (y > 0 && x < point - 1) {
                        when {
                            myboard[y - 1][x + 1] == 'X' -> nMines++      // cell up right
                        }
                    }
                    if (x > 0) {
                        when {
                            myboard[y][x - 1] == 'X' -> nMines++          // cell left
                        }
                    }
                    if (x < point - 1) {
                        when {
                            myboard[y][x + 1] == 'X' -> nMines++          // cell right
                        }
                    }
                    if (y < row - 1 && x > 0) {
                        when {
                            myboard[y + 1][x - 1] == 'X' -> nMines++      // cell bottom left
                        }
                    }
                    if (y < row - 1) {
                        when {
                            myboard[y + 1][x] == 'X' -> nMines++          // cell bottom
                        }
                    }
                    if (y < row - 1 && x < point - 1)
                        when {
                            myboard[y + 1][x + 1] == 'X' -> nMines++      // cell bottom right
                        }
                    if (nMines != 0) {
                        myboard[y][x] = Character.forDigit(nMines, 10)// place number, if it's not 0
                    }
                }
            }
        }
    }

    // Функция показа всех мин после проигрыша
    private fun showAllMines() {
        for (y in 0 until row) {
            for (x in 0 until point) {
                if (myboard[y][x] == 'X')
                    board[y][x] = 'X'
            }
        }
    }

    // Функция печати игрового поля
    private fun printBoard() {
        println(" |123456789|")
        println("—|—————————|")
        for (i in board.indices) println("${i+1}|"  + board[i].joinToString("") + "|")
        println("—|—————————|")
    }

    /* private fun printMyBoard() {
        println(" |123456789|")
        println("—|—————————|")
        for (i in myboard.indices) println("${i+1}|"  + myboard[i].joinToString("") + "|")
        println("—|—————————|")
    } */

    private fun floodFillUtil(x: Int, y: Int) {
        // Базовые проверки
        if (x < 0 || y < 0 || x >= row || y >= point) return
        if (myboard[x][y] == 'X' || myboard[x][y] == '/') return
        if (board[x][y] == '*' && myboard[x][y] == 'X') return

        // Добавление подсказки в окружающие ячейки
        if (listOfHintNumber.contains(myboard[x][y])) {
            board[x][y] = myboard[x][y]
            return
        }

        // Открытие ячейки с '*' в видимом поле и '.' в скрытом поле
        if (board[x][y] == '*' && myboard[x][y] == '.') {
            board[x][y] = '/'
            wrongMark--
            //return
        }

        // Открытие ячейки с '*' в видимом поле и подсказки в скрытом поле
        if (board[x][y] == '*' && listOfHintNumber.contains(myboard[x][y])) {
            board[x][y] = myboard[x][y]
            wrongMark--
            return
        }

        // Меняем '*' на '/'
        board[x][y] = '/'
        myboard[x][y] = '/'

        // Проверка ячеек рядом с открытой ячейкой
        floodFillUtil(x + 1, y)
        floodFillUtil(x + 1, y + 1)
        floodFillUtil(x - 1, y + 1)
        floodFillUtil(x - 1, y - 1)
        floodFillUtil( x, y + 1)
        floodFillUtil(x, y - 1)
        floodFillUtil(x + 1, y - 1)
        floodFillUtil(x - 1, y)
    }

    private fun floodFill(x: Int, y: Int) {
        val prevC = board[x][y]
        if (prevC == '/') return
        floodFillUtil(x, y)
    }

    fun playGame() {
        inputOfMines()
        printBoard()
        //printMyBoard()

        // Ввод ячейки и действия для следующего хода
        print("Set/unset mines marks or claim a cell as free: ")
        val (_x, _y, freeOrMine) = readln().split(' ')
        x = _x.toInt() - 1
        y = _y.toInt() - 1

        var correctMark = 0

        createMines()
        createHints()

        if (myboard[y][x] == '.' && freeOrMine == "free") {
            floodFill(y, x)
            printBoard()
        }
        if (listOfHintNumber.contains(myboard[y][x]) && freeOrMine == "free") {
            board[y][x] = myboard[y][x]
            printBoard()
        }
        if (freeOrMine == "mine") {
            board[y][x] = '*'
            wrongMark++
            printBoard()
        }

        // Цикл пока все мины не будут помечены и wrongMark = 0
        while (correctMark != numberOfMines || wrongMark != 0) {
            print("Set/unset mines marks or claim a cell as free: ")
            val (_x, _y, freeOrMine1) = readln().split(' ')
            x = _x.toInt() - 1
            y = _y.toInt() - 1

            if (myboard[y][x] == '.' && freeOrMine1 == "free") {
                floodFill(y, x)
                board[y][x] = '/'
                printBoard()

                // Отмечает ячейку как free
            } else if (listOfHintNumber.contains(myboard[y][x]) && freeOrMine1 == "free") {
                board[y][x] = myboard[y][x]
                printBoard()

                // Убирает правильную метку
            } else if (myboard[y][x] == 'X' && board[y][x] == '*' && freeOrMine1 == "mine") {
                board[y][x] = '.'
                correctMark--
                printBoard()

                // Ставит правильную метку
            } else if (myboard[y][x] == 'X' && freeOrMine1 == "mine") {
                board[y][x] = '*'
                correctMark++
                printBoard()

                // Убирает метку
            } else if (board[y][x] == '*' && freeOrMine1 == "mine") {
                board[y][x] = '.'
                wrongMark--
                printBoard()

                // Вскрывает неправильную метку и убирает ее
            } else if (board[y][x] == '*' && freeOrMine1 == "free" && myboard[y][x] == '.') {
                board[y][x] = '/'
                wrongMark--
                printBoard()

                // Вскрывает неправильную метку с числом и убирает ее
            } else if (listOfHintNumber.contains(myboard[y][x]) && freeOrMine1 == "free") {
                board[y][x] = myboard[y][x]
                wrongMark--
                printBoard()

                // Уже открытое число
            } else if (listOfHintNumber.contains(board[y][x])) {
                println("There is a number here!")

                // Помечает неправильную ячейку и добавляет ее
            } else if (freeOrMine1 == "mine") {
                board[y][x] = '*'
                wrongMark++
                printBoard()

                // Открывает мину и заканчивает игру!
            } else if (myboard[y][x] == 'X' && freeOrMine1 == "free") {
                showAllMines()
                printBoard()
                println("You stepped on a mine and failed!")
                break
            }

            var i = 0

            if (wrongMark == 0) {
                for (y in 0 until row) {
                    for (x in 0 until point) {
                        if (board[y][x] == '.') {
                            i++
                        }
                    }
                }
            }
            if (wrongMark == 0 && i == numberOfMines) {
                println("Congratulations! You found all the mines!")
                break
            }
        }

        if (correctMark == numberOfMines && wrongMark == 0) {
            println("Congratulations! You found all the mines!")
        }
    }
}

fun main() {
    val minesweeper = Minesweeper(9, 9)
    minesweeper.playGame()
}