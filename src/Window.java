import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Window extends JFrame {
    private JFrame window;
    private JPanel main, choices, background, center, questions;
    private JButton button;
    private JTextArea console;
    private GridBagConstraints constraints;
    private GridBagLayout formation;

    private HashMap<String, JCheckBox[]> checkboxes;
    private HashMap<String, ButtonGroup> radiobuttons;
    ArrayList<String> openanswers;

    volatile boolean proceed = false;

    Window(GridBagLayout formation){
        this.formation = formation;
        setup();
    }

    void setup(){
        this.window = new JFrame("Homework");
        this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.window.setLocation(100, 50);
        this.window.setSize(1600, 900);
        this.main = new JPanel(formation);
        this.main.setBackground(Color.WHITE);
        this.main.setPreferredSize(new Dimension(1500, 800));
        this.constraints = new GridBagConstraints();
        this.constraints.insets = new Insets(20, 20, 20, 20);
        JScrollPane scrollable = new JScrollPane(this.main, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.window.add(scrollable);
        checkboxes = new HashMap<>();
        radiobuttons = new HashMap<>();
        openanswers = new ArrayList<>();
        proceed = false;

        makeBackgroundPane();
        makeCenterPane();
        makeQuestionsPane();
        makeConditionPane();
    }

    private JButton reset(){
        proceed = false;
        button = new JButton("Next");
        button.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e) {
                window.setVisible(false);
                setup();
                proceed = true;
            }
        });
        return button;
    }

    private JButton submit(){
        proceed = false;
        button = new JButton("Submit");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setVisible(false);
                proceed = true;
            }
        });
        return button;
    }

    private JButton close(){
        proceed = false;
        button = new JButton("Close");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                window.setVisible(false);
                System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
                window.dispose();
                proceed = true;
            }
        });
        return button;
    }

    void showIntroduction(String information){
        JPanel intro = new JPanel(formation);
        intro.setBackground(Color.WHITE);
        intro.setPreferredSize(new Dimension(600, 600));
        console = setShowText("Information", false, 800, 400);
        intro.add(console);
        System.out.println(information);
        constraints.gridy = 1;
        button = reset();
        intro.add(button, constraints);
        center.add(intro, BorderLayout.EAST);
        addToMain(2, center);
    }

    private void addToMain(int pos, JPanel toAdd){
        constraints.gridy = 0;
        constraints.gridx = 0;
        for (int i = 0; i < pos; i ++) constraints.gridx++;
        main.add(toAdd, constraints);
    }

    void makeConditionPane(){
        choices = new JPanel(formation);
        choices.setBackground(Color.WHITE);
        choices.setPreferredSize(new Dimension(350, 800));
    }

    void makeCenterPane(){
        center = new JPanel(new BorderLayout());
        center.setBackground(Color.WHITE);
        center.setPreferredSize(new Dimension(900, 800));
    }

    void makeBackgroundPane(){
        background = new JPanel(formation);
        background.setBackground(Color.WHITE);
        background.setPreferredSize(new Dimension(350, 800));
    }

    void makeQuestionsPane(){
        questions = new JPanel(formation);
        questions.setBackground(Color.WHITE);
    }

    void fillCondition(String type, int options, String name, String[] choices, int position){
        GridBagConstraints checkConstraints = new GridBagConstraints();
        JPanel choice = new JPanel();
        choice.setBackground(Color.WHITE);
        JPanel content = new JPanel(formation);
        content.setBackground(Color.WHITE);
        JLabel id = new JLabel(name+":");
        switch (type) {
            case "Radio":
                JRadioButton[] array = new JRadioButton[options];
                for (int i = 0; i < options; i++) {
                    String radioName = choices[i];
                    array[i] = new JRadioButton(radioName);
                    array[i].setBackground(Color.WHITE);
                    array[i].setActionCommand(radioName);
                    array[i].setName(radioName);

                }
                ButtonGroup buttons = new ButtonGroup();
                for (int i = 0; i < options; i++) {
                    buttons.add(array[i]);
                    content.add(array[i]);
                }
                radiobuttons.put(name, buttons);
                break;
            case "Check":
                checkConstraints.gridy = 0;
                JCheckBox[] effects = new JCheckBox[options];
                for (int i = 0; i < options; i++) {
                    final String checkName = choices[i];
                    effects[i] = new JCheckBox(checkName);
                    effects[i].setName(checkName);
                    effects[i].setBackground(Color.WHITE);
                }

                checkboxes.put(name, effects);
                for (int i = 0; i < options; i++) {
                    content.add(effects[i], checkConstraints);
                    checkConstraints.gridy++;
                }
                break;
            case "State":
                content.add(new JLabel(choices[0]));
                break;
        }
        constraints.gridx = 0;
        constraints.gridy = position;
        constraints.fill = GridBagConstraints.VERTICAL;
        choice.add(id);
        choice.add(content);
        this.choices.add(choice, constraints);
        addToMain(0, this.choices);
    }

    void showPicture(String path){
        try {
            BufferedImage pic = ImageIO.read(new File(path));
            JLabel image = new JLabel(new ImageIcon(pic));
            image.setBackground(Color.WHITE);
            center.add(image, BorderLayout.CENTER);
            addToMain(1, center);
        } catch (IOException e) {e.printStackTrace();}
    }

    void showOpenQuestion(String input, int pos, boolean number){
        JPanel question = new JPanel(new BorderLayout());
        constraints.gridx = pos;
        JTextArea answer = new JTextArea("Type here", 1, 10);
        answer.setBorder(BorderFactory.createLineBorder(Color.black));
        input = input + ":";
        JLabel name = new JLabel(input);
        question.add(name, BorderLayout.NORTH);
        question.add(answer, BorderLayout.CENTER);
        JButton button = new JButton("Store");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (button.getText().equals("Stored")) {
                    JOptionPane.showMessageDialog(null, "Already stored.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BufferedWriter writer;
                try {
                    writer = new BufferedWriter(new FileWriter("./Output.txt", false));
                    answer.write(writer);
                    writer.close();
                    File file = new File("./Output.txt");
                    Scanner scan = new Scanner(file);
                    String answer = scan.nextLine();
                    scan.close();
                    if (number) {
                        if (!isDouble(answer)) {
                            JOptionPane.showMessageDialog(null, "Not a number, re-type and press store again once", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    button.setText("Stored");
                    openanswers.add(answer);
                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        question.add(button, BorderLayout.SOUTH);
        constraints.gridy = 0;
        questions.add(question, constraints);
        center.add(questions, BorderLayout.SOUTH);
        addToMain(0, center);
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    void showBackground(String text){
        console = setShowText("Background", false, 350, 800);
        background.add(console);
        System.out.println(text);
        button = submit();
        constraints.ipadx = 5;
        constraints.gridy = 1;
        constraints.gridx = 0;
        background.add(button, constraints);
        addToMain(2, background);
    }

    void showDRAnswers(String[] users, String[] names, Object[][] data){
        JPanel answers = new JPanel(formation);
        GridBagConstraints cons = new GridBagConstraints();
        cons.insets = new Insets(0, 5, 0, 5);
        constraints.gridy = 0;
        try {
            Scanner scan = new Scanner(new File("./Resources/Dose Response/Feedback.txt"));
            String feedback = scan.useDelimiter("\\A").next();
            scan.close();
            JTextArea info = setShowText("Feedback", false, 650, 200);
            System.out.println(feedback);
            background.add(info, constraints);
        } catch (FileNotFoundException e) {e.printStackTrace();}

        constraints.gridy = 1;
        for (int i = 0; i < users.length; i++) {
            JPanel answer = new JPanel(formation);
            JLabel name = new JLabel(users[i]  + " in mM");
            for (int j = 0; j < names.length; j++) {
                JLabel section = new JLabel(names[j]);
                JLabel input = new JLabel(data[i][j].toString());
                cons.gridy = 0;
                answer.add(section, cons);
                cons.gridy = 1;
                answer.add(input, cons);
            }

            cons.gridy = 0;
            answers.add(name, cons);
            cons.gridy = 1;
            answers.add(answer, cons);
            background.add(answers, constraints);

        }
        button = close();
        constraints.gridy = 3;
        background.add(button, constraints);
        addToMain(2, background);
    }

    void showGrade(int counter, int numberOfMistakes) {
        JTextArea grade = setShowText("Mistakes", true, 180, 100);
        System.out.printf("Number of mistakes made: %d out of %d.\n", counter, numberOfMistakes);
        background.add(grade);
    }

    void showWindow() {
        this.window.setVisible(true);
        this.window.pack();
    }

    void presentTable(String[] columns, Object[][] data, int[] widths) {
        JTable table = new JTable(data, columns);

        setTableSize(table, widths);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setFillsViewportHeight(true);
        table.setEnabled(false);
        JScrollPane tableHolder = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tableHolder.setPreferredSize(new Dimension(700, 135));
        tableHolder.setMinimumSize(new Dimension(700, 150));
        center.add(tableHolder, BorderLayout.NORTH);
        addToMain(0, center);
    }

    void presentTables(String[] columns, Object[][][] data, String[] names, int[] widths){
        JPanel tables = new JPanel(formation);
        tables.setBackground(Color.WHITE);
        constraints.gridy = 0;
        for (int i = 0; i < data.length; i++) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setMinimumSize(new Dimension(700, 150));
            JTable table = new JTable(data[i], columns);
            setTableSize(table, widths);
            table.setEnabled(false);
            JScrollPane tableHolder = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            panel.add(tableHolder, BorderLayout.CENTER);
            panel.add(new JLabel(names[i]), BorderLayout.NORTH);
            tables.add(panel, constraints);
            constraints.gridy++;
        }
        center.add(tables, BorderLayout.CENTER);
        addToMain(1, center);
    }

    private void setTableSize(JTable table, int[] widths){
        TableColumn column;
        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
        }
    }

    private JTextArea setShowText(String type, boolean bold, int xSize, int ySize){
        console = new JTextArea(" - " + type + " - \n");
        String font = console.getFont().getFontName();
        console.setSize(xSize, ySize);
        console.setLineWrap(true);
        console.setEditable(false);
        if (bold) {
            console.setFont(new Font(font,  Font.BOLD, 12));
            console.setBorder(BorderFactory.createLineBorder(Color.black));
        }
        PrintStream consoleOut = new PrintStream(new CustomOutputStream(console));
        System.setOut(consoleOut);
        return console;
    }

    DRTable getDRAnswer() {
        DRTable answer = new DRTable();
        for (Map.Entry<String, JCheckBox[]> entry : checkboxes.entrySet()) {
            for (JCheckBox checkBox : entry.getValue()) {
                if (checkBox.isSelected()) answer.getEffectByName(checkBox.getName()).setStatus(true);
            }
        }

        for (Map.Entry<String, ButtonGroup> radios : radiobuttons.entrySet()) {
            String result = "";
            Enumeration<AbstractButton> enumer = radios.getValue().getElements();
            while (enumer.hasMoreElements()) {
                AbstractButton radio = enumer.nextElement();
                if (radio.isSelected()) {
                    result = radio.getName();
                }
            }
            switch (result){
                case "Alive":
                    answer.setResult(DRTable.Result.ALIVE);
                    break;
                case "Affected":
                    answer.setResult(DRTable.Result.AFFECTED);
                    break;
                case "Dead":
                    answer.setResult(DRTable.Result.DEAD);
                    break;
            }
        }
        return answer;
    }

}
