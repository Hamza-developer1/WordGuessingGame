import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hangman implements Serializable {
    public StringBuilder guessWord;
    public String answer;
    public int attempts;

    public static String[] category1 = {"black", "blue", "white", "amber", "violet", "red"};
    public static String[] category2 = {"peach", "mango", "fig", "lemon", "melon", "lime"};
    public static String[] category3 = {"mexico", "belgium", "nepal", "egypt", "vietnam", "uzbekistan"};

    // Lists to store used words for each category
    private static List<String> usedWordsCategory1 = new ArrayList<>();
    private static List<String> usedWordsCategory2 = new ArrayList<>();
    private static List<String> usedWordsCategory3 = new ArrayList<>();

    public Hangman(int categoryIndex) {
        //categoryAttempts = 3;
        attempts = 6;
        guessWord = new StringBuilder();

        String[] selectedCategory;
        List<String> usedWords;

        switch (categoryIndex) {
            case 1:
                selectedCategory = category1;
                usedWords = usedWordsCategory1;
                break;
            case 2:
                selectedCategory = category2;
                usedWords = usedWordsCategory2;
                break;
            case 3:
                selectedCategory = category3;
                usedWords = usedWordsCategory3;
                break;
            default:
                throw new IllegalArgumentException("Invalid category index");
        }

        // Get a random word from the category that hasn't been used before
        do {
            Random random = new Random();
            int randomIndex = random.nextInt(selectedCategory.length);
            answer = selectedCategory[randomIndex];
        } while (usedWords.contains(answer));

        usedWords.add(answer);

        for (int i = 0; i < answer.length(); i++) {
            guessWord.append("*");
        }
    }
    public void restartGame(int categoryIndex) {
        attempts = 6;
        guessWord = new StringBuilder();

        String[] selectedCategory;
        List<String> usedWords;

        switch (categoryIndex) {
            case 1:
                selectedCategory = category1;
                usedWords = usedWordsCategory1;
                break;
            case 2:
                selectedCategory = category2;
                usedWords = usedWordsCategory2;
                break;
            case 3:
                selectedCategory = category3;
                usedWords = usedWordsCategory3;
                break;
            default:
                throw new IllegalArgumentException("Invalid category index");
        }

        // Get a random word from the category that hasn't been used before
        do {
            Random random = new Random();
            int randomIndex = random.nextInt(selectedCategory.length);
            answer = selectedCategory[randomIndex];
        } while (usedWords.contains(answer));

        usedWords.add(answer);

        for (int i = 0; i < answer.length(); i++) {
            guessWord.append("*");
        }
    }

    public void userGuess(char guess) {
        boolean correctGuess = false;
        for (int i = 0; i < answer.length(); i++) {
            if (guess == answer.charAt(i)) {
                guessWord.setCharAt(i, guess);
                correctGuess = true;
            }
        }
        if (!correctGuess) {
            attempts--;
        }
    }
    public String serverResponse(char guess) {
        int index = -1; // Initialize index before the loop
        boolean correctGuess = false;

        for (int i = 0; i < answer.length(); i++) {
            if (guess == answer.charAt(i)) {
                correctGuess = true;
                index = i; // Update index when a correct guess is found
            }
        }

        if (correctGuess) {
            return "Letter " + guess + " is at index " + index;
        } else {
            return "Letter " + guess + " is not in the word";
        }
    }


    public String getEncodedWord(){
        return guessWord.toString();
    }

    public int getAttempts(){
        return attempts;
    }

    public String getAnswer(){
        return answer;
    }

    public boolean isGameWon() {
        return guessWord.toString().equals(answer);
    }

    public boolean isGameOver() {
        return attempts <= 0;
    }

}