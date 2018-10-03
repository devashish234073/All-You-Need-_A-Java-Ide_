import java.awt.Frame;
import java.awt.TextArea;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Popup extends Frame {
    private TextArea ta=new TextArea();
    private Popup me=this;

    private void positionAll(){
        addAtPos(ta,10,30,290,370);
    }    

    private void addAllListeners(){
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                me.setVisible(false);
            }
        });
    }

    private void addAtPos(Component cmp,int left,int top,int width,int height){
        cmp.setBounds(left,top,width,height);
        add(cmp);
    }
    
    Popup(String appName,String content){
        super(appName);
        setSize(300,400);
        setLayout(null);
        positionAll();
        addAllListeners();
        setResizable(false);
        setVisible(true);
        ta.setText(content);
    }
}