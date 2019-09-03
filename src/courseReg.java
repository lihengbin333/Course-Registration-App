import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class courseReg {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/course_registration";
    static final String USER = "root";
    static final String PASS = "password";
    //
    Statement stmt;
    Connection conn;
    JFrame frame;

    //stmt = conn.createStatement();


    public courseReg() throws Exception{
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        stmt = conn.createStatement();

        frame = new JFrame("Course Registration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(login(), BorderLayout.CENTER);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        //frame.pack();
        frame.setVisible(true);


    }

    public JPanel login() {
        JPanel panel1 = new JPanel();

        panel1.setLayout(null);

        //panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        /********* USERNAME **********/
        JLabel label1 = new JLabel("Username:");
        label1.setBounds(20, 180, 100, 10);
        label1.setHorizontalAlignment(JLabel.RIGHT);
        panel1.add(label1);
        JTextField tf1 = new JTextField();
        tf1.setBounds(150, 176, 200, 20);
        panel1.add(tf1);

        /********* PASSWORD **********/
        JLabel label2 = new JLabel("Password:");
        label2.setBounds(20, 220, 100, 10);
        label2.setHorizontalAlignment(JLabel.RIGHT);
        panel1.add(label2);
        JPasswordField tf2 = new JPasswordField();
        tf2.setBounds(150, 216, 200, 20);
        panel1.add(tf2);

        /******** SUBMIT **********/
        JButton submit = new JButton("Enter");
        submit.setBounds(275, 250, 75, 20);
        panel1.add(submit);
        JButton cancel = new JButton("Cancel");
        cancel.setBounds(190, 250, 75, 20);
        panel1.add(cancel);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usr = tf1.getText();
                char[] pw = tf2.getPassword();
                String pass = new String(pw);

                try{
                    JFrame f = new JFrame();
                    //JFrame frame=  new JFrame("Main Window");
                    if(isExist(usr,pass).next()){
                        if(isStudent(usr,pass)){
                            System.out.println("STUDENT");
                            students_window(usr,pass);
                        }else{
                            System.out.println("INSTRUCTOR");
                        }
                        //JOptionPane.showMessageDialog(f,"Existing User", "OK",JOptionPane.PLAIN_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(f,"Username or password doesn't match our record! Please try again!", "ERROR",JOptionPane.ERROR_MESSAGE);
                    }

                }catch (Exception ex){
                    System.out.println(ex);
                }
            }
        });
        /******** CANCEL **********/
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        /**************** REGISTER *******************/
        JButton register = new JButton("Register");
        register.setBounds(250, 275, 100, 20);
        panel1.add(register);

        register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                JFrame regframe = new JFrame("Registration");

                regframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                regframe.add(registration_window(), BorderLayout.CENTER);
                regframe.setSize(500, 500);
                regframe.setLocationRelativeTo(null);
                regframe.setVisible(true);
                regframe.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        regframe.dispose();
                        frame.setVisible(true);
                    }
                });


            }
        });

        return panel1;
    }
    /******************************** USERNAME IDENTUFICATION *****************************************/
    public Boolean isStudent(String username, String password)throws Exception{
        Boolean ret = false;
        ResultSet resultSet = isExist(username,password);

        if(resultSet.next()){
            String sid = resultSet.getString("SID");
            if(!sid.isEmpty()){
                ret =true;
            }
        }

        return ret;
    }
    public ResultSet isExist(String username, String password)throws Exception{
        String query = "SELECT * FROM login WHERE username = ? AND password = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1,username);
        preparedStatement.setString(2,password);
        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet;
    }
    /*********************************** MAIN WINDOWS *******************************************/
    public void students_window(String username, String password){
        frame.dispose();
        JFrame student_window = new JFrame("Main Window");
        JPanel panel = new JPanel();
        panel.setLayout(null);

        panel.add(addLabel("Student Name: ",20,50,100,20,JLabel.RIGHT));
        String name = getName(studentINFO(IDnumber(username,password)));
        panel.add(addLabel(name,150,50,400,20,JLabel.LEFT));

        String sid = getSID(studentINFO(IDnumber(username,password)));
        panel.add(addLabel("SID: ",20,70,100,20,JLabel.RIGHT));
        panel.add(addLabel(sid,150,70,100,20,JLabel.LEFT));

        String major = getMajor(studentINFO(IDnumber(username,password)));
        panel.add(addLabel("Major: ",20,90,100,20,JLabel.RIGHT));
        panel.add(addLabel(major,150,90,400,20,JLabel.LEFT));

        panel.add(addLabel("--------------------------------------------------------------------------------------------------------------------",
                10,130,480,20,JLabel.LEFT));

        JButton course = addButton("My Courses",20,200,200,20);
        panel.add(course);
        course.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame courselist = new JFrame("My Courses");

                courselist.add(myCourse(sid),BorderLayout.CENTER);
                courselist.setSize(500,500);
                courselist.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                courselist.setLocationRelativeTo(null);
                courselist.setVisible(true);
                student_window.setVisible(false);
                courselist.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        student_window.setVisible(true);
                    }
                });
            }
        });

        JButton add_course = addButton("Add Courses", 20, 250,200,20);
        panel.add(add_course);
        add_course.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame add_course_frame = new JFrame();
                //JPanel add_course_panel = new JPanel();
                add_course_frame.add(add_course_panel(sid),BorderLayout.CENTER);
                add_course_frame.setSize(500,500);
                add_course_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                add_course_frame.setLocationRelativeTo(null);
                add_course_frame.setVisible(true);
                student_window.setVisible(false);
                add_course_frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        student_window.setVisible(true);
                    }
                });
            }
        });

        JButton drop_course = addButton("Drop Courses", 20,300,200,20);
        panel.add(drop_course);
        drop_course.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame drop_course_frame = new JFrame();
                JPanel drop_course_panel = new JPanel();
                drop_course_frame.add(drop_course_panel,BorderLayout.CENTER);
                drop_course_frame.setSize(500,500);
                drop_course_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                drop_course_frame.setLocationRelativeTo(null);
                drop_course_frame.setVisible(true);
                student_window.setVisible(false);
                drop_course_frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        student_window.setVisible(true);
                    }
                });
            }
        });

        student_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        student_window.add(panel,BorderLayout.CENTER);
        student_window.setSize(500,500);
        student_window.setLocationRelativeTo(null);
        student_window.setVisible(true);
        //return panel;
    }
    //public JPanel faculties_window(){}
    /************************************* COURSE PANELS ************************************************/
    public JPanel myCourse(String sid){
        JPanel panel = new JPanel();
        panel.setLayout(null);
        JLabel label1 = new JLabel("My Courses:");
        JLabel label2 = new JLabel("Description:");
        label1.setBounds(100,75,100,20);
        label2.setBounds(100,200,100,25);
        panel.add(label1);
        panel.add(label2);

        String description = "";
        String course="";
        String query = "SELECT courseID FROM course WHERE SID = ?";
        DefaultListModel listModel = new DefaultListModel();
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,sid);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                listModel.addElement(resultSet.getNString(1));
                course = course + resultSet.getNString(1)+" ";
            }
            System.out.println(course);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        String[] cl = course.split(" ");
        JList list = new JList(listModel);

        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setBounds(100,100,100,100);

        panel.add(listScroller);

        JTextArea textArea = new JTextArea(10,10);

        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(100,225,250,150);
        panel.add(scrollPane);

        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()){
                    String selected = list.getSelectedValue().toString();
                    System.out.println(selected);
                    String new_query = "SELECT * FROM courselist WHERE courseID = ?";
                    String description = "";

                    try{
                        PreparedStatement ps = conn.prepareStatement(new_query);
                        ps.setString(1,selected);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            description = rs.getNString("courseID") + "\n";
                            description += rs.getNString("courseName") + "\n";
                            description += getName(instructorINFO(rs.getNString("InstID"))) + "\n";
                            description += rs.getNString("location") + "\n";
                            description += rs.getTime("time").toString();
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    textArea.setText(description);

                }
            }
        });

        return panel;
    }
    public JPanel add_course_panel(String sid){
        String[] my_course = getMyCourse(sid);
        String[] course_list = getCourselist();
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.add(addLabel("By Course ID",70,80,100,20,JLabel.RIGHT));
        panel.add(addLabel("By Course List",70,150,100,20,JLabel.RIGHT));
        JTextField tf = new JTextField();
        tf.setBounds(70,115,250,25);
        panel.add(tf);
        JComboBox comboBox = new JComboBox(course_list);
        comboBox.setBounds(70,175,200,25);
        comboBox.setSelectedItem(null);
        panel.add(comboBox);
        JButton submit =  addButton("Submit", 150,300,200,20);
        panel.add(submit);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cid = tf.getText();
                if(cid.isEmpty()){
                    cid = (String)comboBox.getSelectedItem();
                }
            }
        });

        return panel;
    }

    /************************************ ADDING WINDOW COMPONENTS *************************************/
    public JLabel addLabel(String label, int x, int y, int width, int height, int HA){
        JLabel newLabel = new JLabel(label);
        newLabel.setBounds(x,y,width,height);
        newLabel.setHorizontalAlignment(HA);
        return  newLabel;
    }
    public JButton addButton(String button, int x, int y, int width, int height){
        JButton newButton = new JButton(button);
        newButton.setBounds(x,y,width,height);
        return newButton;
    }
    /*********************************** QUERY/UPDATE INFO *********************************************/
    public String[] getMyCourse(String sid){
        String course="";
        String query = "SELECT courseID FROM course WHERE SID = ?";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,sid);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                course = course + resultSet.getNString(1)+" ";
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return course.split(" ");
    }
    public String[] getCourselist(){
        String course="";
        String query = "SELECT courseID FROM courselist";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                course = course + resultSet.getNString(1)+" ";
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return course.split(" ");
    }
    public String IDnumber(String username, String password){
        String id = "";

        try {
            ResultSet resultSet = isExist(username, password);
            if(resultSet.next()) {
                id = resultSet.getString("SID");
                //System.out.println(id);
                if (id.isEmpty()) {
                    id = resultSet.getString("InstID");
                }
            }
        }catch (Exception e){System.out.println(e);}

        return id;
    }
    public void addCourse(String id, String courseid,String instid){
        String query = "INSERT INTO course VALUES(?,?,?)";
        PreparedStatement preparedStatement;
        try{
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,courseid);
            preparedStatement.setString(2,id);
            preparedStatement.setString(3,instid);
            preparedStatement.executeUpdate();
        }catch (Exception e){
            System.out.println(e);
        }

    }
    public void newCourse(String course, String instID){
        String query = "INSERT INTO courselist VALUES(?,?)";
        PreparedStatement preparedStatement;
        try{
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,course);
            preparedStatement.setString(2,instID);
            preparedStatement.executeUpdate();
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void dropCourse(String sid,String courseID){
        String query = "DELETE FROM course WHERE courseID = ? AND SID = ? ";
        PreparedStatement preparedStatement;
        try{
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,courseID);
            preparedStatement.setString(2,sid);
            preparedStatement.executeUpdate();
        }catch (Exception e){System.out.println(e);}
    }
    public void deleteCourse(String course,String instID){
        String query = "DELETE FROM courselist Where courseID = ? AND InstID = ?";
        PreparedStatement preparedStatement;
        try{
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,course);
            preparedStatement.setString(2,instID);
            preparedStatement.executeUpdate();
        }catch (Exception e){System.out.println(e);}
    }
    public ResultSet studentINFO(String id){
        String query = "SELECT * FROM student WHERE SID = ?";

        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try{
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,id);
            resultSet = preparedStatement.executeQuery();
            return resultSet;
        }catch (Exception e){System.out.println(e);}

        return null;
    }
    public ResultSet instructorINFO(String id){
        String query = "SELECT * FROM instructor WHERE InstID = ?";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try{
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1,id);
            resultSet = preparedStatement.executeQuery();
            return resultSet;
        }catch (Exception e){System.out.println(e);}
        return null;
    }
    public String getName(ResultSet rs){
        String name = "";

        try {
            if (rs.next()) {
                name = rs.getString("name");
            }
        }catch (Exception e){System.out.println(e);}

        return name;
    }
    public String getSID(ResultSet rs){
        String sid = "";
        try {
            if (rs.next()) {
                sid = rs.getString("SID");
            }
        }catch (Exception e){System.out.println(e);}
        return sid;
    }
    public String getMajor(ResultSet rs){
        String major = "";
        try {
            if (rs.next()) {
                major = rs.getString("major");
            }
        }catch (Exception e){System.out.println(e);}
        return major;
    }

    /***********************************************************************************************/

    public JPanel registration_window() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        JLabel l1 = new JLabel("Username");
        JLabel l2 = new JLabel("Password");
        JLabel l3 = new JLabel("Student ID");
        JLabel l4 = new JLabel("Instrutor ID");
        JLabel l5 = new JLabel("OR");
        l1.setHorizontalAlignment(JLabel.RIGHT);
        l2.setHorizontalAlignment(JLabel.RIGHT);
        l3.setHorizontalAlignment(JLabel.RIGHT);
        l4.setHorizontalAlignment(JLabel.RIGHT);
        l5.setHorizontalAlignment(JLabel.CENTER);
        l1.setBounds(20,100,100,20);
        l2.setBounds(20,130,100,20);
        l3.setBounds(20,170,100,20);
        l4.setBounds(20,210,100,20);
        l5.setBounds(175,190,50,20);
        panel.add(l1);
        panel.add(l2);
        panel.add(l3);
        panel.add(l4);
        panel.add(l5);

        JTextField tf1 = new JTextField();
        tf1.setBounds(150, 100, 200, 20);
        JTextField tf2 = new JTextField();
        tf2.setBounds(150, 130, 200, 20);
        JTextField tf3 = new JTextField();
        tf3.setBounds(150, 170, 200, 20);
        JTextField tf4 = new JTextField();
        tf4.setBounds(150, 210, 200, 20);
        panel.add(tf1);
        panel.add(tf2);
        panel.add(tf3);
        panel.add(tf4);

        JButton ok = new JButton("OK");
        ok.setBounds(275,250,75,20);
        panel.add(ok);
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String usr = tf1.getText();
                String psw = tf2.getText();
                String sid = tf3.getText();
                String instid = tf4.getText();
                String query = "INSERT INTO login VALUES(?,?,?,?);";
                PreparedStatement preparedStatement;
                try{
                    preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1,usr);
                    preparedStatement.setString(2,psw);
                    preparedStatement.setString(3,sid);
                    preparedStatement.setString(4,instid);
                    preparedStatement.executeUpdate();
                    JFrame f = new JFrame();
                    JOptionPane.showMessageDialog(f,"Successfully Registered!" ,"Complete",JOptionPane.PLAIN_MESSAGE);
                }catch (Exception ex){
                    System.out.println("ALREADY EXIST!");
                    JFrame f = new JFrame();
                    JOptionPane.showMessageDialog(f,"AlReady Exist!" ,"Error",JOptionPane.ERROR_MESSAGE);

                }

            }
        });


        return panel;

    }

    public static void main(String[] args) {
        try {
            //Statement stmt = null;
            //Class.forName(JDBC_DRIVER);
            //Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //stmt = conn.createStatement();
            courseReg cr = new courseReg();

            /******************************************
            String str = "select * from instructor";
            ResultSet rs = stmt.executeQuery(str);
            while(rs.next()){
                int id = rs.getInt("instID");
                String cid = rs.getString("courseID");
                String name = rs.getString("name");
                System.out.println(id+" "+cid+" "+name);
            }
             ******************************************/


        }catch (Exception e){
            System.out.println(e);
        }
        //JFrame frame = new JFrame("Course Registration");

    }
}
