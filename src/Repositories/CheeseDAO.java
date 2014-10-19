package repositories;

import models.Cheese;

public interface CheeseDAO {
	Cheese getCheese(String userId);
	void saveCheese(Cheese cheese);
}
