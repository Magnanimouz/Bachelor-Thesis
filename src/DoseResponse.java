import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

class DoseResponse {

    private final int NUMBER_OF_SAMPLES = 84, SAMPLES_PER_SET = 12, NUMBER_OF_SETS = 7, POSSIBLE_NUMBER_OF_MISTAKES = 20;
    private final String[] columnNames = {"Concentration", "Normal", "Lethal effects", "Non-lethal effects", "%Mortality (only lethal)", "% Affected (non-lethal + lethal)"};
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
            scan.close();
            makeIntroduction(introduction);
        } catch (IOException e) {e.printStackTrace();}

        this.window.setup();

        answers = makeTable();
        student = new DRTable[NUMBER_OF_SAMPLES];

        for (int i = 0; i < NUMBER_OF_SAMPLES; i++) {
            String picture = answers[i].getPicture();
            DRTable.Heartrate heart = answers[i].getHeart();
            DRTable.Movement move = answers[i].getMove();
            String set = answers[i].getSet();
            Double concentration = answers[i].getConcentration();
            student[i] = makeDRQuestion(picture, heart, move, set, concentration);
        }

        this.window.setup();

        Object[][] answerData = calculateTable(answers);
        Object[][] studentData = calculateTable(student);

        showGraph(columnNames, studentData);

        this.window.setup();

        int[] widths = {80, 50, 100, 120, 150, 180};
        this.window.presentTable(columnNames, studentData, widths);
        Calculation answerCalculations = new Calculation();
        answerCalculations.fillAnswers();
        Calculation studentCalculations = new Calculation();
        studentCalculations.calculations = makeDRCalculationQuestion(studentCalculations);

        this.window.setup();

        compareWithAnswers(answerData, studentData, answerCalculations, studentCalculations);
    }

    private void makeIntroduction(String introduction) {
        try {
            this.window.showIntroduction(introduction);
            this.window.showWindow();
            while (!this.window.proceed) {
                Thread.sleep(200);
            }
        } catch (InterruptedException ignored) {}
    }

    private DRTable makeDRQuestion(String picture, DRTable.Heartrate heart, DRTable.Movement move, String set, Double concentration) {
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
            scan.close();
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

    private Double[] makeDRCalculationQuestion(Calculation calc) {
        String instructions = "";
        Double[] answers = new Double[5];
        this.window.makeQuestionsPane();
        for (int i = 0; i < 5; i++) {
            this.window.showOpenQuestion("Calculate " + calc.names[i], i, true);
            instructions += (calc.descriptions[i] + "\n" + "(Only type numbers)\n");
        }
        this.window.makeBackgroundPane();
        this.window.showBackground(instructions);
        this.window.showWindow();

        try {
            while (!this.window.proceed) {
                Thread.sleep(200);
            }
        } catch (InterruptedException ignored) {}

        for (int i = 0; i < this.window.openanswers.size(); i++) {
            String s = this.window.openanswers.get(i);
            answers[i] = Double.parseDouble(s);
        }
        return answers;
    }

    private void compareWithAnswers(Object[][] answerTable, Object[][] studentTable, Calculation answerCalc, Calculation studentCalc){
        String[][] columns = new String[3][];
        columns[0] = columnNames;
        String[] feedbackColumns = {"Image", "Effects", "Result"};
        columns[1] = feedbackColumns;
        String[] effects = new String[8];
        effects[0] = "Concentration";
        for (int i = 0; i < 7; i++) {
            effects[i+1] = student[0].getEffect(i).getName();
        }
        columns[2] = effects;

        Object[][][] data = new Object[3][][];
        data[0] = studentTable;
        data[1] = showInDepthFeedback();
        data[2] = malformationsCounter();

        String[] names = {"Student", "Feedback", "Malformations"};

        int[] width = {80, 50, 100, 120, 150, 180};
        int[] feedbackWidth = {100, 1050, 60};
        int[] malformationsWidth = {80, 80, 50, 150, 150, 150, 150, 150};
        int[][] widths = {width, feedbackWidth, malformationsWidth};

        this.window.presentTables(columns, data, names, widths);

        int mistakes = getGrade(answerTable, studentTable, answerCalc, studentCalc);
        this.window.showDRAnswers(names[0], studentCalc.names, studentCalc.calculations, mistakes, POSSIBLE_NUMBER_OF_MISTAKES);

        try {
            while (!this.window.proceed) {
                Thread.sleep(200);
            }
        } catch (InterruptedException ignored) {}

        printFeedback(columns, data, names);

    }

    private void printFeedback(String[][] columns, Object[][][] data, String[] names) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("./out/Feedback Reference.txt", false));
            for (int i = 0; i < names.length; i++) {
                writer.write(names[i] + "\n");
                for (int j = 0; j < columns[i].length; j++) {writer.write(columns[i][j] + "\t");}
                writer.write("\n");
                for (int j = 0; j < data[i].length; j++) {
                    for (int k = 0; k < data[i][j].length; k++) {
                        writer.write(data[i][j][k] + "\t");
                    }
                    writer.write("\n");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {e.printStackTrace();}
    }

    private Object[][] showInDepthFeedback() {
        int differences = 0;
        for (int i = 0; i < answers.length; i++) {
            if (!answers[i].getResult().equals(student[i].getResult())) differences++;
        }

        DRTable[] ans = new DRTable[differences];
        DRTable[] stu = new DRTable[differences];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = new DRTable();
            stu[i] = new DRTable();
        }
        int pos = 0;
        for (int i = 0; i < answers.length; i++) {
            if (!answers[i].getResult().equals(student[i].getResult())) {
                ans[pos] = answers[i];
                stu[pos] = student[i];
                pos++;
            }
        }

        Object[][] res = new Object[differences][3];
        for (int i = 0; i < res.length; i++) {
            res[i] = compareDRRows(ans[i], stu[i]);
        }
        return res;
    }

    private Object[][] malformationsCounter() {
        Object[][] data = new Object[NUMBER_OF_SETS][8];
        String[] concentrations = {"Negative Control", "Positive Control", "0.06 mM", "0.15 mM", "1.0 mM", "5.0 mM", "11.0 mM"};
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            data[i][0] = concentrations[i];
            for (int j = 0; j < 7; j++) {
                data[i][j+1] = countEffects(i, j, student);
            }
        }
        return data;
    }

    private Object[] compareDRRows(DRTable answers, DRTable student) {
        Object[] ret = new Object[3];
        if (!answers.getResult().equals(student.getResult())) {
            ret[0] = student.getPicture();
            DRTable.Effect[] eff = student.getEffects();
            String effect = "";
            for (int i = 0; i < eff.length; i++) {
                if (i == eff.length-1) effect += (eff[i].getName() + ": " + eff[i].getStatus());
                else effect += (eff[i].getName() + ": " + eff[i].getStatus() + " ");
            }

            ret[1] = effect;
            ret[2] = student.getResult();
        }
        return ret;
    }

    private int getGrade(Object[][] answerTable, Object[][] studentTable, Calculation answerCalc, Calculation studentCalc) {
        int mistakeCounter = 0;
        for (int i = 0; i < answerTable.length - 2; i++) {
            for (int j = 0; j < answerTable[i].length - 2; j++) {
                if (!studentTable[i + 2][j].equals(answerTable[i + 2][j])) mistakeCounter++;
            }
        }
        for (int i = 0; i < answerCalc.calculations.length; i++) {
            if (i > 2) {
                double margin = 1;
                if (i == 3) margin = 0.1;
                Double stud = studentCalc.calculations[i];
                Double ans = answerCalc.calculations[i];
                if (stud < ans - margin) mistakeCounter++;
                else if (stud > ans + margin) mistakeCounter++;
            } else if (!studentCalc.calculations[i].equals(answerCalc.calculations[i])) mistakeCounter++;
        }
        return mistakeCounter;
    }

    private Object[][] calculateTable(DRTable[] table) {
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
            mortality[i] = Math.round(mortality[i] * 100d) / 100d;
            percentAffected[i] = (((dead[i] + affected[i]) / SAMPLES_PER_SET) * 100);
            percentAffected[i] = Math.round(percentAffected[i] * 100d) / 100d;
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

    private int count(int set, DRTable.Result sample, DRTable[] table) {
        int count = 0;
        for (int i = (set * SAMPLES_PER_SET); i < (SAMPLES_PER_SET * (set + 1)); i++) {
            if (table[i].getResult() == sample) count++;
        }
        return count;
    }

    private int countEffects(int set, int effect, DRTable[] table) {
        int count = 0;
        for (int i = (set * SAMPLES_PER_SET); i < (SAMPLES_PER_SET * (set + 1)); i++) {
            if (table[i].getEffect(effect).getStatus()) count++;
        }
        return count;
    }

    private void showGraph(String[] columns, Object[][] table){
        try {
            Scanner scan = new Scanner(new File("./Resources/Dose Response/Graph Question.txt"));
            String text = scan.useDelimiter("\\A").next();
            scan.close();
            String path = "./Resources/Dose Response/Dose Response Graph.png";
            int[] widths = {80, 50, 100, 120, 150, 180};
            this.window.showPicture(path);
            this.window.presentTable(columns, table, widths);
            this.window.showIntroduction(text);
            this.window.showWindow();
        } catch (FileNotFoundException e) {e.printStackTrace();}
        try {
            while (!this.window.proceed) {
                Thread.sleep(200);
            }
        } catch (InterruptedException ignored) {}
    }

    private DRTable[] makeTable() {
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

    enum Heartrate {NORMAL, AFFECTED, NONE}
    enum Movement {NORMAL, AFFECTED, NONE}
    enum Result {ALIVE, AFFECTED, DEAD}

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

    private void nameEffects(){
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

    Effect[] getEffects() { return this.effect;}

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
    Double[] calculations = new Double[NUMBER_OF_CALCULATIONS];

    Calculation() {
        for (int i = 0; i < NUMBER_OF_CALCULATIONS; i++) {
            if (i == 0) {
                names[i] = "NOEC";
                descriptions[i] = "NOEC: \nHighest concentration with no effects compared to\ncontrol group";
            } else if (i == 1) {
                names[i] = "LOEC";
                descriptions[i] = "LOEC: \nLowest concentration with effects compared to\ncontrol group";
            } else if (i == 2) {
                names[i] = "LC50";
                descriptions[i] = "LC50: \nConcentration where 50% of the treated embryos\nare dead";
            } else if (i == 3) {
                names[i] = "EC50";
                descriptions[i] = "EC50: \nConcentration where 50% of the treated embryos\nare affected (malformed and dead)";
            } else if (i == 4) {
                names[i] = "TI";
                descriptions[i] = "TI: \nTeratogenic Index = LC50/EC50";
            }
        }
    }

    @Override
    public String toString() {
        return "Calculation{" +
                "NUMBER_OF_CALCULATIONS=" + NUMBER_OF_CALCULATIONS +
                ", names=" + Arrays.toString(names) +
                ", descriptions=" + Arrays.toString(descriptions) +
                ", calculations=" + Arrays.toString(calculations) +
                '}';
    }

    void fillAnswers() {
        for (int i = 0; i < NUMBER_OF_CALCULATIONS; i++) {
            if (i == 0) this.calculations[i] = 0.06;
            else if (i == 1) this.calculations[i] = 0.15;
            else if (i == 2) this.calculations[i] = 6.0;
            else if (i == 3) this.calculations[i] = 0.2;
            else if (i == 4) this.calculations[i] = 30.0;
        }

    }
}