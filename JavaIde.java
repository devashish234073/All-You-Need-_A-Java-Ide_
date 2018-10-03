import java.awt.Frame;
import java.awt.Component;
import java.awt.Color;

import java.awt.Label;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Choice;
import java.awt.List;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;

public class JavaIde extends Frame {
    private static final String OUTFILE="outttttt.txt";
    private static final int HEIGHT=300;
    private static final int WIDTH=450;
    private Label lblNotif=new Label();
    private Label lblWorkingDir=new Label("Working Dir");
    private TextField txtWorkingDir=new TextField();
    private Button btnOpenWorkingDir=new Button("Open");
    private Label lblTomcatDir=new Label("Tomcat Dir");
    private TextField txtTomcatDir=new TextField();
    private Button btnOpenTomcatDir=new Button("Open");
    private Label lblProjName=new Label("Project Name");
    private TextField txtProjName=new TextField();
    private Choice choiceProjType=new Choice();
    private List lstProjNames=new List();
    private Button btnCreateOrOpen=new Button("Create/Open");
    private Button btnCompile=new Button("Compile");
    private Button btnStartTomcat=new Button("Start tomcat");
    private Button btnStopTomcat=new Button("Stop tomcat");
    private TextField txtFileName=new TextField();
    private Button btnAddNewFile=new Button("+");
    private List lstFileList=new List();
    private static String WEBLIBCLASSPATH="";
    private ArrayList<String> filePathForFileListBox=new ArrayList<String>();
    
    private void initAll(){
        choiceProjType.add("standalone");
        choiceProjType.add("web");
        String tomcatDir=runCommand("echo %CATALINA_HOME%",null,true);
        txtTomcatDir.setText(tomcatDir);
        tomcatDir=txtTomcatDir.getText().trim();
        String workingDir=runCommand("echo %WORKING_DIR%",null,true);
        txtWorkingDir.setText(workingDir);
        txtWorkingDir.setEditable(false);
        txtTomcatDir.setEditable(false);     
        if(!tomcatDir.equals("%CATALINA_HOME%")){ 
            File dir=new File(tomcatDir+"\\lib\\");
            if(dir.exists()){
                File[] fileList=dir.listFiles();
                for(int i=0;i<fileList.length;i++){ 
                    File f=fileList[i];
                    if(WEBLIBCLASSPATH.equals("")){
                        WEBLIBCLASSPATH+=tomcatDir+"\\lib\\"+f.getName();
                    } else {
                        WEBLIBCLASSPATH+=";"+tomcatDir+"\\lib\\"+f.getName();
                    }
                }
            } else {
                System.out.println(tomcatDir+"\\lib\\ is not a valid folder!");
            }
        }
    }

    private void notifyy(Object msg){
        notifyy(msg,null);
    }

    private void notifyy(Object msg,Component comp){
        Color clrB=null;
        Color clrF=null;
        try{
            lblNotif.setText(""+msg);
            if(comp!=null){
                clrB=comp.getBackground();
                clrF=comp.getForeground();
                comp.setBackground(Color.RED);
                comp.setForeground(Color.WHITE);
            }
            Thread.sleep(2500);
            lblNotif.setText("");
        } catch(InterruptedException ie){
            println(ie);
        } finally {
            if(comp!=null && clrF!=null && clrB!=null){
                comp.setBackground(clrB);
                comp.setForeground(clrF);
                comp.requestFocus();
            }
        }
    }

    private void positionAll(){
        addAtPos(lblNotif,10,30,420,20);
        addAtPos(lblWorkingDir,30,60,100,20);
        addAtPos(txtWorkingDir,130,60,200,20);
        addAtPos(btnOpenWorkingDir,330,60,80,20);
        addAtPos(lblTomcatDir,30,80,100,20);
        addAtPos(txtTomcatDir,130,80,200,20);
        addAtPos(btnOpenTomcatDir,330,80,80,20);
        addAtPos(lblProjName,30,110,100,20);
        addAtPos(txtProjName,130,110,120,20);
        addAtPos(choiceProjType,250,110,80,20);
        addAtPos(btnCreateOrOpen,330,110,80,20);
        addAtPos(lstProjNames,130,130,200,60);
        addAtPos(btnCompile,330,130,80,20);
        addAtPos(btnStartTomcat,330,150,80,20);
        addAtPos(btnStopTomcat,330,170,80,20);
        addAtPos(txtFileName,30,130,70,20);
        addAtPos(btnAddNewFile,100,130,20,20);
        addAtPos(lstFileList,30,150,90,130);
    }    

