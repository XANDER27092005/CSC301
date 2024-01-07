/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cbt.project;
import static cbt.project.login.username;
import static cbt.project.login.email;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.sql.Statement;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.Timer;

/** 
 *
 * @author USER
 */
public class TestInterface extends javax.swing.JFrame {
    String durations;
    int duration;
    Timer time;
    private int[] questionid;
    private String[] questions;
    private String[] optionA;
    private String[] optionB;
    private String[] optionC;
    private String[] optionD;
    private String[] answer;
    private String[] yourAnswer;
    private int noOfQuestions;
    public static String matno;
    String yourOption = "";
    int score = 0;
    int i = 0;
    /**
     * Creates new form TestInterface
     */
    
    public TestInterface() {
        initComponents();
        //loadQuestions("MichaelIloba");
        loadQuestions(username);
        getMatNo(username);
        System.out.println("Matno: "+matno);
        setLocationRelativeTo(null);
        time();
    }
    
    private void time(){
    try {
            Class.forName("com.mysql.cj.jdbc.Driver");                               //db name       //username //password
            java.sql.Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cbt", "root", "password123");
            Statement st = (Statement) con.createStatement();
            ResultSet rs = st.executeQuery("select *from time");//automatically selects the last value amongst all values
            while (rs.next()) {
                //jLabel2.setText(rs.getString(1));
                durations = rs.getString(1);
            }
        } catch (Exception e) {

        }
        
        duration = Integer.parseInt(durations);
        System.out.println("" + duration);
//        timer

        setLocationRelativeTo(this);

        time = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int hours = duration / 3600;
                int minutes = (duration % 3600) / 60;
                int seconds = duration % 60;

                String formattedTime = String.format("Time remaining: %02d:%02d:%02d%n", hours, minutes, seconds);
                jLabel1.setText(formattedTime);

                if (duration == 0) {
                    time.stop();
                    JOptionPane.showMessageDialog(null, "Timer finished!");
                    submit();

                }

                --duration;
            }
        });
        time.start();
    
    
    }
    private String getMatNo(String username) {
    matno = null;
    try {
        String selectQuery = "SELECT matno FROM studentrecords WHERE username = ?";
        Class.forName("com.mysql.cj.jdbc.Driver");
        String path = "jdbc:mysql://localhost:3306/cbt";
        String user = "root";
        String pass = "password123";

        try (Connection con = DriverManager.getConnection(path, user, pass);
             PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    matno = rs.getString("matno");
                } else {
                    // Handle the case where no result is found
                    System.out.println("No matno found for username: " + username);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(rootPane, "Error retrieving matno: " + e.getMessage());
    }
    return matno;
}
    private void loadQuestions(String username) {
    try {
        String selectQuery = "SELECT * FROM questionbank ORDER BY RAND() LIMIT ?";
        Class.forName("com.mysql.cj.jdbc.Driver");
        String path = "jdbc:mysql://localhost:3306/cbt";
        String user = "root";
        String pass = "password123";

        try (Connection con = DriverManager.getConnection(path, user, pass);
             PreparedStatement psNumOfQuestions = con.prepareStatement("SELECT noofquestions FROM studentrecords WHERE username = ?")) {

            psNumOfQuestions.setString(1, username);
            ResultSet rsNumOfQuestions = psNumOfQuestions.executeQuery();

            if (rsNumOfQuestions.next()) {
                noOfQuestions = rsNumOfQuestions.getInt("noofquestions");
            } else {
                // Handle the case where the user is not found
                noOfQuestions = 0;
            }
            System.out.println(noOfQuestions);

            // Load questions
            try (PreparedStatement ps = con.prepareStatement(selectQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                ps.setInt(1, noOfQuestions);
                try (ResultSet rs = ps.executeQuery()) {
                    List<Integer> questionIndices = new ArrayList<>();

                    // Populate question indices list
                    while (rs.next()) {
                        questionIndices.add(rs.getRow());
                    }

                    Collections.shuffle(questionIndices);
                    System.out.println("After shuffle, questionIndices: " + questionIndices);

                    // Rest of the code to process the shuffled indices
                    int[] loadedQuestionId = new int[questionIndices.size()];
                    String[] loadedQuestions = new String[questionIndices.size()];
                    String[] loadedOptionA = new String[questionIndices.size()];
                    String[] loadedOptionB = new String[questionIndices.size()];
                    String[] loadedOptionC = new String[questionIndices.size()];
                    String[] loadedOptionD = new String[questionIndices.size()];
                    String[] loadedAnswer = new String[questionIndices.size()];
                    String[] loadedYourAnswer = new String[questionIndices.size()];

                    int currentIndex = 0;
                    for (int shuffledIndex : questionIndices) {
                        System.out.println("Processing index: " + shuffledIndex);
                        // Move the cursor to the next row
                        if (rs.absolute(shuffledIndex)) {
                            // Process the current row
                            loadedQuestionId[currentIndex] = rs.getInt("questionId");
                            loadedQuestions[currentIndex] = rs.getString("questions");
                            loadedOptionA[currentIndex] = rs.getString("optionA");
                            loadedOptionB[currentIndex] = rs.getString("optionB");
                            loadedOptionC[currentIndex] = rs.getString("optionC");
                            loadedOptionD[currentIndex] = rs.getString("optionD");
                            loadedAnswer[currentIndex] = rs.getString("answer");
                            loadedYourAnswer[currentIndex] = "";

                            System.out.println("Question ID: " + loadedQuestionId[currentIndex]);
                            System.out.println("Question: " + loadedQuestions[currentIndex]);
                            System.out.println("Option A: " + loadedOptionA[currentIndex]);
                            System.out.println("Option B: " + loadedOptionB[currentIndex]);
                            System.out.println("Option C: " + loadedOptionC[currentIndex]);
                            System.out.println("Option D: " + loadedOptionD[currentIndex]);

                            currentIndex++;
                        } else {
                            // Handle the case when there are fewer rows than expected
                        }
                    }

                    questionid = loadedQuestionId;
                    questions = loadedQuestions;
                    optionA = loadedOptionA;
                    optionB = loadedOptionB;
                    optionC = loadedOptionC;
                    optionD = loadedOptionD;
                    answer = loadedAnswer;
                    yourAnswer = loadedYourAnswer;

                    // Display the first question in a JTextArea
                    if (loadedQuestions.length > 0) {
                        i = 0;
                        jTextArea1.setText((i+1) + ". " + loadedQuestions[0]);
                        jRadioButton1.setText(loadedOptionA[0]);
                        jRadioButton2.setText(loadedOptionB[0]);
                        jRadioButton3.setText(loadedOptionC[0]);
                        jRadioButton4.setText(loadedOptionD[0]);
                    } else {
                        jTextArea1.setText("No questions found.");
                    }
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(rootPane, "Error retrieving data: " + e.getMessage());
    }
}


  
    private void insertAnsweredQuestions(String matno) {
    try {
        String insertQuery = "INSERT INTO answeredquestions (question,optionA,optionB,optionC,optionD,answer,yourAnswer,matno) values (?,?,?,?,?,?,?,?)";
        Class.forName("com.mysql.cj.jdbc.Driver");
        String path = "jdbc:mysql://localhost:3306/cbt";
        String user = "root";
        String pass = "password123";

        try (Connection con = DriverManager.getConnection(path, user, pass);
             PreparedStatement ps = con.prepareStatement(insertQuery)) {

            for (int j = 0; j < questions.length; j++) {
                
                ps.setString(1, questions[j]);
                ps.setString(2, optionA[j]);
                ps.setString(3, optionB[j]);
                ps.setString(4, optionC[j]);
                ps.setString(5, optionD[j]);
                ps.setString(6, answer[j]);
                ps.setString(7, yourAnswer[j]);
                ps.setString(8, matno);
                
                ps.executeUpdate();
            }
            System.out.println("Answered questions inserted into the database.");
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(rootPane, "Error inserting answered questions: " + e.getMessage());
    }
}
    private int Score(String username) {
        try {
            String selectQuery = "SELECT score FROM studentrecords WHERE username = ?";
            Class.forName("com.mysql.cj.jdbc.Driver");
            String path = "jdbc:mysql://localhost:3306/cbt";
            String user = "root";
            String pass = "password123";

            Connection con = DriverManager.getConnection(path, user, pass);
            PreparedStatement ps = con.prepareStatement(selectQuery); 

                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getInt("score");
                } else {
                    // User not found, handle accordingly
                    return 0;
                }
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Handle the exception, e.g., show an error dialog
            return 0;
        }
    }
 private void putScore(String username) {
        try {
            String updateQuery = "UPDATE studentrecords SET score = ? WHERE username = ?";
            Class.forName("com.mysql.cj.jdbc.Driver");
            String path = "jdbc:mysql://localhost:3306/cbt";
            String user = "root";
            String pass = "password123";

            Connection con = DriverManager.getConnection(path, user, pass);
            PreparedStatement ps = con.prepareStatement(updateQuery); 

                ps.setString(1, username);
                ps.executeUpdate();
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Handle the exception, e.g., show an error dialog
        }
    }
 private void submit(){
     if(jRadioButton1.isSelected()){
    yourOption = jRadioButton1.getText();
    yourAnswer[i] = yourOption;
    }else if(jRadioButton2.isSelected()){
    yourOption = jRadioButton2.getText();
    yourAnswer[i] = yourOption;
    }else if(jRadioButton3.isSelected()){
    yourOption = jRadioButton3.getText();
    yourAnswer[i] = yourOption;
    }else if(jRadioButton4.isSelected()){
    yourOption = jRadioButton4.getText();
    yourAnswer[i] = yourOption;
    }
    String yanswer = "";
    String canswer = "";
    for(int i = 0; i<questions.length-1; i++){
    yanswer = yourAnswer[i];
    canswer = answer[i];
    if(canswer.equals(yanswer)){
     score++;
    }
            }
    
    JOptionPane.showMessageDialog(rootPane,"Your score is " + score);        
            String text ="";   
            for(int j = 0; j<yourAnswer.length; j++){
            text = text + "Your answer for number " + (j+1)+ " is "+yourAnswer[j]+"\n";
            jTextArea1.setText(text);

            //jTextArea1.setText("Your answer for number " + (j+1) + " is "+ yourAnswer[j]);
//            System.out.println("j = "+j);
//            System.out.println("Your answer for number " + j + " is " + yourAnswer[j]);
        }
            //jTextArea1.setText("Your Score is "+ score);
        //System.out.println(yourAnswer.length);
        //System.out.println(i);
            String subject = "SCORE";
            //String receiver = "xander27092005@gmail.com";
            String receiver = email;
            
            String body = "Congratulations on finishing the test" + "\n" + "Your Score is: " + score;
            String senderEmail = "xander27092005@gmail.com";
            // password generated by app and not actual password
            String senderPassword = "seyhijphyakaoocq";
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                }
            );
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
                message.setSubject(subject);
                message.setText(body);
                Transport.send(message);
                JOptionPane.showMessageDialog(rootPane, "Email sent");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(rootPane, e);
            }
      insertAnsweredQuestions(matno);
        //Score(username);
         try {
            String updateQuery = "UPDATE studentrecords SET score = ? WHERE username = ?";
            Class.forName("com.mysql.cj.jdbc.Driver");
            String path = "jdbc:mysql://localhost:3306/cbt";
            String user = "root";
            String pass = "password123";

            Connection con = DriverManager.getConnection(path, user, pass);
            PreparedStatement ps = con.prepareStatement(updateQuery); 
                ps.setInt(1, score);
                ps.setString(2, username);
                ps.executeUpdate();
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // Handle the exception, e.g., show an error dialog
        }
        
      Scripts sc = new Scripts();
                sc.show();
                dispose();
              
 }

  

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 255, 255));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("jRadioButton1");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("jRadioButton1");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("jRadioButton1");

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("jRadioButton1");

        jButton2.setText("Previous");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setText("Next");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setText("Submit");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Calculator");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel1.setText("Timer");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(57, 57, 57)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jRadioButton4)
                                .addComponent(jRadioButton2)
                                .addComponent(jRadioButton1)
                                .addComponent(jRadioButton3)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(23, 23, 23)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 507, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jButton2)
                                    .addGap(60, 60, 60)
                                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton3)
                                    .addGap(64, 64, 64)
                                    .addComponent(jButton4))))))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1)
                .addGap(33, 33, 33)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton1)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton2)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton3)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton4))
                .addGap(34, 34, 34))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
