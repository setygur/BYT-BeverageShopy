import models.Coffee;
import models.Fruit;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Coffee coffee = new Coffee(6);
        List<String> fruitzz = new ArrayList<>();
        Fruit smoothie = new Fruit(fruitzz, true);
    }
}
