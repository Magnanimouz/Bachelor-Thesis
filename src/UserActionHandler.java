import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

class UserActionHandler {

    private static String course, user;
    private static final String PASSWORD = "Password";

    UserActionHandler() throws Exception{
        user = authenticator();
    }

    private static String authenticator() {
        JFrame choice = new JFrame();
        choice.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        String[] users = {"Student", "Teacher"};
        String ret = "";
        Object s = JOptionPane.showInputDialog(choice, "Choose user:\n", "User", JOptionPane.PLAIN_MESSAGE, null, users, users[0]);
        if (s == null) {
            System.err.println("Incorrect input, or cancelled.");
            System.exit(1);
        }
        else {ret = s.toString();}
        if (ret.equals("Teacher")) {
            boolean proceed = false;
            while (!proceed) {
                String inputValue = JOptionPane.showInputDialog("Please input password.");
                if (inputValue == null) System.exit(1);
                else if (!inputValue.equals(PASSWORD))
                    JOptionPane.showMessageDialog(null, "Incorrect password.", "Error", JOptionPane.ERROR_MESSAGE);
                else proceed = true;
            }
        }
        return ret;
    }

    void showOptions() throws Exception{
        switch (user) {
            case "Student":
                course = choice("course");
                String assignment = choice("assignment");

                makeAssignment(assignment);
                break;
            case "Teacher":
                JFrame choice = new JFrame();
                choice.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                String[] options = {"Make course", "Make assignment", "Add data"};
                String ret = "";
                Object s = JOptionPane.showInputDialog(choice, "Choose task:\n", "Task", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (s == null) {
                    System.err.println("Incorrect input, or cancelled.");
                    System.exit(1);
                }
                else {ret = s.toString();}
                switch (ret) {
                    case "Make course":
                        boolean proceed = false;
                        String inputValue = "";
                        while (!proceed) {
                            inputValue = JOptionPane.showInputDialog("Please input course name.");
                            if (checkDuplicate(inputValue)) {
                                JOptionPane.showMessageDialog(null, "Course already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            else if (inputValue == null) System.exit(1);
                            else proceed = true;
                        }
                        FileWriter writer = new FileWriter("./Resources/Courses.txt", true);
                        PrintWriter printer = new PrintWriter(writer, true);
                        printer.println(inputValue + ", ");
                        printer.close();

                        int reply = JOptionPane.showConfirmDialog(null, "Make Assignment as well?", "Make Assignment?", JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.NO_OPTION) break;
                        course = inputValue;
                    case "Make assignment":
                        if (course == null) course = choice("course");
                        String input = JOptionPane.showInputDialog("Please input assignment name.");
                        if (input == null) System.exit(1);

                        File file = new File("./Resources/Courses.txt");
                        Scanner scan = new Scanner(file);
                        int lines = 0;
                        while (scan.hasNextLine()) {
                            scan.nextLine();
                            lines++;
                        }
                        scan = new Scanner(file);
                        String[] text = new String[lines];
                        int pos = 0;
                        while (scan.hasNextLine()) {
                            text[pos] = scan.nextLine();
                            pos++;
                        }
                        for (int i = 0; i < text.length; i++) {
                            Scanner in = new Scanner(text[i]);
                            in.useDelimiter(",");
                            if (in.next().equals(course)) text[i] += "\t"+input;
                        }
                        FileWriter write = new FileWriter("./Resources/Courses.txt");
                        PrintWriter print = new PrintWriter(write, true);
                        for (int i = 0; i < text.length; i++) {
                            print.println(text[i]);
                        }
                        JOptionPane.showMessageDialog(null, "Only a test version. The file holding all courses and assignments has been updated!", "Message", JOptionPane.PLAIN_MESSAGE);
                        break;
                    case "Add data":
                        course = choice("course");
                        String assign = choice("assignment");
                        JOptionPane.showMessageDialog(null, "Only a test version. Assignment chosen, but current framework does not allow dynamic creation.", "Message", JOptionPane.PLAIN_MESSAGE);
                        break;
                }
                int i = JOptionPane.showConfirmDialog(null, "Make another course, assignment, or add data?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION) showOptions();
        }
    }

    private String choice(String option) throws Exception{
        JFrame choice = new JFrame();
        choice.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
                        c.close();
                    }
                    in.close();
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
                            if (!a.hasNext()) {
                                int t = JOptionPane.showConfirmDialog(null, "No available assignments for this course.\nMake assignment first?", "Confirmation", JOptionPane.YES_NO_OPTION);
                                if (t == JOptionPane.YES_OPTION) showOptions();
                                else if (t == JOptionPane.NO_OPTION) System.exit(1);
                            }
                            while (a.hasNext()) {
                                possibilities[j] = a.next();
                                j++;
                            }
                        }
                        a.close();
                    }
                    in.close();
                    break;
            }
            String ret = "";
            Object toReturn = JOptionPane.showInputDialog(choice, "Choose " + option + ":\n", option, JOptionPane.PLAIN_MESSAGE, null, possibilities, possibilities[0]);
            if (toReturn == null) {
                System.err.println("Incorrect input, or cancelled.");
                System.exit(1);
            }
            else {ret = toReturn.toString();}
            return ret;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "null";
    }

    private boolean checkDuplicate(String input) throws Exception{
        boolean toReturn = false;
        File file = new File("./Resources/Courses.txt");
        Scanner scan = new Scanner(file);
        int lines = 0;
        while (scan.hasNextLine()) {
            scan.nextLine();
            lines++;
        }
        scan = new Scanner(file);
        scan.useDelimiter(",");
        String[] text = new String[lines];
        int pos = 0;
        while (scan.hasNextLine()) {
            text[pos] = scan.next();
            if (scan.hasNext()) scan.nextLine();
            pos++;
        }
        for (int i = 0; i < text.length; i++) {
            if (input.equals(text[i])) toReturn = true;
        }
        return toReturn;
    }

   private void makeAssignment(String assignment){
        //Depending on which course and assignment are chosen, switch to class corresponding to that choice.
        switch (assignment) {
            case "Dose Response":
                DoseResponse work = new DoseResponse();
                work.create();
                break;
        }
    }
}
