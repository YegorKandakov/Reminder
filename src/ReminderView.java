
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ReminderView extends JFrame {
	
	BoxLayout bl;
	JLabel wakeLabel;
	JComboBox<String> minBox;
	String[] boxItems = {
		" ", "1", "3", "5", "7", "10", "12", "15", "20",
		"30", "45", "60", "90", "120", "150", "180",
		"240", "300", "360", "420", "480", "540"
	};
	JLabel minutesLabel;
	JButton okButton;
	JButton cancelButton;
	JLabel timerLabel;
	JLabel todoLabel;
	JTextArea todoArea;
	JPanel row1;
	JPanel row2;
	JPanel row3;
	JPanel row4;
	
	public ReminderView() {
		super("Reminder");
		final ReminderEngine re = new ReminderEngine(this);
		Container contentPane = getContentPane();
		setContentPane(contentPane);
		bl = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
		contentPane.setLayout(bl);
		contentPane.setBackground(Color.LIGHT_GRAY);
		
		row1 = new JPanel();
		row1.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		row1.setBackground(Color.LIGHT_GRAY);
		contentPane.add(row1);
		row2 = new JPanel();
		row2.setBackground(Color.LIGHT_GRAY);
		contentPane.add(row2);
		row3 = new JPanel();
		row3.setBackground(Color.LIGHT_GRAY);
		contentPane.add(row3);
		row4 = new JPanel();
		row4.setBackground(Color.LIGHT_GRAY);
		contentPane.add(row4);
		
		wakeLabel = new JLabel("Wake up in:");
		row1.add(wakeLabel);
		minBox = new JComboBox<String>(boxItems);
		row1.add(minBox);
		minutesLabel = new JLabel(" minutes");
		row1.add(minutesLabel);
		
		okButton = new JButton("      OK      ");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				re.launchTimer();
			}
		});
		row2.add(okButton);
		cancelButton = new JButton("   Cancel   ");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				re.cancelAlarm();
			}
		});
		row2.add(cancelButton);
		
		timerLabel = new JLabel("00:00:00");
		timerLabel.setFont(new Font("TimesRoman", Font.BOLD, 16));
		row3.add(timerLabel);
		
		todoLabel = new JLabel("To do:");
		row4.add(todoLabel);
		todoArea = new JTextArea(16, 22);
		todoArea.setBackground(new Color(242, 242, 242));
		todoArea.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		todoArea.setFont(new Font ("TimesRoman", Font.PLAIN, 14));
		todoArea.setLineWrap(true);
		row4.add(todoArea);
		
		re.loadTodoWhenOpen();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				re.saveTodoWhenClose();
			}
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		
		setSize(320, 470);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);
		setFrameToPositionILike();
	}
	
	private void setFrameToPositionILike() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width;
		int height = screenSize.height;
		setLocation((int)(width * 0.63), (int)(height * 0.2));
	}
	
	String getTodoAreatext() {
		return todoArea.getText();
	}
	
	void setTodoAreaText(String loadingString) {
		todoArea.setText(loadingString);
	}
	
	String getSelectedItem(){
		return (String)minBox.getSelectedItem();
	}
	
	void setTimerLabelText(String str1, String str2, String str3) {
		if (str1.length()==1) str1 = "0" + str1;
		if (str2.length()==1) str2 = "0" + str2;
		if (str3.length()==1) str3 = "0" + str3;
		timerLabel.setText(str1 + ":" + str2 + ":" +  str3);
	}
	
	public static void main(String[] args) {
		ReminderView rv = new ReminderView();
	}
}
