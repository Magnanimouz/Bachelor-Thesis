import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;


public class DoseResponse {

    private final int NUMBER_OF_SAMPLES = 84, SAMPLES_PER_SET = 12, NUMBER_OF_SETS = 7;
    private Window window;
    DRTable[] answers, student;

    DoseResponse() {
        GridBagLayout formation = new GridBagLayout();
        this.window = new Window(formation);
    }

    public void create() {
        try {
            Scanner scan = new Scanner(new File("./Resources/Dose Response/Introduction.txt"));
            String introduction = scan.useDelimiter("\\A").next();
            makeIntroduction(introduction);
        } catch (IOException e) {e.printStackTrace();}

        answers = makeTable();
        student = new DRTable[NUMBER_OF_SAMPLES];

//        for (int i = 0; i < NUMBER_OF_SAMPLES; i++) {
//            String picture = answers[i].getPicture();
//            DRTable.Heartrate heart = answers[i].getHeart();
//            DRTable.Movement move = answers[i].getMove();
//            String set = answers[i].getSet();
//            Double concentration = answers[i].getConcentration();
//            student[i] = makeDRQuestion(i, picture, heart, move, set, concentration);
//        }

        String[] columnNames = {"Concentration", "Normal", "Lethal effects", "Non-lethal effects", "%Mortality (only lethal)", "% Affected (non-lethal + lethal)"};
        Object[][] answerData = calculateTable(answers);
        //Object[][] studentData = calculateTable(student);

        this.window.makeCenterPane();
        this.window.presentTable(columnNames, answerData);

        Calculation answerCalculations = new Calculation();
        answerCalculations.fillAnswers();
        Calculation studentCalculations = new Calculation();
        studentCalculations.calculations = makeDRCalculationQuestion(studentCalculations);
        this.window.showWindow();
    }

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
        DRTable submission = new DRTable();

        this.window.setup();

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
                } catch (InterruptedException ignored) {}
            }

            submission = this.window.getDRAnswer();
            submission.setHeartrate(heart);
            submission.setMovement(move);
            submission.setPicture(picture);
            submission.setSet(set);
            submission.setConcentration(concentration);

        } catch (FileNotFoundException e) {e.printStackTrace();}
        return submission;
    }

    public double[] makeDRCalculationQuestion(Calculation calc) {
        String instructions = "";
        double[] answers = new double[5];
        this.window.makeQuestionsPane();
        for (int i = 0; i < 5; i++) {
            this.window.showOpenQuestion("Calculate " + calc.names[i], i);
            instructions += (calc.descriptions[i] + "\n");
        }
        this.window.showBackground(instructions);
        this.window.showWindow();

        return answers;
    }

    Object[][] calculateTable(DRTable[] table) {
        double[] alive = new double[NUMBER_OF_SETS];
        double[] dead = new double[NUMBER_OF_SETS];
        double[] affected = new double[NUMBER_OF_SETS];
        double[] mortality = new double[NUMBER_OF_SETS];
        double[] percentAffected = new double[NUMBER_OF_SETS];
        String[] concentrations = {"Negative Control", "Positive Control", "0.06 mM", "0.15 mM", "1.0 mM", "5.0 mM", "11.0 mM"};
        Object[][] data = new Object[NUMBER_OF_SETS][6];
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            alive[i] = count(i, DRTable.Result.ALIVE, table);
            dead[i] = count(i, DRTable.Result.DEAD, table);
            affected[i] = count(i, DRTable.Result.AFFECTED, table);
            mortality[i] = ((dead[i] / SAMPLES_PER_SET) * 100);
            percentAffected[i] = (((dead[i] + affected[i]) / SAMPLES_PER_SET) * 100);
            for (int j = 0; j < 6; j++) {
                if (j == 0) data[i][j] = concentrations[i];
                else if (j == 1) data[i][j] = alive[i];
                else if (j == 2) data[i][j] = dead[i];
                else if (j == 3) data[i][j] = affected[i];
                else if (j == 4) data[i][j] = mortality[i];
                else if (j == 5) data[i][j] = percentAffected[i];
            }
        }
        return data;
    }

    int count(int set, DRTable.Result sample, DRTable[] table) {
        int count = 0;
        for (int i = (set * SAMPLES_PER_SET); i < (SAMPLES_PER_SET * (set + 1)); i++) {
            if (table[i].getResult() == sample) count++;
        }
        return count;
    }

    DRTable[] makeTable() {
        DRTable[] toFill = new DRTable[NUMBER_OF_SAMPLES];
        for (int i = 0; i < NUMBER_OF_SAMPLES; i++) {
            toFill[i] = new DRTable();
        }
        DRTableReader reader = new DRTableReader(toFill);
        reader.fillTable();

        return toFill;
    }
}

class DRTable {
    private final int NUMBER_OF_EFFECTS = 7;

