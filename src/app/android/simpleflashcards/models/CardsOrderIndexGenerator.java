package app.android.simpleflashcards.models;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CardsOrderIndexGenerator
{
	private int cardsCount;
	private List<Integer> unusedIndexes = new ArrayList<Integer>();
	private Random random = new Random();

	public CardsOrderIndexGenerator(int cardsCount) {
		setCardsCount(cardsCount);
	}

	public void setCardsCount(int cardsCount) {
		if (cardsCount <= 0) {
			throw new ModelsException();
		}

		this.cardsCount = cardsCount;
		resetUnusedIndexes();
	}

	private void resetUnusedIndexes() {
		unusedIndexes.clear();
		for (int i = 0; i < cardsCount; ++i) {
			unusedIndexes.add(i);
		}
	}

	public int generate() {
		if (unusedIndexes.size() == 0) {
			throw new ModelsException();
		}

		int listIndex = random.nextInt(unusedIndexes.size());
		int result = unusedIndexes.get(listIndex);
		unusedIndexes.remove(listIndex);

		return result;
	}
}
