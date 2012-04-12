package app.android.gambit.models;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CardsOrderShuffler
{
	private int cardsCount;
	private List<Integer> unusedIndexes = new ArrayList<Integer>();
	private Random random = new Random();

	public CardsOrderShuffler(int cardsCount) {
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
		for (int i = 0; i < cardsCount; i++) {
			unusedIndexes.add(i);
		}
	}

	public int generateNextIndex() {
		if (unusedIndexes.isEmpty()) {
			throw new ModelsException();
		}

		int listIndex = random.nextInt(unusedIndexes.size());
		int result = unusedIndexes.get(listIndex);
		unusedIndexes.remove(listIndex);

		return result;
	}

	public boolean isFinished() {
		return unusedIndexes.isEmpty();
	}
}
