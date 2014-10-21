package com.stealthecheese.provider;

import com.stealthecheese.model.Cheese;

public interface CheeseDAO {
	Cheese getCheese(String userId);
	void saveCheese(Cheese cheese);
}
