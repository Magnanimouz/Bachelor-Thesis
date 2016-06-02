import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    private static String course;

    public static void main(String[] args){
        course = choice("course");
        String assignment = choice("assignment");

        makeAssignment(assignment);
    }

    static String choice(String option) {
        JFrame choice = new JFrame();
        Object[] possibilities =  new Object[1000];
        File input = new File("./Resources/Courses.txt");
        try {
            Scanner in = new Scanner(input);

            switch (option) {
                case "course":
                    int i = 0;
                    while (in.hasNextLine()) {
                        Scanner c = new Scanner(in.nextLine());
                        c.useDelimiter(",");
                        possibilities[i] = c.next();
                        i++;
                    }
                    break;
                case "assignment":
                    int j = 0;
                    while (in.hasNextLine()) {
                        Scanner a = new Scanner(in.nextLine());
                        a.useDelimiter(",");
                        if (a.next().equals(course)) {
                            a.useDelimiter("([a-z+A-Z])");
                            a.next();
                            a.useDelimiter("\\t");
                            while (a.hasNext()) {
                                possibilities[j] = a.next();
                                j++;
                            }
                        }
                    }
                    break;
            }
            String toReturn = (String) JOptionPane.showInputDialog(choice, "Choose " + option + ":\n", option, -1, (Icon) null, possibilities, possibilities[0]);
            return toReturn;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "null";
    }

    static void makeAssignment(String assignment){
        //Depending on which course and assignment are chosen, switch to class corresponding to that choice.
        switch (assignment) {
            case "Dose Response":
                DoseResponse work = new DoseResponse();
                work.create();
                break;
        }
    }
}