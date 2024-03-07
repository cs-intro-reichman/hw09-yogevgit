import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
        
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
        char chr;
        StringBuilder strb = new StringBuilder();
        In in = new In(fileName);
        for (int i = 0; i < windowLength; i++) 
        {
            strb.append(in.readChar());
        }
        while (!in.isEmpty()) 
        {
            chr = in.readChar();
            List newlist = CharDataMap.get(strb.toString());
            if (newlist == null)
            {
                newlist = new List();
                CharDataMap.put(strb.toString(),newlist);
            }
                newlist.update(chr);
                strb.append(chr);
                strb = new StringBuilder(strb.substring(1));
        }
        for (List newlist : CharDataMap.values()) {
            calculateProbabilities(newlist);
            
        }
        }

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public  void calculateProbabilities(List probs) {
		int numberOfChars = 0;
        for (int i = 0; i < probs.getSize(); i++){
            numberOfChars =+ probs.get(i).count;
        }

        double cpProbability = 0;
        for (int i = 0; i < probs.getSize(); i++) {
            probs.get(i).p = ((double) probs.get(i).count) / numberOfChars;
            probs.get(i).cp = cpProbability + probs.get(i).p;
            cpProbability = probs.get(i).cp;
        }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		char randomChar;
        double randomDouble = randomGenerator.nextDouble();

        for (int i = 0; i < probs.getSize(); i++){
            if (probs.get(i).cp > randomDouble) {
                randomChar = probs.get(i).chr;
                return randomChar;
            }
        }
        return 'a';
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
        if(initialText.length() < windowLength){
            return initialText;
        }
        StringBuilder window = new StringBuilder(initialText.substring(initialText.length() - windowLength));
        StringBuilder generatedText = window;

        while (generatedText.length() < (textLength + windowLength)){
            List currentList = CharDataMap.get(window.toString());
            if (currentList == null){
                break;
            }
            generatedText.append(getRandomChar(currentList));
            window = new StringBuilder(generatedText.substring(generatedText.length() - windowLength));
        }

        return generatedText.toString();
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}
