import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class HangmanTest {

	@Test
	public void testCorrectGuess() {
		Hangman hangman = new Hangman(1);
		char correctGuess = hangman.answer.charAt(0);
		hangman.userGuess(correctGuess);
		System.out.println(correctGuess);
		assertTrue(hangman.guessWord.toString().contains(String.valueOf(correctGuess)));
	}

	@Test
	public void testIncorrectGuess() {
		Hangman hangman = new Hangman(1);
		char incorrectGuess = 'z';
		hangman.userGuess(incorrectGuess);
		assertEquals(5, hangman.attempts);
	}

	@Test
	public void testWinningGame() {
		Hangman hangman = new Hangman(1);
		for (char letter : hangman.answer.toCharArray()) {
			hangman.userGuess(letter);
		}
		assertTrue(hangman.isGameWon());
	}

	@Test
	public void testLosingGame() {
		Hangman hangman = new Hangman(1);

		for (int i = 0; i < 6; i++) {
			hangman.userGuess('z');
		}

		assertFalse(hangman.isGameWon());
		assertTrue(hangman.isGameOver());
	}



	@Test
	public void testCategoryChange() {
		Hangman hangman = new Hangman(1);
		String initialWord = hangman.answer;
		hangman = new Hangman(2);
		assertNotEquals(initialWord, hangman.answer);
	}

	@Test
	public void testInvalidCategoryIndex() {
		assertThrows(IllegalArgumentException.class, () -> new Hangman(0));
	}

	@Test
	public void testWordRemovedFromCategory() {
		Hangman hangman = new Hangman(1);
		String word = hangman.answer;
		hangman = new Hangman(1);
		assertFalse(hangman.answer.equals(word));
	}

	@Test
	public void testUsedWordNotReused() {
		Hangman hangman = new Hangman(1);
		String word = hangman.answer;
		hangman = new Hangman(1);
		assertNotEquals(word, hangman.answer);
	}

	@Test
	public void testMultipleCategories() {
		Hangman hangman1 = new Hangman(1);
		Hangman hangman2 = new Hangman(2);
		assertNotEquals(hangman1.answer, hangman2.answer);
	}

	@Test
	public void testCategory2WinningGame() {
		Hangman hangman = new Hangman(2);
		for (char letter : hangman.answer.toCharArray()) {
			hangman.userGuess(letter);
		}
		assertTrue(hangman.isGameWon());
	}
}