    private String set, picture;
    private Double concentration;

    static enum Heartrate {NORMAL, AFFECTED, NONE}
    static enum Movement {NORMAL, AFFECTED, NONE}
    static enum Result {ALIVE, AFFECTED, DEAD}

    private Effect[] effect = new Effect[NUMBER_OF_EFFECTS];

    private Heartrate heart;
    private Movement move;
    private Result result;

    DRTable() {
        set = "";
        picture = "";
        concentration = 0.0;
        heart = Heartrate.NORMAL;
        move = Movement.NORMAL;
        result = Result.ALIVE;
        for (int i = 0; i < NUMBER_OF_EFFECTS; i++) {
            effect[i] = new Effect();
        }
        nameEffects();
    }

    @Override
    public String toString() {
        String[] names = new String[NUMBER_OF_EFFECTS];
        boolean[] statusses = new boolean[NUMBER_OF_EFFECTS];

        for (int i = 0; i < NUMBER_OF_EFFECTS; i++){
            names[i] = effect[i].getName();
            statusses[i] = effect[i].getStatus();
        }
        return "DRTable{" +
                ", set='" + set + '\'' +
                ", picture='" + picture + '\'' +
                ", concentration=" + concentration +
                ", effect=" + Arrays.toString(names) + Arrays.toString(statusses) +
                ", heart=" + heart +
                ", move=" + move +
                ", result=" + result +
                '}';
    }

    void nameEffects(){
        effect[0].setName("Coagulated");
        effect[1].setName("Edema");
        effect[2].setName("Tail malformation");
        effect[3].setName("Head malformation");
        effect[4].setName("Altered pigmentation");
        effect[5].setName("Eye malformation");
        effect[6].setName("Yolksac malformation");
    }

    /*getters*/
    String getSet() {
        return this.set;
    }

    String getPicture() {
        return this.picture;
    }

    Double getConcentration() {
        return this.concentration;
    }

    Effect getEffect(int number) {
        return this.effect[number];
    }

    Effect getEffectByName(String name) {
        Effect toReturn = new Effect();
        for (int i = 0; i < NUMBER_OF_EFFECTS; i++){
            if (effect[i].getName().equals(name)) toReturn = effect[i];
        }
        return toReturn;
    }

    Heartrate getHeart() {
        return this.heart;
    }

    Movement getMove() {
        return this.move;
    }

    Result getResult() {
        return this.result;
    }

        /*setters*/

    void setSet(String set) {
        this.set = set;
    }

    void setPicture(String picture) {
        this.picture = picture;
    }

    void setConcentration(Double concentration) {
        this.concentration = concentration;
    }

    void setHeartrate(Heartrate value) {
        heart = value;
    }

    void setMovement(Movement value) {
        move = value;
    }

    void setResult(Result value) {
        result = value;
    }

    class Effect {
        private String name;
        private boolean status;

        Effect() {
            name = "";
            status = false;
        }

            /*getters*/

        String getName() {
            return this.name;
        }

        boolean getStatus() {
            return this.status;
        }

            /*setters*/

        String setName(String name) {
            return this.name = name;
        }

        boolean setStatus(boolean status) {
            return this.status = status;
        }
    }
}

class Calculation {
    private final int NUMBER_OF_CALCULATIONS = 5;
    String[] names = new String[NUMBER_OF_CALCULATIONS], descriptions = new String[NUMBER_OF_CALCULATIONS];
    double[] calculations = new double[NUMBER_OF_CALCULATIONS];

    Calculation() {
        for (int i = 0; i < NUMBER_OF_CALCULATIONS; i++) {
            if (i == 0) {
                names[i] = "NOEC";
                descriptions[i] = "NOEC: \nHighest concentration with no effects compared to the control group.";
            } else if (i == 1) {
                names[i] = "LOEC";
                descriptions[i] = "LOEC: \nLowest concentration with effects compared to the control group.";
            } else if (i == 2) {
                names[i] = "LC50";
                descriptions[i] = "LC50: \nConcentration where 50% of the treated embryos are dead.";
            } else if (i == 3) {
                names[i] = "EC50";
                descriptions[i] = "EC50: \nConcentration where 50% of the treated embryos are affected (malformed and dead).";
            } else if (i == 4) {
                names[i] = "TI";
                descriptions[i] = "TI: \nTeratogenic Index = LC50/EC50.";
            }
        }
    }

    void fillAnswers() {
        for (int i = 0; i < NUMBER_OF_CALCULATIONS; i++) {
            if (i == 0) this.calculations[i] = 0.06;
            else if (i == 1) this.calculations[i] = 0.15;
            else if (i == 2) this.calculations[i] = 6;
            else if (i == 3) this.calculations[i] = 0.2;
            else if (i == 4) this.calculations[i] = 30;
        }

    }
}