    private void styleAll(){
        lblNotif.setForeground(Color.BLUE);        
    }

    private String getFormat(String fName){
        String[] fNameSplit=fName.trim().replace(".","\"\"\"").split("\"\"\"");
        if(fNameSplit.length>1){
            return fNameSplit[fNameSplit.length-1].toLowerCase();   
        }
        return "";
    }

    private void addAllListeners(){
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                System.exit(2);
            }
        });
        btnAddNewFile.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                String projName=txtProjName.getText().trim();
                String dirStndaln=txtWorkingDir.getText().trim();
                String dirWeb=txtTomcatDir.getText().trim()+"\\webapps";
                String fName=txtFileName.getText();
                if(fName.equals("")){
                    notifyy("Enter the fileName to create.",txtFileName);
                    return;
                }
                if(projName.equals("")){
                    notifyy("Enter a project name first",txtProjName);
                    return;
                }
                String format=getFormat(fName);
                System.out.println("<<"+format+">>");
                if(format.equals("")){
                    notifyy("Unable to determine format from the file name.",txtFileName);
                    return;
                }
                String dirOfInterest="";
                if(format.equals("java")){
                    if(choiceProjType.getSelectedItem().equals("web")){
                        dirOfInterest=dirWeb+"\\"+projName+"\\WEB-INF\\src";
                        runCommand("echo public class "+projName+"{>>"+fName,dirOfInterest,false);
                        runCommand("echo }>>"+fName,dirOfInterest,false);
                    } else {
                        dirOfInterest=dirStndaln+"\\"+projName+"\\src\\main\\java";
                        runCommand("echo public class "+projName+"{>>"+fName,dirOfInterest,false);
                        runCommand("echo }>>"+fName,dirOfInterest,false);
                    }
                    runCommand("notepad "+fName,dirOfInterest,false);
                } else {
                    if(choiceProjType.getSelectedItem().equals("web")){
                        dirOfInterest=dirWeb+"\\"+projName;
                    } else {
                        dirOfInterest=dirStndaln+"\\"+projName;
                    }
                    runCommand("notepad "+fName,dirOfInterest,false);
                }
            }
        });
        btnCompile.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                String projName=txtProjName.getText().trim();
                String dirStndaln=txtWorkingDir.getText().trim();
                String dirWeb=txtTomcatDir.getText().trim()+"\\webapps";
                String projDir="";
                if(projName.equals("")){
                    notifyy("Enter a project name first",txtProjName);
                    return;
                }
                String srcFolder="";
                if(choiceProjType.getSelectedItem().equals("web")){
                    projDir=dirWeb+"\\"+projName+"\\WEB-INF";
                    srcFolder=projDir+"\\src";
                    runCommand("javac -Xstdout "+OUTFILE+" -cp "+WEBLIBCLASSPATH+" -d "+projDir+"\\classes *.java",srcFolder,false);
                } else {
                    projDir=dirStndaln+"\\"+projName;
                    srcFolder=projDir+"\\src\\main\\java";
                    runCommand("javac -Xstdout "+OUTFILE+" -d "+projDir+"\\classes *.java",srcFolder,false);
                }
                String data=readFile(srcFolder+"\\"+OUTFILE);
                new Popup("compile output",data);
                runCommand("del "+srcFolder+"\\"+OUTFILE,null);
            }
        });
        btnCreateOrOpen.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                String projName=txtProjName.getText().trim();
                String dirStndaln=txtWorkingDir.getText().trim();
                String dirWeb=txtTomcatDir.getText().trim()+"\\webapps";
                if(projName.equals("")){
                    notifyy("Enter a project name first",txtProjName);
                    return;
                }
                if(choiceProjType.getSelectedItem().equals("standalone")){
                    File dir=new File(dirStndaln+"\\"+projName);
                    if(!dir.exists()){
                        runCommand("mkdir "+projName,dirStndaln);
                        runCommand("mkdir src",dirStndaln+"\\"+projName);
                        runCommand("mkdir classes",dirStndaln+"\\"+projName);
                        runCommand("mkdir main",dirStndaln+"\\"+projName+"\\src");
                        runCommand("mkdir java",dirStndaln+"\\"+projName+"\\src\\main");
                    }
                    openDir(dirStndaln+"\\"+projName);
                } else {
                    File dir=new File(dirWeb+"\\"+projName);
                    if(!dir.exists()){
                        runCommand("mkdir "+projName,dirWeb);
                        runCommand("mkdir META-INF",dirWeb+"\\"+projName);
                        runCommand("mkdir WEB-INF",dirWeb+"\\"+projName);
                        runCommand("mkdir src",dirWeb+"\\"+projName+"\\WEB-INF");
                        runCommand("mkdir lib",dirWeb+"\\"+projName+"\\WEB-INF");
                        runCommand("mkdir classes",dirWeb+"\\"+projName+"\\WEB-INF");
                    }
                    openDir(dirWeb+"\\"+projName);
                }
            }
        });
        btnOpenWorkingDir.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                String dirStr=txtWorkingDir.getText().trim();
                if(dirStr.equals("")){
                    notifyy("Enter a working directory first",txtWorkingDir);
                    return;
                }
                openDir(dirStr);
            }
        });
        btnOpenTomcatDir.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                String dirStr=txtTomcatDir.getText().trim();
                if(dirStr.equals("")){
                    notifyy("Enter a tomcat directory first",txtTomcatDir);
                    return;
                }
                openDir(dirStr);
            }
        });
        btnStartTomcat.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                runCommand("catalina start",null);
            }
        });
        btnStopTomcat.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                runCommand("catalina stop",null);
            }
        });
        choiceProjType.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent ie){
                if(choiceProjType.getSelectedItem().equals("web")){
                    loadWebProjs();
                } else {
                    loadStandaloneProjs();
                }
            }
        });
        lstProjNames.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                txtProjName.setText(lstProjNames.getSelectedItem());
                loadFiles();
            }
        });
        lstFileList.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                int indx=lstFileList.getSelectedIndex();
                if(indx>-1){
                    runCommand("notepad "+filePathForFileListBox.get(indx),null);
                }
            }
        });
    }

    private void loadFiles(){
        filePathForFileListBox=new ArrayList<String>();
        lstFileList.removeAll();
        String outerDir="";
        String srcDir="";
        String projName=txtProjName.getText().trim();
        if(projName.equals("")){
            notifyy("Project name can't be empty",txtProjName);
            return;
        }
        if(choiceProjType.getSelectedItem().equals("web")){
            outerDir=txtTomcatDir.getText().trim()+"\\webapps\\"+projName;
            srcDir=txtTomcatDir.getText().trim()+"\\webapps\\"+projName+"\\WEB-INF\\src";
        } else {
            outerDir=txtWorkingDir.getText().trim()+"\\"+projName;
            srcDir=txtWorkingDir.getText().trim()+"\\"+projName+"\\src\\main\\java";
        }
        File dir=new File(outerDir);
        if(!dir.exists()){
            notifyy("invalid folder '"+outerDir+"'",null);
            return;
        }
        File[] fs=dir.listFiles();
        for(int i=0;i<fs.length;i++){
            File f=fs[i];
            if(!f.isDirectory()){
                lstFileList.add(f.getName());
                filePathForFileListBox.add(outerDir+"\\"+f.getName());
            }
        }
        dir=new File(srcDir);
        if(!dir.exists()){
            notifyy("invalid folder '"+srcDir+"'",null);
            return;
        }
        fs=dir.listFiles();
        for(int i=0;i<fs.length;i++){
            File f=fs[i];
            if(!f.isDirectory()){
                lstFileList.add(f.getName());
                filePathForFileListBox.add(srcDir+"\\"+f.getName());
            }
        }
    }

    private void loadStandaloneProjs(){
        lstProjNames.removeAll();
        String dirStr=txtWorkingDir.getText().trim();
        if(!dirExists(dirStr)){
            return;
        }
        File dir=new File(dirStr);
        File[] files=dir.listFiles();
        for(int i=0;i<files.length;i++){
            File file=files[i];
            if(!file.isDirectory()){
                continue;
            }
            File dirInner1=new File(dirStr+"\\"+file.getName()+"\\src");
            File dirInner2=new File(dirStr+"\\"+file.getName()+"\\classes");
            if(dirInner1.exists() && dirInner2.exists()){
                lstProjNames.add(file.getName());
            }
        }
    }

    private void loadWebProjs(){
        lstProjNames.removeAll();
        String dirStr=txtTomcatDir.getText().trim()+"\\webapps";
        if(!dirExists(dirStr)){
            return;
        }
        File dir=new File(dirStr);
        File[] files=dir.listFiles();
        for(int i=0;i<files.length;i++){
            File file=files[i];
            if(!file.isDirectory()){
                continue;
            }
            File dirInner1=new File(dirStr+"\\"+file.getName()+"\\META-INF");
            File dirInner2=new File(dirStr+"\\"+file.getName()+"\\WEB-INF");
            if(dirInner1.exists() && dirInner2.exists()){
                lstProjNames.add(file.getName());
            }
        }
    }

    private boolean dirExists(String dirStr){
        File dir=new File(dirStr);
        if(dir.exists()){
            return true;
        }
        return false;
    }

    private void openDir(String dirStr){
        if(dirExists(dirStr)){
            runCommand("start \"\" .",dirStr);
        } else {
            notifyy("Folder '"+dirStr+"' doesn't exists");
        }
    }

    private void print(Object msg){
        System.out.print(msg);
    }

    private void println(Object msg){
        System.out.println(msg);
    }

    private void addAtPos(Component cmp,int left,int top,int width,int height){
        cmp.setBounds(left,top,width,height);
        add(cmp);
    }

    private String runCommand(String cmnd,String dir){
        return runCommand(cmnd,dir,false);
    }

    private String runCommand(String cmnd,String dir,boolean compileToOutFile){
        String ret="";
        Process p=null;
        String xtra="";
        if(compileToOutFile){
            xtra=">"+OUTFILE;
        }
        try{
            if(dir!=null){
                p=Runtime.getRuntime().exec("cmd /c "+cmnd+xtra,null,new File(dir));
            } else {
                p=Runtime.getRuntime().exec("cmd /c "+cmnd+xtra);
            }
            p.waitFor();
            if(compileToOutFile){
                if(dir!=null){
                    ret=readFile(dir+"\\"+OUTFILE);
                    p=Runtime.getRuntime().exec("cmd /c del "+OUTFILE,null,new File(dir));
                } else {
                    ret=readFile(""+OUTFILE);
                    p=Runtime.getRuntime().exec("cmd /c del "+OUTFILE);
                }
                p.waitFor();
            }
        } catch(IOException ioe){
            println(ioe);            
        } catch(InterruptedException ie) {
            println(ie);
        }
        return ret;
    }

    private String readFile(String filePath){
        String ret="";
        try {
            InputStream is=new FileInputStream(new File(filePath));
            ret=readStream(is);
        } catch(IOException ioe){
            println(ioe);
        }
        return ret;
    }

    private String readStream(InputStream is) throws IOException {
        int c=0;
        String ret="";
        do{
            c=is.read();
            ret+=(char)c;
        } while(is.available()>0);
        is.close();
        return ret;
    }
    
    private JavaIde(String appName){
        super(appName);
        setSize(WIDTH,HEIGHT);
        initAll();
        setLayout(null);
        positionAll();
        styleAll();
        addAllListeners();
        setResizable(false);
        loadStandaloneProjs();
        setVisible(true);
        if(txtTomcatDir.getText().trim().equals("%CATALINA_HOME%")){
            notifyy("Please set ''CATALINA_HOME' environment variable");
        } else if(txtWorkingDir.getText().trim().equals("%WORKING_DIR%")){
            notifyy("Please set 'WORKING_DIR' environment variable");
        } else {
            notifyy("WELCOME");
        }
    }

    public static void main(String[] args){
        JavaIde ide=new JavaIde("AYN - All You Need                          (Devashish)");
    }

}