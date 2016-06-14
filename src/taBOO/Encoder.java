package taBOO;

import java.util.HashMap;

public class Encoder {

	private HashMap<Integer, String> reConversionMap;
	private HashMap<String, Integer> conversionMap;// = new HashMap<String, Integer>();
	private int[][] mmlu;
	private int wordLength;
	
	//Constructor::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**
	 * Creates an Encoder object with default wordlength 5
	 */
	public Encoder() {
		this(5);
	}

	/**
	 * Creates an Encoder object with wordlength as specified by input parameter.
	 * @param l the length of the words
	 */
	public Encoder(int wordLength) {
		this.wordLength = wordLength;
		generate();
	}

	//Methods::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/**
	 * Factory method for generation of conversion maps and mismatch-table.
	 */
	private void generate() {
		this.conversionMap = new HashMap<String, Integer>();
		this.reConversionMap = new HashMap<Integer, String>();

		String[] bases = {"A", "C", "G", "T", "N"};
		int length = (int)(Math.pow(bases.length, this.wordLength));
		int[] indx = new int[this.wordLength];
		int maxBases = bases.length-1;

		for(int i = 0; i < length; i++) {
			StringBuilder tempWord = new StringBuilder();

			for(int j = 0; j < indx.length; j++) {
				tempWord.append(bases[indx[j]]);
			}

			conversionMap.put(tempWord.toString(), i);
			reConversionMap.put(i, tempWord.toString());

			for(int k = indx.length-1; k >= 0; k--) {
				if(indx[k] == maxBases) {
					indx[k] = 0;
				}
				else {
					indx[k]++;
					k = -1;
				}
			}
		}

		this.mmlu= new int[length][length];

		for(int i= 0; i < length; i++) {
			for(int j = 0; j < length; j++) {
				this.mmlu[i][j] = numbMismatch(reConversionMap.get(i), reConversionMap.get(j));
			}
		}
	}

	/**
	 * Method converts the input string into N-code. The length of the input string is 
	 * expected to be divisible by the wordlength of the Encoder object.<br><br>
	 * Example:<br>Given that the wordlength is 2:<br>"ACAA" --> {1, 0}
	 * @param s The string to be converted
	 * @return N-coded sequence as an int array.
	 * @throws EncoderException 
	 */
	public int[] encode(String s) throws EncoderException {
		if(s.length()%this.wordLength != 0) {
			throw new EncoderException("Length of input string (" + s.length() + ")" +
				 " not divisible with word length (" +  this.wordLength + ").");
		}
		
		int n = (s.length()/this.wordLength);
		int[] out = new int[n];

		for(int i = 0; i < n; i++) {
			String temp = s.substring(i*this.wordLength, (i+1)*this.wordLength);
			out[i] = this.conversionMap.get(temp);
		}
		return out;
	}

	/**
	 * This method compares two sequences and returns
	 * back the number of mismatches between them.
	 * CASE SENSITIVE!<br><br>Example:<br>numbMismatch("AA", "GA") --> 1
	 * @param s1 The first sequence
	 * @param s2 The second sequence
	 * @return The number of mismatches between the two sequences
	 */
	public static int numbMismatch (String s1, String s2){		
		// Initialize number of mistaches and set to zero.
		int n = 0;	
		// compare input strings and allow only stings of equal lenngths
		if (s1.length() == s2.length()) {
			/*  Go through each position of both strings and check for equality.
			 *  If characters are not equal, increment n, else move to next position. 
			 */
			for (int i=0; i<s1.length(); i++){	
				if(s1.charAt(i) == 'N' || s2.charAt(i) == 'N') {
					n++;
				}
				else {
					if (s1.charAt(i) != s2.charAt(i)){
						n++;
					}
				}
			}
			return n;
		} else {
			throw new RuntimeException();
		}
	}

	/**
	 * Compares two sequences and checks whether the mismatches between those are
	 * within a threshold, k.
	 * <br><br>Example:<br>kmm("AA", "AG", 1) --> true
	 * <br>kmm("AA", "AG", 0) --> false
	 * @param s1 The first sequence
	 * @param s2 The second sequence
	 * @param k The mismatch threshold
	 * @return False if the number of mismatches between the two input sequences
	 * is strictly greater than k. True otherwise.
	 */
	public static boolean withinKMismatches (String s1, String s2, int k){	
		int n = 0;	

		for (int i=0; i<s1.length(); i++){	

			if(s1.charAt(i) == 'N' || s2.charAt(i) == 'N') {
				n++;
			}
			else {
				if (s1.charAt(i) != s2.charAt(i)){
					n++;
				}
			}
			if(n>k) {
				return false;
			}
		}
		return true;
	}

	/**	
	 * Compares two N-coded sequences and checks whether the mismatches between those are
	 * within a threshold, k.
	 * @param s1 The first sequence as an integer array in N-code
	 * @param s2 The second sequence as an integer array in N-code
	 * @param k The mismatch threshold
	 * @param mmluN The mismatch matrix
	 * @return False if the number of mismatches between the two input sequences
	 * is strictly greater than k. True otherwise.
	 */
	public boolean withinKMismatches (int[] s1, int[] s2, int k){	
		int n = 0;	

		for (int i=0; i<s1.length; i++){		
			n = n + this.mmlu[s1[i]][s2[i]];
			if(n>k) {
				return false;
			}
		}		
		return true;
	}

	/**
	 * This method compares two sequences in N-code format
	 * and returns the number of mismatches between them.
	 * @param s1 First sequence as integer array in N-code
	 * @param s2 Second sequence as integer array in N-code
	 * @return The number of mismatches between s1 and s2
	 */
	public int numbMismatch (int[] s1, int[] s2){	
		// Initialize number of mismatches and set to zero.
		int n = 0;	
		// compare input strings and allow only stings of equal lengths

		for (int i=0; i<s1.length; i++){		
			n= n + this.mmlu[ s1[i] ][s2[i] ];
		}		
		return n;
	}
	
	/**
	 * Custom classException
	 * @author RockefellerSuperstar
	 *
	 */
	public static class EncoderException extends Exception {
		public EncoderException(String msg) {
			super(msg);
		}
	}
	
	
	public static void main(String [] args) throws EncoderException {
		int l = (int)Math.pow(5, 2);
		Encoder e = new Encoder(2);
		for(int i = 0; i < l; i++) {
			System.out.println(e.reConversionMap.get(i));
		}

		for(int i = 0; i < l; i++) {
			for(int j = 0; j < l; j++) {
				System.out.print(e.mmlu[i][j] + " ");
			}
			System.out.println();
		}

	}// end Main


}// end class