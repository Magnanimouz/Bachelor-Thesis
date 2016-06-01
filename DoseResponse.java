import java.io.*;
import java.util.Scanner;


public class DoseResponse extends Layout {

    private final int NUMBER_OF_SAMPLES = 84, SAMPLES_PER_SET = 12, NUMBER_OF_SETS = 7;
    DRTable[] answers, student;

    DoseResponse() {
        super();
    }

    public void create(){
//        try {
//            Scanner scan = new Scanner(new File("./Resources/Dose Response/Introduction.txt"));
//            String introduction = scan.useDelimiter("\\A").next();
//            makeIntroduction(introduction);
//        } catch (IOException e) {e.printStackTrace();}
//
//            //For testing with console output!
//            //System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
//
            answers = makeTable();
//
//        for (int i = 0; i < NUMBER_OF_SAMPLES; i++){
//            String picture = answers[i].getPicture();
//            DRTable.Heartrate h = answers[i].getHeart();
//            String heart = h.name();
//            DRTable.Movement m = answers[i].getMove();
//            String move = m.name();
//            student[i] = makeDRQuestion(i, picture, heart, move);
//
//        }
        Object[][] answerData = calculateTable(answers);

        String[] columnNames = {"Concentration", "Normal", "Lethal effects", "Non-lethal effects", "%Mortality (only lethal)", "% Affected (non-lethal + lethal)" };
//        Object[][] studentData = calculateTable(student);

        Calculation answerCalculations = new Calculation();
        answerCalculations.fill();
        Calculation studentCalculations = new Calculation();
        studentCalculations.calculations = makeDRCalculationQuestion(studentCalculations);
    }

    Object[][] calculateTable(DRTable[] table){
        int[] alive = new int[NUMBER_OF_SETS];
        int[] dead = new int[NUMBER_OF_SETS];
        int[] affected = new int[NUMBER_OF_SETS];
        double[] mortality = new double[NUMBER_OF_SETS];
        double[] percentAffected = new double[NUMBER_OF_SETS];
        Object[][] data = new Object[NUMBER_OF_SETS][5];
        for (int i = 0; i < NUMBER_OF_SETS; i++) {
            alive[i] = count(i, DRTable.Result.ALIVE, table);
            dead[i] = count(i, DRTable.Result.DEAD, table);
            affected[i] = count(i, DRTable.Result.AFFECTED, table);
            mortality[i] = ((dead[i] / SAMPLES_PER_SET) * 100);
            percentAffected[i] = (((dead[i] + affected[i]) / SAMPLES_PER_SET) * 100);
            for (int j = 0; j < 5; j++){
                if (j == 0) data[i][j] = alive[i];
                else if (j == 1) data[i][j] = dead[i];
                else if (j == 2) data[i][j] = affected[i];
                else if (j == 3) data[i][j] = mortality[i];
                else if (j == 4) data[i][j] = percentAffected[i];
            }
        }
        return data;
    }

    int count(int set, DRTable.Result sample, DRTable[] table){
        int count = 0;
        for (int i = (set*SAMPLES_PER_SET); i < (SAMPLES_PER_SET*(set+1)); i ++){
            if (table[i].getResult() == sample) count++;
        }
        return count;
    }

    DRTable[] makeTable(){
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
        enum Heartrate{NORMAL, AFFECTED, NONE}

        enum Movement{NORMAL, AFFECTED, NONE}

        enum Result{ALIVE, AFFECTED, DEAD}

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
            for (int i = 0; i < NUMBER_OF_EFFECTS; i++) { effect[i] = new Effect(); }
        }

        /*getters*/
        String getSet() {return this.set;}

        String getPicture() {return this.picture;}

        Double getConcentration() {return this.concentration;}

        Effect getEffect(int number) {return this.effect[number];}

        Heartrate getHeart() {return this.heart;}

        Movement getMove() {return this.move;}

        Result getResult() {return this.result;}

        /*setters*/

        void setSet(String set) {this.set = set;}

        void setPicture(String picture) {this.picture = picture;}

        void setConcentration(Double concentration) {this.concentration = concentration;}

        void setHeartrate(Heartrate value) {heart = value;}

        void setMovement(Movement value) {move = value;}

        void setResult(Result value) {result = value;}

        class Effect{
            private String name;
            private boolean status;

            Effect() {
                name = "";
                status = false;
            }

            /*getters*/

            String getName() {return this.name;}

            boolean getStatus() {return this.status;}

            /*setters*/

            String setName(String name) {return this.name = name;}

            boolean setStatus(boolean status) {return this.status = status;}
        }
    }

    class Calculation {
        private final double tempVar = 0;
        private final int NUMBER_OF_CALCULATIONS = 5;
        String[] names = new String[NUMBER_OF_CALCULATIONS], descriptions = new String[NUMBER_OF_CALCULATIONS];
        double[] calculations = new double[NUMBER_OF_CALCULATIONS];

        Calculation() {
            for (int i = 0; i < NUMBER_OF_CALCULATIONS; i++){
                if (i == 0) {
                    names[i] = "NOEC";
                    descriptions[i] = "NOEC: \n Highest concentration with no effects compared to the control group.";
                }
                else if (i == 1) {
                    names[i] = "LOEC";
                    descriptions[i] = "LOEC: \n Lowest concentration with effects compared to the control group.";
                }
                else if (i == 2) {
                    names[i] = "LC50";
                    descriptions[i] = "LC50: \n Concentration where 50% of the treated embryos are dead.";
                }
                else if (i == 3) {
                    names[i] = "EC50";
                    descriptions[i] = "EC50: \n Concentration where 50% of the treated embryos are affected (malformed and dead).";
                }
                else if (i == 4) {
                    names[i] = "TI";
                    descriptions[i] = "TI: \n Teratogenic Index = LC50/EC50.";
                }
            }
        }

        void fill(){
            for (int i = 0; i < NUMBER_OF_CALCULATIONS; i++){
                if (i == 0) this.calculations[i] = tempVar;
                else if (i == 1) this.calculations[i] = tempVar;
                else if (i == 2) this.calculations[i] = tempVar;
                else if (i == 3) this.calculations[i] = tempVar;
                else if (i == 4) this.calculations[i] = tempVar;
            }

        }
    }