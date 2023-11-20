package cards;

public class ProtectCard implements Card {

    private int number;

    public ProtectCard() {
        this.number = 1;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void effect() {
        System.out.println("Protect card effect activated! Player deflects opponent's attack!\r\n");
    }

    @Override
    public String description() {
        return "Protect card";
    }

}
