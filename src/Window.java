import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Window extends JFrame {
    private JFrame window;
    private JPanel main, choices, background, center, question;
    private JButton button;
    private JTextArea console;
    private GridBagConstraints constraints;
    private GridBagLayout formation;

    HashMap<String, JCheckBox[]> checkboxes;
    HashMap<String, ButtonGroup> radiobuttons;
    volatile boolean proceed = false;

    Window(GridBagLayout formation){
        this.formation = formation;
    }

    void setup(){
        this.window = new JFrame("Homework");
        this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.window.setLocation(100, 50);
        this.window.setSize(1600, 900);
        this.main = new JPanel(formation);
        this.main.setBackground(Color.WHITE);
        this.main.setPreferredSize(new Dimension(1200, 800));
        this.constraints = new GridBagConstraints();
        this.constraints.insets = new Insets(20, 20, 20, 20);
        JScrollPane scrollable = new JScrollPane(this.main, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.window.add(scrollable);
        checkboxes = new HashMap<>();
        radiobuttons = new HashMap<>();
        proceed = false;
    }

    JButton reset(){
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

    JButton submit(){
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

    public void showIntroduction(String introduction){
        setup();
        console = setShowText("Introduction", 800, 400);
        main.add(console, constraints);
        System.out.println(introduction);
        constraints.gridy = 1;
        button = reset();
        main.add(button, constraints);
        showWindow();
    }

    public void addToMain(int pos, JPanel toAdd){
        constraints.gridy = 0;
        constraints.gridx = 0;
        for (int i = 0; i < pos; i ++) constraints.gridx++;
        main.add(toAdd, constraints);
    }

    public void makeConditionPane(){
        choices = new JPanel(formation);
        choices.setBackground(Color.WHITE);
        this.choices.setPreferredSize(new Dimension(350, 800));
    }

    public void fillCondition(String type, int options, String name, String[] choices, int position){
        GridBagConstraints checkConstraints = new GridBagConstraints();
        JPanel choice = new JPanel();
        choice.setBackground(Color.WHITE);
        JPanel content = new JPanel(formation);
        content.setBackground(Color.WHITE);
        JLabel id = new JLabel(name+":");
        if (type.equals("Radio")) {
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
        }
        else if (type.equals("Check")) {
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
        }
        else if (type.equals("State")){
            content.add(new JLabel(choices[0]+":"));
        }
        constraints.gridx = 0;
        constraints.gridy = position;
        constraints.fill = GridBagConstraints.VERTICAL;
        choice.add(id);
        choice.add(content);
        this.choices.add(choice, constraints);
        addToMain(0, this.choices);
    }

    public void showPicture(String path){
        try {
            BufferedImage pic = ImageIO.read(new File(path));
            JLabel image = new JLabel(new ImageIcon(pic));
            image.setBackground(Color.WHITE);
            center = new JPanel(formation);
            center.setBackground(Color.WHITE);
            center.add(image, constraints);
            addToMain(1, center);
        } catch (IOException e) {e.printStackTrace();}
    }

    public void showOpenQuestion(String question, int pos){
        /*POSITIONING BETTER*/
        constraints.gridy = pos;
        this.question = new JPanel(formation);
        JTextArea answer = new JTextArea("Answer:");
        JLabel name = new JLabel(question);
        this.question.add(answer, constraints);
        this.question.add(name, constraints);
        addToMain(0, this.question);
    }

    public void showBackground(String text){
        background = new JPanel(formation);
        background.setBackground(Color.WHITE);
        console = setShowText("Background", 350, 800);
        background.add(console);
        System.out.println(text);
        button = submit();
        constraints.ipadx = 5;
        constraints.gridy = 1;
        constraints.gridx = 0;
        background.add(button, constraints);
        addToMain(2, background);
    }

    public void showWindow() {this.window.setVisible(true);}

    JTextArea setShowText(String type, int xSize, int ySize){
        console = new JTextArea(" - " + type + " - \n");
        console.setSize(xSize, ySize);
        console.setLineWrap(true);
        console.setEditable(false);
        PrintStream consoleOut = new PrintStream(new CustomOutputStream(console));
        System.setOut(consoleOut);
        return console;
    }

    public DRTable getDRAnswer() {
        DRTable fuck = new DRTable();
        for (Map.Entry<String, JCheckBox[]> swag : checkboxes.entrySet()) {
            for (JCheckBox yolo : swag.getValue()) {
                System.err.printf("Checkbox %s [%s]: %s\n", swag.getKey(), yolo.getName(), yolo.isSelected());
            }
        }

        for (Map.Entry<String, ButtonGroup> radios : radiobuttons.entrySet()) {
            String result = null;
            Enumeration<AbstractButton> enumer = radios.getValue().getElements();
            while (enumer.hasMoreElements()) {
                AbstractButton radio = enumer.nextElement();
                if (radio.isSelected())
                    result = radio.getName();
            }
            System.err.printf("Radiogroup %s: %s\n", radios.getKey(), result);
        }
        return null;
    }

}