if(i==0){
    JOptionPane.showMessageDialog(rootPane, "This is the first question");
}else{
    if(jRadioButton1.isSelected()){
    yourOption = jRadioButton1.getText();
    yourAnswer[i] = yourOption;
    }else if(jRadioButton2.isSelected()){
    yourOption = jRadioButton2.getText();
    yourAnswer[i] = yourOption;
    }else if(jRadioButton3.isSelected()){
    yourOption = jRadioButton3.getText();
    yourAnswer[i] = yourOption;
    }else if(jRadioButton4.isSelected()){
    yourOption = jRadioButton4.getText();
    yourAnswer[i] = yourOption;
    }else{
        yourAnswer[i] = "";
    }
    
        --i;
        buttonGroup1.clearSelection();
        
        jTextArea1.setText((i+1) + ". " + questions[i]);
        jRadioButton1.setText(optionA[i]);
        jRadioButton2.setText(optionB[i]);
        jRadioButton3.setText(optionC[i]);
        jRadioButton4.setText(optionD[i]);
        yourOption = yourAnswer[i];
        if(yourOption.equals(jRadioButton1.getText())){
            jRadioButton1.setSelected(true);
        }else if(yourOption.equals(jRadioButton2.getText())){
            jRadioButton2.setSelected(true);
        }else if(yourOption.equals(jRadioButton3.getText())){
            jRadioButton3.setSelected(true);
        }else if(yourOption.equals(jRadioButton4.getText())){
            jRadioButton4.setSelected(true);
        }
        
// TODO add your handling code here:
}       
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
if (i < questions.length) {
    if (jRadioButton1.isSelected()) {
        yourOption = jRadioButton1.getText();
        yourAnswer[i] = yourOption;
    } else if (jRadioButton2.isSelected()) {
        yourOption = jRadioButton2.getText();
        yourAnswer[i] = yourOption;
    } else if (jRadioButton3.isSelected()) {
        yourOption = jRadioButton3.getText();
        yourAnswer[i] = yourOption;
    } else if (jRadioButton4.isSelected()) {
        yourOption = jRadioButton4.getText();
        yourAnswer[i] = yourOption;
    } else {
        yourAnswer[i] = "";
    }
    
    if (i < questions.length - 1) {
        // Increment i only if it's less than questions.length - 1
        i++;
        
        // Update the UI with the next question
        jTextArea1.setText((i+1) + ". " + questions[i]);
        jRadioButton1.setText(optionA[i]);
        jRadioButton2.setText(optionB[i]);
        jRadioButton3.setText(optionC[i]);
        jRadioButton4.setText(optionD[i]);
        buttonGroup1.clearSelection();
        yourOption = yourAnswer[i];
        if (yourOption.equals(jRadioButton1.getText())) {
            jRadioButton1.setSelected(true);
        } else if (yourOption.equals(jRadioButton2.getText())) {
            jRadioButton2.setSelected(true);
        } else if (yourOption.equals(jRadioButton3.getText())) {
            jRadioButton3.setSelected(true);
        } else if (yourOption.equals(jRadioButton4.getText())) {
            jRadioButton4.setSelected(true);
        }
    } else {
        JOptionPane.showMessageDialog(rootPane, "This is the last question");
        // TODO add your handling code here:
    }
}

    
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
submit();
// if(jRadioButton1.isSelected()){
//    yourOption = jRadioButton1.getText();
//    yourAnswer[i] = yourOption;
//    }else if(jRadioButton2.isSelected()){
//    yourOption = jRadioButton2.getText();
//    yourAnswer[i] = yourOption;
//    }else if(jRadioButton3.isSelected()){
//    yourOption = jRadioButton3.getText();
//    yourAnswer[i] = yourOption;
//    }else if(jRadioButton4.isSelected()){
//    yourOption = jRadioButton4.getText();
//    yourAnswer[i] = yourOption;
//    }
//    String yanswer = "";
//    String canswer = "";
//    for(int i = 0; i<questions.length-1; i++){
//    yanswer = yourAnswer[i];
//    canswer = answer[i];
//    if(canswer.equals(yanswer)){
//     score++;
//    }
//            }
//    
//    JOptionPane.showMessageDialog(rootPane,"Your score is " + score);        
//            String text ="";   
//            for(int j = 0; j<yourAnswer.length; j++){
//            text = text + "Your answer for number " + (j+1)+ " is "+yourAnswer[j]+"\n";
//            jTextArea1.setText(text);
//
//            //jTextArea1.setText("Your answer for number " + (j+1) + " is "+ yourAnswer[j]);
////            System.out.println("j = "+j);
////            System.out.println("Your answer for number " + j + " is " + yourAnswer[j]);
//        }
//            //jTextArea1.setText("Your Score is "+ score);
//        //System.out.println(yourAnswer.length);
//        //System.out.println(i);
//            String subject = "OTP";
//            //String receiver = "xander27092005@gmail.com";
//            String receiver = email;
//            
//            String body = "Congratulations on finishing the test" + "\n" + "Your Score is: " + score;
//            String senderEmail = "xander27092005@gmail.com";
//            // password generated by app and not actual password
//            String senderPassword = "seyhijphyakaoocq";
//            Properties props = new Properties();
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.smtp.host", "smtp.gmail.com");
//            props.put("mail.smtp.port", "587");
//
//            Session session = Session.getInstance(props,
//                new javax.mail.Authenticator() {
//                    protected PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(senderEmail, senderPassword);
//                    }
//                }
//            );
//            try {
//                Message message = new MimeMessage(session);
//                message.setFrom(new InternetAddress(senderEmail));
//                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
//                message.setSubject(subject);
//                message.setText(body);
//                Transport.send(message);
//                JOptionPane.showMessageDialog(rootPane, "Email sent");
//
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(rootPane, e);
//            }
//      insertAnsweredQuestions(matno);
//        //Score(username);
//         try {
//            String updateQuery = "UPDATE studentrecords SET score = ? WHERE username = ?";
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            String path = "jdbc:mysql://localhost:3306/cbt";
//            String user = "root";
//            String pass = "password123";
//
//            Connection con = DriverManager.getConnection(path, user, pass);
//            PreparedStatement ps = con.prepareStatement(updateQuery); 
//                ps.setInt(1, score);
//                ps.setString(2, username);
//                ps.executeUpdate();
//            
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//            // Handle the exception, e.g., show an error dialog
//        }
//        
//      Scripts sc = new Scripts();
//                sc.show();
//                dispose();
//              


     
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
             // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TestInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestInterface().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
