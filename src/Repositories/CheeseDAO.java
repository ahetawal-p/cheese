package Repositories;

import Models.Cheese;

public interface CheeseDAO {
	Cheese getCheese(String userId);
	void saveCheese(Cheese cheese);
}
