package cards;

public class BoostAttackCard implements Card {
    private int number;
    private static int boost = 3;

    public BoostAttackCard() {
        this.number = 2;
    }

    @Override
    public int getNumber() {
        return number;
    }

    public int getBoost() {
        return boost;
    }

    @Override
    public void effect() {
        System.out.println("Boost Attack card effect activated! Player have increased attack damage in this turn by " + boost + "!\r\n");
    }

    @Override
    public String description() {
        return "Boost card";
    }
}
