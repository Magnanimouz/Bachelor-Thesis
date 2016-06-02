import java.awt.*;
import java.io.*;
import java.util.Scanner;

public abstract class Layout {

    public Window window;

    Layout() {
        GridBagLayout formation = new GridBagLayout();
        this.window = new Window(formation);
    }

    public abstract void create();

    public void makeIntroduction(String introduction) {
        try {
            this.window.showIntroduction(introduction);
            while (!this.window.proceed) {
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public DRTable makeDRQuestion(int i, String picture, DRTable.Heartrate heart, DRTable.Movement move, String set, Double concentration) {
        /*HOW TO RETURN DRTABLE FILLED IN THROUGH SUBMIT BUTTON*/
        DRTable submission = new DRTable();
        submission.setHeartrate(heart);
        submission.setMovement(move);
        submission.setPicture(picture);
        submission.setSet(set);
        submission.setConcentration(concentration);

        this.window.setup();
        this.window.makeConditionPane();
        String[] status = new String[1];
        status[0] = heart.name();
        this.window.fillCondition("State", 0, "Heartrate", status, 0);
        status[0] = move.name();
        this.window.fillCondition("State", 0, "Movement", status, 1);
        String[] effects = {"Coagulated", "Edema", "Tail malformation", "Head malformation", "Altered pigmentation", "Eye malformation", "Yolksac malformation"};
        this.window.fillCondition("Check", 7, "Effects", effects, 2);
        String[] condition = {"Alive", "Affected", "Dead"};
        this.window.fillCondition("Radio", 3, "Result", condition, 3);

        String path = "./Resources/Dose Response/Pictures/" + picture;
        this.window.showPicture(path);
        try {
            Scanner scan = new Scanner(new File("./Resources/Dose Response/Table Question.txt"));
            String background = scan.useDelimiter("\\A").next();
            this.window.showBackground(background);

            this.window.showWindow();
            while (!this.window.proceed) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }

            System.err.println(this.window.getDRAnswer());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return submission;
    }

    public double[] makeDRCalculationQuestion(Calculation calc) {
        String instructions = "";
        double[] answers = new double[5];
        for (int i = 0; i < 5; i++) {
            this.window.showOpenQuestion("Calculate " + calc.names[i], i);
            instructions += (calc.descriptions[i] + "\n");
        }
        this.window.showBackground(instructions);
        this.window.showWindow();

        return answers;
    }

    public static class DaListenerMan {
        private String result;

        public void setResult(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }
    }
}
