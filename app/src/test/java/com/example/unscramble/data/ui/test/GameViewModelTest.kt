package com.example.unscramble.data.ui.test

import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.getUnscrambledWord
import com.example.unscramble.ui.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertNotEquals

class GameViewModelTest
{
    private val viewModel = GameViewModel()

    //asegura que la puntuacion se actualize correctamente
    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset()
    {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        // Assert que el metodo checkUserGuess() actualiza isGuessedWordWrong correctamente.
        assertFalse(currentGameUiState.isGuessedWordWrong)
        // Assert que la puntuacion se actualiza correctamente.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }
    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }

    //confirma que la puntuacion no cambie al introducir una palabra incorrectamente
    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        // palabra incorrecta de entrada
        val incorrectPlayerWord = "and"

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()
        val currentGameUiState = viewModel.uiState.value
        // Assert que la puntuacion no ha cambiado
        assertEquals(0, currentGameUiState.score)
        // Assert que el metodo checkUserGuess() actualiza isGuessedWordWrong correctamente
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    // asegura que todos los elementos UI inicien en sus valores predeterminados
    @Test
    fun gameViewModel_Initialization_FirstWordLoaded()
    {
        val gameUiState = viewModel.uiState.value
        val unScrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        // assert de que la palabra actual esta desordenada
        assertNotEquals(unScrambledWord, gameUiState.currentScrambledWord)
        // assert que el recuento de palabras actual este en 1
        assertTrue(gameUiState.currentWordCount == 1)
        // assert de que la puntuacion inicie en 0
        assertTrue(gameUiState.score == 0)
        //assert de que la palabra incorrecta adivinada es falsa
        assertFalse(gameUiState.isGuessedWordWrong)
        // assert de que el juego no ha terminado
        assertFalse(gameUiState.isGameOver)
    }

    //simula una partida entera donde se adivinan todas las palabras
    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly()
    {
        var expectedScore = 0
        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        repeat(MAX_NO_OF_WORDS) {
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            //assert que depues de cada respuesta correcta, se actulize la puntuacion correctamente
            assertEquals(expectedScore, currentGameUiState.score)
        }
        //assert que después de responder todas las preguntas, el recuento de palabras actual este actualizado
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        //assert que después de responder 10 preguntas, el juego termina
        assertTrue(currentGameUiState.isGameOver)
    }
}