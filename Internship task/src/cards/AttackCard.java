package cards;

public class AttackCard implements Card {
    private int number;

    public AttackCard(int number) {
        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void effect() {
        System.out.println("Attack card effect activated!\r\n");
    }

    @Override
    public String description() {
        return "Attack card";
    }
}
